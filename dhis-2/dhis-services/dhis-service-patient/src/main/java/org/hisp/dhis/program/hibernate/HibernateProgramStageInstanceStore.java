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
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifierType;
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

    public Grid getTabularReport( ProgramStage programStage, List<Boolean> hiddenCols, Map<Integer, OrganisationUnitLevel> orgUnitLevelMap,
        List<PatientIdentifierType> identifiers, List<String> fixedAttributes, List<PatientAttribute> attributes,
        List<DataElement> dataElements, Map<Integer, String> identifierKeys, Map<Integer, String> attributeKeys,
        Map<Integer, String> dataElementKeys, Collection<Integer> orgUnits,
        int level, int maxLevel, Date startDate, Date endDate, boolean descOrder, Integer min, Integer max )
    {
        Grid grid = new ListGrid();
        
        grid.addHeader( new GridHeader( "id", true, true ) );
        
        grid.addHeader( new GridHeader( "Report date", false, true ) );
        
        //TODO hidden cols
        
        for ( int i = 0; i < maxLevel; i++ )
        {
            int l = i + 1;
            String name = orgUnitLevelMap.containsKey( l ) ? orgUnitLevelMap.get( l ).getName() : "Level " + l;
            grid.addHeader( new GridHeader( name, false, true ) );
        }

        for ( PatientIdentifierType type : identifiers )
        {
            grid.addHeader( new GridHeader( type.getName(), false, true ) );
        }
        
        for ( String attribute : fixedAttributes )
        {
            grid.addHeader( new GridHeader( attribute, false, true ) );
        }

        for ( PatientAttribute attribute : attributes )
        {
            grid.addHeader( new GridHeader( attribute.getName(), false, true ) );
        }

        for ( DataElement element : dataElements )
        {
            grid.addHeader( new GridHeader( element.getDisplayName(), false, true ) );
        }
        
        String sql = getTabularReportSql( false, programStage, identifiers, fixedAttributes, attributes, dataElements, 
            identifierKeys, attributeKeys, dataElementKeys, orgUnits, level, maxLevel, startDate, endDate, descOrder, min, max );
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
        
        GridUtils.addRows( grid, rowSet );
        
        return grid;
    }
    
    public int getTabularReportCount( ProgramStage programStage, List<PatientIdentifierType> identifiers, List<String> fixedAttributes, List<PatientAttribute> attributes,
        List<DataElement> dataElements, Map<Integer, String> identifierKeys, Map<Integer, String> attributeKeys,
        Map<Integer, String> dataElementKeys, Collection<Integer> orgUnits,
        int level, int maxLevel, Date startDate, Date endDate )
    {
        String sql = getTabularReportSql( true, programStage, identifiers, fixedAttributes, attributes, dataElements, identifierKeys, attributeKeys, 
            dataElementKeys, orgUnits, level, maxLevel, startDate, endDate, false, null, null );
        
        return jdbcTemplate.queryForInt( sql );
    }

    private String getTabularReportSql( boolean count, ProgramStage programStage, List<PatientIdentifierType> identifiers, List<String> fixedAttributes, List<PatientAttribute> attributes,
        List<DataElement> dataElements, Map<Integer, String> identifierKeys, Map<Integer, String> attributeKeys,
        Map<Integer, String> dataElementKeys, Collection<Integer> orgUnits,
        int level, int maxLevel, Date startDate, Date endDate, boolean descOrder,
        Integer min, Integer max )
    {
        String sDate = DateUtils.getMediumDateString( startDate );
        String eDate = DateUtils.getMediumDateString( endDate );
        
        String selector = count ? "count(*) " : "* ";
        
        String sql = "select " + selector + "from ( select psi.programstageinstanceid, psi.executiondate,";
        
        for ( int i = 0; i < maxLevel; i++ )
        {
            int l = i + 1;            
            sql += "(select name from organisationunit where organisationunitid=ous.idlevel" + l + ") as level_" + i + ",";
        }

        for ( PatientIdentifierType type : identifiers )
        {
            sql += "(select identifier from patientidentifier where patientid=p.patientid and patientidentifiertypeid=" + type.getId() + ") as identifier_" + type.getId() + ",";
        }
        
        for ( String attribute : fixedAttributes )
        {
            sql += "p." + attribute + ",";
        }

        for ( PatientAttribute attribute : attributes )
        {
            sql += "(select value from patientattributevalue where patientid=p.patientid and patientattributeid=" + attribute.getId() + ") as attribute_" + attribute.getId() + ",";
        }

        for ( DataElement element : dataElements )
        {
            sql += "(select value from patientdatavalue where programstageinstanceid=psi.programstageinstanceid and dataelementid=" + element.getId() + ") as element_" + element.getId() + ",";
        }
        
        sql = sql.substring( 0, sql.length() - 1 ) + " "; // Removing last comma

        sql += "from programstageinstance psi ";
        sql += "left join programinstance pi on (psi.programinstanceid=pi.programinstanceid) ";
        sql += "left join patient p on (pi.patientid=p.patientid) ";
        sql += "join organisationunit ou on (ou.organisationunitid=psi.organisationunitid) ";
        sql += "join _orgunitstructure ous on (psi.organisationunitid=ous.organisationunitid) ";
        
        sql += "where psi.programstageid=" + programStage.getId() + " ";
        sql += "and psi.executiondate >= '" + sDate + "' ";
        sql += "and psi.executiondate < '" + eDate + "' ";

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
        sql += min != null && max != null ? "offset 0 limit 50 " : ""; //TODO page size
        sql += ") as tabular ";
        
        String operator = "where ";
        
        for ( Integer key : identifierKeys.keySet() )
        {
            sql += operator + "identifier_" + key + identifierKeys.get( key ) + " ";
            operator = "and ";
        }
        
        for ( Integer key : attributeKeys.keySet() )
        {
            sql += operator + "attribute_" + key + attributeKeys.get( key ) + " ";
            operator = "and ";
        }
        
        for ( Integer key : dataElementKeys.keySet() )
        {
            sql += operator + "element_" + key + dataElementKeys.get( key ) + " ";
            operator = "and ";
        }

        sql = sql.substring( 0, sql.length() - 1 ) + " "; // Remove last comma if exists

        log.info(sql);
        
        return sql;
    }
}
