package org.hisp.dhis.api.controller;

import org.hisp.dhis.dxf2.metadata.Options;

import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class WebOptions
    extends Options
{
    public WebOptions()
    {
    }

    public WebOptions( boolean assumeTrue )
    {
        super( assumeTrue );
    }

    public WebOptions( Map<String, String> options )
    {
        super( options );
    }

    public WebOptions( Map<String, String> options, boolean assumeTrue )
    {
        super( options, assumeTrue );
    }

    //--------------------------------------------------------------------------
    // Getters for standard web options
    //--------------------------------------------------------------------------

    public boolean hasLinks()
    {
        return stringAsBoolean( options.get( "links" ), true );
    }

    public boolean hasPaging()
    {
        return stringAsBoolean( options.get( "paging" ), true );
    }

    public int getPage()
    {
        return stringAsInt( options.get( "page" ), 1 );
    }

    public String getViewClass( String defaultValue )
    {
        return stringAsString( options.get( "viewClass" ), defaultValue );
    }
}
