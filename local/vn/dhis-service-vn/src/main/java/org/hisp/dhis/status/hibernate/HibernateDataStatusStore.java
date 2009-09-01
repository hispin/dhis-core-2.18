package org.hisp.dhis.status.hibernate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.hibernate.HibernateSessionManager;
import org.hisp.dhis.status.DataStatus;
import org.hisp.dhis.status.DataStatusStore;

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

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class HibernateDataStatusStore
    implements DataStatusStore
{
    // -------------------------------------------------
    // Dependency
    // -------------------------------------------------

    private HibernateSessionManager hibernateSessionManager;

    public void setHibernateSessionManager( HibernateSessionManager hibernateSessionManager )
    {
        this.hibernateSessionManager = hibernateSessionManager;
    }

    // -------------------------------------------------
    // Implement
    // -------------------------------------------------

    public void delete( int id )
    {
        Session session = hibernateSessionManager.getCurrentSession();

        session.delete( session.get( DataStatus.class, id ) );

    }

    public DataStatus get( int id )
    {
        Session session = hibernateSessionManager.getCurrentSession();

        return (DataStatus) session.get( DataStatus.class, id );
    }

    public void save( DataStatus dataStatus )
    {
        Session session = hibernateSessionManager.getCurrentSession();

        session.save( dataStatus );

    }

    public void update( DataStatus dataStatus )
    {
        Session session = hibernateSessionManager.getCurrentSession();

        session.update( dataStatus );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataStatus> getALL()
    {
        Session session = hibernateSessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( DataStatus.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataStatus> getDataStatusDefault()
    {
        Session session = hibernateSessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( DataStatus.class );
        criteria.add( Restrictions.eq( "frontPage", true ) );
        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataStatus> getDataStatusByDataSets( Collection<DataSet> dataSets )
    {
        Collection<DataStatus> result = new HashSet<DataStatus>();
        if ( !dataSets.isEmpty() )
        {
            Session session = hibernateSessionManager.getCurrentSession();
            
            for ( DataSet dataSet : dataSets )
            {
                Criteria criteria = session.createCriteria( DataStatus.class );            
                criteria.add( Restrictions.eq( "dataSet", dataSet ) );
                result.addAll( criteria.list() );
            }            
        }
        return result;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataStatus> getDataStatusDefaultByDataSets( Collection<DataSet> dataSets )
    {
        Collection<DataStatus> result = new HashSet<DataStatus>();
        if ( !dataSets.isEmpty() )
        {
            Session session = hibernateSessionManager.getCurrentSession();
            
            for ( DataSet dataSet : dataSets )
            {
                Criteria criteria = session.createCriteria( DataStatus.class );            
                criteria.add( Restrictions.eq( "dataSet", dataSet ) );
                criteria.add( Restrictions.eq( "frontPage", true ) );
                result.addAll( criteria.list() );
            }            
        }
        return result;

    }
}
