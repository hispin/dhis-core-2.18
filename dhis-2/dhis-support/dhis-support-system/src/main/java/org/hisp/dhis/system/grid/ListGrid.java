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

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.commons.math.stat.regression.SimpleRegression;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;

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
     * The name of a potential corresponding table.
     */
    private String table;
    
    /**
     * A List which represents the column headers of the grid.
     */
    private List<GridHeader> headers;
    
    /**
     * A two dimensional List which simulates a grid where the first list
     * represents rows and the second represents columns.
     */
    private List<List<String>> grid;
    
    /**
     * Indicating the current row in the grid for writing data.
     */
    private int currentRowWriteIndex = -1;
    
    /**
     * Indicating the current row in the grid for reading data.
     */
    private int currentRowReadIndex = -1;
    
    /**
     * Default constructor.
     */
    public ListGrid()
    {
        headers = new ArrayList<GridHeader>();
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
    
    public String getTable()
    {
        return table;
    }
    
    public Grid setTable( String table )
    {
        this.table = table;
        
        return this;
    }
    
    public List<GridHeader> getHeaders()
    {
        return headers;
    }

    public List<GridHeader> getVisibleHeaders()
    {
        List<GridHeader> tempHeaders = new ArrayList<GridHeader>();
        
        for ( GridHeader header : headers )
        {
            if ( !header.isHidden() )
            {
                tempHeaders.add( header );
            }
        }
        
        return tempHeaders;
    }
    
    public Grid addHeader( GridHeader header )
    {
        headers.add( header );
        
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
    
    public int getVisibleWidth()
    {
        verifyGridState();
        
        return ( grid != null && grid.size() > 0 )  ? getVisibleRows().get( 0 ).size() : 0;
    }
    
    public Grid addRow()
    {
        grid.add( new ArrayList<String>() );
        
        currentRowWriteIndex++;
        
        return this;
    }
    
    public Grid addValue( String value )
    {
        grid.get( currentRowWriteIndex ).add( value );
        
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
    
    public List<List<String>> getVisibleRows()
    {
        verifyGridState();
        
        List<List<String>> tempGrid = new ArrayList<List<String>>();
        
        if ( headers != null && headers.size() > 0 )
        {
            for ( List<String> row : grid )
            {
                List<String> tempRow = new ArrayList<String>();
                
                for ( int i = 0; i < row.size(); i++ )
                {
                    if ( !headers.get( i ).isHidden() )
                    {
                        tempRow.add( row.get( i ) );
                    }
                }
                
                tempGrid.add( tempRow );
            }
        }
        
        return tempGrid;
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
    
    public Grid removeColumn( GridHeader header )
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

    // ---------------------------------------------------------------------
    // JRDataSource implementation
    // ---------------------------------------------------------------------

    public boolean next()
        throws JRException
    {
        int height = getHeight();
        
        return ++currentRowReadIndex < height; 
    }
    
    public Object getFieldValue( JRField field )
        throws JRException
    {
        int headerIndex = -1;
        
        for ( int i = 0; i < headers.size(); i++ )
        {
            if ( headers.get( i ).getColumn() != null && headers.get( i ).getColumn().equals( field.getName() ) )
            {
                headerIndex = i;
                break;
            }
        }
        
        return headerIndex != -1 ? getRow( currentRowReadIndex ).get( headerIndex ) : null;
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
            throw new IllegalStateException( 
                "Number of headers is not 0 and not equal to the number of columns (headers: " + headers.size() + ", cols: " + rowLength + ")" );
        }
    }
    
    // ---------------------------------------------------------------------
    // toString
    // ---------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer( "[" );
        
        if ( headers != null && headers.size() > 0 )
        {
            List<String> headerNames = new ArrayList<String>();
            
            for ( GridHeader header : headers )
            {
                headerNames.add( header.getName() );
            }
            
            buffer.append( headerNames  ).append( "\n" );
        }
        
        for ( List<String> row : grid )
        {
            buffer.append( row ).append( "\n" );
        }
        
        return buffer.append( "]" ).toString();
    }
}
