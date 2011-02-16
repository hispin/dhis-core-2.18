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

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author joakibj, briane, eivinhb
 * @version $Id$
 */
public class DataBrowserStoreTest
    extends DataBrowserTest
{
    private DataBrowserStore dataBrowserStore;

    @Override
    public void setUpTest()
        throws Exception
    {
        dataBrowserStore = (DataBrowserStore) getBean( DataBrowserStore.ID );
        
        super.setUpDataBrowserTest();
    }
    
    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }    
   
    /**
     * DataBrowserTable getDataSetsBetweenPeriods( List<Integer>
     * betweenPeriodIds );
     */
    public void testGetDataSetsBetweenPeriods()
    {
        List<Integer> betweenPeriodIds = new ArrayList<Integer>();
        betweenPeriodIds.add( periodA.getId() );
        betweenPeriodIds.add( periodB.getId() );
        betweenPeriodIds.add( periodC.getId() );
        betweenPeriodIds.add( periodD.getId() );

        DataBrowserTable table = dataBrowserStore.getDataSetsBetweenPeriods( betweenPeriodIds );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "DataSet", table.getColumns().get( 0 ).getName() );
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
        assertEquals( "DataValues in dataSetB", "24", table.getRowBasedOnRowName( dataSetB.getName() ).get( 0 )
             );
        assertEquals( "DataValues in dataSetA", "18", table.getRowBasedOnRowName( dataSetA.getName() ).get( 0 )
             );
        assertEquals( "DataValues in dataSetC", "12", table.getRowBasedOnRowName( dataSetC.getName() ).get( 0 )
          );
    }

    /**
     * DataBrowserTable getDataElementGroupsBetweenPeriods( List<Integer>
     * betweenPeriodIds );
     */
    public void testGetDataElementGroupsBetweenPeriods()
    {
        List<Integer> betweenPeriodIds = new ArrayList<Integer>();
        betweenPeriodIds.add( periodA.getId() );
        betweenPeriodIds.add( periodB.getId() );
        betweenPeriodIds.add( periodC.getId() );
        betweenPeriodIds.add( periodD.getId() );

        DataBrowserTable table = dataBrowserStore.getDataElementGroupsBetweenPeriods( betweenPeriodIds );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "DataElementGroup", table.getColumns().get( 0 ).getName() );
        assertEquals( "Count", table.getColumns().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( dataElementGroupB.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementGroupB.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementGroupA.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementGroupA.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementGroupC.getId(), table.getRows().get( 2 ).getId().intValue() );

        assertEquals( "Row count entries", 3, table.getCounts().size() );
        assertEquals( "DataValues in dataElementGroupB", "24", table.getRowBasedOnRowName( dataElementGroupB.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementGroupA", "18", table.getRowBasedOnRowName( dataElementGroupA.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementGroupC", "12", table.getRowBasedOnRowName( dataElementGroupC.getName() )
            .get( 0 ) );
    }

    /**
     * DataBrowserTable getOrgUnitGroupsBetweenPeriods( List<Integer> betweenPeriodIds );
     */
    public void testGetOrgUnitGroupsBetweenPeriods()
    {
        List<Integer> betweenPeriodIds = new ArrayList<Integer>();
        betweenPeriodIds.add( periodA.getId() );
        betweenPeriodIds.add( periodB.getId() );
        betweenPeriodIds.add( periodC.getId() );
        betweenPeriodIds.add( periodD.getId() );
        
        DataBrowserTable table = dataBrowserStore.getOrgUnitGroupsBetweenPeriods( betweenPeriodIds );
        
        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "OrgUnitGroup", table.getColumns().get( 0 ).getName() );
        assertEquals( "counts_of_aggregated_values", table.getColumns().get( 1 ).getName() );

        // Sorted by count
        assertEquals( "Metarows", 2, table.getRows().size() );
        assertEquals( unitGroupB.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( unitGroupB.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( unitGroupA.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( unitGroupA.getId(), table.getRows().get( 1 ).getId().intValue() );

        assertEquals( "Row count entries", 2, table.getCounts().size() );
        // unitD has 10 DataValues, unitE has 10 DataValues and unitF has 8 DataValues
        assertEquals( "DataValues in unitGroupB", "28", table.getRowBasedOnRowName( unitGroupB.getName() )
            .get( 0 ) );
        // unitB has 0 DataValues and unitC has 10 DataValues 
        assertEquals( "DataValues in unitGroupA", "10", table.getRowBasedOnRowName( unitGroupA.getName() )
            .get( 0 ) );
    }

    /**
     * void setDataElementStructureForDataSetBetweenPeriods( DataBrowserTable
     * table, Integer dataSetId, List<Integer> betweenPeriods );
     */
    public void testSetDataElementStructureForDataSetBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );
        pList.add( periodB.getId() );
        pList.add( periodC.getId() );
        pList.add( periodD.getId() );

        // Retrieve dataElements of DataSetA - one dataElement
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForDataSetBetweenPeriods( table, dataSetA.getId(), pList );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 1, table.getColumns().size() );
        assertEquals( "DataElement", table.getColumns().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 1, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );

        // Retrieve dataElements of DataSetC - three dataElements
        table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForDataSetBetweenPeriods( table, dataSetC.getId(), pList );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 1, table.getColumns().size() );
        assertEquals( "DataElement", table.getColumns().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( dataElementC.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementC.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementE.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementE.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementF.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementF.getId(), table.getRows().get( 2 ).getId().intValue() );
    }

    /**
     * void setDataElementStructureForDataElementGroupBetweenPeriods(
     * DataBrowserTable table, Integer dataElementGroupId, List<Integer>
     * betweenPeriods );
     */
    public void testSetDataElementStructureForDataElementGroupBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );
        pList.add( periodB.getId() );
        pList.add( periodC.getId() );
        pList.add( periodD.getId() );

        // Retrieve dataElements of DataElementGroupA - one dataElement
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForDataElementGroupBetweenPeriods( table, dataElementGroupA.getId(),
            pList );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 1, table.getColumns().size() );
        assertEquals( "DataElement", table.getColumns().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 1, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );

        // Retrieve dataElements of DataElementGroupC - three dataElements
        table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForDataSetBetweenPeriods( table, dataElementGroupC.getId(), pList );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 1, table.getColumns().size() );
        assertEquals( "DataElement", table.getColumns().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( dataElementC.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementC.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementE.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementE.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementF.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementF.getId(), table.getRows().get( 2 ).getId().intValue() );
    }

    /**
     * void setDataElementGroupStructureForOrgUnitGroupBetweenPeriods(
     * DataBrowserTable table, Integer orgUnitGroupId, List<Integer>
     * betweenPeriods );
     */
    public void testSetDataElementGroupStructureForOrgUnitGroupBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );
        pList.add( periodB.getId() );
        pList.add( periodC.getId() );
        pList.add( periodD.getId() );
        
        // Retrieve orgUnitGroupA - three dataElementGroups
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementGroupStructureForOrgUnitGroupBetweenPeriods( table, unitGroupA.getId(), pList );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 1, table.getColumns().size() );
        assertEquals( "DataElementGroup", table.getColumns().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( dataElementGroupA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementGroupA.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementGroupB.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementGroupB.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementGroupC.getId(), table.getRows().get( 2 ).getId().intValue() );

        // Retrieve dataElements of orgUnitGroupB - three dataElementGroups
        table = new DataBrowserTable();
        dataBrowserStore.setDataElementGroupStructureForOrgUnitGroupBetweenPeriods( table, unitGroupB.getId(), pList );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 1, table.getColumns().size() );
        assertEquals( "DataElementGroup", table.getColumns().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( dataElementGroupA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementGroupA.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementGroupB.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementGroupB.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementGroupC.getId(), table.getRows().get( 2 ).getId().intValue() );
    }

    /**
     * void setStructureForOrgUnitBetweenPeriods( DataBrowserTable table,
     * Integer orgUnitParent, List<Integer> betweenPeriods );
     */
    public void testSetStructureForOrgUnitBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );
        pList.add( periodB.getId() );
        pList.add( periodC.getId() );
        pList.add( periodD.getId() );

        // Retrieve children of unitB - three children
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setStructureForOrgUnitBetweenPeriods( table, unitB.getId(), pList );
        
        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 1, table.getColumns().size() );
        assertEquals( "OrganisationUnit", table.getColumns().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( unitD.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( unitD.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( unitE.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( unitE.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( unitF.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( unitF.getId(), table.getRows().get( 2 ).getId().intValue() );

        // Retrieve children of unitG - zero children
        table = new DataBrowserTable();
        dataBrowserStore.setStructureForOrgUnitBetweenPeriods( table, unitG.getId(), pList );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 1, table.getColumns().size() );
        assertEquals( "OrganisationUnit", table.getColumns().get( 0 ).getName() );

        assertEquals( "Metarows", 0, table.getRows().size() );
    }

    /**
     * void setDataElementStructureForOrgUnitBetweenPeriods(
     * DataBrowserTable table, String orgUnitId, List<Integer> betweenPeriodIds
     * );
     */
    public void testSetDataElementStructureForOrgUnitBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );

        // Retrieve structure for dataElements in periodA for unitC - six dataElements
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForOrgUnitBetweenPeriods( table, unitC.getId(), pList );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 1, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 1, table.getColumns().size() );
        assertEquals( "DataElement", table.getColumns().get( 0 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 6, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementB.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementB.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementC.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementC.getId(), table.getRows().get( 2 ).getId().intValue() );
        assertEquals( dataElementD.getName(), table.getRows().get( 3 ).getName() );
        assertEquals( dataElementD.getId(), table.getRows().get( 3 ).getId().intValue() );
        assertEquals( dataElementE.getName(), table.getRows().get( 4 ).getName() );
        assertEquals( dataElementE.getId(), table.getRows().get( 4 ).getId().intValue() );
        assertEquals( dataElementF.getName(), table.getRows().get( 5 ).getName() );
        assertEquals( dataElementF.getId(), table.getRows().get( 5 ).getId().intValue() );
    }
    
    /**
     * Integer setCountDataElementsForDataSetBetweenPeriods( DataBrowserTable
     * table, Integer dataSetId, List<Integer> betweenPeriodIds );
     */
    public void testSetCountDataElementsForDataSetBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );

        // Retrieve structure for dataElements in periodA for dataSetA - one dataElement
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForDataSetBetweenPeriods( table, dataSetA.getId(), pList );

        // Retrieve actual count for dataElements in periodA for dataSetA
        int results = dataBrowserStore.setCountDataElementsForDataSetBetweenPeriods( table, dataSetA.getId(), pList );
        assertEquals( "DataValue results", 1, results );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "DataElement", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", table.getColumns().get( 1 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 1, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );

        assertEquals( "Row count entries", 1, table.getCounts().size() );
        assertEquals( "DataValues in dataElementA for periodA", "6", table.getRowBasedOnRowName( dataElementA.getName() )
            .get( 0 ) );
    }

    /**
     * Integer setCountDataElementsForDataElementGroupBetweenPeriods(
     * DataBrowserTable table, Integer dataElementGroupId, List<Integer>
     * betweenPeriodIds );
     */
    public void testSetCountDataElementsForDataElementGroupBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );

        // Retrieve structure for dataElements in periodA for dataElementGroupA - one dataElement
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForDataSetBetweenPeriods( table, dataElementGroupA.getId(), pList );

        // Retrieve actual count for dataElements in periodA for dataElementGroupA
        int results = dataBrowserStore.setCountDataElementsForDataSetBetweenPeriods( table, dataElementGroupA.getId(),
            pList );
        assertEquals( "DataValue results", 1, results );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "DataElement", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", table.getColumns().get( 1 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 1, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );

        assertEquals( "Row count entries", 1, table.getCounts().size() );
        assertEquals( "DataValues in dataElementA for periodA", "6", table.getRowBasedOnRowName( dataElementA.getName() )
            .get( 0 ) );
    }
    
    /**
     * Integer setCountDataElementGroupsForOrgUnitGroupBetweenPeriods(
     * DataBrowserTable table, Integer orgUnitGroupId, List<Integer>
     * betweenPeriodIds );
     */
    public void testSetCountDataElementGroupsForOrgUnitGroupBetweenPeriods ()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );

        // Retrieve structure for dataElementGroups in periodA for unitGroupA - three dataElementGroups
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementGroupStructureForOrgUnitGroupBetweenPeriods( table, unitGroupA.getId(), pList );

        // Retrieve actual count for dataElementGroups in periodA for unitGroupA
        int results = dataBrowserStore.setCountDataElementGroupsForOrgUnitGroupBetweenPeriods( table, unitGroupA.getId(), pList );
        assertEquals( "DataValue results", 3, results );
        
        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );
        
        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "DataElementGroup", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", table.getColumns().get( 1 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 3, table.getRows().size() );
        assertEquals( dataElementGroupA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementGroupA.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementGroupB.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementGroupB.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementGroupC.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementGroupC.getId(), table.getRows().get( 2 ).getId().intValue() );

        assertEquals( "Row count entries", 3, table.getCounts().size() );
        assertEquals( "DataValues in dataElementGroupA for periodA", "1", table.getRowBasedOnRowName( dataElementGroupA.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementGroupB for periodA", "2", table.getRowBasedOnRowName( dataElementGroupB.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementGroupC for periodA", "1", table.getRowBasedOnRowName( dataElementGroupC.getName() )
            .get( 0 ) );
    }

    /**
     * Integer setCountOrgUnitsBetweenPeriods( DataBrowserTable table, Integer
     * orgUnitParent, List<Integer> betweenPeriodIds );
     */
    public void testSetCountOrgUnitsBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodC.getId() );

        // Retrieve structure for dataElements in periodC for unitD - six dataElements
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForOrgUnitBetweenPeriods( table, unitD.getId(), pList );

        // Retrieve actual count for dataElements in periodC for unitD
        int results = dataBrowserStore.setCountDataElementsForOrgUnitBetweenPeriods( table, unitD.getId(), pList );
        assertEquals( "DataValue results", 3, results );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        // Sorted by name
        assertEquals( "Metarows", 6, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementB.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementB.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementC.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementC.getId(), table.getRows().get( 2 ).getId().intValue() );
        assertEquals( dataElementD.getName(), table.getRows().get( 3 ).getName() );
        assertEquals( dataElementD.getId(), table.getRows().get( 3 ).getId().intValue() );
        assertEquals( dataElementE.getName(), table.getRows().get( 4 ).getName() );
        assertEquals( dataElementE.getId(), table.getRows().get( 4 ).getId().intValue() );
        assertEquals( dataElementF.getName(), table.getRows().get( 5 ).getName() );
        assertEquals( dataElementF.getId(), table.getRows().get( 5 ).getId().intValue() );

        // unitD has all six dataElements but only dataValues in periodC for
        // three of them. The other three (C, D, F) are zero
        assertEquals( "Row count entries", 6, table.getCounts().size() );
        assertEquals( "DataValues in dataElementA for periodC", "1", table.getRowBasedOnRowName( dataElementA.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementB for periodC", "1", table.getRowBasedOnRowName( dataElementB.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementC for periodC", "0", table.getRowBasedOnRowName( dataElementC.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementD for periodC", "0", table.getRowBasedOnRowName( dataElementD.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementE for periodC", "1", table.getRowBasedOnRowName( dataElementE.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementF for periodC", "0", table.getRowBasedOnRowName( dataElementF.getName() )
            .get( 0 ) );
    }

    /**
     * Integer setCountDataElementsForOrgUnitBetweenPeriods( DataBrowserTable
     * table, String orgUnitId, List<Integer> betweenPeriodIds );
     */
    public void testSetCountDataElementsForOrgUnitBetweenPeriods()
    {
        List<Integer> pList = new ArrayList<Integer>();
        pList.add( periodA.getId() );

        // Retrieve structure for dataElements in periodA for unitC - six dataElements
        DataBrowserTable table = new DataBrowserTable();
        dataBrowserStore.setDataElementStructureForOrgUnitBetweenPeriods( table, unitC.getId(), pList );

        // Retrieve actual count for dataElements in periodA for unitC
        int results = dataBrowserStore.setCountDataElementsForOrgUnitBetweenPeriods( table, unitC.getId(), pList );
        assertEquals( "DataValue results", 4, results );

        assertNotNull( "DataBrowserTable not supposed to be null", table );
        assertEquals( "No. of queries", 2, table.getQueryCount() );
        assertNotSame( "Querytime more than 0", 0, table.getQueryTime() );

        assertEquals( "Metacolumns", 2, table.getColumns().size() );
        assertEquals( "DataElement", table.getColumns().get( 0 ).getName() );
        assertEquals( "Period column header", "2005-03-01", table.getColumns().get( 1 ).getName() );

        // Sorted by name
        assertEquals( "Metarows", 6, table.getRows().size() );
        assertEquals( dataElementA.getName(), table.getRows().get( 0 ).getName() );
        assertEquals( dataElementA.getId(), table.getRows().get( 0 ).getId().intValue() );
        assertEquals( dataElementB.getName(), table.getRows().get( 1 ).getName() );
        assertEquals( dataElementB.getId(), table.getRows().get( 1 ).getId().intValue() );
        assertEquals( dataElementC.getName(), table.getRows().get( 2 ).getName() );
        assertEquals( dataElementC.getId(), table.getRows().get( 2 ).getId().intValue() );
        assertEquals( dataElementD.getName(), table.getRows().get( 3 ).getName() );
        assertEquals( dataElementD.getId(), table.getRows().get( 3 ).getId().intValue() );
        assertEquals( dataElementE.getName(), table.getRows().get( 4 ).getName() );
        assertEquals( dataElementE.getId(), table.getRows().get( 4 ).getId().intValue() );
        assertEquals( dataElementF.getName(), table.getRows().get( 5 ).getName() );
        assertEquals( dataElementF.getId(), table.getRows().get( 5 ).getId().intValue() );

        // unitC has all six dataElements but only dataValues in periodA for
        // four of them. The other two (C and E) are zero
        assertEquals( "Row count entries", 6, table.getCounts().size() );
        assertEquals( "DataValues in dataElementA for periodA", "1" , table.getRowBasedOnRowName( dataElementA.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementB for periodA", "1" , table.getRowBasedOnRowName( dataElementB.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementC for periodA", "0" , table.getRowBasedOnRowName( dataElementC.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementD for periodA", "1" , table.getRowBasedOnRowName( dataElementD.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementE for periodA", "0" , table.getRowBasedOnRowName( dataElementE.getName() )
            .get( 0 ) );
        assertEquals( "DataValues in dataElementF for periodA", "1" , table.getRowBasedOnRowName( dataElementF.getName() )
            .get( 0 ) );
    }
    
}
