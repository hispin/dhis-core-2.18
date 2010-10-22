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
package org.hisp.dhis.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.aggregation.AggregatedDataSetAssociation;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Lars Helge Overland
 * @version $ID : DefaultPivotDataSetAssociationTableService.java 10:28:18 AM
 *          Jul 30, 2010
 */
public class DefaultPivotDataSetAssociationTableService
    implements PivotDataSetAssociationTableService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public PivotDataSetAssociationTable getPivotDataSetAssociationTable( int level, String periodTypeName )
    {
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );

        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService
            .getOrganisationUnitsAtLevel( level ) );

        List<AggregatedDataSetAssociation> associations = new ArrayList<AggregatedDataSetAssociation>();

        Map<Integer, Collection<DataSet>> dataSetMap = getDataSetMap( organisationUnits );

        List<DataSet> dataSetsByPeriodType = new ArrayList<DataSet>( dataSetService
            .getDataSetsByPeriodType( periodType ) );

        if ( dataSetsByPeriodType.size() > 0 )
        {
            Collections.sort( dataSetsByPeriodType, new DataSetNameComparator() );

            for ( OrganisationUnit orgunit : organisationUnits )
            {
                Collection<DataSet> dataSetCollection = dataSetMap.get( orgunit.getId() );
                
                for ( DataSet dataSet : dataSetsByPeriodType )
                {
                    boolean assigned = dataSetCollection.contains( dataSet );

                    AggregatedDataSetAssociation assoc = new AggregatedDataSetAssociation();
                    assoc.setOrganisationUnitId( orgunit.getId() );
                    assoc.setDataSetId( dataSet.getId() );
                    assoc.setLevel( level );
                    assoc.setAssigned( assigned );

                    associations.add( assoc );
                }
            }
        }

        PivotDataSetAssociationTable table = new PivotDataSetAssociationTable();
        table.setOrganisationUnits( organisationUnits );
        table.setDataSets( dataSetsByPeriodType );
        table.setAssociations( associations );

        return table;
    }

    // -------------------------------------------------------------------------
    // Private methods
    // -------------------------------------------------------------------------

    private Map<Integer, Collection<DataSet>> getDataSetMap( Collection<OrganisationUnit> organisationUnits )
    {
        Map<Integer, Collection<DataSet>> dataSetMap = new HashMap<Integer, Collection<DataSet>>();

        for ( OrganisationUnit unit : organisationUnits )
        {
            dataSetMap.put( unit.getId(), dataSetService.getDataSetsBySource( unit ) );
        }

        return dataSetMap;
    }
}
