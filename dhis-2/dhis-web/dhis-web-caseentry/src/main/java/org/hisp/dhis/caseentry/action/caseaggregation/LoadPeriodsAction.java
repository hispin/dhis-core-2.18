package org.hisp.dhis.caseentry.action.caseaggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.caseentry.state.PeriodGenericManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

public class LoadPeriodsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodGenericManager periodGenericManager;

    public void setPeriodGenericManager( PeriodGenericManager periodGenericManager )
    {
        this.periodGenericManager = periodGenericManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    private boolean periodValid;

    public boolean isPeriodValid()
    {
        return periodValid;
    }

    private Collection<DataElement> significantZeros = new HashSet<DataElement>();

    public Collection<DataElement> getSignificantZeros()
    {
        return significantZeros;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
    {
        DataSet selectedDataSet = dataSetService.getDataSet( dataSetId );

        periodGenericManager.clearSelectedPeriod();
        periodGenericManager.clearBasePeriod();

        periodGenericManager.setPeriodType( selectedDataSet.getPeriodType().getName() );

        if ( selectedDataSet != null )
        {
            periods = getPeriodList( (CalendarPeriodType) selectedDataSet.getPeriodType() );
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Support method
    // -------------------------------------------------------------------------

    private List<Period> getPeriodList( CalendarPeriodType periodType )
    {
        Period basePeriod = periodType.createPeriod();

        List<Period> periods = periodType.generatePeriods( basePeriod );

        Date now = new Date();

        Iterator<Period> iterator = periods.iterator();

        while ( iterator.hasNext() )
        {
            if ( iterator.next().getStartDate().after( now ) )
            {
                iterator.remove();
            }
        }

        return periods;
    }

}
