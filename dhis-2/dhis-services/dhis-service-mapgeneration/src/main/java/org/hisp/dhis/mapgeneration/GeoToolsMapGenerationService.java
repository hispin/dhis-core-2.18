package org.hisp.dhis.mapgeneration;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hisp.dhis.aggregation.AggregatedMapValue;
import org.hisp.dhis.mapgeneration.IntervalSet.DistributionStrategy;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.springframework.util.Assert;

/**
 * An implementation of MapGenerationService that uses GeoTools to generate
 * maps.
 * 
 * @author Kenneth Solbø Andersen <kennetsa@ifi.uio.no>
 * @author Kristin Simonsen <krissimo@ifi.uio.no>
 * @author Kjetil Andresen <kjetand@ifi.uio.no>
 * @author Olai Solheim <olais@ifi.uio.no>
 */
public class GeoToolsMapGenerationService
    implements MapGenerationService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    // -------------------------------------------------------------------------
    // MapGenerationService implementation
    // -------------------------------------------------------------------------

    public BufferedImage generateMapImage( Map map )
    {
        Assert.isTrue( map != null );
        Assert.isTrue( map.getMapViews() != null );
        Assert.isTrue( map.getMapViews().size() > 0 );

        int height = 512;

        // Build internal map layer representation
        List<InternalMapLayer> mapLayers = buildInternalMapLayers( map.getMapViews() );

        // Build internal representation of a map using GeoTools, then render it
        // to an image
        GeoToolsMap gtMap = new GeoToolsMap( mapLayers );
        BufferedImage mapImage = gtMap.render( height );

        // Build the legend set, then render it to an image
        LegendSet legendSet = new LegendSet( mapLayers );
        BufferedImage legendImage = legendSet.render( height );

        // Combine the legend image and the map image into one image
        BufferedImage finalImage = combineLegendAndMapImages( legendImage, mapImage );

        return finalImage;
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private static final String DEFAULT_COLOR_HIGH = "#ff0000";

    private static final String DEFAULT_COLOR_LOW = "#ffff00";

    private static final float DEFAULT_OPACITY = 0.75f;

    private static final String DEFAULT_STROKE_COLOR = "#ffffff";

    private static final int DEFAULT_STROKE_WIDTH = 1;

    private static final int DEFAULT_RADIUS_HIGH = 35;

    private static final int DEFAULT_RADIUS_LOW = 15;

    private List<InternalMapLayer> buildInternalMapLayers( List<MapView> mapViews )
    {
        // Create the list of internal map layers
        List<InternalMapLayer> mapLayers = new LinkedList<InternalMapLayer>();

        // Build internal layers for each external layer
        for ( MapView mapView : mapViews )
        {
            mapLayers.add( buildSingleInternalMapLayer( mapView ) );
        }

        return mapLayers;
    }

    private InternalMapLayer buildSingleInternalMapLayer( MapView mapView )
    {

        Assert.isTrue( mapView != null );
        Assert.isTrue( mapView.getMapValueType() != null );

        boolean isIndicator = "indicator".equals( mapView.getMapValueType() );

        // Get the name from the external layer
        String name = mapView.getName();

        // Get the period
        Period period = mapView.getPeriod();

        // Get the low and high radii
        int radiusLow = !isIndicator ? mapView.getRadiusLow() : DEFAULT_RADIUS_LOW;
        int radiusHigh = !isIndicator ? mapView.getRadiusHigh() : DEFAULT_RADIUS_HIGH;

        // Get the low and high colors, typically in hexadecimal form, e.g.
        // '#ff3200' is an orange color
        Color colorLow = Utilities.createColorFromString( mapView.getColorLow() != null ? mapView.getColorLow()
            : DEFAULT_COLOR_LOW );
        Color colorHigh = Utilities.createColorFromString( mapView.getColorHigh() != null ? mapView.getColorHigh()
            : DEFAULT_COLOR_HIGH );

        // TODO MapView should be extended to feature opacity
        float opacity = DEFAULT_OPACITY;

        // TODO MapView should be extended to feature stroke color
        Color strokeColor = Utilities.createColorFromString( DEFAULT_STROKE_COLOR );

        // TODO MapView might be extended to feature stroke width
        int strokeWidth = DEFAULT_STROKE_WIDTH;

        // Create and setup an internal layer
        InternalMapLayer mapLayer = new InternalMapLayer();
        mapLayer.setName( name );
        mapLayer.setPeriod( period );
        mapLayer.setRadiusLow( radiusLow );
        mapLayer.setRadiusHigh( radiusHigh );
        mapLayer.setColorLow( colorLow );
        mapLayer.setColorHigh( colorHigh );
        mapLayer.setOpacity( opacity );
        mapLayer.setStrokeColor( strokeColor );
        mapLayer.setStrokeWidth( strokeWidth );

        // Get the aggregated map values
        // TODO Might make version of getIndicatorMapValues that takes Indicator
        // and
        // parent OrganisationUnit *directly*, i.e. not from ID-s, since we have
        // them
        // NOTE There is no need to provide startDate and endDate as period is
        // set
        Collection<AggregatedMapValue> mapValues;
        if ( mapView.getMapValueType().equals( "dataelement" ) )
        {
            mapValues = mappingService.getDataElementMapValues( mapView.getDataElement().getId(), mapView.getPeriod()
                .getId(), mapView.getParentOrganisationUnit().getId(), mapView.getOrganisationUnitLevel().getLevel() );
        }
        else
        {
            mapValues = mappingService.getIndicatorMapValues( mapView.getIndicator().getId(), mapView.getPeriod()
                .getId(), mapView.getParentOrganisationUnit().getId(), mapView.getOrganisationUnitLevel().getLevel() );
        }

        // Build and set the internal GeoTools map objects for the layer
        buildGeoToolsMapObjectsForMapLayer( mapLayer, mapValues );

        // Create an interval set for this map layer that distributes its map
        // objects into their respective intervals
        // TODO Make interval length a parameter
        IntervalSet.applyIntervalSetToMapLayer( DistributionStrategy.STRATEGY_EQUAL_RANGE, mapLayer, 5 );

        // Update the radius of each map object in this map layer according to
        // its map object's highest and lowest values
        if ( !isIndicator )
        {
            mapLayer.applyInterpolatedRadii();
        }

        return mapLayer;
    }

    private List<GeoToolsMapObject> buildGeoToolsMapObjectsForMapLayer( InternalMapLayer mapLayer,
        Collection<AggregatedMapValue> mapValues )
    {

        // Create a list of map objects
        List<GeoToolsMapObject> mapObjects = new LinkedList<GeoToolsMapObject>();

        // Build internal map objects for each map value
        for ( AggregatedMapValue mapValue : mapValues )
        {
            mapObjects.add( buildSingleGeoToolsMapObjectForMapLayer( mapLayer, mapValue ) );
        }

        return mapObjects;
    }

    private GeoToolsMapObject buildSingleGeoToolsMapObjectForMapLayer( InternalMapLayer mapLayer,
        AggregatedMapValue mapValue )
    {

        // Get the org unit for this map value
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( mapValue.getOrganisationUnitId() );

        // Create and setup an internal map object
        GeoToolsMapObject mapObject = new GeoToolsMapObject();
        mapObject.setName( orgUnit.getName() );
        mapObject.setValue( mapValue.getValue() );
        mapObject.setFillOpacity( mapLayer.getOpacity() );
        mapObject.setStrokeColor( mapLayer.getStrokeColor() );
        mapObject.setStrokeWidth( mapLayer.getStrokeWidth() );

        // Build and set the GeoTools-specific geometric primitive that outlines
        // the org unit on the map
        mapObject.buildAndApplyGeometryForOrganisationUnit( orgUnit );

        // Add the map object to the map layer
        mapLayer.addMapObject( mapObject );

        // Set the map layer for the map object
        mapObject.setMapLayer( mapLayer );

        return mapObject;
    }

    private BufferedImage combineLegendAndMapImages( BufferedImage legendImage, BufferedImage mapImage )
    {

        Assert.isTrue( legendImage != null );
        Assert.isTrue( mapImage != null );
        Assert.isTrue( legendImage.getType() == mapImage.getType() );

        // Create a new image with dimension (legend.width + map.width,
        // max(legend.height, map.height))
        BufferedImage finalImage = new BufferedImage( legendImage.getWidth() + mapImage.getWidth(), Math.max(
            mapImage.getHeight(), mapImage.getHeight() ), mapImage.getType() );

        // Draw the two images onto the final image with the legend to the left
        // and the map to the right
        Graphics g = finalImage.getGraphics();
        g.drawImage( legendImage, 0, 0, null );
        g.drawImage( mapImage, legendImage.getWidth(), 0, null );

        return finalImage;
    }
}
