package org.hisp.dhis.webapi.controller.organisationunit;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import com.google.common.collect.Lists;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitByLevelComparator;
import org.hisp.dhis.schema.descriptors.OrganisationUnitSchemaDescriptor;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = OrganisationUnitSchemaDescriptor.API_ENDPOINT )
public class OrganisationUnitController
    extends AbstractCrudController<OrganisationUnit>
{
    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private CurrentUserService currentUserService;

    @Override
    protected List<OrganisationUnit> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<OrganisationUnit> entityList;

        Integer level = null;

        boolean levelSorted = options.isTrue( "levelSorted" );

        Integer maxLevel = options.getInt( "level" );

        if ( options.contains( "maxLevel" ) )
        {
            maxLevel = options.getInt( "maxLevel" );

            if ( organisationUnitService.getOrganisationUnitLevelByLevel( maxLevel ) == null )
            {
                maxLevel = null;
            }

            if ( level == null )
            {
                level = 1;
            }
        }

        if ( options.isTrue( "userOnly" ) )
        {
            entityList = new ArrayList<>( currentUserService.getCurrentUser().getOrganisationUnits() );
        }
        else if ( options.isTrue( "userDataViewOnly" ) )
        {
            entityList = new ArrayList<>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
        }
        else if ( options.isTrue( "userDataViewFallback" ) )
        {
            User user = currentUserService.getCurrentUser();

            if ( user != null && user.hasDataViewOrganisationUnit() )
            {
                entityList = new ArrayList<>( user.getDataViewOrganisationUnits() );
            }
            else
            {
                entityList = new ArrayList<>( organisationUnitService.getOrganisationUnitsAtLevel( 1 ) );
            }
        }
        else if ( options.contains( "query" ) )
        {
            entityList = new ArrayList<>( manager.filter( getEntityClass(), options.get( "query" ) ) );

            if ( levelSorted )
            {
                Collections.sort( entityList, OrganisationUnitByLevelComparator.INSTANCE );
            }
        }
        else if ( maxLevel != null || level != null )
        {
            entityList = new ArrayList<>();

            if ( maxLevel == null )
            {
                entityList.addAll( organisationUnitService.getOrganisationUnitsAtLevel( level ) );
            }
            else
            {
                entityList.addAll( organisationUnitService.getOrganisationUnitsAtLevel( level ) );

                while ( !level.equals( maxLevel ) )
                {
                    entityList.addAll( organisationUnitService.getOrganisationUnitsAtLevel( ++level ) );
                }
            }
        }
        else if ( levelSorted )
        {
            entityList = new ArrayList<>( manager.getAll( getEntityClass() ) );
            Collections.sort( entityList, OrganisationUnitByLevelComparator.INSTANCE );
        }
        else if ( options.hasPaging() )
        {
            int count = manager.getCount( getEntityClass() );

            Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
            metaData.setPager( pager );

            entityList = new ArrayList<>( manager.getBetween( getEntityClass(), pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<>( manager.getAllSorted( getEntityClass() ) );
        }

        return entityList;
    }

    @Override
    protected List<OrganisationUnit> getEntity( String uid, WebOptions options )
    {
        OrganisationUnit organisationUnit = manager.get( getEntityClass(), uid );

        if ( organisationUnit == null )
        {
            return Lists.newArrayList();
        }

        List<OrganisationUnit> organisationUnits = Lists.newArrayList();

        if ( options.getOptions().containsKey( "includeChildren" ) )
        {
            options.getOptions().put( "useWrapper", "true" );
            organisationUnits.add( organisationUnit );
            organisationUnits.addAll( organisationUnit.getChildren() );
        }
        else if ( options.getOptions().containsKey( "includeDescendants" ) )
        {
            options.getOptions().put( "useWrapper", "true" );
            organisationUnits.addAll( organisationUnitService.getOrganisationUnitsWithChildren( uid ) );
        }
        else
        {
            organisationUnits.add( organisationUnit );
        }

        return organisationUnits;
    }
}
