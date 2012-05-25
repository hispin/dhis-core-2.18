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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Service
public class DefaultExportService
    implements ExportService
{
    private static final Log log = LogFactory.getLog( DefaultExportService.class );

    //-------------------------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------------------------

    @Autowired
    protected IdentifiableObjectManager manager;

    @Autowired
    private CurrentUserService currentUserService;

    //-------------------------------------------------------------------------------------------------------
    // ExportService Implementation
    //-------------------------------------------------------------------------------------------------------

    @Override
    public MetaData getMetaData()
    {
        return getMetaData( ExportOptions.getDefaultExportOptions() );
    }

    @Override
    public MetaData getMetaData( ExportOptions exportOptions )
    {
        MetaData metaData = new MetaData();

        log.info( "User '" + currentUserService.getCurrentUsername() + "' started export at " + new Date() );

        for ( Map.Entry<String, Class<?>> entry : ExchangeClasses.getExportMap().entrySet() )
        {
            @SuppressWarnings( "unchecked" )
            Class<? extends IdentifiableObject> idObjectClass = (Class<? extends IdentifiableObject>) entry.getValue();

            Collection<? extends IdentifiableObject> idObjects = manager.getAll( idObjectClass );

            if ( idObjects != null )
            {
                log.info( "Exporting " + idObjects.size() + " " + StringUtils.capitalize( entry.getKey() ) );

                List<? extends IdentifiableObject> idObjectsList = new ArrayList<IdentifiableObject>( idObjects );
                ReflectionUtils.invokeSetterMethod( entry.getKey(), metaData, idObjectsList );
            }
            else
            {
                log.warn( "Skipping objects of type '" + entry.getValue().getSimpleName() + "'." );
            }
        }

        log.info( "Finished export at " + new Date() );

        return metaData;
    }
}
