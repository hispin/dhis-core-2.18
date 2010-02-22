package org.hisp.dhis.dataentryform.hibernate;

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
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataentryform.DataEntryFormAssociation;
import org.hisp.dhis.dataentryform.DataEntryFormAssociationStore;
import org.springframework.orm.hibernate3.HibernateQueryException;

/**
 * @author Viet
 */
public class HibernateDataEntryFormAssociationStore
    implements DataEntryFormAssociationStore
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
    // DataEntryFormAssociationStore implementation
    // -------------------------------------------------------------------------

    public void addDataEntryFormAssociation( DataEntryFormAssociation dataEntryFormAssociation )
    {
        Session session = sessionFactory.getCurrentSession();
        
        try {
            System.out.println("save ne");
            session.save( dataEntryFormAssociation );
        }catch (HibernateQueryException e) {
            e.printStackTrace();
        }

    }

    public void deleteDataEntryFormAssociation( DataEntryFormAssociation dataEntryFormAssociation )
    {
        Session session = sessionFactory.getCurrentSession();
        session.delete( dataEntryFormAssociation );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryFormAssociation> getAllDataEntryFormAssociations()
    {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria( DataEntryFormAssociation.class ).list();
    }

    public DataEntryFormAssociation getDataEntryFormAssociation( String associationTableName, int associationId )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( DataEntryFormAssociation.class );
        criteria.add( Restrictions.eq( "associationTableName", associationTableName ) ).add(
            Restrictions.eq( "associationId", associationId ) );
        return (DataEntryFormAssociation) criteria.uniqueResult();
    }

    public void updateDataEntryFormAssociation( DataEntryFormAssociation dataEntryFormAssociation )
    {
        Session session = sessionFactory.getCurrentSession();
        session.update( dataEntryFormAssociation );
    }

    public DataEntryFormAssociation getDataEntryFormAssociationByDataEntryForm( DataEntryForm dataEntryForm )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( DataEntryFormAssociation.class );
        criteria.add( Restrictions.eq( "dataEntryForm", dataEntryForm ) );

        return (DataEntryFormAssociation) criteria.uniqueResult();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataEntryForm> listDisctinctDataEntryFormByAssociationIds(String associationName, List<Integer> associationIds )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( DataEntryFormAssociation.class )
        .add( Restrictions.eq( "associationTableName", associationName ) )
        .add( Restrictions.in( "associationId", associationIds ) )
        .setProjection( Projections.distinct( Projections.property( "dataEntryForm" ) ) );
        return criteria.list();
    }
}
