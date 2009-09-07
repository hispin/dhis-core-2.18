package org.hisp.dhis.dashboard.ds.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetStore;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.util.comparator.PeriodStartDateComparator;

import com.opensymphony.xwork2.Action;

public class GenerateGroupWiseDataStatusFormAction
    implements Action
{

    /* Dependencies */
    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    private DataSetStore dataSetStore;

    public void setDataSetStore( DataSetStore dataSetStore )
    {
        this.dataSetStore = dataSetStore;
    }

    /* Output Parameters */
    private List<DataSet> dataSetList;

    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    private List<Period> monthlyPeriods;

    public List<Period> getMonthlyPeriods()
    {
        return monthlyPeriods;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    public String execute()
        throws Exception
    {
        /* DataSet List */
        
        dataSetList = new ArrayList<DataSet>( dataSetStore.getAllDataSets() );
        
        Collections.sort( dataSetList, new DataSetNameComparator() );

        /* Monthly Periods */
        //monthlyPeriods = new ArrayList<Period>( periodStore.getPeriodsByPeriodType( new MonthlyPeriodType() ) );
        //Collections.sort( monthlyPeriods, new PeriodStartDateComparator() );
        //simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        return SUCCESS;
    }
 

}// class end
