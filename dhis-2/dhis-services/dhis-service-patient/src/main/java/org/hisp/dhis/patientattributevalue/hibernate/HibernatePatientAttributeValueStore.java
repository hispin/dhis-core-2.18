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
package org.hisp.dhis.patientattributevalue.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueStore;
import org.hisp.dhis.program.Program;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class HibernatePatientAttributeValueStore
    extends HibernateGenericStore<PatientAttributeValue>
    implements PatientAttributeValueStore
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public void saveVoid( PatientAttributeValue patientAttributeValue )
    {
        sessionFactory.getCurrentSession().save( patientAttributeValue );
    }

    public int deleteByAttribute( PatientAttribute patientAttribute )
    {
        Query query = getQuery( "delete from PatientAttributeValue where patientAttribute = :patientAttribute" );
        query.setEntity( "patientAttribute", patientAttribute );
        return query.executeUpdate();
    }

    public int deleteByPatient( Patient patient )
    {
        Query query = getQuery( "delete from PatientAttributeValue where patient = :patient" );
        query.setEntity( "patient", patient );
        return query.executeUpdate();
    }

    public PatientAttributeValue get( Patient patient, PatientAttribute patientAttribute )
    {
        return (PatientAttributeValue) getCriteria( Restrictions.eq( "patient", patient ),
            Restrictions.eq( "patientAttribute", patientAttribute ) ).uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttributeValue> get( Patient patient )
    {
        return getCriteria( Restrictions.eq( "patient", patient ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttributeValue> get( PatientAttribute patientAttribute )
    {
        return getCriteria( Restrictions.eq( "patientAttribute", patientAttribute ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttributeValue> get( Collection<Patient> patients )
    {
        return getCriteria( Restrictions.in( "patient", patients ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttributeValue> searchByValue( PatientAttribute patientAttribute, String searchText )
    {
        return getCriteria( Restrictions.eq( "patientAttribute", patientAttribute ),
            Restrictions.ilike( "value", "%" + searchText + "%" ) ).list();
    }

    public int countByPatientAttributeoption( PatientAttributeOption attributeOption )
    {
        Number rs = (Number) getCriteria( Restrictions.eq( "patientAttributeOption", attributeOption ) ).setProjection(
            Projections.rowCount() ).uniqueResult();
        return rs != null ? rs.intValue() : 0;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getPatient( PatientAttribute attribute, String value )
    {
        return getCriteria(
            Restrictions.and( Restrictions.eq( "patientAttribute", attribute ), Restrictions.eq( "value", value ) ) )
            .setProjection( Projections.property( "patient" ) ).list();
    }

    public int countSearchPatientAttributeValue( PatientAttribute patientAttribute, String searchText )
    {
        Number rs = (Number) getCriteria( Restrictions.eq( "patientAttribute", patientAttribute ),
            Restrictions.ilike( "value", "%" + searchText + "%" ) ).setProjection( Projections.rowCount() )
            .uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Patient> searchPatients( PatientAttribute patientAttribute, String searchText, int min, int max )
    {
        return getCriteria( Restrictions.eq( "patientAttribute", patientAttribute ),
            Restrictions.ilike( "value", "%" + searchText + "%" ) ).setProjection(
            Projections.distinct( Projections.property( "patient" ) ) ).setFirstResult( min ).setMaxResults( max )
            .list();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Patient> searchPatients( PatientAttribute patientAttribute, String searchText )
    {
        String hql = "select pav.patient from PatientAttributeValue pav where pav.patientAttribute = :patientAttribute and pav.value like '%"
            + searchText + "%'";

        Query query = getQuery( hql );
        query.setEntity( "patientAttribute", patientAttribute );

        return query.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Patient> searchPatients( List<Integer> patientAttributeIds, List<String> searchTexts, int min,
        int max )
    {
        String hql = "SELECT DISTINCT p FROM Patient as p WHERE p in ";
        String end = "";

        int index = 0;
        for ( Integer patientAttributeId : patientAttributeIds )
        {
            end += ")";

            hql += createHQL( patientAttributeId, searchTexts.get( index ), index, patientAttributeIds.size() );

            index++;
        }

        hql += " ORDER BY p.id ASC";

        Query query = getQuery( hql + end ).setFirstResult( min ).setMaxResults( max );

        return query.list();
    }

    public int countSearchPatients( List<Integer> patientAttributeIds, List<String> searchTexts )
    {
        String hql = "SELECT COUNT( DISTINCT p ) FROM Patient as p WHERE p in ";
        String end = "";

        int index = 0;
        for ( Integer patientAttributeId : patientAttributeIds )
        {
            end += ")";

            hql += createHQL( patientAttributeId, searchTexts.get( index ), index, patientAttributeIds.size() );

            index++;
        }

        Query query = getQuery( hql + end );

        Number rs = (Number) query.uniqueResult();

        return (rs != null) ? rs.intValue() : 0;
    }

    public void updatePatientAttributeValues( PatientAttributeOption patientAttributeOption )
    {
        String sql = "UPDATE patientattributevalue SET value='" + patientAttributeOption.getName()
            + "' WHERE patientattributeoptionid='" + patientAttributeOption.getId() + "'";

        jdbcTemplate.execute( sql );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttributeValue> get( Patient patient, Program program )
    {
        String hql = "SELECT pav FROM PatientAttributeValue as pav WHERE pav.patient=:patient and pav.patientAttribute.program=:program";
        Query query = getQuery( hql );
        query.setEntity( "patient", patient );
        query.setEntity( "program", program );

        return query.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<PatientAttributeValue> getWithoutProgram( Patient patient )
    {
        String hql = "SELECT pav FROM PatientAttributeValue as pav WHERE pav.patient=:patient and pav.patientAttribute.program IS NULL";
        Query query = getQuery( hql );
        query.setEntity( "patient", patient );

        return query.list();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String createHQL( Integer patientAttributeId, String searchText, int index, int noCondition )
    {
        String hql = "";
        searchText = searchText.trim();
        boolean isSearchByAttribute = true;

        // ---------------------------------------------------------------------
        // search patients by name or identifier
        // ---------------------------------------------------------------------
        if ( patientAttributeId == null )
        {
            int startIndex = searchText.indexOf( ' ' );
            int endIndex = searchText.lastIndexOf( ' ' );

            String firstName = searchText.toString();
            String middleName = "";
            String lastName = "";
            
            // ---------------------------------------------------------------------
            // search patients by full-name or identifier
            // ---------------------------------------------------------------------
            
            if ( searchText.indexOf( ' ' ) != -1 )
            {
                firstName = searchText.substring( 0, startIndex );
                if ( startIndex == endIndex )
                {
                    middleName = "";
                    lastName = searchText.substring( startIndex + 1, searchText.length() );
                }
                else
                {
                    middleName = searchText.substring( startIndex + 1, endIndex );
                    lastName = searchText.substring( endIndex + 1, searchText.length() );
                }
            }

            hql += " ( SELECT p" + index + " FROM Patient as p" + index + " JOIN p" + index
                + ".identifiers as identifier" + index + " " + "WHERE lower(identifier" + index
                + ".identifier)=lower('" + searchText + "') " + "OR (lower(p" + index + ".firstName) LIKE lower('%"
                + firstName + "%') " + "AND lower(p" + index + ".middleName) = lower('" + middleName + "') "
                + "AND lower(p" + index + ".lastName) LIKE lower('%" + lastName + "%')) ";

            isSearchByAttribute = false;
        }
        
        // ---------------------------------------------------------------------
        // search patients by birth-date
        // ---------------------------------------------------------------------
        
        else if ( patientAttributeId == -1 )
        {
            hql += " ( SELECT p" + index + " FROM Patient AS p" + index 
                + " WHERE p" + index + ".birthDate " + searchText;
            isSearchByAttribute = false; 
        } 
        // gender
        else if ( patientAttributeId == -2 )
        {
            hql += " ( SELECT p" + index + " FROM Patient AS p" + index 
                + " WHERE p" + index + ".gender='" + searchText + "'";
            isSearchByAttribute = false; 
        }
        // age
        else if ( patientAttributeId == -3 )
        {
            hql += " ( SELECT p" + index + " FROM Patient AS p" + index 
                + " WHERE p" + index + ".integerValueOfAge='" + searchText + "'";
            isSearchByAttribute = false; 
        }
        // blood-group
        else if ( patientAttributeId == -4 )
        {
            hql += " ( SELECT p" + index + " FROM Patient AS p" + index 
                + " WHERE p" + index + ".bloodGroup='" + searchText + "'";
            isSearchByAttribute = false; 
        }
        // phone number
        else if ( patientAttributeId == -5 )
        {
            hql += " ( SELECT p" + index + " FROM Patient AS p" + index 
                + " WHERE p" + index + ".phoneNumber='" + searchText + "'";
            isSearchByAttribute = false; 
        }
        
        
        // ---------------------------------------------------------------------
        // search patients by program
        // ---------------------------------------------------------------------
        else if ( patientAttributeId == 0 )
        {
            hql += " ( SELECT p" + index + " FROM Patient AS p" + index + " " + " JOIN p" + index
                + ".programs AS program" + index + " WHERE program" + index + ".id=" + searchText;

            isSearchByAttribute = false;
        }
        // -----------------------------------------------------------------
        // search patients by attribute
        // -----------------------------------------------------------------
        else
        {
            hql += " ( SELECT pav" + index + ".patient FROM PatientAttributeValue as pav" + index + " " + "WHERE pav"
                + index + ".patientAttribute.id=" + patientAttributeId + " AND lower(pav" + index
                + ".value) LIKE lower('%" + searchText + "%') ";
        }

        if ( index < noCondition - 1 )
        {

            if ( isSearchByAttribute )
            {
                hql += " AND pav" + index + ".patient in ";
            }
            else
            {
                hql += " AND p" + index + " in ";
            }
        }

        return hql;

    }

    @Override
    public int countSearchPatients( List<Integer> patientAttributeIds, List<String> searchTexts,
        OrganisationUnit orgunit )
    {
        String hql = "SELECT COUNT( DISTINCT p ) FROM Patient as p WHERE p in ";
        String end = "";

        int index = 0;
        for ( Integer patientAttributeId : patientAttributeIds )
        {
            end += ")";

            hql += createHQL( patientAttributeId, searchTexts.get( index ), index, patientAttributeIds.size() );

            index++;
        }

        Query query = getQuery( hql + end + " AND p.organisationUnit.id=" + orgunit.getId() );

        Number rs = (Number) query.uniqueResult();

        return (rs != null) ? rs.intValue() : 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Patient> searchPatients( List<Integer> patientAttributeIds, List<String> searchTexts,
        OrganisationUnit orgunit, int min, int max )
    {
        String hql = "SELECT DISTINCT p FROM Patient as p WHERE p in ";
        String end = "";
        
        int index = 0;
        for ( Integer patientAttributeId : patientAttributeIds )
        {
            end += ")";

            hql += createHQL( patientAttributeId, searchTexts.get( index ), index, patientAttributeIds.size() );

            index++;
        }

        hql += " ORDER BY p.id ASC";

        Query query = getQuery( hql + end + " AND p.organisationUnit.id=" + orgunit.getId() ).setFirstResult( min ).setMaxResults( max );

        return query.list();
    }
}
