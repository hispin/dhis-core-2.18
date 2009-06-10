package org.hisp.dhis.design;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.junit.Test;

import static junit.framework.Assert.*;

public class DesignStoreTest
    extends DhisSpringTest
{
    private DesignStore designStore;
    
    private ChartService chartService;
    
    private ReportTableService reportTableService;

    private Design designA;
    private Design designB;
    
    private Chart chartA = new Chart( "ChartA" );
    private Chart chartB = new Chart( "ChartB" );

    private ReportTable reportTableA = new ReportTable( "ReportTableA", "TableNameA" );
    private ReportTable reportTableB = new ReportTable( "ReportTableB", "TableNameB" );    
    
    @Override
    public void setUpTest()
    {
        designStore = (DesignStore) getBean( DesignStore.ID );
        
        chartService = (ChartService) getBean( ChartService.ID );
        
        reportTableService = (ReportTableService) getBean( ReportTableService.ID );
        
        chartService.saveChart( chartA );
        chartService.saveChart( chartB );
        
        reportTableService.saveReportTable( reportTableA );
        reportTableService.saveReportTable( reportTableB );
        
        designA = new Design( "DesignA" );
        designB = new Design( "DesignB" );
        
        designA.getCharts().add( chartA );
        designA.getCharts().add( chartB );
        designB.getCharts().add( chartA );
        designB.getCharts().add( chartB );
        
        designA.getReportTables().add( reportTableA );
        designA.getReportTables().add( reportTableB );
        designB.getReportTables().add( reportTableA );
        designB.getReportTables().add( reportTableB );
                
        designStore.saveDesign( designA );
        designStore.saveDesign( designB );
    }

    @Test
    public void testSave()
    {
        int idA = designStore.saveDesign( designA );
        int idB = designStore.saveDesign( designB );
        
        assertEquals( designA, designStore.getDesign( idA ) );
        assertEquals( designB, designStore.getDesign( idB ) );

        assertNotNull( designStore.getDesign( idA ).getCharts() );
        assertNotNull( designStore.getDesign( idA ).getReportTables() );
        
        assertEquals( designStore.getDesign( idA ).getCharts().size(), 2 );
        assertEquals( designStore.getDesign( idA ).getReportTables().size(), 2 );
    }

    @Test
    public void testDelete()
    {
        int idA = designStore.saveDesign( designA );
        int idB = designStore.saveDesign( designB );
        
        assertNotNull( designStore.getDesign( idA ) );
        assertNotNull( designStore.getDesign( idB ) );
        
        designStore.deleteDesign( designA );

        assertNull( designStore.getDesign( idA ) );
        assertNotNull( designStore.getDesign( idB ) );

        designStore.deleteDesign( designB );

        assertNull( designStore.getDesign( idA ) );
        assertNull( designStore.getDesign( idB ) );        
    }

    @Test
    public void testGetAll()
    {
        designStore.saveDesign( designA );
        designStore.saveDesign( designB );
        
        equals( designStore.getAllDesigns(), designA, designB );
    }
}
