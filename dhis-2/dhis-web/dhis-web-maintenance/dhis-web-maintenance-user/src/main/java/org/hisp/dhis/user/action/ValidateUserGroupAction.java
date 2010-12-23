package org.hisp.dhis.user.action;

import java.util.ArrayList;

import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.ActionSupport;

public class ValidateUserGroupAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private Integer id;

    private String name;

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        UserGroup group = userGroupService.getUserGroupByName( name );
        
        if( (id==null && group!= null ) || ( id!= null && id!= group.getId())){
            
            return INPUT;
        }
        return SUCCESS;
    }

}
