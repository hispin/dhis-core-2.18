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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
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

        return (list == null) ? null : list.get( list.size() - 1 );
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

    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> get( ProgramStage programStage, Map<Integer, String> searchingKeys,
        Collection<Integer> orgunitIds, Date startDate, Date endDate, int min, int max )
    {
        if ( searchingKeys.keySet().size() > 0 )
        {
            String sql = getBySearchingValues( false, programStage, searchingKeys, orgunitIds, startDate, endDate )
                + statementBuilder.limitRecord( min, max );

            List<Integer> ids = executeSQL( sql );

            List<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

            for ( Integer id : ids )
            {
                programStageInstances.add( get( id ) );
            }

            return programStageInstances;
        }

        return (getCriteria( Restrictions.eq( "programStage", programStage ), Restrictions.in( "organisationUnit.id",
            orgunitIds ), Restrictions.between( "dueDate", startDate, endDate ) )).setFirstResult( min ).setMaxResults(
            max ).list();
    }

    @SuppressWarnings( "unchecked" )
    public List<ProgramStageInstance> get( ProgramStage programStage, Map<Integer, String> searchingKeys,
        Collection<Integer> orgunitIds, Date startDate, Date endDate )
    {
        if ( searchingKeys.keySet().size() > 0 )
        {
            String sql = getBySearchingValues( false, programStage, searchingKeys, orgunitIds, startDate, endDate );

            List<Integer> ids = executeSQL( sql );

            List<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

            for ( Integer id : ids )
            {
                programStageInstances.add( get( id ) );
            }

            return programStageInstances;
        }

        return (getCriteria( Restrictions.eq( "programStage", programStage ), Restrictions.in( "organisationUnit.id",
            orgunitIds ), Restrictions.between( "dueDate", startDate, endDate ) )).list();
    }

    public int count( ProgramStage programStage, Map<Integer, String> searchingKeys, Collection<Integer> orgunitIds,
        Date startDate, Date endDate )
    {
        if ( searchingKeys.keySet().size() > 0 )
        {
            String sql = getBySearchingValues( true, programStage, searchingKeys, orgunitIds, startDate, endDate );
            List<Integer> countRow = executeSQL( sql );
            return (countRow != null && countRow.size() > 0) ? countRow.get( 0 ) : 0;
        }

        Number rs = (Number) (getCriteria( Restrictions.eq( "programStage", programStage ), Restrictions.in(
            "organisationUnit.id", orgunitIds ), Restrictions.between( "dueDate", startDate, endDate ) ))
            .setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    private String getBySearchingValues( boolean isCount, ProgramStage programStage,
        Map<Integer, String> searchingKeys, Collection<Integer> orgunitIds, Date startDate, Date endDate )
    {
        String sql = "select distinct( psi.programstageinstanceid) from patientdatavalue pdv "
            + "inner join programstageinstance psi on pdv.programstageinstanceid=psi.programstageinstanceid ";

        String condition = " WHERE psi.duedate >= '" + DateUtils.getMediumDateString( startDate )
            + "' AND psi.duedate <= '" + DateUtils.getMediumDateString( endDate ) + "' "
            + " AND psi.organisationunitid in " + splitListHelper ( orgunitIds ) + " ";

        Iterator<Integer> keys = searchingKeys.keySet().iterator();
        boolean index = false;
        while ( keys.hasNext() )
        {
            Integer dataElementId = keys.next();

            if ( index )
            {
                condition += " AND psi.programstageinstanceid in ( " + sql + " WHERE 1=1 ";
            }

            condition += " AND pdv.dataElementid=" + dataElementId + " AND lower(pdv.value) ";

            String compareValue = searchingKeys.get( dataElementId ).toLowerCase();

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

        if ( isCount )
        {
            return "select count(psi.programstageinstanceid) from patientdatavalue pdv "
                + "inner join programstageinstance psi on pdv.programstageinstanceid=psi.programstageinstanceid "
                + condition;
        }

        return (sql + condition);
    }

    private List<Integer> executeSQL( String sql )
    {
        StatementHolder holder = statementManager.getHolder();

        List<Integer> ids = new ArrayList<Integer>();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet resultSet = statement.executeQuery( sql );

            while ( resultSet.next() )
            {
                int id = resultSet.getInt( 1 );

                ids.add( id );
            }

            return ids;

        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            return new ArrayList<Integer>();
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

}
