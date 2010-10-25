package org.hisp.dhis.mobile.user.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork2.Action;

public class UpdateUserGroupAction implements Action
{ 
    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private List<Integer> groupMembers;

    public void setGroupMembers( List<Integer> groupMembers )
    {
        this.groupMembers = groupMembers;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private Integer userGroupId;
    
    public void setUserGroupId( Integer userGroupId )
    {
        this.userGroupId = userGroupId;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        System.out.println("  groupMembers size : "+ groupMembers.size());
        
        Set<User> userList = new HashSet<User>();

        for ( Integer groupMember : groupMembers )
        {
            User user = userStore.getUser( groupMember );
            userList.add( user );
        }

        UserGroup userGroup = userGroupService.getUserGroup( userGroupId );
        
        userGroup.setName( name );
        userGroup.setMembers( userList );

        userGroupService.updateUserGroup( userGroup);
        
        return SUCCESS;
    }

}
