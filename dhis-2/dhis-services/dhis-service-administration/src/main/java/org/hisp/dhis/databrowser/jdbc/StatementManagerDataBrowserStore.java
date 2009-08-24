package org.hisp.dhis.databrowser.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.databrowser.DataBrowserStore;
import org.hisp.dhis.databrowser.DataBrowserTable;
import org.hisp.dhis.system.util.TimeUtils;

/**
 * @author joakibj, martinwa, briane, eivinhb
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
    
    public DataBrowserTable getDataSetsBetweenPeriods( List<Integer> betweenPeriodIds )
    {
        StatementHolder holder = statementManager.getHolder();

        DataBrowserTable table = null;

        // Gets all the dataSets in a period with a count attached to the
        // dataSet. The table returned has only 2 columns. They are created here
        // in this method directly
        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "SELECT d.datasetid AS ID, d.name AS DataSet, count(*) AS Count " );
            sqlsb.append( "FROM datavalue dv " );
            sqlsb.append( "JOIN datasetmembers dsm ON (dv.dataelementid = dsm.dataelementid) " );
            sqlsb.append( "JOIN dataset d ON (d.datasetid = dsm.datasetid) " );
            sqlsb.append( "WHERE dv.periodid IN " + splitListHelper( betweenPeriodIds ) + " " );
            sqlsb.append( "GROUP BY d.datasetid, d.name " );
            sqlsb.append( "ORDER BY Count DESC" );

            String sql = sqlsb.toString();

            table = new DataBrowserTable();
            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();

            // Create the column names.
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

    public DataBrowserTable getDataElementGroupsBetweenPeriods( List<Integer> betweenPeriodIds )
    {
        StatementHolder holder = statementManager.getHolder();

        DataBrowserTable table = null;

        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "SELECT d.dataelementgroupid AS ID, d.name AS DataElementGroup, count(*) AS Count " );
            sqlsb.append( "FROM datavalue dv " );
            sqlsb
                .append( "JOIN dataelementgroupmembers degm ON (dv.dataelementid = degm.dataelementid) JOIN dataelementgroup d ON (d.dataelementgroupid = degm.dataelementgroupid) " );
            sqlsb.append( "WHERE dv.periodid IN " + splitListHelper( betweenPeriodIds ) + " " );
            sqlsb.append( "GROUP BY d.dataelementgroupid, d.name " );
            sqlsb.append( "ORDER BY Count DESC;" );

            String sql = sqlsb.toString();

            table = new DataBrowserTable();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();
            table.addColumnName( "DataElementGroup" );
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

    public DataBrowserTable getOrgUnitGroupsBetweenPeriods( List<Integer> betweenPeriodIds )
    {
        StatementHolder holder = statementManager.getHolder();

        DataBrowserTable table = null;
        String sql = "";
        
        try
        {
            StringBuffer sqlsb = new StringBuffer();

            sqlsb.append( "SELECT oug.orgunitgroupid, oug.name, Count(*) as ant " );
            sqlsb.append( "FROM orgunitgroup oug  " );
            sqlsb.append( "Join orgunitgroupmembers ougm ON oug.orgunitgroupid = ougm.orgunitgroupid " );
            sqlsb.append( "Join organisationunit ou ON  ougm.organisationunitid = ou.organisationunitid " );
            sqlsb.append( "Join datavalue dv ON ou.organisationunitid = dv.sourceid " );
            sqlsb.append( "WHERE dv.periodid IN " + splitListHelper( betweenPeriodIds ) + " " );
            sqlsb.append( "GROUP BY oug.orgunitgroupid, oug.name ORDER BY ant desc " );

            sql = sqlsb.toString();

            table = new DataBrowserTable();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();
            table.addColumnName( "OrgUnitGroup" );
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
            table.addColumnName( "DataElement" );
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

    public void setDataElementGroupStructureForOrgUnitGroupBetweenPeriods( DataBrowserTable table,
        Integer orgUnitGroupId, List<Integer> betweenPeriods )
    {
        StatementHolder holder = statementManager.getHolder();

        StringBuffer sqlsb = new StringBuffer();
        String sql = "";       
        
        try
        {
            sqlsb.append( "SELECT deg.dataelementgroupid, deg.name AS DataElementGroup " );
            sqlsb.append( "FROM dataelementgroup deg " );
            sqlsb.append( "Join dataelementgroupmembers degm ON deg.dataelementgroupid = degm.dataelementgroupid " );
            sqlsb.append( "Join datavalue dv ON degm.dataelementid = dv.dataelementid " );
            sqlsb.append( "Join organisationunit ou ON dv.sourceid = ou.organisationunitid " );
            sqlsb.append( "Join orgunitgroupmembers ougm ON ou.organisationunitid = ougm.organisationunitid " );
            sqlsb.append( "WHERE ougm.orgunitgroupid =  '" + orgUnitGroupId + "' AND dv.periodid IN "
                + splitListHelper( betweenPeriods ) + " " );
            sqlsb.append( "GROUP BY deg.dataelementgroupid, deg.name " );
            sqlsb.append( "ORDER BY deg.name ASC " );

            sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();
            table.addColumnName( "DataElementGroup" );
            table.createStructure( resultSet );
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( "Failed to get aggregated data value - " + sql, e );
        }
        finally
        {
            holder.close();
        }
    }

    public void setDataElementStructureForDataElementGroupBetweenPeriods( DataBrowserTable table,
        Integer dataElementGroupId, List<Integer> betweenPeriods )
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "SELECT de.dataelementid, de.name AS Name " );
            sqlsb.append( "FROM dataelement de JOIN datavalue dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "JOIN dataelementgroupmembers degm ON (de.dataelementid = degm.dataelementid) " );
            sqlsb.append( "WHERE degm.dataelementgroupid = " + dataElementGroupId + " AND dv.periodid IN "
                + splitListHelper( betweenPeriods ) + " " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name " );
            sqlsb.append( "ORDER BY de.name " );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );

            table.addQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();
            table.incrementQueryCount();

            table.addColumnName( "DataElement" );
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

    public void setStructureForOrgUnitBetweenPeriods( DataBrowserTable table, Integer orgUnitParent,
        List<Integer> betweenPeriods )
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "SELECT o.organisationunitid, o.name AS OrganisationUnit " );
            sqlsb.append( "FROM organisationunit o " );
            sqlsb.append( "WHERE o.parentid = " + orgUnitParent + "" );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.addQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();

            table.createStructure( resultSet );

            table.addColumnName( "OrganisationUnit" );
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

    public void setDataElementStructureForOrgUnitBetweenPeriods( DataBrowserTable table, Integer orgUnitId, List<Integer> betweenPeriods)
    {
        StatementHolder holder = statementManager.getHolder();
        
        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "SELECT de.dataelementid, de.name AS DataElementGroup " );
            sqlsb.append( "FROM dataelement AS de " );
            sqlsb.append( "Inner Join datavalue AS dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "Inner Join datasetmembers AS dsm ON (de.dataelementid = dsm.dataelementid) " );
            sqlsb.append( "Inner Join organisationunit ON dv.sourceid = organisationunit.organisationunitid " );
            sqlsb.append( "WHERE organisationunit.organisationunitid = " + orgUnitId + " " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name " );
            sqlsb.append( "ORDER BY de.name " );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();
            table.addColumnName("DataElement");
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
    
    public Integer setCountDataElementsForDataSetBetweenPeriods( DataBrowserTable table, Integer dataSetId, List<Integer> betweenPeriodIds)
    {
        StatementHolder holder = statementManager.getHolder();
        
        // Here we uses a for loop to create one big sql statement using UNION.
        // This is done because the count and GROUP BY parts of this query can't
        // be done in another way. The alternative to this method is to actually
        // query the database as many time than betweenPeriodIds.size() tells.
        // But the overhead cost of doing that is bigger than the creation of
        // this UNION query.
        Integer numResults = 0;
        StringBuffer sqlsb = new StringBuffer();

        int i = 0;
        for ( Integer periodid : betweenPeriodIds )
        {
            i++;

            sqlsb.append( "(SELECT de.dataelementid, de.name AS DataElement, count(*) AS Count, p.periodid AS PeriodId, p.startDate AS ColumnHeader " );
            sqlsb.append( "FROM dataelement de JOIN datavalue dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "JOIN datasetmembers dsm ON (de.dataelementid = dsm.dataelementid) " );
            sqlsb.append( "JOIN period p ON (dv.periodid = p.periodid) " );
            sqlsb.append( "WHERE dsm.datasetid = " + dataSetId + " AND dv.periodid = " + periodid + " " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name, p.periodid, p.startDate )" );

            if ( i == betweenPeriodIds.size() )
            	sqlsb.append( "ORDER BY PeriodId ");
            else
            	sqlsb.append( " UNION ");
        }
        
        try
        {
            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sqlsb.toString(), holder );
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

    public Integer setCountDataElementsForDataElementGroupBetweenPeriods( DataBrowserTable table,
        Integer dataElementGroupId, List<Integer> betweenPeriodIds )
    {
        StatementHolder holder = statementManager.getHolder();

        Integer numResults = 0;
        StringBuffer sqlsb = new StringBuffer();

        int i = 0;
        for ( Integer periodid : betweenPeriodIds )
        {
            i++;
            
            sqlsb
                .append( "(SELECT de.dataelementid, de.name AS DataElement, count(*) AS Count, p.periodid AS PeriodId, p.startDate AS ColumnHeader " );
            sqlsb.append( "FROM dataelement de JOIN datavalue dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "JOIN dataelementgroupmembers degm ON (de.dataelementid = degm.dataelementid) " );
            sqlsb.append( "JOIN period p ON (dv.periodid = p.periodid) " );
            sqlsb.append( "WHERE degm.dataelementgroupid = " + dataElementGroupId + " AND dv.periodid = " + periodid
                + " " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name, p.periodid, p.startDate) " );
            
            if ( i == betweenPeriodIds.size() )
                sqlsb.append( "ORDER BY PeriodId " );
            else
                sqlsb.append( " UNION " );
        }

        try
        {
            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sqlsb.toString(), holder );
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
    
    public Integer setCountDataElementGroupsForOrgUnitGroupBetweenPeriods( DataBrowserTable table,
        Integer orgUnitGroupId, List<Integer> betweenPeriodIds )
    {
        StatementHolder holder = statementManager.getHolder();

        Integer numResults = 0;
        StringBuffer sqlsb = new StringBuffer();
        
        int i = 0;
        for ( Integer periodid : betweenPeriodIds )
        {
            i++;

            sqlsb
                .append( " (SELECT deg.dataelementgroupid, deg.name, Count(*) AS Count, p.periodid AS PeriodId, p.startdate AS ColumnHeader " );
            sqlsb.append( "FROM dataelementgroup AS deg " );
            sqlsb
                .append( "Inner Join dataelementgroupmembers AS degm ON deg.dataelementgroupid = degm.dataelementgroupid " );
            sqlsb.append( "Inner Join datavalue AS dv ON degm.dataelementid = dv.dataelementid " );
            sqlsb.append( "Inner Join period AS p ON dv.periodid = p.periodid " );
            sqlsb.append( "Inner Join organisationunit AS ou ON dv.sourceid = ou.organisationunitid " );
            sqlsb.append( "Inner Join orgunitgroupmembers AS ougm ON ou.organisationunitid = ougm.organisationunitid " );
            sqlsb
                .append( "WHERE p.periodid =  '" + periodid + "' AND ougm.orgunitgroupid =  '" + orgUnitGroupId + "' " );
            sqlsb.append( "GROUP BY deg.dataelementgroupid,deg.name,p.periodid,p.startdate) " );

            if ( i == betweenPeriodIds.size() )
                sqlsb.append( "ORDER BY PeriodId " );
            else
                sqlsb.append( "\n UNION \n" );
        }

        try
        {
            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sqlsb.toString(), holder );
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

    public Integer setCountOrgUnitsBetweenPeriods( DataBrowserTable table, Integer orgUnitParent,
        List<Integer> betweenPeriodIds )
    {
        StatementHolder holder = statementManager.getHolder();

        Integer numResults = 0;
        StringBuffer sqlsb = new StringBuffer();

        int i = 0;
        for ( Integer periodid : betweenPeriodIds )
        {
            i++;
        
            sqlsb
                .append( "(SELECT o.organisationunitid, o.name AS OrganisationUnit, count(*) AS Count, p.periodid AS PeriodId, p.startDate AS ColumnHeader " );
            sqlsb.append( "FROM organisationunit o JOIN datavalue dv ON (o.organisationunitid = dv.sourceid) " );
            sqlsb.append( "JOIN period p ON (dv.periodid = p.periodid) " );
            sqlsb.append( "WHERE o.parentid = " + orgUnitParent + " AND dv.periodid = " + periodid + " " );
            sqlsb.append( "GROUP BY o.organisationunitid, o.name, p.periodid, p.startDate ) " );
            
            if ( i == betweenPeriodIds.size() )
                sqlsb.append( "ORDER BY PeriodId " );
            else
                sqlsb.append( " UNION " );
        }

        try
        {
            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sqlsb.toString(), holder );
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

    public Integer setCountDataElementsForOrgUnitBetweenPeriods( DataBrowserTable table, Integer orgUnitId,
        List<Integer> betweenPeriodIds )
    {
        StatementHolder holder = statementManager.getHolder();

        Integer numResults = 0;
        StringBuffer sqlsb = new StringBuffer();

        int i = 0;
        for ( Integer periodid : betweenPeriodIds )
        {
            i++;
            
            sqlsb
                .append( "(SELECT de.dataelementid, de.name AS DataElementGroup, count(*) AS Count, p.periodid AS PeriodId, p.startDate AS ColumnHeader " );
            sqlsb.append( "FROM dataelement AS de " );
            sqlsb.append( "Inner Join datavalue AS dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "Inner Join datasetmembers AS dsm ON (de.dataelementid = dsm.dataelementid) " );
            sqlsb.append( "Inner Join organisationunit ON dv.sourceid = organisationunit.organisationunitid " );
            sqlsb.append( "JOIN period p ON (dv.periodid = p.periodid) " );
            sqlsb.append( "WHERE organisationunit.organisationunitid = " + orgUnitId + " AND " );
            sqlsb.append( "dv.periodid = " + periodid + " " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name, p.periodid, p.startDate ) " );
            
            if ( i == betweenPeriodIds.size() )
                sqlsb.append( "ORDER BY PeriodId " );
            else
                sqlsb.append( " UNION " );
        }

        try
        {
            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sqlsb.toString(), holder );
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

    /**
     * Splits a list of integers by by comma. Use this method if you have a 
     * list that will be used in f.ins. a WHERE xxx IN (list) clause in SQL.
     * 
     * @param List<Integer> list of Integers 
     * @return the list as a string splitted by a comma. 
     */
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
     * Uses StatementManager to obtain a scrollable, read-only ResultSet based
     * on the query string.
     * 
     * @param sql the query
     * @param holder the StatementHolder object
     * @return null or the ResultSet
     */
    private ResultSet getScrollableResult( String sql, StatementHolder holder )
        throws SQLException
    {
        // The current code for building the DataBrowserTable requires that a
        // scrollable ResultSet must be used for PostgreSQL.
        Connection con = holder.getConnection();
        Statement stm = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
        stm.execute( sql );
        return stm.getResultSet();
    }
}
