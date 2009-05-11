package org.hisp.dhis.mapping.hibernate;

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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MapOrganisationUnitRelation;
import org.hisp.dhis.mapping.MappingStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
public class HibernateMappingStore
    implements MappingStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }

    // -------------------------------------------------------------------------
    // Map
    // -------------------------------------------------------------------------

    public int addMap( Map map )
    {
        Session session = sessionManager.getCurrentSession();

        return (Integer) session.save( map );
    }

    public void updateMap( Map map )
    {
        Session session = sessionManager.getCurrentSession();

        session.update( map );
    }

    public void deleteMap( Map map )
    {
        Session session = sessionManager.getCurrentSession();

        session.delete( map );
    }

    public Map getMap( int id )
    {
        Session session = sessionManager.getCurrentSession();

        return (Map) session.get( Map.class, id );
    }
    
    public Map getMapByMapLayerPath( String mapLayerPath )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Map.class );

        criteria.add( Restrictions.eq( "mapLayerPath", mapLayerPath ) );

        return (Map) criteria.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public Collection<Map> getMapsByType( String type )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Map.class );

        criteria.add( Restrictions.eq( "type", type ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Map> getAllMaps()
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Map.class );

        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Map> getMapsAtLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Map.class );
        
        criteria.add( Restrictions.eq( "organisationUnitLevel", organisationUnitLevel ) );

        return criteria.list();
    }

    // -------------------------------------------------------------------------
    // MapOrganisationUnitRelation
    // -------------------------------------------------------------------------

    public int addMapOrganisationUnitRelation( MapOrganisationUnitRelation mapOrganisationUnitRelation )
    {
        Session session = sessionManager.getCurrentSession();

        return (Integer) session.save( mapOrganisationUnitRelation );
    }

    public void updateMapOrganisationUnitRelation( MapOrganisationUnitRelation mapOrganisationUnitRelation )
    {
        Session session = sessionManager.getCurrentSession();

        session.update( mapOrganisationUnitRelation );
    }

    public void deleteMapOrganisationUnitRelation( MapOrganisationUnitRelation mapOrganisationUnitRelation )
    {
        Session session = sessionManager.getCurrentSession();

        session.delete( mapOrganisationUnitRelation );
    }

    public MapOrganisationUnitRelation getMapOrganisationUnitRelation( int id )
    {
        Session session = sessionManager.getCurrentSession();

        return (MapOrganisationUnitRelation) session.get( MapOrganisationUnitRelation.class, id );
    }
    
    public MapOrganisationUnitRelation getMapOrganisationUnitRelation( Map map, OrganisationUnit organisationUnit )
    {
        Session session = sessionManager.getCurrentSession();
        
        Criteria criteria = session.createCriteria( MapOrganisationUnitRelation.class );
        
        criteria.add( Restrictions.eq( "map", map ) );        
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        
        return (MapOrganisationUnitRelation) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<MapOrganisationUnitRelation> getAllMapOrganisationUnitRelations()
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( MapOrganisationUnitRelation.class );

        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<MapOrganisationUnitRelation> getMapOrganisationUnitRelationByMap( Map map )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( MapOrganisationUnitRelation.class );
        
        criteria.add( Restrictions.eq( "map", map ) );

        return criteria.list();        
    }

    public int deleteMapOrganisationUnitRelations( OrganisationUnit organisationUnit )
    {
        Session session = sessionManager.getCurrentSession();
        
        Query query = session.createQuery( "delete from OrganisationUnitRelation where organisationUnit = :organisationUnit" );
        
        return query.setParameter( "organisationUnit", organisationUnit ).executeUpdate();
    }

    public int deleteMapOrganisationUnitRelations( Map map )
    {
        Session session = sessionManager.getCurrentSession();
        
        Query query = session.createQuery( "delete from OrganisationUnitRelation where map = :map" );
        
        return query.setParameter( "map", map ).executeUpdate();
    }
    
    // -------------------------------------------------------------------------
    // LegendSet
    // -------------------------------------------------------------------------    
    
    public int addMapLegendSet( MapLegendSet legendSet )
    {
        Session session = sessionManager.getCurrentSession();

        return (Integer) session.save( legendSet );
    }
    
    public void updateMapLegendSet( MapLegendSet legendSet )
    {
        Session session = sessionManager.getCurrentSession();
        
        session.update( legendSet );
    }
    
    public void deleteMapLegendSet( MapLegendSet legendSet )
    {
        Session session = sessionManager.getCurrentSession();

        session.delete( legendSet );
    }
    
    public MapLegendSet getMapLegendSet( int id )
    {
        Session session = sessionManager.getCurrentSession();

        return (MapLegendSet) session.get( MapLegendSet.class, id );
    }
    
    @SuppressWarnings("unchecked")
    public Collection<MapLegendSet> getAllMapLegendSets()
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( MapLegendSet.class );

        return criteria.list();
    }
}
