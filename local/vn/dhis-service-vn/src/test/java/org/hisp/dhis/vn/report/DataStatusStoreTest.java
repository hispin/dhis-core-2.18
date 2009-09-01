package org.hisp.dhis.vn.report;

import static junit.framework.Assert.assertNotNull;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.vn.status.DataStatus;
import org.hisp.dhis.vn.status.DataStatusStore;
import org.junit.Test;

public class DataStatusStoreTest
    extends DhisSpringTest
{
    private DataStatusStore dataStatusStore;

    private PeriodType periodType;
    
    private DataSet dataSet;
    
    @Override
    public void setUpTest()
    {
        dataStatusStore = (DataStatusStore) getBean( DataStatusStore.ID );
        
        dataSetService = (DataSetService) getBean( DataSetService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );
        
        periodType = periodService.getPeriodTypeByClass( MonthlyPeriodType.class );
        
        dataSet = createDataSet( 'A', periodType );
        
        dataSetService.addDataSet( dataSet );
    }
    
    @Test
    public void testAddGet()
    {
        DataStatus dataStatus = new DataStatus( dataSet, true, periodType );
        
        int id = dataStatusStore.save( dataStatus );
        
        assertNotNull( dataStatusStore.get( id ) );
    }
}
