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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.CrossTabDataValue;

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

    // -------------------------------------------------------------------------
    // CrossTabStore implementation
    // -------------------------------------------------------------------------

    public void createCrossTabTable( final Collection<DataElementOperand> operands, String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {
            final StringBuffer sql = new StringBuffer( "CREATE TABLE " + TABLE_PREFIX + key + " ( " );
            
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
    
    public void dropCrossTabTable( String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {
            final String sql = "DROP TABLE IF EXISTS " + TABLE_PREFIX + key;
            
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
        
    // -------------------------------------------------------------------------
    // CrossTabDataValue
    // -------------------------------------------------------------------------

    public Collection<CrossTabDataValue> getCrossTabDataValues( Collection<DataElementOperand> operands, 
        Collection<Integer> periodIds, Collection<Integer> sourceIds, String key )
    {
        final StatementHolder holder = statementManager.getHolder();
        
        final String sql = "SELECT * FROM " + TABLE_PREFIX + key + " AS c WHERE c.periodid IN (" + 
            getCommaDelimitedString( periodIds ) + ") AND c.sourceid IN (" + getCommaDelimitedString( sourceIds ) + ")";
        
        try
        {            
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return getCrossTabDataValues( resultSet, operands );
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
    
    public Collection<CrossTabDataValue> getCrossTabDataValues( Collection<DataElementOperand> operands, 
        Collection<Integer> periodIds, int sourceId, String key )
    {
        final StatementHolder holder = statementManager.getHolder();

        final String sql = "SELECT * FROM " + TABLE_PREFIX + key + " AS c WHERE c.periodid IN (" + 
            getCommaDelimitedString( periodIds ) + ") AND c.sourceid = " + sourceId;

        try
        {
            final ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            return getCrossTabDataValues( resultSet, operands );
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

    private Collection<CrossTabDataValue> getCrossTabDataValues( ResultSet resultSet, Collection<DataElementOperand> operands )
        throws SQLException
    {
        final Collection<CrossTabDataValue> values = new ArrayList<CrossTabDataValue>();
        
        while ( resultSet.next() )
        {
            final CrossTabDataValue value = new CrossTabDataValue();
            
            value.setPeriodId( resultSet.getInt( 1 ) );
            value.setSourceId( resultSet.getInt( 2 ) );
            
            for ( DataElementOperand operand : operands )
            {
                final String columnName = operand.getColumnName();
                
                final String columnValue = resultSet.getString( columnName );
                
                if ( columnValue != null )
                {
                    value.getValueMap().put( operand, columnValue );
                }
            }
            
            values.add( value );
        }
        
        return values;
    }
}
