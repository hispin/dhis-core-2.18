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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.system.util.AuditLogLevel;
import org.hisp.dhis.system.util.AuditLogUtil;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserSetting;
import org.hisp.dhis.user.UserStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Nguyen Hong Duc
 * @version $Id: HibernateUserStore.java 6530 2008-11-28 15:02:47Z eivindwa $
 */
@Transactional
public class HibernateUserStore
    implements UserStore
{
    private Logger logger = Logger.getLogger( getClass() );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;
    
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private GenericIdentifiableObjectStore<UserAuthorityGroup> userRoleStore;
    
    public GenericIdentifiableObjectStore<UserAuthorityGroup> getUserRoleStore()
    {
        return userRoleStore;
    }
    
    public void setUserRoleStore( GenericIdentifiableObjectStore<UserAuthorityGroup> userRoleStore )
    {
        this.userRoleStore = userRoleStore;
    }

    // -------------------------------------------------------------------------
    // User
    // -------------------------------------------------------------------------

    public int addUser( User user )
    {
        Session session = sessionFactory.getCurrentSession();

        logger.log( AuditLogLevel.AUDIT_TRAIL, 
            AuditLogUtil.logMessage( currentUserService.getCurrentUsername(),
            AuditLogUtil.ACTION_ADD , 
            User.class.getSimpleName(), 
            user.getName()) );
        
        return (Integer) session.save( user );
    }

    public void updateUser( User user )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( user );
        
        logger.log( AuditLogLevel.AUDIT_TRAIL, 
            AuditLogUtil.logMessage( currentUserService.getCurrentUsername(),
                AuditLogUtil.ACTION_EDIT , 
                User.class.getSimpleName(), 
                user.getName()) );
    }

    public User getUser( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (User) session.get( User.class, id );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<User> getAllUsers()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from User" ).list();
    }

    public Collection<User> getUsersByOrganisationUnit( OrganisationUnit organisationUnit )
    {   
        Collection<User> users = getAllUsers();
        
        Iterator<User> iterator = users.iterator();
        
        while( iterator.hasNext() )
        {
            if( ! iterator.next().getOrganisationUnits().contains( organisationUnit ) )
            {
        	iterator.remove();
            }
        }
        
        return users;
    }

    public Collection<User> getUsersWithoutOrganisationUnit()
    {    	
    	Collection<User> users = getAllUsers();
        
        Iterator<User> iterator = users.iterator();
        
        while( iterator.hasNext() )
        {
            if( iterator.next().getOrganisationUnits().size() > 0 )
            {
        	iterator.remove();
            }
        }
        
        return users;
    }

    @SuppressWarnings("unchecked")
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
        
        logger.log( AuditLogLevel.AUDIT_TRAIL, 
            AuditLogUtil.logMessage( currentUserService.getCurrentUsername(),
            AuditLogUtil.ACTION_DELETE , 
            User.class.getSimpleName(), 
            user.getName()) );
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

    public void deleteUserSetting( UserSetting userSetting )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( userSetting );
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
    
    public int getUserCount()
    {
        Session session = sessionFactory.getCurrentSession();
        
        Query query = session.createQuery( "select count(*) from User" );
        
        Number rs = (Number) query.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    public int getUserCountByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UserCredentials.class );
      
        criteria.add( Restrictions.ilike( "username", "%" + name + "%" ) );
        
        criteria.setProjection( Projections.rowCount() ).uniqueResult();

        Number rs = (Number) criteria.uniqueResult();
        
        return rs != null ? rs.intValue() : 0;
    }

    @SuppressWarnings("unchecked")
    public Collection<UserCredentials> getUsersBetween( int first, int max )
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from UserCredentials" ).setFirstResult( first ).setMaxResults( max ).list();
    }

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

    public int getUserRoleCount()
    {
        return userRoleStore.getCount();
    }

    public int getUserRoleCountByName( String name )
    {
        return userRoleStore.getCountByName( name );
    }

    public Collection<UserAuthorityGroup> getUserRolesBetween( int first, int max )
    {
        return userRoleStore.getBetween( first, max );
    }

    public Collection<UserAuthorityGroup> getUserRolesBetweenByName( String name, int first, int max )
    {
        return userRoleStore.getBetweenByName( name, first, max );
    }

    public Collection<UserCredentials> getUsersByOrganisationUnitBetween( OrganisationUnit orgUnit, int first, int max )
    {
        return getBlockUser( toUserCredentials( getUsersByOrganisationUnit( orgUnit ) ), first, max );
    }

    public Collection<UserCredentials> getUsersByOrganisationUnitBetweenByName( OrganisationUnit orgUnit, String name,
        int first, int max )
    {
        return getBlockUser( findByName( toUserCredentials( getUsersByOrganisationUnit( orgUnit ) ), name ), first, max );
    }

    public int getUsersByOrganisationUnitCount( OrganisationUnit orgUnit )
    {
        return getUsersByOrganisationUnit( orgUnit ).size();
    }

    public int getUsersByOrganisationUnitCountByName( OrganisationUnit orgUnit, String name )
    {
        return findByName( toUserCredentials( getUsersByOrganisationUnit( orgUnit ) ), name ).size();
    }

    public Collection<UserCredentials> getUsersWithoutOrganisationUnitBetween( int first, int max )
    {
        return getBlockUser( toUserCredentials( getUsersWithoutOrganisationUnit()), first, max );
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

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private Collection<UserCredentials> findByName( Collection<UserCredentials> users, String key )
    {
        List<UserCredentials> returnList = new ArrayList<UserCredentials>();

        for ( UserCredentials user : users )
        {
            if ( user.getUsername().toLowerCase().contains( key.toLowerCase() ) )
            {
                returnList.add( user );
            }
        }

        return returnList;
    }

    private List<UserCredentials> getBlockUser( Collection<UserCredentials> usersList, int startPos, int pageSize )
    {
        List<UserCredentials> returnList;
        List<UserCredentials> elementList = new ArrayList<UserCredentials>( usersList );
        
        try
        {
            returnList = elementList.subList( startPos, startPos + pageSize );
        }
        catch ( IndexOutOfBoundsException ex )
        {
            returnList = elementList.subList( startPos, elementList.size() );
        }
        
        return returnList;
    }
    
    private List<UserCredentials> toUserCredentials( Collection<User> users )
    {
        List<UserCredentials> returnUserCredentials = new ArrayList<UserCredentials>();

        for ( User user : users )
        {
            returnUserCredentials.add( getUserCredentials( user ) );
        }
        return returnUserCredentials;
    }
}
