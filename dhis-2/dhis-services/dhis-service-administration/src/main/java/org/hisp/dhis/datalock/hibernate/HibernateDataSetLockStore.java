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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.datalock.DataSetLock;
import org.hisp.dhis.datalock.DataSetLockStore;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.source.Source;

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
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
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

    @SuppressWarnings("unused")
	private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodService.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }
    
    private final DataSet reloadDataSet( DataSet dataSet )
    {
    	Session session = sessionFactory.getCurrentSession();

        if ( session.contains( dataSet ) )
        {
            return dataSet; // Already in session, no reload needed
        }

        return dataSetService.getDataSet( dataSet.getId());
    }

    @SuppressWarnings("unused")
	private final DataSet reloadDataSetForceAdd( DataSet dataSet )
    {
    	DataSet storedDataSet = reloadDataSet( dataSet );

        if ( storedDataSet == null )
        {
            dataSetService.addDataSet( storedDataSet );

            return dataSet;
        }

        return storedDataSet;
    }
   
    // -------------------------------------------------------------------------
    // DataSetLock
    // -------------------------------------------------------------------------
    
    public int addDataSetLock( DataSetLock dataSetLock )
    {
    	dataSetLock.setPeriod( reloadPeriodForceAdd( dataSetLock.getPeriod() ) );
    	dataSetLock.setDataSet( reloadDataSetForceAdd( dataSetLock.getDataSet() ) );
    	
    	Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( dataSetLock );
    }
   
    public void updateDataSetLock( DataSetLock dataSetLock )
    {
    	dataSetLock.setPeriod( reloadPeriodForceAdd( dataSetLock.getPeriod() ) );
    	dataSetLock.setDataSet( reloadDataSetForceAdd( dataSetLock.getDataSet() ) );
    	
    	Session session = sessionFactory.getCurrentSession();

        session.update( dataSetLock );
    }
       
    public void deleteDataSetLock( DataSetLock dataSetLock )
    {
    	Session session = sessionFactory.getCurrentSession();

        session.delete( dataSetLock );
    }
   
    @SuppressWarnings("unchecked")
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
    
    public DataSetLock getDataSetLockByDataSet( DataSet dataSet )
    {
    	Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataSetLock.class );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return (DataSetLock) criteria.uniqueResult();
    }
   
    public DataSetLock getDataSetLockByPeriod( Period period )
    {
    	Session session = sessionFactory.getCurrentSession();
        
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
           return null;
        }

        Criteria criteria = session.createCriteria( DataSetLock.class );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return (DataSetLock) criteria.uniqueResult();
    }
    
    public DataSetLock getDataSetLockByDataSetAndPeriod( DataSet dataSet, Period period )
    {
    	Session session = sessionFactory.getCurrentSession();
        
        Period storedPeriod = reloadPeriod( period );     
        DataSet storedDataSet = reloadDataSet( dataSet );

        if (( storedPeriod == null ) || ( storedDataSet == null ))
        {
           return null;
        }

        Criteria criteria = session.createCriteria( DataSetLock.class );
        criteria.add( Restrictions.eq( "dataSet", storedDataSet ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return (DataSetLock) criteria.uniqueResult();
    }
    
    public DataSetLock getDataSetLockByDataSetPeriodAndSource( DataSet dataSet, Period period, Source source )
    {
    	Session session = sessionFactory.getCurrentSession();
    	
    	Period storedPeriod = reloadPeriod( period );     
        DataSet storedDataSet = reloadDataSet( dataSet );

        if (( storedPeriod == null) || (storedDataSet == null))
        {
           return null;
        }

        Criteria criteria = session.createCriteria( DataSetLock.class );
        criteria.add( Restrictions.eq( "dataSet", storedDataSet ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.createAlias( "sources", "s" );
        criteria.add( Restrictions.eq( "s.id", source.getId() ) );
        
        return (DataSetLock) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public Collection<DataSetLock> getDataSetLocksBySource( Source source )
    {
    	Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataSetLock.class );
        criteria.createAlias( "sources", "s" );
        criteria.add( Restrictions.eq( "s.id", source.getId() ) );

        return criteria.list();
    }
}
