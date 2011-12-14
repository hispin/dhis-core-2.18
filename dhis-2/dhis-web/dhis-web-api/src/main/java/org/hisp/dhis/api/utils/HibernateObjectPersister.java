package org.hisp.dhis.api.utils;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
@Transactional
public class HibernateObjectPersister implements ObjectPersister
{
    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @Override
    public DataElement persistDataElement( DataElement dataElement )
    {
        Collection<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>( dataElement.getGroups() );
        Collection<DataSet> dataSets = new ArrayList<DataSet>( dataElement.getDataSets() );
        dataElement.getGroups().clear();
        dataElement.getDataSets().clear();
        dataElement.getAttributeValues().clear();
        dataElement.setCategoryCombo( null );

        dataElementService.addDataElement( dataElement );

        if ( dataElement.getCategoryCombo() != null )
        {
            DataElementCategoryCombo dataElementCategoryCombo = dataElementCategoryService.getDataElementCategoryCombo( dataElement.getCategoryCombo().getUid() );
            dataElement.setCategoryCombo( dataElementCategoryCombo );
        }

        for ( DataElementGroup dataElementGroup : dataElementGroups )
        {
            dataElementGroup = dataElementService.getDataElementGroup( dataElementGroup.getUid() );
            dataElement.addDataElementGroup( dataElementGroup );
        }

        for ( DataSet dataSet : dataSets )
        {
            dataSet = dataSetService.getDataSet( dataSet.getUid() );
            dataSet.addDataElement( dataElement );
        }

        dataElementService.updateDataElement( dataElement );

        return dataElement;
    }

    @Override
    public DataElementGroup persistDataElementGroup( DataElementGroup dataElementGroup )
    {
        Collection<DataElement> dataElements = new ArrayList<DataElement>( dataElementGroup.getMembers() );
        DataElementGroupSet dataElementGroupSet = dataElementGroup.getGroupSet();
        dataElementGroup.getMembers().clear();
        dataElementGroup.setGroupSet( null );

        dataElementService.addDataElementGroup( dataElementGroup );

        for ( DataElement dataElement : dataElements )
        {
            dataElement = dataElementService.getDataElement( dataElement.getUid() );
            dataElementGroup.addDataElement( dataElement );
        }

        if ( dataElementGroupSet != null )
        {
            dataElementGroupSet = dataElementService.getDataElementGroupSet( dataElementGroup.getGroupSet().getUid() );
            dataElementGroup.setGroupSet( dataElementGroupSet );
        }

        dataElementService.updateDataElementGroup( dataElementGroup );

        return dataElementGroup;
    }

    @Override
    public DataElementGroupSet persistDataElementGroupSet( DataElementGroupSet dataElementGroupSet )
    {
        Collection<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>( dataElementGroupSet.getMembers() );
        dataElementGroupSet.getMembers().clear();

        dataElementService.addDataElementGroupSet( dataElementGroupSet );

        for ( DataElementGroup dataElementGroup : dataElementGroups )
        {
            dataElementGroup = dataElementService.getDataElementGroup( dataElementGroup.getUid() );
            dataElementGroupSet.addDataElementGroup( dataElementGroup );
        }

        dataElementService.updateDataElementGroupSet( dataElementGroupSet );

        return dataElementGroupSet;
    }

    @Override
    public OrganisationUnit persistOrganisationUnit( OrganisationUnit organisationUnit )
    {
        Collection<OrganisationUnitGroup> organisationUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnit.getGroups() );
        Collection<DataSet> dataSets = new ArrayList<DataSet>( organisationUnit.getDataSets() );
        OrganisationUnit parent = organisationUnit.getParent();

        organisationUnit.getGroups().clear();
        organisationUnit.getDataSets().clear();
        organisationUnit.setParent( null );
        organisationUnit.getAttributeValues().clear();

        organisationUnitService.addOrganisationUnit( organisationUnit );

        for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitGroups )
        {
            organisationUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( organisationUnitGroup.getUid() );
            organisationUnit.addOrganisationUnitGroup( organisationUnitGroup );
        }

        for ( DataSet dataSet : dataSets )
        {
            dataSet = dataSetService.getDataSet( dataSet.getUid() );
            organisationUnit.addDataSet( dataSet );
        }

        if ( parent != null )
        {
            parent = organisationUnitService.getOrganisationUnit( parent.getUid() );
            organisationUnit.setParent( parent );
        }

        organisationUnitService.updateOrganisationUnit( organisationUnit );

        return organisationUnit;
    }

    @Override
    public OrganisationUnitLevel persistOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        organisationUnitService.addOrganisationUnitLevel( organisationUnitLevel );

        return organisationUnitLevel;
    }

    @Override
    public OrganisationUnitGroup persistOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup )
    {
        Collection<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitGroup.getMembers() );
        OrganisationUnitGroupSet organisationUnitGroupSet = organisationUnitGroup.getGroupSet();

        organisationUnitGroup.getMembers().clear();
        organisationUnitGroup.setGroupSet( null );

        organisationUnitGroupService.addOrganisationUnitGroup( organisationUnitGroup );

        for ( OrganisationUnit organisationUnit : organisationUnits )
        {
            organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnit.getUid() );
            organisationUnitGroup.addOrganisationUnit( organisationUnit );
        }

        if ( organisationUnitGroupSet != null )
        {
            organisationUnitGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( organisationUnitGroupSet.getUid() );
            organisationUnitGroupSet.addOrganisationUnitGroup( organisationUnitGroup );
        }

        organisationUnitGroupService.updateOrganisationUnitGroup( organisationUnitGroup );

        return organisationUnitGroup;
    }

    @Override
    public OrganisationUnitGroupSet persistOrganisationUnitGroupSet( OrganisationUnitGroupSet organisationUnitGroupSet )
    {
        Collection<OrganisationUnitGroup> organisationUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupSet.getOrganisationUnitGroups() );
        organisationUnitGroupSet.getOrganisationUnitGroups().clear();

        organisationUnitGroupService.addOrganisationUnitGroupSet( organisationUnitGroupSet );

        for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitGroups )
        {
            organisationUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( organisationUnitGroup.getUid() );
            organisationUnitGroupSet.addOrganisationUnitGroup( organisationUnitGroup );
        }

        organisationUnitGroupService.updateOrganisationUnitGroupSet( organisationUnitGroupSet );

        return organisationUnitGroupSet;
    }
}
