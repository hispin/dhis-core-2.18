package org.hisp.dhis.dashboard.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.ActionSupport;

public class GetOrgUnitsAction
    extends ActionSupport
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
    // Getters & Setters
    // -------------------------------------------------------------------------

    private Integer orgUnitId;

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private OrganisationUnit orgUnit;

    public OrganisationUnit getOrgUnit()
    {
        return orgUnit;
    }

    private Integer orgUnitLevel;

    public Integer getOrgUnitLevel()
    {
        return orgUnitLevel;
    }

    private Integer maxOrgUnitLevel;
    
    public Integer getMaxOrgUnitLevel()
    {
        return maxOrgUnitLevel;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------



    public String execute()
        throws Exception
    {
        /* OrganisationUnit */
        if ( orgUnitId != null )
        {
            orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        }

        orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );
        maxOrgUnitLevel = orgUnitLevel;

        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
        
        for( OrganisationUnit ou : orgUnitTree )
        {
            if( organisationUnitService.getLevelOfOrganisationUnit( ou ) > maxOrgUnitLevel )
            {
                maxOrgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( ou );
            }
        }

        /*if ( orgUnit.getChildren() == null || orgUnit.getChildren().size() == 0 )
        {
            orgUnitLevel = organisationUnitService.getNumberOfOrganisationalLevels();
        }
*/
        System.out.println( "OrgUnitLevel : " + orgUnitLevel + " Name : " + orgUnit.getShortName() + " MaxLevel : "+ maxOrgUnitLevel );

        return SUCCESS;
    }

}
