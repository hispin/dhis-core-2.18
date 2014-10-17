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
import static org.hisp.dhis.setting.SystemSettingManager.KEY_ONLY_MANAGE_WITHIN_USER_GROUPS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.AuditLogUtil;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.filter.UserAuthorityGroupCanIssueFilter;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.Filter;
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
    
    private UserSettingStore userSettingStore;

    public void setUserSettingStore( UserSettingStore userSettingStore )
    {
        this.userSettingStore = userSettingStore;
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
    
    private SecurityService securityService;
    
    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Implementing methods
    // -------------------------------------------------------------------------

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

        return (userAuthorityGroup.getAuthorities().contains( "ALL" )) ? true : false;
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
    public Collection<UserCredentials> getUsersByOrganisationUnitBetween( OrganisationUnit unit, int first, int max )
    {
        return userCredentialsStore.getUsersByOrganisationUnitBetween( unit, first, max );
    }

    @Override
    public Collection<UserCredentials> getUsersByOrganisationUnitBetweenByName( OrganisationUnit unit, String userName,
        int first, int max )
    {
        return userCredentialsStore.getUsersByOrganisationUnitBetweenByName( unit, userName, first, max );
    }

    @Override
    public int getUsersByOrganisationUnitCount( OrganisationUnit unit )
    {
        return userCredentialsStore.getUsersByOrganisationUnitCount( unit );
    }

    @Override
    public int getUsersByOrganisationUnitCountByName( OrganisationUnit unit, String userName )
    {
        return userCredentialsStore.getUsersByOrganisationUnitCountByName( unit, userName );
    }

    @Override
    public Collection<User> getUsersByPhoneNumber( String phoneNumber )
    {
        return userStore.getUsersByPhoneNumber( phoneNumber );
    }

    @Override
    public Collection<User> getUsersByName( String name )
    {
        return userStore.getUsersByName( name );
    }

    @Override
    public Collection<User> getUsersWithoutOrganisationUnit()
    {
        return userStore.getUsersWithoutOrganisationUnit();
    }

    @Override
    public int getUsersWithoutOrganisationUnitCount()
    {
        return userCredentialsStore.getUsersWithoutOrganisationUnitCount();
    }

    @Override
    public int getUsersWithoutOrganisationUnitCountByName( String userName )
    {
        return userCredentialsStore.getUsersWithoutOrganisationUnitCountByName( userName );
    }

    @Override
    public User searchForUser( String query )
    {
        User user = userStore.getByUid( query );

        if ( user == null )
        {
            UserCredentials credentials = userCredentialsStore.getUserCredentialsByUsername( query );
            user = credentials != null ? credentials.getUser() : null;
        }

        return user;
    }

    @Override
    public List<User> queryForUsers( String query )
    {
        List<User> users = new ArrayList<>();

        User uidUser = userStore.getByUid( query );

        if ( uidUser != null )
        {
            users.add( uidUser );
        }

        users.addAll( userStore.getAllLikeName( query, 0, 1000 ) ); //TODO

        return users;
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

            for ( DataElementCategory cat : catConstraints )
            {
                for ( DataElementCategoryOption o : cat.getCategoryOptions() )
                {
                    if ( securityService.canRead( o ) )
                    {
                        options.add( o );
                    }
                }
            }
        }

        return options;
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
    public Collection<UserCredentials> getUsersBetween( int first, int max )
    {
        return userCredentialsStore.getUsersBetween( first, max );
    }

    @Override
    public Collection<UserCredentials> getUsersBetweenByName( String username, int first, int max )
    {
        return userCredentialsStore.getUsersBetweenByName( username, first, max );
    }

    @Override
    public int getUserCount()
    {
        return userCredentialsStore.getUserCount();
    }

    @Override
    public int getUserCountByName( String userName )
    {
        return userCredentialsStore.getUserCountByName( userName );
    }

    @Override
    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetween( int first, int max )
    {
        return userCredentialsStore.getUsersWithoutOrganisationUnitBetween( first, max );
    }

    @Override
    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetweenByName( String username, int first, int max )
    {
        return userCredentialsStore.getUsersWithoutOrganisationUnitBetweenByName( username, first, max );
    }

    @Override
    public Collection<UserCredentials> searchUsersByName( String name )
    {
        return userCredentialsStore.searchUsersByName( name );
    }

    @Override
    public Collection<UserCredentials> searchUsersByName( String name, int first, int max )
    {
        return userCredentialsStore.searchUsersByName( name, first, max );
    }

    @Override
    public void setLastLogin( String username )
    {
        UserCredentials credentials = getUserCredentialsByUsername( username );
        credentials.setLastLogin( new Date() );
        updateUserCredentials( credentials );
    }

    @Override
    public Collection<UserCredentials> getSelfRegisteredUserCredentials( int first, int max )
    {
        return userCredentialsStore.getSelfRegisteredUserCredentials( first, max );
    }

    @Override
    public int getSelfRegisteredUserCredentialsCount()
    {
        return userCredentialsStore.getSelfRegisteredUserCredentialsCount();
    }

    @Override
    public Collection<UserCredentials> getInactiveUsers( int months )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.MONTH, (months * -1) );

        return userCredentialsStore.getInactiveUsers( cal.getTime() );
    }

    @Override
    public Collection<UserCredentials> getInactiveUsers( int months, int first, int max )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.MONTH, (months * -1) );

        return userCredentialsStore.getInactiveUsers( cal.getTime(), first, max );
    }

    @Override
    public int getInactiveUsersCount( int months )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.MONTH, (months * -1) );

        return userCredentialsStore.getInactiveUsersCount( cal.getTime() );
    }

    @Override
    public int getActiveUsersCount( int days )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.DAY_OF_YEAR, (days * -1) );

        return userCredentialsStore.getActiveUsersCount( cal.getTime() );
    }

    @Override
    public int getActiveUsersCount( Date since )
    {
        return userCredentialsStore.getActiveUsersCount( since );
    }

    @Override
    public void canUpdateUsersFilter( Collection<User> users )
    {
        FilterUtils.filter( users,
            new Filter<User>()
            {
                @Override
                public boolean retain( User object )
                {
                    return canUpdate( object.getUserCredentials() );
                }
            }
        );
    }

    @Override
    public void canUpdateFilter( Collection<UserCredentials> userCredentials )
    {
        FilterUtils.filter( userCredentials,
            new Filter<UserCredentials>()
            {
                @Override
                public boolean retain( UserCredentials object )
                {
                    return canUpdate( object );
                }
            }
        );
    }

    @Override
    public boolean canUpdate( UserCredentials userCredentials )
    {
        return hasAuthorityToUpdateUser( userCredentials ) && hasGroupsToUpdateUser( userCredentials );
    }

    // -------------------------------------------------------------------------
    // UserSettings
    // -------------------------------------------------------------------------

    @Override
    public void addUserSetting( UserSetting userSetting )
    {
        userSettingStore.addUserSetting( userSetting );
    }

    @Override
    public void addOrUpdateUserSetting( UserSetting userSetting )
    {
        UserSetting setting = getUserSetting( userSetting.getUser(), userSetting.getName() );

        if ( setting != null )
        {
            setting.mergeWith( userSetting );
            updateUserSetting( setting );
        }
        else
        {
            addUserSetting( userSetting );
        }
    }

    @Override
    public void updateUserSetting( UserSetting userSetting )
    {
        userSettingStore.updateUserSetting( userSetting );
    }

    @Override
    public void deleteUserSetting( UserSetting userSetting )
    {
        userSettingStore.deleteUserSetting( userSetting );
    }

    @Override
    public Collection<UserSetting> getAllUserSettings( User user )
    {
        return userSettingStore.getAllUserSettings( user );
    }

    @Override
    public Collection<UserSetting> getUserSettings( String name )
    {
        return userSettingStore.getUserSettings( name );
    }

    @Override
    public UserSetting getUserSetting( User user, String name )
    {
        return userSettingStore.getUserSetting( user, name );
    }

    @Override
    public Serializable getUserSettingValue( User user, String name, Serializable defaultValue )
    {
        UserSetting setting = getUserSetting( user, name );

        return setting != null && setting.getValue() != null ? setting.getValue() : defaultValue;
    }

    @Override
    public Map<User, Serializable> getUserSettings( String name, Serializable defaultValue )
    {
        Map<User, Serializable> map = new HashMap<>();

        for ( UserSetting setting : userSettingStore.getUserSettings( name ) )
        {
            map.put( setting.getUser(), setting.getValue() != null ? setting.getValue() : defaultValue );
        }

        return map;
    }

    @Override
    public Collection<User> getUsersByOrganisationUnits( Collection<OrganisationUnit> units )
    {
        return userStore.getUsersByOrganisationUnits( units );
    }

    @Override
    public void removeUserSettings( User user )
    {
        userStore.removeUserSettings( user );
    }

    @Override
    public Collection<String> getUsernames( String query, Integer max )
    {
        return userCredentialsStore.getUsernames( query, max );
    }

    @Override
    public int countDataSetUserAuthorityGroups( DataSet dataSet )
    {
        return userAuthorityGroupStore.countDataSetUserAuthorityGroups( dataSet );
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

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Determines if the current user has all the authorities required to
     * update a user.
     *
     * @param userCredentials The user to be updated.
     * @return true if current user has authorities, else false.
     */
    private boolean hasAuthorityToUpdateUser( UserCredentials userCredentials )
    {
        UserCredentials currentUserCredentials = currentUserService.getCurrentUser().getUserCredentials();

        boolean canGrantOwnUserAuthorityGroups = (Boolean) systemSettingManager.getSystemSetting( KEY_CAN_GRANT_OWN_USER_AUTHORITY_GROUPS, false );

        return currentUserCredentials != null && userCredentials != null
                && currentUserCredentials.canIssueAll( userCredentials.getUserAuthorityGroups(), canGrantOwnUserAuthorityGroups );
    }

    /**
     * Determines if the current user read/write access to at least one group
     * to which the user belongs, if this is a requirement on this system
     * for updating a user.
     *
     * @param userCredentials The user to be updated.
     * @return true if current user has read/write access to a group to which
     * the user belongs, or if this requirement is not applicable, else false.
     */
    private boolean hasGroupsToUpdateUser( UserCredentials userCredentials )
    {
        User user = currentUserService.getCurrentUser();

        boolean onlyManageWithinUserGroups = (Boolean) systemSettingManager.getSystemSetting( KEY_ONLY_MANAGE_WITHIN_USER_GROUPS, false );

        if ( onlyManageWithinUserGroups && !user.getUserCredentials().getAllAuthorities().contains( UserAuthorityGroup.AUTHORITY_ALL ) )
        {
            if ( userCredentials.getUser().getGroups() != null )
            {
                for ( UserGroup group : userCredentials.getUser().getGroups() )
                {
                    if ( securityService.canWrite( group ) )
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        return true;
    }
}
