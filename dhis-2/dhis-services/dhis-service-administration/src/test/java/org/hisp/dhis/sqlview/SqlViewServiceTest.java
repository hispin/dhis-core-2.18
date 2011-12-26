package org.hisp.dhis.sqlview;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;

import java.util.Collection;

import org.junit.Test;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class SqlViewServiceTest
    extends SqlViewTest
{
    @Override
    public void setUpTest()
        throws Exception
    {
        setUpSqlViewTest();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void assertEq( char uniqueCharacter, SqlView sqlView, String sql )
    {
        assertEquals( "SqlView" + uniqueCharacter, sqlView.getName() );
        assertEquals( "Description" + uniqueCharacter, sqlView.getDescription() );
        assertEquals( sql, sqlView.getSqlQuery() );
    }

    // -------------------------------------------------------------------------
    // SqlView
    // -------------------------------------------------------------------------

    @Test
    public void testAddSqlView()
    {
        SqlView sqlViewA = createSqlView( 'A', sql1 );
        SqlView sqlViewB = createSqlView( 'B', sql2 );

        int idA = sqlViewService.saveSqlView( sqlViewA );
        int idB = sqlViewService.saveSqlView( sqlViewB );

        sqlViewA = sqlViewService.getSqlView( idA );
        sqlViewB = sqlViewService.getSqlView( idB );

        assertEquals( idA, sqlViewA.getId() );
        assertEq( 'A', sqlViewA, sql1 );

        assertEquals( idB, sqlViewB.getId() );
        assertEq( 'B', sqlViewB, sql2 );
    }

    @Test
    public void testUpdateSqlView()
    {
        SqlView sqlView = createSqlView( 'A', sql1 );

        int id = sqlViewService.saveSqlView( sqlView );

        sqlView = sqlViewService.getSqlView( id );

        assertEq( 'A', sqlView, sql1 );

        sqlView.setName( "SqlViewC" );

        sqlViewService.updateSqlView( sqlView );

        sqlView = sqlViewService.getSqlView( id );

        assertEquals( sqlView.getName(), "SqlViewC" );
    }

    @Test
    public void testDeleteAndGetSqlView()
    {
        SqlView sqlViewA = createSqlView( 'A', sql3 );
        SqlView sqlViewB = createSqlView( 'B', sql4 );

        int idA = sqlViewService.saveSqlView( sqlViewA );
        int idB = sqlViewService.saveSqlView( sqlViewB );

        assertNotNull( sqlViewService.getSqlView( idA ) );
        assertNotNull( sqlViewService.getSqlView( idB ) );

        sqlViewService.deleteSqlView( sqlViewService.getSqlView( idA ) );

        assertNull( sqlViewService.getSqlView( idA ) );
        assertNotNull( sqlViewService.getSqlView( idB ) );

        sqlViewService.deleteSqlView( sqlViewService.getSqlView( idB ) );

        assertNull( sqlViewService.getSqlView( idA ) );
        assertNull( sqlViewService.getSqlView( idB ) );
    }

    @Test
    public void testGetSqlViewByName()
        throws Exception
    {
        SqlView sqlViewA = createSqlView( 'A', sql1 );
        SqlView sqlViewB = createSqlView( 'B', sql2 );

        int idA = sqlViewService.saveSqlView( sqlViewA );
        int idB = sqlViewService.saveSqlView( sqlViewB );

        assertEquals( sqlViewService.getSqlView( "SqlViewA" ).getId(), idA );
        assertEquals( sqlViewService.getSqlView( "SqlViewB" ).getId(), idB );
        assertNull( sqlViewService.getSqlView( "SqlViewC" ) );
    }

    @Test
    public void testGetAllSqlViews()
    {
        SqlView sqlViewA = createSqlView( 'A', sql1 );
        SqlView sqlViewB = createSqlView( 'B', sql2 );
        SqlView sqlViewC = createSqlView( 'C', sql3 );
        SqlView sqlViewD = createSqlView( 'D', sql4 );

        sqlViewService.saveSqlView( sqlViewA );
        sqlViewService.saveSqlView( sqlViewB );
        sqlViewService.saveSqlView( sqlViewC );

        Collection<SqlView> sqlViews = sqlViewService.getAllSqlViews();

        assertEquals( sqlViews.size(), 3 );
        assertTrue( sqlViews.contains( sqlViewA ) );
        assertTrue( sqlViews.contains( sqlViewB ) );
        assertTrue( sqlViews.contains( sqlViewC ) );
        assertTrue( !sqlViews.contains( sqlViewD ) );
    }

    @Test
    public void testMakeUpForQueryStatement()
    {
        SqlView sqlViewA = createSqlView( 'A', sql1 );

        sqlViewA.setSqlQuery( sqlViewService.makeUpForQueryStatement( sqlViewA.getSqlQuery() ) );

        int idA = sqlViewService.saveSqlView( sqlViewA );

        assertEquals( sqlViewService.getSqlView( "SqlViewA" ).getId(), idA );

        SqlView sqlViewB = sqlViewService.getSqlView( idA );

        assertEq( 'A', sqlViewB, "SELECT * FROM _categorystructure;" );
    }

    @Test
    public void testSetUpViewTableName()
    {
        SqlView sqlViewC = createSqlView( 'C', sql3 );
        SqlView sqlViewD = createSqlView( 'D', sql4 );

        sqlViewService.saveSqlView( sqlViewC );
        sqlViewService.saveSqlView( sqlViewD );

        String viewC = sqlViewService.setUpViewTableName( sqlViewService.getSqlView( "SqlViewC" ).getName() );
        String viewD = sqlViewService.setUpViewTableName( sqlViewService.getSqlView( "SqlViewD" ).getName() );

        assertEquals( "_view_SqlViewC", viewC );
        assertNotSame( "_view_SqlViewC", viewD );

    }

    @Test
    public void testGetAllSqlViewNames()
    {
        SqlView sqlViewA = createSqlView( 'A', sql4 );
        SqlView sqlViewB = createSqlView( 'B', sql4 );
        SqlView sqlViewC = createSqlView( 'C', sql4 );
        SqlView sqlViewD = createSqlView( 'D', sql4 );

        sqlViewService.saveSqlView( sqlViewA );
        sqlViewService.saveSqlView( sqlViewB );
        sqlViewService.saveSqlView( sqlViewC );
        sqlViewService.saveSqlView( sqlViewD );

        boolean flag = sqlViewService.createAllViewTables();

        assertTrue( flag );
    }
}
