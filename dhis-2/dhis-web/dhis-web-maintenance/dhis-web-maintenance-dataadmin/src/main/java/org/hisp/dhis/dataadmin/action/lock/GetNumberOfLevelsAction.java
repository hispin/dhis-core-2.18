/**
 * 
 */
package org.hisp.dhis.dataadmin.action.lock;

import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Brajesh Murari
 * @version $Id$
 */
public class GetNumberOfLevelsAction extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnitLevel> levels;
    
    public List<OrganisationUnitLevel> getLevels()
    {
        return levels;
    }

    // -------------------------------------------------------------------------
    // ActionSupport implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        levels = organisationUnitService.getOrganisationUnitLevels();
        
        return SUCCESS;
    }
}
