package org.hisp.dhis.databrowser;

/*
 * Copyright (c) 2004-2008, University of Oslo
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;

import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author joakibj, briane, eivinhb
 * @version $Id$
 * @modifier Dang Duy Hieu
 * @since 2010-04-15
 */
public class DataBrowserServiceTest
    extends DataBrowserTest
{
    private DataBrowserService dataBrowserService;

    @Override
    public void setUpTest()
        throws Exception
    {
        dataBrowserService = (DataBrowserService) getBean( DataBrowserService.ID );
        periodService = (PeriodService) getBean( PeriodService.ID );

        super.setUpDataBrowserTest();
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    /**
     * DataBrowserTable getDataSetsInPeriod( String startDate, String endDate,
     * PeriodType periodType );
     */
    @Test
    public void testGetDataSetsInPeriod()
    {
        // Get all DataSets from earliest to latest registered on daily basis
        // (this should be period A and B data values)
        DataBrowserTable table = dataBrowserService.getDataSetsInPeriod( null, null, periodA.getPeriodType(),
            mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "drilldown_data_set", table.getColumns().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", table.getColumns().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( dataSetB.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataSetB.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataSetA.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataSetA.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataSetC.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataSetC.getId(), table.getRows().get( 2 ).getId().intValue() );

        assertEquals( "Row count entries", 3, table.getCounts().size() );
        assertEquals( "DataValues in dataSetB", "18", table.getRowBasedOnRowName( dataSetB.getName() ).get( 0 )
           );
        assertEquals( "DataValues in dataSetA", "12", table.getRowBasedOnRowName( dataSetA.getName() ).get( 0 )
             );
        assertEquals( "DataValues in dataSetC", "3" , table.getRowBasedOnRowName( dataSetC.getName() ).get( 0 ) );

        // Get all DataSets from 2005-05-01 to 2005-05-31 registered on weekly
        // basis (this should be only period D data values)
        table = dataBrowserService
            .getDataSetsInPeriod( "2005-05-01", "2005-05-31", periodD.getPeriodType(), mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "drilldown_data_set", table.getColumns().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", table.getColumns().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "Metarows", 1, table.getRows().size() );
        assertEquals( dataSetC.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataSetC.getId(), table.getRows().get( 0 ).getId().intValue() );

        assertEquals( "Row count entries", 1, table.getCounts().size() );
        assertEquals( "DataValues in dataSetC", "6", table.getRowBasedOnRowName( dataSetC.getName() ).get( 0 ) );
    }

    /**
     * DataBrowserTable getDataElementGroupsInPeriod( String startDate, String
     * endDate, PeriodType periodType );
     */
    @Test
    public void testGetDataElementGroupsInPeriod()
    {
        // Get all DataElementGroups from earliest to latest registered on daily
        // basis (this should be period A and B data values)
        DataBrowserTable table = dataBrowserService.getDataElementGroupsInPeriod( null, null, periodA.getPeriodType(),
            mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "drilldown_data_element_group", table.getColumns().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", table.getColumns().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( dataElementGroupB.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementGroupB.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementGroupA.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementGroupA.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementGroupC.getId(), table.getRows().get( 2 ).getId().intValue() );

        assertEquals( "Row count entries", 3, table.getCounts().size() );
        assertEquals( "DataValues in dataElementGroupB", "18", table.getRowBasedOnRowName( dataElementGroupB.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementGroupA", "12", table.getRowBasedOnRowName( dataElementGroupA.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementGroupC", "3", table.getRowBasedOnRowName( dataElementGroupC.getName() )
            .get( 0 ) );

        // Get all DataElementGroups from 2005-05-01 to 2005-05-31 registered on
        // weekly basis (this should be only period D data values)
        table = dataBrowserService.getDataElementGroupsInPeriod( "2005-05-01", "2005-05-31", periodD.getPeriodType(),
            mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "drilldown_data_element_group", table.getColumns().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", table.getColumns().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "Metarows", 1, table.getRows().size() );
        assertEquals( dataElementGroupC.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementGroupC.getId(), table.getRows().get( 0 ).getId().intValue() );

        assertEquals( "Row count entries", 1, table.getCounts().size() );
        assertEquals( "DataValues in dataElementGroupC", "6", table.getRowBasedOnRowName( dataElementGroupC.getName() )
            .get( 0 ) );
    }

    /**
     * DataBrowserTable getOrgUnitGroupsInPeriod( String startDate, String
     * endDate, PeriodType periodType );
     */
    @Test
    public void testGetOrgUnitGroupsInPeriod()
    {
        // Get all OrganisationUnitGroups from earliest to latest registered on
        // daily
        // basis (this should be period A and B data values)
        DataBrowserTable table = dataBrowserService.getOrgUnitGroupsInPeriod( null, null, periodA.getPeriodType(),
            mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "drilldown_orgunit_group", table.getColumns().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", table.getColumns().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "Metarows", 2, table.getRows().size() );
        assertEquals( unitGroupB.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( unitGroupB.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( unitGroupA.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( unitGroupA.getId(), table.getRows().get( 1 ).getId().intValue() );

        assertEquals( "Row count entries", 2, table.getCounts().size() );
        // unitD has 6 datavalues, unitE has 6 datavalues and unitF has 5
        // datavalues for periods A and B
        assertEquals( "DataValues in unitGroupB", "17", table.getRowBasedOnRowName( unitGroupB.getName() ).get( 0 )
             );
        // unitB has 0 datavalues and unitC has 6 datavalues for periods A and B
        assertEquals( "DataValues in unitGroupA", "6", table.getRowBasedOnRowName( unitGroupA.getName() ).get( 0 )
            );
    }

    /**
     * DataBrowserTable getOrgUnitsInPeriod( Integer orgUnitParent, String
     * startDate, String endDate, PeriodType periodType );
     */
    @Test
    @Ignore
    public void testGetOrgUnitsInPeriod()
    {
        // Get all children of unit B from 2005-03-01 to 2005-04-30 registered
        // on daily basis (this should be period A and B data values)
        DataBrowserTable table = dataBrowserService.getOrgUnitsInPeriod( unitB.getId(), "2005-03-01", "2005-04-30",
            periodA.getPeriodType(), 4, mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 3, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 3, table.getColumns().size() );
        assertEquals( "drilldown_organisation_unit", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", table.getColumns().get( 1 ).getName() );
        assertEquals( "Period column header", "2005-04-01", table.getColumns().get( 2 ).getName() );

        // unitB has three children - sorted by name
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( unitD.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( unitD.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( unitE.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( unitE.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( unitF.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( unitF.getId(), table.getRows().get( 2 ).getId().intValue() );

        assertEquals( "Row count entries", 3, table.getCounts().size() );
        assertEquals( "DataValues in unitD for periodA", "4", table.getRowBasedOnRowName( unitD.getName() ).get( 0 )
             );
        assertEquals( "DataValues in unitD for periodB", "2", table.getRowBasedOnRowName( unitD.getName() ).get( 1 )
             );
        assertEquals( "DataValues in unitE for periodA", "4", table.getRowBasedOnRowName( unitE.getName() ).get( 0 )
             );
        assertEquals( "DataValues in unitE for periodB", "2", table.getRowBasedOnRowName( unitE.getName() ).get( 1 )
            );
        assertEquals( "DataValues in unitF for periodA", "2", table.getRowBasedOnRowName( unitF.getName() ).get( 0 )
           );
        assertEquals( "DataValues in unitF for periodB", "3", table.getRowBasedOnRowName( unitF.getName() ).get( 1 )
            );

        // Retrieve children of unitG - zero children
        table = dataBrowserService.getOrgUnitsInPeriod( unitG.getId(), null, null, periodA.getPeriodType(), 4,
            mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 3, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "drilldown_organisation_unit", table.getColumns().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", table.getColumns().get( 1 ).getName() );
        // Service layer adds "zero-column"
        assertEquals( "Metarows", 0, table.getRows().size() );
    }

    /**
     * DataBrowserTable getCountDataElementsForDataSetInPeriod( Integer
     * dataSetId, String startDate, String endDate, PeriodType periodType );
     */
    @Test
    public void testGetCountDataElementsForDataSetInPeriod()
    {
        // Get count for dataSetA from 2005-03-01 to 2005-04-30 registered on
        // daily basis (this should be period A and B data values)
        DataBrowserTable table = dataBrowserService.getCountDataElementsForDataSetInPeriod( dataSetA.getId(),
            "2005-03-01", "2005-04-30", periodA.getPeriodType(), mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 3, table.getColumns().size() );
        assertEquals( "drilldown_data_element", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", table.getColumns().get( 1 ).getName() );
        assertEquals( "Period column header", "2005-04-01", table.getColumns().get( 2 ).getName() );

        // dataSetA has one dataElement - sorted by name
        assertEquals( "Metarows", 1, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );

        assertEquals( "Row count entries", 1, table.getCounts().size() );
        assertEquals( "DataValues in dataElementA", "6", table.getRowBasedOnRowName( dataElementA.getName() ).get( 0 )
             );

        // Get count for dataSetC from 2005-05-01 to 2005-05-31 registered on
        // weekly basis (this should be only period D data values)
        table = dataBrowserService.getCountDataElementsForDataSetInPeriod( dataSetC.getId(), "2005-05-01",
            "2005-05-31", periodD.getPeriodType(), mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "drilldown_data_element", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-05-01", table.getColumns().get( 1 ).getName() );

        // dataSetC has two dataElements - sorted by name
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( dataElementC.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementC.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementE.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementE.getId(), table.getRows().get( 1 ).getId().intValue() );

        assertEquals( "Row count entries", 3, table.getCounts().size() );
        assertEquals( "DataValues in dataElementC", "3", table.getRowBasedOnRowName( dataElementC.getName() ).get( 0 )
             );
        assertEquals( "DataValues in dataElementE", "3", table.getRowBasedOnRowName( dataElementE.getName() ).get( 0 )
          );
    }

    /**
     * DataBrowserTable getCountDataElementsForDataElementGroupInPeriod( Integer
     * dataElementGroupId, String startDate, String endDate, PeriodType
     * periodType );
     */
    @Test
    public void testGetCountDataElementsForDataElementGroupInPeriod()
    {
        // Get count for dataElementGroupA from 2005-03-01 to 2005-04-30
        // registered on daily basis (this should be period A and B data values)
        DataBrowserTable table = dataBrowserService.getCountDataElementsForDataElementGroupInPeriod( dataElementGroupA
            .getId(), "2005-03-01", "2005-04-30", periodA.getPeriodType(), mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 3, table.getColumns().size() );
        assertEquals( "drilldown_data_element", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", table.getColumns().get( 1 ).getName() );
        assertEquals( "Period column header", "2005-04-01", table.getColumns().get( 2 ).getName() );

        // dataElementGroupA has one dataElement - sorted by name
        assertEquals( "Metarows", 1, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );

        assertEquals( "Row count entries", 1, table.getCounts().size() );
        assertEquals( "DataValues in dataElementA", "6", table.getRowBasedOnRowName( dataElementA.getName() ).get( 0 )
         );

        // Get count for dataElementGroupC from 2005-05-01 to 2005-05-31
        // registered on weekly basis (this should be only period D data values)
        table = dataBrowserService.getCountDataElementsForDataElementGroupInPeriod( dataElementGroupC.getId(),
            "2005-05-01", "2005-05-31", periodD.getPeriodType(), mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "drilldown_data_element", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-05-01", table.getColumns().get( 1 ).getName() );

        // dataElementGroupC has two dataElements - sorted by name
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( dataElementC.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementC.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementE.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementE.getId(), table.getRows().get( 1 ).getId().intValue() );

        assertEquals( "Row count entries", 3, table.getCounts().size() );
        assertEquals( "DataValues in dataElementC", "3", table.getRowBasedOnRowName( dataElementC.getName() ).get( 0 )
         );
        assertEquals( "DataValues in dataElementE", "3", table.getRowBasedOnRowName( dataElementE.getName() ).get( 0 )
          );
    }

    /**
     * DataBrowserTable getCountDataElementGroupsForOrgUnitGroupInPeriod(
     * Integer orgUnitGroupId, String startDate, String endDate, PeriodType
     * periodType );
     */
    @Test
    public void testGetCountDataElementGroupsForOrgUnitGroupInPeriod()
    {
        // Get count for unitGroupA from 2005-03-01 to 2005-04-30 registered on
        // daily
        // basis (this should be period A and B data values)
        DataBrowserTable table = dataBrowserService.getCountDataElementGroupsForOrgUnitGroupInPeriod( unitGroupA
            .getId(), "2005-03-01", "2005-04-30", periodA.getPeriodType(), mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        // unitGroupA has data values for dataElementGroup A, B and C in the two
        // periods
        assertEquals( "Metacolumns", 3, table.getColumns().size() );
        assertEquals( "drilldown_data_element_group", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", table.getColumns().get( 1 ).getName() );
        assertEquals( "Period column header", "2005-04-01", table.getColumns().get( 2 ).getName() );

        // unitGroupA has data values for dataElementGroup A, B and C - sorted
        // by name
        assertEquals( dataElementGroupA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementGroupA.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementGroupB.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementGroupB.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementGroupC.getId(), table.getRows().get( 2 ).getId().intValue() );

        assertEquals( "Row count entries", 3, table.getCounts().size() );
        assertEquals( "DataValues in dataElementGroupA for periodA", "1", table.getRowBasedOnRowName(
            dataElementGroupA.getName() ).get( 0 ) );
        assertEquals( "DataValues in dataElementGroupA for PeriodB", "1", table.getRowBasedOnRowName(
            dataElementGroupA.getName() ).get( 1 ) );
        assertEquals( "DataValues in dataElementGroupB for PeriodA", "2", table.getRowBasedOnRowName(
            dataElementGroupB.getName() ).get( 0 ) );
        assertEquals( "DataValues in dataElementGroupB for PeriodB", "1", table.getRowBasedOnRowName(
            dataElementGroupB.getName() ).get( 1 ) );
        assertEquals( "DataValues in dataElementGroupC for PeriodA", "1", table.getRowBasedOnRowName(
            dataElementGroupC.getName() ).get( 0 ) );
        assertEquals( "DataValues in dataElementGroupC for PeriodB", "0", table.getRowBasedOnRowName(
            dataElementGroupC.getName() ).get( 1 ) );
    }

    /**
     * DataBrowserTable getCountDataElementsForOrgUnitInPeriod( Integer
     * organizationUnitId, String startDate, String endDate, PeriodType
     * periodType );
     */
    @Test
    @Ignore
    public void testGetCountDataElementsForOrgUnitInPeriod()
    {
        // Get count for unitB from 2005-03-01 to 2005-04-30 registered on daily
        // basis (this should be period A and B data values)
        DataBrowserTable table = dataBrowserService.getCountDataElementsForOrgUnitInPeriod( unitB.getId(),
            "2005-03-01", "2005-04-30", periodA.getPeriodType(), mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        // unitB has no data values
        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "drilldown_data_element", table.getColumns().get( 0 ).getName() );
        // Service layer adds "zero-column"
        assertEquals( "Period column header", "counts_of_aggregated_values", table.getColumns().get( 1 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 0, table.getRows().size() );
        assertEquals( "Row count entries", 0, table.getCounts().size() );

        // Get count for unitF from 2005-03-01 to 2005-04-30 registered on daily
        // basis (this should be period A and B data values)
        table = dataBrowserService.getCountDataElementsForOrgUnitInPeriod( unitF.getId(), "2005-03-01", "2005-04-30",
            periodA.getPeriodType(), mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        // unitF has data values for dataElements A, B, D and E in two periods
        assertEquals( "Metacolumns", 3, table.getColumns().size() );
        assertEquals( "drilldown_data_element", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", table.getColumns().get( 1 ).getName() );
        assertEquals( "Period column header", "2005-04-01", table.getColumns().get( 2 ).getName() );

        // unitF has data values for data elements A, B, and D - sorted by name
        // Consists:
        // two data values for A count
        // two data values for B count
        // one data value for D count

        assertEquals( "Metarows", 4, table.getRows().size() );

        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementB.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementB.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementD.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementD.getId(), table.getRows().get( 2 ).getId().intValue() );

        assertEquals( "Row count entries", 4, table.getCounts().size() );

        assertEquals( "DataValues in dataElementA for periodA", "1", table.getRowBasedOnRowName( dataElementA.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementA for PeriodB", "1", table.getRowBasedOnRowName( dataElementA.getName() )
            .get( 1 ) );
        assertEquals( "DataValues in dataElementB for PeriodA", "1", table.getRowBasedOnRowName( dataElementB.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementB for PeriodB", "1", table.getRowBasedOnRowName( dataElementB.getName() )
            .get( 1 ) );
        assertEquals( "DataValues in dataElementD for PeriodA", "0", table.getRowBasedOnRowName( dataElementD.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementD for PeriodB", "1", table.getRowBasedOnRowName( dataElementD.getName() )
            .get( 1 ) );
    }

    /**
     * String convertDate( PeriodType periodType, String dateString, I18nFormat
     * format );
     */
    @Test
    @Ignore
    public void testConvertDate()
    {
        PeriodType monthlyPeriodType = periodService.getPeriodTypeByName( MonthlyPeriodType.NAME );

        // Get all children of unit B from 2005-03-01 to 2005-04-30 registered
        // on monthly basis (this should be period A and B data values)
        DataBrowserTable table = dataBrowserService.getOrgUnitsInPeriod( unitB.getId(), "2005-03-01", "2005-04-30",
            periodA.getPeriodType(), 4, mockFormat );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 3, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 3, table.getColumns().size() );
        assertEquals( "drilldown_organisation_unit", dataBrowserService.convertDate( monthlyPeriodType, table
            .getColumns().get( 0 ).getName(), mockFormat ) );
    }
}
