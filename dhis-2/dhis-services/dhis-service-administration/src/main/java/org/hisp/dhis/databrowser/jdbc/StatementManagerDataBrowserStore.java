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
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.util.TimeUtils;

/**
 * @author joakibj, martinwa, briane, eivinhb
 * @version $Id StatementManagerDataBrowserStore.java 2010-04-06 Jason
 *          Pickering, Dang Duy Hieu$
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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
            sqlsb.append( "(SELECT d.datasetid AS ID, d.name AS DataSet, COUNT(*) AS counts_of_aggregated_values " );
            sqlsb.append( "FROM datavalue dv " );
            sqlsb.append( "JOIN datasetmembers dsm ON (dv.dataelementid = dsm.dataelementid) " );
            sqlsb.append( "JOIN dataset d ON (d.datasetid = dsm.datasetid) " );
            sqlsb.append( "WHERE dv.periodid IN " + splitListHelper( betweenPeriodIds ) + " " );
            sqlsb.append( "GROUP BY d.datasetid, d.name " );
            sqlsb.append( "ORDER BY counts_of_aggregated_values DESC)" );

            String sql = sqlsb.toString();

            table = new DataBrowserTable();
            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();

            // Create the column names.
            table.addColumnName( "drilldown_data_set" );
            table.addColumnName( "counts_of_aggregated_values" );
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
            sqlsb
                .append( "(SELECT d.dataelementgroupid AS ID, d.name AS DataElementGroup, COUNT(*) AS counts_of_aggregated_values " );
            sqlsb.append( "FROM datavalue dv " );
            sqlsb.append( "JOIN dataelementgroupmembers degm ON (dv.dataelementid = degm.dataelementid)" );
            sqlsb.append( "JOIN dataelementgroup d ON (d.dataelementgroupid = degm.dataelementgroupid) " );
            sqlsb.append( "WHERE dv.periodid IN " + splitListHelper( betweenPeriodIds ) + " " );
            sqlsb.append( "GROUP BY d.dataelementgroupid, d.name " );
            sqlsb.append( "ORDER BY counts_of_aggregated_values DESC)" );

            String sql = sqlsb.toString();

            table = new DataBrowserTable();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();
            table.addColumnName( "drilldown_data_element_group" );
            table.addColumnName( "counts_of_aggregated_values" );
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

            sqlsb
                .append( "(SELECT oug.orgunitgroupid, oug.name AS OrgUnitGroup, COUNT(*) AS counts_of_aggregated_values " );
            sqlsb.append( "FROM orgunitgroup oug " );
            sqlsb.append( "JOIN orgunitgroupmembers ougm ON oug.orgunitgroupid = ougm.orgunitgroupid " );
            sqlsb.append( "JOIN organisationunit ou ON  ougm.organisationunitid = ou.organisationunitid " );
            sqlsb.append( "JOIN datavalue dv ON ou.organisationunitid = dv.sourceid " );
            sqlsb.append( "WHERE dv.periodid IN " + splitListHelper( betweenPeriodIds ) + " " );
            sqlsb.append( "GROUP BY oug.orgunitgroupid, oug.name " );
            sqlsb.append( "ORDER BY counts_of_aggregated_values DESC) " );

            sql = sqlsb.toString();

            table = new DataBrowserTable();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();
            table.addColumnName( "drilldown_orgunit_group" );
            table.addColumnName( "counts_of_aggregated_values" );
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
            sqlsb.append( "(SELECT de.dataelementid, de.name AS DataElement " );
            sqlsb.append( "FROM dataelement de JOIN datavalue dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "JOIN datasetmembers dsm ON (de.dataelementid = dsm.dataelementid) " );
            sqlsb.append( "WHERE dsm.datasetid = '" + dataSetId + "' " );
            sqlsb.append( "AND dv.periodid IN " + splitListHelper( betweenPeriods ) + " " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name " );
            sqlsb.append( "ORDER BY de.name) " );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.addQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();
            table.incrementQueryCount();

            table.createStructure( resultSet );
            table.addColumnName( "drilldown_data_element" );
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
            sqlsb.append( "(SELECT deg.dataelementgroupid, deg.name AS DataElementGroup " );
            sqlsb.append( "FROM dataelementgroup deg " );
            sqlsb.append( "JOIN dataelementgroupmembers degm ON deg.dataelementgroupid = degm.dataelementgroupid " );
            sqlsb.append( "JOIN datavalue dv ON degm.dataelementid = dv.dataelementid " );
            sqlsb.append( "JOIN organisationunit ou ON dv.sourceid = ou.organisationunitid " );
            sqlsb.append( "JOIN orgunitgroupmembers ougm ON ou.organisationunitid = ougm.organisationunitid " );
            sqlsb.append( "WHERE ougm.orgunitgroupid = '" + orgUnitGroupId + "' " );
            sqlsb.append( "AND dv.periodid IN " + splitListHelper( betweenPeriods ) + " " );
            sqlsb.append( "GROUP BY deg.dataelementgroupid, deg.name " );
            sqlsb.append( "ORDER BY deg.name ASC) " );

            sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();
            table.addColumnName( "drilldown_data_element_group" );
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
            sqlsb.append( "(SELECT de.dataelementid, de.name AS DataElement " );
            sqlsb.append( "FROM dataelement de JOIN datavalue dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "JOIN dataelementgroupmembers degm ON (de.dataelementid = degm.dataelementid) " );
            sqlsb.append( "WHERE degm.dataelementgroupid = '" + dataElementGroupId + "' " );
            sqlsb.append( "AND dv.periodid IN " + splitListHelper( betweenPeriods ) + " " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name " );
            sqlsb.append( "ORDER BY de.name) " );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );

            table.addQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();
            table.incrementQueryCount();

            table.addColumnName( "drilldown_data_element" );
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
            sqlsb.append( "(SELECT o.organisationunitid, o.name AS OrganisationUnit " );
            sqlsb.append( "FROM organisationunit o " );
            sqlsb.append( "WHERE o.parentid = '" + orgUnitParent + "' " );
            sqlsb.append( "ORDER BY o.name)" );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );
            table.addQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();

            table.createStructure( resultSet );

            table.addColumnName( "drilldown_organisation_unit" );
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

    public void setDataElementStructureForOrgUnitBetweenPeriods( DataBrowserTable table, Integer orgUnitId,
        List<Integer> betweenPeriods )
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            StringBuffer sqlsb = new StringBuffer();
            sqlsb.append( "(SELECT de.dataelementid, de.name AS DataElement " );
            sqlsb.append( "FROM dataelement AS de " );
            sqlsb.append( "INNER JOIN datavalue AS dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "INNER JOIN datasetmembers AS dsm ON (de.dataelementid = dsm.dataelementid) " );
            sqlsb.append( "INNER JOIN organisationunit AS o ON (dv.sourceid = o.organisationunitid) " );
            sqlsb.append( "WHERE o.organisationunitid = '" + orgUnitId + "' " );
            sqlsb.append( "AND dv.periodid IN " + splitListHelper( betweenPeriods ) + " " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name " );
            sqlsb.append( "ORDER BY de.name) " );

            String sql = sqlsb.toString();

            TimeUtils.start();
            ResultSet resultSet = getScrollableResult( sql, holder );

            table.setQueryTime( TimeUtils.getMillis() );
            TimeUtils.stop();

            table.incrementQueryCount();
            table.addColumnName( "drilldown_data_element" );
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

    public Integer setCountDataElementsForDataSetBetweenPeriods( DataBrowserTable table, Integer dataSetId,
        List<Integer> betweenPeriodIds )
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
        for ( Integer periodId : betweenPeriodIds )
        {
            i++;

            sqlsb
                .append( "(SELECT de.dataelementid, de.name AS DataElement, Count(dv.value) AS counts_of_aggregated_values, p.periodid AS PeriodId, p.startDate AS ColumnHeader " );
            sqlsb.append( "FROM dataelement de JOIN datavalue dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "JOIN datasetmembers dsm ON (de.dataelementid = dsm.dataelementid) " );
            sqlsb.append( "JOIN period p ON (dv.periodid = p.periodid) " );
            sqlsb.append( "WHERE dsm.datasetid = '" + dataSetId + "' AND dv.periodid = '" + periodId + "' " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name, p.periodid, p.startDate)" );

            sqlsb.append( i == betweenPeriodIds.size() ? "ORDER BY PeriodId " : "\n UNION \n" );
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
                .append( "(SELECT de.dataelementid, de.name AS DataElement, COUNT(dv.value) AS counts_of_aggregated_values, p.periodid AS PeriodId, p.startDate AS ColumnHeader " );
            sqlsb.append( "FROM dataelement de JOIN datavalue dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "JOIN dataelementgroupmembers degm ON (de.dataelementid = degm.dataelementid) " );
            sqlsb.append( "JOIN period p ON (dv.periodid = p.periodid) " );
            sqlsb.append( "WHERE degm.dataelementgroupid = '" + dataElementGroupId + "' " );
            sqlsb.append( "AND dv.periodid = '" + periodid + "' " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name, p.periodid, p.startDate) " );

            sqlsb.append( i == betweenPeriodIds.size() ? "ORDER BY PeriodId " : "\n UNION \n" );
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
                .append( " (SELECT deg.dataelementgroupid, deg.name, COUNT(dv.value) AS counts_of_aggregated_values, p.periodid AS PeriodId, p.startdate AS ColumnHeader " );
            sqlsb.append( "FROM dataelementgroup AS deg " );
            sqlsb
                .append( "INNER JOIN dataelementgroupmembers AS degm ON deg.dataelementgroupid = degm.dataelementgroupid " );
            sqlsb.append( "INNER JOIN datavalue AS dv ON degm.dataelementid = dv.dataelementid " );
            sqlsb.append( "INNER JOIN period AS p ON dv.periodid = p.periodid " );
            sqlsb.append( "INNER JOIN organisationunit AS ou ON dv.sourceid = ou.organisationunitid " );
            sqlsb.append( "INNER JOIN orgunitgroupmembers AS ougm ON ou.organisationunitid = ougm.organisationunitid " );
            sqlsb
                .append( "WHERE p.periodid =  '" + periodid + "' AND ougm.orgunitgroupid =  '" + orgUnitGroupId + "' " );
            sqlsb.append( "GROUP BY deg.dataelementgroupid,deg.name,p.periodid,p.startdate) " );

            sqlsb.append( i == betweenPeriodIds.size() ? "ORDER BY PeriodId " : "\n UNION \n" );
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
        List<Integer> betweenPeriodIds, Integer maxLevel )
    {
        StatementHolder holder = statementManager.getHolder();

        Integer numResults = 0;
        StringBuffer sqlsbDescentdants = new StringBuffer();

        this.setUpQueryForDrillDownDescendants( sqlsbDescentdants, orgUnitParent, betweenPeriodIds, maxLevel );

        try
        {
            TimeUtils.start();

            ResultSet resultSet = this.getScrollableResult( sqlsbDescentdants.toString(), holder );

            table.addQueryTime( TimeUtils.getMillis() );
            table.incrementQueryCount();

            numResults = table.addColumnToAllRows( resultSet );

            TimeUtils.stop();
        }
        catch ( Exception e )
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
        for ( Integer periodId : betweenPeriodIds )
        {
            i++;

            sqlsb
                .append( "(SELECT de.dataelementid, de.name AS DataElement, Count(dv.value) AS counts_of_aggregated_values, p.periodid AS PeriodId, p.startDate AS ColumnHeader " );
            sqlsb.append( "FROM dataelement AS de " );
            sqlsb.append( "INNER JOIN datavalue AS dv ON (de.dataelementid = dv.dataelementid) " );
            sqlsb.append( "INNER JOIN organisationunit As o ON (dv.sourceid = o.organisationunitid) " );
            sqlsb.append( "JOIN period p ON (dv.periodid = p.periodid) " );
            sqlsb.append( "WHERE o.organisationunitid = '" + orgUnitId + "' " );
            sqlsb.append( "AND dv.periodid = '" + periodId + "' " );
            sqlsb.append( "GROUP BY de.dataelementid, de.name, p.periodid, p.startDate)" );

            sqlsb.append( i == betweenPeriodIds.size() ? "ORDER BY PeriodId " : "\n UNION \n" );
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
     * Splits a list of integers by by comma. Use this method if you have a list
     * that will be used in f.ins. a WHERE xxx IN (list) clause in SQL.
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
        Connection con = holder.getConnection();
        Statement stm = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
        stm.execute( sql );
        return stm.getResultSet();
    }

    private String setUpQueryForDrillDownDescendants( StringBuffer sb, Integer orgUnitSelected,
        List<Integer> betweenPeriodIds, Integer maxLevel )
    {
        if ( maxLevel == null )
        {
            maxLevel = organisationUnitService.getMaxOfOrganisationUnitLevels();
        }

        int curLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnitSelected );
        int loopSize = betweenPeriodIds.size();

        String descendantQuery = this.setUpQueryGetDescendants( curLevel, maxLevel, orgUnitSelected );
        int i = 0;

        for ( Integer periodid : betweenPeriodIds )
        {
            i++;
            /**
             * Get all descendant level data for all orgunits under the
             * selected, grouped by the next immediate children of the selected
             * orgunit Looping through each period UNION construct appears to be
             * faster with an index placed on periodid's rather than joining on
             * periodids and then performing the aggregation step.
             * 
             */
            sb.append( " SELECT a.parentid,a.name AS organisationunit,COUNT(*),p.periodid,p.startdate AS columnheader" );
            sb.append( " FROM datavalue dv" );
            sb.append( " INNER JOIN (SELECT DISTINCT x.parentid,x.childid,ou.name FROM(" + descendantQuery + ")x" );
            sb.append( " INNER JOIN organisationunit ou ON x.parentid=ou.organisationunitid) a ON dv.sourceid=a.childid" );
            sb.append( " INNER JOIN period p ON dv.periodid=p.periodid" );
            sb.append( " WHERE dv.periodid=" + periodid );
            sb.append( " GROUP BY a.parentid,a.name,p.periodid,p.startdate" );
            sb.append( i < loopSize ? " UNION " : "" );

        }
        sb.append( " ORDER BY columnheader,organisationunit" );

        return sb.toString();
    }

    private String setUpQueryGetDescendants( int curLevel, int maxLevel, Integer orgUnitSelected )
    {
        Integer childLevel = curLevel + 1;
        Integer diffLevel = maxLevel - curLevel;

        // The immediate child level can probably be combined into the for loop
        // but we need to clarify whether the selected unit should be present,
        // and if so, how?

        final StringBuilder desc_query = new StringBuilder();

        // Loop through each of the descendants until the diff level is reached
        for ( int j = 0; j < diffLevel; j++ )
        {
            if ( j != 0 )
            {
                desc_query.append( " UNION " );
            }
            desc_query.append( "SELECT DISTINCT idlevel" + (childLevel) + " AS parentid," );
            desc_query.append( "idlevel" + (childLevel + j) + " AS childid" );
            desc_query.append( " FROM _orgunitstructure" );
            desc_query.append( " WHERE idlevel" + (curLevel) + "='" + orgUnitSelected + "'" );
            desc_query.append( " AND idlevel" + (childLevel + j) + "<>0" );
        }

        return desc_query.toString();
    }
}
