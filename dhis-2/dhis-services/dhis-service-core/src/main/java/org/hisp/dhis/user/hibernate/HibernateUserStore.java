package org.hisp.dhis.user.hibernate;

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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.*;

import java.util.*;

/**
 * @author Nguyen Hong Duc
 * @version $Id: HibernateUserStore.java 6530 2008-11-28 15:02:47Z eivindwa $
 */
public class HibernateUserStore
    implements UserStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // User
    // -------------------------------------------------------------------------

    public int addUser( User user )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( user );
    }

    public void updateUser( User user )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( user );
    }

    public User getUser( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (User) session.get( User.class, id );
    }

    public User getUser( String uid )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( User.class );
        criteria.add( Restrictions.like( "uid", uid ) );

        return (User) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<User> getAllUsers()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from User" ).list();
    }

    public Collection<User> getUsersWithoutOrganisationUnit()
    {
        Collection<User> users = getAllUsers();

        Iterator<User> iterator = users.iterator();

        while ( iterator.hasNext() )
        {
            if ( iterator.next().getOrganisationUnits().size() > 0 )
            {
                iterator.remove();
            }
        }

        return users;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<User> getUsersByPhoneNumber( String phoneNumber )
    {
        String hql = "from User u where u.phoneNumber = :phoneNumber";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );
        query.setString( "phoneNumber", phoneNumber );

        return query.list();
    }

    public void deleteUser( User user )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( user );
    }

    // -------------------------------------------------------------------------
    // UserCredentials
    // -------------------------------------------------------------------------

    public User addUserCredentials( UserCredentials userCredentials )
    {
        Session session = sessionFactory.getCurrentSession();

        int id = (Integer) session.save( userCredentials );

        return getUser( id );
    }

    public void updateUserCredentials( UserCredentials userCredentials )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( userCredentials );
    }

    public UserCredentials getUserCredentials( User user )
    {
        Session session = sessionFactory.getCurrentSession();

        return (UserCredentials) session.get( UserCredentials.class, user.getId() );
    }

    public UserCredentials getUserCredentialsByUsername( String username )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from UserCredentials uc where uc.username = :username" );

        query.setString( "username", username );
        query.setCacheable( true );

        return (UserCredentials) query.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<UserCredentials> getAllUserCredentials()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( UserCredentials.class ).list();
    }

    public void deleteUserCredentials( UserCredentials userCredentials )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( userCredentials );
    }

    public int getUserCount()
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "select count(*) from UserCredentials" );

        Number rs = (Number) query.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<UserCredentials> searchUsersByName( String key )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UserCredentials.class );

        criteria.add( Restrictions.ilike( "username", "%" + key + "%" ) );
        criteria.addOrder( Order.asc( "username" ) );

        return criteria.list();
    }

    public int getUserCountByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UserCredentials.class );

        criteria.add( Restrictions.ilike( "username", "%" + name + "%" ) );

        criteria.setProjection( Projections.rowCount() );

        Number rs = (Number) criteria.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<UserCredentials> getUsersBetween( int first, int max )
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from UserCredentials order by username" ).setFirstResult( first ).setMaxResults(
            max ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<UserCredentials> getUsersBetweenByName( String name, int first, int max )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UserCredentials.class );

        criteria.add( Restrictions.ilike( "username", "%" + name + "%" ) );
        criteria.addOrder( Order.asc( "username" ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );

        return criteria.list();
    }

    public Collection<UserCredentials> getUsersByOrganisationUnitBetween( OrganisationUnit orgUnit, int first, int max )
    {
        return getBlockUser( toUserCredentials( orgUnit.getUsers() ), first, max );
    }

    public Collection<UserCredentials> getUsersByOrganisationUnitBetweenByName( OrganisationUnit orgUnit, String name,
                                                                                int first, int max )
    {
        return getBlockUser( findByName( toUserCredentials( orgUnit.getUsers() ), name ), first, max );
    }

    public int getUsersByOrganisationUnitCount( OrganisationUnit orgUnit )
    {
        return orgUnit.getUsers().size();
    }

    public int getUsersByOrganisationUnitCountByName( OrganisationUnit orgUnit, String name )
    {
        return findByName( toUserCredentials( orgUnit.getUsers() ), name ).size();
    }

    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetween( int first, int max )
    {
        return getBlockUser( toUserCredentials( getUsersWithoutOrganisationUnit() ), first, max );
    }

    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetweenByName( String name, int first, int max )
    {
        return getBlockUser( findByName( toUserCredentials( getUsersWithoutOrganisationUnit() ), name ), first, max );
    }

    public int getUsersWithoutOrganisationUnitCount()
    {
        return getUsersWithoutOrganisationUnit().size();
    }

    public int getUsersWithoutOrganisationUnitCountByName( String name )
    {
        return findByName( toUserCredentials( getUsersWithoutOrganisationUnit() ), name ).size();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<UserCredentials> getInactiveUsers( Date date )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( UserCredentials.class );
        criteria.add( Restrictions.lt( "lastLogin", date ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<UserCredentials> getInactiveUsers( Date date, int first, int max )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( UserCredentials.class );
        criteria.add( Restrictions.lt( "lastLogin", date ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );

        return criteria.list();
    }

    public int getInactiveUsersCount( Date date )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( UserCredentials.class );
        criteria.add( Restrictions.lt( "lastLogin", date ) );
        criteria.setProjection( Projections.rowCount() );

        Number rs = (Number) criteria.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    public int getActiveUsersCount( Date date )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( UserCredentials.class );
        criteria.add( Restrictions.ge( "lastLogin", date ) );
        criteria.setProjection( Projections.rowCount() );

        Number rs = (Number) criteria.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    // -------------------------------------------------------------------------
    // UserAuthorityGroup
    // -------------------------------------------------------------------------

    public int addUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( userAuthorityGroup );
    }

    public void updateUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( userAuthorityGroup );
    }

    public UserAuthorityGroup getUserAuthorityGroup( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (UserAuthorityGroup) session.get( UserAuthorityGroup.class, id );
    }

    public UserAuthorityGroup getUserAuthorityGroupByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UserAuthorityGroup.class );

        criteria.add( Restrictions.eq( "name", name ) );

        return (UserAuthorityGroup) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<UserAuthorityGroup> getAllUserAuthorityGroups()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from UserAuthorityGroup" ).list();
    }

    public void deleteUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( userAuthorityGroup );
    }

    // -------------------------------------------------------------------------
    // UserSettings
    // -------------------------------------------------------------------------

    public void addUserSetting( UserSetting userSetting )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( userSetting );
    }

    public void updateUserSetting( UserSetting userSetting )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( userSetting );
    }

    public UserSetting getUserSetting( User user, String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from UserSetting us where us.user = :user and us.name = :name" );

        query.setEntity( "user", user );
        query.setString( "name", name );
        query.setCacheable( true );

        return (UserSetting) query.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<UserSetting> getAllUserSettings( User user )
    {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery( "from UserSetting us where us.user = :user" );
        query.setEntity( "user", user );

        return query.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<UserSetting> getUserSettings( String name )
    {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery( "from UserSetting us where us.name = :name" );
        query.setString( "name", name );

        return query.list();
    }

    public void deleteUserSetting( UserSetting userSetting )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( userSetting );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<UserCredentials> findByName( Collection<UserCredentials> users, String key )
    {
        List<UserCredentials> returnList = new ArrayList<UserCredentials>();

        for ( UserCredentials user : users )
        {
            if ( user != null )
            {
                if ( user.getUsername().toLowerCase().contains( key.toLowerCase() ) )
                {
                    returnList.add( user );
                }
            }
        }

        return returnList;
    }

    private List<UserCredentials> getBlockUser( Collection<UserCredentials> usersList, int startPos, int pageSize )
    {
        List<UserCredentials> elementList = new ArrayList<UserCredentials>( usersList );

        int toIndex = Math.min( startPos + pageSize, elementList.size() );

        return elementList.subList( startPos, toIndex );
    }

    private List<UserCredentials> toUserCredentials( Collection<User> users )
    {
        List<UserCredentials> credentials = new ArrayList<UserCredentials>();

        for ( User user : users )
        {
            credentials.add( user.getUserCredentials() );
        }

        return credentials;
    }
}
