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
package org.hisp.dhis.program.hibernate;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientreport.PatientAggregateReport;
import org.hisp.dhis.patientreport.TabularReportColumn;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceStore;
import org.hisp.dhis.program.SchedulingProgramObject;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Abyot Asalefew
 */
public class HibernateProgramStageInstanceStore
    extends HibernateGenericStore<ProgramStageInstance>
    implements ProgramStageInstanceStore
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Implemented methods
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public ProgramStageInstance get( ProgramInstance programInstance, ProgramStage programStage )
    {
        List<ProgramStageInstance> list = new ArrayList<ProgramStageInstance>( getCriteria(
            Restrictions.eq( "programInstance", programInstance ), Restrictions.eq( "programStage", programStage ) )
            .addOrder( Order.asc( "id" ) ).list() );

        return (list == null || list.size() == 0) ? null : list.get( list.size() - 1 );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( ProgramStage programStage )
    {
        return getCriteria( Restrictions.eq( "programStage", programStage ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Collection<ProgramInstance> programInstances )
    {
        return getCriteria( Restrictions.in( "programInstance", programInstances ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Date dueDate )
    {
        return getCriteria( Restrictions.eq( "dueDate", dueDate ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Date dueDate, Boolean completed )
    {
        return getCriteria( Restrictions.eq( "dueDate", dueDate ), Restrictions.eq( "completed", completed ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Date startDate, Date endDate )
    {
        return (getCriteria( Restrictions.ge( "dueDate", startDate ), Restrictions.le( "dueDate", endDate ) )).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageInstance> get( Date startDate, Date endDate, Boolean completed )
    {
        return (getCriteria( Restrictions.ge( "dueDate", startDate ), Restrictions.le( "dueDate", endDate ),
            Restrictions.eq( "completed", completed ) )).list();
    }

    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> get( OrganisationUnit unit, Date after, Date before, Boolean completed )
    {
        String hql = "from ProgramStageInstance psi where psi.organisationUnit = :unit";

        if ( after != null )
        {
            hql += " and dueDate >= :after";
        }

        if ( before != null )
        {
            hql += " and dueDate <= :before";
        }

        if ( completed != null )
        {
            hql += " and completed = :completed";
        }

        Query q = getQuery( hql ).setEntity( "unit", unit );

        if ( after != null )
        {
            q.setDate( "after", after );
        }

        if ( before != null )
        {
            q.setDate( "before", before );
        }

        if ( completed != null )
        {
            q.setBoolean( "completed", completed );
        }

        return q.list();
    }

    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> get( Patient patient, Boolean completed )
    {
        String hql = "from ProgramStageInstance where programInstance.patient = :patient and completed = :completed";

        return getQuery( hql ).setEntity( "patient", patient ).setBoolean( "completed", completed ).list();
    }

    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> get( ProgramStage programStage, OrganisationUnit orgunit, Date startDate,
        Date endDate, int min, int max )
    {
        return getCriteria( Restrictions.eq( "programStage", programStage ),
            Restrictions.eq( "organisationUnit", orgunit ), Restrictions.between( "dueDate", startDate, endDate ) )
            .setFirstResult( min ).setMaxResults( max ).list();
    }

    public Grid getTabularReport( ProgramStage programStage, Map<Integer, OrganisationUnitLevel> orgUnitLevelMap,
        Collection<Integer> orgUnits, List<TabularReportColumn> columns, int level, int maxLevel, Date startDate,
        Date endDate, boolean descOrder, Boolean completed, Integer min, Integer max )
    {
        // ---------------------------------------------------------------------
        // Headers cols
        // ---------------------------------------------------------------------

        Grid grid = new ListGrid();

        grid.addHeader( new GridHeader( "id", true, true ) );
        grid.addHeader( new GridHeader( programStage.getReportDateDescription(), false, true ) );

        for ( int i = level; i <= maxLevel; i++ )
        {
            String name = orgUnitLevelMap.containsKey( i ) ? orgUnitLevelMap.get( i ).getName() : "Level " + i;
            grid.addHeader( new GridHeader( name, false, true ) );
        }

        Collection<String> deKeys = new HashSet<String>();
        for ( TabularReportColumn column : columns )
        {
            if ( !column.isMeta() )
            {
                String deKey = "element_" + column.getIdentifier();
                if ( !deKeys.contains( deKey ) )
                {
                    grid.addHeader( new GridHeader( column.getName(), column.isHidden(), true ) );
                    deKeys.add( deKey );
                }
            }
        }

        grid.addHeader( new GridHeader( "Complete", true, true ) );

        // ---------------------------------------------------------------------
        // Get SQL and build grid
        // ---------------------------------------------------------------------

        String sql = getTabularReportSql( false, programStage, columns, orgUnits, level, maxLevel, startDate, endDate,
            descOrder, completed, min, max );

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        GridUtils.addRows( grid, rowSet );

        return grid;
    }

    public int getTabularReportCount( ProgramStage programStage, List<TabularReportColumn> columns,
        Collection<Integer> organisationUnits, int level, int maxLevel, Date startDate, Date endDate, Boolean completed )
    {
        String sql = getTabularReportSql( true, programStage, columns, organisationUnits, level, maxLevel, startDate,
            endDate, false, completed, null, null );

        return jdbcTemplate.queryForInt( sql );
    }

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

    public Collection<SchedulingProgramObject> getSendMesssageEvents()
    {
        String sql = "select psi.programstageinstanceid, p.phonenumber, prm.templatemessage, p.firstname, p.middlename, p.lastname, org.name as orgunitName "
            + ",pg.name as programName, ps.name as programStageName, psi.duedate,(DATE(now()) - DATE(psi.duedate) ) as days_since_due_date,psi.duedate "
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
            + "WHERE pi.completed=false  "
            + "     and p.phonenumber is not NULL and p.phonenumber != '' "
            + "     and prm.templatemessage is not NULL and prm.templatemessage != '' "
            + "     and pg.type=1 and prm.daysallowedsendmessage is not null  "
            + "     and psi.executiondate is null "
            + "     and (  DATE(now()) - DATE(psi.duedate) ) = prm.daysallowedsendmessage ";

        SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );

        int cols = rs.getMetaData().getColumnCount();

        Collection<SchedulingProgramObject> schedulingProgramObjects = new HashSet<SchedulingProgramObject>();

        while ( rs.next() )
        {
            String message = "";
            for ( int i = 1; i <= cols; i++ )
            {

                message = rs.getString( "templatemessage" );
                String patientName = rs.getString( "firstName" );
                String organisationunitName = rs.getString( "orgunitName" );
                String programName = rs.getString( "programName" );
                String programStageName = rs.getString( "programStageName" );
                String daysSinceDueDate = rs.getString( "days_since_due_date" );
                String dueDate = rs.getString( "duedate" );

                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_PATIENT_NAME, patientName );
                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_PROGRAM_NAME, programName );
                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_PROGAM_STAGE_NAME, programStageName );
                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_DUE_DATE, dueDate );
                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_ORGUNIT_NAME, organisationunitName );
                message = message.replace( ProgramStage.TEMPLATE_MESSSAGE_DAYS_SINCE_DUE_DATE, daysSinceDueDate );
            }

            SchedulingProgramObject schedulingProgramObject = new SchedulingProgramObject();
            schedulingProgramObject.setProgramStageInstanceId( rs.getInt( "programstageinstanceid" ) );
            schedulingProgramObject.setPhoneNumber( rs.getString( "phonenumber" ) );
            schedulingProgramObject.setMessage( message );

            schedulingProgramObjects.add( schedulingProgramObject );
        }

        return schedulingProgramObjects;
    }

    public int getStatisticalProgramStageReport( ProgramStage programStage, Collection<Integer> orgunitIds,
        Date startDate, Date endDate, int status )
    {
        Criteria criteria = getStatisticalProgramStageCriteria( programStage, orgunitIds, startDate, endDate, status );

        Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

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

    public Grid getAggregateReport( int position, ProgramStage programStage, Collection<Integer> orgunitIds,
        String facilityLB, Integer deGroupBy, Map<Integer, Collection<String>> deFilters, Collection<Period> periods,
        String aggregateType, Integer limit, Boolean useCompletedEvents, I18nFormat format, I18n i18n )
    {
        String sql = "";
        List<String> deValues = new ArrayList<String>();
        String filterSQL = filterSQLStatement( deFilters );

        Grid grid = new ListGrid();
        grid.setTitle( programStage.getProgram().getDisplayName() );
        grid.setSubtitle( programStage.getDisplayName() );

        // ---------------------------------------------------------------------
        // Get SQL and build grid
        // ---------------------------------------------------------------------

        // Type = 1
        if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_COLUMN_PERIOD )
        {
            sql = getAggregateReportSQL12( programStage, orgunitIds, facilityLB, filterSQL, deGroupBy, periods,
                aggregateType, useCompletedEvents, format );
        }
        // Type = 2
        if ( position == PatientAggregateReport.POSITION_ROW_PERIOD_COLUMN_ORGUNIT )
        {
            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL12( programStage, orgunitIds, facilityLB, filterSQL, deGroupBy, periods,
                aggregateType, useCompletedEvents, format );

        }
        // Type = 3
        else if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_ROW_PERIOD )
        {
            sql = getAggregateReportSQL3( position, programStage, orgunitIds, facilityLB, filterSQL, deGroupBy,
                periods, aggregateType, useCompletedEvents, format );
        }
        // Type = 4
        else if ( position == PatientAggregateReport.POSITION_ROW_PERIOD )
        {
            sql = getAggregateReportSQL4( position, programStage, orgunitIds, facilityLB, filterSQL, deGroupBy,
                periods, aggregateType, useCompletedEvents, format );
        }
        // type = 5
        else if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT )
        {
            List<Period> firstPeriod = new ArrayList<Period>();
            firstPeriod.add( periods.iterator().next() );
            sql = getAggregateReportSQL5( position, programStage, orgunitIds, facilityLB, filterSQL, deGroupBy, periods
                .iterator().next(), aggregateType, useCompletedEvents, format );
        }

        // Type = 6 && With group-by
        else if ( (position == PatientAggregateReport.POSITION_ROW_PERIOD_COLUMN_DATA || position == PatientAggregateReport.POSITION_ROW_DATA_COLUMN_PERIOD)
            && deGroupBy != null )
        {
            deValues = dataElementService.getDataElement( deGroupBy ).getOptionSet().getOptions();

            sql = getAggregateReportSQL6( programStage, orgunitIds.iterator().next(), facilityLB, filterSQL, deGroupBy,
                deValues, periods, aggregateType, useCompletedEvents, format );
        }

        // Type = 6 && NOT group-by
        else if ( (position == PatientAggregateReport.POSITION_ROW_PERIOD_COLUMN_DATA || position == PatientAggregateReport.POSITION_ROW_DATA_COLUMN_PERIOD)
            && deGroupBy == null )
        {
            sql = getAggregateReportSQL6WithoutGroup( programStage, orgunitIds.iterator().next(), facilityLB,
                filterSQL, deGroupBy, periods, aggregateType, useCompletedEvents, format );
        }

        // Type = 7 && Group-by
        else if ( (position == PatientAggregateReport.POSITION_ROW_ORGUNIT_COLUMN_DATA || position == PatientAggregateReport.POSITION_ROW_DATA_COLUMN_ORGUNIT)
            && deGroupBy != null )
        {
            deValues = dataElementService.getDataElement( deGroupBy ).getOptionSet().getOptions();

            sql = getAggregateReportSQL7( programStage, orgunitIds, facilityLB, filterSQL, deGroupBy, deValues, periods
                .iterator().next(), aggregateType, useCompletedEvents, format );
        }

        // Type = 7 && NOT group-by
        else if ( (position == PatientAggregateReport.POSITION_ROW_ORGUNIT_COLUMN_DATA || position == PatientAggregateReport.POSITION_ROW_DATA_COLUMN_ORGUNIT)
            && deGroupBy == null )
        {
            sql = getAggregateReportSQL7WithoutGroup( programStage, orgunitIds, facilityLB, filterSQL, periods
                .iterator().next(), aggregateType, useCompletedEvents, format );

        }

        // type = 8 && With group-by
        else if ( position == PatientAggregateReport.POSITION_ROW_DATA )
        {
            sql = getAggregateReportSQL8( programStage, orgunitIds, facilityLB, filterSQL, deGroupBy, periods
                .iterator().next(), aggregateType, limit, useCompletedEvents, format );
        }

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        // Type ==2 && ==9 && ==10
        if ( position == PatientAggregateReport.POSITION_ROW_PERIOD_COLUMN_ORGUNIT
            || position == PatientAggregateReport.POSITION_ROW_DATA_COLUMN_PERIOD
            || position == PatientAggregateReport.POSITION_ROW_DATA_COLUMN_ORGUNIT )
        {
            pivotTable( grid, rowSet, i18n );
        }
        else
        {
            fillDataInGrid( grid, rowSet, i18n );
        }

        return grid;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String getTabularReportSql( boolean count, ProgramStage programStage, List<TabularReportColumn> columns,
        Collection<Integer> orgUnits, int level, int maxLevel, Date startDate, Date endDate, boolean descOrder,
        Boolean completed, Integer min, Integer max )
    {
        Set<String> deKeys = new HashSet<String>();
        String selector = count ? "count(*) " : "* ";

        String sql = "select " + selector + "from ( select DISTINCT psi.programstageinstanceid, psi.executiondate,";
        String where = "";
        String operator = "where ";

        for ( int i = level; i <= maxLevel; i++ )
        {
            sql += "(select name from organisationunit where organisationunitid=ous.idlevel" + i + ") as level_" + i
                + ",";
        }

        for ( TabularReportColumn column : columns )
        {
            if ( column.isFixedAttribute() )
            {
                sql += "p." + column.getIdentifier() + ",";

                if ( column.hasQuery() )
                {
                    where += operator + "lower(" + column.getIdentifier() + ") " + column.getQuery() + " ";
                    operator = "and ";
                }
            }
            else if ( column.isIdentifierType() )
            {
                String deKey = "identifier_" + column.getIdentifier();
                if ( !deKeys.contains( deKey ) )
                {
                    sql += "(select identifier from patientidentifier where patientid=p.patientid and patientidentifiertypeid="
                        + column.getIdentifier() + ") as identifier_" + column.getIdentifier() + ",";
                }

                if ( column.hasQuery() )
                {
                    where += operator + "lower(identifier_" + column.getIdentifier() + ") " + column.getQuery() + " ";
                    operator = "and ";
                }
            }
            else if ( column.isDynamicAttribute() )
            {
                String deKey = "attribute_" + column.getIdentifier();
                if ( !deKeys.contains( deKey ) )
                {
                    sql += "(select value from patientattributevalue where patientid=p.patientid and patientattributeid="
                        + column.getIdentifier() + ") as attribute_" + column.getIdentifier() + ",";
                }

                if ( column.hasQuery() )
                {
                    where += operator + "lower(attribute_" + column.getIdentifier() + ") " + column.getQuery() + " ";
                    operator = "and ";
                }
            }
            if ( column.isNumberDataElement() )
            {
                String deKey = "element_" + column.getIdentifier();
                if ( !deKeys.contains( deKey ) )
                {
                    sql += "(select cast( value as "
                        + statementBuilder.getDoubleColumnType()
                        + " ) from patientdatavalue where programstageinstanceid=psi.programstageinstanceid and dataelementid="
                        + column.getIdentifier() + ") as element_" + column.getIdentifier() + ",";
                    deKeys.add( deKey );
                }

                if ( column.hasQuery() )
                {
                    where += operator + "element_" + column.getIdentifier() + " " + column.getQuery() + " ";
                    operator = "and ";
                }
            }
            else if ( column.isDataElement() )
            {
                String deKey = "element_" + column.getIdentifier();
                if ( !deKeys.contains( deKey ) )
                {
                    sql += "(select value from patientdatavalue where programstageinstanceid=psi.programstageinstanceid and dataelementid="
                        + column.getIdentifier() + ") as element_" + column.getIdentifier() + ",";
                    deKeys.add( deKey );
                }

                if ( column.hasQuery() )
                {
                    where += operator + "lower(element_" + column.getIdentifier() + ") " + column.getQuery() + " ";
                    operator = "and ";
                }
            }
        }

        sql += " psi.completed ";
        sql += "from programstageinstance psi ";
        sql += "left join programinstance pi on (psi.programinstanceid=pi.programinstanceid) ";
        sql += "left join patient p on (pi.patientid=p.patientid) ";
        sql += "join organisationunit ou on (ou.organisationunitid=psi.organisationunitid) ";
        sql += "join _orgunitstructure ous on (psi.organisationunitid=ous.organisationunitid) ";

        sql += "where psi.programstageid=" + programStage.getId() + " ";

        if ( startDate != null && endDate != null )
        {
            String sDate = DateUtils.getMediumDateString( startDate );
            String eDate = DateUtils.getMediumDateString( endDate );

            sql += "and psi.executiondate >= '" + sDate + "' ";
            sql += "and psi.executiondate <= '" + eDate + "' ";
        }

        if ( orgUnits != null )
        {
            sql += "and ou.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgUnits ) + ") ";
        }
        if ( completed != null )
        {
            sql += "and psi.completed=" + completed + " ";
        }

        sql += "order by ";

        for ( int i = level; i <= maxLevel; i++ )
        {
            sql += "level_" + i + ",";
        }

        sql += "psi.executiondate ";
        sql += descOrder ? "desc " : "";
        sql += ") as tabular ";
        sql += where; // filters
        sql = sql.substring( 0, sql.length() - 1 ) + " "; // Remove last comma
        sql += (min != null && max != null) ? statementBuilder.limitRecord( min, max ) : "";

        return sql;
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

    /**
     * Aggregate report Position Orgunit Rows - Period Columns - Data Filter
     * Aggregate report Position Orgunit Columns - Period Rows - Data Filter
     * 
     **/
    private String getAggregateReportSQL12( ProgramStage programStage, Collection<Integer> roots, String facilityLB,
        String filterSQL, Integer deGroupBy, Collection<Period> periods, String aggregateType,
        Boolean useCompletedEvents, I18nFormat format )
    {
        String sql = "";

        // orgunit
        for ( Integer root : roots )
        {
            Collection<Integer> orgunitIds = getOrganisationUnits( root, facilityLB );

            sql += " (SELECT ";

            sql += "( SELECT ou.name FROM organisationunit ou ";
            sql += "WHERE ou.organisationunitid=" + root + " ) as orgunit, ";

            // -- period
            for ( Period period : periods )
            {
                String periodName = "";
                String startDate = format.formatDate( period.getStartDate() );
                String endDate = format.formatDate( period.getEndDate() );
                if ( period.getPeriodType() != null )
                {
                    periodName = format.formatPeriod( period );
                }
                else
                {
                    periodName = startDate + " -> " + endDate;
                }

                sql += " ( SELECT " + aggregateType + "(*) ";
                sql += "FROM programstageinstance psi_1 ";
                sql += "        JOIN patientdatavalue pdv_1 ";
                sql += "                ON psi_1.programstageinstanceid=pdv_1.programstageinstanceid ";
                sql += "WHERE ";
                sql += "     psi_1.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds )
                    + " )  AND ";
                sql += "     psi_1.executiondate >= '" + startDate + "' AND ";
                sql += "     psi_1.executiondate <= '" + endDate + "' AND ";
                if ( useCompletedEvents )
                {
                    sql += " psi_1.completed = true AND ";
                }
                if ( deGroupBy != null )
                {
                    sql += "(SELECT value from patientdatavalue ";
                    sql += "WHERE programstageinstanceid=psi_1.programstageinstanceid AND ";
                    sql += "      dataelementid=" + deGroupBy + ") is not null AND ";
                }
                sql += "     psi_1.programstageid=" + programStage.getId() + " ";
                sql += filterSQL + "LIMIT 1 ) as \"" + periodName + "\" ,";
            }
            sql = sql.substring( 0, sql.length() - 1 ) + " ";
            // -- end period

            sql += " ) ";
            sql += " UNION ";
        }

        sql = sql.substring( 0, sql.length() - 6 ) + " ";
        sql += "ORDER BY orgunit asc";

        return sql;
    }

    /**
     * Aggregate report Position Orgunit Rows - Period Rows - Data Filter
     * 
     **/
    private String getAggregateReportSQL3( int position, ProgramStage programStage, Collection<Integer> roots,
        String facilityLB, String filterSQL, Integer deGroupBy, Collection<Period> periods, String aggregateType,
        Boolean useCompletedEvents, I18nFormat format )
    {
        String sql = "";

        for ( Integer root : roots )
        {
            Collection<Integer> orgunitIds = getOrganisationUnits( root, facilityLB );

            for ( Period period : periods )
            {
                String periodName = "";
                String startDate = format.formatDate( period.getStartDate() );
                String endDate = format.formatDate( period.getEndDate() );
                if ( period.getPeriodType() != null )
                {
                    periodName = format.formatPeriod( period );
                }
                else
                {
                    periodName = startDate + " -> " + endDate;
                }

                sql += "( SELECT ";
                sql += "( SELECT ou.name FROM organisationunit ou WHERE organisationunitid=" + root + " ) as orgunit, ";
                sql += "'" + periodName + "' as period, ";

                sql += " ( SELECT " + aggregateType + "(pdv_1.value)   ";
                sql += "FROM ";
                sql += "   patientdatavalue pdv_1 JOIN programstageinstance psi_1 ";
                sql += "        ON psi_1.programstageinstanceid=pdv_1.programstageinstanceid ";
                sql += "   JOIN organisationunit ou on (ou.organisationunitid=psi_1.organisationunitid ) ";
                sql += "WHERE ";
                sql += "    ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) AND ";
                sql += "    psi_1.programstageid=" + programStage.getId() + " AND ";
                if ( useCompletedEvents )
                {
                    sql += " psi_1.completed = true AND ";
                }
                if ( deGroupBy != null )
                {
                    sql += "(SELECT value from patientdatavalue ";
                    sql += "WHERE programstageinstanceid=psi_1.programstageinstanceid AND ";
                    sql += "      dataelementid=" + deGroupBy + ") is not null AND ";
                }
                sql += "     psi_1.executiondate >= '" + startDate + "' AND ";
                sql += "     psi_1.executiondate <= '" + endDate + "' ";
                sql += filterSQL + " ) as " + aggregateType + "  ) ";
                sql += " UNION ";
            }
        }

        sql = sql.substring( 0, sql.length() - 6 ) + " ";

        sql += "ORDER BY orgunit asc";

        return sql;
    }

    /**
     * Aggregate report Period Rows - Orgunit Filter - Data Filter
     * 
     **/
    private String getAggregateReportSQL4( int position, ProgramStage programStage, Collection<Integer> roots,
        String facilityLB, String filterSQL, Integer deGroupBy, Collection<Period> periods, String aggregateType,
        Boolean useCompletedEvents, I18nFormat format )
    {
        String sql = "";

        for ( Integer root : roots )
        {
            Collection<Integer> orgunitIds = getOrganisationUnits( root, facilityLB );

            for ( Period period : periods )
            {
                String periodName = "";
                String startDate = format.formatDate( period.getStartDate() );
                String endDate = format.formatDate( period.getEndDate() );
                if ( period.getPeriodType() != null )
                {
                    periodName = format.formatPeriod( period );
                }
                else
                {
                    periodName = startDate + " -> " + endDate;
                }

                sql += "( SELECT ";
                sql += "'" + periodName + "' as period, ";

                sql += " ( SELECT " + aggregateType + "(pdv_1.value)   ";
                sql += "FROM ";
                sql += "   patientdatavalue pdv_1 JOIN programstageinstance psi_1 ";
                sql += "        ON psi_1.programstageinstanceid=pdv_1.programstageinstanceid ";
                sql += "   JOIN organisationunit ou on (ou.organisationunitid=psi_1.organisationunitid ) ";
                sql += "WHERE ";
                sql += "    ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) AND ";
                sql += "    psi_1.programstageid=" + programStage.getId() + " AND ";
                if ( useCompletedEvents )
                {
                    sql += " psi_1.completed = true AND ";
                }
                if ( deGroupBy != null )
                {
                    sql += "(SELECT value from patientdatavalue ";
                    sql += "WHERE programstageinstanceid=psi_1.programstageinstanceid AND ";
                    sql += "      dataelementid=" + deGroupBy + ") is not null AND ";
                }
                sql += "     psi_1.executiondate >= '" + startDate + "' AND ";
                sql += "     psi_1.executiondate <= '" + endDate + "' ";
                sql += filterSQL + " )  as " + aggregateType + ") ";
                sql += " UNION ";
            }
        }

        sql = sql.substring( 0, sql.length() - 6 );

        return sql;
    }

    /**
     * Aggregate report Position Orgunit Rows -Period Filter - Data Filter
     * 
     **/
    private String getAggregateReportSQL5( int position, ProgramStage programStage, Collection<Integer> roots,
        String facilityLB, String filterSQL, Integer deGroupBy, Period period, String aggregateType,
        Boolean useCompletedEvents, I18nFormat format )
    {
        String sql = "";

        for ( Integer root : roots )
        {
            Collection<Integer> orgunitIds = getOrganisationUnits( root, facilityLB );

            sql += "( SELECT  ";
            sql += "( SELECT ou.name  ";
            sql += "FROM organisationunit ou  ";
            sql += "WHERE ou.organisationunitid=" + root + " ) as orgunit, ";

            sql += "(select " + aggregateType + "(pdv_1.value)  ";
            sql += "FROM ";
            sql += "    patientdatavalue pdv_1 RIGHT JOIN programstageinstance psi_1 ";
            sql += "            ON psi_1.programstageinstanceid=pdv_1.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi_1.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) AND ";
            sql += "    psi_1.programstageid=" + programStage.getId() + " AND ";
            sql += "    psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
            sql += "    psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' ";
            sql += filterSQL + " ";
            if ( deGroupBy != null )
            {
                sql += " AND (SELECT value from patientdatavalue ";
                sql += "WHERE programstageinstanceid=psi_1.programstageinstanceid AND ";
                sql += "      dataelementid=" + deGroupBy + ") is not null ";
            }
            if ( useCompletedEvents )
            {
                sql += " AND psi_1.completed = true ";
            }
            sql += " ) as " + aggregateType + "  ) ";
            sql += " UNION ";
        }

        sql = sql.substring( 0, sql.length() - 6 ) + " ";

        sql += "ORDER BY orgunit asc";

        return sql;
    }

    /**
     * Aggregate report Position Orgunit Filter - Period Rows - Data Columns
     * with group-by
     **/
    private String getAggregateReportSQL6( ProgramStage programStage, Integer root, String facilityLB,
        String filterSQL, Integer deGroupBy, Collection<String> deValues, Collection<Period> periods,
        String aggregateType, Boolean useCompletedEvents, I18nFormat format )
    {
        String sql = "";

        Collection<Integer> orgunitIds = getOrganisationUnits( root, facilityLB );

        for ( Period period : periods )
        {
            String periodName = "";
            String startDate = format.formatDate( period.getStartDate() );
            String endDate = format.formatDate( period.getEndDate() );
            if ( period.getPeriodType() != null )
            {
                periodName = format.formatPeriod( period );
            }
            else
            {
                periodName = startDate + " -> " + endDate;
            }

            sql += "(SELECT '" + periodName + "' as period, ";
            for ( String deValue : deValues )
            {
                sql += "(SELECT " + aggregateType + "(value)  ";
                sql += "FROM programstageinstance psi_1 JOIN patientdatavalue pdv_1 ";
                sql += "    on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
                sql += "WHERE ";
                sql += "    psi_1.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds )
                    + "     ) AND ";
                sql += "    psi_1.executiondate >= '" + startDate + "' AND ";
                sql += "    psi_1.executiondate <= '" + endDate + "' ";
                sql += filterSQL + " AND ";
                sql += "        (SELECT value from patientdatavalue ";
                sql += "        WHERE programstageinstanceid=psi_1.programstageinstanceid AND ";
                sql += "              dataelementid=" + deGroupBy + ") = '" + deValue + "' ";
                sql += ") as \"" + deValue + "\",";
            }
            sql = sql.substring( 0, sql.length() - 1 ) + " ";

            sql += "FROM  programstageinstance psi JOIN patientdatavalue pdv ";
            sql += "    on psi.programstageinstanceid = pdv.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) AND ";
            sql += "    psi.programstageid=" + programStage.getId() + " ";
            if ( useCompletedEvents )
            {
                sql += " AND psi.completed = true ";
            }
            sql += "GROUP BY dataelementid ";
            sql += "  LIMIT 1 ";

            sql += ") UNION ";

        }

        return sql.substring( 0, sql.length() - 6 );
    }

    /**
     * Aggregate report Position Orgunit Filter - Period Rows - Data Columns
     * without group-by
     **/
    private String getAggregateReportSQL6WithoutGroup( ProgramStage programStage, Integer root, String facilityLB,
        String filterSQL, Integer deGroupBy, Collection<Period> periods, String aggregateType,
        Boolean useCompletedEvents, I18nFormat format )
    {
        String sql = "";

        Collection<Integer> orgunitIds = getOrganisationUnits( root, facilityLB );

        for ( Period period : periods )
        {
            String periodName = "";
            String startDate = format.formatDate( period.getStartDate() );
            String endDate = format.formatDate( period.getEndDate() );
            if ( period.getPeriodType() != null )
            {
                periodName = format.formatPeriod( period );
            }
            else
            {
                periodName = startDate + " -> " + endDate;
            }

            sql += "(SELECT '" + periodName + "' as period, ";

            sql += "(SELECT " + aggregateType + "(value)  ";
            sql += "FROM programstageinstance psi_1 JOIN patientdatavalue pdv_1 ";
            sql += "    on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi_1.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds )
                + "     ) AND ";
            sql += "    psi_1.executiondate >= '" + startDate + "' AND ";
            sql += "    psi_1.executiondate <= '" + endDate + "' ";
            if ( deGroupBy != null )
            {
                sql += " AND (SELECT value from patientdatavalue ";
                sql += "WHERE programstageinstanceid=psi_1.programstageinstanceid AND ";
                sql += "      dataelementid=" + deGroupBy + ") is not null ";
            }
            sql += filterSQL + ") as \"" + aggregateType + "\",";

            sql = sql.substring( 0, sql.length() - 1 ) + " ";

            sql += "FROM  programstageinstance psi JOIN patientdatavalue pdv ";
            sql += "    on psi.programstageinstanceid = pdv.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) AND ";
            sql += "    psi.programstageid=" + programStage.getId() + " ";
            if ( useCompletedEvents )
            {
                sql += " AND psi.completed = true ";
            }
            sql += "GROUP BY dataelementid ";
            sql += "  LIMIT 1 ";

            sql += ") UNION ";
        }

        return sql.substring( 0, sql.length() - 6 );
    }

    /**
     * Aggregate report Position Orgunit Rows - Period Filter - Data Columns
     * 
     **/
    private String getAggregateReportSQL7( ProgramStage programStage, Collection<Integer> roots, String facilityLB,
        String filterSQL, Integer deGroupBy, List<String> deValues, Period period, String aggregateType,
        Boolean useCompletedEvents, I18nFormat format )
    {
        String sql = "";

        for ( Integer root : roots )
        {
            Collection<Integer> orgunitIds = getOrganisationUnits( root, facilityLB );

            sql += "(SELECT ";
            sql += "( SELECT ou.name FROM organisationunit ou WHERE ou.organisationunitid=" + root + " ) as orgunit, ";
            for ( String deValue : deValues )
            {
                sql += "( SELECT " + aggregateType + "(value) FROM patientdatavalue pdv_1 ";
                sql += "        inner join programstageinstance psi_1 ";
                sql += "          on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
                sql += "WHERE ";
                sql += "        psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
                sql += "        psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND ";
                sql += "        psi_1.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgunitIds )
                    + ") AND ";
                if ( useCompletedEvents )
                {
                    sql += " psi_1.completed = true AND ";
                }
                sql += "        psi_1.programstageid=" + programStage.getId() + " ";
                sql += filterSQL + " AND ";
                sql += "   (SELECT value FROM patientdatavalue  ";
                sql += "   WHERE programstageinstanceid=psi_1.programstageinstanceid AND ";
                sql += "     dataelementid= pdv_1.dataelementid AND ";
                sql += "     dataelementid=" + deGroupBy + "  ) = '" + deValue + "' ";
                sql += ") as \"" + deValue + "\",";
            }

            sql = sql.substring( 0, sql.length() - 1 ) + " ) ";
            sql += " UNION ";
        }

        sql = sql.substring( 0, sql.length() - 6 );

        return sql;
    }

    /**
     * Aggregate report Position Orgunit Rows - Period Filter - Data Columns
     * 
     **/
    private String getAggregateReportSQL7WithoutGroup( ProgramStage programStage, Collection<Integer> roots,
        String facilityLB, String filterSQL, Period period, String aggregateType, Boolean useCompletedEvents,
        I18nFormat format )
    {

        String sql = "";

        for ( Integer root : roots )
        {
            Collection<Integer> orgunitIds = getOrganisationUnits( root, facilityLB );

            sql += "(SELECT ";
            sql += "( SELECT ou.name FROM organisationunit ou WHERE ou.organisationunitid=" + root + " ) as orgunit, ";

            sql += "( SELECT " + aggregateType + "(value) FROM patientdatavalue pdv_1 ";
            sql += "        inner join programstageinstance psi_1 ";
            sql += "          on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
            sql += "WHERE ";
            sql += "        psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
            sql += "        psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND ";
            if ( useCompletedEvents )
            {
                sql += " psi_1.completed = true AND ";
            }
            sql += "        psi_1.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgunitIds ) + ") AND ";
            sql += "        psi_1.programstageid=" + programStage.getId() + " ";
            sql += filterSQL + ") as \"" + aggregateType + "\" ) ";

            sql += " UNION ";
        }

        return sql.substring( 0, sql.length() - 6 );
    }

    /**
     * Aggregate report Position Data Rows
     * 
     **/
    private String getAggregateReportSQL8( ProgramStage programStage, Collection<Integer> roots, String facilityLB,
        String filterSQL, Integer deGroupBy, Period period, String aggregateType, Integer limit,
        Boolean useCompletedEvents, I18nFormat format )
    {
        String sql = "";
        for ( Integer root : roots )
        {
            Collection<Integer> orgunitIds = getOrganisationUnits( root, facilityLB );

            sql += "(SELECT pdv_1.value, " + aggregateType + "(pdv_1.value) as \"" + aggregateType + "\" ";
            sql += "FROM patientdatavalue pdv_1 ";
            sql += "    JOIN programstageinstance psi_1 ";
            sql += "            ON psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
            sql += "WHERE ";
            sql += " psi_1.programstageid=" + programStage.getId() + " AND ";
            if ( useCompletedEvents )
            {
                sql += " psi_1.completed = true AND ";
            }
            sql += "    psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
            sql += "    psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND ";
            sql += "    psi_1.organisationunitid in( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " )  ";
            if ( deGroupBy != null )
            {
                sql += " AND pdv_1.dataelementid=" + deGroupBy + " ";
            }
            sql += filterSQL + " ";
            sql += "GROUP BY pdv_1.value )";
            sql += " UNION ";
        }

        sql = sql.substring( 0, sql.length() - 6 ) + " ";

        sql += "ORDER BY  \"" + aggregateType + "\" desc ";

        if ( limit != null )
        {
            sql += "LIMIT " + limit;
        }

        return sql;
    }

    private void pivotTable( Grid grid, SqlRowSet rowSet, I18n i18n )
    {
        try
        {
            int cols = rowSet.getMetaData().getColumnCount();
            int total = 0;
            Map<Integer, List<Object>> columnValues = new HashMap<Integer, List<Object>>();
            int index = 2;

            grid.addHeader( new GridHeader( "", false, true ) );
            while ( rowSet.next() )
            {
                // Header grid
                grid.addHeader( new GridHeader( rowSet.getString( 1 ), false, false ) );

                // Column values
                List<Object> column = new ArrayList<Object>();
                total = 0;
                for ( int i = 2; i <= cols; i++ )
                {
                    column.add( rowSet.getObject( i ) );
                    // value
                    if ( rowSet.getMetaData().getColumnType( i ) != Types.VARCHAR )
                    {
                        total += rowSet.getInt( i );
                    }
                }
                column.add( total );
                columnValues.put( index, column );
                index++;
            }
            // Add total header
            grid.addHeader( new GridHeader( i18n.getString( "total" ), false, false ) );

            // First column
            List<Object> column = new ArrayList<Object>();
            for ( int i = 2; i <= cols; i++ )
            {
                grid.addRow();
                column.add( i18n.getString( rowSet.getMetaData().getColumnLabel( i ) ) );
            }
            grid.addRow();
            column.add( i18n.getString( "total" ) );
            grid.addColumn( column );

            // Other columns
            for ( int i = 2; i < index; i++ )
            {
                grid.addColumn( columnValues.get( i ) );
            }

            // Total column
            int allTotal = 0;
            column = new ArrayList<Object>();
            for ( int j = 0; j < cols - 1; j++ )
            {
                total = 0;
                for ( int i = 2; i < index; i++ )
                {
                    if ( rowSet.getMetaData().getColumnType( i ) != Types.VARCHAR )
                    {
                        total += (Long) columnValues.get( i ).get( j );
                    }
                }
                column.add( total );
                allTotal += total;
            }
            column.add( allTotal );
            grid.addColumn( column );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }

    private String filterSQLStatement( Map<Integer, Collection<String>> deFilters )
    {
        String filter = "";
        if ( deFilters != null )
        {
            // Get filter criteria
            Iterator<Integer> iterFilter = deFilters.keySet().iterator();
            boolean flag = false;
            while ( iterFilter.hasNext() )
            {
                Integer id = iterFilter.next();
                for ( String filterValue : deFilters.get( id ) )
                {
                    int index = filterValue.indexOf( PatientAggregateReport.SEPARATE_FILTER );
                    String operator = (filterValue.substring( 0, index ));
                    String value = filterValue.substring( index + 1, filterValue.length() );

                    filter += "AND (SELECT value ";
                    filter += "FROM patientdatavalue ";
                    filter += "WHERE programstageinstanceid=psi_1.programstageinstanceid AND ";
                    if ( !flag )
                    {
                        filter += "dataelementid= pdv_1.dataelementid AND ";
                        flag = true;
                    }
                    filter += "dataelementid=" + id + "  ";
                    filter += ") " + operator + " " + value + " ";
                }
            }
        }

        return filter;
    }

    // ---------------------------------------------------------------------
    // Get orgunitIds
    // ---------------------------------------------------------------------

    private Collection<Integer> getOrganisationUnits( Integer root, String facilityLB )
    {
        Set<Integer> orgunitIds = new HashSet<Integer>();

        if ( facilityLB.equals( "selected" ) )
        {
            orgunitIds.add( root );
        }
        else if ( facilityLB.equals( "childrenOnly" ) )
        {
            orgunitIds.addAll( organisationUnitService.getOrganisationUnitHierarchy().getChildren( root ) );
            orgunitIds.remove( root );
        }
        else
        {
            orgunitIds.addAll( organisationUnitService.getOrganisationUnitHierarchy().getChildren( root ) );
        }

        return orgunitIds;
    }

    public static void fillDataInGrid( Grid grid, SqlRowSet rs, I18n i18n )
    {
        int cols = rs.getMetaData().getColumnCount();

        // Create column with Total column
        for ( int i = 1; i <= cols; i++ )
        {
            grid.addHeader( new GridHeader( i18n.getString( rs.getMetaData().getColumnLabel( i ) ), false, false ) );
        }
        grid.addHeader( new GridHeader( i18n.getString( "total" ), false, false ) );

        int[] sumRow = new int[rs.getMetaData().getColumnCount() + 1];
        while ( rs.next() )
        {
            grid.addRow();

            int total = 0;
            for ( int i = 1; i <= cols; i++ )
            {
                // values
                if ( rs.getMetaData().getColumnType( i ) == Types.VARCHAR )
                {
                    grid.addValue( rs.getObject( i ) );
                }
                // meta column
                else
                {
                    Integer value = rs.getInt( i );
                    sumRow[i] += value;
                    grid.addValue( value );
                    total += rs.getInt( i );
                }
            }

            // total
            grid.addValue( total );
        }

        grid.addRow();
        grid.addValue( i18n.getString( "total" ) );
        int total = 0;
        for ( int i = 2; i <= cols; i++ )
        {
            total += sumRow[i];
            grid.addValue( sumRow[i] );
        }
        grid.addValue( total );
    }
}
