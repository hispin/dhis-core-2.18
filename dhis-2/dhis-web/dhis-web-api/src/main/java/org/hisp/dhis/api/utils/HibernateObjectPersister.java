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

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class HibernateObjectPersister implements ObjectPersister
{
    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    @Autowired
    private DataSetService dataSetService;

    @Override
    public void persistAttribute( Attribute attribute )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void persistDataElement( DataElement dataElement )
    {
        if ( dataElement.getCategoryCombo() != null )
        {
            DataElementCategoryCombo dataElementCategoryCombo = dataElementCategoryService.getDataElementCategoryCombo( dataElement.getCategoryCombo().getUid() );
            dataElement.setCategoryCombo( dataElementCategoryCombo );
        }

        Collection<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>( dataElement.getGroups() );
        Collection<DataSet> dataSets = new ArrayList<DataSet>( dataElement.getDataSets() );
        dataElement.getGroups().clear();
        dataElement.getDataSets().clear();

        dataElementService.addDataElement( dataElement );

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
    }

    @Override
    public void persistDataElementGroup( DataElementGroup dataElementGroup )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistDataElementGroupSet( DataElementGroupSet dataElementGroupSet )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistCategory( DataElementCategory category )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistCategoryOption( DataElementCategoryOption categoryOption )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistCategoryOptionCombo( DataElementCategoryOptionCombo categoryOptionCombo )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistIndicator( Indicator indicator )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistIndicatorType( IndicatorType indicatorType )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistIndicatorGroupSet( IndicatorGroupSet indicatorGroupSet )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistOrganisationUnit( OrganisationUnit organisationUnit )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistOrganisationUnitGroupSet( OrganisationUnitGroupSet organisationUnitGroupSet )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistDataSet( DataSet dataSet )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistChart( Chart chart )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void persistUser( User user )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
