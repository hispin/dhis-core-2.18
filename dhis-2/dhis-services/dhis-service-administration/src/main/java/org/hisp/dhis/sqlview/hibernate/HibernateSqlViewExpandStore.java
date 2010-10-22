package org.hisp.dhis.sqlview.hibernate;

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

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.sqlview.SqlViewExpandStore;
import org.hisp.dhis.sqlview.SqlViewJoinLib;
import org.hisp.dhis.sqlview.SqlViewTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Dang Duy Hieu
 * @version $Id HibernateSqlViewExpandStore.java July 06, 2010$
 */
public class HibernateSqlViewExpandStore
    implements SqlViewExpandStore
{
    private static final String PREFIX_SELECT_QUERY = "SELECT * FROM ";

    private static final String PREFIX_VIEWNAME = "_view";

    private static final String ENDLINE = "<br/>";

    private static final String[] types = { "VIEW" };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private StatementManager statementManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
            throw new RuntimeException( "Failed to get data from view " + PREFIX_SELECT_QUERY + viewTableName, e );
        }

        sqlViewTable.createViewerStructure( rs );
        sqlViewTable.addRecord( rs );

        holder.close();
    }

    @Override
    public Collection<String> getAllResourceProperties( String resourceTableName )
    {
        final StatementHolder holder = statementManager.getHolder();
        Set<String> propertiesName = new HashSet<String>();

        try
        {
            ResultSet rs = holder.getStatement().executeQuery( "SELECT * FROM " + resourceTableName + " LIMIT 1" );
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
    public String setUpJoinQuery( Collection<String> tableList )
    {
        String joinQuery = "";

        if ( tableList.size() == 2 )
        {
            // -----------------------------------------------------------------
            // CATEGORYOPTIONCOMBONAME
            // -----------------------------------------------------------------
            if ( tableList.contains( "_CategoryOptionComboname" ) )
            {
                joinQuery += "_CategoryOptionComboname AS _cocn \n";

                if ( tableList.contains( "_CategoryStructure" ) )
                {
                    joinQuery += "JOIN _CategoryStructure AS _cs ON _cocn.categoryoptioncomboid = _cs.categoryoptioncomboid \n";
                }
                else if ( tableList.contains( "_DataElementGroupSetStructure" ) )
                {
                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_OrgUnitStructure" ) )
                {
                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;
                }
                else if ( tableList.contains( "_OrgUnitGroupSetStructure" ) )
                {
                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;

                }
                else if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUSTGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUSTGSS;
                }
            }

            // -----------------------------------------------------------------
            // ORGUNITSTRUCTURE
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitStructure" ) )
            {
                joinQuery += "_OrgUnitStructure AS _ous \n";

                if ( tableList.contains( "_OrgUnitGroupSetStructure" ) )
                {
                    joinQuery += "JOIN _OrgUnitGroupSetStructure AS _ougss ON _ous.organisationunitid = _ougss.organisationunitid \n";
                }
                else if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ous.organisationunitid = _oustgss.organisationunitid \n";
                }
                else if ( tableList.contains( "_CategoryOptionComboname" ) )
                {
                    // "OUS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;

                    // "DEGSS and COCN"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN;
                }
                else if ( tableList.contains( "_DataElementGroupSetStructure" ) )
                {
                    // "OUS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_CategoryStructure" ) )
                {
                    // "OUS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;

                    // "DEGSS and COCN"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN;

                    joinQuery += "JOIN _CategoryStructure AS _cs ON _cocn.categoryoptioncomboid = _cs.categoryoptioncomboid \n";
                }
            }

            // -----------------------------------------------------------------
            // ORGUNITGROUPSETSTRUCTURE
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitGroupSetStructure" ) )
            {
                joinQuery += "_OrgUnitGroupSetStructure AS _ougss \n";

                if ( tableList.contains( "_CategoryOptionComboname" ) )
                {
                    joinQuery += "JOIN _OrgUnitStructure AS _ous ON _ougss.organisationunitid = _ous.organisationunitid \n";

                    // "OUS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;

                    // "DEGSS and COCN"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN;

                }
                else if ( tableList.contains( "_OrgUnitStructure" ) )
                {
                    joinQuery += "JOIN _OrgUnitStructure AS _ous ON _ougss.organisationunitid = _ous.organisationunitid \n";
                }
                else if ( tableList.contains( "_CategoryStructure" ) )
                {
                    joinQuery += "JOIN _OrgUnitStructure AS _ous ON _ougss.organisationunitid = _ous.organisationunitid \n";

                    // "OUS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;

                    // "DEGSS and COCN"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN;

                    joinQuery += "JOIN _CategoryStructure AS _cs ON _cocn.categoryoptioncomboid = _cs.categoryoptioncomboid \n";
                }
                else if ( tableList.contains( "_DataElementGroupSetStructure" ) )
                {
                    joinQuery += "JOIN _OrgUnitStructure AS _ous ON _ougss.organisationunitid = _ous.organisationunitid \n";

                    // "OUS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";
                }
            }

            // -----------------------------------------------------------------
            // _CATEGORYSTRUCTURE
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_CategoryStructure" ) )
            {
                joinQuery += "_CategoryStructure AS _cs \n";

                if ( tableList.contains( "_CategoryOptionComboname" ) )
                {
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";
                }
                else if ( tableList.contains( "_OrgUnitStructure" ) )
                {
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                }
                else if ( tableList.contains( "_OrgUnitGroupSetStructure" ) )
                {
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;
                }
                else if ( tableList.contains( "_DataElementGroupSetStructure" ) )
                {
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"TGSS
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUSTGSS;
                }
            }

            // -----------------------------------------------------------------
            // _DATAELEMENTGROUPSETSTRUCTURE
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_DataElementGroupSetStructure" ) )
            {
                joinQuery += "_DataElementGroupSetStructure AS _degss \n";

                if ( tableList.contains( "_CategoryOptionComboname" ) )
                {
                    // "DEGSS and COCN"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN;
                }
                else if ( tableList.contains( "_OrgUnitStructure" ) )
                {
                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;
                }
                else if ( tableList.contains( "_OrgUnitGroupSetStructure" ) )
                {
                    // "DEGSS and OUGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;
                }
                else if ( tableList.contains( "_CategoryStructure" ) )
                {
                    // "DEGSS and COCN"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN;

                    joinQuery += "JOIN _CategoryStructure AS _cs ON _cocn.categoryoptioncomboid = _cs.categoryoptioncomboid \n";
                }
                else if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    // "DEGSS and OUS"TGSS
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUSTGSS;
                }
            }

            // -----------------------------------------------------------------
            // _ORGANISATIONUNITGROUPSETSTRUCTURE
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                joinQuery += "_OrganisationUnitGroupSetStructure AS _oustgss \n";

                if ( tableList.contains( "_CategoryOptionComboname" ) || tableList.contains( "_CategoryStructure" ) )
                {
                    joinQuery += "JOIN _OrgUnitStructure AS _ous ON _oustgss.organisationunitid = _ous.organisationunitid \n";

                    // "OUS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;

                    // "DEGSS and COCN"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN;

                    if ( tableList.contains( "_CategoryStructure" ) )
                    {
                        joinQuery += "JOIN _CategoryStructure AS _cs ON _cocn.categoryoptioncomboid = _cs.categoryoptioncomboid \n";
                    }
                }
                else if ( tableList.contains( "_DataElementGroupSetStructure" ) )
                {
                    joinQuery += "JOIN _OrgUnitStructure AS _ous ON _oustgss.organisationunitid = _ous.organisationunitid \n";

                    // "OUS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;
                }
                else if ( tableList.contains( "_OrgUnitStructure" ) )
                {
                    joinQuery += "JOIN _OrgUnitStructure AS _ous ON _oustgss.organisationunitid = _ous.organisationunitid \n";
                }
                else if ( tableList.contains( "_OrgUnitGroupSetStructure" ) )
                {
                    joinQuery += "JOIN _OrgUnitGroupSetStructure AS _ougss ON _oustgss.organisationunitid = _ougss.organisationunitid \n";
                }

            }
        }
        else if ( tableList.size() == 3 )
        {
            // -----------------------------------------------------------------
            // CategoryOptionComboname OrgUnitStructure
            // -----------------------------------------------------------------
            if ( tableList.contains( "_CategoryOptionComboname" ) && tableList.contains( "_OrgUnitStructure" ) )
            {

                if ( tableList.contains( "_DataElementGroupSetStructure" )
                    || tableList.contains( "_OrgUnitGroupSetStructure" )
                    || tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "_CategoryOptionComboname AS _cocn \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                    if ( tableList.contains( "_OrgUnitGroupSetStructure" ) )
                    {
                        joinQuery += "JOIN _OrgUnitGroupSetStructure AS _ougss ON _ous.organisationunitid = _ougss.organisationunitid \n";
                    }
                    else if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                    {
                        joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ous.organisationunitid = _oustgss.organisationunitid \n";
                    }
                }
                else if ( tableList.contains( "_CategoryStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";

                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;
                }
            }

            // -----------------------------------------------------------------
            // CategoryOptionComboname OrgUnitGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_CategoryOptionComboname" )
                && tableList.contains( "_OrgUnitGroupSetStructure" ) )
            {
                if ( tableList.contains( "_CategoryStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;
                }
                else if ( tableList.contains( "_DataElementGroupSetStructure" )
                    || tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "_CategoryOptionComboname AS _cocn \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;

                    if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                    {
                        joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";
                    }
                }
            }

            // -----------------------------------------------------------------
            // CategoryOptionComboname _CategoryStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_CategoryOptionComboname" ) && tableList.contains( "_CategoryStructure" ) )
            {
                if ( tableList.contains( "_DataElementGroupSetStructure" )
                    || tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                    {
                        // "DEGSS and OUS"TGSS
                        joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUSTGSS;
                    }
                }
            }

            // -----------------------------------------------------------------
            // CategoryOptionComboname _OrganisationUnitGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_CategoryOptionComboname" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" )
                && tableList.contains( "_DataElementGroupSetStructure" ) )
            {
                joinQuery += "_CategoryOptionComboname AS _cocn \n";

                // "COCN and DEGSS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                // "DEGSS and OUS"TGSS
                joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUSTGSS;
            }

            // -----------------------------------------------------------------
            // OrgUnitStructure OrgUnitGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitStructure" ) && tableList.contains( "_OrgUnitGroupSetStructure" ) )
            {
                if ( tableList.contains( "_CategoryStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                    joinQuery += "JOIN _OrgUnitGroupSetStructure AS _ougss ON _ous.organisationunitid = _ougss.organisationunitid \n";
                }
                else if ( tableList.contains( "_DataElementGroupSetStructure" )
                    || tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "_DataElementGroupSetStructure AS _degss \n";

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                    joinQuery += "JOIN _OrgUnitGroupSetStructure AS _ougss ON _ous.organisationunitid = _ougss.organisationunitid \n";

                    if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                    {
                        joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";
                    }
                }
            }

            // -----------------------------------------------------------------
            // OrgUnitStructure _CategoryStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitStructure" ) && tableList.contains( "_CategoryStructure" ) )
            {
                if ( tableList.contains( "_DataElementGroupSetStructure" )
                    || tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                    if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                    {
                        joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ous.organisationunitid = _oustgss.organisationunitid \n";
                    }
                }
            }

            // -----------------------------------------------------------------
            // OrgUnitStructure _DataElementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitStructure" ) && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                joinQuery += "_DataElementGroupSetStructure AS _degss \n";

                // "DEGSS and OUS"
                joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ous.organisationunitid = _oustgss.organisationunitid \n";

            }

            // -----------------------------------------------------------------
            // OrgUnitGroupSetStructure _CategoryStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitGroupSetStructure" ) && tableList.contains( "_CategoryStructure" ) )
            {
                joinQuery += "_CategoryStructure AS _cs \n";
                joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                // "COCN and DEGSS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                if ( tableList.contains( "_DataElementGroupSetStructure" ) )
                {
                    // "DEGSS and OUGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;
                }
                else if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    // "DEGSS and OUS"TGSS
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUSTGSS;
                }
            }

            // -----------------------------------------------------------------
            // OrgUnitGroupSetStructure _DataElementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitGroupSetStructure" )
                && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                joinQuery += "_DataElementGroupSetStructure AS _degss \n";

                // "DEGSS and OUGSS"
                joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;

                joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";

            }

            // -----------------------------------------------------------------
            // _CategoryStructure _DataElementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_CategoryStructure" )
                && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                joinQuery += "_CategoryStructure AS _cs \n";
                joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                // "COCN and DEGSS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                // "DEGSS and OUS"TGSS
                joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUSTGSS;

            }
        }
        else if ( tableList.size() == 4 )
        {
            // -----------------------------------------------------------------
            // CategoryOptionComboname OrgUnitStructure OrgUnitGroupSetStructure
            // -----------------------------------------------------------------
            if ( tableList.contains( "_CategoryOptionComboname" ) && tableList.contains( "_OrgUnitStructure" )
                && tableList.contains( "_OrgUnitGroupSetStructure" ) )
            {
                if ( tableList.contains( "_CategoryStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                    joinQuery += "JOIN _OrgUnitGroupSetStructure AS _ougss ON _ous.organisationunitid = _ougss.organisationunitid \n";

                }
                else if ( tableList.contains( "_DataElementGroupSetStructure" )
                    || tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "_CategoryOptionComboname AS _cocn \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                    joinQuery += "JOIN _OrgUnitGroupSetStructure AS _ougss ON _ous.organisationunitid = _ougss.organisationunitid \n";

                    if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                    {
                        joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";
                    }
                }
            }
            // -----------------------------------------------------------------
            // CategoryOptionComboname OrgUnitStructure _CategoryStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_CategoryOptionComboname" ) && tableList.contains( "_OrgUnitStructure" )
                && tableList.contains( "_CategoryStructure" ) )
            {
                if ( tableList.contains( "_DataElementGroupSetStructure" )
                    || tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                    if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                    {
                        joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ous.organisationunitid = _oustgss.organisationunitid \n";
                    }
                }
            }

            // -----------------------------------------------------------------
            // CategoryOptionComboname OrgUnitStructure
            // _DataElementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_CategoryOptionComboname" ) && tableList.contains( "_OrgUnitStructure" )
                && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                joinQuery += "_CategoryOptionComboname AS _cocn \n";

                // "COCN and DEGSS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                // "DEGSS and OUS"
                joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ous.organisationunitid = _oustgss.organisationunitid \n";

            }

            // -----------------------------------------------------------------
            // CategoryOptionComboname OrgUnitGroupSetStructure
            // _CategoryStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_CategoryOptionComboname" )
                && tableList.contains( "_OrgUnitGroupSetStructure" ) && tableList.contains( "_CategoryStructure" ) )
            {
                if ( tableList.contains( "_DataElementGroupSetStructure" )
                    || tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;

                    if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                    {
                        joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";
                    }
                }
            }

            // -----------------------------------------------------------------
            // CategoryOptionComboname _CategoryStructure
            // _DataElementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_CategoryOptionComboname" ) && tableList.contains( "_CategoryStructure" )
                && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                joinQuery += "_CategoryStructure AS _cs \n";
                joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                // "COCN and DEGSS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                // "DEGSS and OUS"TGSS
                joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUSTGSS;

            }

            // -----------------------------------------------------------------
            // _OrgUnitStructure _OrgUnitGroupSetStructure _CategoryStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitStructure" ) && tableList.contains( "_OrgUnitGroupSetStructure" )
                && tableList.contains( "_CategoryStructure" ) )
            {
                if ( tableList.contains( "_DataElementGroupSetStructure" )
                    || tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;

                    if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                    {
                        joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";
                    }
                }
            }

            // -----------------------------------------------------------------
            // _OrgUnitStructure _DataElementGroupSetStructure
            // _OrganisationUnitGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitStructure" ) && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                if ( tableList.contains( "_OrgUnitGroupSetStructure" ) || tableList.contains( "_CategoryStructure" ) )
                {
                    joinQuery += "_OrganisationUnitGroupSetStructure AS _oustgss \n"
                        + "JOIN _OrgUnitGroupSetStructure AS _ougss ON _oustgss.organisationunitid = _ougss.organisationunitid \n"
                        + "JOIN _OrgUnitStructure AS _ous ON _ougss.organisationunitid = _ous.organisationunitid \n";

                    // "OUS and DEGSS"
                    joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;

                    if ( tableList.contains( "_CategoryStructure" ) )
                    {
                        joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN;

                        joinQuery += "JOIN _CategoryStructure AS _cs ON _cocn.categoryoptioncomboid = _cs.categoryoptioncomboid \n";
                    }
                }
            }

            // -----------------------------------------------------------------
            // OrgUnitGroupSetStructure _CategoryStructure
            // _DataElementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitGroupSetStructure" ) && tableList.contains( "_CategoryStructure" )
                && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                joinQuery += "_CategoryStructure AS _cs \n";
                joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                // "COCN and DEGSS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                // "DEGSS and OUGSS"
                joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;

                joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";

            }
        }
        else if ( tableList.size() == 5 )
        {
            // -----------------------------------------------------------------
            // _CategoryOptionComboname _OrgUnitStructure
            // _OrgUnitGroupSetStructure _CategoryStructure
            // -----------------------------------------------------------------
            if ( tableList.contains( "_CategoryOptionComboname" ) && tableList.contains( "_OrgUnitStructure" )
                && tableList.contains( "_OrgUnitGroupSetStructure" ) && tableList.contains( "_CategoryStructure" ) )
            {
                if ( tableList.contains( "_DataElementGroupSetStructure" )
                    || tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                    joinQuery += "JOIN _OrgUnitGroupSetStructure AS _ougss ON _ous.organisationunitid = _ougss.organisationunitid \n";

                    if ( tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
                    {
                        joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";
                    }
                }
            }

            // -----------------------------------------------------------------
            // _CategoryOptionComboname _OrgUnitStructure
            // _DataElementGroupSetStructure _OrganisationUnitGroupSetStructure
            // -----------------------------------------------------------------
            if ( tableList.contains( "_CategoryOptionComboname" ) && tableList.contains( "_OrgUnitStructure" )
                && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                if ( tableList.contains( "_CategoryStructure" ) )
                {
                    joinQuery += "_CategoryStructure AS _cs \n";
                    joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                    joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ous.organisationunitid = _oustgss.organisationunitid \n";

                }
                else if ( tableList.contains( "_OrgUnitGroupSetStructure" ) )
                {
                    joinQuery += "_CategoryOptionComboname AS _cocn \n";

                    // "COCN and DEGSS"
                    joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                    // "DEGSS and OUGSS"
                    joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;

                    joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";

                }
            }

            // -----------------------------------------------------------------
            // _CategoryOptionComboname _OrgUnitGroupSetStructure
            // _CategoryStructure _DataElementGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_CategoryOptionComboname" )
                && tableList.contains( "_OrgUnitGroupSetStructure" ) && tableList.contains( "_CategoryStructure" )
                && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                joinQuery += "_CategoryStructure AS _cs \n";
                joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                // "COCN and DEGSS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                // "DEGSS and OUGSS"
                joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUGSS;

                joinQuery += "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";

            }

            // -----------------------------------------------------------------
            // _OrgUnitStructure _OrgUnitGroupSetStructure
            // _CategoryStructure _DataElementGroupSetStructure
            // _OrganisationUnitGroupSetStructure
            // -----------------------------------------------------------------
            else if ( tableList.contains( "_OrgUnitStructure" ) && tableList.contains( "_OrgUnitGroupSetStructure" )
                && tableList.contains( "_CategoryStructure" ) && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                joinQuery += "_OrganisationUnitGroupSetStructure AS _oustgss \n"
                    + "JOIN _OrgUnitGroupSetStructure AS _ougss ON _oustgss.organisationunitid = _ougss.organisationunitid \n"
                    + "JOIN _OrgUnitStructure AS _ous ON _ougss.organisationunitid = _ous.organisationunitid \n";

                // "OUS and DEGSS"
                joinQuery += SqlViewJoinLib.OUS_JOIN_DEGSS;

                joinQuery += SqlViewJoinLib.DEGSS_JOIN_COCN;

                joinQuery += "JOIN _CategoryStructure AS _cs ON _cocn.categoryoptioncomboid = _cs.categoryoptioncomboid \n";

            }
        }
        else if ( tableList.size() == 6 )
        {
            // -----------------------------------------------------------------
            // CategoryOptionComboname OrgUnitStructure OrgUnitGroupSetStructure
            // _CategoryStructure _DataElementGroupSetStructure
            // _OrganisationUnitGroupSetStructure
            // -----------------------------------------------------------------
            if ( tableList.contains( "_CategoryOptionComboname" ) && tableList.contains( "_OrgUnitStructure" )
                && tableList.contains( "_OrgUnitGroupSetStructure" ) && tableList.contains( "_CategoryStructure" )
                && tableList.contains( "_DataElementGroupSetStructure" )
                && tableList.contains( "_OrganisationUnitGroupSetStructure" ) )
            {
                joinQuery += "_CategoryStructure AS _cs \n";
                joinQuery += "JOIN _CategoryOptionComboname AS _cocn ON _cs.categoryoptioncomboid = _cocn.categoryoptioncomboid \n";

                // "COCN and DEGSS"
                joinQuery += SqlViewJoinLib.COCN_JOIN_DEGSS;

                // "DEGSS and OUS"
                joinQuery += SqlViewJoinLib.DEGSS_JOIN_OUS;

                joinQuery += "JOIN _OrgUnitGroupSetStructure AS _ougss ON _ous.organisationunitid = _ougss.organisationunitid \n"
                    + "JOIN _OrganisationUnitGroupSetStructure AS _oustgss ON _ougss.organisationunitid = _oustgss.organisationunitid \n";
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
        input = input.replaceFirst( "(?i)\\s*error:", ENDLINE + "ERROR:" ).replaceFirst( "(?i)\\s*hint:",
            ENDLINE + "HINT:" ).replaceFirst( "(?i)\\s*Position:", ENDLINE + "POSITION:" );

        return input;
    }

}