package org.hisp.dhis.rt;

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
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.rt.report.ReportStore;

public class ReportStoreTest
    extends DhisSpringTest
{
    private final String reportName = "NeverUsedTestReport";
        
    private ReportStore reportStore;
    
    private DataElementService dataElementService;
    
    public void setUpTest() throws Exception
    {
        reportStore = (ReportStore) getBean( ReportStore.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
    }

    public void testReportStore() throws Exception
    {
        DataElement dataElement1 = new DataElement(
            "name1", "shortName1", "description", true, DataElement.TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, null );
        int dataElementId1 = dataElementService.addDataElement( dataElement1 );
        
        DataElement dataElement2 = new DataElement(
            "name2", "shortName2", "description", true, DataElement.TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, null );
        int dataElementId2 = dataElementService.addDataElement( dataElement2 );
        
        DataElement dataElement3 = new DataElement(
            "name3", "shortName3", "description", true, DataElement.TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, null );
        int dataElementId3 = dataElementService.addDataElement( dataElement3 );
        
        /*
        reportStore.addReport( reportName, ReportStore.GENERIC );        

        //Collection reports = reportStore.getAllReports();
        //assertTrue( reports.size() == 1 );
        
        reportStore.addReportElement( reportName, ReportStore.DATAELEMENT, dataElementId1 );
        reportStore.addReportElement( reportName, ReportStore.DATAELEMENT, dataElementId2 );
        reportStore.addReportElement( reportName, ReportStore.DATAELEMENT, dataElementId3 );
        
        assertTrue( reportStore.getAllReportElements( reportName ).size() == 3 );
        
        reportStore.removeReportElement( reportName, ReportStore.DATAELEMENT + ":" + dataElementId3 );

        assertTrue( reportStore.getAllReportElements( reportName ).size() == 2 );
        
        reportStore.addChartElement( reportName, ReportStore.DATAELEMENT, dataElementId1 );
        reportStore.addChartElement( reportName, ReportStore.DATAELEMENT, dataElementId2 );
        reportStore.addChartElement( reportName, ReportStore.DATAELEMENT, dataElementId3 );
        
        assertTrue( reportStore.getAllChartElements( reportName ).size() == 3 );
        
        reportStore.removeChartElement( reportName, ReportStore.DATAELEMENT + ":" + dataElementId3 );
        
        assertTrue( reportStore.getAllChartElements( reportName ).size() == 2 );
        
        reportStore.setDesignTemplate( reportName, 1 );
        
        assertTrue( reportStore.getDesignTemplate( reportName ) == 1 );
        
        reportStore.setChartTemplate( reportName, 2 );
        
        assertTrue( reportStore.getChartTemplate( reportName ) == 2 );
        
        assertTrue( reportStore.deleteReport( reportName ) );
        */
    }
}
