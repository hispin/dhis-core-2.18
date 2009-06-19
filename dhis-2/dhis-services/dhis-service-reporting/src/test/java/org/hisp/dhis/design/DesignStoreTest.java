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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@SuppressWarnings( "unchecked" )
public class DesignStoreTest
    extends DhisSpringTest
{
    private GenericStore<Design> designStore;
    
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
        designStore = (GenericStore<Design>) getBean( "org.hisp.dhis.design.DesignStore" );
        
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
                
        designStore.save( designA );
        designStore.save( designB );
    }

    @Test
    public void testSave()
    {
        int idA = designStore.save( designA );
        int idB = designStore.save( designB );
        
        assertEquals( designA, designStore.get( idA ) );
        assertEquals( designB, designStore.get( idB ) );

        assertNotNull( designStore.get( idA ).getCharts() );
        assertNotNull( designStore.get( idA ).getReportTables() );
        
        assertEquals( designStore.get( idA ).getCharts().size(), 2 );
        assertEquals( designStore.get( idA ).getReportTables().size(), 2 );
    }

    @Test
    public void testDelete()
    {
        int idA = designStore.save( designA );
        int idB = designStore.save( designB );
        
        assertNotNull( designStore.get( idA ) );
        assertNotNull( designStore.get( idB ) );
        
        designStore.delete( designA );

        assertNull( designStore.get( idA ) );
        assertNotNull( designStore.get( idB ) );

        designStore.delete( designB );

        assertNull( designStore.get( idA ) );
        assertNull( designStore.get( idB ) );        
    }

    @Test
    public void testGetAll()
    {
        designStore.save( designA );
        designStore.save( designB );
        
        equals( designStore.getAll(), designA, designB );
    }
}
