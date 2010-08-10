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
import org.hisp.dhis.sqlview.SqlViewTable;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2010-07-06
 */
public class HibernateSqlViewExpandStore
    implements SqlViewExpandStore
{
    private static final String PREFIX_SELECT_QUERY = "SELECT * FROM ";

    private static final String PREFIX_VIEWNAME = "_view";

    private static final String[] types = { "VIEW" };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private StatementManager statementManager;

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
            ResultSet rs = holder.getStatement().executeQuery( "SELECT * FROM " + resourceTableName + " LIMIT 1");
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

}