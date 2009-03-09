package org.hisp.dhis.dataelement;

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

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.i18n.I18nService;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class DefaultDataElementCategoryOptionService
    implements DataElementCategoryOptionService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementCategoryOptionStore dataElementCategoryOptionStore;

    public void setDataElementCategoryOptionStore( DataElementCategoryOptionStore dataElementCategoryOptionStore )
    {
        this.dataElementCategoryOptionStore = dataElementCategoryOptionStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // DataElementCategoryOption
    // -------------------------------------------------------------------------

    public int addDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        int id = dataElementCategoryOptionStore.addDataElementCategoryOption( dataElementCategoryOption );
        
        i18nService.addObject( dataElementCategoryOption );
        
        return id;
    }

    public void updateDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        dataElementCategoryOptionStore.addDataElementCategoryOption( dataElementCategoryOption );
        
        i18nService.verify( dataElementCategoryOption );
    }

    public void deleteDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption )
    {
        i18nService.removeObject( dataElementCategoryOption );
        
        dataElementCategoryOptionStore.deleteDataElementCategoryOption( dataElementCategoryOption );
    }

    public DataElementCategoryOption getDataElementCategoryOption( int id )
    {
        return dataElementCategoryOptionStore.getDataElementCategoryOption( id );
    }
    
    public Collection<DataElementCategoryOption> getDataElementCategoryOptions( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllDataElementCategoryOptions();
        }
        
        Collection<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();
        
        for ( Integer id : identifiers )
        {
            categoryOptions.add( getDataElementCategoryOption( id ) );
        }
        
        return categoryOptions;
    }

    public DataElementCategoryOption getDataElementCategoryOptionByName( String name )
    {
        return dataElementCategoryOptionStore.getDataElementCategoryOptionByName( name );
    }
    
    public DataElementCategoryOption getDataElementCategoryOptionByShortName( String shortName )
    {
        return dataElementCategoryOptionStore.getDataElementCategoryOptionByShortName( shortName );
    }

    public Collection<DataElementCategoryOption> getAllDataElementCategoryOptions()
    {
        return dataElementCategoryOptionStore.getAllDataElementCategoryOptions();
    }
}
