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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Nguyen Hong Duc
 */
public interface UserStore
    extends GenericIdentifiableObjectStore<User>
{
    String ID = UserStore.class.getName();

    /**
     * Returns a Collection of Users which are having given Phone number.
     * 
     * @param phoneNumber
     * @return a Collection of Users.
     */
    Collection<User> getUsersByPhoneNumber( String phoneNumber );

    /**
     * Returns all users with the given name. Matches on the first name and
     * surname properties of the user.
     * 
     * @param name the name.
     * @return a Collection of Users.
     */
    Collection<User> getUsersByName( String name );

    /**
     * Returns all users which are managed by the given user through its managed
     * groups association.
     * 
     * @param searchKey the string to search by first name, surname and user name, 
     *        no search if null.
     * @param user the user.
     * @param constrainManagedGroups constrain the result to users within managed groups.
     * @param constrainAuthSubset constrain the result to users with a subset of
     *        authorities.
     * @param inactiveSince date for last login.
     * @param selfRegistered constrain the result to self-registered users.
     * @param organisationUnit constrain the result to users associated with the
     *        organisation unit.
     * @param first the first record to return, null if 0.
     * @param max the max number of records to return, null if none.
     * @return a List of users.
     */
    List<User> getManagedUsersBetween( String searchKey, User user, 
        boolean constrainManagedGroups, boolean constrainAuthSubset, 
        Date inactiveSince, boolean selfRegistered, OrganisationUnit organisationUnit, Integer first, Integer max );
}
