package org.hisp.dhis.mobile.user.action;

import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;


 
public class GetUserGroupAction implements Action 
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

  //  private UserStore userStore;

    //public void setUserStore( UserStore userStore )
    //{
    //    this.userStore = userStore;
   // }

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }


    // -------------------------------------------------------------------------
    // Input/output
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

    private  UserGroup group ;
    
    public UserGroup getGroup()
    {
        return group;
    }

    private int memberCount;

    public int getMemberCount()
    {
        return memberCount;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        group = userGroupService.getUserGroup(userGroupId );

        memberCount =group.getMembers().size();

        return SUCCESS;
    }
    
    
    
    
    
    
    
}
