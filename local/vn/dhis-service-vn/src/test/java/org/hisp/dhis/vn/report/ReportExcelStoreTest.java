package org.hisp.dhis.vn.report;

import static junit.framework.Assert.assertNotNull;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

public class ReportExcelStoreTest
    extends DhisSpringTest
{
    private ReportExcelStore reportExcelStore;
    
    @Override
    public void setUpTest()
    {
        reportExcelStore = (ReportExcelStore) getBean( ReportExcelStore.ID );
    }
    
    @Test
    public void testAddGet()
    {
        ReportExcelInterface report = new ReportExcelNormal( "ReportA", "TemplateA", 1, 1, 1, 1 );
        
        int id = reportExcelStore.addReport( report );
        
        assertNotNull( reportExcelStore.getReport( id ) );
    }
}
