package org.hisp.dhis.dxf2.metadata;

/*
 * Copyright (c) 2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class Options
{
    //--------------------------------------------------------------------------
    // Static helpers
    //--------------------------------------------------------------------------

    static private final Options DEFAULT_OPTIONS = new Options( true );

    static public Options getDefaultOptions()
    {
        return DEFAULT_OPTIONS;
    }

    private static boolean isTrue( String bool )
    {
        return bool != null && bool.equalsIgnoreCase( "true" );
    }

    private static boolean isFalse( String bool )
    {
        return !isTrue( bool );
    }

    //--------------------------------------------------------------------------
    // Internal State
    //--------------------------------------------------------------------------

    private Map<String, String> options = new HashMap<String, String>();

    private boolean assumeTrue;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    public Options()
    {
    }

    public Options( boolean assumeTrue )
    {
        this.assumeTrue = assumeTrue;
    }

    public Options( Map<String, String> options )
    {
        System.err.println("Options: " + options);

        this.options = options;
        this.assumeTrue = options.get( "assumeTrue" ) == null || options.get( "assumeTrue" ).equalsIgnoreCase( "true" );
    }

    public Options( Map<String, String> options, boolean assumeTrue )
    {
        System.err.println("Options: " + options);

        this.options = options;
        this.assumeTrue = assumeTrue;
    }

    //--------------------------------------------------------------------------
    // Get options for classes/strings etc
    //--------------------------------------------------------------------------

    public boolean isEnabled( String type )
    {
        String enabled = options.get( type );

        return isTrue( enabled ) || enabled == null && assumeTrue;
    }

    public boolean isDisabled( String type )
    {
        return !isEnabled( type );
    }

    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------

    public Map<String, String> getOptions()
    {
        return options;
    }

    public void setOptions( Map<String, String> options )
    {
        this.options = options;
    }

    public boolean isAssumeTrue()
    {
        return assumeTrue;
    }

    public void setAssumeTrue( boolean assumeTrue )
    {
        this.assumeTrue = assumeTrue;
    }
}
