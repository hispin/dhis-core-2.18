/*
 * Copyright (c) 2004-2013, University of Oslo
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

package org.hisp.dhis.dataelement;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version $ DefaultCategoryOptionGroupService.java Feb 12, 2014 11:38:43 PM $
 */
@Transactional
public class DefaultCategoryOptionGroupService
    implements CategoryOptionGroupService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<CategoryOptionGroup> categoryOptionGroupStore;

    public void setCategoryOptionGroupStore(
        GenericIdentifiableObjectStore<CategoryOptionGroup> categoryOptionGroupStore )
    {
        this.categoryOptionGroupStore = categoryOptionGroupStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // CategoryOptionGroup
    // -------------------------------------------------------------------------

    @Override
    public int addCategoryOptionGroup( CategoryOptionGroup categoryOptionGroup )
    {
        return categoryOptionGroupStore.save( categoryOptionGroup );
    }

    @Override
    public void deleteCategoryOptionGroup( CategoryOptionGroup categoryOptionGroup )
    {
        categoryOptionGroupStore.delete( categoryOptionGroup );
    }

    @Override
    public void updateCategoryOptionGroup( CategoryOptionGroup categoryOptionGroup )
    {
        categoryOptionGroupStore.update( categoryOptionGroup );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroup( int id )
    {
        return i18n( i18nService, categoryOptionGroupStore.get( id ) );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroupByUid( String uid )
    {
        return i18n( i18nService, categoryOptionGroupStore.getByUid( uid ) );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroupByName( String name )
    {
        return i18n( i18nService, categoryOptionGroupStore.getByName( name ) );
    }

    @Override
    public Collection<CategoryOptionGroup> getAllCategoryOptionGroups()
    {
        return i18n( i18nService, categoryOptionGroupStore.getAll() );
    }

    @Override
    public Collection<CategoryOptionGroup> getCategoryOptionGroupsBetweenByName( String name, int first, int max )
    {
        return i18n( i18nService, categoryOptionGroupStore.getAllLikeNameOrderedName( name, first, max ) );
    }

    @Override
    public int getCategoryOptionGroupCount()
    {
        return categoryOptionGroupStore.getCount();
    }

    @Override
    public Collection<CategoryOptionGroup> getCategoryOptionGroupsBetween( int first, int max )
    {
        return i18n( i18nService, categoryOptionGroupStore.getAllOrderedName( first, max ) );
    }

    @Override
    public int getCategoryOptionGroupCountByName( String name )
    {
        return categoryOptionGroupStore.getCountLikeName( name );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroupByShortName( String shortName )
    {
        List<CategoryOptionGroup> categoryOptionGroups = new ArrayList<CategoryOptionGroup>(
            categoryOptionGroupStore.getAllLikeShortName( shortName ) );

        if ( categoryOptionGroups.isEmpty() )
        {
            return null;
        }

        return i18n( i18nService, categoryOptionGroups.get( 0 ) );
    }

    @Override
    public CategoryOptionGroup getCategoryOptionGroupByCode( String code )
    {
        return i18n( i18nService, categoryOptionGroupStore.getByCode( code ) );
    }

}
