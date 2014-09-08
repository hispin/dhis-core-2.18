package org.hisp.dhis.caseaggregation.hibernate;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_ORGUNIT_COMPLETE_PROGRAM_STAGE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_PROPERTY;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE_DATAELEMENT;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_PROGRAM_STAGE_PROPERTY;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_TRACKED_ENTITY_ATTRIBUTE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.OBJECT_TRACKED_ENTITY_PROGRAM_STAGE_PROPERTY;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.PARAM_PERIOD_END_DATE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.PARAM_PERIOD_ID;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.PARAM_PERIOD_ISO_DATE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.PARAM_PERIOD_START_DATE;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_ID;
import static org.hisp.dhis.caseaggregation.CaseAggregationCondition.SEPARATOR_OBJECT;
import static org.hisp.dhis.scheduling.CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_LAST_12_MONTH;
import static org.hisp.dhis.scheduling.CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_LAST_3_MONTH;
import static org.hisp.dhis.scheduling.CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_LAST_6_MONTH;
import static org.hisp.dhis.scheduling.CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_LAST_MONTH;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.caseaggregation.CaseAggregateSchedule;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionStore;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Chau Thu Tran
 * 
 * @version HibernateCaseAggregationConditionStore.java Nov 18, 2010 9:36:20 AM
 */
public class HibernateCaseAggregationConditionStore
    extends HibernateIdentifiableObjectStore<CaseAggregationCondition>
    implements CaseAggregationConditionStore
{
    private static final String IS_NULL = "is null";

    private static final String IN_CONDITION_GET_ALL = "*";

    private static final String IN_CONDITION_START_SIGN = "@";

    private static final String IN_CONDITION_END_SIGN = "#";

    private static final String IN_CONDITION_COUNT_X_TIMES = "COUNT";

    // -------------------------------------------------------------------------
    // Dependencies
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

    @Autowired
    DataValueService dataValueService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private OrganisationUnitService orgunitService;
    
    // -------------------------------------------------------------------------
    // Implementation Methods
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<CaseAggregationCondition> get( DataElement dataElement )
    {
        return getCriteria( Restrictions.eq( "aggregationDataElement", dataElement ) ).list();
    }

    @Override
    public int count( Collection<DataElement> dataElements, String key )
    {
        Criteria criteria = getCriteria();

        if ( dataElements != null )
        {
            criteria.add( Restrictions.in( "aggregationDataElement", dataElements ) );
        }
        if ( key != null )
        {
            criteria.add( Restrictions.ilike( "name", "%" + key + "%" ) );
        }

        Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();
        return rs != null ? rs.intValue() : 0;
    }

    @Override
    public CaseAggregationCondition get( DataElement dataElement, DataElementCategoryOptionCombo optionCombo )
    {
        return (CaseAggregationCondition) getCriteria( Restrictions.eq( "aggregationDataElement", dataElement ),
            Restrictions.eq( "optionCombo", optionCombo ) ).uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<CaseAggregationCondition> get( Collection<DataElement> dataElements, String key, Integer first,
        Integer max )
    {
        Criteria criteria = getCriteria();

        if ( dataElements != null )
        {
            criteria.add( Restrictions.in( "aggregationDataElement", dataElements ) );
        }
        if ( key != null )
        {
            criteria.add( Restrictions.ilike( "name", "%" + key + "%" ) );
        }

        if ( first != null && max != null )
        {
            criteria.setFirstResult( first );
            criteria.setMaxResults( max );
        }

        criteria.addOrder( Order.desc( "name" ) );

        return criteria.list();
    }

    public Grid getAggregateValue( String sql, I18nFormat format, I18n i18n )
    {
        Grid grid = new ListGrid();

        grid.addHeader( new GridHeader( i18n.getString( "dataelementid" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "categoryoptioncomboid" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "periodid" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "organisationunitid" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "storedby" ), true, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "dataelementname" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "categoryoptioncomboname" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "organisationunitname" ), false, true ) );
        grid.addHeader( new GridHeader( i18n.getString( "value" ), false, true ) );

        SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );
        grid.addRows( rs );

        return grid;
    }

    @Override
    public Grid getAggregateValueDetails( CaseAggregationCondition aggregationCondition, OrganisationUnit orgunit,
        Period period, boolean nonRegistrationProgram, I18nFormat format, I18n i18n )
    {
        Grid grid = new ListGrid();
        grid.setTitle( orgunit.getName() + " - " + aggregationCondition.getDisplayName() );
        grid.setSubtitle( format.formatPeriod( period ) );

        String sql = parseExpressionDetailsToSql( aggregationCondition.getAggregationExpression(),
            aggregationCondition.getOperator(), orgunit.getId(), period, nonRegistrationProgram );

        SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );

        for ( String colName : rs.getMetaData().getColumnNames() )
        {
            grid.addHeader( new GridHeader( i18n.getString( colName ), false, true ) );
        }

        addRows( rs, grid );

        return grid;
    }

    public void addRows( SqlRowSet rs, Grid grid )
    {
        int cols = rs.getMetaData().getColumnCount();
        String idValue = "";
        int index = 1;
        while ( rs.next() )
        {
            grid.addRow();
            for ( int i = 1; i <= cols; i++ )
            {
                Object value = rs.getObject( i );
                if ( i == 1 )
                {
                    if ( !value.toString().equals( idValue ) )
                    {
                        grid.addValue( index );
                        idValue = value.toString();
                        index++;
                    }
                    else
                    {
                        grid.addValue( "" );
                    }
                }
                else
                {
                    grid.addValue( value );
                }
            }
        }
    }

    @Override
    public void insertAggregateValue( String sql, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, 
        DataElementCategoryOptionCombo attributeOptionCombo, Collection<Integer> orgunitIds, Period period )
    {        
        try
        {
            SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
            while (row.next())
            {
                int value = row.getInt("value");
                OrganisationUnit source = orgunitService.getOrganisationUnit( row.getInt( "sourceid" ) );
                
                DataValue dataValue = dataValueService.getDataValue( dataElement, period, source, optionCombo );
                
                if ( dataValue == null && value != 0 )
                {
                    dataValue = new DataValue( dataElement, period, source, optionCombo, attributeOptionCombo );
                    dataValue.setValue( value + "" );
                    dataValue.setStoredBy( row.getString("storedby") );
                    dataValueService.addDataValue( dataValue );
                }
                else if ( dataValue != null && value == 0 )
                {
                    dataValueService.deleteDataValue( dataValue );
                }
                else if ( dataValue != null && value != 0 )
                {
                    dataValue.setValue( value + "" );
                    dataValueService.updateDataValue( dataValue );
                } 
            }
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void insertAggregateValue( String sql, int dataElementId, int optionComboId, Collection<Integer> orgunitIds,
        Period period )
    {
        try
        {
            int periodId = 0;
            periodId = jdbcTemplate.queryForInt(  "select periodid from period where periodtypeid = ( select periodtypeid from periodtype where name='"
                    + period.getPeriodType().getName()
                    + "' )"
                    + " and startdate='"
                    + DateUtils.getMediumDateString( period.getStartDate() )
                    + "' and enddate='"
                    + DateUtils.getMediumDateString( period.getEndDate() ) + "'" );
            
            if ( periodId == 0 )
            {
                String insertSql = "insert into period (periodid, periodtypeid,startdate,enddate) " + " VALUES " + "("
                    + statementBuilder.getAutoIncrementValue() + ","+ period.getPeriodType().getId() + ",'" + DateUtils.getMediumDateString( period.getStartDate() )
                    + "','" + DateUtils.getMediumDateString( period.getEndDate() ) + "' )";
                
               jdbcTemplate.update( insertSql );
            }
            else
            {
                String deleteDataValueSql = "delete from datavalue where dataelementid=" + dataElementId
                    + " and categoryoptioncomboid=" + optionComboId + " and sourceid in ("
                    + TextUtils.getCommaDelimitedString( orgunitIds ) + ") "
                        + "and periodid = " + periodId;
               jdbcTemplate.update( deleteDataValueSql );
            }

            jdbcTemplate.update( sql );

        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }

    @Override
    public String parseExpressionToSql( boolean isInsert, CaseAggregationCondition aggregationCondition,
        int attributeOptionComboId, Collection<Integer> orgunitIds )
    {
        String caseExpression = aggregationCondition.getAggregationExpression();
        String operator = aggregationCondition.getOperator();
        Integer aggregateDeId = aggregationCondition.getAggregationDataElement().getId();
        String aggregateDeName = aggregationCondition.getAggregationDataElement().getName();
        Integer optionComboId = aggregationCondition.getOptionCombo().getId();
        String optionComboName = aggregationCondition.getOptionCombo().getName();
        Integer deSumId = (aggregationCondition.getDeSum() == null) ? null : aggregationCondition.getDeSum().getId();

        return parseExpressionToSql( isInsert, caseExpression, operator, aggregateDeId, aggregateDeName, optionComboId,
            optionComboName, attributeOptionComboId, deSumId, orgunitIds );
    }

    @Override
    public String parseExpressionToSql( boolean isInsert, String caseExpression, String operator, Integer aggregateDeId,
        String aggregateDeName, Integer optionComboId, String optionComboName,  int attributeOptioncomboId, Integer deSumId,
        Collection<Integer> orgunitIds )
    {
        String sql = "SELECT '" + aggregateDeId + "' as dataelementid, '" + optionComboId
            + "' as categoryoptioncomboid, '" + attributeOptioncomboId
            + "' as attributeoptioncomboid, ou.organisationunitid as sourceid, '" + PARAM_PERIOD_ID + "' as periodid,'"
            + CaseAggregationCondition.AUTO_STORED_BY + "' as storedby, ";

        if ( isInsert )
        {
            sql = "INSERT INTO datavalue (dataelementid, categoryoptioncomboid, attributeoptioncomboid, sourceid, periodid, storedby, lastupdated, followup, created, value) "
                + sql + " now(), false, now(), ";
        }
        else
        {
            sql += "'" + PARAM_PERIOD_ISO_DATE + "' as periodIsoDate,'" + aggregateDeName + "' as dataelementname, '"
                + optionComboName + "' as categoryoptioncomboname, ou.name as organisationunitname, ";
        }

        if ( operator.equals( CaseAggregationCondition.AGGRERATION_COUNT )
            || operator.equals( CaseAggregationCondition.AGGRERATION_SUM ) )
        {
            if ( hasOrgunitProgramStageCompleted( caseExpression ) )
            {
                sql += createSQL( caseExpression, operator, orgunitIds );
            }
            else
            {
                if ( operator.equals( CaseAggregationCondition.AGGRERATION_COUNT ) )
                {
                    sql += " count(distinct(pi.trackedentityinstanceid)) as value ";
                }
                else
                {
                    sql += " count(psi.programinstanceid) as value ";
                }

                sql += "FROM ";
                boolean hasDataelement = hasDataelementCriteria( caseExpression );
                boolean hasEntityInstance = hasEntityInstanceCriteria( caseExpression );

                if ( hasEntityInstance && hasDataelement )
                {
                    sql += " programinstance as pi ";
                    sql += " INNER JOIN trackedentityinstance p on p.trackedentityinstanceid=pi.trackedentityinstanceid ";
                    sql += " INNER JOIN programstageinstance psi ON pi.programinstanceid=psi.programinstanceid ";
                    sql += " INNER JOIN organisationunit ou ON ou.organisationunitid=psi.organisationunitid ";
                }
                else if ( hasEntityInstance )
                {
                    sql += " programinstance as pi INNER JOIN trackedentityinstance p on p.trackedentityinstanceid=pi.trackedentityinstanceid ";
                    sql += " INNER JOIN organisationunit ou ON ou.organisationunitid=p.organisationunitid ";
                }
                else
                {
                    sql += " programinstance as pi ";
                    sql += " INNER JOIN programstageinstance psi ON pi.programinstanceid=psi.programinstanceid ";
                    sql += " INNER JOIN organisationunit ou ON ou.organisationunitid=psi.organisationunitid ";
                }

                sql += " WHERE " + createSQL( caseExpression, operator, orgunitIds );

                sql += "GROUP BY ou.organisationunitid, ou.name";
            }
        }
        else
        {
            sql += " " + operator + "( cast( pdv.value as " + statementBuilder.getDoubleColumnType() + " ) ) ";
            sql += "FROM trackedentitydatavalue pdv ";
            sql += "    INNER JOIN programstageinstance psi  ";
            sql += "            ON psi.programstageinstanceid = pdv.programstageinstanceid ";
            sql += "    INNER JOIN organisationunit ou ";
            sql += "            ON ou.organisationunitid=psi.organisationunitid ";
            sql += "WHERE executiondate >='" + PARAM_PERIOD_START_DATE + "'  ";
            sql += "    AND executiondate <='" + PARAM_PERIOD_END_DATE + "' AND pdv.dataelementid=" + deSumId;

            if ( caseExpression != null && !caseExpression.isEmpty() )
            {
                sql += " AND " + createSQL( caseExpression, operator, orgunitIds );
            }

            sql += "GROUP BY ou.organisationunitid, ou.name";
            
        }

        return sql;
    }

    @Override
    public void runAggregate( Collection<Integer> orgunitIds, CaseAggregateSchedule dataSet, Collection<Period> periods, int attributeOptioncomboId )
    {
        Collection<Integer> _orgunitIds = getServiceOrgunit();

        if ( orgunitIds == null )
        {
            orgunitIds = new HashSet<>();
            orgunitIds.addAll( _orgunitIds );
        }
        else
        {
            orgunitIds.retainAll( _orgunitIds );
        }
        
        if(orgunitIds.size() > 0 )
        {
            String sql = "select caseaggregationconditionid, aggregationdataelementid, optioncomboid, "
                + " cagg.aggregationexpression as caseexpression, cagg.operator as caseoperator, cagg.desum as desumid "
                + "     from caseaggregationcondition cagg inner join datasetmembers dm "
                + "             on cagg.aggregationdataelementid=dm.dataelementid inner join dataset ds "
                + "             on ds.datasetid = dm.datasetid inner join periodtype pt "
                + "             on pt.periodtypeid=ds.periodtypeid inner join dataelement de "
                + "             on de.dataelementid=dm.dataelementid where ds.datasetid = " + dataSet.getDataSetId();
    
            SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );
    
             while ( rs.next() )
            {
                 String caseExpression = rs.getString( "caseexpression" );
                 int dataElementId = rs.getInt( "aggregationdataelementid" );
                 int optionComboId = rs.getInt( "optioncomboid" );
                 String caseOperator = rs.getString( "caseoperator" );
                 int deSumId = rs.getInt( "desumid" );
                 String insertParamsSql = parseExpressionToSql( true, caseExpression, caseOperator, dataElementId, "de_name", optionComboId, "optioncombo_name", attributeOptioncomboId, deSumId, _orgunitIds );
                 
                for ( Period period : periods )
                {
                    String insertSql = replacePeriodSql( insertParamsSql, period );
                    
                    if ( !orgunitIds.isEmpty() )
                    { 
                        insertAggregateValue( insertSql, dataElementId, optionComboId, _orgunitIds, period );
                    }
                }
    
            }
        }
    }

    /**
     * Return standard SQL from query builder formula
     * 
     * @param caseExpression The query builder expression
     * @param operator There are six operators, includes Number of persons,
     *        Number of visits, Sum, Average, Minimum and Maximum of data
     *        element values.
     * @param deType Aggregate Data element type
     * @param orgunitIds The ids of organisation units where to aggregate data
     *        value
     */
    private String createSQL( String caseExpression, String operator, Collection<Integer> orgunitIds )
    {
        caseExpression = caseExpression.replaceAll( "\"", "'" );
        boolean orgunitCompletedProgramStage = false;

        StringBuffer sqlResult = new StringBuffer();

        String sqlOrgunitCompleted = "";

        // Get minus(DATE dataelement, DATE dataelement) out from the expression
        // and run them later

        Map<Integer, String> minus2SQLMap = new HashMap<>();
        int idx2 = 0;
        Pattern patternMinus2 = Pattern.compile( CaseAggregationCondition.minusDataelementRegExp );
        Matcher matcherMinus2 = patternMinus2.matcher( caseExpression );
        while ( matcherMinus2.find() )
        {
            String[] ids1 = matcherMinus2.group( 2 ).split( SEPARATOR_ID );
            String[] ids2 = matcherMinus2.group( 5 ).split( SEPARATOR_ID );

            minus2SQLMap.put(
                idx2,
                getConditionForMisus2DataElement( orgunitIds, ids1[1], ids1[2], ids2[1], ids2[2],
                    matcherMinus2.group( 6 ) + matcherMinus2.group( 7 ) ) );

            caseExpression = caseExpression.replace( matcherMinus2.group( 0 ),
                CaseAggregationCondition.MINUS_DATAELEMENT_OPERATOR + "_" + idx2 );

            idx2++;
        }

        // Get minus(DATE attribute, DATE attribute) out from the expression
        // and run them later

        Map<Integer, String> minus2AttributeSQLMap = new HashMap<>();
        int idx1 = 0;
        Pattern patternAttrMinus2 = Pattern.compile( CaseAggregationCondition.minusAttributeRegExp );
        Matcher matcherAttrMinus2 = patternAttrMinus2.matcher( caseExpression );
        while ( matcherAttrMinus2.find() )
        {
            String attribute1 = matcherAttrMinus2.group( 2 );
            String attribute2 = matcherAttrMinus2.group( 5 );
            String compareSide = matcherAttrMinus2.group( 6 ) + matcherAttrMinus2.group( 7 );
            minus2AttributeSQLMap.put( idx1, getConditionForMisus2Attribute( attribute1, attribute2, compareSide ) );

            caseExpression = caseExpression.replace( matcherAttrMinus2.group( 0 ),
                CaseAggregationCondition.MINUS_ATTRIBUTE_OPERATOR + "_" + idx1 );

            idx1++;
        }

        // Get minus(date dataelement, date) out from the expression and run
        // them later

        Map<Integer, String> minusSQLMap = new HashMap<>();
        int idx = 0;
        Pattern patternMinus = Pattern.compile( CaseAggregationCondition.dataelementRegExp );
        Matcher matcherMinus = patternMinus.matcher( caseExpression );
        while ( matcherMinus.find() )
        {
            String[] ids = matcherMinus.group( 2 ).split( SEPARATOR_ID );

            minusSQLMap.put(
                idx,
                getConditionForMinusDataElement( orgunitIds, Integer.parseInt( ids[1] ), Integer.parseInt( ids[2] ),
                    matcherMinus.group( 4 ) ) );

            caseExpression = caseExpression.replace( matcherMinus.group( 0 ), CaseAggregationCondition.MINUS_OPERATOR
                + "_" + idx );

            idx++;
        }

        // Run nornal expression
        String[] expression = caseExpression.split( "(AND|OR)" );
        caseExpression = caseExpression.replaceAll( "AND", " ) AND " );
        caseExpression = caseExpression.replaceAll( "OR", " ) OR " );

        // ---------------------------------------------------------------------
        // parse expressions
        // ---------------------------------------------------------------------

        Pattern patternCondition = Pattern.compile( CaseAggregationCondition.regExp );

        Matcher matcherCondition = patternCondition.matcher( caseExpression );

        String condition = "";

        int index = 0;
        while ( matcherCondition.find() )
        {
            String match = matcherCondition.group();

            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );
            if ( info[0].equalsIgnoreCase( OBJECT_TRACKED_ENTITY_ATTRIBUTE ) )
            {
                String attributeId = info[1];

                String compareValue = expression[index].replace( "[" + match + "]", "" ).trim();

                boolean isExist = compareValue.equals( IS_NULL ) ? false : true;
                condition = getConditionForTrackedEntityAttribute( attributeId, orgunitIds, isExist );
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_STAGE_DATAELEMENT ) )
            {
                String[] ids = info[1].split( SEPARATOR_ID );

                int programId = Integer.parseInt( ids[0] );
                String programStageId = ids[1];
                int dataElementId = Integer.parseInt( ids[2] );

                String compareValue = expression[index].replace( "[" + match + "]", "" ).trim();

                boolean isExist = compareValue.equals( IS_NULL ) ? false : true;
                condition = getConditionForDataElement( isExist, programId, programStageId, dataElementId, orgunitIds );
            }

            else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_PROPERTY ) )
            {
                condition = getConditionForProgramProperty( operator, info[1] );
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM ) )
            {
                String[] ids = info[1].split( SEPARATOR_ID );
                condition = getConditionForProgram( ids[0], operator, orgunitIds );
                if ( ids.length > 1 )
                {
                    condition += ids[1];
                }
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_STAGE ) )
            {
                String[] ids = info[1].split( SEPARATOR_ID );
                if ( ids.length == 2 && ids[1].equals( IN_CONDITION_COUNT_X_TIMES ) )
                {
                    condition = getConditionForCountProgramStage( ids[0], operator, orgunitIds );
                }
                else
                {
                    condition = getConditionForProgramStage( ids[0], orgunitIds );
                }
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_PROGRAM_STAGE_PROPERTY ) )
            {
                condition = getConditionForProgramStageProperty( info[1], operator, orgunitIds );
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_TRACKED_ENTITY_PROGRAM_STAGE_PROPERTY ) )
            {
                condition = getConditionForTrackedEntityProgramStageProperty( info[1], operator );
            }
            else if ( info[0].equalsIgnoreCase( OBJECT_ORGUNIT_COMPLETE_PROGRAM_STAGE ) )
            {
                sqlOrgunitCompleted += getConditionForOrgunitProgramStageCompleted( info[1], operator, orgunitIds,
                    orgunitCompletedProgramStage );
                orgunitCompletedProgramStage = true;
            }

            matcherCondition.appendReplacement( sqlResult, condition );

            index++;
        }

        matcherCondition.appendTail( sqlResult );

        if ( !sqlOrgunitCompleted.isEmpty() )
        {
            sqlOrgunitCompleted = sqlOrgunitCompleted.substring( 0, sqlOrgunitCompleted.length() - 2 );
        }

        sqlResult.append( sqlOrgunitCompleted );

        String sql = sqlResult.toString();

        sql = sql.replaceAll( IN_CONDITION_START_SIGN, "(" );
        sql = sql.replaceAll( IN_CONDITION_END_SIGN, ")" );
        sql = sql.replaceAll( IS_NULL, " " );

        for ( int key = 0; key < idx; key++ )
        {
            sql = sql.replace( CaseAggregationCondition.MINUS_OPERATOR + "_" + key, minusSQLMap.get( key ) );
        }

        for ( int key = 0; key < idx2; key++ )
        {
            sql = sql
                .replace( CaseAggregationCondition.MINUS_DATAELEMENT_OPERATOR + "_" + key, minus2SQLMap.get( key ) );
        }

        for ( int key = 0; key < idx1; key++ )
        {
            sql = sql.replace( CaseAggregationCondition.MINUS_ATTRIBUTE_OPERATOR + "_" + key,
                minus2AttributeSQLMap.get( key ) );
        }

        return sql + " ) ";
    }

    /**
     * Return standard SQL of the expression to compare data value as null
     * 
     */
    private String getConditionForDataElement( boolean isExist, int programId, String programStageId,
        int dataElementId, Collection<Integer> orgunitIds )
    {
        String keyExist = (isExist == true) ? "EXISTS" : "NOT EXISTS";

        String sql = " " + keyExist + " ( SELECT * "
            + "FROM trackedentitydatavalue _pdv inner join programstageinstance _psi "
            + "ON _pdv.programstageinstanceid=_psi.programstageinstanceid JOIN programinstance _pi "
            + "ON _pi.programinstanceid=_psi.programinstanceid "
            + "WHERE psi.programstageinstanceid=_pdv.programstageinstanceid AND _pdv.dataelementid=" + dataElementId
            + "  AND _psi.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgunitIds ) + ")  "
            + "  AND _pi.programid = " + programId + " AND _psi.executionDate>='" + PARAM_PERIOD_START_DATE
            + "' AND _psi.executionDate <= '" + PARAM_PERIOD_END_DATE + "' ";

        if ( !programStageId.equals( IN_CONDITION_GET_ALL ) )
        {
            sql += " AND _psi.programstageid = " + programStageId;
        }

        if ( isExist )
        {
            DataElement dataElement = dataElementService.getDataElement( dataElementId );
            if ( dataElement.getType().equals( DataElement.VALUE_TYPE_INT ) )
            {
                sql += " AND ( cast( _pdv.value as " + statementBuilder.getDoubleColumnType() + " )  ) ";
            }
            else
            {
                sql += " AND _pdv.value ";
            }
        }

        if ( !isExist )
        {
            sql = "(" + sql + " ) AND " + getConditionForProgramStage( programStageId, orgunitIds ) + ")";
        }

        return sql;
    }

    /**
     * Return standard SQL of a dynamic tracked-entity-attribute expression. E.g
     * [CA:1] OR [CA:1.age]
     * 
     */
    private String getConditionForTrackedEntityAttribute( String attributeId, Collection<Integer> orgunitIds,
        boolean isExist )
    {
        String sql = "  SELECT * FROM trackedentityattributevalue _pav ";

        if ( attributeId.split( SEPARATOR_ID ).length == 2 )
        {
            if ( attributeId.split( SEPARATOR_ID )[1].equals( CaseAggregationCondition.FORMULA_VISIT ) )
            {
                sql += " inner join programinstance _pi on _pav.trackedentityinstanceid=_pi.trackedentityinstanceid ";
                sql += " inner join programstageinstance _psi on _pi.programinstanceid=_psi.programinstanceid ";

                attributeId = attributeId.split( SEPARATOR_ID )[0];
                sql += " WHERE _pav.trackedentityinstanceid=pi.trackedentityinstanceid AND _pav.trackedentityattributeid="
                    + attributeId + " AND DATE(_psi.executiondate) - DATE( _pav.value ) ";
            }
            else if ( attributeId.split( SEPARATOR_ID )[1].equals( CaseAggregationCondition.FORMULA_AGE ) )
            {
                sql += " inner join programinstance _pi on _pav.trackedentityinstanceid=_pi.trackedentityinstanceid ";

                attributeId = attributeId.split( SEPARATOR_ID )[0];
                sql += " WHERE _pav.trackedentityinstanceid=pi.trackedentityinstanceid AND _pav.trackedentityattributeid="
                    + attributeId + " AND DATE(_psi.enrollmentdate) - DATE( _pav.value ) ";
            }
        }
        else
        {
            sql += " WHERE _pav.trackedentityinstanceid=pi.trackedentityinstanceid AND _pav.trackedentityattributeid="
                + attributeId;

            if ( isExist )
            {
                sql += " AND _pav.value ";
            }
        }

        if ( isExist )
        {
            sql = " EXISTS ( " + sql;
        }
        else
        {
            sql = " NOT EXISTS ( " + sql;
        }

        return sql;
    }

    /**
     * Return standard SQL of the program-property expression. E.g
     * [PC:executionDate]
     * 
     */
    private String getConditionForTrackedEntityProgramStageProperty( String propertyName, String operator )
    {
        String sql = " EXISTS ( SELECT _psi.programstageinstanceid from programstageinstance _psi "
            + "WHERE _psi.programstageinstanceid=psi.programstageinstanceid AND ( _psi.executionDate BETWEEN '"
            + PARAM_PERIOD_START_DATE + "' AND '" + PARAM_PERIOD_END_DATE + "') AND " + propertyName;

        return sql;
    }

    /**
     * Return standard SQL of the program expression. E.g
     * [PP:DATE@enrollmentdate#-DATE@dateofincident#] for geting the number of
     * days between date of enrollment and date of incident.
     * 
     */
    private String getConditionForProgramProperty( String operator, String property )
    {
        String sql = " EXISTS ( SELECT _pi.programinstanceid FROM programinstance as _pi WHERE _pi.programinstanceid=pi.programinstanceid AND "
            + "pi.enrollmentdate >= '"
            + PARAM_PERIOD_START_DATE
            + "' AND pi.enrollmentdate <= '"
            + PARAM_PERIOD_END_DATE + "' AND " + property + " ";

        return sql;
    }

    /**
     * Return standard SQL to retrieve the number of persons enrolled into the
     * program. E.g [PG:1]
     * 
     */
    private String getConditionForProgram( String programId, String operator, Collection<Integer> orgunitIds )
    {
        String sql = " EXISTS ( SELECT * FROM programinstance as _pi inner join trackedentityinstance _p on _p.trackedentityinstanceid=_pi.trackedentityinstanceid "
            + "WHERE _pi.trackedentityinstanceid=pi.trackedentityinstanceid AND _pi.programid="
            + programId
            + " AND _p.organisationunitid in ("
            + TextUtils.getCommaDelimitedString( orgunitIds )
            + ") AND _pi.enrollmentdate >= '"
            + PARAM_PERIOD_START_DATE
            + "' AND _pi.enrollmentdate <= '"
            + PARAM_PERIOD_END_DATE + "' ";

        return sql;
    }

    /**
     * Return standard SQL to retrieve the number of visits a program-stage. E.g
     * [PS:1]
     * 
     */
    private String getConditionForProgramStage( String programStageId, Collection<Integer> orgunitIds )
    {
        String sql = " EXISTS ( SELECT _psi.programstageinstanceid FROM programstageinstance _psi "
            + "WHERE _psi.programstageinstanceid=psi.programstageinstanceid " + "AND _psi.programstageid="
            + programStageId + "  AND _psi.executiondate >= '" + PARAM_PERIOD_START_DATE
            + "' AND _psi.executiondate <= '" + PARAM_PERIOD_END_DATE + "' AND _psi.organisationunitid in ("
            + TextUtils.getCommaDelimitedString( orgunitIds ) + ")  ";

        return sql;
    }

    /**
     * Return standard SQL to retrieve the x-time of a person visited one
     * program-stage. E.g a mother came to a hospital 3th time for third
     * trimester.
     * 
     */
    private String getConditionForCountProgramStage( String programStageId, String operator,
        Collection<Integer> orgunitIds )
    {
        String sql = " EXISTS ( SELECT _psi.programstageinstanceid FROM programstageinstance as _psi "
            + "WHERE psi.programstageinstanceid=_psi.programstageinstanceid AND _psi.organisationunitid in ("
            + TextUtils.getCommaDelimitedString( orgunitIds ) + ") and _psi.programstageid = " + programStageId + " "
            + "AND _psi.executionDate >= '" + PARAM_PERIOD_START_DATE + "' AND _psi.executionDate <= '"
            + PARAM_PERIOD_END_DATE + "' " + "GROUP BY _psi.programinstanceid,_psi.programstageinstanceid "
            + "HAVING count(_psi.programstageinstanceid) ";

        return sql;

    }

    /**
     * Return standard SQL to retrieve the number of days between report-date
     * and due-date. E.g [PSP:DATE@executionDate#-DATE@dueDate#]
     * 
     */
    private String getConditionForProgramStageProperty( String property, String operator, Collection<Integer> orgunitIds )
    {
        String sql = " EXISTS ( SELECT * FROM programstageinstance _psi "
            + "WHERE psi.programstageinstanceid=_psi.programstageinstanceid AND _psi.executiondate >= '"
            + PARAM_PERIOD_START_DATE + "' AND _psi.executiondate <= '" + PARAM_PERIOD_END_DATE
            + "' AND _psi.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgunitIds ) + ") AND "
            + property + " ";

        return sql;
    }

    /**
     * Return standard SQL to retrieve the number of children orgunits has all
     * program-stage-instance completed and due-date. E.g [PSIC:1]
     * 
     * @flag True if there are many stages in the expression
     * 
     */
    private String getConditionForOrgunitProgramStageCompleted( String programStageId, String operator,
        Collection<Integer> orgunitIds, boolean flag )
    {
        String sql = "";
        if ( !flag )
        {
            sql = " '1' FROM organisationunit ou WHERE ou.organisationunitid in ("
                + TextUtils.getCommaDelimitedString( orgunitIds ) + ")  ";
        }

        sql += " AND EXISTS ( SELECT programstageinstanceid FROM programstageinstance _psi "
            + " WHERE _psi.organisationunitid=ou.organisationunitid AND _psi.programstageid = " + programStageId
            + " AND _psi.completed=true AND _psi.executiondate >= '" + PARAM_PERIOD_START_DATE
            + "' AND _psi.executiondate <= '" + PARAM_PERIOD_END_DATE + "' ) ";

        return sql;
    }

    private String getConditionForMinusDataElement( Collection<Integer> orgunitIds, Integer programStageId,
        Integer dataElementId, String compareSide )
    {
        return " EXISTS ( SELECT _pdv.value FROM trackedentitydatavalue _pdv inner join programstageinstance _psi "
            + "                         ON _pdv.programstageinstanceid=_psi.programstageinstanceid "
            + "                 JOIN programinstance _pi ON _pi.programinstanceid=_psi.programinstanceid "
            + "           WHERE psi.programstageinstanceid=_pdv.programstageinstanceid "
            + "                  AND _pdv.dataelementid=" + dataElementId
            + "                 AND _psi.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgunitIds )
            + ") " + "                 AND _psi.programstageid = " + programStageId
            + " AND ( _psi.executionDate BETWEEN '" + PARAM_PERIOD_START_DATE + "' AND '" + PARAM_PERIOD_END_DATE
            + "') " + "                 AND ( DATE(_pdv.value) - DATE(" + compareSide + ") ) ";
    }

    private String getConditionForMisus2DataElement( Collection<Integer> orgunitIds, String programStageId1,
        String dataElementId1, String programStageId2, String dataElementId2, String compareSide )
    {
        return " EXISTS ( SELECT * FROM ( SELECT _pdv.value FROM trackedentitydatavalue _pdv "
            + "                 INNER JOIN programstageinstance _psi ON _pdv.programstageinstanceid=_psi.programstageinstanceid "
            + "                 JOIN programinstance _pi ON _pi.programinstanceid=_psi.programinstanceid "
            + "           WHERE psi.programstageinstanceid=_pdv.programstageinstanceid AND _pdv.dataelementid= "
            + dataElementId1
            + "                 AND _psi.organisationunitid in ("
            + TextUtils.getCommaDelimitedString( orgunitIds )
            + ") "
            + "                 AND _psi.programstageid = "
            + programStageId1
            + "                 AND _psi.executionDate>='"
            + PARAM_PERIOD_START_DATE
            + "'  "
            + "                 AND _psi.executionDate <= '"
            + PARAM_PERIOD_END_DATE
            + "' ) AS d1 cross join "
            + "         (  SELECT _pdv.value FROM trackedentitydatavalue _pdv INNER JOIN programstageinstance _psi "
            + "                        ON _pdv.programstageinstanceid=_psi.programstageinstanceid "
            + "                  JOIN programinstance _pi ON _pi.programinstanceid=_psi.programinstanceid "
            + "           WHERE psi.programstageinstanceid=_pdv.programstageinstanceid and _pdv.dataelementid= "
            + dataElementId2
            + "                 AND _psi.organisationunitid in ("
            + TextUtils.getCommaDelimitedString( orgunitIds )
            + ") "
            + "                 AND _psi.programstageid =  "
            + programStageId2
            + "                 AND _psi.executionDate>='"
            + PARAM_PERIOD_START_DATE
            + "'  "
            + "                 AND _psi.executionDate <= '"
            + PARAM_PERIOD_END_DATE
            + "' ) AS d2 WHERE DATE(d1.value ) - DATE(d2.value) " + compareSide;
    }

    private String getConditionForMisus2Attribute( String attribute1, String attribute2, String compareSide )
    {
        return " EXISTS ( SELECT * FROM (  SELECT _teav.value FROM trackedentityattributevalue _teav "
            + " WHERE _teav.trackedentityinstanceid=p.trackedentityinstanceid "
            + " and _teav.trackedentityattributeid = " + attribute1 + " ) as a1 , "
            + " ( SELECT _teav.value FROM trackedentityattributevalue _teav "
            + " WHERE _teav.trackedentityinstanceid=p.trackedentityinstanceid  "
            + " and  _teav.trackedentityattributeid =  " + attribute2 + " ) as a2 "
            + " WHERE DATE(a1.value ) - DATE(a2.value) " + compareSide;
    }

    /**
     * Return the Ids of organisation units which entity instances registered or
     * events happened.
     * 
     */
    @Override
    public Collection<Integer> getServiceOrgunit()
    {
        String sql = "(select distinct organisationunitid from trackedentityinstance)";
        sql += " UNION ";
        sql += "(select distinct organisationunitid from programstageinstance where organisationunitid is not null)";

        Collection<Integer> orgunitIds = jdbcTemplate.query( sql, new RowMapper<Integer>()
        {
            public Integer mapRow( ResultSet rs, int rowNum )
                throws SQLException
            {
                return rs.getInt( 1 );
            }
        } );

        return orgunitIds;
    }

    @Override
    public String parseExpressionDetailsToSql( String caseExpression, String operator, Integer orgunitId,
        Period period, boolean nonRegistrationProgram )
    {
        String sql = "SELECT ";

        Collection<Integer> orgunitIds = new HashSet<>();
        orgunitIds.add( orgunitId );

        if ( hasOrgunitProgramStageCompleted( caseExpression ) )
        {
            sql += "ou.name " + createSQL( caseExpression, operator, orgunitIds );
        }
        else if ( nonRegistrationProgram )
        {
            sql += " pdv.programstageinstanceid as event, pgs.name as program_stage, de.name as data_element, pdv.value, psi.executiondate as report_date ";
            sql += " FROM programstageinstance psi inner join programinstance pi on pi.programinstanceid=psi.programinstanceid ";
            sql += " INNER JOIN organisationunit ou ON ou.organisationunitid=psi.organisationunitid ";
            sql += " INNER JOIN trackedentitydatavalue pdv ON pdv.programstageinstanceid=psi.programstageinstanceid ";
            sql += " INNER JOIN dataelement de ON de.dataelementid=pdv.dataelementid ";
            sql += " INNER JOIN program pg on pg.programid=pi.programid ";
            sql += " INNER JOIN programstage pgs ON pgs.programid=pg.programid ";
        }
        else
        {
            sql += " p.trackedentityinstanceid as tracked_entity_instance, tea.name as attribute, ";
            sql += "teav.value as value, pg.name as program ";
            sql += "FROM trackedentityinstance p ";
            sql += "INNER JOIN trackedentityattributevalue teav on p.trackedentityinstanceid=teav.trackedentityinstanceid  ";
            sql += "INNER JOIN trackedentityattribute tea on tea.trackedentityattributeid=teav.trackedentityattributeid  ";
            sql += "INNER JOIN programinstance as pi on p.trackedentityinstanceid=pi.trackedentityinstanceid  ";
            sql += "INNER JOIN program pg on pg.programid=pi.programid  ";
            sql += "INNER JOIN programstage pgs on pgs.programid=pg.programid  ";

            if ( hasDataelementCriteria( caseExpression ) )
            {
                sql += " INNER JOIN programstageinstance psi on pi.programinstanceid=psi.programinstanceid ";
                sql += " INNER JOIN organisationunit ou on ou.organisationunitid=psi.organisationunitid ";
                sql += " INNER JOIN trackedentitydatavalue pdv on pdv.programstageinstanceid=psi.programstageinstanceid ";
            }
        }

        sql += " WHERE " + createSQL( caseExpression, operator, orgunitIds );

        sql = sql.replaceAll( "COMBINE", "" );

        if ( nonRegistrationProgram )
        {
            sql += " ORDER BY pdv.programstageinstanceid";
        }
        else
        {
            sql += " ORDER BY  p.trackedentityinstanceid ";
        }

        sql = replacePeriodSql( sql, period );

        return sql;
    }

    @Override
    public List<Integer> executeSQL( String sql )
    {
        try
        {
            List<Integer> entityInstanceIds = jdbcTemplate.query( sql, new RowMapper<Integer>()
            {
                public Integer mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    return rs.getInt( 1 );
                }
            } );

            return entityInstanceIds;
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<Period> getPeriods( String periodTypeName, String taskStrategy )
    {
        Calendar calStartDate = Calendar.getInstance();

        if ( TASK_AGGREGATE_QUERY_BUILDER_LAST_MONTH.equals( taskStrategy ) )
        {
            calStartDate.add( Calendar.MONTH, -1 );
        }
        else if ( TASK_AGGREGATE_QUERY_BUILDER_LAST_3_MONTH.equals( taskStrategy ) )
        {
            calStartDate.add( Calendar.MONTH, -3 );
        }
        else if ( TASK_AGGREGATE_QUERY_BUILDER_LAST_6_MONTH.equals( taskStrategy ) )
        {
            calStartDate.add( Calendar.MONTH, -6 );
        }
        else if ( TASK_AGGREGATE_QUERY_BUILDER_LAST_12_MONTH.equals( taskStrategy ) )
        {
            calStartDate.add( Calendar.MONTH, -12 );
        }

        Date startDate = calStartDate.getTime();

        Calendar calEndDate = Calendar.getInstance();

        Date endDate = calEndDate.getTime();

        CalendarPeriodType periodType = (CalendarPeriodType) PeriodType.getPeriodTypeByName( periodTypeName );
        String sql = "select periodtypeid from periodtype where name='" + periodTypeName + "'";
        int periodTypeId = jdbcTemplate.queryForObject( sql, Integer.class );

        Collection<Period> periods = periodType.generatePeriods( startDate, endDate );

        for ( Period period : periods )
        {
            String start = DateUtils.getMediumDateString( period.getStartDate() );
            String end = DateUtils.getMediumDateString( period.getEndDate() );

            sql = "select periodid from period where periodtypeid=" + periodTypeId + " and startdate='" + start
                + "' and enddate='" + end + "'";
            Integer periodid = null;
            SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );
            if ( rs.next() )
            {
                periodid = rs.getInt( "periodid" );
            }

            if ( periodid == null )
            {
                String insertSql = "insert into period (periodid, periodtypeid,startdate,enddate) " + " VALUES " + "("
                    + statementBuilder.getAutoIncrementValue() + "," + periodTypeId + ",'" + start + "','" + end
                    + "' )";
                jdbcTemplate.execute( insertSql );

                period.setId( jdbcTemplate.queryForObject( sql, Integer.class ) );
            }
            else
            {
                period.setId( periodid );
            }
        }

        return periods;
    }

    private boolean hasOrgunitProgramStageCompleted( String expresstion )
    {
        Pattern pattern = Pattern.compile( CaseAggregationCondition.regExp );
        Matcher matcher = pattern.matcher( expresstion );
        while ( matcher.find() )
        {
            String match = matcher.group();

            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );

            if ( info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_ORGUNIT_COMPLETE_PROGRAM_STAGE ) )
            {
                return true;
            }
        }

        return false;
    }

    private boolean hasEntityInstanceCriteria( String expresstion )
    {
        Pattern pattern = Pattern.compile( CaseAggregationCondition.regExp );
        Matcher matcher = pattern.matcher( expresstion );
        while ( matcher.find() )
        {
            String match = matcher.group();

            match = match.replaceAll( "[\\[\\]]", "" );

            String[] info = match.split( SEPARATOR_OBJECT );

            if ( info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_TRACKED_ENTITY_ATTRIBUTE ) )
            {
                return true;
            }
        }

        return false;
    }

    private boolean hasDataelementCriteria( String expression )
    {
        Pattern pattern = Pattern.compile( CaseAggregationCondition.regExp );
        Matcher matcher = pattern.matcher( expression );
        while ( matcher.find() )
        {
            String match = matcher.group();

            match = match.replaceAll( "[\\[\\]]", "" );
            String[] info = match.split( SEPARATOR_OBJECT );

            if ( info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_PROGRAM_STAGE_DATAELEMENT )
                || info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_PROGRAM_STAGE )
                || info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_PROGRAM_STAGE_PROPERTY )
                || info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_TRACKED_ENTITY_PROGRAM_STAGE_PROPERTY )
                || info[0].equalsIgnoreCase( CaseAggregationCondition.OBJECT_ORGUNIT_COMPLETE_PROGRAM_STAGE ) )
            {
                return true;
            }
        }

        return false;
    }

   
    private String replacePeriodSql( String sql, Period period )
    {
        sql = sql.replaceAll( "COMBINE", "" );
        sql = sql.replaceAll( PARAM_PERIOD_START_DATE, DateUtils.getMediumDateString( period.getStartDate() ) );
        sql = sql.replaceAll( PARAM_PERIOD_END_DATE, DateUtils.getMediumDateString( period.getEndDate() ) );
        sql = sql.replaceAll( PARAM_PERIOD_ID, period.getId() + "" );
        sql = sql.replaceAll( PARAM_PERIOD_ISO_DATE, period.getIsoDate() );

        return sql;
    }
    
}
