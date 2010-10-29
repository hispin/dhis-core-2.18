package org.hisp.dhis.sqlview.jdbc;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.sqlview.ResourceTableNameMap;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.sqlview.SqlViewExpandStore;
import org.hisp.dhis.sqlview.SqlViewJoinLib;
import org.hisp.dhis.sqlview.SqlViewTable;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Dang Duy Hieu
 * @version $Id JdbcSqlViewExpandStore.java July 06, 2010$
 */
public class JdbcSqlViewExpandStore
    implements SqlViewExpandStore
{
    private static final String PREFIX_CREATEVIEW_QUERY = "CREATE VIEW ";

    private static final String PREFIX_DROPVIEW_QUERY = "DROP VIEW IF EXISTS ";

    private static final String PREFIX_SELECT_QUERY = "SELECT * FROM ";

    private static final String PREFIX_VIEWNAME = "_view";

    private static final String ENDLINE = "<br/>";

    private static final String[] types = { "VIEW" };

    private static final Pattern p = Pattern.compile( "\\W" );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // Implementing methods
    // -------------------------------------------------------------------------

    @Override
    public Collection<String> getAllSqlViewNames()
    {
        final StatementHolder holder = statementManager.getHolder();
        DatabaseMetaData mtdt;
        Set<String> viewersName = new HashSet<String>();

        try
        {
            mtdt = holder.getConnection().getMetaData();

            ResultSet rs = mtdt.getTables( null, null, PREFIX_VIEWNAME + "%", types );

            while ( rs.next() )
            {
                viewersName.add( rs.getString( "TABLE_NAME" ) );
            }

        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            holder.close();
        }

        return viewersName;

    }

    @Override
    public Collection<String> getAllResourceProperties( String resourceTableName )
    {
        final StatementHolder holder = statementManager.getHolder();
        Set<String> propertiesName = new HashSet<String>();

        try
        {
            ResultSet rs = holder.getStatement().executeQuery(
                PREFIX_SELECT_QUERY + ResourceTableNameMap.getNameByAlias( resourceTableName ) + " LIMIT 1" );
            ResultSetMetaData rsmd = rs.getMetaData();

            int countCols = rsmd.getColumnCount();

            for ( int i = 1; i <= countCols; i++ )
            {
                propertiesName.add( rsmd.getColumnName( i ) );
            }

        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            holder.close();
        }

        return propertiesName;
    }

    @Override
    public boolean isViewTableExists( String viewTableName )
    {
        final StatementHolder holder = statementManager.getHolder();
        DatabaseMetaData mtdt;

        try
        {
            mtdt = holder.getConnection().getMetaData();
            ResultSet rs = mtdt.getTables( null, null, viewTableName.toLowerCase(), types );

            return rs.next();
        }
        catch ( Exception e )
        {
            return false;
        }
        finally
        {
            holder.close();
        }
    }

    @Override
    public boolean createView( SqlView sqlViewInstance )
    {
        String viewName = setUpViewTableName( sqlViewInstance.getName() );

        try
        {
            this.dropViewTable( viewName );

            jdbcTemplate.execute( PREFIX_CREATEVIEW_QUERY + viewName + " AS " + sqlViewInstance.getSqlQuery() );
        }
        catch ( BadSqlGrammarException bge )
        {
            return false;
        }

        return true;
    }

    @Override
    public void setUpDataSqlViewTable( SqlViewTable sqlViewTable, String viewTableName )
    {
        final StatementHolder holder = statementManager.getHolder();

        ResultSet rs;
        try
        {
            rs = this.getScrollableResult( PREFIX_SELECT_QUERY + viewTableName, holder );
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( "Failed to get data from view " + viewTableName, e );
        }

        sqlViewTable.createViewerStructure( rs );
        sqlViewTable.addRecord( rs );

        holder.close();
    }

    @Override
    public String setUpViewTableName( String input )
    {
        String[] items = p.split( input.trim().replaceAll( "_", "" ) );

        input = "";

        for ( String s : items )
        {
            input += (s.equals( "" ) == true) ? "" : ("_" + s);
        }

        return PREFIX_VIEWNAME + input;
    }

    @Override
    public String testSqlGrammar( String sql )
    {
        String errorMessage = "";

        try
        {
            jdbcTemplate.queryForList( sql );
        }
        catch ( BadSqlGrammarException bge )
        {
            errorMessage = setUpMessage( bge.getRootCause().toString() );
            return errorMessage;
        }

        return errorMessage;
    }

    @Override
    public void dropViewTable( String viewName )
    {
        final StatementHolder holder = statementManager.getHolder();

        try
        {
            holder.getStatement().executeUpdate( PREFIX_DROPVIEW_QUERY + viewName );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to drop view: " + viewName, ex );
        }
        finally
        {
            holder.close();
        }
    }

    @Override
    public String setUpJoinQuery( Collection<String> tableList )
    {
        String joinQuery = "";

        if ( tableList.size() == 2 )
        {
            // -----------------------------------------------------------------
            // CATEGORYOPTIONCOMBONAME
            // -----------------------------------------------------------------
            if ( tableList.contains( "_cocn" ) )
            {
                joinQuery += "_categoryoptioncomboname AS _cocn \n";

                if ( tableList.contains( "_cs" ) )
                {
                    // "COCN and CS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_CS;
                }
                else if ( tableList.contains( "_degss" ) )
                {
                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_ous" ) )
                {
                    // "COCN and OUS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_OUS;
                }
                else if ( tableList.contains( "_oustgss" ) )
                {
                    // "COCN and OUSTGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_OUSTGSS;
                }
            }

            // -----------------------------------------------------------------
            // ORGUNITSTRUCTURE
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_ous" ) )
            {
                joinQuery += "_orgunitstructure AS _ous \n";

                if ( tableList.contains( "_oustgss" ) )
                {
                    // "OUS and OUSTGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_OUSTGSS;
                }
                else if ( tableList.contains( "_cocn" ) )
                {
                    // "OUS and COCN"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_COCN;
                }
                else if ( tableList.contains( "_degss" ) )
                {
                    // "OUS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_cs" ) )
                {
                    // "OUS and COCN"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_COCN;

                    // "COCN and CS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_CS;
                }
            }

            // -----------------------------------------------------------------
            // _CATEGORYSTRUCTURE
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_cs" ) )
            {
                joinQuery += "_categorystructure AS _cs \n";

                // "COCN and CS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_CS;

                if ( tableList.contains( "_cocn" ) )
                {
                }
                else if ( tableList.contains( "_ous" ) )
                {
                    // "COCN and OUS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_OUS;
                }
                else if ( tableList.contains( "_degss" ) )
                {
                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_oustgss" ) )
                {
                    // "COCN and OUSTGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_OUSTGSS;
                }
            }

            // -----------------------------------------------------------------
            // _DATAELEMENTGROUPSETSTRUCTURE
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_degss" ) )
            {
                joinQuery += "_dataelementgroupsetstructure AS _degss \n";

                if ( tableList.contains( "_cocn" ) )
                {
                    // "DEGSS and COCN"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN;
                }
                else if ( tableList.contains( "_ous" ) )
                {
                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;
                }
                else if ( tableList.contains( "_cs" ) )
                {
                    // "DEGSS and COCN and CS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN + SqlViewJoinLib.COCN_JOIN_CS;
                }
                else if ( tableList.contains( "_oustgss" ) )
                {
                    // "DEGSS and OUSTGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUSTGSS;
                }
            }

            // -----------------------------------------------------------------
            // _ORGANISATIONUNITGROUPSETSTRUCTURE
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_oustgss" ) )
            {
                joinQuery += "_organisationunitgroupsetstructure AS _oustgss \n";

                if ( tableList.contains( "_cocn" ) || tableList.contains( "_cs" ) )
                {
                    // "OUSTGSS and COCN"
                    joinQuery += SqlViewJoinLib.OUSTGSS_JOIN_COCN;

                    if ( tableList.contains( "_cs" ) )
                    {
                        // "COCN and CS"
                        joinQuery += SqlViewJoinLib.COCN_JOIN_CS;
                    }
                }
                else if ( tableList.contains( "_degss" ) )
                {
                    // "OUSTGSS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUSTGSS_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_ous" ) )
                {
                    // "OUSTGSS and OUS"
                    joinQuery += SqlViewJoinLib.OUSTGSS_JOIN_OUS;
                }
            }
        }
        else if ( tableList.size() == 3 )
        {
            // -----------------------------------------------------------------
            // CategoryOptionComboname OrgUnitStructure
            // -----------------------------------------------------------------
            if ( tableList.contains( "_cocn" ) && tableList.contains( "_ous" ) )
            {
                joinQuery += "_categoryoptioncomboname AS _cocn \n";

                // "COCN and OUS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_OUS;

                if ( tableList.contains( "_degss" ) )
                {
                    // "DV and DEGSS"
                    joinQuery += SqlViewJoinLib.DV_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_oustgss" ) )
                {
                    // "OUS and OUSTGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_OUSTGSS;
                }
                else if ( tableList.contains( "_cs" ) )
                {
                    // "COCN and CS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_CS;
                }
            }

            // -----------------------------------------------------------------
            // CategoryOptionComboname _CategoryStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_cocn" ) && tableList.contains( "_cs" ) )
            {
                joinQuery += "_categorystructure AS _cs \n";

                // "CS and COCN"
                joinQuery += SqlViewJoinLib.CS_JOIN_COCN;

                if ( tableList.contains( "_degss" ) )
                {
                    // "DV and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_oustgss" ) )
                {
                    // "DV and OUSTGSS"
                    joinQuery += SqlViewJoinLib.DV_JOIN_OUSTGSS;
                }
            }

            // -----------------------------------------------------------------
            // CategoryOptionComboname _OrganisationUnitGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_cocn" ) && tableList.contains( "_oustgss" )
                && tableList.contains( "_degss" ) )
            {
                joinQuery += "_categoryoptioncomboname AS _cocn \n";

                // "COCN and OUSTGSS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_OUSTGSS;

                // "DV and DEGSS"
                joinQuery += SqlViewJoinLib.DV_JOIN_DEGSS;
            }

            // -----------------------------------------------------------------
            // OrgUnitStructure _CategoryStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_ous" ) && tableList.contains( "_cs" ) )
            {
                joinQuery += "_categorystructure AS _cs \n";

                // "CS and COCN"
                joinQuery += SqlViewJoinLib.CS_JOIN_COCN;

                // "COCN and OUS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_OUS;

                if ( tableList.contains( "_degss" ) )
                {
                    // "DV and DEGSS"
                    joinQuery += SqlViewJoinLib.DV_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_oustgss" ) )
                {
                    // "OUS and OUSTGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_OUSTGSS;
                }
            }

            // -----------------------------------------------------------------
            // OrgUnitStructure _DataelementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_ous" ) && tableList.contains( "_degss" ) && tableList.contains( "_oustgss" ) )
            {
                joinQuery += "_dataelementgroupsetstructure AS _degss \n";

                // "DEGSS and OUS"
                joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                // "OUS and OUSTGSS"
                joinQuery += SqlViewJoinLib.OUS_JOIN_OUSTGSS;

            }

            // -----------------------------------------------------------------
            // _CategoryStructure _DataelementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_cs" ) && tableList.contains( "_degss" ) && tableList.contains( "_oustgss" ) )
            {
                joinQuery += "_categorystructure AS _cs \n";

                // "CS and COCN and DEGSS"
                joinQuery += SqlViewJoinLib.CS_JOIN_COCN + SqlViewJoinLib.COCN_JOIN_DEGSS;

                // "DV and OUSTGSS"
                joinQuery += SqlViewJoinLib.DV_JOIN_OUSTGSS;

            }
        }
        else if ( tableList.size() == 4 )
        {
            // -----------------------------------------------------------------
            // _CategoryOptionComboname _OrgUnitStructure _CategoryStructure
            // -----------------------------------------------------------------
            if ( tableList.contains( "_cocn" ) && tableList.contains( "_ous" ) && tableList.contains( "_cs" ) )
            {
                if ( tableList.contains( "_degss" ) )
                {
                    joinQuery += "_categorystructure AS _cs \n";

                    // "CS and COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.CS_JOIN_COCN + SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DV and OUS"
                    joinQuery += SqlViewJoinLib.DV_JOIN_OUS;

                }
                else if ( tableList.contains( "_oustgss" ) )
                {
                    joinQuery += "_orgunitstructure AS _ous \n";

                    // "OUS and OUSTGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_OUSTGSS;

                    // "OUSTGSS and COCN"
                    joinQuery += SqlViewJoinLib.OUSTGSS_JOIN_COCN;

                    // "COCN and CS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_CS;
                }
            }

            // -----------------------------------------------------------------
            // _CategoryOptionComboname _OrgUnitStructure
            // _DataelementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_cocn" ) && tableList.contains( "_ous" ) && tableList.contains( "_degss" )
                && tableList.contains( "_oustgss" ) )
            {
                joinQuery += "_orgunitstructure AS _ous \n";

                // "OUS and OUSTGSS"
                joinQuery += SqlViewJoinLib.OUS_JOIN_OUSTGSS;

                // "OUSTGSS and DEGSS"
                joinQuery += SqlViewJoinLib.OUSTGSS_JOIN_DEGSS;

                // "DV and COCN"
                joinQuery += SqlViewJoinLib.DV_JOIN_COCN;

            }

            // -----------------------------------------------------------------
            // _CategoryOptionComboname _CategoryStructure
            // _DataelementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_cocn" ) && tableList.contains( "_cs" ) && tableList.contains( "_degss" )
                && tableList.contains( "_oustgss" ) )
            {
                joinQuery += "_categorystructure AS _cs \n";

                // "CS and COCN"
                joinQuery += SqlViewJoinLib.CS_JOIN_COCN;

                // "COCN and DEGSS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                // "DV and OUSTGSS
                joinQuery += SqlViewJoinLib.DV_JOIN_OUSTGSS;

            }

            // -----------------------------------------------------------------
            // _OrgunitStructure _DataelementGroupSetStructure
            // _OrganisationunitGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_ous" ) && tableList.contains( "_degss" ) && tableList.contains( "_oustgss" ) )
            {
                joinQuery += "_organisationunitgroupsetstructure AS _oustgss \n";

                if ( tableList.contains( "_cs" ) )
                {
                    // "OUSTGSS and OUS and COCN"
                    joinQuery += SqlViewJoinLib.OUSTGSS_JOIN_OUS + SqlViewJoinLib.OUS_JOIN_COCN;

                    // "COCN and CS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_CS;
                }
            }
        }
        else if ( tableList.size() == 5 )
        {
            // -----------------------------------------------------------------
            // _CategoryOptionComboname _OrgUnitStructure
            // _DataelementGroupSetStructure _OrganisationunitGroupSetStructure
            // -----------------------------------------------------------------
            if ( tableList.contains( "_cocn" ) && tableList.contains( "_ous" ) && tableList.contains( "_degss" )
                && tableList.contains( "_oustgss" ) && tableList.contains( "_cs" ) )
            {

                joinQuery += "_categorystructure AS _cs \n";

                // "CS and COCN and DEGSS"
                joinQuery += SqlViewJoinLib.CS_JOIN_COCN + SqlViewJoinLib.COCN_JOIN_DEGSS;

                // "DV and OUS and OUSTGSS"
                joinQuery += SqlViewJoinLib.DV_JOIN_OUS + SqlViewJoinLib.OUS_JOIN_OUSTGSS;

            }
        }

        return joinQuery;
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

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

    private String setUpMessage( String input )
    {
        input = input.replaceFirst( "(?i)\\s*error:", ENDLINE + ENDLINE + "ERROR:" ).replaceFirst( "(?i)\\s*hint:",
            ENDLINE + "HINT:" ).replaceFirst( "(?i)\\s*Position:", ENDLINE + "POSITION:" );

        return input;
    }
}