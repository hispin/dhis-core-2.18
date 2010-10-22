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

package org.hisp.dhis.patient.hibernate;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class HibernatePatientStore
    extends HibernateGenericStore<Patient> implements PatientStore
{    
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> get( Boolean isDead )
    {        
        return getCriteria( Restrictions.eq( "isDead", isDead ) ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByGender( String gender )
    {
        return getCriteria( Restrictions.eq( "gender", gender ) ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByBirthDate( Date birthDate )
    {
        return getCriteria( Restrictions.eq( "birthDate", birthDate ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByNames( String name )
    {
        return getCriteria( 
            Restrictions.disjunction().add( Restrictions.ilike( "firstName", "%" + name + "%" ) ).add(
            Restrictions.ilike( "middleName", "%" + name + "%" ) ).add(
            Restrictions.ilike( "lastName", "%" + name + "%" ) ) ).addOrder( Order.asc( "firstName" ) ).list();        
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByNames( String name, int min, int max )
    {
        return getCriteria( 
            Restrictions.disjunction().add( Restrictions.ilike( "firstName", "%" + name + "%" ) ).add(
                Restrictions.ilike( "middleName", "%" + name + "%" ) ).add(
                Restrictions.ilike( "lastName", "%" + name + "%" ) ) ).addOrder( Order.asc( "firstName" ) ).setFirstResult( min ).setMaxResults( max ).list(); 
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getPatient( String firstName, String middleName, String lastName, Date birthdate , String gender)
    {
        Criteria crit = getCriteria( );
        Conjunction con = Restrictions.conjunction();
        
        if( StringUtils.isNotBlank( firstName ))
            con.add( Restrictions.eq( "firstName", firstName ) );
        
        if( StringUtils.isNotBlank( middleName ))
            con.add(Restrictions.eq( "middleName", middleName ) );
        
        if( StringUtils.isNotBlank( lastName ))
            con.add(Restrictions.eq( "lastName",  lastName ) );
        
        con.add( Restrictions.eq( "gender",  gender ) ) ;
        con.add( Restrictions.eq( "birthDate",  birthdate ) );
        
        crit.add( con );
        
        crit.addOrder( Order.asc( "firstName" ) );   
        
        return crit.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByOrgUnit( OrganisationUnit organisationUnit )
    {
        String hql = "select distinct p from Patient p where p.organisationUnit = :organisationUnit order by p.id";
    
        return getQuery( hql ).setEntity( "organisationUnit", organisationUnit ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByOrgUnit( OrganisationUnit organisationUnit, int min, int max )
    {
        String hql = "select p from Patient p where p.organisationUnit = :organisationUnit order by p.id";
       
        return getQuery( hql ).setEntity( "organisationUnit", organisationUnit ).setFirstResult( min ).setMaxResults( max ).list(); 
    }
    
    public int countGetPatientsByNames( String name )
    {
        Number rs =  (Number)getCriteria( 
            Restrictions.disjunction().add( Restrictions.ilike( "firstName", "%" + name + "%" ) ).add(
                Restrictions.ilike( "middleName", "%" + name + "%" ) ).add(
                Restrictions.ilike( "lastName", "%" + name + "%" ) ) ).setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    public int countListPatientByOrgunit( OrganisationUnit organisationUnit )
    {
        Query query = getQuery("select count(p.id) from Patient p where p.organisationUnit.id=:orgUnitId ");

        query.setParameter("orgUnitId", organisationUnit.getId());
        
        Number rs = (Number) query.uniqueResult();
        
        return rs != null ? rs.intValue() : 0;
    }
    
}
