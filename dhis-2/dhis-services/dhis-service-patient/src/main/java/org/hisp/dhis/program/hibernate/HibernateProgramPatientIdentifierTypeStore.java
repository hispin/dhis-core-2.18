/*
 * Copyright (c) 2004-2013, University of Oslo
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

package org.hisp.dhis.program.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramPatientIdentifierType;
import org.hisp.dhis.program.ProgramPatientIdentifierTypeStore;

/**
 * @author Chau Thu Tran
 * 
 * @version $ HibernateProgramPatientIdentifierTypeStore.java Jan 7, 2014 9:49:20 AM
 *          $
 */
public class HibernateProgramPatientIdentifierTypeStore
    implements ProgramPatientIdentifierTypeStore
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
    // Basic ProgramPatientIdentifierType
    // -------------------------------------------------------------------------

    public void save( ProgramPatientIdentifierType programPatientIdentifierType )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( programPatientIdentifierType );
    }

    public void update( ProgramPatientIdentifierType programPatientIdentifierType )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( programPatientIdentifierType );
    }

    public void delete( ProgramPatientIdentifierType programPatientIdentifierType )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( programPatientIdentifierType );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramPatientIdentifierType> getAll()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientIdentifierType.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramPatientIdentifierType> get( Program program )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientIdentifierType.class );

        return criteria.add( Restrictions.eq( "program", program ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramPatientIdentifierType> get( Program program, boolean compulsory )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientIdentifierType.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.eq( "compulsory", compulsory ) );

        return criteria.list();
    }

    public ProgramPatientIdentifierType get( Program program, PatientIdentifierType patientIdentifierType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientIdentifierType.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.eq( "patientIdentifierType", patientIdentifierType ) );

        return (ProgramPatientIdentifierType) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientIdentifierType> getListPatientIdentifierType( Program program )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( ProgramPatientIdentifierType.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.setProjection( Projections.property( "patientIdentifierType" ) );
        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientIdentifierType> getPatientIdentifierTypes()
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( ProgramPatientIdentifierType.class );
        criteria.setProjection( Projections.property( "patientIdentifierType" ) );
        return criteria.list();
    }
}
