package org.hisp.dhis.web.api2.resources;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.web.api2.ResponseUtils;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

@Path( "/chart" )
public class ChartResource
{
    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }
    
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nManager i18nManager;

    public void setI18nManager( I18nManager manager )
    {
        i18nManager = manager;
    }

    @GET
    @Path( "/{id}/{width}/{height}" )
    @Produces( ContextUtils.CONTENT_TYPE_PNG )
    public Response getChart( @PathParam("id") Integer id, @PathParam("width") final Integer width, @PathParam("height") final Integer height )
        throws Exception
    {
        final JFreeChart jFreeChart = chartService.getJFreeChart( id, i18nManager.getI18nFormat() );
        
        final Chart chart = chartService.getChart( id );
        
        final String filename = CodecUtils.filenameEncode( chart.getName() + ".png" );
        
        return ResponseUtils.response( true, filename, false ).entity( new StreamingOutput()
        {
            @Override
            public void write( OutputStream out )
                throws IOException, WebApplicationException
            {
                ChartUtilities.writeChartAsPNG( out, jFreeChart, width, height, true, 0 );
            }
        } ).build();
    }
    
    @GET
    @Path( "/period/{indicator}/{orgUnit}/{width}/{height}/{title}" )
    @Produces( ContextUtils.CONTENT_TYPE_PNG )
    public Response getPeriodChart( @PathParam("indicator") String indicatorUuid, @PathParam("orgUnit") String orgUnitUuid, 
        @PathParam("width") final Integer width, @PathParam("height") final Integer height, @PathParam("title") Boolean title ) 
            throws Exception
    {
        final Indicator indicator = indicatorService.getIndicator( indicatorUuid );
        
        final OrganisationUnit unit = organisationUnitService.getOrganisationUnit( orgUnitUuid );
        
        if ( indicator == null || unit == null )
        {
            return null;
        }
        
        final String filename = CodecUtils.filenameEncode( indicator.getName() + ".png" );
        
        final JFreeChart jFreeChart = chartService.getJFreePeriodChart( indicator, unit, title, i18nManager.getI18nFormat() );
        
        return ResponseUtils.response( true, filename, false ).entity( new StreamingOutput()
        {
            public void write( OutputStream out )
                throws IOException, WebApplicationException
            {
                ChartUtilities.writeChartAsPNG( out, jFreeChart, width, height, true, 0 );
            }
        } ).build();
    }

    @GET
    @Path( "/orgUnit/{indicator}/{orgUnit}/{width}/{height}/{title}" )
    @Produces( ContextUtils.CONTENT_TYPE_PNG )
    public Response getOrganisationUnitChart( @PathParam("indicator") String indicatorUuid, @PathParam("orgUnit") String orgUnitUuid, 
        @PathParam("width") final Integer width, @PathParam("height") final Integer height, @PathParam("title") Boolean title ) 
            throws Exception
    {
        final Indicator indicator = indicatorService.getIndicator( indicatorUuid );
        
        final OrganisationUnit unit = organisationUnitService.getOrganisationUnit( orgUnitUuid );
        
        if ( indicator == null || unit == null )
        {
            return null;
        }
        
        final String filename = CodecUtils.filenameEncode( indicator.getName() + ".png" );
        
        final JFreeChart jFreeChart = chartService.getJFreeOrganisationUnitChart( indicator, unit, title, i18nManager.getI18nFormat() );
        
        return ResponseUtils.response( true, filename, false ).entity( new StreamingOutput()
        {
            public void write( OutputStream out )
                throws IOException, WebApplicationException
            {
                ChartUtilities.writeChartAsPNG( out, jFreeChart, width, height, true, 0 );
            }
        } ).build();
    }
}
