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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientreport.PatientTabularReport;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceStore;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class HibernateProgramStageInstanceStore
    extends HibernateGenericStore<ProgramStageInstance>
    implements ProgramStageInstanceStore
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
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

    public Map<String, String> get( ProgramStage programStage, List<String> keys,
        Map<Integer, String> searchingIdenKeys, List<String> fixedAttributes, Map<Integer, String> searchingAttrKeys,
        Map<Integer, String> searchingDEKeys, Collection<Integer> upperOrgunitIds,
        Collection<Integer> bottomOrgunitIds, Date startDate, Date endDate, boolean orderByOrgunitAsc,
        boolean orderByExecutionDateByAsc, int min, int max )
    {
        String sql = getTabularReportStatement( 1, programStage, searchingIdenKeys, fixedAttributes, searchingAttrKeys,
            searchingDEKeys, upperOrgunitIds, startDate, endDate, orderByOrgunitAsc, orderByExecutionDateByAsc );

        if ( bottomOrgunitIds.size() > 0 )
        {
            String sqlBottom = getTabularReportStatement( 1, programStage, searchingIdenKeys, fixedAttributes,
                searchingAttrKeys, searchingDEKeys, bottomOrgunitIds, startDate, endDate, orderByOrgunitAsc,
                orderByExecutionDateByAsc );
            sql = "( " + sqlBottom + ") union all ( " + sql + " ) ";
        }

        sql += statementBuilder.limitRecord( min, max );
        List<Integer> ids = executeSQL( sql );
        if ( ids != null && ids.size() > 0 )
        {
            sql = getTabularReportStatement( ids, searchingIdenKeys, fixedAttributes, searchingAttrKeys,
                orderByOrgunitAsc, orderByExecutionDateByAsc );
            return executeSQL( sql, keys, fixedAttributes );
        }

        return null;
    }

    public Map<String, String> get( ProgramStage programStage, List<String> keys,
        Map<Integer, String> searchingIdenKeys, List<String> fixedAttributes, Map<Integer, String> searchingAttrKeys,
        Map<Integer, String> searchingDEKeys, Collection<Integer> upperOrgunitIds,
        Collection<Integer> bottomOrgunitIds, Date startDate, Date endDate, boolean orderByOrgunitAsc,
        boolean orderByExecutionDateByAsc )
    {
        String sql = getTabularReportStatement( 1, programStage, searchingIdenKeys, fixedAttributes, searchingAttrKeys,
            searchingDEKeys, upperOrgunitIds, startDate, endDate, orderByOrgunitAsc, orderByExecutionDateByAsc );

        if ( bottomOrgunitIds.size() > 0 )
        {
            String sqlBottom = getTabularReportStatement( 1, programStage, searchingIdenKeys, fixedAttributes,
                searchingAttrKeys, searchingDEKeys, bottomOrgunitIds, startDate, endDate, orderByOrgunitAsc,
                orderByExecutionDateByAsc );
            sql = "( " + sqlBottom + ") union all ( " + sql + " ) ";
        }

        List<Integer> ids = executeSQL( sql );
        if ( ids != null && ids.size() > 0 )
        {
            sql = getTabularReportStatement( ids, searchingIdenKeys, fixedAttributes, searchingAttrKeys,
                orderByOrgunitAsc, orderByExecutionDateByAsc );

            return executeSQL( sql, keys, fixedAttributes );
        }

        return null;
    }

    public int count( ProgramStage programStage, Map<Integer, String> searchingIdenKeys,
        Map<Integer, String> searchingAttrKeys, Map<Integer, String> searchingDEKeys, Collection<Integer> orgunitIds,
        Date startDate, Date endDate )
    {
        String sql = getTabularReportStatement( 0, programStage, searchingIdenKeys, null, searchingAttrKeys,
            searchingDEKeys, orgunitIds, startDate, endDate, true, true );

        return executeCountSQL( sql );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String getData( int mark, Map<Integer, String> searchingIdenKeys, List<String> fixedAttributes,
        Map<Integer, String> searchingAttrKeys )
    {
        String select = "";
        switch ( mark )
        {
        // count result
        case 0:
            select = "select count( distinct psi.programstageinstanceid ) ";
            break;
        // Get data with limit
        case 1:
            select = "SELECT distinct psi.programstageinstanceid, ogu.name , psi.executiondate ";
            break;
        case 2:
            select = "SELECT psi.programstageinstanceid AS psiid, ogu.organisationunitid AS orgunitid, ogu.name AS orgunitname, psi.executiondate AS "
                + PatientTabularReport.PREFIX_EXECUTION_DATE;

            if ( fixedAttributes != null )
            {
                for ( String fixedAttribute : fixedAttributes )
                {
                    select += ",p." + fixedAttribute + " AS " + PatientTabularReport.PREFIX_FIXED_ATTRIBUTE + "_"
                        + fixedAttribute;
                }
            }
            if ( searchingIdenKeys.size() > 0 )
            {
                select += ",pid.patientidentifiertypeid AS " + PatientTabularReport.PREFIX_IDENTIFIER_TYPE
                    + "_id, pid.identifier AS " + PatientTabularReport.PREFIX_IDENTIFIER_TYPE + "_value";
            }
            if ( searchingAttrKeys.size() > 0 )
            {
                select += ",pav.patientattributeid AS " + PatientTabularReport.PREFIX_PATIENT_ATTRIBUTE
                    + "_id, pav.value AS " + PatientTabularReport.PREFIX_PATIENT_ATTRIBUTE + "_value ";
            }
            break;
        default:
            return "";
        }

        if ( mark == 2 )
        {
            select += ",pdv.dataelementid AS " + PatientTabularReport.PREFIX_DATA_ELEMENT + "_id, pdv.value AS "
                + PatientTabularReport.PREFIX_DATA_ELEMENT + "_value FROM programstageinstance AS psi "
                + " INNER JOIN patientdatavalue AS pdv ON pdv.programstageinstanceid=psi.programstageinstanceid "
                + " INNER JOIN programinstance pi ON pi.programinstanceid=psi.programinstanceid "
                + " INNER JOIN organisationunit ogu ON ogu.organisationunitid=psi.organisationunitid ";

            if ( (fixedAttributes != null && fixedAttributes.size() > 0) || searchingIdenKeys.size() > 0
                || searchingAttrKeys.size() > 0 )
            {
                select += " INNER JOIN patient AS p on p.patientid = pi.patientid ";
            }

            if ( searchingIdenKeys.size() > 0 )
            {
                select += " INNER JOIN patientidentifier AS pid ON pid.patientid = p.patientid ";
            }
            if ( searchingAttrKeys.size() > 0 )
            {
                select += " INNER JOIN patientattributevalue AS pav ON pav.patientid = p.patientid ";
            }
        }
        else
        {
            select += " FROM programstageinstance AS psi "
                + " INNER JOIN patientdatavalue AS pdv ON pdv.programstageinstanceid=psi.programstageinstanceid "
                + " INNER JOIN programinstance pi ON pi.programinstanceid=psi.programinstanceid "
                + " INNER JOIN organisationunit ogu ON ogu.organisationunitid=psi.organisationunitid ";
        }

        return select;
    }

    private String getTabularReportStatement( int mark, ProgramStage programStage,
        Map<Integer, String> searchingIdenKeys, List<String> fixedAttributes, Map<Integer, String> searchingAttrKeys,
        Map<Integer, String> searchingDEKeys, Collection<Integer> orgunitIds, Date startDate, Date endDate,
        boolean orderByOrgunitAsc, boolean orderByExecutionDateByAsc )
    {
        String select = getData( mark, searchingIdenKeys, fixedAttributes, searchingAttrKeys );

        String sqlID = " select distinct psi.programstageinstanceid from patientdatavalue pdv "
            + "inner join programstageinstance psi on pdv.programstageinstanceid=psi.programstageinstanceid "
            + "INNER JOIN patientidentifier as pid ON pid.patientid = p.patientid "
            + "INNER JOIN patientidentifiertype as pit ON pid.patientidentifiertypeid = pit.patientidentifiertypeid ";

        String sqlATTR = " SELECT distinct psi.programstageinstanceid " + "FROM programstageinstance AS psi "
            + "INNER JOIN patientdatavalue AS pdv ON pdv.programstageinstanceid=psi.programstageinstanceid "
            + "INNER JOIN programinstance pi ON pi.programinstanceid=psi.programinstanceid "
            + "INNER JOIN organisationunit ogu ON ogu.organisationunitid=psi.organisationunitid "
            + "INNER JOIN patient AS p on p.patientid = pi.patientid "
            + "INNER JOIN patientattributevalue AS pav ON pav.patientid = p.patientid ";

        String sqlDE = " select distinct psi.programstageinstanceid from patientdatavalue pdv "
            + "inner join programstageinstance psi on pdv.programstageinstanceid=psi.programstageinstanceid ";
        
        String condition = " WHERE psi.executiondate >= '" + DateUtils.getMediumDateString( startDate )
            + "' AND psi.executiondate <= '" + DateUtils.getMediumDateString( endDate ) + "' "
            + " AND psi.organisationunitid in " + splitListHelper( orgunitIds ) + " AND psi.programstageid = "
            + programStage.getId() + " ";

        // ---------------------------------------------------------------------
        // Searching program-stage-instances by patient-identifiers
        // ---------------------------------------------------------------------

        Iterator<Integer> idenKeys = searchingIdenKeys.keySet().iterator();
        boolean index = false;
        while ( idenKeys.hasNext() )
        {
            Integer attributeId = idenKeys.next();

            if ( index )
            {
                condition += " AND psi.programstageinstanceid in ( " + sqlID + " WHERE psi.programstageid = "
                    + programStage.getId() + " ";
            }

            condition += " AND pid.patientidentifierTypeid=" + attributeId + " AND lower(pid.identifier) ";

            String compareValue = searchingIdenKeys.get( attributeId ).toLowerCase();

            if ( compareValue.contains( "%" ) )
            {
                compareValue = compareValue.replace( "=", "like " );
            }

            condition += compareValue;

            if ( index )
            {
                condition += ") ";
            }

            index = true;
        }

        // ---------------------------------------------------------------------
        // Searching program-stage-instances by patient-attributes
        // ---------------------------------------------------------------------

        Iterator<Integer> attrKeys = searchingAttrKeys.keySet().iterator();
        index = false;
        while ( attrKeys.hasNext() )
        {
            Integer attributeId = attrKeys.next();

            if ( index )
            {
                condition += " AND psi.programstageinstanceid in ( " + sqlATTR + " WHERE 1=1 ";
            }

            condition += " AND pav.patientattributeid=" + attributeId + " AND lower(pav.value) ";

            String compareValue = searchingAttrKeys.get( attributeId ).toLowerCase();

            if ( compareValue.contains( "%" ) )
            {
                compareValue = compareValue.replace( "=", "like " );
            }

            condition += compareValue;

            if ( index )
            {
                condition += ") ";
            }

            index = true;
        }

        // ---------------------------------------------------------------------
        // Searching program-stage-instances by dataelements
        // ---------------------------------------------------------------------

        Iterator<Integer> deKeys = searchingDEKeys.keySet().iterator();

        index = false;
        while ( deKeys.hasNext() )
        {
            Integer dataElementId = deKeys.next();

            condition += " AND psi.programstageinstanceid in ( " + sqlDE + " WHERE 1=1 ";

            condition += " AND pdv.dataElementid=" + dataElementId + " AND lower(pdv.value) ";

            String compareValue = searchingDEKeys.get( dataElementId ).toLowerCase();

            if ( compareValue.contains( "%" ) )
            {
                compareValue = compareValue.replace( "=", "like " );
            }

            condition += compareValue;

            condition += ") ";
        }

        if ( mark == 0 )
        {
            return select + condition;
        }

        condition += " ORDER BY ogu.name ";
        condition += orderByOrgunitAsc ? "asc" : "desc";
        condition += ", psi.executiondate ";
        condition += orderByExecutionDateByAsc ? "asc" : "desc";

        return select + condition;
    }

    private String getTabularReportStatement( List<Integer> ids, Map<Integer, String> searchingIdenKeys,
        List<String> fixedAttributes, Map<Integer, String> searchingAttrKeys, boolean orderByOrgunitAsc,
        boolean orderByExecutionDateByAsc )
    {
        String sql = getData( 2, searchingIdenKeys, fixedAttributes, searchingAttrKeys );

        sql += " WHERE psi.programstageinstanceid in " + splitListHelper( ids ) + " ";
        sql += " ORDER BY ogu.name ";
        sql += orderByOrgunitAsc ? "asc" : "desc";
        sql += ", psi.executiondate ";
        sql += orderByExecutionDateByAsc ? "asc" : "desc";

        return sql;
    }

    private Map<String, String> executeSQL( String sql, List<String> keys, List<String> fixedAttributes )
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement.executeQuery( sql );

            return gridMapping( resultSet, fixedAttributes, keys );
        }
        catch ( Exception ex )
        {
            return null;
        }
        finally
        {
            holder.close();
        }
    }

    private List<Integer> executeSQL( String sql )
    {
        List<Integer> result = new ArrayList<Integer>();

        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement.executeQuery( sql );

            while ( resultSet.next() )
            {
                result.add( resultSet.getInt( 1 ) );
            }

            return result;
        }
        catch ( Exception ex )
        {
            return null;
        }
        finally
        {
            holder.close();
        }
    }

    private int executeCountSQL( String sql )
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement.executeQuery( sql );

            return (resultSet != null && resultSet.next()) ? resultSet.getInt( 1 ) : 0;
        }
        catch ( Exception ex )
        {
            return 0;
        }
        finally
        {
            holder.close();
        }
    }

    /**
     * Splits a list of integers by comma. Use this method if you have a list
     * that will be used in f.ins. a WHERE xxx IN (list) clause in SQL.
     * 
     * @param Collection <Integer> list of Integers
     * @return the list as a string splitted by a comma.
     */
    private String splitListHelper( Collection<Integer> list )
    {
        StringBuffer sb = new StringBuffer();
        int count = 0;

        sb.append( "(" );
        for ( Integer i : list )
        {
            sb.append( i );

            count++;

            if ( count < list.size() )
            {
                sb.append( "," );
            }
        }
        sb.append( ")" );

        return sb.toString();
    }

    private Map<String, String> gridMapping( ResultSet resultSet, List<String> fixedAttributes, List<String> keys )
    {
        Map<String, String> valuesMap = new HashMap<String, String>();

        try
        {
            ResultSetMetaData meta = resultSet.getMetaData();

            while ( resultSet.next() )
            {
                String key = resultSet.getString( "psiid" );

                // Get execution-date
                if ( !keys.contains( key ) )
                {
                    keys.add( key );
                }

                valuesMap.put( key + "_" + PatientTabularReport.PREFIX_EXECUTION_DATE, resultSet
                    .getString( PatientTabularReport.PREFIX_EXECUTION_DATE ) );

                // Get orgunit-id
                valuesMap.put( key + "_" + PatientTabularReport.PREFIX_ORGUNIT, resultSet.getString( "orgunitid" ) );

                for ( String fixedAttr : fixedAttributes )
                {
                    // Get fixed-attributes
                    key = resultSet.getInt( "psiid" ) + "_" + PatientTabularReport.PREFIX_FIXED_ATTRIBUTE + "_"
                        + fixedAttr;
                    valuesMap.put( key, resultSet.getString( fixedAttr ) );
                }

                // Get idens
                String colname = PatientTabularReport.PREFIX_IDENTIFIER_TYPE + "_id";
                if ( existedCol( colname, meta ) )
                {
                    key = resultSet.getInt( "psiid" ) + "_" + PatientTabularReport.PREFIX_IDENTIFIER_TYPE + "_"
                        + resultSet.getString( colname );
                    valuesMap.put( key, resultSet.getString( PatientTabularReport.PREFIX_IDENTIFIER_TYPE + "_value" ) );
                }

                // Get dynmic-attributes
                colname = PatientTabularReport.PREFIX_PATIENT_ATTRIBUTE + "_id";
                if ( existedCol( colname, meta ) )
                {
                    key = resultSet.getInt( "psiid" ) + "_" + PatientTabularReport.PREFIX_PATIENT_ATTRIBUTE + "_"
                        + resultSet.getString( colname );
                    valuesMap
                        .put( key, resultSet.getString( PatientTabularReport.PREFIX_PATIENT_ATTRIBUTE + "_value" ) );
                }

                // Get data-elements
                colname = PatientTabularReport.PREFIX_DATA_ELEMENT + "_id";
                if ( existedCol( colname, meta ) )
                {
                    key = resultSet.getInt( "psiid" ) + "_" + PatientTabularReport.PREFIX_DATA_ELEMENT + "_"
                        + resultSet.getString( colname );
                    valuesMap.put( key, resultSet.getString( PatientTabularReport.PREFIX_DATA_ELEMENT + "_value" ) );
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return valuesMap;
    }

    private boolean existedCol( String colname, ResultSetMetaData meta )
    {
        int numCol = 0;
        try
        {
            numCol = meta.getColumnCount();

            for ( int i = 1; i < numCol + 1; i++ )
            {
                if ( meta.getColumnName( i ).equals( colname ) )
                {
                    return true;
                }

            }
        }
        catch ( Exception e )
        {
            return false;
        }

        return false;
    }
}
