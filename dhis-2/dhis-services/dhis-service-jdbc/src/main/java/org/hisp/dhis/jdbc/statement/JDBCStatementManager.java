package org.hisp.dhis.jdbc.statement;

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

import java.sql.Connection;
import java.sql.DriverManager;

import org.hisp.dhis.jdbc.JDBCConfiguration;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.jdbc.StatementHolder;
import org.hisp.dhis.jdbc.StatementManager;
import org.hisp.dhis.jdbc.factory.StatementBuilderFactory;

/**
 * @author Lars Helge Overland
 * @version $Id: JDBCStatementManager.java 5714 2008-09-17 13:05:36Z larshelg $
 */
public class JDBCStatementManager
    implements StatementManager
{
    private ThreadLocal<StatementHolder> holderTag = new ThreadLocal<StatementHolder>();
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JDBCConfiguration jdbcConfiguration;

    public void setJdbcConfiguration( JDBCConfiguration jdbcConfiguration )
    {
        this.jdbcConfiguration = jdbcConfiguration;
    }
    
    // -------------------------------------------------------------------------
    // StatementManager implementation
    // -------------------------------------------------------------------------

    public void initialise()
    {
        Connection connection = getConnection();

        StatementHolder holder = new DefaultStatementHolder( connection, true );
        
        holderTag.set( holder );
    }
    
    public StatementHolder getHolder()
    {
        StatementHolder holder = holderTag.get();
        
        if ( holder != null )
        {
            return holder;
        }
        
        return new DefaultStatementHolder( getConnection(), false );        
    }
        
    public void destroy()
    {
        StatementHolder holder = holderTag.get();
        
        if ( holder != null )
        {
            holder.close();
        
            holderTag.remove();
        }
    }
        
    public StatementBuilder getStatementBuilder()
    {
        return StatementBuilderFactory.createStatementBuilder( jdbcConfiguration.getDialect() );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Connection getConnection()
    {
        try
        {            
            Class.forName( jdbcConfiguration.getDriverClass() );
            
            Connection connection = DriverManager.getConnection( 
                jdbcConfiguration.getConnectionUrl(),
                jdbcConfiguration.getUsername(),
                jdbcConfiguration.getPassword() );
            
            return connection;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to create connection", ex );
        }
    }
}
