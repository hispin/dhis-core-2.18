package org.hisp.dhis.user.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork2.Action;

public class AddUserGroupAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {

        
        
        Set<User> userList = new HashSet<User>();
        
        for( Integer groupMember : groupMembers )
        {
            User user = userStore.getUser( groupMember );
            userList.add( user );
           
            
        }
        
        UserGroup userGroup = new UserGroup( name, userList );
        
        userGroupService.addUserGroup( userGroup );
        
        
        
        
        return SUCCESS;
    }

}
