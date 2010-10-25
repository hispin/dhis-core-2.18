package org.hisp.dhis.mobile.user.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork2.Action;

public class EditUserGroupFormAction
    implements Action
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

    private Integer userGroupId;

    public void setUserGroupId( Integer userGroupId )
    {
        this.userGroupId = userGroupId;
    }
    
    public Integer getUserGroupId()
    {
        return userGroupId;
    }

    private List<User> availableUsers = new ArrayList<User>();

    public List<User> getAvailableUsers()
    {
        return availableUsers;
    }

    private List<User> groupMembers = new ArrayList<User>();

    public List<User> getGroupMembers()
    {
        return groupMembers;

    }

    
   private  UserGroup group ;
   
   public UserGroup getGroup()
   {
       return group;
   }

   
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------


    public String execute()
        throws Exception
    {
       
        availableUsers = new ArrayList<User>( userStore.getAllUsers() );

        group = userGroupService.getUserGroup( userGroupId );
        
        groupMembers = new ArrayList<User>( group.getMembers() );
       
        
        return SUCCESS;
    }

}
