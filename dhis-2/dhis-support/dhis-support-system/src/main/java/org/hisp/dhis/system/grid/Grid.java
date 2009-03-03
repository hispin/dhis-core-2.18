package org.hisp.dhis.system.grid;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import org.apache.commons.math.stat.regression.SimpleRegression;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class Grid
{
    /**
     * A two dimensional List which simulates a grid where the first list
     * represents rows and the second represents columns.
     */
    private final List<List<String>> grid;
    
    /**
     * Indicating the current row in the grid.
     */
    private int currentRowIndex = -1;
    
    /**
     * Default constructor.
     */
    public Grid()
    {
        grid = new ArrayList<List<String>>();
    }

    // ---------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------

    /**
     * Returns the current height / number of rows in the grid.
     */
    public int getHeight()
    {        
        return ( grid != null && grid.size() > 0 ) ? grid.size() : 0;
    }
    
    /**
     * Returns the current width / number of columns in the grid.
     */
    public int getWidth()
    {
        verifyGridState();
        
        return ( grid != null && grid.size() > 0 ) ? grid.get( 0 ).size() : 0;
    }
    
    /**
     * Adds a new row the the grid and moves the cursor accordingly.
     */
    public void nextRow()
    {
        grid.add( new ArrayList<String>() );
        
        currentRowIndex++;
    }
    
    /**
     * Adds the value to the end of the current row.
     * 
     * @param value the value to add.
     */
    public void addValue( String value )
    {
        grid.get( currentRowIndex ).add( value );
    }
    
    /**
     * Returns the row with the given index.
     * 
     * @param rowIndex the index of the row.
     */
    public List<String> getRow( int rowIndex )
    {
        return grid.get( rowIndex );
    }
    
    /**
     * Returns all rows.
     */
    public List<List<String>> getRows()
    {
        return grid;
    }
    
    /**
     * Returns the column with the given index.
     * 
     * @param columnIndex the index of the column.
     */
    public List<String> getColumn( int columnIndex )
    {
        List<String> column = new ArrayList<String>();
        
        for ( List<String> row : grid )
        {
            column.add( row.get( columnIndex ) );
        }
        
        return column;
    }
    
    /**
     * Adds a new column at the end of the grid.
     * 
     * @param columnValues the column values to add.
     * @throws IllegalStateException if the columnValues has different length
     *         than the rows in grid, or if the grid rows are not of the same length.
     */
    public void addColumn( List<String> columnValues )
    {
        verifyGridState();
        
        int rowIndex = 0;
        int columnIndex = 0;
        
        if ( grid.size() != columnValues.size() )
        {
            throw new IllegalStateException( "Column values are not equal to number of rows" );
        }
        
        for ( int i = 0; i < grid.size(); i++ )
        {
            grid.get( rowIndex++ ).add( columnValues.get( columnIndex++ ) );
        }
    }
    
    /**
     * Column must hold numeric data.
     * 
     * @param columnIndex the index of the base column.
     */
    public void addRegressionColumn( int columnIndex )
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
        }

        addColumn( regressionColumn );
    }

    // ---------------------------------------------------------------------
    // Supportive methods
    // ---------------------------------------------------------------------

    /**
     * Verifies that all grid rows are of the same length.
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
