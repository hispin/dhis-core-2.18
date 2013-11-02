package org.hisp.dhis.system.util;

import java.awt.geom.Point2D;

import org.geotools.referencing.GeodeticCalculator;

public class GeoUtils
{
    
    /**
     * Returns boundaries of a box shape which centre is the point defined by the 
     * given longitude and latitude. The distance between the center point and the
     * edges of the box is defined in meters by the given distance. Based on standard
     * EPSG:4326 long/lat projection. The result is an array of length 4 where
     * the values at each index are:
     * 
     * <ul>
     * <li>Index 0: Maximum latitude (north edge of box shape).</li>
     * <li>Index 1: Maxium longitude (east edge of box shape).</li>
     * <li>Index 2: Minimum latitude (south edge of box shape).</li>
     * <li>Index 3: Minumum longitude (west edge of box shape).</li>
     * </ul>
     * 
     * @param longitude the longitude.
     * @param latitude the latitude.
     * @param distance the distance in meters to each box edge.
     * @return an array of length 4.
     */
    public static double[] getBoxShape( double longitude, double latitude, double distance )
    {
        double[] box = new double[4];
        
        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint( longitude, latitude );
        
        calc.setDirection( 0, distance );
        Point2D north = calc.getDestinationGeographicPoint();
        
        calc.setDirection( 90, distance );
        Point2D east = calc.getDestinationGeographicPoint();
        
        calc.setDirection( 180, distance );
        Point2D south = calc.getDestinationGeographicPoint();
        
        calc.setDirection( -90, distance );
        Point2D west = calc.getDestinationGeographicPoint();
        
        box[0] = north.getY();
        box[1] = east.getX();
        box[2] = south.getY();
        box[3] = west.getX();
        
        return box;
    }
    
    /**
     * Creates the distance between two points.
     */
    public static double getDistanceBetweenTwoPoints( Point2D from, Point2D to)
    {                        
        GeodeticCalculator calc = new GeodeticCalculator();
        calc.setStartingGeographicPoint( from );
        calc.setDestinationGeographicPoint( to);
        
        return calc.getOrthodromicDistance();
    }
    
}
