package org.hisp.dhis.patient.hibernate;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import static org.hisp.dhis.patient.Patient.FIXED_ATTR_AGE;
import static org.hisp.dhis.patient.Patient.FIXED_ATTR_BIRTH_DATE;
import static org.hisp.dhis.patient.Patient.FIXED_ATTR_REGISTRATION_DATE;
import static org.hisp.dhis.patient.Patient.PREFIX_FIXED_ATTRIBUTE;
import static org.hisp.dhis.patient.Patient.PREFIX_IDENTIFIER_TYPE;
import static org.hisp.dhis.patient.Patient.PREFIX_PATIENT_ATTRIBUTE;
import static org.hisp.dhis.patient.Patient.PREFIX_PROGRAM;
import static org.hisp.dhis.patient.Patient.PREFIX_PROGRAM_EVENT_BY_STATUS;
import static org.hisp.dhis.patient.Patient.PREFIX_PROGRAM_INSTANCE;
import static org.hisp.dhis.patient.Patient.PREFIX_PROGRAM_STAGE;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.PatientStore;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.validation.ValidationCriteria;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 */
@Transactional
public class HibernatePatientStore
    extends HibernateIdentifiableObjectStore<Patient>
    implements PatientStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public Collection<Patient> getByNames( String fullName, Integer min, Integer max )
    {
        if ( min == null || max == null )
        {
            return getAllLikeNameOrderedName( fullName, 0, Integer.MAX_VALUE );
        }

        return getAllLikeNameOrderedName( fullName, min, max );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> get( String name, Date birthdate, String gender )
    {
        Criteria criteria = getCriteria();
        Conjunction con = Restrictions.conjunction();
        con.add( Restrictions.ilike( "name", name ) );
        con.add( Restrictions.eq( "gender", gender ) );
        con.add( Restrictions.eq( "birthDate", birthdate ) );
        criteria.add( con );

        criteria.addOrder( Order.asc( "name" ) );

        return criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByOrgUnit( OrganisationUnit organisationUnit, Integer min, Integer max )
    {
        String hql = "select p from Patient p where p.organisationUnit = :organisationUnit order by p.id DESC";

        Query query = getQuery( hql );
        query.setEntity( "organisationUnit", organisationUnit );

        if ( min != null && max != null )
        {
            query.setFirstResult( min ).setMaxResults( max );
        }

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByOrgUnitAndNameLike( OrganisationUnit organisationUnit, String nameLike,
        Integer min, Integer max )
    {
        String hql = "select p from Patient p where p.organisationUnit = :organisationUnit "
            + " and lower(p.name) like :nameLike" + " order by p.name";

        Query query = getQuery( hql );
        query.setEntity( "organisationUnit", organisationUnit );
        query.setString( "nameLike", "%" + nameLike.toLowerCase() + "%" );

        if ( min != null && max != null )
        {
            query.setFirstResult( min ).setMaxResults( max );
        }

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByOrgUnitProgram( OrganisationUnit organisationUnit, Program program, Integer min,
        Integer max )
    {
        String hql = "select pt from Patient pt " + "inner join pt.programInstances pi "
            + "where pt.organisationUnit = :organisationUnit " + "and pi.program = :program "
            + "and pi.status = :status";

        Query query = getQuery( hql );
        query.setEntity( "organisationUnit", organisationUnit );
        query.setEntity( "program", program );
        query.setInteger( "status", ProgramInstance.STATUS_ACTIVE );

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByProgram( Program program, Integer min, Integer max )
    {
        String hql = "select pt from Patient pt inner join pt.programInstances pi "
            + "where pi.program = :program and pi.status = :status";

        Query query = getQuery( hql );
        query.setEntity( "program", program );
        query.setInteger( "status", ProgramInstance.STATUS_ACTIVE );

        return query.list();
    }

    @Override
    public int countGetPatientsByName( String fullName )
    {
        fullName = fullName.toLowerCase();
        String sql = "SELECT count(*) FROM patient where lower( name ) " + "like '%" + fullName + "%' ";

        return jdbcTemplate.queryForObject( sql, Integer.class );
    }

    @Override
    public int countListPatientByOrgunit( OrganisationUnit organisationUnit )
    {
        Query query = getQuery( "select count(p.id) from Patient p where p.organisationUnit.id=:orgUnitId " );

        query.setParameter( "orgUnitId", organisationUnit.getId() );

        Number rs = (Number) query.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    public int countGetPatientsByOrgUnitProgram( OrganisationUnit organisationUnit, Program program )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.createAlias( "programInstances", "programInstance" );
        criteria.createAlias( "programInstance.program", "program" );
        criteria.add( Restrictions.eq( "program.id", program.getId() ) );
        criteria.add( Restrictions.eq( "programInstance.status", ProgramInstance.STATUS_ACTIVE ) );

        Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getRepresentatives( Patient patient )
    {
        String hql = "select distinct p from Patient p where p.representative = :representative order by p.id DESC";

        return getQuery( hql ).setEntity( "representative", patient ).list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> search( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max )
    {
        Criteria criteria = searchPatientCriteria( false, searchKeys, orgunits, followup, patientAttributes,
            identifierTypes, statusEnrollment, min, max );

        return criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<Integer> getProgramStageInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max )
    {
        Criteria criteria = searchPatientCriteria( false, searchKeys, orgunits, followup, patientAttributes,
            identifierTypes, statusEnrollment, min, max );
        criteria.setProjection( Projections.property( "programStageInstance" ) );

        return criteria.list();
    }

    public int countSearch( List<String> searchKeys, Collection<OrganisationUnit> orgunits, Boolean followup,
        Integer statusEnrollment )
    {
        Criteria criteria = searchPatientCriteria( true, searchKeys, orgunits, followup, null, null, statusEnrollment,
            null, null );

        Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    public Grid getPatientEventReport( Grid grid, List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max )
    {

        Criteria criteria = searchPatientCriteria( false, searchKeys, orgunits, followup, patientAttributes,
            identifierTypes, statusEnrollment, min, max );

        ProjectionList proList = Projections.projectionList();
        proList.add( Projections.property( "registrationDate" ) );
        proList.add( Projections.property( "name" ) );
        proList.add( Projections.property( "birthDate" ) );
        proList.add( Projections.property( "phoneNumber" ) );
        proList.add( Projections.property( "attributeValue.patientAttribute.name" ) );
        criteria.setProjection( proList );

        // Convert HQL to SQL
        try
        {
            CriteriaImpl c = (CriteriaImpl) criteria;
            SessionImpl s = (SessionImpl) c.getSession();
            SessionFactoryImplementor factory = (SessionFactoryImplementor) s.getSessionFactory();
            String[] implementors = factory.getImplementors( c.getEntityOrClassName() );
            LoadQueryInfluencers lqis = new LoadQueryInfluencers();
            CriteriaLoader loader = new CriteriaLoader(
                (OuterJoinLoadable) factory.getEntityPersister( implementors[0] ), factory, c, implementors[0], lqis );
            Field f = OuterJoinLoader.class.getDeclaredField( "sql" );
            f.setAccessible( true );
            String sql = (String) f.get( loader );

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

            GridUtils.addRows( grid, rowSet );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        return grid;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByPhoneNumber( String phoneNumber, Integer min, Integer max )
    {
        Criteria criteria = getCriteria( Restrictions.ilike( "phoneNumber", phoneNumber ) );
        if ( min != null && max != null )
        {
            criteria.setFirstResult( min ).setMaxResults( max );
        }

        return criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Patient> getByFullName( String name, OrganisationUnit organisationUnit )
    {
        Criteria criteria = getCriteria( Restrictions.eq( "name", name ).ignoreCase() );

        if ( organisationUnit != null )
        {
            criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        }

        return criteria.setMaxResults( MAX_RESULTS ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Integer> getRegistrationOrgunitIds( Date startDate, Date endDate )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.between( "registrationDate", startDate, endDate ) );
        criteria.createAlias( "organisationUnit", "orgunit" );
        criteria.setProjection( Projections.distinct( Projections.projectionList().add(
            Projections.property( "orgunit.id" ), "orgunitid" ) ) );

        return criteria.list();
    }

    public int validate( Patient patient, Program program )
    {
        if ( patient.getIdentifiers() != null && patient.getIdentifiers().size() > 0 )
        {
            Criteria criteria = getCriteria();
            criteria.createAlias( "identifiers", "patientIdentifier" );
            criteria.createAlias( "organisationUnit", "orgunit" );
            criteria.createAlias( "programInstances", "programInstance" );
            criteria.createAlias( "programInstance.program", "program" );

            Disjunction disjunction = Restrictions.disjunction();

            for ( PatientIdentifier identifier : patient.getIdentifiers() )
            {
                PatientIdentifierType patientIdentifierType = identifier.getIdentifierType();

                Conjunction conjunction = Restrictions.conjunction();
                conjunction.add( Restrictions.eq( "patientIdentifier.identifier", identifier.getIdentifier() ) );
                conjunction.add( Restrictions.eq( "patientIdentifier.identifierType", patientIdentifierType ) );

                if ( patient.getId() != 0 )
                {
                    conjunction.add( Restrictions.ne( "id", patient.getId() ) );
                }

                if ( patientIdentifierType.getType().equals( PatientIdentifierType.VALUE_TYPE_LOCAL_ID )
                    && patientIdentifierType.getOrgunitScope() )
                {
                    conjunction.add( Restrictions.eq( "orgunit.id", patient.getOrganisationUnit().getId() ) );
                }

                if ( program != null
                    && patientIdentifierType.getType().equals( PatientIdentifierType.VALUE_TYPE_LOCAL_ID )
                    && patientIdentifierType.getProgramScope() )
                {
                    conjunction.add( Restrictions.eq( "program", program ) );
                }

                if ( patientIdentifierType.getType().equals( PatientIdentifierType.VALUE_TYPE_LOCAL_ID )
                    && patientIdentifierType.getPeriodType() != null )
                {
                    Date currentDate = new Date();
                    Period period = patientIdentifierType.getPeriodType().createPeriod( currentDate );
                    conjunction
                        .add( Restrictions.between( "enrollmentdate", period.getStartDate(), period.getEndDate() ) );
                }

                disjunction.add( conjunction );
            }

            criteria.add( disjunction );

            Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();

            if ( rs != null && rs.intValue() > 0 )
            {
                return PatientService.ERROR_DUPLICATE_IDENTIFIER;
            }
        }

        if ( program != null )
        {
            ValidationCriteria validationCriteria = program.isValid( patient );

            if ( validationCriteria != null )
            {
                return PatientService.ERROR_ENROLLMENT;
            }
        }

        return PatientService.ERROR_NONE;
    }

    // -------------------------------------------------------------------------
    // Supportive methods TODO Remplement all this!
    // -------------------------------------------------------------------------

    private Criteria searchPatientCriteria( boolean count, List<String> searchKeys,
        Collection<OrganisationUnit> orgunits, Boolean followup, Collection<PatientAttribute> patientAttributes,
        Collection<PatientIdentifierType> identifierTypes, Integer statusEnrollment, Integer min, Integer max )
    {
        Criteria criteria = getCriteria();
        criteria.createAlias( "identifiers", "patientIdentifier" );
        criteria.createAlias( "organisationUnit", "orgunit" );

        boolean isSearchEvent = false;
        boolean searchAttr = false;
        boolean searchProgram = false;
        Collection<Integer> orgunitChilrenIds = null;

        if ( orgunits != null )
        {
            orgunitChilrenIds = getOrgunitChildren( orgunits );
        }

        for ( String searchKey : searchKeys )
        {
            String[] keys = searchKey.split( "_" );

            if ( keys.length <= 1 || keys[1] == null || keys[1].trim().isEmpty() || keys[1].equals( "null" ) )
            {
                continue;
            }

            String id = keys[1];
            String value = "";

            if ( keys.length >= 3 )
            {
                value = keys[2];
            }

            if ( keys[0].equals( PREFIX_FIXED_ATTRIBUTE ) )
            {
                if ( id.equals( FIXED_ATTR_BIRTH_DATE ) )
                {
                    criteria.add( Restrictions.eq( id, Integer.parseInt( value ) ) );
                }
                else if ( id.equals( FIXED_ATTR_AGE ) )
                {
                    Calendar c = Calendar.getInstance();
                    PeriodType.clearTimeOfDay( c );
                    c.add( Calendar.YEAR, -1 * Integer.parseInt( value ) );
                    criteria.add( Restrictions.eq( "birthdate", c.getTime() ) );
                }
                else if ( id.equals( FIXED_ATTR_REGISTRATION_DATE ) )
                {
                    Calendar c = Calendar.getInstance();
                    PeriodType.clearTimeOfDay( c );
                    c.add( Calendar.YEAR, -1 * Integer.parseInt( value ) );
                    criteria.add( Restrictions.eq( "registrationDate", c.getTime() ) );
                }
                else
                {
                    criteria.add( Restrictions.ilike( id, "%" + value + "%" ) );
                }
            }
            else if ( keys[0].equals( PREFIX_IDENTIFIER_TYPE ) )
            {
                String[] keyValues = id.split( " " );
                Disjunction disjunction = Restrictions.disjunction();
                for ( String v : keyValues )
                {
                    disjunction.add( Restrictions.ilike( "name", "%" + v + "%" ) );

                    Conjunction conjunction = Restrictions.conjunction();
                    conjunction.add( Restrictions.ilike( "patientIdentifier.identifier", "%" + v + "%" ) );
                    conjunction.add( Restrictions.isNotNull( "patientIdentifier.identifierType" ) );

                    disjunction.add( conjunction );
                }
                criteria.add( disjunction );
            }
            else if ( keys[0].equals( PREFIX_PATIENT_ATTRIBUTE ) )
            {
                if ( !searchAttr )
                {
                    criteria.createAlias( "attributeValues", "attributeValue" );
                    searchAttr = true;
                }

                String[] keyValues = value.split( " " );
                Conjunction conjunction = Restrictions.conjunction();
                for ( String v : keyValues )
                {
                    conjunction.add( Restrictions.eq( "attributeValue.patientAttribute.id", Integer.parseInt( id ) ) );
                    conjunction.add( Restrictions.ilike( "attributeValue.value", "%" + v + "%" ) );
                }
                criteria.add( conjunction );
            }
            else if ( keys[0].equals( PREFIX_PROGRAM ) )
            {
                if ( !searchProgram )
                {
                    criteria.createAlias( "programInstances", "programInstance" );
                    criteria.createAlias( "programInstance.program", "program" );
                    searchProgram = true;
                }

                criteria.add( Restrictions.eq( "program.id", Integer.parseInt( id ) ) );

                if ( statusEnrollment != null )
                {
                    criteria.add( Restrictions.eq( "programInstance.status", statusEnrollment ) );
                }
            }
            else if ( keys[0].equals( PREFIX_PROGRAM_INSTANCE ) )
            {
                if ( !searchProgram )
                {
                    criteria.createAlias( "programInstances", "programInstance" );
                    criteria.createAlias( "programInstance.program", "program" );
                    searchProgram = true;
                }

                criteria.add( Restrictions.eq( "programInstance.status", statusEnrollment ) );

                if ( keys.length == 5 )
                {
                    criteria.add( Restrictions.eq( "program.id", Integer.parseInt( keys[4] ) ) );
                }
            }
            else if ( keys[0].equals( PREFIX_PROGRAM_EVENT_BY_STATUS ) )
            {
                if ( !searchProgram )
                {
                    criteria.createAlias( "programInstances", "programInstance" );
                    criteria.createAlias( "programInstance.program", "program" );
                    searchProgram = true;
                }

                criteria.add( Restrictions.eq( "program.id", Integer.parseInt( id ) ) );
                criteria.add( Restrictions.eq( "programInstance.status", ProgramInstance.STATUS_ACTIVE ) );

                for ( int index = 6; index < keys.length; index++ )
                {
                    int statusEvent = Integer.parseInt( keys[index] );

                    Calendar c = Calendar.getInstance();
                    PeriodType.clearTimeOfDay( c );

                    switch ( statusEvent )
                    {
                    case ProgramStageInstance.COMPLETED_STATUS:
                        criteria.add( Restrictions.isNotNull( "programStageInstance.executiondate" ) );
                        criteria.add( Restrictions.between( "programStageInstance.executiondate",
                            DateUtils.getDefaultDate( keys[2] ), DateUtils.getDefaultDate( keys[3] ) ) );
                        criteria.add( Restrictions.eq( "programStageInstance.completed", true ) );

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            criteria.add( Restrictions.in( "eventOrg.id", orgunitChilrenIds ) );
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            criteria.add( Restrictions.eq( "eventOrg.id", Integer.parseInt( keys[4] ) ) );
                        }
                        continue;
                    case ProgramStageInstance.VISITED_STATUS:
                        criteria.add( Restrictions.isNotNull( "programStageInstance.executiondate" ) );
                        criteria.add( Restrictions.between( "programStageInstance.executiondate",
                            DateUtils.getDefaultDate( keys[2] ), DateUtils.getDefaultDate( keys[3] ) ) );
                        criteria.add( Restrictions.eq( "programStageInstance.completed", false ) );

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            criteria.add( Restrictions.in( "eventOrg.id", orgunitChilrenIds ) );
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            criteria.add( Restrictions.eq( "eventOrg.id", Integer.parseInt( keys[4] ) ) );
                        }
                        continue;
                    case ProgramStageInstance.FUTURE_VISIT_STATUS:
                        criteria.add( Restrictions.isNull( "programStageInstance.executiondate" ) );
                        criteria.add( Restrictions.between( "programStageInstance.duedate",
                            DateUtils.getDefaultDate( keys[2] ), DateUtils.getDefaultDate( keys[3] ) ) );
                        criteria.add( Restrictions.eq( "programStageInstance.status",
                            ProgramStageInstance.ACTIVE_STATUS ) );
                        criteria.add( Restrictions.le( "duedate", c.getTime() ) );

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            criteria.add( Restrictions.in( "eventOrg.id", orgunitChilrenIds ) );
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            criteria.add( Restrictions.eq( "eventOrg.id", Integer.parseInt( keys[4] ) ) );
                        }
                        continue;
                    case ProgramStageInstance.LATE_VISIT_STATUS:
                        criteria.add( Restrictions.isNull( "programStageInstance.executiondate" ) );
                        criteria.add( Restrictions.between( "programStageInstance.duedate",
                            DateUtils.getDefaultDate( keys[2] ), DateUtils.getDefaultDate( keys[3] ) ) );
                        criteria.add( Restrictions.eq( "programStageInstance.completed", false ) );
                        criteria.add( Restrictions.le( "duedate", c.getTime() ) );

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            criteria.add( Restrictions.in( "eventOrg.id", orgunitChilrenIds ) );
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            criteria.add( Restrictions.eq( "eventOrg.id", Integer.parseInt( keys[4] ) ) );
                        }
                        continue;
                    case ProgramStageInstance.SKIPPED_STATUS:
                        criteria.add( Restrictions.between( "programStageInstance.duedate",
                            DateUtils.getDefaultDate( keys[2] ), DateUtils.getDefaultDate( keys[3] ) ) );
                        criteria.add( Restrictions.eq( "programStageInstance.status",
                            ProgramStageInstance.SKIPPED_STATUS ) );

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            criteria.add( Restrictions.in( "eventOrg.id", orgunitChilrenIds ) );
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            criteria.add( Restrictions.eq( "eventOrg.id", Integer.parseInt( keys[4] ) ) );
                        }
                        continue;
                    default:
                        continue;
                    }
                }

                criteria.add( Restrictions.eq( "programInstance.status", ProgramInstance.STATUS_ACTIVE ) );
            }
            else if ( keys[0].equals( PREFIX_PROGRAM_STAGE ) )
            {

                if ( !searchProgram )
                {
                    criteria.createAlias( "programInstances", "programInstance" );
                    criteria.createAlias( "programInstance.program", "program" );
                    searchProgram = true;
                }

                if ( !isSearchEvent )
                {
                    criteria.createAlias( "programInstance.programStageInstances", "programStageInstance" );
                    criteria.createAlias( "programStageInstance.organisationUnit", "eventOrg" );
                    criteria.createAlias( "programStageInstance.programStage", "programStage" );
                    isSearchEvent = true;
                }
                criteria.add( Restrictions.eq( "programStage.id", Integer.parseInt( id ) ) );
                criteria.add( Restrictions.between( "programStageInstance.duedate",
                    DateUtils.getDefaultDate( keys[3] ), DateUtils.getDefaultDate( keys[4] ) ) );
                criteria.add( Restrictions.eq( "eventOrg.id", Integer.parseInt( keys[5] ) ) );

                Calendar c = Calendar.getInstance();
                PeriodType.clearTimeOfDay( c );

                int statusEvent = Integer.parseInt( keys[2] );
                switch ( statusEvent )
                {
                case ProgramStageInstance.COMPLETED_STATUS:
                    criteria.add( Restrictions.eq( "programStageInstance.completed", true ) );
                    break;
                case ProgramStageInstance.VISITED_STATUS:
                    criteria.add( Restrictions.isNotNull( "programStageInstance.executiondate" ) );
                    criteria.add( Restrictions.eq( "programStageInstance.completed", false ) );
                    break;
                case ProgramStageInstance.FUTURE_VISIT_STATUS:
                    criteria.add( Restrictions.isNull( "programStageInstance.executiondate" ) );
                    criteria.add( Restrictions.ge( "programStageInstance.duedate", c.getTime() ) );
                    break;
                case ProgramStageInstance.LATE_VISIT_STATUS:
                    criteria.add( Restrictions.isNull( "programStageInstance.executiondate" ) );
                    criteria.add( Restrictions.le( "programStageInstance.duedate", c.getTime() ) );
                    break;
                default:
                    break;
                }
                criteria.add( Restrictions.le( "programInstance.status", ProgramInstance.STATUS_ACTIVE ) );
            }
        }

        if ( orgunits != null && !isSearchEvent )
        {
            criteria.add( Restrictions.in( "orgunit.id", getOrganisationUnitIds( orgunits ) ) );

        }

        if ( followup != null )
        {
            if ( !searchProgram )
            {
                criteria.createAlias( "programInstances", "programInstance" );
                criteria.createAlias( "programInstance.program", "program" );
                searchProgram = true;
            }
            criteria.add( Restrictions.eq( "programInstance.followup", followup ) );
        }

        if ( min != null && max != null )
        {
            criteria.setFirstResult( min ).setMaxResults( max );
        }

        return criteria;
    }

    private Collection<Integer> getOrgunitChildren( Collection<OrganisationUnit> orgunits )
    {
        Collection<Integer> orgUnitIds = new HashSet<Integer>();

        if ( orgunits != null )
        {
            for ( OrganisationUnit orgunit : orgunits )
            {
                orgUnitIds
                    .addAll( organisationUnitService.getOrganisationUnitHierarchy().getChildren( orgunit.getId() ) );
                orgUnitIds.remove( orgunit.getId() );
            }
        }

        if ( orgUnitIds.size() == 0 )
        {
            orgUnitIds.add( 0 );
        }

        return orgUnitIds;
    }

    private Collection<Integer> getOrganisationUnitIds( Collection<OrganisationUnit> orgunits )
    {
        Collection<Integer> orgUnitIds = new HashSet<Integer>();

        for ( OrganisationUnit orgUnit : orgunits )
        {
            orgUnitIds.add( orgUnit.getId() );
        }

        if ( orgUnitIds.size() == 0 )
        {
            orgUnitIds.add( 0 );
        }

        return orgUnitIds;
    }
}
