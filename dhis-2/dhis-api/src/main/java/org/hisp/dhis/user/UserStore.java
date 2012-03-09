package org.hisp.dhis.user;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Nguyen Hong Duc
 * @version $Id: UserStore.java 5724 2008-09-18 14:37:01Z larshelg $
 */
public interface UserStore
{
    String ID = UserStore.class.getName();

    // -------------------------------------------------------------------------
    // User
    // -------------------------------------------------------------------------

    /**
     * Adds a User.
     * 
     * @param user the User to add.
     * @return the generated identifier.
     */
    int addUser( User user );

    /**
     * Updates a User.
     * 
     * @param user the User to update.
     */
    void updateUser( User user );

    /**
     * Retrieves the User with the given identifier.
     *
     * @param id the identifier of the User to retrieve.
     * @return the User.
     */
    User getUser( int id );

    /**
     * Retrieves the User with the given unique identifier.
     *
     * @param id the identifier of the User to retrieve.
     * @return the User.
     */
    User getUser( String uid );

    /**
     * Returns a Collection of all Users.
     * 
     * @return a Collection of Users.
     */
    Collection<User> getAllUsers();

    /**
     * Returns a Collection of the Users which are not associated with any
     * OrganisationUnits.
     * 
     * @return a Collection of Users.
     */
    Collection<User> getUsersWithoutOrganisationUnit();

    /**
     * Returns a Collection of the Users which are associated with OrganisationUnits.
     * 
     * @param orgunits a Collection of the organization units.
     * 
     * @return a Collection of Users.
     */
    Collection<User> getUsersByOrganisationUnits( Collection<OrganisationUnit> orgunits );

    /**
     * Returns a Collection of Users which are having given Phone number.
     * 
     * @param phoneNumber
     * @return a Collection of Users.
     */
    Collection<User> getUsersByPhoneNumber( String phoneNumber );

    /**
     * Deletes a User.
     * 
     * @param user the User to delete.
     */
    void deleteUser( User user );

    int getUserCount();

    int getUserCountByName( String name );

    int getUsersWithoutOrganisationUnitCount();

    int getUsersWithoutOrganisationUnitCountByName( String name );

    int getUsersByOrganisationUnitCount( OrganisationUnit orgUnit );

    int getUsersByOrganisationUnitCountByName( OrganisationUnit orgUnit, String name );

    // -------------------------------------------------------------------------
    // UserCredentials
    // -------------------------------------------------------------------------

    /**
     * Adds a UserCredentials.
     * 
     * @param userCredentials the UserCredentials to add.
     * @return the User which the UserCredentials is associated with.
     */
    User addUserCredentials( UserCredentials userCredentials );

    /**
     * Updates a UserCredentials.
     * 
     * @param userCredentials the UserCredentials to update.
     */
    void updateUserCredentials( UserCredentials userCredentials );

    /**
     * Retrieves the UserCredentials of the given User.
     * 
     * @param user the User.
     * @return the UserCredentials.
     */
    UserCredentials getUserCredentials( User user );

    /**
     * Retrieves the UserCredentials associated with the User with the given
     * name.
     * 
     * @param username the name of the User.
     * @return the UserCredentials.
     */
    UserCredentials getUserCredentialsByUsername( String username );

    /**
     * Retrieves all UserCredentials.
     * 
     * @return a Collection of UserCredentials.
     */
    Collection<UserCredentials> getAllUserCredentials();

    /**
     * Deletes a UserCredentials.
     * 
     * @param userCredentials the UserCredentials.
     */
    void deleteUserCredentials( UserCredentials userCredentials );

    Collection<UserCredentials> searchUsersByName( String key );

    Collection<UserCredentials> getUsersBetween( int first, int max );

    Collection<UserCredentials> getUsersBetweenByName( String name, int first, int max );

    Collection<UserCredentials> getUsersWithoutOrganisationUnitBetween( int first, int max );

    Collection<UserCredentials> getUsersWithoutOrganisationUnitBetweenByName( String name, int first, int max );

    Collection<UserCredentials> getUsersByOrganisationUnitBetween( OrganisationUnit orgUnit, int first, int max );

    Collection<UserCredentials> getUsersByOrganisationUnitBetweenByName( OrganisationUnit orgUnit, String name,
        int first, int max );

    Collection<UserCredentials> getInactiveUsers( Date date );
    
    Collection<UserCredentials> getInactiveUsers( Date date, int first, int max );

    int getInactiveUsersCount( Date date );

    int getActiveUsersCount( Date date );

    // -------------------------------------------------------------------------
    // UserAuthorityGroup
    // -------------------------------------------------------------------------

    /**
     * Adds a UserAuthorityGroup.
     * 
     * @param userAuthorityGroup the UserAuthorityGroup.
     * @return the generated identifier.
     */
    int addUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup );

    /**
     * Updates a UserAuthorityGroup.
     * 
     * @param userAuthorityGroup the UserAuthorityGroup.
     */
    void updateUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup );

    /**
     * Retrieves the UserAuthorityGroup with the given identifier.
     * 
     * @param id the identifier of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroup( int id );

    /**
     * Retrieves the UserAuthorityGroup with the given name.
     * 
     * @param name the name of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroupByName( String name );

    /**
     * Deletes a UserAuthorityGroup.
     * 
     * @param userAuthorityGroup the UserAuthorityGroup to delete.
     */
    void deleteUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup );

    /**
     * Retrieves all UserAuthorityGroups.
     * 
     * @return a Collectio of UserAuthorityGroups.
     */
    Collection<UserAuthorityGroup> getAllUserAuthorityGroups();

    // -------------------------------------------------------------------------
    // UserSettings
    // -------------------------------------------------------------------------

    /**
     * Adds a UserSetting.
     * 
     * @param userSetting the UserSetting to add.
     */
    void addUserSetting( UserSetting userSetting );

    /**
     * Updates a UserSetting.
     * 
     * @param userSetting the UserSetting to update.
     */
    void updateUserSetting( UserSetting userSetting );

    /**
     * Retrieves the UserSetting associated with the given User for the given
     * UserSetting name.
     * 
     * @param user the User.
     * @param name the name of the UserSetting.
     * @return the UserSetting.
     */
    UserSetting getUserSetting( User user, String name );

    /**
     * Retrieves all UserSettings for the given User.
     * 
     * @param user the User.
     * @return a Collection of UserSettings.
     */
    Collection<UserSetting> getAllUserSettings( User user );

    /**
     * Deletes a UserSetting.
     * 
     * @param userSetting the UserSetting to delete.
     */
    void deleteUserSetting( UserSetting userSetting );
    
    /**
     * Returns all UserSettings with the given name.
     * 
     * @param name the name.
     * @return a Collection of UserSettings.
     */
    Collection<UserSetting> getUserSettings( String name );
}
