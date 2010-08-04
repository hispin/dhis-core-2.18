package org.hisp.dhis.dataadmin.action.lock;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

public class OrgUnitValidateAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        Collection<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();
        organisationUnits = selectionTreeManager.getSelectedOrganisationUnits();
        
        if ( organisationUnits == null || organisationUnits.size() == 0 )
        {
            message = "Please Select Org Unit";        
            return INPUT;
        }

        message = "Success"; 
        
        return SUCCESS;
    }
}
