package org.hisp.dhis.user.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork2.Action;

public class AddUserGroupFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserStore userStore;
    
    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }
    
    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private List<User> availableUsers;
    
    public List<User> getAvailableUsers()
    {
        return availableUsers;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        availableUsers = new ArrayList<User>( userStore.getAllUsers() );
        
        return SUCCESS;
    }
}
