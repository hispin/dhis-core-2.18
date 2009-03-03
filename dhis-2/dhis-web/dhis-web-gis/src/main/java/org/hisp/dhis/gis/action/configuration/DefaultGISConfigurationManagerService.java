package org.hisp.dhis.gis.action.configuration;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.io.File;

import org.hisp.dhis.gis.GISConfiguration;
import org.hisp.dhis.gis.GISConfigurationService;

public class DefaultGISConfigurationManagerService
    implements GISConfigurationManagerService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GISConfigurationService gisConfigurationService;

    public void setGisConfigurationService( GISConfigurationService gisConfigurationService )
    {
        this.gisConfigurationService = gisConfigurationService;
    }

    // -------------------------------------------------------------------------
    // Implement
    // -------------------------------------------------------------------------

    public boolean isNULL( String key )
    {
        GISConfiguration gisConfiguration = gisConfigurationService.get( key );
        if ( gisConfiguration == null )
            return true;
        if ( gisConfiguration.getValue() == null )
            return true;
        if ( gisConfiguration.getValue() == "" )
            return true;
        return false;
    }

    public File getGISDirectory()
    {
        String value = gisConfigurationService.getValue( GISConfiguration.KEY_DIRECTORY );

        if ( value != null )
        {
            return new File( value );
        }

        return null;

    }

    public File getGISMapDirectory()
    {
        if ( getGISDirectory() != null )
        {
            return new File( getGISDirectory(), MAP_DIR );
        }
        return null;

    }

    public File getGISTempDirectory()
    {

        if ( getGISDirectory() != null )
        {
            return new File( getGISDirectory(), TEMP_DIR );
        }
        return null;
    }

    public String getIndicatorFrom()
    {

        return gisConfigurationService.getValue( GISConfiguration.KEY_GETINDICATOR );
    }

}
