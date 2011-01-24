package org.hisp.dhis.de.action;

import org.hisp.dhis.de.state.SelectedStateManager;

import com.opensymphony.xwork2.Action;

public class LoadOrganisationUnitAction
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
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        selectedStateManager.clearSelectedOrganisationUnits();
        selectedStateManager.clearSelectedDataSet();
        selectedStateManager.clearSelectedPeriod();

        return SUCCESS;
    }
}
