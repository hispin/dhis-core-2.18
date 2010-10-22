package org.hisp.dhis.sqlview;

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
import java.util.Collection;
import java.util.regex.Pattern;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Dang Duy Hieu
 * @version $Id DefaultSqlViewService.java July 06, 2010$
 */
@Transactional
public class DefaultSqlViewService
    implements SqlViewService
{

    private static final Pattern p = Pattern.compile( "\\W" );

    private static final String PREFIX_VIEWNAME = "_view";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<SqlView> sqlViewStore;

    public void setSqlViewStore( GenericIdentifiableObjectStore<SqlView> sqlViewStore )
    {
        this.sqlViewStore = sqlViewStore;
    }

    private SqlViewExpandStore sqlViewExpandStore;

    public void setSqlViewExpandStore( SqlViewExpandStore sqlViewExpandStore )
    {
        this.sqlViewExpandStore = sqlViewExpandStore;
    }

    // -------------------------------------------------------------------------
    // Implement methods
    // -------------------------------------------------------------------------

    @Override
    public void deleteSqlView( SqlView sqlViewObject )
    {
        sqlViewStore.delete( sqlViewObject );
    }

    @Override
    public Collection<SqlView> getAllSqlViews()
    {
        return sqlViewStore.getAll();
    }

    @Override
    public SqlView getSqlView( int viewId )
    {
        return sqlViewStore.get( viewId );
    }

    @Override
    public SqlView getSqlView( String viewName )
    {
        return sqlViewStore.getByName( viewName );
    }

    @Override
    public int saveSqlView( SqlView sqlViewObject )
    {
        return sqlViewStore.save( sqlViewObject );
    }

    @Override
    public void updateSqlView( SqlView sqlViewObject )
    {
        sqlViewStore.update( sqlViewObject );
    }

    @Override
    public String makeUpForQueryStatement( String query )
    {
        return query.replaceAll( ";\\s+", ";" ).replaceAll( ";+", ";" ).replaceAll( "\\s+", " " ).trim();
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

    // -------------------------------------------------------------------------
    // SqlView expanded
    // -------------------------------------------------------------------------

    @Override
    public Collection<String> getAllSqlViewNames()
    {
        return sqlViewExpandStore.getAllSqlViewNames();
    }

    @Override
    public boolean isViewTableExists( String viewTableName )
    {
        return sqlViewExpandStore.isViewTableExists( viewTableName );
    }
        
    @Override
    public SqlViewTable getDataSqlViewTable( String viewTableName )
    {
        SqlViewTable sqlViewTable = new SqlViewTable();
        
        sqlViewExpandStore.setUpDataSqlViewTable( sqlViewTable, viewTableName );
        
        return sqlViewTable;
    }

    @Override
    public Collection<String> getAllResourceProperties( String resourceTableName )
    {
        return sqlViewExpandStore.getAllResourceProperties( resourceTableName );
    }

    @Override
    public String testSqlGrammar( String sql )
    {
        return sqlViewExpandStore.testSqlGrammar( sql );
    }

    @Override
    public String setUpJoinQuery( Collection<String> tables )
    {
        return sqlViewExpandStore.setUpJoinQuery( tables );
    }

}