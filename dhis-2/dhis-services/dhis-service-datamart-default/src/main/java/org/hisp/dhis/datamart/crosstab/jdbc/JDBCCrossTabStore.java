package org.hisp.dhis.datamart.crosstab.jdbc;

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

import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;
import org.hisp.dhis.jdbc.StatementBuilder;

/**
 * @author Lars Helge Overland
 * @version $Id: JDBCCrossTabStore.java 6216 2008-11-06 18:06:42Z eivindwa $
 */
public class JDBCCrossTabStore
    implements CrossTabStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;
    
    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // CrossTabStore implementation
    // -------------------------------------------------------------------------

    public void createCrossTabTable( final List<DataElementOperand> operands, String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {
            final StringBuffer sql = new StringBuffer( "CREATE TABLE " + TABLE_NAME + key + " ( " );
            
            sql.append( "periodid INTEGER NOT NULL, " );
            sql.append( "sourceid INTEGER NOT NULL, " );
            
            for ( DataElementOperand operand : operands )
            {
                sql.append( operand.getColumnName() ).append( " VARCHAR(30), " );
            }
            
            sql.append( "PRIMARY KEY ( periodid, sourceid ) );" );
            
            holder.getStatement().executeUpdate( sql.toString() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to create datavalue crosstab table", ex );
        }
        finally
        {
            holder.close();
        }
    }

    public Map<String, Integer> getCrossTabTableColumns( String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {
            final String sql = "SELECT * FROM " + TABLE_NAME + key + " LIMIT 0";
            
            final ResultSetMetaData metaData = holder.getStatement().executeQuery( sql ).getMetaData();
            
            final Map<String, Integer> columnIndexMap = new HashMap<String, Integer>();
            
            for ( int i = 0; i < metaData.getColumnCount(); i++ )
            {
                final int index = i + 1;
                
                final String columnName = metaData.getColumnName( index ).toLowerCase();
                
                columnIndexMap.put( columnName, index );
            }
            
            return columnIndexMap;
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get crosstab table columns", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    public void dropCrossTabTable( String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {
            final String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + key;
            
            holder.getStatement().executeUpdate( sql );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to drop datavalue crosstab table", ex );
        }
        finally
        {
            holder.close();
        }
    }

    public void dropTrimmedCrossTabTable( String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {
            final String sql = "DROP TABLE IF EXISTS " + TABLE_NAME_TRIMMED + key;
            
            holder.getStatement().executeUpdate( sql );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to drop trimmed datavalue crosstab table", ex );
        }
        finally
        {
            holder.close();
        }
    }

    public void renameTrimmedCrossTabTable( String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {
            final String sql = "ALTER TABLE " + TABLE_NAME_TRIMMED + key + " RENAME TO " + TABLE_NAME + key;
            
            holder.getStatement().executeUpdate( sql );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to rename trimmed crosstab table", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    public void createTrimmedCrossTabTable( Collection<DataElementOperand> operands, String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {            
            final StringBuffer buffer = new StringBuffer( "CREATE TABLE " + TABLE_NAME_TRIMMED + key + " AS SELECT periodid, sourceid, " );
            
            for ( final DataElementOperand operand : operands )
            {
                buffer.append( operand.getColumnName() ).append( ", " );
            }
            
            if ( buffer.length() > 1 )
            {
                buffer.deleteCharAt( buffer.length() - 1 );
                buffer.deleteCharAt( buffer.length() - 1 );
            }
            
            buffer.append( " FROM " + TABLE_NAME + key );
            
            holder.getStatement().executeUpdate( buffer.toString() );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get crosstab table columns", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    public int validateCrossTabTable( final Collection<DataElementOperand> operands )
    {
        int maxColumns = statementBuilder.getMaximumNumberOfColumns();
        
        return ( operands != null && operands.size() > maxColumns ) ? operands.size() - maxColumns : 0;
    }
    
    // -------------------------------------------------------------------------
    // CrossTabDataValue
    // -------------------------------------------------------------------------

    public Collection<CrossTabDataValue> getCrossTabDataValues( Map<DataElementOperand, Integer> operandIndexMap, 
        Collection<Integer> periodIds, Collection<Integer> sourceIds, String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {
            final String sql =
                "SELECT * " +
                "FROM " + TABLE_NAME + key + " " +
                "WHERE periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
                "AND sourceid IN ( " + getCommaDelimitedString( sourceIds ) + " )";
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return getCrossTabDataValues( resultSet, operandIndexMap );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get CrossTabDataValues", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    public Collection<CrossTabDataValue> getCrossTabDataValues( Map<DataElementOperand, Integer> operandIndexMap, Collection<Integer> periodIds, int sourceId, String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {
            final String sql = 
                "SELECT * " +
                "FROM " + TABLE_NAME + key + " " +
                "WHERE periodid IN ( " + getCommaDelimitedString( periodIds ) + " ) " +
                "AND sourceid = " + sourceId;
            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return getCrossTabDataValues( resultSet, operandIndexMap );
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get CrossTabDataValues", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<CrossTabDataValue> getCrossTabDataValues( ResultSet resultSet, Map<DataElementOperand, Integer> operandIndexMap )
        throws SQLException
    {
        final Collection<CrossTabDataValue> values = new ArrayList<CrossTabDataValue>();
        
        while ( resultSet.next() )
        {
            final CrossTabDataValue value = new CrossTabDataValue();
            
            value.setPeriodId( resultSet.getInt( 1 ) );
            value.setSourceId( resultSet.getInt( 2 ) );
            
            for ( Map.Entry<DataElementOperand, Integer> entry : operandIndexMap.entrySet() )
            {
                String columnValue = resultSet.getString( entry.getValue() );
                
                if ( columnValue != null )
                {
                    value.getValueMap().put( entry.getKey(), columnValue );
                }
            }
            
            values.add( value );
        }
        
        return values;
    }
}
