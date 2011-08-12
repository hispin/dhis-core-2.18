/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.datalock.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.datalock.DataSetLock;
import org.hisp.dhis.datalock.DataSetLockStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

/**
 * @author Brajesh Murari
 * @version $Id$
 */
public class HibernateDataSetLockStore
    implements DataSetLockStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Support methods for reloading periods
    // -------------------------------------------------------------------------

    private final Period reloadPeriod( Period period )
    {
        Session session = sessionFactory.getCurrentSession();

        if ( session.contains( period ) )
        {
            return period; // Already in session, no reload needed
        }

        return periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    // -------------------------------------------------------------------------
    // DataSetLock
    // -------------------------------------------------------------------------

    public int addDataSetLock( DataSetLock dataSetLock )
    {
        dataSetLock.setPeriod( reloadPeriod( dataSetLock.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( dataSetLock );
    }

    public void updateDataSetLock( DataSetLock dataSetLock )
    {
        dataSetLock.setPeriod( reloadPeriod( dataSetLock.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.update( dataSetLock );
    }

    public void deleteDataSetLock( DataSetLock dataSetLock )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dataSetLock );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataSetLock> getAllDataSetLocks()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( DataSetLock.class ).list();
    }

    public DataSetLock getDataSetLock( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (DataSetLock) session.get( DataSetLock.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataSetLock> getDataSetLockByDataSet( DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataSetLock.class );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataSetLock> getDataSetLockByPeriod( Period period )
    {
        Session session = sessionFactory.getCurrentSession();

        Period storedPeriod = reloadPeriod( period );

        Criteria criteria = session.createCriteria( DataSetLock.class );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return criteria.list();
    }

    public DataSetLock getDataSetLockByDataSetAndPeriod( DataSet dataSet, Period period )
    {
        Session session = sessionFactory.getCurrentSession();

        Period storedPeriod = reloadPeriod( period );

        Criteria criteria = session.createCriteria( DataSetLock.class );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return (DataSetLock) criteria.uniqueResult();
    }

    public DataSetLock getDataSetLockByDataSetPeriodAndSource( DataSet dataSet, Period period, OrganisationUnit source )
    {
        Period storedPeriod = reloadPeriod( period );

        String hql = "from DataSetLock d ";
        hql += "where dataSet = :dataSet ";
        hql += "and period = :period ";
        hql += "and :source in elements(d.sources)";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );
        query.setEntity( "dataSet", dataSet );
        query.setEntity( "period", storedPeriod );
        query.setEntity( "source", source );

        return (DataSetLock) query.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataSetLock> getDataSetLocksBySource( OrganisationUnit source )
    {
        String hql = "from DataSetLock d where :source in elements(d.sources)";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );
        query.setEntity( "source", source );

        return query.list();
    }
}
