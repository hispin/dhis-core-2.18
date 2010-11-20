package org.hisp.dhis.de.action;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.de.state.SelectedStateManager;

import com.opensymphony.xwork2.Action;

public class LoadDisplayModesAction
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

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private boolean customForm;
    
    public boolean isCustomForm()
    {
        return customForm;
    }

    private boolean sectionForm;

    public boolean isSectionForm()
    {
        return sectionForm;
    }
    
    private String displayMode;

    public String getDisplayMode()
    {
        return displayMode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        DataSet dataSet = selectedStateManager.getSelectedDataSet();
        
        customForm = dataSet.hasDataEntryForm();

        sectionForm = dataSet.hasSections();

        displayMode = selectedStateManager.getSelectedDisplayMode();
        
        return SUCCESS;
    }
}
