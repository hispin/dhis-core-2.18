package org.hisp.dhis.dataanalyser.action;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class GetOrgUnitsAction implements Action
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
    
    private String type;
    
    public void setType( String type )
    {
        this.type = type;
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
        
     //   System.out.println("org Id is : " + orgUnitId );
        
        if ( orgUnitId != null )
        {
            orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        }
        
        System.out.println(" orgUnit Id is : " + orgUnit.getId() + " , orgUnit Name is : " + orgUnit.getName() );
        orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );
        //orgUnitLevel = orgUnit.getLevel();
        maxOrgUnitLevel = organisationUnitService.getNumberOfOrganisationalLevels();
        
        // Hardcoded : if it is Tabular Analysis, Null Reporter
        if( type != null && type.equalsIgnoreCase( "ta" ) )
        {
            for( int i = orgUnitLevel+1; i <= maxOrgUnitLevel; i++ )
            {
                Collection<OrganisationUnit> tempOrgUnitList = organisationUnitService.getOrganisationUnitsAtLevel( i, orgUnit );
                if( tempOrgUnitList == null || tempOrgUnitList.size() == 0 )
                {
                    maxOrgUnitLevel = i-1;
                    break;
                }
            }
        }
        
        return SUCCESS;
    }

}
