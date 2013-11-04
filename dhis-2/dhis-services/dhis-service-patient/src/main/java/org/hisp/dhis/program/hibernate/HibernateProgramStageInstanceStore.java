package org.hisp.dhis.program.hibernate;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceStore;
import org.hisp.dhis.program.SchedulingProgramObject;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Abyot Asalefew
 */
public class HibernateProgramStageInstanceStore
    extends HibernateIdentifiableObjectStore<ProgramStageInstance>
    implements ProgramStageInstanceStore
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    // -------------------------------------------------------------------------
    // Implemented methods
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings( "unchecked" )
    public ProgramStageInstance get( ProgramInstance programInstance, ProgramStage programStage )
    {
        List<ProgramStageInstance> list = new ArrayList<ProgramStageInstance>( getCriteria(
            Restrictions.eq( "programInstance", programInstance ), Restrictions.eq( "programStage", programStage ) )
            .addOrder( Order.asc( "id" ) ).list() );

        return list.isEmpty() ? null : list.get( list.size() - 1 );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> getAll( ProgramInstance programInstance, ProgramStage programStage )
    {
        Criteria criteria = getCriteria( Restrictions.eq( "programInstance", programInstance ),
            Restrictions.eq( "programStage", programStage ) ).addOrder( Order.asc( "id" ) );

        return criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( ProgramStage programStage )
    {
        return getCriteria( Restrictions.eq( "programStage", programStage ) ).list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Collection<ProgramInstance> programInstances, boolean completed )
    {
        return getCriteria( Restrictions.in( "programInstance", programInstances ),
            Restrictions.eq( "completed", completed ) ).list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> get( Patient patient, Boolean completed )
    {
        String hql = "from ProgramStageInstance where programInstance.patient = :patient and completed = :completed";

        return getQuery( hql ).setEntity( "patient", patient ).setBoolean( "completed", completed ).list();
    }


    @Override
    public void removeEmptyEvents( ProgramStage programStage, OrganisationUnit organisationUnit )
    {
        String sql = "delete from programstageinstance where programstageid=" + programStage.getId()
            + " and organisationunitid=" + organisationUnit.getId() + " and programstageinstanceid not in "
            + "(select pdv.programstageinstanceid from patientdatavalue pdv )";
        jdbcTemplate.execute( sql );
    }

    @Override
    public void update( Collection<Integer> programStageInstanceIds, OutboundSms outboundSms )
    {
        for ( Integer programStageInstanceId : programStageInstanceIds )
        {
            if ( programStageInstanceId != null && programStageInstanceId != 0 )
            {
                ProgramStageInstance programStageInstance = get( programStageInstanceId );

                List<OutboundSms> outboundSmsList = programStageInstance.getOutboundSms();

                if ( outboundSmsList == null )
                {
                    outboundSmsList = new ArrayList<OutboundSms>();
                }

                outboundSmsList.add( outboundSms );
                programStageInstance.setOutboundSms( outboundSmsList );
                update( programStageInstance );
            }
        }
    }

    @Override
    public Collection<SchedulingProgramObject> getSendMesssageEvents()
    {
        String sql = " ( " + sendMessageToPatientSql() + " ) ";

        sql += " UNION ( " + sendMessageToHealthWorkerSql() + " ) ";

        sql += " UNION ( " + sendMessageToOrgunitRegisteredSql() + " ) ";

        sql += " UNION ( " + sendMessageToUsersSql() + " ) ";

        sql += " UNION ( " + sendMessageToUserGroupsSql() + " ) ";

        SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );

        int cols = rs.getMetaData().getColumnCount();

        Collection<SchedulingProgramObject> schedulingProgramObjects = new HashSet<SchedulingProgramObject>();

        while ( rs.next() )
        {
            String message = "";
            for ( int i = 1; i <= cols; i++ )
            {
                message = rs.getString( "templatemessage" );
                String patientName = rs.getString( "name" );
                String organisationunitName = rs.getString( "orgunitName" );
                String programName = rs.getString( "programName" );
                String programStageName = rs.getString( "programStageName" );
                String daysSinceDueDate = rs.getString( "days_since_due_date" );
                String dueDate = rs.getString( "duedate" ).split( " " )[0];// just
                // get
                // date,
                // remove
                // timestamp

                message = message.replace( PatientReminder.TEMPLATE_MESSSAGE_PATIENT_NAME, patientName );
                message = message.replace( PatientReminder.TEMPLATE_MESSSAGE_PROGRAM_NAME, programName );
                message = message.replace( PatientReminder.TEMPLATE_MESSSAGE_PROGAM_STAGE_NAME, programStageName );
                message = message.replace( PatientReminder.TEMPLATE_MESSSAGE_DUE_DATE, dueDate );
                message = message.replace( PatientReminder.TEMPLATE_MESSSAGE_ORGUNIT_NAME, organisationunitName );
                message = message.replace( PatientReminder.TEMPLATE_MESSSAGE_DAYS_SINCE_DUE_DATE, daysSinceDueDate );
            }

            SchedulingProgramObject schedulingProgramObject = new SchedulingProgramObject();
            schedulingProgramObject.setProgramStageInstanceId( rs.getInt( "programstageinstanceid" ) );
            schedulingProgramObject.setPhoneNumber( rs.getString( "phonenumber" ) );
            schedulingProgramObject.setMessage( message );

            schedulingProgramObjects.add( schedulingProgramObject );
        }

        return schedulingProgramObjects;
    }

    @Override
    public int getStatisticalProgramStageReport( ProgramStage programStage, Collection<Integer> orgunitIds,
        Date startDate, Date endDate, int status )
    {
        Criteria criteria = getStatisticalProgramStageCriteria( programStage, orgunitIds, startDate, endDate, status );

        Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> getStatisticalProgramStageDetailsReport( ProgramStage programStage,
        Collection<Integer> orgunitIds, Date startDate, Date endDate, int status, Integer min, Integer max )
    {
        Criteria criteria = getStatisticalProgramStageCriteria( programStage, orgunitIds, startDate, endDate, status );

        if ( min != null && max != null )
        {
            criteria.setFirstResult( min );
            criteria.setMaxResults( max );
        }

        return criteria.list();
    }

    @Override
    public int getOverDueCount( ProgramStage programStage, Collection<Integer> orgunitIds, Date startDate, Date endDate )
    {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add( Calendar.DATE, -1 );
        PeriodType.clearTimeOfDay( yesterday );
        Date now = yesterday.getTime();

        if ( endDate.before( now ) )
        {
            now = endDate;
        }

        Criteria criteria = getCriteria();
        criteria.createAlias( "programInstance", "programInstance" );
        criteria.createAlias( "programInstance.patient", "patient" );
        criteria.createAlias( "patient.organisationUnit", "regOrgunit" );
        criteria.add( Restrictions.eq( "programStage", programStage ) );
        criteria.add( Restrictions.isNull( "programInstance.endDate" ) );
        criteria.add( Restrictions.isNull( "executionDate" ) );
        criteria.add( Restrictions.between( "dueDate", startDate, now ) );
        criteria.add( Restrictions.in( "regOrgunit.id", orgunitIds ) );
        criteria.setProjection( Projections.rowCount() ).uniqueResult();

        Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Program program, Collection<Integer> orgunitIds, Date startDate,
        Date endDate, Boolean completed )
    {
        return getCriteria( program, orgunitIds, startDate, endDate, completed ).list();
    }

    @Override
    public int count( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate, Boolean completed )
    {
        Number rs = (Number) getCriteria( program, orgunitIds, startDate, endDate, completed ).setProjection(
            Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    public int count( ProgramStage programStage, Collection<Integer> orgunitIds, Date startDate, Date endDate,
        Boolean completed )
    {
        Number rs = (Number) getCriteria( programStage, orgunitIds, startDate, endDate, completed ).setProjection(
            Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    public Grid getCompleteness( Collection<Integer> orgunitIds, Program program, String startDate, String endDate,
        I18n i18n )
    {
        String sql = "SELECT ou.name as orgunit, ps.name as events, psi.completeduser as user_name, count(psi.programstageinstanceid) as number_of_events "
            + "         FROM programstageinstance psi INNER JOIN programstage ps "
            + "                         ON psi.programstageid = ps.programstageid "
            + "                 INNER JOIN organisationunit ou "
            + "                         ON ou.organisationunitid=psi.organisationunitid"
            + "                 INNER JOIN program pg "
            + "                         ON pg.programid = ps.programid "
            + "         WHERE ou.organisationunitid in ( "
            + TextUtils.getCommaDelimitedString( orgunitIds )
            + " )                AND pg.programid = "
            + program.getId()
            + "         GROUP BY ou.name, ps.name, psi.completeduser, psi.completeddate, psi.completed "
            + "         HAVING psi.completeddate >= '"
            + startDate
            + "'                AND psi.completeddate <= '"
            + endDate
            + "' "
            + "                 AND psi.completed=true "
            + "         ORDER BY ou.name, ps.name, psi.completeduser";

        SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );

        // Create column with Total column

        Grid grid = new ListGrid();

        grid.setTitle( program.getDisplayName() );
        grid.setSubtitle( i18n.getString( "from" ) + " " + startDate + " " + i18n.getString( "to" ) + " " + endDate );

        int cols = rs.getMetaData().getColumnCount();

        for ( int i = 1; i <= cols; i++ )
        {
            grid.addHeader( new GridHeader( i18n.getString( rs.getMetaData().getColumnLabel( i ) ), false, false ) );
        }

        GridUtils.addRows( grid, rs );

        return grid;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Criteria getCriteria( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate,
        Boolean completed )
    {
        Criteria criteria = getCriteria();
        criteria.createAlias( "programInstance", "programInstance" );
        criteria.add( Restrictions.eq( "programInstance.program", program ) );

        if ( completed == null )
        {
            criteria.add( Restrictions.between( "programInstance.enrollmentDate", startDate, endDate ) );
        }
        else
        {
            if ( completed )
            {
                criteria.add( Restrictions.and( Restrictions.eq( "completed", true ),
                    Restrictions.between( "executionDate", startDate, endDate ),
                    Restrictions.in( "organisationUnit.id", orgunitIds ) ) );
            }
            else
            {
                criteria.createAlias( "programInstance.patient", "patient" );
                criteria.createAlias( "patient.organisationUnit", "regOrgunit" );
                criteria.add( Restrictions.or( Restrictions.and( Restrictions.isNull( "executionDate" ),
                    Restrictions.between( "dueDate", startDate, endDate ),
                    Restrictions.in( "regOrgunit.id", orgunitIds ) ), Restrictions.and(
                    Restrictions.eq( "completed", false ), Restrictions.isNotNull( "executionDate" ),
                    Restrictions.between( "executionDate", startDate, endDate ),
                    Restrictions.in( "organisationUnit.id", orgunitIds ) ) ) );
            }
        }

        return criteria;
    }

    private Criteria getCriteria( ProgramStage programStage, Collection<Integer> orgunitIds, Date startDate,
        Date endDate, Boolean completed )
    {
        Criteria criteria = getCriteria();
        criteria.createAlias( "programInstance", "programInstance" );
        criteria.add( Restrictions.eq( "programStage", programStage ) );

        if ( completed == null )
        {
            criteria.createAlias( "programInstance.patient", "patient" );
            criteria.createAlias( "patient.organisationUnit", "regOrgunit" );
            criteria.add( Restrictions.or( Restrictions.and( Restrictions.eq( "completed", true ),
                Restrictions.between( "executionDate", startDate, endDate ),
                Restrictions.in( "organisationUnit.id", orgunitIds ) ), Restrictions.and(
                Restrictions.eq( "completed", false ), Restrictions.isNotNull( "executionDate" ),
                Restrictions.between( "executionDate", startDate, endDate ),
                Restrictions.in( "organisationUnit.id", orgunitIds ) ),
                Restrictions.and( Restrictions.eq( "completed", false ), Restrictions.isNull( "executionDate" ),
                    Restrictions.between( "dueDate", startDate, endDate ),
                    Restrictions.in( "regOrgunit.id", orgunitIds ) ), Restrictions.and(
                    Restrictions.eq( "status", ProgramStageInstance.SKIPPED_STATUS ),
                    Restrictions.between( "dueDate", startDate, endDate ),
                    Restrictions.in( "regOrgunit.id", orgunitIds ) ) ) );
        }
        else
        {
            if ( completed )
            {
                criteria.add( Restrictions.and( Restrictions.eq( "completed", true ),
                    Restrictions.between( "executionDate", startDate, endDate ),
                    Restrictions.in( "organisationUnit.id", orgunitIds ) ) );
            }
            else
            {
                criteria.createAlias( "programInstance.patient", "patient" );
                criteria.createAlias( "patient.organisationUnit", "regOrgunit" );
                criteria.add( Restrictions.and( Restrictions.eq( "completed", false ),
                    Restrictions.isNotNull( "executionDate" ),
                    Restrictions.between( "executionDate", startDate, endDate ),
                    Restrictions.in( "organisationUnit.id", orgunitIds ) ) );
            }
        }

        return criteria;
    }

    private Criteria getStatisticalProgramStageCriteria( ProgramStage programStage, Collection<Integer> orgunitIds,
        Date startDate, Date endDate, int status )
    {
        Criteria criteria = getCriteria( Restrictions.eq( "programStage", programStage ),
            Restrictions.isNull( "programInstance.endDate" ) );
        criteria.createAlias( "programInstance", "programInstance" );
        criteria.createAlias( "programInstance.patient", "patient" );
        criteria.createAlias( "patient.organisationUnit", "regOrgunit" );
        criteria.add( Restrictions.in( "regOrgunit.id", orgunitIds ) );

        switch ( status )
        {
        case ProgramStageInstance.COMPLETED_STATUS:
            criteria.add( Restrictions.eq( "completed", true ) );
            criteria.add( Restrictions.between( "executionDate", startDate, endDate ) );
            break;
        case ProgramStageInstance.VISITED_STATUS:
            criteria.add( Restrictions.eq( "completed", false ) );
            criteria.add( Restrictions.between( "executionDate", startDate, endDate ) );
            break;
        case ProgramStageInstance.FUTURE_VISIT_STATUS:
            criteria.add( Restrictions.between( "programInstance.enrollmentDate", startDate, endDate ) );
            criteria.add( Restrictions.isNull( "executionDate" ) );
            criteria.add( Restrictions.ge( "dueDate", new Date() ) );
            break;
        case ProgramStageInstance.LATE_VISIT_STATUS:
            criteria.add( Restrictions.between( "programInstance.enrollmentDate", startDate, endDate ) );
            criteria.add( Restrictions.isNull( "executionDate" ) );
            criteria.add( Restrictions.lt( "dueDate", new Date() ) );
            break;
        default:
            break;
        }

        return criteria;
    }

    @Override
    public int averageNumberCompleted( Program program, Collection<Integer> orgunitIds, Date after, Date before,
        int status )
    {
        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstancesByStatus(
            ProgramInstance.STATUS_COMPLETED, program, orgunitIds, after, before );
        Criteria criteria = getCriteria();
        criteria.createAlias( "programInstance", "programInstance" );
        criteria.createAlias( "programStage", "programStage" );
        criteria.createAlias( "programInstance.patient", "patient" );
        criteria.add( Restrictions.eq( "programInstance.program", program ) );
        criteria.add( Restrictions.eq( "programInstance.status", status ) );
        criteria.add( Restrictions.in( "organisationUnit.id", orgunitIds ) );
        criteria.add( Restrictions.between( "programInstance.endDate", after, before ) );
        criteria.add( Restrictions.eq( "completed", true ) );
        if ( programInstances != null && programInstances.size() > 0 )
        {
            criteria.add( Restrictions.not( Restrictions.in( "programInstance", programInstances ) ) );
        }

        Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();
        return rs != null ? rs.intValue() : 0;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Integer> getOrgunitIds( Date startDate, Date endDate )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.between( "executionDate", startDate, endDate ) );
        criteria.createAlias( "organisationUnit", "orgunit" );
        criteria.setProjection( Projections.distinct( Projections.projectionList().add(
            Projections.property( "orgunit.id" ), "orgunitid" ) ) );
        return criteria.list();
    }

    // ---------------------------------------------------------------------
    // Supportive methods
    // ---------------------------------------------------------------------

    private String sendMessageToPatientSql()
    {
        return "select psi.programstageinstanceid, p.phonenumber, prm.templatemessage, p.name, org.name as orgunitName "
            + ",pg.name as programName, ps.name as programStageName, psi.duedate,(DATE(now()) - DATE(psi.duedate) ) as days_since_due_date "
            + "from patient p INNER JOIN programinstance pi "
            + "     ON p.patientid=pi.patientid "
            + " INNER JOIN programstageinstance psi  "
            + "     ON psi.programinstanceid=pi.programinstanceid "
            + " INNER JOIN program pg  "
            + "     ON pg.programid=pi.programid "
            + " INNER JOIN programstage ps  "
            + "     ON ps.programstageid=psi.programstageid "
            + " INNER JOIN organisationunit org  "
            + "     ON org.organisationunitid = p.organisationunitid "
            + " INNER JOIN patientreminder prm  "
            + "     ON prm.programstageid = ps.programstageid "
            + "WHERE pi.status="
            + ProgramInstance.STATUS_ACTIVE
            + "     and p.phonenumber is not NULL and p.phonenumber != '' "
            + "     and prm.templatemessage is not NULL and prm.templatemessage != '' "
            + "     and pg.type=1 and prm.daysallowedsendmessage is not null  "
            + "     and psi.executiondate is null "
            + "     and (  DATE(now()) - DATE(psi.duedate) ) = prm.daysallowedsendmessage "
            + "     and prm.whentosend is null and prm.sendto = " + PatientReminder.SEND_TO_PATIENT;
    }

    private String sendMessageToHealthWorkerSql()
    {
        return "SELECT psi.programstageinstanceid, uif.phonenumber, prm.templatemessage, p.name, org.name as orgunitName, "
            + "pg.name as programName, ps.name as programStageName, psi.duedate, "
            + "         (DATE(now()) - DATE(psi.duedate) ) as days_since_due_date "
            + " FROM patient p INNER JOIN programinstance pi "
            + "          ON p.patientid=pi.patientid "
            + "           INNER JOIN programstageinstance psi  "
            + "                ON psi.programinstanceid=pi.programinstanceid "
            + "             INNER JOIN program pg  "
            + "               ON pg.programid=pi.programid "
            + "           INNER JOIN programstage ps  "
            + "               ON ps.programstageid=psi.programstageid "
            + "           INNER JOIN organisationunit org  "
            + "               ON org.organisationunitid = p.organisationunitid "
            + "           INNER JOIN patientreminder prm  "
            + "               ON prm.programstageid = ps.programstageid "
            + "           INNER JOIN users us"
            + "               ON us.userid=p.healthworkerid "
            + "           INNER JOIN userinfo uif "
            + "               ON us.userid=uif.userinfoid "
            + " WHERE pi.status="
            + ProgramInstance.STATUS_ACTIVE
            + " and uif.phonenumber is not NULL and uif.phonenumber != '' "
            + "               and prm.templatemessage is not NULL and prm.templatemessage != '' "
            + "               and pg.type=1 and prm.daysallowedsendmessage is not null "
            + "               and psi.executiondate is null "
            + "               and (  DATE(now()) - DATE(psi.duedate) ) = prm.daysallowedsendmessage "
            + "               and prm.whentosend is null and prm.sendto = " + PatientReminder.SEND_TO_HEALTH_WORKER;
    }

    private String sendMessageToOrgunitRegisteredSql()
    {
        return "select psi.programstageinstanceid, ou.phonenumber, prm.templatemessage, p.name, org.name as orgunitName, "
            + "pg.name as programName, ps.name as programStageName, psi.duedate,"
            + "(DATE(now()) - DATE(psi.duedate) ) as days_since_due_date "
            + "            from patient p INNER JOIN programinstance pi "
            + "               ON p.patientid=pi.patientid "
            + "           INNER JOIN programstageinstance psi "
            + "               ON psi.programinstanceid=pi.programinstanceid "
            + "           INNER JOIN program pg "
            + "               ON pg.programid=pi.programid "
            + "           INNER JOIN programstage ps "
            + "               ON ps.programstageid=psi.programstageid "
            + "           INNER JOIN organisationunit org "
            + "               ON org.organisationunitid = p.organisationunitid "
            + "           INNER JOIN patientreminder prm "
            + "               ON prm.programstageid = ps.programstageid "
            + "           INNER JOIN organisationunit ou "
            + "               ON ou.organisationunitid=p.organisationunitid "
            + "WHERE pi.status= "
            + ProgramInstance.STATUS_ACTIVE
            + "               and ou.phonenumber is not NULL and ou.phonenumber != '' "
            + "               and prm.templatemessage is not NULL and prm.templatemessage != '' "
            + "               and pg.type=1 and prm.daysallowedsendmessage is not null "
            + "               and psi.executiondate is null "
            + "               and (  DATE(now()) - DATE(psi.duedate) ) = prm.daysallowedsendmessage "
            + "               and prm.whentosend is null and prm.sendto = "
            + +PatientReminder.SEND_TO_ORGUGNIT_REGISTERED;
    }

    private String sendMessageToUsersSql()
    {
        return "select psi.programstageinstanceid, uif.phonenumber,prm.templatemessage, p.name, org.name as orgunitName ,"
            + " pg.name as programName, ps.name as programStageName, psi.duedate, "
            + "(DATE(now()) - DATE(psi.duedate) ) as days_since_due_date "
            + "  from patient p INNER JOIN programinstance pi "
            + "       ON p.patientid=pi.patientid "
            + "   INNER JOIN programstageinstance psi "
            + "       ON psi.programinstanceid=pi.programinstanceid "
            + "   INNER JOIN program pg "
            + "       ON pg.programid=pi.programid "
            + "   INNER JOIN programstage ps "
            + "       ON ps.programstageid=psi.programstageid "
            + "   INNER JOIN patientreminder prm "
            + "       ON prm.programstageid = ps.programstageid "
            + "   INNER JOIN organisationunit org "
            + "       ON org.organisationunitid = p.organisationunitid "
            + "   INNER JOIN usermembership ums "
            + "       ON ums.organisationunitid = p.organisationunitid "
            + "   INNER JOIN userinfo uif "
            + "       ON uif.userinfoid = ums.userinfoid "
            + "  WHERE pi.status= "
            + ProgramInstance.STATUS_ACTIVE
            + "       and uif.phonenumber is not NULL and uif.phonenumber != '' "
            + "       and prm.templatemessage is not NULL and prm.templatemessage != '' "
            + "       and pg.type=1 and prm.daysallowedsendmessage is not null "
            + "       and psi.executiondate is null "
            + "       and (  DATE(now()) - DATE(psi.duedate) ) = prm.daysallowedsendmessage "
            + "       and prm.whentosend is null and prm.sendto = "
            + PatientReminder.SEND_TO_ALL_USERS_IN_ORGUGNIT_REGISTERED;
    }

    private String sendMessageToUserGroupsSql()
    {
        return "select psi.programstageinstanceid, uif.phonenumber,prm.templatemessage, p.name, org.name as orgunitName ,"
            + " pg.name as programName, ps.name as programStageName, psi.duedate, "
            + "(DATE(now()) - DATE(psi.duedate) ) as days_since_due_date "
            + "  from patient p INNER JOIN programinstance pi "
            + "       ON p.patientid=pi.patientid "
            + "   INNER JOIN programstageinstance psi "
            + "       ON psi.programinstanceid=pi.programinstanceid "
            + "   INNER JOIN program pg "
            + "       ON pg.programid=pi.programid "
            + "   INNER JOIN programstage ps "
            + "       ON ps.programstageid=psi.programstageid "
            + "   INNER JOIN patientreminder prm "
            + "       ON prm.programstageid = ps.programstageid "
            + "   INNER JOIN organisationunit org "
            + "       ON org.organisationunitid = p.organisationunitid "
            + "   INNER JOIN usergroupmembers ugm "
            + "       ON ugm.usergroupid = prm.usergroupid "
            + "   INNER JOIN userinfo uif "
            + "       ON uif.userinfoid = ugm.userid "
            + "  WHERE pi.status= "
            + ProgramInstance.STATUS_ACTIVE
            + "       and uif.phonenumber is not NULL and uif.phonenumber != '' "
            + "       and prm.templatemessage is not NULL and prm.templatemessage != '' "
            + "       and pg.type=1 and prm.daysallowedsendmessage is not null "
            + "       and psi.executiondate is not null "
            + "       and (  DATE(now()) - DATE(psi.duedate) ) = prm.daysallowedsendmessage "
            + "       and prm.whentosend is null " + "       and prm.sendto = " + PatientReminder.SEND_TO_USER_GROUP;
    }
}