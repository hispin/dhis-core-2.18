package org.hisp.dhis.de.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.de.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

public class LoadPeriodsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    private boolean next;

    public void setNext( boolean next )
    {
        this.next = next;
    }

    private boolean previous;

    public void setPrevious( boolean previous )
    {
        this.previous = previous;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    private boolean selectionValid;

    public boolean isSelectionValid()
    {
        return selectionValid;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        DataSet selectedDataSet = dataSetService.getDataSet( dataSetId );
        
        if ( selectedDataSet != null )
        {
            DataSet previousDataSet = selectedStateManager.getSelectedDataSet();
            
            selectionValid = previousDataSet != null && previousDataSet.getPeriodType().equals( selectedDataSet.getPeriodType() );
            
            selectedStateManager.setSelectedDataSet( selectedDataSet );
            
            if ( next )
            {
                selectedStateManager.nextPeriodSpan();
            }
            else if ( previous )
            {
                selectedStateManager.previousPeriodSpan();
            }
            
            periods = selectedStateManager.getPeriodList();
            
            for ( Period period : periods )
            {
                period.setName( format.formatPeriod( period ) );
            }
        }
        
        return SUCCESS;
    }
}
