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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
import org.hisp.dhis.dataelement.DataElement;
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
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
        // Headers TODO hidden cols
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
        Collection<Integer> dataElementIds, Collection<Period> periods, String aggregateType, I18nFormat format,
        I18n i18n )
    {
        String sql = "";

        Grid grid = new ListGrid();

        // Type = 1
        if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_COLUMN_PERIOD )
        {
            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "orgunit" ), false, true ) );

            for ( Period period : periods )
            {
                grid.addHeader( new GridHeader( format.formatPeriod( period ), false, false ) );
            }

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL1( programStage, orgunitIds, dataElementIds, periods, aggregateType, format );
        }
        // Type = 2
        if ( position == PatientAggregateReport.POSITION_ROW_PERIOD_COLUMN_ORGUNIT )
        {
            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "period" ), false, true ) );

            for ( Integer orgunitId : orgunitIds )
            {
                grid.addHeader( new GridHeader( organisationUnitService.getOrganisationUnit( orgunitId )
                    .getDisplayName(), false, false ) );
            }

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL2( programStage, orgunitIds, dataElementIds, periods, aggregateType, format );
        }
        // Type = 3
        else if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_ROW_PERIOD )
        {
            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "orgunit" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( "period" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( aggregateType ), false, false ) );

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL345( position, programStage, orgunitIds, dataElementIds, periods, aggregateType,
                format );

        }
        // Type = 4
        else if ( position == PatientAggregateReport.POSITION_ROW_PERIOD )
        {
            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "period" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( aggregateType ), false, false ) );

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL345( position, programStage, orgunitIds, dataElementIds, periods, aggregateType,
                format );

        }
        // type = 5
        else if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT )
        {
            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "orgunit" ), false, true ) );
            grid.addHeader( new GridHeader( i18n.getString( aggregateType ), false, false ) );

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL345( position, programStage, orgunitIds, dataElementIds, periods, aggregateType,
                format );

        }
        // Type = 6
        else if ( position == PatientAggregateReport.POSITION_ROW_PERIOD_COLUMN_DATA )
        {
            Integer dataElementId = dataElementIds.iterator().next();
            List<String> deValues = dataElementService.getDataElement( dataElementId ).getOptionSet().getOptions();

            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "period" ), false, true ) );

            for ( String deValue : deValues )
            {
                grid.addHeader( new GridHeader( deValue, false, false ) );
            }

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL6( programStage, orgunitIds, dataElementId, deValues, periods, aggregateType,
                format );
        }
        // Type = 7
        else if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_COLUMN_DATA )
        {
            Integer dataElementId = dataElementIds.iterator().next();
            List<String> deValues = dataElementService.getDataElement( dataElementId ).getOptionSet().getOptions();

            // ---------------------------------------------------------------------
            // Headers TODO hidden cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "orgunit" ), false, true ) );

            for ( String deValue : deValues )
            {
                grid.addHeader( new GridHeader( deValue, false, false ) );
            }

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL7( programStage, orgunitIds, dataElementId, deValues, periods.iterator().next(),
                aggregateType, format );
        }
        // type = 8
        else if ( position == PatientAggregateReport.POSITION_ROW_DATA )
        {
            Integer dataElementId = dataElementIds.iterator().next();
            DataElement dataElement = dataElementService.getDataElement( dataElementId );

            // ---------------------------------------------------------------------
            // Headers cols
            // ---------------------------------------------------------------------

            grid.addHeader( new GridHeader( dataElement.getDisplayName(), false, true ) );

            grid.addHeader( new GridHeader( i18n.getString( aggregateType ), false, false ) );

            // ---------------------------------------------------------------------
            // Get SQL and build grid
            // ---------------------------------------------------------------------

            sql = getAggregateReportSQL8( programStage, orgunitIds, dataElementId, periods.iterator().next(),
                aggregateType, format );
        }
        System.out.println( "\n\n " + sql + "\n\n" );
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        GridUtils.addRows( grid, rowSet );

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
        sql += ") as tabular ";// TODO page size
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
     **/
    private String getAggregateReportSQL1( ProgramStage programStage, Collection<Integer> orgunitIds,
        Collection<Integer> dataElementIds, Collection<Period> periods, String aggregateType, I18nFormat format )
    {
        String orderBy = "";

        String sql = "select * from ( ";
        sql += "select ou.name, ";

        int index = 0;
        for ( Period period : periods )
        {
            sql += "( select " + aggregateType + "(value) ";
            sql += "FROM patientdatavalue pdv_1 ";
            sql += "    inner join programstageinstance psi_1 ";
            sql += "    on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi_1.organisationunitid=psi.organisationunitid AND ";
            sql += "    dataelementid in ( " + TextUtils.getCommaDelimitedString( dataElementIds ) + " ) AND ";
            sql += "    psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
            sql += "    psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND ";
            sql += "    psi_1.programstageid=" + programStage.getId() + " AND ";
            sql += "    psi_1.completed = true ";
            sql += ") as p_" + index + ",";

            orderBy += "p_" + index + ",";
            index++;
        }
        sql = sql.substring( 0, sql.length() - 1 ) + " ";
        orderBy = orderBy.substring( 0, orderBy.length() - 1 );

        sql += "FROM programstageinstance psi ";
        sql += "        RIGHT JOIN organisationunit ou on ou.organisationunitid=psi.organisationunitid ";
        sql += "WHERE ";
        sql += "        ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + ") ";
        sql += "GROUP BY ";
        sql += "        psi.organisationunitid,ou.name ";
        sql += "ORDER BY " + orderBy + " desc ";
        sql += " ) as aggregate";

        return sql;
    }

    /**
     * Aggregate report Position Orgunit Columns - Period Rows - Data Filter
     **/
    private String getAggregateReportSQL2( ProgramStage programStage, Collection<Integer> orgunitIds,
        Collection<Integer> dataElementIds, Collection<Period> periods, String aggregateType, I18nFormat format )
    {
        String sql = "";

        for ( Period period : periods )
        {
            sql = "(select * from ( ";
            sql += "select '" + format.formatPeriod( period ) + "', ";
            for ( Integer orgunitId : orgunitIds )
            {
                sql += "( select " + aggregateType + "(value) ";
                sql += "FROM patientdatavalue pdv ";
                sql += "inner join programstageinstance psi ";
                sql += "        on psi.programstageinstanceid = pdv.programstageinstanceid  ";
                sql += "WHERE  ";
                sql += "        psi.organisationunitid=ou.organisationunitid AND ";
                sql += "        pdv.dataelementid = 456 AND  ";
                sql += "        psi.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND  ";
                sql += "        psi.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND  ";
                sql += "        psi.programstageid=" + programStage.getId() + " AND  ";
                sql += "        psi.organisationunitid = " + orgunitId + " AND ";
                sql += "        psi.completed = true ";
                sql += ") as ou_" + orgunitId + ",";
            }

            sql = sql.substring( 0, sql.length() - 1 ) + " ";
            sql += "FROM organisationunit ou where ou.organisationunitid in ( "
                + TextUtils.getCommaDelimitedString( dataElementIds ) + " ";
            sql += ") ) as aggregate ) UNION ";
        }
        return sql.substring( 0, sql.length() - 6 ) + " ";
    }

    /**
     * Generate SQL statement for 3 report type - Aggregate report Position
     * Orgunit Rows - Period Rows - Data Filter Aggregate report Period Rows -
     * Orgunit Filter - Data Filter Aggregate report Position Orgunit Rows -
     * Period Filter - Data Filter
     * 
     **/
    private String getAggregateReportSQL345( int position, ProgramStage programStage, Collection<Integer> orgunitIds,
        Collection<Integer> dataElementIds, Collection<Period> periods, String aggregateType, I18nFormat format )
    {
        String sql = "";
        for ( Period period : periods )
        {
            sql += "( " + getColumnAggregateReportSQL345( position, format.formatPeriod( period ), aggregateType )
                + " ";
            sql += "FROM ";
            sql += "    patientdatavalue pdv RIGHT JOIN programstageinstance psi ";
            sql += "            ON psi.programstageinstanceid=pdv.programstageinstanceid ";
            sql += "    RIGHT JOIN organisationunit ou on (ou.organisationunitid=psi.organisationunitid ) ";
            sql += "WHERE ";
            sql += "        ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) AND ";
            sql += "       (( psi.programstageid=" + programStage.getId() + " AND ";
            sql += "       psi.completed = true AND ";
            sql += "       psi.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND  ";
            sql += "       psi.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND  ";
            sql += "       dataelementid in ( " + TextUtils.getCommaDelimitedString( dataElementIds ) + " )  ) ";
            sql += "    OR ( ";
            sql += "      psi.programstageid is null ";
            sql += "      AND psi.completed is null ";
            sql += "       AND psi.executiondate is NULL ";
            sql += "      AND dataelementid is null ";
            sql += "      AND psi.completed is null ) ) ";

            if ( position == PatientAggregateReport.POSITION_ROW_ORGUNIT_ROW_PERIOD
                || position == PatientAggregateReport.POSITION_ROW_ORGUNIT )
            {
                sql += "GROUP BY ";
                sql += "    ou.name ";
                sql += "ORDER BY ou.name desc ";
            }
            sql += ") UNION ";
        }

        return sql.substring( 0, sql.length() - 6 ) + " ";
    }

    /**
     * Generate SELECT statement for 3 report type - Aggregate report Position
     * Orgunit Rows - Period Rows - Data Filter - Aggregate report Period Rows
     * Orgunit Filter - Data Filter - Aggregate report Position Orgunit Rows -
     * Period Filter - Data Filter
     * 
     **/
    private String getColumnAggregateReportSQL345( int position, String periodColumnName, String aggregateType )
    {
        switch ( position )
        {
        case PatientAggregateReport.POSITION_ROW_ORGUNIT_ROW_PERIOD:
            return "select ou.name, '" + periodColumnName + "', " + aggregateType + "(pdv.value) ";
        case PatientAggregateReport.POSITION_ROW_PERIOD:
            return "select '" + periodColumnName + "', count(pdv.value) ";
        case PatientAggregateReport.POSITION_ROW_ORGUNIT:
            return "select  ou.name, count(pdv.value) ";
        default:
            return "";
        }
    }

    /**
     * Aggregate report Position Orgunit Filter - Period Rows - Data Columns
     * 
     **/
    private String getAggregateReportSQL6( ProgramStage programStage, Collection<Integer> orgunitIds,
        Integer dataElementId, Collection<String> deValues, Collection<Period> periods, String aggregateType,
        I18nFormat format )
    {
        String sql = "";

        for ( Period period : periods )
        {
            sql = "( SELECT * from ( ";
            sql += "SELECT '" + format.formatPeriod( period ) + "',";

            for ( String deValue : deValues )
            {
                sql += "(SELECT count(value) ";
                sql += "FROM patientdatavalue pdv_1 ";
                sql += "        inner join programstageinstance psi_1 ";
                sql += "               on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
                sql += "WHERE ";
                sql += "        psi_1.organisationunitid=ou.organisationunitid AND ";
                sql += "        psi_1.programstageid=" + programStage.getId() + " AND ";
                sql += "        psi_1.completed = true AND ";
                sql += "        psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
                sql += "        psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND ";
                sql += "        pdv_1.dataelementid=" + dataElementId + " AND ";
                sql += "        pdv_1.value='" + deValue + "' ";
                sql += ") as de_value1,";

            }
            sql = sql.substring( 0, sql.length() - 1 ) + " ";
            sql += "FROM organisationunit ou ";
            sql += "WHERE  ";
            sql += "ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + " ) ";
            sql += "  ) as aggregate ";

            sql += ") UNION ";
        }

        return sql.substring( 0, sql.length() - 6 ) + " ";
    }

    /**
     * Aggregate report Position Orgunit Rows - Period Filter - Data Columns
     * 
     **/
    private String getAggregateReportSQL7( ProgramStage programStage, Collection<Integer> orgunitIds,
        Integer dataElementId, List<String> deValues, Period period, String aggregateType, I18nFormat format )
    {
        String orderBy = "";

        String sql = "select * from ( ";
        sql += "select ou.name, ";

        int index = 0;
        for ( String deValue : deValues )
        {
            sql += "( select " + aggregateType + "(value) ";
            sql += "FROM patientdatavalue pdv_1 ";
            sql += "    inner join programstageinstance psi_1 ";
            sql += "    on psi_1.programstageinstanceid = pdv_1.programstageinstanceid ";
            sql += "WHERE ";
            sql += "    psi_1.organisationunitid=psi.organisationunitid AND ";
            sql += "    pdv_1.dataelementid =  " + dataElementId + " AND ";
            sql += "    psi_1.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
            sql += "    psi_1.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND ";
            sql += "    psi_1.programstageid=" + programStage.getId() + " AND ";
            sql += "    psi_1.completed = true AND ";
            sql += "    pdv_1.value='" + deValue + "' ";
            sql += ") as v_" + index + ",";

            orderBy += "v_" + index + ",";
            index++;
        }
        sql = sql.substring( 0, sql.length() - 1 ) + " ";
        orderBy = orderBy.substring( 0, orderBy.length() - 1 );

        sql += "FROM programstageinstance psi ";
        sql += "        RIGHT JOIN organisationunit ou on ou.organisationunitid=psi.organisationunitid ";
        sql += "WHERE ";
        sql += "        ou.organisationunitid in ( " + TextUtils.getCommaDelimitedString( orgunitIds ) + ") ";
        sql += "GROUP BY ";
        sql += "        psi.organisationunitid,ou.name ";
        sql += "ORDER BY " + orderBy + " desc ";
        sql += " ) as aggregate";

        return sql;
    }

    /**
     * Aggregate report Position Data Rows
     * 
     **/
    private String getAggregateReportSQL8( ProgramStage programStage, Collection<Integer> orgunitIds,
        Integer dataElementId, Period period, String aggregateType, I18nFormat format )
    {
        String sql = "SELECT  ov.optionvalue, " + aggregateType + "(value) ";
        sql += "FROM ( dataelement de ";
        sql += "        JOIN optionset opt ";
        sql += "                ON opt.optionsetid = de.optionsetid ";
        sql += "        JOIN optionsetmembers ov  ";
        sql += "                ON opt.optionsetid = ov.optionsetid ) ";
        sql += "        LEFT JOIN patientdatavalue pdv ";
        sql += "                ON de.dataelementid=pdv.dataelementid ";
        sql += "        JOIN programstageinstance psi ";
        sql += "                 ON psi.programstageinstanceid = pdv.programstageinstanceid ";
        sql += "WHERE ";
        sql += "        (( de.dataelementid=" + dataElementId + " AND ";
        sql += "        psi.programstageid=" + programStage.getId() + " AND ";
        sql += "        psi.completed = true  AND ";
        sql += "        psi.executiondate >= '" + format.formatDate( period.getStartDate() ) + "' AND ";
        sql += "        psi.executiondate <= '" + format.formatDate( period.getEndDate() ) + "' AND ";
        sql += "        psi.organisationunitid in( " + orgunitIds + ") ) ";
        sql += "        OR ";
        sql += "        (psi.programstageid is null AND ";
        sql += "        psi.completed is null AND ";
        sql += "        psi.executiondate is null AND  ";
        sql += "        organisationunitid is null ) ) ";
        sql += "GROUP BY ov.optionvalue ";
        sql += "ORDER BY count(value) desc ";

        return sql;
    }
}
