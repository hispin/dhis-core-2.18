package org.hisp.dhis.user.hibernate;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserCredentialsStore;
import org.hisp.dhis.user.UserService;

/**
 * TODO extend BaseIdentifiableObjectStore
 * 
 * @author Lars Helge Overland
 */
public class HibernateUserCredentialsStore
    implements UserCredentialsStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // UserCredentials
    // -------------------------------------------------------------------------

    @Override
    public int addUserCredentials( UserCredentials userCredentials )
    {
        return (Integer) sessionFactory.getCurrentSession().save( userCredentials );
    }

    @Override
    public void updateUserCredentials( UserCredentials userCredentials )
    {
        userCredentials.setPasswordLastUpdated( new Date() ); //TODO only update when password changed
        
        sessionFactory.getCurrentSession().update( userCredentials );
    }

    @Override
    public UserCredentials getUserCredentials( User user )
    {
        if ( user == null )
        {
            return null;
        }

        Session session = sessionFactory.getCurrentSession();

        return (UserCredentials) session.get( UserCredentials.class, user.getId() );
    }
    
    @Override
    public UserCredentials getUserCredentials( int id )
    {
        return (UserCredentials) sessionFactory.getCurrentSession().get( UserCredentials.class, id );
    }

    @Override
    public UserCredentials getUserCredentialsByUsername( String username )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from UserCredentials uc where uc.username = :username" );

        query.setString( "username", username );
        query.setCacheable( true );

        return (UserCredentials) query.uniqueResult();
    }

    @Override
    public UserCredentials getUserCredentialsByOpenID( String openId )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from UserCredentials uc where uc.openId = :openId" );

        query.setString( "openId", openId );
        query.setCacheable( true );

        return (UserCredentials) query.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<UserCredentials> getAllUserCredentials()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( UserCredentials.class ).list();
    }

    @Override
    public void deleteUserCredentials( UserCredentials userCredentials )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( userCredentials );
    }

    @Override
    public int getUserCount()
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "select count(*) from UserCredentials" );

        Number rs = (Number) query.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<UserCredentials> searchUsersByName( String key )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UserCredentials.class );
        criteria.createAlias( "user", "user" );

        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add( Restrictions.ilike( "user.surname", "%" + key + "%" ) );
        disjunction.add( Restrictions.ilike( "user.firstName", "%" + key + "%" ) );
        disjunction.add( Restrictions.ilike( "username", "%" + key + "%" ) );

        criteria.add( disjunction );
        criteria.addOrder( Order.asc( "username" ) );

        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<UserCredentials> searchUsersByName( String key, int first, int max )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UserCredentials.class );
        criteria.createAlias( "user", "user" );

        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add( Restrictions.ilike( "user.surname", "%" + key + "%" ) );
        disjunction.add( Restrictions.ilike( "user.firstName", "%" + key + "%" ) );
        disjunction.add( Restrictions.ilike( "username", "%" + key + "%" ) );

        criteria.add( disjunction );
        criteria.addOrder( Order.asc( "username" ) );

        criteria.setFirstResult( first );
        criteria.setMaxResults( max );

        return criteria.list();
    }

    @Override
    public int getUserCountByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UserCredentials.class );

        criteria.add( Restrictions.ilike( "username", "%" + name + "%" ) );

        criteria.setProjection( Projections.rowCount() );

        Number rs = (Number) criteria.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<UserCredentials> getUsersBetween( int first, int max )
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from UserCredentials order by username" ).setFirstResult( first )
            .setMaxResults( max ).list();
    }

    @Override
    @SuppressWarnings("unchecked")
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

    @Override
    public Collection<UserCredentials> getUsersByOrganisationUnitBetween( OrganisationUnit orgUnit, int first, int max )
    {
        return getBlockUser( toUserCredentials( orgUnit.getUsers() ), first, max );
    }

    @Override
    public Collection<UserCredentials> getUsersByOrganisationUnitBetweenByName( OrganisationUnit orgUnit, String name,
        int first, int max )
    {
        return getBlockUser( findByName( toUserCredentials( orgUnit.getUsers() ), name ), first, max );
    }

    @Override
    public int getUsersByOrganisationUnitCount( OrganisationUnit orgUnit )
    {
        return orgUnit.getUsers().size();
    }

    @Override
    public int getUsersByOrganisationUnitCountByName( OrganisationUnit orgUnit, String name )
    {
        return findByName( toUserCredentials( orgUnit.getUsers() ), name ).size();
    }

    @Override
    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetween( int first, int max )
    {
        return getBlockUser( toUserCredentials( userService.getUsersWithoutOrganisationUnit() ), first, max );
    }

    @Override
    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetweenByName( String name, int first, int max )
    {
        return getBlockUser( findByName( toUserCredentials( userService.getUsersWithoutOrganisationUnit() ), name ), first, max );
    }

    @Override
    public int getUsersWithoutOrganisationUnitCount()
    {
        return userService.getUsersWithoutOrganisationUnit().size();
    }

    @Override
    public int getUsersWithoutOrganisationUnitCountByName( String name )
    {
        return findByName( toUserCredentials( userService.getUsersWithoutOrganisationUnit() ), name ).size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<UserCredentials> getSelfRegisteredUserCredentials( int first, int max )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( UserCredentials.class );
        criteria.add( Restrictions.eq( "selfRegistered", true ) );
        criteria.addOrder( Order.desc( "created" ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );

        return criteria.list();
    }

    @Override
    public int getSelfRegisteredUserCredentialsCount()
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( UserCredentials.class );
        criteria.add( Restrictions.eq( "selfRegistered", true ) );
        criteria.setProjection( Projections.rowCount() );

        Number rs = (Number) criteria.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<UserCredentials> getInactiveUsers( Date date )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( UserCredentials.class );
        criteria.add( Restrictions.lt( "lastLogin", date ) );

        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<UserCredentials> getInactiveUsers( Date date, int first, int max )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( UserCredentials.class );
        criteria.add( Restrictions.lt( "lastLogin", date ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );

        return criteria.list();
    }

    @Override
    public int getInactiveUsersCount( Date date )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( UserCredentials.class );
        criteria.add( Restrictions.lt( "lastLogin", date ) );
        criteria.setProjection( Projections.rowCount() );

        Number rs = (Number) criteria.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    public int getActiveUsersCount( Date date )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( UserCredentials.class );
        criteria.add( Restrictions.ge( "lastLogin", date ) );
        criteria.setProjection( Projections.rowCount() );

        Number rs = (Number) criteria.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getUsernames( String key, Integer max )
    {
        Session session = sessionFactory.getCurrentSession();

        String hql = "select username from UserCredentials ";

        if ( key != null )
        {
            hql += "where lower(username) like lower('%" + key + "%') ";
        }

        Query query = session.createQuery( hql );

        if ( max != null )
        {
            query.setMaxResults( max );
        }

        return query.list();
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<UserCredentials> findByName( Collection<UserCredentials> users, String key )
    {
        List<UserCredentials> returnList = new ArrayList<>();

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
        List<UserCredentials> elementList = new ArrayList<>( usersList );

        int toIndex = Math.min( startPos + pageSize, elementList.size() );

        return elementList.subList( startPos, toIndex );
    }

    private List<UserCredentials> toUserCredentials( Collection<User> users )
    {
        List<UserCredentials> credentials = new ArrayList<>();

        for ( User user : users )
        {
            credentials.add( user.getUserCredentials() );
        }

        return credentials;
    }
}
