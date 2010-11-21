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
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        DataSet selectedDataSet = dataSetService.getDataSet( dataSetId );
        
        if ( selectedDataSet != null )
        {
            // -----------------------------------------------------------------
            // Check if previous data set has same period type as selected
            // -----------------------------------------------------------------

            DataSet previousDataSet = selectedStateManager.getSelectedDataSet();
            
            if ( previousDataSet != null && previousDataSet.getPeriodType().equals( selectedDataSet.getPeriodType() ) )
            {
                periodValid = true;
            }
            else
            {
                selectedStateManager.clearSelectedPeriod();
            }

            // -----------------------------------------------------------------
            // Load periods for period type of selected data set
            // -----------------------------------------------------------------

            selectedStateManager.setSelectedDataSet( selectedDataSet );
            
            periods = selectedStateManager.getPeriodList();
            
            for ( Period period : periods )
            {
                period.setName( format.formatPeriod( period ) );
            }

            // -----------------------------------------------------------------
            // Clear display mode when loading new data set
            // -----------------------------------------------------------------

            selectedStateManager.clearSelectedDisplayMode();
        }

        return SUCCESS;
    }
}
