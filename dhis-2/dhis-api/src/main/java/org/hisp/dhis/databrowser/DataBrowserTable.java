package org.hisp.dhis.databrowser;

/*
 * Copyright (c) 2004-${year}, University of Oslo
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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author Joakim Bj�rnstad, mod: eivinhb
 * @version $Id$
 */
public class DataBrowserTable
{
    /**
     * A List of List with integers to simulate a 2D array.
     */
    private List<List<String>> counts = new Vector<List<String>>();

    /**
     * A List of the MetaValues for columns.
     */
    private List<MetaValue> columnMeta = new Vector<MetaValue>();

    /**
     * A List of the MetaValues for rows.
     */
    private List<MetaValue> rowMeta = new Vector<MetaValue>();

    /**
     * Metadata - this value will hold how long the query took in the Store
     * layer.
     */
    private long queryTime = 0;

    /**
     * Metadata - this value will hold number of queries this DataBrowserTable
     * has results from.
     */
    private int queryCount = 0;

    /**
     * 
     * Takes a sql ResultSet and creates the structure for the DataBrowserTable.
     * Creates a new List for every Row in the ResultSet and a new MetaValue
     * row.
     * 
     * index 1 = id index 2 = name
     * 
     * @param resultSet the SQL ResultSet
     */
    public void createStructure( ResultSet resultSet )
    {
        try
        {
            while ( resultSet.next() )
            {
                Integer rowId = resultSet.getInt( 1 );
                String rowName = resultSet.getString( 2 );
                List<String> rowItem = new Vector<String>();
                counts.add( rowItem );
                addRowNameAndId( rowId, rowName );
            }
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
    }

    /**
     * 
     * Adds one Column to the table structure.
     * 
     * ResultSet contents: index 1: Id index 2: Name index 3:
     * counts_of_aggregated_values
     * 
     * The ResultSet can also contain index 4: PeriodId AND index 5: ColumnName
     * if it does, this functions will add data to rows divided in new columns
     * for each PeriodId in the Set. IMPORTANT: index4 has to have a AS PeriodId
     * in the query IMPORTANT: index5 has to have a AS ColumnHeader in the query
     * 
     * Initially adds 0 to each row in the column. Looks up in RowMeta and finds
     * index based on Name. Inserts into counts based on that. If the ResultSet
     * is empty, nothing is inserted into the list. (the Period has no
     * DataValues referenced)
     * 
     * @param resultSet the SQL ResultSet
     * @return 0 if ResultSet was empty else number of rows inserted with
     *         column.
     */
    public Integer addColumnToAllRows( ResultSet resultSet )
    {
        boolean hasColumnName = false;
        boolean hasPeriodIds = false;
        try
        {
            ResultSetMetaData data = resultSet.getMetaData();
            if ( data.getColumnCount() == 5 )
            {
                if ( data.getColumnLabel( 5 ).equalsIgnoreCase( "columnheader" ) )
                {
                    hasColumnName = true;
                }
                if ( data.getColumnLabel( 4 ).equalsIgnoreCase( "periodid" ) )
                {
                    hasPeriodIds = true;
                }
            }
        }
        catch ( SQLException e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Integer countRows = 0;
        try
        {
            if ( resultSet.first() != true )
            {
                return countRows;
            }
            resultSet.beforeFirst();

            Integer prid = 0;
            boolean makeEmptyCol = false;

            while ( resultSet.next() )
            {
                if ( resultSet.isFirst() )
                {
                    for ( List<String> rowItem : this.counts )
                    {
                        rowItem.add( "0" );
                    }
                    if ( hasPeriodIds && hasColumnName )
                    {
                        this.addColumnName( resultSet.getString( 5 ) );
                    }
                }

                if ( hasPeriodIds )
                {
                    int tmp = resultSet.getInt( 4 );

                    if ( prid == 0 )
                    {
                        prid = tmp;
                    }
                    if ( tmp != prid )
                    {
                        prid = tmp;
                        makeEmptyCol = true;
                    }

                    if ( makeEmptyCol )
                    {
                        makeEmptyCol = false;
                        for ( List<String> rowItem : this.counts )
                        {
                            rowItem.add( "0" );
                        }
                        if ( hasColumnName )
                        {
                            this.addColumnName( resultSet.getString( 5 ) );
                        }
                    }
                }

                String name = resultSet.getString( 2 );
                String value = resultSet.getString( 3 );
                List<String> rowItem = getRowBasedOnRowName( name );
                rowItem.remove( rowItem.size() - 1 );
                rowItem.add( value );
                countRows++;
            }

        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }

        if ( countRows == 0 )
        {
            this.addZeroColumn();
        }
        return countRows;
    }

    public void addZeroColumn()
    {
        this.addColumnName( "counts_of_aggregated_values" );
        for ( List<String> rowItem : this.counts )
        {
            rowItem.add( "0" );
        }
    }

    /**
     * Adds a name to a column
     * 
     * @param name The name of the column
     */
    public void addColumnName( String name )
    {
        columnMeta.add( new MetaValue( name ) );
    }

    /**
     * Adds a name and metadata to column
     * 
     * @param name The name of the column
     * @param metaValue Metadata of the column
     */
    public void addColumnName( String name, String metaValue )
    {
        columnMeta.add( new MetaValue( name, metaValue ) );
    }

    /**
     * Adds a name to a row
     * 
     * @param name The name of the row
     */
    public void addRowName( String name )
    {
        MetaValue mv = new MetaValue();
        mv.setName( name );
        rowMeta.add( mv );
    }

    /**
     * Adds id and name to column metadata
     * 
     * @param id The id of the row
     * @param name The name of the row
     */
    public void addRowNameAndId( Integer id, String name )
    {
        rowMeta.add( new MetaValue( id, name ) );
    }

    /**
     * Finds the index of a Row based on the name of the row, this is used to
     * insert the correct data into the correct List of Lists for count values.
     * 
     * @param rowName the rowName to check
     * @return index in rowMeta
     */
    public List<String> getRowBasedOnRowName( String rowName )
    {
        int rowIndex = rowMeta.indexOf( new MetaValue( rowName ) );
        return counts.get( rowIndex );
    }

    /**
     * 
     * Finds the count value of x,y count.
     * 
     * No error handling.
     * 
     * @param x
     * @param y
     * @return
     */
    public String getCountFromRowAndColumnIndex( int x, int y )
    {
        return counts.get( x ).get( y );

    }

    public long getQueryTime()
    {
        return queryTime;
    }

    public void setQueryTime( long queryTime )
    {
        this.queryTime = queryTime;
    }

    public void addQueryTime( long queryTime )
    {
        this.queryTime += queryTime;
    }

    public List<List<String>> getCounts()
    {
        return counts;
    }

    public void setCounts( List<List<String>> counts )
    {
        this.counts = counts;
    }

    public List<MetaValue> getColumns()
    {
        return columnMeta;
    }

    public void setColumnNames( List<MetaValue> columnMeta )
    {
        this.columnMeta = columnMeta;
    }

    public List<MetaValue> getRows()
    {
        return rowMeta;
    }

    public void setRowNames( List<MetaValue> rowMeta )
    {
        this.rowMeta = rowMeta;
    }

    public int getQueryCount()
    {
        return queryCount;
    }

    /**
     * Helper method to increase queryCount by one.
     */
    public void incrementQueryCount()
    {
        queryCount++;
    }

    /**
     * This overridden toString method writes out the DataBrowserTable as a
     * table to screen. Very handy in case of debugging and testing.
     */
    @Override
    public String toString()
    {
        String ret = "\n\n";

        for ( MetaValue col : this.getColumns() )
        {
            ret += "|" + col.getName();
        }

        ret += "|\n";
        int i = ret.length();

        for ( int o = 0; o < i; o++ )
        {
            ret += "-";
        }

        ret += "\n";
        Iterator<MetaValue> it = this.getRows().iterator();

        for ( List<String> col : this.getCounts() )
        {
            MetaValue rowMeta = it.next();
            ret += "|" + rowMeta.getName();

            for ( String rowItem : col )
            {
                ret += "|" + rowItem;
            }
            ret += "|\n";
        }

        ret += "\n\n";
        return ret;
    }

}
