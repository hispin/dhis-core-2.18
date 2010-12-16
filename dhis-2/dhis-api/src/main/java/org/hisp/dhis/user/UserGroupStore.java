package org.hisp.dhis.user;

import java.util.Collection;


public interface UserGroupStore
{
    String ID = UserGroupStore.class.getName();
    
    int addUserGroup( UserGroup userGroup );
    
    void updateUserGroup( UserGroup userGroup );
    
    void deleteUserGroup( UserGroup userGroup );
    
    UserGroup getUserGroup( int userGroupId );
    
    Collection<UserGroup> getAllUserGroups();
    
    UserGroup getUserGroupByName( String name );
}
