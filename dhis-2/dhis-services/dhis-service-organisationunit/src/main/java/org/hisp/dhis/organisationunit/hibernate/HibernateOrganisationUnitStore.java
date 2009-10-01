package org.hisp.dhis.organisationunit.hibernate;

/*
 * Copyright (c) 2004-2007, University of Oslo
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.source.SourceStore;

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

    private SourceStore sourceStore;

    public void setSourceStore( SourceStore sourceStore )
    {
        this.sourceStore = sourceStore;
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

    public OrganisationUnit getOrganisationUnitByShortName( String shortName )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from OrganisationUnit o where o.shortName = :shortName" );

        query.setString( "shortName", shortName );

        return (OrganisationUnit) query.uniqueResult();
    }

    public OrganisationUnit getOrganisationUnitByCode( String code )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from OrganisationUnit o where o.code = :code" );

        query.setString( "code", code );

        return (OrganisationUnit) query.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> getRootOrganisationUnits()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "from OrganisationUnit o where o.parent is null" ).list();
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitHierarchy
    // -------------------------------------------------------------------------

    /**
     * Adds a organisation unit hierarchy with the given date. A hierarchy
     * object stores the parent-child relationship of every organisation unit in
     * a Map, where the key column holds the organisation unit id and the value
     * column the id of the parent organisation unit.
     * 
     * @param date The date to assign to the organisation unit hierarchy
     * @return Id of the organisation unit hierarchy
     */
    public int addOrganisationUnitHierarchy( Date date )
    {
        Session session = sessionFactory.getCurrentSession();

        // -----------------------------------------------------------------
        // Removes any hierarchies registered on the same date
        // -----------------------------------------------------------------

        removeOrganisationUnitHierarchies( date );

        Collection<OrganisationUnit> units = sourceStore.getAllSources( OrganisationUnit.class );

        Map<Integer, Integer> structure = new HashMap<Integer, Integer>( units.size() );

        // -----------------------------------------------------------------
        // Fills the array with id / parentId pairs for all organisation
        // units
        // -----------------------------------------------------------------

        for ( OrganisationUnit unit : units )
        {
            if ( unit.getParent() != null )
            {
                structure.put( unit.getId(), unit.getParent().getId() );
            }
            else
            {
                structure.put( unit.getId(), 0 );
            }
        }

        OrganisationUnitHierarchy organisationUnitHierarchy = new OrganisationUnitHierarchy( date, structure );

        return (Integer) session.save( organisationUnitHierarchy );
    }

    public OrganisationUnitHierarchy getOrganisationUnitHierarchy( int id )
    {
        Session session = sessionFactory.getCurrentSession();
        
        return (OrganisationUnitHierarchy) session.get( OrganisationUnitHierarchy.class, id );
    }

    public OrganisationUnitHierarchy getLatestOrganisationUnitHierarchy()
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( OrganisationUnitHierarchy.class );
        
        criteria.addOrder( Order.desc( "date" ) );
        criteria.setMaxResults( 1 );
        
        return (OrganisationUnitHierarchy) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public void removeOrganisationUnitHierarchies( Date date )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from OrganisationUnitHierarchy o where o.date = :date" );
        query.setDate( "date", date );

        Iterator<OrganisationUnitHierarchy> hierarchies = query.list().iterator();

        while ( hierarchies.hasNext() )
        {
            OrganisationUnitHierarchy hierarchy = hierarchies.next();
            session.delete( hierarchy );
        }
    }

    /**
     * Retrieves the organisation unit hierarchies between start and end date.
     * The latest hierarchy added before startdate is included.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return Collection of organisation unit hierarchies
     */
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnitHierarchy> getOrganisationUnitHierarchies( Date startDate, Date endDate )
    {
        Session session = sessionFactory.getCurrentSession();

        // -----------------------------------------------------------------
        // Counts number of hierarchies between start and end date
        // -----------------------------------------------------------------

        Query countQuery = session
            .createQuery( "select count(*) from OrganisationUnitHierarchy h where h.date > :startDate and h.date < :endDate" );
        countQuery.setDate( "startDate", startDate );
        countQuery.setDate( "endDate", endDate );

        Object count = countQuery.uniqueResult();

        int number;

        if ( count instanceof Long )
        {
            number = ((Long) count).intValue();
        }
        else
        {
            number = (Integer) count;
        }

        // -----------------------------------------------------------------
        // Retrieves ordered collection of hierarchies added before end date
        // where the number is limited to the previous count plus 1
        // -----------------------------------------------------------------

        Query query = session
            .createQuery( "from OrganisationUnitHierarchy h left join fetch h.structure where h.date < :endDate order by date desc" );
        query.setDate( "endDate", endDate );
        query.setMaxResults( number + 1 );
        List<OrganisationUnitHierarchy> list = query.list();

        // -----------------------------------------------------------------
        // Reversing list
        // -----------------------------------------------------------------

        Collections.reverse( list );

        return list;
    }

    @SuppressWarnings( "unchecked" )    
    public void deleteOrganisationUnitHierarchies()
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( OrganisationUnitHierarchy.class );
                
        Collection<OrganisationUnitHierarchy> hierarchies = criteria.list();
        
        for ( OrganisationUnitHierarchy hierarchy : hierarchies )
        {
            session.delete( hierarchy );
        }
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
}
