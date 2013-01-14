package org.hisp.dhis.hibernate;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.AccessUtils;
import org.hisp.dhis.common.AuditLogUtil;
import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class HibernateGenericStore<T>
    implements GenericNameableObjectStore<T>
{
    private static final Log log = LogFactory.getLog( HibernateGenericStore.class );

    protected SessionFactory sessionFactory;

    @Required
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    protected JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private CurrentUserService currentUserService;

    private Class<T> clazz;

    /**
     * Could be overridden programmatically.
     */
    public Class<T> getClazz()
    {
        return clazz;
    }

    /**
     * Could be injected through container.
     */
    @Required
    public void setClazz( Class<T> clazz )
    {
        this.clazz = clazz;
    }

    private boolean cacheable = false;

    /**
     * Could be overridden programmatically.
     */
    protected boolean isCacheable()
    {
        return cacheable;
    }

    /**
     * Could be injected through container.
     */
    public void setCacheable( boolean cacheable )
    {
        this.cacheable = cacheable;
    }

    // -------------------------------------------------------------------------
    // Convenience methods
    // -------------------------------------------------------------------------

    /**
     * Creates a Query.
     *
     * @param hql the hql query.
     * @return a Query instance.
     */
    protected final Query getQuery( String hql )
    {
        return sessionFactory.getCurrentSession().createQuery( hql ).setCacheable( cacheable );
    }

    /**
     * Creates a SqlQuery.
     *
     * @param sql the sql query.
     * @return a SqlQuery instance.
     */
    protected final SQLQuery getSqlQuery( String sql )
    {
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery( sql );
        query.setCacheable( cacheable );
        return query;
    }

    private boolean hasShareProperties()
    {
        try
        {
            // for now we need to have this test, since not all idObjectClasses are converted
            sessionFactory.getClassMetadata( clazz ).getPropertyType( "publicAccess" );
        }
        catch ( HibernateException ignored )
        {
            return false;
        }

        return true;
    }

    /**
     * Creates a Criteria for the implementation Class type.
     *
     * @return a Criteria instance.
     */
    protected final Criteria getCriteria()
    {
        return getClazzCriteria().setCacheable( cacheable );
    }

    protected Criteria getClazzCriteria()
    {
        return sessionFactory.getCurrentSession().createCriteria( getClazz() );
    }

    /**
     * Creates a Criteria for the implementation Class type restricted by the
     * given Criterions.
     *
     * @param expressions the Criterions for the Criteria.
     * @return a Criteria instance.
     */
    protected final Criteria getCriteria( Criterion... expressions )
    {
        Criteria criteria = getCriteria();

        for ( Criterion expression : expressions )
        {
            criteria.add( expression );
        }

        return criteria;
    }

    /**
     * Retrieves an object based on the given Criterions.
     *
     * @param expressions the Criterions for the Criteria.
     * @return an object of the implementation Class type.
     */
    @SuppressWarnings( "unchecked" )
    protected final T getObject( Criterion... expressions )
    {
        return (T) getCriteria( expressions ).uniqueResult();
    }

    /**
     * Retrieves a List based on the given Criterions.
     *
     * @param expressions the Criterions for the Criteria.
     * @return a List with objects of the implementation Class type.
     */
    @SuppressWarnings( "unchecked" )
    protected final List<T> getList( Criterion... expressions )
    {
        return getCriteria( expressions ).list();
    }

    // -------------------------------------------------------------------------
    // GenericIdentifiableObjectStore implementation
    // -------------------------------------------------------------------------

    @Override
    public int save( T object )
    {
        if ( !isWriteAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_CREATE_DENIED );
            throw new AccessDeniedException( "You do not have write access to object" );
        }

        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_CREATE );
        return (Integer) sessionFactory.getCurrentSession().save( object );
    }

    @Override
    public void update( T object )
    {
        if ( !isUpdateAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_UPDATE_DENIED );
            throw new AccessDeniedException( "You do not have update access to object" );
        }

        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_UPDATE );
        sessionFactory.getCurrentSession().update( object );
    }

    @Override
    public void saveOrUpdate( T object )
    {
        if ( !isWriteAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_UPDATE_DENIED );
            throw new AccessDeniedException( "You do not have write access to object" );
        }

        // TODO check if object is persisted or not to decide logging? defaulting to edit for now
        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_UPDATE );
        sessionFactory.getCurrentSession().saveOrUpdate( object );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final T get( int id )
    {
        T object = (T) sessionFactory.getCurrentSession().get( getClazz(), id );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new AccessDeniedException( "You do not have read access to object with id " + id );
        }

        // AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ );
        return object;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final T load( int id )
    {
        T object = (T) sessionFactory.getCurrentSession().load( getClazz(), id );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new AccessDeniedException( "You do not have read access to object with id " + id );
        }

        // AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ );
        return object;
    }

    @Override
    public final T getByUid( String uid )
    {
        T object = getObject( Restrictions.eq( "uid", uid ) );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new AccessDeniedException( "You do not have read access to object with uid " + uid );
        }

        // AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ );
        return object;
    }

    @Override
    public final T getByName( String name )
    {
        T object = getObject( Restrictions.eq( "name", name ) );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new AccessDeniedException( "You do not have read access to object with name " + name );
        }

        // AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ );
        return object;
    }

    @Override
    public final T getByShortName( String shortName )
    {
        T object = getObject( Restrictions.eq( "shortName", shortName ) );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new AccessDeniedException( "You do not have read access to object with shortName " + shortName );
        }

        // AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ );
        return object;
    }

    @Override
    public final T getByCode( String code )
    {
        T object = getObject( Restrictions.eq( "code", code ) );

        if ( !isReadAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ_DENIED );
            throw new AccessDeniedException( "You do not have read access to object with code " + code );
        }

        // AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_READ );
        return object;
    }

    @Override
    public final void delete( T object )
    {
        if ( !isDeleteAllowed( object ) )
        {
            AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_DELETE_DENIED );
            throw new AccessDeniedException( "You do not have delete access to this object." );
        }

        AuditLogUtil.infoWrapper( log, currentUserService.getCurrentUsername(), object, AuditLogUtil.ACTION_DELETE );
        sessionFactory.getCurrentSession().delete( object );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<T> getLikeName( String name )
    {
        String hql = "from " + clazz.getName() + " c where lower(name) like :name";

        if ( disableSharing() )
        {
            hql = "from " + clazz.getName() + " c where lower(name) like :name";
        }

        Query query = getQuery( hql );
        query.setString( "name", "%" + name.toLowerCase() + "%" );

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final Collection<T> getAll()
    {
        String hql = "from " + clazz.getName() + " c";

        if ( disableSharing() )
        {
            hql = "from " + clazz.getName() + " c";
        }

        return getQuery( hql ).list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public final Collection<T> getAllSorted()
    {
        String hql = "from " + clazz.getName() + " c order by name";

        if ( disableSharing() )
        {
            hql = "from " + clazz.getName() + " c order by name";
        }

        return getQuery( hql ).list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<T> getBetween( int first, int max )
    {
        String hql = "from " + clazz.getName() + " c order by name";

        if ( disableSharing() )
        {
            hql = "from " + clazz.getName() + " c order by name";
        }

        Query query = getQuery( hql );
        query.setFirstResult( first );
        query.setMaxResults( max );

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<T> getBetweenOrderedByLastUpdated( int first, int max )
    {
        String hql = "from " + clazz.getName() + " c order by lastUpdated desc";

        if ( disableSharing() )
        {
            hql = "from " + clazz.getName() + " c order by lastUpdated desc";
        }

        Query query = getQuery( hql );
        query.setFirstResult( first );
        query.setMaxResults( max );

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<T> getBetweenByName( String name, int first, int max )
    {
        String hql = "from " + clazz.getName() + " c where lower(name) like :name order by name";

        if ( disableSharing() )
        {
            hql = "from " + clazz.getName() + " c where lower(name) like :name order by name";
        }

        Query query = getQuery( hql );
        query.setString( "name", "%" + name.toLowerCase() + "%" );
        query.setFirstResult( first );
        query.setMaxResults( max );

        return query.list();
    }

    @Override
    public int getCount()
    {
        String hql = "select count(c) from " + clazz.getName() + " c";

        if ( disableSharing() )
        {
            hql = "select count(c) from " + clazz.getName() + " c";
        }

        return ((Long) getQuery( hql ).uniqueResult()).intValue();

        // Criteria criteria = getCriteria();
        // criteria.setProjection( Projections.rowCount() );
        //return ((Number) criteria.uniqueResult()).intValue();
    }

    @Override
    public int getCountByName( String name )
    {
        String hql = "select count(c) from " + clazz.getName() + " c where lower(name) like :name";

        if ( disableSharing() )
        {
            hql = "select count(c) from " + clazz.getName() + " c where lower(name) like :name";
        }

        Query query = getQuery( hql );
        query.setString( "name", "%" + name.toLowerCase() + "%" );

        return ((Long) query.uniqueResult()).intValue();

        //Criteria criteria = getCriteria();
        //criteria.setProjection( Projections.rowCount() );
        //criteria.add( Restrictions.ilike( "name", "%" + name + "%" ) );
        //return ((Number) criteria.uniqueResult()).intValue();
    }

    @Override
    public long getCountByLastUpdated( Date lastUpdated )
    {
        String hql = "select count(c) from " + clazz.getName() + " c where lastUpdated >= :lastUpdated";

        if ( disableSharing() )
        {
            hql = "select count(c) from " + clazz.getName() + " c where lastUpdated >= :lastUpdated";
        }

        Query query = getQuery( hql );
        query.setDate( "lastUpdated", lastUpdated );

        return ((Long) query.uniqueResult()).intValue();

        //Object count = getCriteria().add( Restrictions.ge( "lastUpdated", lastUpdated ) ).setProjection( Projections.rowCount() ).list().get( 0 );
        //return count != null ? (Long) count : -1;
    }

    @Override
    public List<T> getByUid( Collection<String> uids )
    {
        List<T> list = new ArrayList<T>();

        if ( uids != null )
        {
            for ( String uid : uids )
            {
                T object = getByUid( uid );

                if ( object != null )
                {
                    list.add( object );
                }
            }
        }

        return list;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<T> getByLastUpdated( Date lastUpdated )
    {
        String hql = "from " + clazz.getName() + " c where lastUpdated >= :lastUpdated";

        if ( disableSharing() )
        {
            hql = "from " + clazz.getName() + " c where lastUpdated >= :lastUpdated";
        }

        Query query = getQuery( hql );
        query.setDate( "lastUpdated", lastUpdated );

        return query.list();
        // return getCriteria().add( Restrictions.ge( "lastUpdated", lastUpdated ) ).list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<T> getByCreated( Date created )
    {
        String hql = "from " + clazz.getName() + " c where created >= :created";

        if ( disableSharing() )
        {
            hql = "from " + clazz.getName() + " c where created >= :created";
        }

        Query query = getQuery( hql );
        query.setDate( "created", created );

        return query.list();
        // return getCriteria().add( Restrictions.ge( "created", created ) ).list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<T> getByLastUpdatedSorted( Date lastUpdated )
    {
        String hql = "from " + clazz.getName() + " c where lastUpdated >= :lastUpdated order by name";

        if ( disableSharing() )
        {
            hql = "from " + clazz.getName() + " c where lastUpdated >= :lastUpdated order by name";
        }

        Query query = getQuery( hql );
        query.setDate( "lastUpdated", lastUpdated );

        return query.list();
        //return getCriteria().add( Restrictions.ge( "lastUpdated", lastUpdated ) ).addOrder( Order.asc( "name" ) ).list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<T> getByUser( User user )
    {
        String hql = "from " + clazz.getName() + " c where user = :user";

        if ( disableSharing() )
        {
            hql = "from " + clazz.getName() + " c where user = :user";
        }

        Query query = getQuery( hql );
        query.setEntity( "user", user );

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<T> getAccessibleByUser( User user )
    {
        //TODO link to interface

        Criteria criteria = getCriteria();
        criteria.add( Restrictions.or( Restrictions.eq( "user", user ), Restrictions.isNull( "user" ) ) );
        criteria.addOrder( Order.asc( "name" ) );
        return criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<T> getAccessibleByLastUpdated( User user, Date lastUpdated )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.or( Restrictions.eq( "user", user ), Restrictions.isNull( "user" ) ) );
        criteria.add( Restrictions.ge( "lastUpdated", lastUpdated ) );
        criteria.addOrder( Order.asc( "name" ) ).list();
        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public List<T> getAccessibleLikeName( User user, String name )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.ilike( "name", "%" + name + "%" ) );
        criteria.add( Restrictions.or( Restrictions.eq( "user", user ), Restrictions.isNull( "user" ) ) );
        criteria.addOrder( Order.asc( "name" ) );
        return criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<T> getAccessibleBetween( User user, int first, int max )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.or( Restrictions.eq( "user", user ), Restrictions.isNull( "user" ) ) );
        criteria.addOrder( Order.asc( "name" ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );
        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public List<T> getAccessibleBetweenLikeName( User user, String name, int first, int max )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.ilike( "name", "%" + name + "%" ) );
        criteria.add( Restrictions.or( Restrictions.eq( "user", user ), Restrictions.isNull( "user" ) ) );
        criteria.addOrder( Order.asc( "name" ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );
        return criteria.list();
    }

    //----------------------------------------------------------------------------------------------------------------
    // Helpers
    //----------------------------------------------------------------------------------------------------------------

    private boolean disableSharing()
    {
        return currentUserService.getCurrentUser() == null || currentUserService.getCurrentUser().getUserCredentials().getAllAuthorities().contains( "ALL" );
    }

    private boolean isReadAllowed( T object )
    {
        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject idObject = (IdentifiableObject) object;

            if ( hasShareProperties() )
            {
                return AccessUtils.canRead( currentUserService.getCurrentUser(), idObject );
            }
        }

        return true;
    }

    private boolean isWriteAllowed( T object )
    {
        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject idObject = (IdentifiableObject) object;

            if ( hasShareProperties() )
            {
                return AccessUtils.canWrite( currentUserService.getCurrentUser(), idObject );
            }
        }

        return true;
    }

    private boolean isUpdateAllowed( T object )
    {
        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject idObject = (IdentifiableObject) object;

            if ( hasShareProperties() )
            {
                return AccessUtils.canUpdate( currentUserService.getCurrentUser(), idObject );
            }
        }

        return true;
    }

    private boolean isDeleteAllowed( T object )
    {
        if ( IdentifiableObject.class.isInstance( object ) )
        {
            IdentifiableObject idObject = (IdentifiableObject) object;

            if ( hasShareProperties() )
            {
                return AccessUtils.canDelete( currentUserService.getCurrentUser(), idObject );
            }
        }

        return true;
    }
}
