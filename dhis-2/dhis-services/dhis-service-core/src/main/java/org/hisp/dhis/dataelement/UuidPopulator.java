package org.hisp.dhis.dataelement;

/*
 * Copyright (c) 2004-2010, University of Oslo
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
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.hisp.dhis.system.util.UUIdUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Bob Jolliffe
 * @version $Id$
 * 
 * Provides uuids to uniquely indentifiable objects which do not already have them.
 * 
 * <p>Should be all the things requiring uuids .. there are more.
 *    What follows is a bit of a compromise hack essentially copying
 *    and pasting 3 times. Should reimplement with common interface
 *    IdentifiableObject or something similar.
 */
public class UuidPopulator
    extends AbstractStartupRoutine
{
    private Log log = LogFactory.getLog( UuidPopulator.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementCategoryService categoryService;

    private DataElementService dataElementService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // StartupRoutine implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void execute()
        throws Exception
    {
        for ( DataElementCategoryOption option : categoryService.getAllDataElementCategoryOptions() )
        {
            if ( option.getUuid() == null )
            {
                option.setUuid( UUIdUtils.getUUId() );
                categoryService.updateDataElementCategoryOption( option );
                log.info( "Added uuid for CategoryOption '" + option.getName() + "'" );

            }
        }
        log.info( "Checked CategoryOption uuids" );

        for ( DataElementCategory category : categoryService.getAllDataElementCategories() )
        {
            if ( category.getUuid() == null )
            {
                category.setUuid( UUIdUtils.getUUId() );
                categoryService.updateDataElementCategory( category );
                log.info( "Added uuid for Category '" + category.getName() + "'" );

            }
        }
        log.info( "Checked Category uuids" );

        for ( DataElement de : dataElementService.getAllDataElements() )
        {
            if ( de.getUuid() == null )
            {
                de.setUuid( UUIdUtils.getUUId() );
                dataElementService.updateDataElement( de );
                log.info( "Added uuid for DataElement '" + de.getName() + "'" );

            }
        }
        log.info( "Checked DataElement uuids" );

        for ( DataElementCategoryOptionCombo combo : categoryService.getAllDataElementCategoryOptionCombos() )
        {
            if ( combo.getUuid() == null )
            {
                combo.setUuid( UUIdUtils.getUUId() );
                categoryService.updateDataElementCategoryOptionCombo( combo );
                log.info( "Added uuid for CategoryOptionCombo '" + combo.getName() + "'" );
            }
        }
        log.info( "Checked CategoryOptionCombo uuids" );

    }
}
