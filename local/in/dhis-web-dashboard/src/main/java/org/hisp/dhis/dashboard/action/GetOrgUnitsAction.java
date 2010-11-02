package org.hisp.dhis.dashboard.action;

import java.util.Collection;
import java.util.Date;

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
        System.out.println( "StartTime: "+ new Date() );
        /* OrganisationUnit */
        if ( orgUnitId != null )
        {
            orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        }

        orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );
        maxOrgUnitLevel = orgUnitLevel;
        int maxNumberofLevels = organisationUnitService.getNumberOfOrganisationalLevels();
        
        // Hardcoded : if it is Tabular Analysis, Null Reporter
        if( type != null && type.equalsIgnoreCase( "ta" ) )
        {
            for( int i = orgUnitLevel+1; i <= maxNumberofLevels; i++ )
            {
                Collection<OrganisationUnit> tempOrgUnitList = organisationUnitService.getOrganisationUnitsAtLevel( i, orgUnit );
                if( tempOrgUnitList == null || tempOrgUnitList.size() == 0 )
                {
                    maxOrgUnitLevel = i;
                    break;
                }
            }
        }
        
        System.out.println( "EndTime: "+ new Date() );
        
        /*
        orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );
        maxOrgUnitLevel = orgUnitLevel;
        
        // Hardcoded : if it is Tabular Analysis, Null Reporter
        if( type != null && type.equalsIgnoreCase( "ta" ) )
        {
            List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId ) );
            
            
            for( OrganisationUnit ou : orgUnitTree )
            {
                if( organisationUnitService.getLevelOfOrganisationUnit( ou ) > maxOrgUnitLevel )
                {
                    maxOrgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( ou );
                }
            }
        }
        */

        return SUCCESS;
    }

}
