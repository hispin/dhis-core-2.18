package org.hisp.dhis.statistics.jdbc;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.common.Objects;
import org.hisp.dhis.jdbc.StatementHolder;
import org.hisp.dhis.jdbc.StatementManager;
import org.hisp.dhis.statistics.StatisticsProvider;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class JdbcStatisticsProvider
    implements StatisticsProvider
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
    // StatisticsProvider implementation
    // -------------------------------------------------------------------------
    
    public Map<Objects, Integer> getObjectCounts()
    {
        final Map<Objects, Integer> objectCounts = new HashMap<Objects, Integer>();
        
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {
            objectCounts.put( Objects.DATAELEMENT, getObjectCount( holder, "SELECT COUNT(*) FROM dataelement" ) );
            objectCounts.put( Objects.DATAELEMENTGROUP, getObjectCount( holder, "SELECT COUNT(*) FROM dataelementgroup" ) );
            objectCounts.put( Objects.INDICATORTYPE, getObjectCount( holder, "SELECT COUNT(*) FROM indicatortype" ) );
            objectCounts.put( Objects.INDICATOR, getObjectCount( holder, "SELECT COUNT(*) FROM indicator" ) );
            objectCounts.put( Objects.INDICATORGROUP, getObjectCount( holder, "SELECT COUNT(*) FROM indicatorgroup" ) );
            objectCounts.put( Objects.DATASET, getObjectCount( holder, "SELECT COUNT(*) FROM dataset" ) );
            objectCounts.put( Objects.DATADICTIONARY, getObjectCount( holder, "SELECT COUNT(*) FROM datadictionary" ) );
            objectCounts.put( Objects.SOURCE, getObjectCount( holder, "SELECT COUNT(*) FROM source" ) );
            objectCounts.put( Objects.VALIDATIONRULE, getObjectCount( holder, "SELECT COUNT(*) FROM validationrule" ) );
            objectCounts.put( Objects.PERIOD, getObjectCount( holder, "SELECT COUNT(*) FROM period" ) );
            objectCounts.put( Objects.DATAVALUE, getObjectCount( holder, "SELECT COUNT(*) FROM datavalue" ) );
            
            return objectCounts;            
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get aggregated data value", ex );
        }
        finally
        {
            holder.close();
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private Integer getObjectCount( StatementHolder holder, String sql )
        throws SQLException
    {
        final ResultSet resultSet = holder.getStatement().executeQuery( sql );
        
        return resultSet.next() ? resultSet.getInt( 1 ) : 0;
    }
}
