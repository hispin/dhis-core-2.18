package org.hisp.dhis.user;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.hisp.dhis.setting.SystemSettingManager.KEY_CAN_GRANT_OWN_USER_AUTHORITY_GROUPS;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.AuditLogUtil;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.security.migration.MigrationPasswordManager;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.filter.UserAuthorityGroupCanIssueFilter;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 */
@Transactional
public class DefaultUserService
    implements UserService
{
    private static final Log log = LogFactory.getLog( DefaultUserService.class );

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

    private UserCredentialsStore userCredentialsStore;

    public void setUserCredentialsStore( UserCredentialsStore userCredentialsStore )
    {
        this.userCredentialsStore = userCredentialsStore;
    }

    private UserAuthorityGroupStore userAuthorityGroupStore;

    public void setUserAuthorityGroupStore( UserAuthorityGroupStore userAuthorityGroupStore )
    {
        this.userAuthorityGroupStore = userAuthorityGroupStore;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataElementCategoryService categoryService;
    
    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }
    
    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private MigrationPasswordManager passwordManager;

    public void setPasswordManager( MigrationPasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    // -------------------------------------------------------------------------
    // UserService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // User
    // -------------------------------------------------------------------------

    @Override
    public int addUser( User user )
    {
        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), user, AuditLogUtil.ACTION_CREATE );

        return userStore.save( user );
    }

    @Override
    public void updateUser( User user )
    {
        userStore.update( user );

        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), user, AuditLogUtil.ACTION_UPDATE );
    }

    @Override
    public void deleteUser( User user )
    {
        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), user, AuditLogUtil.ACTION_DELETE );

        userCredentialsStore.deleteUserCredentials( user.getUserCredentials() );

        userStore.delete( user );
    }

    @Override
    public Collection<User> getAllUsers()
    {
        return userStore.getAll();
    }

    @Override
    public List<User> getAllUsersBetween( int first, int max )
    {
        return userStore.getAllOrderedName( first, max );
    }

    @Override
    public List<User> getAllUsersBetweenByName( String name, int first, int max )
    {
        return userStore.getAllLikeName( name, first, max );
    }

    @Override
    public Collection<User> getUsersByLastUpdated( Date lastUpdated )
    {
        return userStore.getAllGeLastUpdated( lastUpdated );
    }

    @Override
    public User getUser( int userId )
    {
        return userStore.get( userId );
    }

    @Override
    public User getUser( String uid )
    {
        return userStore.getByUid( uid );
    }

    @Override
    public List<User> getUsersByUid( List<String> uids )
    {
        return userStore.getByUid( uids );
    }

    @Override
    public List<User> getManagedUsers( User user )
    {
        UserQueryParams params = new UserQueryParams( user );
        params.setCanManage( true );
        params.setAuthSubset( true );
        
        return userStore.getUsers( params );
    }

    @Override
    public int getManagedUserCount( User user )
    {
        UserQueryParams params = new UserQueryParams( user );
        params.setCanManage( true );
        params.setAuthSubset( true );
        
        return userStore.getUserCount( params );
    }
    
    @Override
    public List<User> getUsers( UserQueryParams params )
    {
        handleUserQueryParams( params );
        return userStore.getUsers( params );
    }

    @Override
    public int getUserCount( UserQueryParams params )
    {
        handleUserQueryParams( params );
        return userStore.getUserCount( params );
    }
    
    private void handleUserQueryParams( UserQueryParams params )
    {
        boolean disjointRoles = (Boolean) systemSettingManager.getSystemSetting( KEY_CAN_GRANT_OWN_USER_AUTHORITY_GROUPS, false );
        params.setDisjointRoles( disjointRoles );
        
        if ( params.getUser() == null )
        {
            params.setUser( currentUserService.getCurrentUser() );
        }
        
        if ( params.getUser() != null && params.getUser().isSuper() )
        {
            params.setCanManage( false );
            params.setAuthSubset( false );
        }

        if ( params.getInactiveMonths() != null )
        {
            Calendar cal = PeriodType.createCalendarInstance();
            cal.add( Calendar.MONTH, ( params.getInactiveMonths() * -1 ) );
            params.setInactiveSince( cal.getTime() );
        }
    }
    
    @Override
    public List<User> getUsersByPhoneNumber( String phoneNumber )
    {
        UserQueryParams params = new UserQueryParams();
        params.setPhoneNumber( phoneNumber );
        return getUsers( params );   
    }
    
    @Override
    public Set<CategoryOptionGroup> getCogDimensionConstraints( UserCredentials userCredentials )
    {
        Set<CategoryOptionGroup> groups = null;

        Set<CategoryOptionGroupSet> cogsConstraints = userCredentials.getCogsDimensionConstraints();

        if ( cogsConstraints != null && !cogsConstraints.isEmpty() )
        {
            groups = new HashSet<>();

            for ( CategoryOptionGroupSet cogs : cogsConstraints )
            {
                groups.addAll( categoryService.getCategoryOptionGroups( cogs ) );
            }
        }

        return groups;
    }

    @Override
    public Set<DataElementCategoryOption> getCoDimensionConstraints( UserCredentials userCredentials )
    {
        Set<DataElementCategoryOption> options = null;

        Set<DataElementCategory> catConstraints = userCredentials.getCatDimensionConstraints();

        if ( catConstraints != null && !catConstraints.isEmpty() )
        {
            options = new HashSet<>();

            for ( DataElementCategory category : catConstraints )
            {
                options.addAll( categoryService.getDataElementCategoryOptions( category ) );
            }
        }

        return options;
    }

    @Override
    public boolean isSuperUser( UserCredentials userCredentials )
    {
        if ( userCredentials == null )
        {
            return false;
        }

        for ( UserAuthorityGroup group : userCredentials.getUserAuthorityGroups() )
        {
            if ( group.getAuthorities().contains( "ALL" ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isLastSuperUser( UserCredentials userCredentials )
    {
        if ( !isSuperUser( userCredentials ) )
        {
            return false; // Cannot be last if not super user
        }

        Collection<UserCredentials> users = userCredentialsStore.getAllUserCredentials();

        for ( UserCredentials user : users )
        {
            if ( isSuperUser( user ) && !user.equals( userCredentials ) )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isSuperRole( UserAuthorityGroup userAuthorityGroup )
    {
        if ( userAuthorityGroup == null )
        {
            return false;
        }

        return ( userAuthorityGroup.getAuthorities().contains( "ALL" ) );
    }

    @Override
    public boolean isLastSuperRole( UserAuthorityGroup userAuthorityGroup )
    {
        Collection<UserAuthorityGroup> groups = userAuthorityGroupStore.getAll();

        for ( UserAuthorityGroup group : groups )
        {
            if ( isSuperRole( group ) && group.getId() != userAuthorityGroup.getId() )
            {
                return false;
            }
        }

        return true;
    }

    public boolean canAddOrUpdateUser( Collection<String> uids )
    {
    	User currentUser = currentUserService.getCurrentUser();
    	
    	if ( currentUser == null )
    	{
    	    return false;
    	}
    	
    	boolean canAdd = currentUser.getUserCredentials().isAuthorized( UserGroup.AUTH_USER_ADD );
    	
    	if ( canAdd )
    	{
    	    return true;
    	}
    	
    	boolean canAddInGroup = currentUser.getUserCredentials().isAuthorized( UserGroup.AUTH_USER_ADD_IN_GROUP );
    	
    	if ( !canAddInGroup )
    	{
    	    return false;
    	}
    	
    	for ( String uid : uids )
    	{
    	    UserGroup userGroup = userGroupService.getUserGroup( uid );
            
            if ( currentUser.canManage( userGroup ) )
            {
                return true;
            }
    	}
    	
    	return true;
    }
    
    // -------------------------------------------------------------------------
    // UserAuthorityGroup
    // -------------------------------------------------------------------------

    @Override
    public int addUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        return userAuthorityGroupStore.save( userAuthorityGroup );
    }

    @Override
    public void updateUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        userAuthorityGroupStore.update( userAuthorityGroup );
    }

    @Override
    public void deleteUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        userAuthorityGroupStore.delete( userAuthorityGroup );
    }

    @Override
    public Collection<UserAuthorityGroup> getAllUserAuthorityGroups()
    {
        return userAuthorityGroupStore.getAll();
    }

    @Override
    public UserAuthorityGroup getUserAuthorityGroup( int id )
    {
        return userAuthorityGroupStore.get( id );
    }

    @Override
    public UserAuthorityGroup getUserAuthorityGroup( String uid )
    {
        return userAuthorityGroupStore.getByUid( uid );
    }

    @Override
    public UserAuthorityGroup getUserAuthorityGroupByName( String name )
    {
        return userAuthorityGroupStore.getByName( name );
    }

    @Override
    public List<UserAuthorityGroup> getUserRolesByUid( Collection<String> uids )
    {
        return userAuthorityGroupStore.getByUid( uids );
    }

    @Override
    public Collection<UserAuthorityGroup> getUserRolesBetween( int first, int max )
    {
        return userAuthorityGroupStore.getAllOrderedName( first, max );
    }

    @Override
    public Collection<UserAuthorityGroup> getUserRolesBetweenByName( String name, int first, int max )
    {
        return userAuthorityGroupStore.getAllLikeName( name, first, max );
    }

    @Override
    public int getUserRoleCount()
    {
        return userAuthorityGroupStore.getCount();
    }

    @Override
    public int getUserRoleCountByName( String name )
    {
        return userAuthorityGroupStore.getCountLikeName( name );
    }

    @Override
    public int countDataSetUserAuthorityGroups( DataSet dataSet )
    {
        return userAuthorityGroupStore.countDataSetUserAuthorityGroups( dataSet );
    }
    
    @Override
    public void assignDataSetToUserRole( DataSet dataSet )
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( !currentUserService.currentUserIsSuper() && currentUser != null )
        {
            UserCredentials userCredentials = getUserCredentials( currentUser );

            for ( UserAuthorityGroup userAuthorityGroup : userCredentials.getUserAuthorityGroups() )
            {
                userAuthorityGroup.getDataSets().add( dataSet );

                updateUserAuthorityGroup( userAuthorityGroup );
            }
        }
    }

    @Override
    public void canIssueFilter( Collection<UserAuthorityGroup> userRoles )
    {
        User user = currentUserService.getCurrentUser();

        boolean canGrantOwnUserAuthorityGroups = (Boolean) systemSettingManager.getSystemSetting( KEY_CAN_GRANT_OWN_USER_AUTHORITY_GROUPS, false );

        FilterUtils.filter( userRoles, new UserAuthorityGroupCanIssueFilter( user, canGrantOwnUserAuthorityGroups ) );
    }

    // -------------------------------------------------------------------------
    // UserCredentials
    // -------------------------------------------------------------------------

    @Override
    public int addUserCredentials( UserCredentials userCredentials )
    {
        return userCredentialsStore.addUserCredentials( userCredentials );
    }

    @Override
    public void updateUserCredentials( UserCredentials userCredentials )
    {
        userCredentialsStore.updateUserCredentials( userCredentials );
    }

    @Override
    public Collection<UserCredentials> getAllUserCredentials()
    {
        return userCredentialsStore.getAllUserCredentials();
    }

    @Override
    public void encodeAndSetPassword( User user, String rawPassword )
    {
        encodeAndSetPassword( user.getUserCredentials(), rawPassword );
    }

    @Override
    public void encodeAndSetPassword( UserCredentials userCredentials, String rawPassword )
    {
        boolean isNewPassword = StringUtils.isBlank( userCredentials.getPassword() ) ||
            !passwordManager.legacyOrCurrentMatches( rawPassword, userCredentials.getPassword(), userCredentials.getUsername() );

        if ( isNewPassword )
        {
            userCredentials.setPasswordLastUpdated( new Date() );
        }

        userCredentials.setPassword( passwordManager.encode( rawPassword ) );
    }

    @Override
    public UserCredentials getUserCredentials( User user )
    {
        return userCredentialsStore.getUserCredentials( user );
    }

    @Override
    public UserCredentials getUserCredentialsByUsername( String username )
    {
        return userCredentialsStore.getUserCredentialsByUsername( username );
    }

    @Override
    public UserCredentials getUserCredentialsByOpenID( String openId )
    {
        return userCredentialsStore.getUserCredentialsByOpenID( openId );
    }

    @Override
    public void setLastLogin( String username )
    {
        UserCredentials credentials = getUserCredentialsByUsername( username );
        credentials.setLastLogin( new Date() );
        updateUserCredentials( credentials );
    }

    @Override
    public int getActiveUsersCount( int days )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.DAY_OF_YEAR, (days * -1) );

        return getActiveUsersCount( cal.getTime() );
    }

    @Override
    public int getActiveUsersCount( Date since )
    {
        UserQueryParams params = new UserQueryParams();
        params.setLastLogin( since );
        
        return getUserCount( params );
    }
    
    @Override
    public boolean credentialsNonExpired( UserCredentials credentials )
    {
        int credentialsExpires = systemSettingManager.credentialsExpires();

        if ( credentialsExpires == 0 )
        {
            return true;
        }

        int months = DateUtils.monthsBetween( credentials.getPasswordLastUpdated(), new Date() );

        return months < credentialsExpires;
    }
}
