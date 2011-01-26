package org.hisp.dhis.system.grid;

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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class GridTest
{
    private Grid grid;
    
    private GridHeader headerA;
    private GridHeader headerB;
    private GridHeader headerC;
    
    @Before
    public void setUp()
    {
        grid = new ListGrid();
        
        headerA = new GridHeader( "ColA", "colA", String.class.getName(), false, false );
        headerB = new GridHeader( "ColB", "colB", String.class.getName(), false, false );
        headerC = new GridHeader( "ColC", "colC", String.class.getName(), true, false );
        
        grid.addHeader( headerA );
        grid.addHeader( headerB );
        grid.addHeader( headerC );
        
        grid.addRow();        
        grid.addValue( "11" );
        grid.addValue( "12" );
        grid.addValue( "13" );

        grid.addRow();        
        grid.addValue( "21" );
        grid.addValue( "22" );
        grid.addValue( "23" );

        grid.addRow();        
        grid.addValue( "31" );
        grid.addValue( "32" );
        grid.addValue( "33" );

        grid.addRow();        
        grid.addValue( "41" );
        grid.addValue( "42" );
        grid.addValue( "43" );
    }
    
    @Test
    public void testGetHeight()
    {
        assertEquals( 4, grid.getHeight() );
    }
    
    @Test
    public void testGetWidth()
    {
        assertEquals( 3, grid.getWidth() );
    }
        
    @Test
    public void testGetRow()
    {
        List<String> rowA = grid.getRow( 0 );
        
        assertTrue( rowA.size() == 3 );
        assertTrue( rowA.contains( "11" ) );
        assertTrue( rowA.contains( "12" ) );
        assertTrue( rowA.contains( "13" ) );
        
        List<String> rowB = grid.getRow( 1 );
        
        assertTrue( rowB.size() == 3 );
        assertTrue( rowB.contains( "21" ) );
        assertTrue( rowB.contains( "22" ) );
        assertTrue( rowB.contains( "23" ) );
    }

    @Test
    public void testGetHeaders()
    {
        assertEquals( 3, grid.getHeaders().size() );
    }
    
    @Test
    public void tetsGetVisibleHeaders()
    {
        assertEquals( 2, grid.getVisibleHeaders().size() );
        assertTrue( grid.getVisibleHeaders().contains( headerA ) );
        assertTrue( grid.getVisibleHeaders().contains( headerB ) );
    }

    @Test
    public void testGetRows()
    {
        assertEquals( 4, grid.getRows().size() );
        assertEquals( 3, grid.getWidth() );
    }

    @Test
    public void testGetGetVisibleRows()
    {
        assertEquals( 4, grid.getVisibleRows().size() );
        assertEquals( 2, grid.getVisibleRows().get( 0 ).size() );
        assertEquals( 2, grid.getVisibleRows().get( 1 ).size() );
        assertEquals( 2, grid.getVisibleRows().get( 2 ).size() );
        assertEquals( 2, grid.getVisibleRows().get( 3 ).size() );
    }
    
    @Test
    public void testGetColumn()
    {        
        List<String> column1 = grid.getColumn( 1 );
        
        assertEquals( 4, column1.size() );
        assertTrue( column1.contains( "12" ) );
        assertTrue( column1.contains( "22" ) );
        assertTrue( column1.contains( "32" ) );
        assertTrue( column1.contains( "42" ) );

        List<String> column2 = grid.getColumn( 2 );
        
        assertEquals( 4, column2.size() );
        assertTrue( column2.contains( "13" ) );
        assertTrue( column2.contains( "23" ) );
        assertTrue( column2.contains( "33" ) );
        assertTrue( column2.contains( "43" ) );
    }
    
    @Test
    public void testAddColumn()
    {
        List<String> columnValues = new ArrayList<String>();
        columnValues.add( "14" );
        columnValues.add( "24" );
        columnValues.add( "34" );
        columnValues.add( "44" );
        
        grid.addColumn( columnValues );
        
        List<String> column3 = grid.getColumn( 3 );
        
        assertEquals( 4, column3.size() );
        assertTrue( column3.contains( "14" ) );
        assertTrue( column3.contains( "24" ) );
        assertTrue( column3.contains( "34" ) );
        assertTrue( column3.contains( "44" ) );
        
        List<String> row2 = grid.getRow( 1 );
        
        assertEquals( 4, row2.size() );
        assertTrue( row2.contains( "21" ) );
        assertTrue( row2.contains( "22" ) );
        assertTrue( row2.contains( "23" ) );
        assertTrue( row2.contains( "24" ) );
    }
    
    @Test
    public void testRemoveColumn()
    {
        assertEquals( 3, grid.getWidth() );
        
        grid.removeColumn( 2 );
        
        assertEquals( 2, grid.getWidth() );
    }
    
    @Test
    public void testRemoveColumnByHeader()
    {
        assertEquals( 3, grid.getWidth() );
        
        grid.removeColumn( headerB );
        
        assertEquals( 2, grid.getWidth() );
    }
    
    @Test
    public void testAddRegressionColumn()
    {
        grid = new ListGrid();        

        grid.addRow();        
        grid.addValue( "10.0" );
        grid.addRow();        
        grid.addValue( "50.0" );
        grid.addRow();        
        grid.addValue( "20.0" );
        grid.addRow();        
        grid.addValue( "60.0" );
        
        grid.addRegressionColumn( 0 );
        
        List<String> column = grid.getColumn( 1 );
        
        assertTrue( column.size() == 4 );
        assertTrue( column.contains( "5.0" ) );
        assertTrue( column.contains( "17.0" ) );
        assertTrue( column.contains( "29.0" ) );
        assertTrue( column.contains( "41.0" ) );
    }

    @Test
    public void testJRDataSource() throws Exception
    {
        assertTrue( grid.next() );
        assertEquals( "11", (String)grid.getFieldValue( new MockJRField( "colA" ) ) );
        assertEquals( "12", (String)grid.getFieldValue( new MockJRField( "colB" ) ) );
        assertEquals( "13", (String)grid.getFieldValue( new MockJRField( "colC" ) ) );

        assertTrue( grid.next() );
        assertEquals( "21", (String)grid.getFieldValue( new MockJRField( "colA" ) ) );
        assertEquals( "22", (String)grid.getFieldValue( new MockJRField( "colB" ) ) );
        assertEquals( "23", (String)grid.getFieldValue( new MockJRField( "colC" ) ) );

        assertTrue( grid.next() );
        assertEquals( "31", (String)grid.getFieldValue( new MockJRField( "colA" ) ) );
        assertEquals( "32", (String)grid.getFieldValue( new MockJRField( "colB" ) ) );
        assertEquals( "33", (String)grid.getFieldValue( new MockJRField( "colC" ) ) );

        assertTrue( grid.next() );
        assertEquals( "41", (String)grid.getFieldValue( new MockJRField( "colA" ) ) );
        assertEquals( "42", (String)grid.getFieldValue( new MockJRField( "colB" ) ) );
        assertEquals( "43", (String)grid.getFieldValue( new MockJRField( "colC" ) ) );
        
        assertFalse( grid.next() );
    }
}
