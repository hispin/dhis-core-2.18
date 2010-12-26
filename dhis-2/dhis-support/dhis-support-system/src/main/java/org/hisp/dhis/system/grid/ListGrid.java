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

import static org.hisp.dhis.system.util.MathUtils.getRounded;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.stat.regression.SimpleRegression;
import org.hisp.dhis.common.Grid;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ListGrid
    implements Grid
{
    /**
     * The title of the grid.
     */
    private String title;
    
    /**
     * The subtitle of the grid.
     */
    private String subtitle;
    
    /**
     * A List which represents the column headers of the grid.
     */
    private List<String> headers;
    
    /**
     * A two dimensional List which simulates a grid where the first list
     * represents rows and the second represents columns.
     */
    private List<List<String>> grid;
    
    /**
     * Indicating the current row in the grid.
     */
    private int currentRowIndex = -1;
    
    /**
     * Default constructor.
     */
    public ListGrid()
    {
        headers = new ArrayList<String>();
        grid = new ArrayList<List<String>>();
    }

    // ---------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------

    public String getTitle()
    {
        return title;
    }

    public Grid setTitle( String title )
    {
        this.title = title;
        
        return this;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public Grid setSubtitle( String subtitle )
    {
        this.subtitle = subtitle;
        
        return this;
    }

    public List<String> getHeaders()
    {
        return headers;
    }
    
    public Grid addHeader( String value )
    {
        headers.add( value );
        
        return this;
    }
    
    public Grid replaceHeader( String currentHeader, String newHeader )
    {
        if ( headers.contains( currentHeader ) )
        {
            headers.set( headers.indexOf( currentHeader ), newHeader );
        }
        
        return this;
    }
    
    public int getHeight()
    {        
        return ( grid != null && grid.size() > 0 ) ? grid.size() : 0;
    }
    
    public int getWidth()
    {
        verifyGridState();
        
        return ( grid != null && grid.size() > 0 ) ? grid.get( 0 ).size() : 0;
    }
    
    public Grid nextRow()
    {
        grid.add( new ArrayList<String>() );
        
        currentRowIndex++;
        
        return this;
    }
    
    public Grid addValue( String value )
    {
        grid.get( currentRowIndex ).add( value );
        
        return this;
    }
    
    public List<String> getRow( int rowIndex )
    {
        return grid.get( rowIndex );
    }
    
    public List<List<String>> getRows()
    {
        return grid;
    }
    
    public List<String> getColumn( int columnIndex )
    {
        List<String> column = new ArrayList<String>();
        
        for ( List<String> row : grid )
        {
            column.add( row.get( columnIndex ) );
        }
        
        return column;
    }
    
    public String getValue( int rowIndex, int columnIndex )
    {
        if ( grid.size() < rowIndex || grid.get( rowIndex ) == null || grid.get( rowIndex ).size() < columnIndex )
        {
            throw new IllegalArgumentException( "Grid does not contain the requested row / column" );
        }
        
        return grid.get( rowIndex ).get( columnIndex );
    }
    
    public Grid addColumn( List<String> columnValues )
    {
        verifyGridState();
        
        int rowIndex = 0;
        int columnIndex = 0;
        
        if ( grid.size() != columnValues.size() )
        {
            throw new IllegalStateException( "Number of column values (" + columnValues.size() + ") is not equal to number of rows (" + grid.size() + ")" );
        }
        
        for ( int i = 0; i < grid.size(); i++ )
        {
            grid.get( rowIndex++ ).add( columnValues.get( columnIndex++ ) );
        }
        
        return this;
    }
    
    public Grid removeColumn( int columnIndex )
    {
        verifyGridState();
        
        if ( headers.size() > 0 )
        {
            headers.remove( columnIndex );
        }
        
        for ( List<String> row : grid )
        {
            row.remove( columnIndex );
        }
        
        return this;
    }
    
    public Grid removeColumn( String header )
    {
        int index = headers.indexOf( header );
        
        if ( index != -1 )
        {
            removeColumn( index );
        }
        
        return this;
    }
    
    public Grid addRegressionColumn( int columnIndex )
    {
        verifyGridState();
        
        SimpleRegression regression = new SimpleRegression();
        
        List<String> column = getColumn( columnIndex );
        
        int index = 0;
        
        for ( String value : column )
        {
            index++;
            
            if ( Double.parseDouble( value ) != 0.0 ) // 0 omitted from regression
            {
                regression.addData( index, Double.parseDouble( value ) );
            }
        }
        
        List<String> regressionColumn = new ArrayList<String>();
        
        index = 0;
        
        for ( int i = 0; i < column.size(); i++ )
        {
            final double predicted = regression.predict( index++ );
            
            if ( !Double.isNaN( predicted ) ) // Enough values must exist for regression
            {
                regressionColumn.add( String.valueOf( getRounded( predicted, 1 ) ) );
            }
            else
            {
                regressionColumn.add( null );
            }
        }

        addColumn( regressionColumn );
        
        return this;
    }
    
    public Grid fromResultSet( ResultSet resultSet )
        throws SQLException
    {
        headers = new ArrayList<String>();
        grid = new ArrayList<List<String>>();
                
        ResultSetMetaData metaData = resultSet.getMetaData();
        
        int cols = metaData.getColumnCount();
        
        for ( int i = 0; i < cols; i++ )
        {
            this.addHeader( String.valueOf( metaData.getColumnName( i + 1 ) ) );
        }
        
        while ( resultSet.next() )
        {
            this.nextRow();
            
            for ( int i = 0; i < cols; i++ )
            {
                this.addValue( String.valueOf( resultSet.getObject( i + 1 ) ) );
            }
        }
        
        return this;
    }

    // ---------------------------------------------------------------------
    // Supportive methods
    // ---------------------------------------------------------------------

    /**
     * Verifies that all grid rows are of the same length, and that the number
     * of headers is the same as number of columns or 0.
     */
    private void verifyGridState()
    {
        Integer rowLength = null;    
    
        for ( List<String> row : grid )
        {
            if ( rowLength != null && rowLength != row.size() )
            {
                throw new IllegalStateException( "Grid rows do not have the same number of cells" );
            }
            
            rowLength = row.size();
        }
        
        if ( rowLength != null && headers.size() != 0 && headers.size() != rowLength )
        {
            throw new IllegalStateException( "Number of headers is not 0 and not equal to the number of columns" );
        }
    }

    // ---------------------------------------------------------------------
    // toString
    // ---------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer( "[" );
        
        for ( List<String> row : grid )
        {
            buffer.append( row );
        }
        
        return buffer.append( "]" ).toString();
    }
}
