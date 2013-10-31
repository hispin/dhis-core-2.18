package org.hisp.dhis.filter;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.hisp.dhis.i18n.I18nUtils.*;

/**
 * @author Ovidiu Rosu <rosu.ovi@gmail.com>
 */
@Transactional
public class DefaultFilterService
    implements FilterService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private FilterStore filterStore;

    public FilterStore getFilterStore()
    {
        return filterStore;
    }

    public void setFilterStore( FilterStore filterStore )
    {
        this.filterStore = filterStore;
    }

    private I18nService i18nService;

    public I18nService getI18nService()
    {
        return i18nService;
    }

    public void setI18nService( I18nService i18nService )
    {
        this.i18nService = i18nService;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    @Override
    public MetaDataFilter getFilter( Integer id )
    {
        return filterStore.get( id );
    }

    @Override
    public MetaDataFilter getFilterByUid( String uid )
    {
        return filterStore.getByUid( uid );
    }

    @Override
    public Collection<MetaDataFilter> getAllFilters()
    {
        return filterStore.getAll();
    }

    @Override
    public Collection<MetaDataFilter> getFiltersBetweenByName( String name, int first, int max )
    {
        return getObjectsBetweenByName( i18nService, filterStore, name, first, max );
    }

    @Override
    public Collection<MetaDataFilter> getFiltersBetween( int first, int max )
    {
        return getObjectsBetween( i18nService, filterStore, first, max );
    }

    @Override
    public void saveFilter( MetaDataFilter metaDataFilter )
    {
        filterStore.save( metaDataFilter );
    }

    @Override
    public void updateFilter( MetaDataFilter metaDataFilter )
    {
        filterStore.update( metaDataFilter );
    }

    @Override
    public void deleteFilter( MetaDataFilter metaDataFilter )
    {
        filterStore.delete( metaDataFilter );
    }

    @Override
    public int getFilterCountByName( String name )
    {
        return getCountByName( i18nService, filterStore, name );
    }

    @Override
    public int getFilterCount()
    {
        return filterStore.getCount();
    }
}
