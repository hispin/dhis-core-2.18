/*
 * Copyright (c) 2004-2008, University of Oslo
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
package org.hisp.dhis.integration.rims.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.integration.rims.api.RIMSTable;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;
import org.hisp.dhis.integration.rims.api.tables.RIMSDistrict;
import org.hisp.dhis.integration.rims.api.tables.RIMSOrgUnit;
import org.hisp.dhis.integration.rims.api.tables.RIMS_PHC;

/**
 * Imported entries are cached, so take care with reusing instances of this class.
 * @author Leif Arne Storset
 * @version $Id$
 */
public abstract class RIMSTableAdapter
    implements RIMSTable
{
    private Configuration config;
    private Connection conn;

    /**
     * Get the current connection instance, creating one if necessary.
     * 
     * @return
     * @throws SQLException if the connection could not be created.
     */
    private Connection getConnection() throws SQLException
    {
        if ( conn == null )
        {
            conn = config.getConnection();
            // Hopefully this will prevent SQLExceptions such as 
            // "Could not update; currently locked by user 'admin'"
            conn.setTransactionIsolation( Connection.TRANSACTION_NONE );
        }
        return conn;
    }
    
    public void setConnection( Connection conn )
    {
        this.conn = conn;
    }
    
    public boolean isData( RIMSOrgUnit orgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement )
        throws SQLException
    {
        if ( orgUnit instanceof RIMS_PHC )
        {
            return isData( (RIMS_PHC) orgUnit, month, year, mappingDataElement );
        }
        else if ( orgUnit instanceof RIMSDistrict )
        {
            return isData( (RIMSDistrict) orgUnit, month, year, mappingDataElement );
        }
        else
        {
            throw new IllegalArgumentException ( "orgUnit must be one of" +
                        " RIMS_PHC and RIMSDistrict" );
        }
    }
    
    public abstract boolean isData( RIMS_PHC phc, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement ) throws SQLException;
    
    public abstract boolean isData( RIMSDistrict district, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement ) throws SQLException;

    public int insertData( RIMSOrgUnit orgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String value )
        throws SQLException
    {
        if ( orgUnit instanceof RIMS_PHC )
        {
            return insertData( (RIMS_PHC) orgUnit, month, year, mappingDataElement, value );
        }
        else if ( orgUnit instanceof RIMSDistrict )
        {
            return insertData( (RIMSDistrict) orgUnit, month, year, mappingDataElement, value );
        }
        else
        {
            throw new IllegalArgumentException ( "orgUnit must be one of" +
                        " RIMS_PHC and RIMSDistrict" );
        }
    }
    
    public abstract int insertData( RIMS_PHC phc, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String value ) throws SQLException;
    
    public abstract int insertData( RIMSDistrict district, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String value ) throws SQLException;
    
    public int updateData( RIMSOrgUnit orgUnit, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String value )
        throws SQLException
    {
        if ( orgUnit instanceof RIMS_PHC )
        {
            return updateData( (RIMS_PHC) orgUnit, month, year, mappingDataElement, value );
        }
        else if ( orgUnit instanceof RIMSDistrict )
        {
            return updateData( (RIMSDistrict) orgUnit, month, year, mappingDataElement, value );
        }
        else
        {
            throw new IllegalArgumentException ( "orgUnit must be one of" +
                        " RIMS_PHC and RIMSDistrict" );
        }
    }
    
    public abstract int updateData( RIMS_PHC phc, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String value ) throws SQLException;
    
    public abstract int updateData( RIMSDistrict district, int month, int year,
        RIMS_Mapping_DataElement mappingDataElement, String value ) throws SQLException;
    
    
    protected int executeUpdate( String query ) throws SQLException
    {        
        return executeUpdate( query, null );
    }
    
    public String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMSOrgUnit orgUnit, int month, int year ) throws SQLException
    {
        if ( orgUnit instanceof RIMS_PHC )
        {
            return getDataValue( mappingDataElement, (RIMS_PHC) orgUnit, month, year );
        }
        else if ( orgUnit instanceof RIMSDistrict )
        {
            return getDataValue( mappingDataElement, (RIMSDistrict) orgUnit, month, year );
        }
        else
        {
            throw new IllegalArgumentException ( "orgUnit must be one of" +
                        " RIMS_PHC and RIMSDistrict" );
        }
    }
    
    public abstract String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMS_PHC orgUnit, int month, int year ) throws SQLException;
    public abstract String getDataValue( RIMS_Mapping_DataElement mappingDataElement, RIMSDistrict orgUnit, int month, int year ) throws SQLException;
    

    /**
     * Checks if there is existing data using the provided query.
     * 
     * @param query
     * @return
     * @throws SQLException 
     */
    protected boolean existingData( String query ) throws SQLException
    {
        return existingData( query, null );
    }

    /**
     * Checks if there is existing data using the provided query.
     * 
     * @param query
     * @param params
     * @return
     * @throws SQLException 
     */
    protected boolean existingData( String query, Object[] params ) throws SQLException
    {
        PreparedStatement st = null;
        ResultSet rs = null;

        boolean res = false;
        try
        {
            st = getConnection().prepareStatement( query );
            
            if ( params != null )
            {
                for ( int i = 0; i < params.length; i++ )
                {
                    st.setObject( i + 1, params[i] );
                }
            }
            
            rs = st.executeQuery();
            if ( rs.next() )
            {
                res = true;
            }
        }
        finally
        {
            if ( rs != null )
                rs.close();
            if ( st != null )
                st.close();
        }
        return res;
    }
    
    public void finalize()
    {

        try
        {
            if ( conn != null && !conn.isClosed() )
            {
                conn.close();
            }
        }
        catch ( SQLException e )
        {
            // No sense in rethrowing since it is ignored anyhow
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private Cache cache = new Cache();

    private class Cache {
        /**
         *  {@code timeout} seconds after the last cache put, getting the cache
         *  will automatically clear it. 
         */ 
        private int timeout = 60;
        private long lastPut;
        
        public Cache()
        {
            init();
        }
        
        private void init()
        {
            cache = new HashMap<String, Object[]>();
            columns = null;
        }
        
        @SuppressWarnings("hiding")
        private HashMap<String, Object[]> cache;
        private String[] columns;
        
        private String cacheKey( String query, Object[] params )
        {
            StringBuffer sb = new StringBuffer();
            sb.append( query ).append( '\n' );
            for( Object param: params )
            {
                sb.append( param.toString() ).append( '\n' );
            }
            return sb.toString();
        }
        
        /**
         * Inserts into the cache. Returns a {@link Map} as if 
         * {@link #getFromCache(String, Object[])} had been called.
         * @param query
         * @param params
         * @param resultSet
         * @return
         */
        @SuppressWarnings("unused")
        public Map<String, String> putInCache( String query, Object[] params, ResultSet resultSet )
        {
            lastPut = System.currentTimeMillis();
            Object[] row = null;
            try
            {
                if ( columns == null )
                {
                    ResultSetMetaData meta = resultSet.getMetaData();
                    columns = new String[ meta.getColumnCount() ];
                    for( int i = 0; i < columns.length; i++ )
                    {
                        columns[i] = meta.getColumnName( i + 1 );
                    }
                }
                
                row = new Object[ columns.length ];
                for( int i = 0; i < row.length; i++ )
                {
                    row[i] = resultSet.getObject( i + 1 );  
                    if ( resultSet.wasNull() )
                    {
                        row[i] = null;
                    }
                }

                cache.put( cacheKey( query, params ), row );
            }
            catch ( SQLException e )
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return createMap( row );
        }
        
        @SuppressWarnings("unused")
        public Map<String, String> getFromCache( String query, Object[] params )
        {
            // Clear the cache if we've gone past timeout.
            if ( System.currentTimeMillis() - lastPut > timeout * 1000 )
            {
                init();
            }
            
            // Get the cache key
            Object[] row = cache.get( cacheKey( query, params ) );
            if ( row == null )
            {
                return null;
            }
            return createMap( row );
        }

        private Map<String, String> createMap( Object[] row )
        {
            Map<String, String> ret = new HashMap<String, String>( row.length );
            for ( int i = 0; i < row.length; i++ )
            {
                ret.put( columns[i], row[i] + "" );
            }
            return ret;
        }

        public int getTimeout()
        {
            return timeout;
        }

        public void setTimeout( int timeout )
        {
            this.timeout = timeout;
        }
    }
    
    /**
     * Executes the given query with the given parameters.
     * 
     * @param query the query to execute.
     * @param getColumn the column to read.
     * @param params the parameters to replace with.
     * @return the value of the given column, or null if it is NULL or no rows
     *         returned.
     * @throws SQLException
     */
    protected String executeQuery( String query, String getColumn, Object[] params ) throws SQLException
    {
        PreparedStatement st = null;
        ResultSet resultSet = null;
        Object rimsValue = null;
        
        /*
        Map<String, String> cachehit = cache.getFromCache( query, params );
        if ( cachehit != null )
        {
            return cachehit.get(  getColumn ) + "";
        }
        */
    
        try
        {
            st = getConnection().prepareStatement( query );
            
            for( int i = 0; i < params.length; i++ )
            {
                st.setObject( i + 1, params[i] );
            }
            
            resultSet = st.executeQuery();
            if( !resultSet.next() )
            {
                return null;
            }
            rimsValue = resultSet.getObject( getColumn );
            if (resultSet.wasNull())
            {
                return null;
            }
            
            //cache.putInCache( query, params, resultSet );
            // Must cache first since the ResultSet only supports getting data once.
            //rimsValue = cache.putInCache( query, params, resultSet ).get( "getColumn" );
        }
        finally
        {
            if ( resultSet != null )
                resultSet.close();
            if ( st != null )
                st.close();
        }
    
        return rimsValue +"";
    }
    
    /**
     * Executes the given update with the given parameters.
     * 
     * @param update the update to execute.
     * @param params the parameters to replace with.
     * @return the value of the given column, or null if it is NULL.
     * @throws SQLException 
     */
    protected int executeUpdate( String update, Object[] params ) throws SQLException
    {
        PreparedStatement st = null;
        int recordCount = -1;
    
        try
        {
            st = getConnection().prepareStatement( update );
            
            if ( params != null )
            {
                for ( int i = 0; i < params.length; i++ )
                {
                    st.setObject( i + 1, params[i] );
                }
            }
            
            recordCount = st.executeUpdate();
        }

        finally
        {
            if ( st != null )
                st.close();
        }
    
        return recordCount;
    }
    
    public int truncate() throws SQLException
    {
        @SuppressWarnings("hiding")
        Connection conn = getConnection();
        Statement st = conn.createStatement();
        int rowCount;
        try {
            rowCount = st.executeUpdate( "DELETE FROM "+ getTableName() );
            conn.commit();
            }
        finally
        {
            if ( st != null )
            {
                st.close();
            }
        }
        return rowCount;
    }
    
    public void commit() throws SQLException
    {
        if ( conn != null )
        {
            conn.commit();
            conn.setAutoCommit( true );
        }
    }
    
    public void beginTransaction() throws SQLException
    {
        if ( conn != null )
        {
            conn.setAutoCommit( false );
        }
    }

    public void setConfiguration( Configuration config )
    {
        this.config = config;
    }
}
