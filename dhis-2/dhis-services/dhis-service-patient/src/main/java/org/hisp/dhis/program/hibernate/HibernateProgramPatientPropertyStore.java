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
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramPatientProperty;
import org.hisp.dhis.program.ProgramPatientPropertyStore;

/**
 * @author Chau Thu Tran
 * 
 * @version $ HibernateProgramPatientPropertyStore.java Sep 26, 2013 16:51:42 PM
 *          $
 */
public class HibernateProgramPatientPropertyStore
    implements ProgramPatientPropertyStore
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
    // Basic ProgramPatientAttribute
    // -------------------------------------------------------------------------

    @Override
    public void save( ProgramPatientProperty programPatientAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( programPatientAttribute );
    }

    @Override
    public void update( ProgramPatientProperty programPatientAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( programPatientAttribute );
    }

    @Override
    public void delete( ProgramPatientProperty programPatientAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( programPatientAttribute );
    }

    @Override
    public ProgramPatientProperty get( Program program, PatientAttribute patientAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientProperty.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.isNotNull( "patientAttribute" ) );
        criteria.add( Restrictions.eq( "patientAttribute", patientAttribute ) );

        return (ProgramPatientProperty) criteria.uniqueResult();
    }

    @Override
    public ProgramPatientProperty get( Program program, PatientIdentifierType patientIdentifierType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientProperty.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.isNotNull( "patientIdentifierType" ) );
        criteria.add( Restrictions.eq( "patientIdentifierType", patientIdentifierType ) );

        return (ProgramPatientProperty) criteria.uniqueResult();
    }

    @Override
    public ProgramPatientProperty get( Program program, String propertyName )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientProperty.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.isNotNull( "propertyName" ) );
        criteria.add( Restrictions.eq( "propertyName", propertyName ) );

        return (ProgramPatientProperty) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<ProgramPatientProperty> getProgramPatientIdentifierTypes( Program program )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientProperty.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.isNotNull( "patientIdentifierType" ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<ProgramPatientProperty> getProgramPatientAttributes( Program program )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientProperty.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.isNotNull( "patientAttribute" ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<ProgramPatientProperty> getProgramPatientProperties( Program program )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientProperty.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.isNotNull( "propertyName" ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<PatientIdentifierType> getPatientIdentifierTypes( Program program )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( ProgramPatientProperty.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.isNotNull( "patientIdentifierType" ) );
        criteria.setProjection( Projections.property( "patientIdentifierType" ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<PatientAttribute> getPatientAttributes( Program program )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( ProgramPatientProperty.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.isNotNull( "patientAttribute" ) );
        criteria.setProjection( Projections.property( "patientAttribute" ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<String> getPatientProperties( Program program )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( ProgramPatientProperty.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.isNotNull( "patientProperty" ) );
        criteria.setProjection( Projections.property( "patientProperty" ) );

        return criteria.list();
    }

}
