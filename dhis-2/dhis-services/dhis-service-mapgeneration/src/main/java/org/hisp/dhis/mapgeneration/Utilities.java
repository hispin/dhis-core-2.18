package org.hisp.dhis.mapgeneration;

import java.awt.Color;

/**
 * Utility class.
 * 
 * @author Olai Solheim <olais@ifi.uio.no>
 */
public class Utilities
{
    /**
     * Linear interpolation of int.
     * 
     * @param a from
     * @param b to
     * @param t factor, typically 0-1
     * @return the interpolated int
     */
    public static int lerp( int a, int b, double t )
    {
        return a + (int) ((b - a) * t);
    }

    /**
     * Linear interpolation of double.
     * 
     * @param a from
     * @param b to
     * @param t factor, typically 0-1
     * @return the interpolated double
     */
    public static double lerp( double a, double b, double t )
    {
        return a + (b - a) * t;
    }

    /**
     * Linear interpolation of RGB colors.
     * 
     * @param a from
     * @param b to
     * @param t interpolation factor, typically 0-1
     * @return the interpolated color
     */
    public static Color lerp( Color a, Color b, double t )
    {
        return new Color( lerp( a.getRed(), b.getRed(), t ), lerp( a.getGreen(), b.getGreen(), t ), lerp( a.getBlue(),
            b.getBlue(), t ), lerp( a.getAlpha(), b.getAlpha(), t ) );
    }

    /**
     * Creates a java.awt.Color from a dhis style color string, e.g. '#ff3200'
     * is an orange color.
     * 
     * @param str the color in string, e.g. '#ff3200'
     * @return the color
     */
    public static Color createColorFromString( String str )
    {
        return new Color( Integer.parseInt( str.substring( 1 ), 16 ) );
    }
}
