package org.hisp.dhis.datavalue;

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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.jdbc.StatementHolder;
import org.hisp.dhis.jdbc.StatementManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.deletion.DeletionHandler;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataValueDeletionHandler
    extends DeletionHandler
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
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return DataValue.class.getSimpleName();
    }
    
    @Override
    public boolean allowDeleteDataElement( DataElement dataElement )
    {
        StatementHolder holder = statementManager.getHolder();
        
        try
        {
            String sql = "SELECT COUNT(*) FROM datavalue where dataelementid=" + dataElement.getId();
            
            ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            int count = resultSet.next() ? resultSet.getInt( 1 ) : 0;
            
            return count == 0;
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get number of datavalues for dataelement", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    @Override
    public boolean allowDeletePeriod( Period period )
    {
        StatementHolder holder = statementManager.getHolder();
        
        try
        {
            String sql = "SELECT COUNT(*) FROM datavalue where periodid=" + period.getId();
            
            ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            int count = resultSet.next() ? resultSet.getInt( 1 ) : 0;
            
            return count == 0;
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get number of datavalues for period", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    @Override
    public boolean allowDeleteSource( Source source )
    {
        StatementHolder holder = statementManager.getHolder();
        
        try
        {
            String sql = "SELECT COUNT(*) FROM datavalue where sourceid=" + source.getId();
            
            ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            int count = resultSet.next() ? resultSet.getInt( 1 ) : 0;
            
            return count == 0;
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get number of datavalues for source", ex );
        }
        finally
        {
            holder.close();
        }
    }
    
    @Override
    public boolean allowDeleteDataElementCategoryOptionCombo( DataElementCategoryOptionCombo combo )
    {
        StatementHolder holder = statementManager.getHolder();
        
        try
        {
            String sql = "SELECT COUNT(*) FROM datavalue where categoryoptioncomboid=" + combo.getId();
            
            ResultSet resultSet = holder.getStatement().executeQuery( sql );
            
            int count = resultSet.next() ? resultSet.getInt( 1 ) : 0;
            
            return count == 0;
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( "Failed to get number of datavalues for dataelementcategoryoptioncombo", ex );
        }
        finally
        {
            holder.close();
        }
    }
}
