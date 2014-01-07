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
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramPatientAttribute;
import org.hisp.dhis.program.ProgramPatientAttributeStore;

/**
 * @author Chau Thu Tran
 * 
 * @version $ HibernateProgramPatientAttributeStore.java Jan 7, 2014 9:49:20 AM
 *          $
 */
public class HibernateProgramPatientAttributeStore
    implements ProgramPatientAttributeStore
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

    public void save( ProgramPatientAttribute programPatientAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( programPatientAttribute );
    }

    public void update( ProgramPatientAttribute programPatientAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( programPatientAttribute );
    }

    public void delete( ProgramPatientAttribute programPatientAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( programPatientAttribute );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramPatientAttribute> getAll()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientAttribute.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramPatientAttribute> get( Program program )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientAttribute.class );

        return criteria.add( Restrictions.eq( "program", program ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramPatientAttribute> get( Program program, boolean compulsory )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientAttribute.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.eq( "compulsory", compulsory ) );

        return criteria.list();
    }

    public ProgramPatientAttribute get( Program program, PatientAttribute patientAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ProgramPatientAttribute.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.add( Restrictions.eq( "patientAttribute", patientAttribute ) );

        return (ProgramPatientAttribute) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttribute> getListPatientAttribute( Program program )
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( ProgramPatientAttribute.class );
        criteria.add( Restrictions.eq( "program", program ) );
        criteria.setProjection( Projections.property( "patientAttribute" ) );
        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttribute> getPatientAttributes()
    {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( ProgramPatientAttribute.class );
        criteria.setProjection( Projections.property( "patientAttribute" ) );
        return criteria.list();
    }
}
