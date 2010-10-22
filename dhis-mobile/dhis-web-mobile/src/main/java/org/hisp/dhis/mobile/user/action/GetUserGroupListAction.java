package org.hisp.dhis.mobile.user.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;

public class GetUserGroupListAction
    implements Action
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

    private List<UserGroup> userGroupList;

    public List<UserGroup> getUserGroupList()
    {
        return userGroupList;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        userGroupList = new ArrayList<UserGroup>( userGroupService.getAllUserGroups() );
        
        return SUCCESS;
    }

}
