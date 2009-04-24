package org.hisp.dhis.databrowser.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.hisp.dhis.databrowser.DataBrowserStore;
import org.hisp.dhis.databrowser.DataBrowserTable;
import org.hisp.dhis.jdbc.StatementHolder;
import org.hisp.dhis.jdbc.StatementManager;
import org.hisp.dhis.system.util.TimeUtils;

/**
 * @author joakibj, martinwa
 * @version $Id$
 */
public class StatementManagerDataBrowserStore
    implements DataBrowserStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    // -------------------------------------------------------------------------
    // DataBrowserStore implementation
    // -------------------------------------------------------------------------

    public DataBrowserTable getDataSetsInPeriod( List<Integer> betweenPeriodIds )
    {
        StatementHolder holder = statementManager.getHolder();

        DataBrowserTable table = null;

        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "SELECT d.datasetid AS ID, d.name AS DataSet, count(*) AS Count " );
            sqlsb.append( "FROM datavalue dv " );
            sqlsb.append( "JOIN datasetmembers dsm ON (dv.dataelementid = dsm.dataelementid) JOIN dataset d ON (d.datasetid = dsm.datasetid) " );
            sqlsb.append( "WHERE dv.periodid IN " + splitListHelper( betweenPeriodIds ) + " " );
            sqlsb.append( "GROUP BY d.datasetid, d.name " );
            sqlsb.append( "ORDER BY Count DESC;" );

            String sql = sqlsb.toString();

            table = new DataBrowserTable();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();
            table.addColumnName( "DataSet" );
            table.addColumnName( "Count" );
            table.createStructure( resultSet );
            table.addColumnToAllRows( resultSet );
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( "Failed to get aggregated data value", e );
        }
        finally
        {
            holder.close();
        }
        return table;

    }

    public void setDataElementStructureForDataSetBetweenPeriods( DataBrowserTable table, Integer dataSetId,
        List<Integer> betweenPeriods )
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "SELECT de.dataelementid, de.name AS Name " );
            sqlsb.append( "FROM dataelement de JOIN datavalue dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "JOIN datasetmembers dsm ON (de.dataelementid = dsm.dataelementid) " );
            sqlsb.append( "WHERE dsm.datasetid = " + dataSetId + " AND dv.periodid IN "
                + splitListHelper( betweenPeriods ) + " " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name " );
            sqlsb.append( "ORDER BY de.name " );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.addQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();
            table.incrementQueryCount();

            table.createStructure( resultSet );
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( "Failed to get aggregated data value", e );
        }
        finally
        {
            holder.close();
        }

    }

    public Integer setCountDataElementsInOnePeriod( DataBrowserTable table, Integer dataSetId, Integer periodId )
    {
        StatementHolder holder = statementManager.getHolder();

        Integer numResults = 0;
        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "SELECT de.dataelementid, de.name AS DataElement, count(*) AS Count " );
            sqlsb.append( "FROM dataelement de JOIN datavalue dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "JOIN datasetmembers dsm ON (de.dataelementid = dsm.dataelementid) " );
            sqlsb.append( "WHERE dsm.datasetid = " + dataSetId + " AND dv.periodid = " + periodId + " " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name " );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.addQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();

            numResults = table.addColumnToAllRows( resultSet );
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( "Failed to get aggregated data value", e );
        }
        finally
        {
            holder.close();
        }

        return numResults;

    }

    public void setStructureForOrgUnitBetweenPeriods( DataBrowserTable table, Integer orgUnitParent,
        List<Integer> betweenPeriods )
    {
        StatementHolder holder = statementManager.getHolder();
        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "SELECT o.organisationunitid, o.name AS OrganisationUnit " );
            sqlsb.append( "FROM organisationunit o JOIN datavalue dv ON (o.organisationunitid = dv.sourceid) " );
            sqlsb.append( "WHERE o.parentid = " + orgUnitParent + " AND dv.periodid IN "
                + splitListHelper( betweenPeriods ) + " " );

            sqlsb.append( "GROUP BY o.organisationunitid, o.name " );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.addQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();

            table.createStructure( resultSet );
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( "Failed to get aggregated data value", e );
        }
        finally
        {
            holder.close();
        }

    }

    public Integer setCountOrgUnitsInOnePeriod( DataBrowserTable table, Integer orgUnitParent, Integer periodId )
    {
        StatementHolder holder = statementManager.getHolder();

        Integer numResults = 0;
        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "SELECT o.organisationunitid, o.name AS OrganisationUnit, count(*) AS Count " );
            sqlsb.append( "FROM organisationunit o JOIN datavalue dv ON (o.organisationunitid = dv.sourceid) " );
            sqlsb.append( "WHERE o.parentid = " + orgUnitParent + " AND dv.periodid = " + periodId + " " );
            sqlsb.append( "GROUP BY o.organisationunitid, o.name " );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.addQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();

            numResults = table.addColumnToAllRows( resultSet );
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( "Failed to get aggregated data value", e );
        }
        finally
        {
            holder.close();
        }

        return numResults;

    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String splitListHelper( List<Integer> list )
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

    /**
     * Uses statementmanager to obtain a scrollable, read-only ResultSet based
     * on the query string.
     * 
     * @param sql the query
     * @param holder the StatementHolder object
     * @return null or the ResultSet
     */
    private ResultSet getScrollableResult( String sql, StatementHolder holder )
        throws SQLException
    {
        Connection con = holder.getConnection();
        Statement stm = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
        stm.execute( sql );
        return stm.getResultSet();
    }
}