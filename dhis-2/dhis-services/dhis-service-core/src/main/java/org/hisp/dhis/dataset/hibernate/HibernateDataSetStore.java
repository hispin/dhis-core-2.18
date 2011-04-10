package org.hisp.dhis.dataset.hibernate;

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

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.util.ConversionUtils;

/**
 * @author Kristian Nordal
 * @version $Id: HibernateDataSetStore.java 3303 2007-05-14 13:39:34Z larshelg $
 */
public class HibernateDataSetStore
    extends HibernateGenericStore<DataSet>
    implements DataSetStore
{
    private static final Log log = LogFactory.getLog( HibernateDataSetStore.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    // -------------------------------------------------------------------------
    // DataSet
    // -------------------------------------------------------------------------

    public int addDataSet( DataSet dataSet )
    {
        PeriodType periodType = periodStore.getPeriodType( dataSet.getPeriodType().getClass() );

        dataSet.setPeriodType( periodType );

        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( dataSet );
    }

    public void updateDataSet( DataSet dataSet )
    {
        PeriodType periodType = periodStore.getPeriodType( dataSet.getPeriodType().getClass() );

        dataSet.setPeriodType( periodType );

        Session session = sessionFactory.getCurrentSession();

        session.update( dataSet );
    }

    public void deleteDataSet( DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dataSet );
    }

    public DataSet getDataSet( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (DataSet) session.get( DataSet.class, id );
    }

    public DataSet getDataSet( String uuid )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataSet.class );
        criteria.add( Restrictions.eq( "uuid", uuid ) );

        return (DataSet) criteria.uniqueResult();
    }

    
    public DataSet getDataSetByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataSet.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (DataSet) criteria.uniqueResult();
    }

    public DataSet getDataSetByShortName( String shortName )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataSet.class );
        criteria.add( Restrictions.eq( "shortName", shortName ) );

        return (DataSet) criteria.uniqueResult();
    }

    public DataSet getDataSetByCode( String code )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataSet.class );
        criteria.add( Restrictions.eq( "code", code ) );

        return (DataSet) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataSet> getAllDataSets()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( DataSet.class ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataSet> getDataSetsByPeriodType( PeriodType periodType )
    {
        periodType = periodStore.getPeriodType( periodType.getClass() );

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataSet.class );
        criteria.add( Restrictions.eq( "periodType", periodType ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataSet> getDataSetsBySources( Collection<? extends Source> sources )
    {
        String hql = "select distinct d from DataSet d join d.sources s where s.id in (:ids)";

        return sessionFactory.getCurrentSession().createQuery( hql )
            .setParameterList( "ids", ConversionUtils.getIdentifiers( Source.class, sources ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataSet> getDataSetsForMobile( Source source )
    {
        String hql = "from DataSet d where :source in elements(d.sources) and d.mobile = true";
        Query query = sessionFactory.getCurrentSession().createQuery( hql );
        query.setEntity( "source", source );

        return query.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataSet> getDataSetsForMobile()
    {
        String hql = "from DataSet d where d.mobile = true";
        Query query = sessionFactory.getCurrentSession().createQuery( hql );

        return query.list();
    }    

    public Collection<DataSet> getMobileDataSetsFromCategoryOption( int categoryOptionId )
    {
        StatementHolder holder = statementManager.getHolder();

        List<DataSet> mobileDataSets = new ArrayList<DataSet>();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement.executeQuery( "select * from dataset where datasetid in " +
                "(select DISTINCT datasetid from datasetmembers where dataelementid in (select dataelementid from dataelement where categorycomboid in " +
                "(select categorycomboid from categorycombos_categories where categoryid in (select categoryid from categories_categoryoptions where categoryoptionid = '"
                + categoryOptionId + "')))) and (mobile = true and mobile is not null)" );

            while ( resultSet.next() )
            {
                DataSet dataSet = getDataSet( resultSet.getInt( 1 ) );
                mobileDataSets.add( dataSet );
            }
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }

        return mobileDataSets;
    }

    public Collection<DataSet> getMobileDataSetsFromCategory( int categoryId )
    {
        StatementHolder holder = statementManager.getHolder();

        List<DataSet> mobileDataSets = new ArrayList<DataSet>();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement.executeQuery( "select * from dataset where datasetid in " + 
                "(select DISTINCT datasetid from datasetmembers where dataelementid in " +
                "(select dataelementid from dataelement where categorycomboid in (select categorycomboid from categorycombos_categories where categoryid ='"
                + categoryId + "'))) and (mobile = true and mobile is not null)" );
            
            while ( resultSet.next() )
            {
                DataSet dataSet = getDataSet( resultSet.getInt( 1 ) );
                mobileDataSets.add( dataSet );
            }
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }
        return mobileDataSets;
    }
    
    @Override
    public int getDataSetCount()
    {
        return getCount();
    }

    @Override
    public int getDataSetCountByName( String name )
    {
        return getCountByName( name );
    }

    @Override
    public Collection<DataSet> getDataSetsBetween( int first, int max )
    {
        return getBetween( first, max );
    }

    @Override
    public Collection<DataSet> getDataSetsBetweenByName( String name, int first, int max )
    {
        return getBetweenByName( name, first, max );
    }
}
