package org.hisp.dhis.organisationunit.hibernate;

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

import java.util.Collection;
import java.util.HashSet;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.system.objectmapper.OrganisationUnitRelationshipRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Kristian Nordal
 * @version $Id: HibernateOrganisationUnitStore.java 6251 2008-11-10 14:37:05Z larshelg $
 */
public class HibernateOrganisationUnitStore
    implements OrganisationUnitStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // -------------------------------------------------------------------------
    // OrganisationUnit
    // -------------------------------------------------------------------------

    public OrganisationUnit getOrganisationUnit( String uuid )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( OrganisationUnit.class );
        criteria.add( Restrictions.eq( "uuid", uuid ) );
        
        return (OrganisationUnit) criteria.uniqueResult();                
    }

    public OrganisationUnit getOrganisationUnitByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from OrganisationUnit o where o.name = :name" );

        query.setString( "name", name );

        return (OrganisationUnit) query.uniqueResult();
    }
    
    public OrganisationUnit getOrganisationUnitByNameIgnoreCase( String name )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( OrganisationUnit.class );        
        criteria.add( Restrictions.eq( "name", name ).ignoreCase() );
        return (OrganisationUnit) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> getRootOrganisationUnits()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from OrganisationUnit o where o.parent is null" ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> getOrganisationUnitsWithoutGroups()
    {
        String hql = "from OrganisationUnit o where o.groups.size = 0";
        
        return sessionFactory.getCurrentSession().createQuery( hql ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> getOrganisationUnitsByNameAndGroups( String name, Collection<OrganisationUnitGroup> groups )
    {
        name = StringUtils.trimToNull( name );
        groups = CollectionUtils.isEmpty( groups ) ? null : groups;
        
        if ( name == null && groups == null )
        {
            return new HashSet<OrganisationUnit>();
        }
        
        StringBuilder hql = new StringBuilder( "from OrganisationUnit o where" );
        
        if ( name != null )
        {
            hql.append(  " lower(name) like :name and" );
        }
        
        if ( groups != null )
        {
            for ( int i = 0; i < groups.size(); i++ )
            {
                hql.append( " :g" ).append( i ).append( " in elements( o.groups ) and" );
            }
        }

        hql.delete( hql.length() - " and".length(), hql.length() );
        
        Query query = sessionFactory.getCurrentSession().createQuery( hql.toString() );
        
        if ( name != null )
        {
            query.setString( "name", "%" + name.toLowerCase() + "%" );
        }
        
        if ( groups != null )
        {
            int i = 0;
            
            for ( OrganisationUnitGroup group : groups )
            {
                query.setEntity( "g" + i++, group );
            }
        }
        
        return query.list();        
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitHierarchy
    // -------------------------------------------------------------------------

    public OrganisationUnitHierarchy getOrganisationUnitHierarchy()
    {
        final String sql = "SELECT organisationunitid, parentid FROM organisationunit";
        
        return new OrganisationUnitHierarchy( jdbcTemplate.query( sql, new OrganisationUnitRelationshipRowMapper() ) );
    }
        
    public void updateOrganisationUnitParent( int organisationUnitId, int parentId )
    {
        StatementHolder holder = statementManager.getHolder();
        
        final String sql = 
            "UPDATE organisationunit " + 
            "SET parentid='" + parentId + "' " +
            "WHERE organisationunitid='" + organisationUnitId + "'";
        
        holder.executeUpdate( sql );
    }
    
    // -------------------------------------------------------------------------
    // OrganisationUnitLevel
    // -------------------------------------------------------------------------

    public int addOrganisationUnitLevel( OrganisationUnitLevel level )
    {
        return (Integer) sessionFactory.getCurrentSession().save( level );
    }

    public void updateOrganisationUnitLevel( OrganisationUnitLevel level )
    {
        sessionFactory.getCurrentSession().update( level );
    }

    public OrganisationUnitLevel getOrganisationUnitLevel( int id )
    {
        return (OrganisationUnitLevel) sessionFactory.getCurrentSession().get( OrganisationUnitLevel.class, id );
    }

    public void deleteOrganisationUnitLevel( OrganisationUnitLevel level )
    {
        sessionFactory.getCurrentSession().delete( level );
    }

    public void deleteOrganisationUnitLevels()
    {
        String hql = "delete from OrganisationUnitLevel";
        
        sessionFactory.getCurrentSession().createQuery( hql ).executeUpdate();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnitLevel> getOrganisationUnitLevels()
    {
        return sessionFactory.getCurrentSession().createCriteria( OrganisationUnitLevel.class ).list();
    }

    public OrganisationUnitLevel getOrganisationUnitLevelByLevel( int level )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( OrganisationUnitLevel.class );
        
        return (OrganisationUnitLevel) criteria.add( Restrictions.eq( "level", level ) ).uniqueResult();
    }

    public OrganisationUnitLevel getOrganisationUnitLevelByName( String name )
    {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria( OrganisationUnitLevel.class );
        
        return (OrganisationUnitLevel) criteria.add( Restrictions.eq( "name", name ) ).uniqueResult();
    }

    @Override
    public int getNumberOfOrganisationUnits()
    {
        final String sql = "SELECT count(*) FROM organisationunit";

        return jdbcTemplate.queryForInt( sql );
    }

    @Override
    public int getMaxOfOrganisationUnitLevels()
    {
        final String sql = "SELECT MAX(level) FROM orgunitlevel";

        return jdbcTemplate.queryForInt( sql );
    }
}
