package org.hisp.dhis.design;

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

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
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
