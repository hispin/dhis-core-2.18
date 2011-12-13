package org.hisp.dhis.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.filter.UserCredentialsCanUpdateFilter;
import org.hisp.dhis.system.util.AuditLogUtil;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

/**
 * @author Chau Thu Tran
 * @version $Id$
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

    private GenericIdentifiableObjectStore<UserAuthorityGroup> userRoleStore;

    public void setUserRoleStore( GenericIdentifiableObjectStore<UserAuthorityGroup> userRoleStore )
    {
        this.userRoleStore = userRoleStore;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Implementing methods
    // -------------------------------------------------------------------------

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

    public boolean isLastSuperUser( UserCredentials userCredentials )
    {
        Collection<UserCredentials> users = userStore.getAllUserCredentials();

        for ( UserCredentials user : users )
        {
            if ( isSuperUser( user ) && user.getId() != userCredentials.getId() )
            {
                return false;
            }
        }

        return true;
    }

    public boolean isSuperRole( UserAuthorityGroup userAuthorityGroup )
    {
        if ( userAuthorityGroup == null )
        {
            return false;
        }

        return (userAuthorityGroup.getAuthorities().contains( "ALL" )) ? true : false;
    }

    public boolean isLastSuperRole( UserAuthorityGroup userAuthorityGroup )
    {
        Collection<UserAuthorityGroup> groups = userStore.getAllUserAuthorityGroups();

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

    public int addUser( User user )
    {
        log.info( AuditLogUtil.logMessage( currentUserService.getCurrentUsername(), AuditLogUtil.ACTION_ADD, User.class
            .getSimpleName(), user.getName() ) );

        return userStore.addUser( user );
    }

    public void deleteUser( User user )
    {
        userStore.deleteUser( user );

        log.info( AuditLogUtil.logMessage( currentUserService.getCurrentUsername(), AuditLogUtil.ACTION_DELETE,
            User.class.getSimpleName(), user.getName() ) );
    }

    public void updateUser( User user )
    {
        userStore.updateUser( user );

        log.info( AuditLogUtil.logMessage( currentUserService.getCurrentUsername(), AuditLogUtil.ACTION_EDIT,
            User.class.getSimpleName(), user.getName() ) );
    }

    public Collection<User> getAllUsers()
    {
        return userStore.getAllUsers();
    }

    public User getUser( int userId )
    {
        return userStore.getUser( userId );
    }

    public User getUser( String uid )
    {
        return userStore.getUser( uid );
    }

    public int getUserCount()
    {
        return userStore.getUserCount();
    }

    public int getUserCountByName( String userName )
    {
        return userStore.getUserCountByName( userName );
    }

    public Collection<UserCredentials> getUsers( final Collection<Integer> identifiers, User user )
    {
        Collection<UserCredentials> userCredentialsS = getAllUserCredentials();

        FilterUtils.filter( userCredentialsS, new UserCredentialsCanUpdateFilter( user ) );

        return identifiers == null ? userCredentialsS : FilterUtils.filter( userCredentialsS,
            new Filter<UserCredentials>()
            {
                public boolean retain( UserCredentials object )
                {
                    return identifiers.contains( object.getId() );
                }
            } );
    }

    public Collection<UserCredentials> getUsersByOrganisationUnitBetween( OrganisationUnit unit, int first, int max )
    {
        return userStore.getUsersByOrganisationUnitBetween( unit, first, max );
    }

    public Collection<UserCredentials> getUsersByOrganisationUnitBetweenByName( OrganisationUnit unit, String userName,
                                                                                int first, int max )
    {
        return userStore.getUsersByOrganisationUnitBetweenByName( unit, userName, first, max );
    }

    public int getUsersByOrganisationUnitCount( OrganisationUnit unit )
    {
        return userStore.getUsersByOrganisationUnitCount( unit );
    }

    public int getUsersByOrganisationUnitCountByName( OrganisationUnit unit, String userName )
    {
        return userStore.getUsersByOrganisationUnitCountByName( unit, userName );
    }

    public Collection<User> getUsersByPhoneNumber( String phoneNumber )
    {
        return userStore.getUsersByPhoneNumber( phoneNumber );
    }

    public Collection<User> getUsersWithoutOrganisationUnit()
    {
        return userStore.getUsersWithoutOrganisationUnit();
    }

    public int getUsersWithoutOrganisationUnitCount()
    {
        return userStore.getUsersWithoutOrganisationUnitCount();
    }

    public int getUsersWithoutOrganisationUnitCountByName( String userName )
    {
        return userStore.getUsersWithoutOrganisationUnitCountByName( userName );
    }

    // -------------------------------------------------------------------------
    // UserAuthorityGroup
    // -------------------------------------------------------------------------

    public int addUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        return userStore.addUserAuthorityGroup( userAuthorityGroup );
    }

    public void updateUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        userStore.updateUserAuthorityGroup( userAuthorityGroup );
    }

    public void deleteUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        userStore.deleteUserAuthorityGroup( userAuthorityGroup );
    }

    public Collection<UserAuthorityGroup> getAllUserAuthorityGroups()
    {
        return userStore.getAllUserAuthorityGroups();
    }

    public UserAuthorityGroup getUserAuthorityGroup( int userAuthorityGroupId )
    {
        return userStore.getUserAuthorityGroup( userAuthorityGroupId );
    }

    public UserAuthorityGroup getUserAuthorityGroupByName( String userAuthorityGroupName )
    {
        return userStore.getUserAuthorityGroupByName( userAuthorityGroupName );
    }

    public Collection<UserAuthorityGroup> getUserRolesBetween( int first, int max )
    {
        return userRoleStore.getBetween( first, max );
    }

    public Collection<UserAuthorityGroup> getUserRolesBetweenByName( String name, int first, int max )
    {
        return userRoleStore.getBetweenByName( name, first, max );
    }

    public int getUserRoleCount()
    {
        return userRoleStore.getCount();
    }

    public int getUserRoleCountByName( String name )
    {
        return userRoleStore.getCountByName( name );
    }

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

    // -------------------------------------------------------------------------
    // UserCredentials
    // -------------------------------------------------------------------------

    public User addUserCredentials( UserCredentials userCredentials )
    {
        return userStore.addUserCredentials( userCredentials );
    }

    public void updateUserCredentials( UserCredentials userCredentials )
    {
        userStore.updateUserCredentials( userCredentials );
    }

    public void deleteUserCredentials( UserCredentials userCredentials )
    {
        userStore.deleteUserCredentials( userCredentials );
    }

    public Collection<UserCredentials> getAllUserCredentials()
    {
        return userStore.getAllUserCredentials();
    }

    public UserCredentials getUserCredentials( User user )
    {
        return userStore.getUserCredentials( user );
    }

    public UserCredentials getUserCredentialsByUsername( String username )
    {
        return userStore.getUserCredentialsByUsername( username );
    }

    public Collection<UserCredentials> getUsersBetween( int first, int max )
    {
        return userStore.getUsersBetween( first, max );
    }

    public Collection<UserCredentials> getUsersBetweenByName( String username, int first, int max )
    {
        return userStore.getUsersBetweenByName( username, first, max );
    }

    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetween( int first, int max )
    {
        return userStore.getUsersWithoutOrganisationUnitBetween( first, max );
    }

    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetweenByName( String username, int first, int max )
    {
        return userStore.getUsersWithoutOrganisationUnitBetweenByName( username, first, max );
    }

    public Collection<UserCredentials> searchUsersByName( String username )
    {
        return userStore.searchUsersByName( username );
    }

    public void setLastLogin( String username )
    {
        UserCredentials credentials = getUserCredentialsByUsername( username );
        credentials.setLastLogin( new Date() );
        updateUserCredentials( credentials );
    }

    public Collection<UserCredentials> getInactiveUsers( int months )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.MONTH, (months * -1) );

        return userStore.getInactiveUsers( cal.getTime() );
    }

    public Collection<UserCredentials> getInactiveUsers( int months, int first, int max )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.MONTH, (months * -1) );

        return userStore.getInactiveUsers( cal.getTime(), first, max );
    }

    public int getInactiveUsersCount( int months )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.MONTH, (months * -1) );

        return userStore.getInactiveUsersCount( cal.getTime() );
    }

    public int getActiveUsersCount( int days )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        cal.add( Calendar.DAY_OF_YEAR, (days * -1) );

        return userStore.getActiveUsersCount( cal.getTime() );
    }

    // -------------------------------------------------------------------------
    // UserSettings
    // -------------------------------------------------------------------------

    public void addUserSetting( UserSetting userSetting )
    {
        userStore.addUserSetting( userSetting );
    }

    public void updateUserSetting( UserSetting userSetting )
    {
        userStore.updateUserSetting( userSetting );
    }

    public void deleteUserSetting( UserSetting userSetting )
    {
        userStore.deleteUserSetting( userSetting );
    }

    public Collection<UserSetting> getAllUserSettings( User user )
    {
        return userStore.getAllUserSettings( user );
    }

    public UserSetting getUserSetting( User user, String name )
    {
        return userStore.getUserSetting( user, name );
    }

    public Map<User, Serializable> getUserSettings( String name, Serializable defaultValue )
    {
        Map<User, Serializable> map = new HashMap<User, Serializable>();

        for ( UserSetting setting : userStore.getUserSettings( name ) )
        {
            map.put( setting.getUser(), setting.getValue() != null ? setting.getValue() : defaultValue );
        }

        return map;
    }
}
