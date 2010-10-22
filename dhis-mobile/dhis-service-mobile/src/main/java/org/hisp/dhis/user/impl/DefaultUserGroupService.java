package org.hisp.dhis.user.impl;

import java.util.Collection;

import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserGroupStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultUserGroupService implements UserGroupService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserGroupStore userGroupStore;

    public void setUserGroupStore( UserGroupStore userGroupStore )
    {
        this.userGroupStore = userGroupStore;
    }
    
    // -------------------------------------------------------------------------
    // UserGroup
    // -------------------------------------------------------------------------

    @Override
    public int addUserGroup( UserGroup userGroup )
    {
        return userGroupStore.addUserGroup( userGroup );
    }

    @Override
    public void deleteUserGroup( UserGroup userGroup )
    {
        userGroupStore.deleteUserGroup( userGroup );
    }

    @Override
    public void updateUserGroup( UserGroup userGroup )
    {
        userGroupStore.updateUserGroup( userGroup );
    }

    
    @Override
    public Collection<UserGroup> getAllUserGroups()
    {
        return userGroupStore.getAllUserGroups();
    }

    @Override
    public UserGroup getUserGroup( int userGroupId )
    {
        return userGroupStore.getUserGroup( userGroupId );
    }

    @Override
    public UserGroup getUserGroupByName( String name )
    {
        return userGroupStore.getUserGroupByName( name );
    }

}
