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

package org.hisp.dhis.patient.startup;

import java.sql.ResultSet;
import java.sql.Statement;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version TableAlteror.java Sep 9, 2010 10:22:29 PM
 */
public class TableAlteror
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( TableAlteror.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    @Transactional
    public void execute()
        throws Exception
    {
        updatePatientOrgunitAssociation();

        updateDOBType();

        executeSql( "UPDATE program SET version = 1 WHERE version is NULL" );

        updateDataSetMobileAttribute();

        updateDataSetVersionAttribute();

        executeSql( "UPDATE patientidentifiertype SET type='" + PatientIdentifierType.VALUE_TYPE_TEXT
            + "' WHERE type IS NULL" );

        executeSql( "UPDATE program SET minDaysAllowedInputData=0 WHERE minDaysAllowedInputData IS NULL" );

        executeSql( "UPDATE program SET maxDaysAllowedInputData=0 WHERE maxDaysAllowedInputData IS NULL" );
        
        executeSql( "UPDATE patient SET isdead=false WHERE isdead IS NULL" );

    }

    private void updatePatientOrgunitAssociation()
    {
    	StatementHolder holder = statementManager.getHolder();

        try
        {
            Statement statement = holder.getStatement();

            ResultSet isUpdated = statement
                .executeQuery( "SELECT organisationunitid FROM patient where organisationunitid is null " );

            if ( isUpdated.next() )
            {
                ResultSet resultSet = statement
                    .executeQuery( "SELECT patientid, organisationunitid FROM patientidentifier" );
                while ( resultSet.next() )
                {
                    executeSql( "UPDATE patient SET organisationunitid=" + resultSet.getInt( 2 ) + " WHERE patientid="
                        + resultSet.getInt( 1 ) );
                }

                executeSql( "ALTER TABLE patientidentifier DROP COLUMN organisationunitid" );
            }
        }
        catch ( Exception ex )
        {
        	ex.printStackTrace();
            log.error( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private void updateDOBType()
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            executeSql( "UPDATE patient SET dobType='A' WHERE birthdateestimated=true" );

            executeSql( "ALTER TABLE patient drop column birthdateestimated" );

            executeSql( "DELETE FROM validationcriteria where property='birthdateestimated'" );
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private void updateDataSetMobileAttribute()
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            executeSql( "UPDATE dataset SET mobile = false WHERE mobile is null" );
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private void updateDataSetVersionAttribute()
    {
        StatementHolder holder = statementManager.getHolder();

        try
        {
            executeSql( "UPDATE dataset SET version = 1 WHERE version is null" );
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
        finally
        {
            holder.close();
        }
    }

    private int executeSql( String sql )
    {
        try
        {
            return statementManager.getHolder().executeUpdate( sql );
        }
        catch ( Exception ex )
        {
            log.debug( ex );

            return -1;
        }
    }
}
