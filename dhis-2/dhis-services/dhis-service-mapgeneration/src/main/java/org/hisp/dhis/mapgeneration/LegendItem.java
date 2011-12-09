package org.hisp.dhis.mapgeneration;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 * A legend item is a graphical presentation of a interval. It serves as a
 * helper for the Legend class.
 * 
 * @author Kristin Simonsen <krissimo@ifi.uio.no>
 * @author Kjetil Andresen <kjetil.andrese@gmail.com>
 */
public class LegendItem
{
    private Interval interval;

    private static final int WIDTH = 25;

    private static final int HEIGHT = 20;

    public LegendItem( Interval interval )
    {
        this.interval = interval;
    }

    public void draw( Graphics2D g )
    {
        String label = String.format( "%.2f - %.2f (%d)", interval.getValueLow(), interval.getValueHigh(), interval
            .getMembers().size() );
        Stroke s = new BasicStroke( 1.0f );
        Rectangle r = new Rectangle( 0, 0, WIDTH, HEIGHT );

        g.setColor( interval.getColor() );
        g.fill( r );
        g.setPaint( Color.BLACK );
        g.setStroke( s );
        g.draw( r );

        g.setColor( Color.BLACK );
        g.setFont( Legend.PLAIN_FONT );
        g.drawString( label, WIDTH + 15, HEIGHT - 5 );
    }

    public int getHeight()
    {
        return HEIGHT;
    }

    public Interval getInterval()
    {
        return interval;
    }

    public void setInterval( Interval interval )
    {
        this.interval = interval;
    }
}
