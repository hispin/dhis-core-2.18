package org.hisp.dhis.user.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupStore;

public class HiberntateUserGroupStore implements UserGroupStore
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
    // UserGroup
    // -------------------------------------------------------------------------
    
    @Override
    public int addUserGroup( UserGroup userGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( userGroup );
    }

    @Override
    public void deleteUserGroup( UserGroup userGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( userGroup );        
    }

    @Override
    public void updateUserGroup( UserGroup userGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( userGroup );
    }

    
    @Override
    public Collection<UserGroup> getAllUserGroups()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UserGroup.class );
        
        return criteria.list();

    }

    @Override
    public UserGroup getUserGroup( int userGroupId )
    {
        Session session = sessionFactory.getCurrentSession();

        return (UserGroup) session.get( UserGroup.class, userGroupId );
    }

    @Override
    public UserGroup getUserGroupByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (UserGroup) criteria.uniqueResult();
    }


}
