package org.hisp.dhis.user;

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultUserGroupService implements UserGroupService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private GenericIdentifiableObjectStore<UserGroup> userGroupStore;
    
    public void setUserGroupStore( GenericIdentifiableObjectStore<UserGroup> userGroupStore )
    {
        this.userGroupStore = userGroupStore;
    }

    // -------------------------------------------------------------------------
    // UserGroup
    // -------------------------------------------------------------------------

    @Override
    public void addUserGroup( UserGroup userGroup )
    {
        userGroupStore.saveOrUpdate( userGroup );
    }

    @Override
    public void deleteUserGroup( UserGroup userGroup )
    {
        userGroupStore.delete( userGroup );
    }

    @Override
    public void updateUserGroup( UserGroup userGroup )
    {
        userGroupStore.update( userGroup );
    }

    
    @Override
    public Collection<UserGroup> getAllUserGroups()
    {
        return userGroupStore.getAll();
    }

    @Override
    public UserGroup getUserGroup( int userGroupId )
    {
        return userGroupStore.get( userGroupId );
    }

    @Override
    public UserGroup getUserGroupByName( String name )
    {
        return userGroupStore.getByName( name );
    }

}
