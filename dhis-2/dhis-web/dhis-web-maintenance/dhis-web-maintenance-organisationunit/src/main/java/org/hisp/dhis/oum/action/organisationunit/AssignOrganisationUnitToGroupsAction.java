package org.hisp.dhis.oum.action.organisationunit;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.util.ConversionUtils;

import com.opensymphony.xwork2.Action;

public class AssignOrganisationUnitToGroupsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer organisationUnitId;

    public Integer getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private Collection<String> organisationUnitGroupId;

    public void setOrganisationUnitGroupId( Collection<String> organisationUnitGroupId )
    {
        this.organisationUnitGroupId = organisationUnitGroupId;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        
        for ( Integer id : ConversionUtils.getIntegerCollection( organisationUnitGroupId ) )
        {
            OrganisationUnitGroup group = organisationUnitGroupService.getOrganisationUnitGroup( id );
            
            group.getMembers().add( unit );
            
            organisationUnitGroupService.updateOrganisationUnitGroup( group );
        }
        
        return SUCCESS;
    }
}
