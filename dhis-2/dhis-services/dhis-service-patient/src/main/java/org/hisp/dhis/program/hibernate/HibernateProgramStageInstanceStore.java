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

import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_DATA_ELEMENT;
import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_FIXED_ATTRIBUTE;
import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_IDENTIFIER_TYPE;
import static org.hisp.dhis.patientreport.PatientTabularReport.PREFIX_PATIENT_ATTRIBUTE;
import static org.hisp.dhis.system.util.TextUtils.lower;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceStore;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class HibernateProgramStageInstanceStore
    extends HibernateGenericStore<ProgramStageInstance>
    implements ProgramStageInstanceStore
{
    private static final Log log = LogFactory.getLog( HibernateProgramStageInstanceStore.class );

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

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
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
        Collection<Integer> orgUnits, List<String> searchKeys, int level, int maxLevel, Date startDate,
        Date endDate, boolean descOrder, Integer min, Integer max )
    {
        // ---------------------------------------------------------------------
        // Headers TODO hidden cols
        // ---------------------------------------------------------------------
        
        Grid grid = new ListGrid();

        grid.addHeader( new GridHeader( "id", true, true ) );
        grid.addHeader( new GridHeader( "Report date", false, true ) );

        for ( int i = 0; i < maxLevel; i++ )
        {
            int l = i + 1;
            String name = orgUnitLevelMap.containsKey( l ) ? orgUnitLevelMap.get( l ).getName() : "Level " + l;

            grid.addHeader( new GridHeader( name, false, true ) );
        }

        for ( String searchKey : searchKeys )
        {
            String[] values = searchKey.split( "_" );
            String objectType = values[0];

            boolean hidden = Boolean.parseBoolean( values[2] );
            String name = "";

            if ( objectType.equals( PREFIX_FIXED_ATTRIBUTE ) )
            {
                name = values[1];
            }
            else
            {
                int objectId = Integer.parseInt( values[1] );

                if ( objectType.equals( PREFIX_IDENTIFIER_TYPE ) )
                {
                    name = patientIdentifierTypeService.getPatientIdentifierType( objectId ).getName();
                }
                else if ( objectType.equals( PREFIX_PATIENT_ATTRIBUTE ) )
                {
                    name = patientAttributeService.getPatientAttribute( objectId ).getName();
                }
                else if ( objectType.equals( PREFIX_DATA_ELEMENT ) )
                {
                    name = dataElementService.getDataElement( objectId ).getName();
                }
            }

            grid.addHeader( new GridHeader( name, hidden, true ) );
        }

        // ---------------------------------------------------------------------
        // Get SQL and build grid 
        // ---------------------------------------------------------------------
        
        String sql = getTabularReportSql( false, programStage, searchKeys, orgUnits, level, maxLevel, startDate,
            endDate, descOrder, min, max );

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        GridUtils.addRows( grid, rowSet );

        return grid;
    }

    public int getTabularReportCount( ProgramStage programStage, List<String> searchKeys,
        Collection<Integer> organisationUnits, int level, int maxLevel, Date startDate, Date endDate )
    {
        String sql = getTabularReportSql( true, programStage, searchKeys, organisationUnits, level, maxLevel,
            startDate, endDate, false, null, null );

        return jdbcTemplate.queryForInt( sql );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Search values on format type_id/name_hidden_='query'
     */
    private String getTabularReportSql( boolean count, ProgramStage programStage, List<String> searchKeys,
        Collection<Integer> orgUnits, int level, int maxLevel, Date startDate, Date endDate, boolean descOrder,
        Integer min, Integer max )
    {
        String selector = count ? "count(*) " : "* ";

        String sql = "select " + selector + "from ( select psi.programstageinstanceid, psi.executiondate,";
        String where = "";
        String operator = "where ";

        for ( int i = 0; i < maxLevel; i++ )
        {
            int l = i + 1;
            sql += "(select name from organisationunit where organisationunitid=ous.idlevel" + l + ") as level_" + i + ",";
        }

        for ( String searchKey : searchKeys )
        {
            String[] values = searchKey.split( "_" );
            String objectType = values[0];

            if ( objectType.equals( PREFIX_FIXED_ATTRIBUTE ) )
            {
                sql += "p." + values[1] + ",";

                if ( values.length == 4 )
                {
                    where += operator + "lower(" + values[1] + ") " + lower( values[3] ) + " ";
                    operator = "and ";
                }
            }
            else
            {
                int objectId = Integer.parseInt( values[1] );

                if ( objectType.equals( PREFIX_IDENTIFIER_TYPE ) )
                {
                    sql += "(select identifier from patientidentifier where patientid=p.patientid and patientidentifiertypeid="
                        + objectId + ") as identifier_" + objectId + ",";

                    if ( values.length == 4 )
                    {
                        where += operator + "lower(identifier_" + objectId + ") " + lower( values[3] ) + " ";
                        operator = "and ";
                    }
                }
                else if ( objectType.equals( PREFIX_PATIENT_ATTRIBUTE ) )
                {
                    sql += "(select value from patientattributevalue where patientid=p.patientid and patientattributeid="
                        + objectId + ") as attribute_" + objectId + ",";
                    
                    if ( values.length == 4 )
                    {
                        where += operator + "lower(attribute_" + objectId + ") " + lower( values[3] ) + " ";
                        operator = "and ";
                    }
                }
                else if ( objectType.equals( PREFIX_DATA_ELEMENT ) )
                {
                    sql += "(select value from patientdatavalue where programstageinstanceid=psi.programstageinstanceid and dataelementid="
                        + objectId + ") as element_" + objectId + ",";

                    if ( values.length == 4 )
                    {
                        where += operator + "lower(element_" + objectId + ") " + lower( values[3] ) + " ";
                        operator = "and ";
                    }
                }
            }
        }

        sql = sql.substring( 0, sql.length() - 1 ) + " "; // Removing last comma

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
            sql += "and psi.executiondate < '" + eDate + "' ";
        }
        
        if ( orgUnits != null )
        {
            sql += "and ou.organisationunitid in (" + TextUtils.getCommaDelimitedString( orgUnits ) + ") ";
        }

        sql += "order by ";

        for ( int i = 0; i < maxLevel; i++ )
        {
            sql += "level_" + i + ",";
        }

        sql += "psi.executiondate ";
        sql += descOrder ? "desc " : "";
        sql += (min != null && max != null) ? statementBuilder.limitRecord( min, max ) : "";
        sql += ") as tabular ";// TODO page size

        // filters
        sql += where;

        sql = sql.substring( 0, sql.length() - 1 ) + " "; // Remove last comma

        log.info( sql );

        return sql;
    }
}
