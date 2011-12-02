package org.hisp.dhis.indicator.hibernate;

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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorStore;

/**
 * @author Lars Helge Overland
 * @version $Id: HibernateIndicatorStore.java 3287 2007-05-08 00:26:53Z larshelg $
 */
public class HibernateIndicatorStore
    extends HibernateIdentifiableObjectStore<Indicator>
    implements IndicatorStore
{
    // -------------------------------------------------------------------------
    // Indicator
    // -------------------------------------------------------------------------

    @Override
    public int addIndicator( Indicator indicator )
    {
        return this.save(indicator);
    }

    @Override
    public void updateIndicator( Indicator indicator )
    {
        this.update( indicator );
    }

    public void deleteIndicator( Indicator indicator )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( indicator );
    }

    public Indicator getIndicator( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Indicator) session.get( Indicator.class, id );
    }

    public Indicator getIndicator( String uid )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Indicator.class );
        criteria.add( Restrictions.eq( "uid", uid ) );

        return (Indicator) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Indicator> getAllIndicators()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Indicator.class );
        criteria.setCacheable( true );

        return criteria.list();
    }

    public Indicator getIndicatorByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from Indicator i where i.name = :name" );

        query.setString( "name", name );

        return (Indicator) query.uniqueResult();
    }

    public Indicator getIndicatorByShortName( String shortName )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from Indicator i where i.shortName = :shortName" );

        query.setString( "shortName", shortName );

        return (Indicator) query.uniqueResult();
    }

    public Indicator getIndicatorByAlternativeName( String alternativeName )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from Indicator i where i.alternativeName = :alternativeName" );

        query.setString( "alternativeName", alternativeName );

        return (Indicator) query.uniqueResult();
    }

    public Indicator getIndicatorByCode( String code )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from Indicator i where i.code = :code" );

        query.setString( "code", code );

        return (Indicator) query.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Indicator> getIndicatorsWithGroupSets()
    {
        final String hql = "from Indicator d where d.groupSets.size > 0";

        return getQuery( hql ).setCacheable( true ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Indicator> getIndicatorsWithoutGroups()
    {
        final String hql = "from Indicator d where d.groups.size = 0";

        return getQuery( hql ).setCacheable( true ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Indicator> getIndicatorsWithDataSets()
    {
        final String hql = "from Indicator d where d.dataSets.size > 0";

        return getQuery( hql ).setCacheable( true ).list();
    }

    public int getIndicatorCount()
    {
        return getCount();
    }

    public int getIndicatorCountByName( String name )
    {
        return getCountByName( name );
    }

    public Collection<Indicator> getIndicatorsLikeName( String name )
    {
        return getLikeName( name );
    }

    public Collection<Indicator> getIndicatorsBetween( int first, int max )
    {
        return getBetween( first, max );
    }

    public Collection<Indicator> getIndicatorsBetweenByName( String name, int first, int max )
    {
        return getBetweenByName( name, first, max );
    }
}
