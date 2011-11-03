package org.hisp.dhis.common;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.transaction.annotation.Transactional;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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
/**
 *
 * @author bobj
 * @version created 01-Nov-2011
 */
public class IdentityPopulator
    extends AbstractStartupRoutine
{

    private static final Log log = LogFactory.getLog( IdentityPopulator.class );

    private static String[] tables =
    {
        "constant", "attribute", "indicatortype", "indicatorgroupset", "indicator",
        "indicatorgroup", "datadictionary", "validationrulegroup", "validationrule",
        "dataset", "orgunitlevel", "organisationunit", "orgunitgroup",
        "orgunitgroupset", "dataelementcategoryoption", "dataelementgroup",
        "dataelement", "dataelementgroupset", "dataelementcategory", "categorycombo"
    };

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
    @Override
    public void execute()
    {
        StatementHolder holder = statementManager.getHolder();

        Statement dummyStatement = holder.getStatement();

        Statement statement;

        try
        {
            Connection conn = dummyStatement.getConnection();

            statement = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE );

            for ( String table : tables )
            {
                try
                {
                    int count = 0;
                    ResultSet resultSet = statement.executeQuery( "SELECT * from " + table + " WHERE uid IS NULL" );
                    while ( resultSet.next() )
                    {
                        ++count;
                        resultSet.updateString( "uid", CodeGenerator.generateCode() );
                        resultSet.updateRow();
                    }
                    if ( count > 0 )
                    {
                        log.info( count + " uids updated on " + table );
                    }

                    count = 0;

                    resultSet = statement.executeQuery( "SELECT * from " + table + " WHERE uuid IS NULL" );
                    while ( resultSet.next() )
                    {
                        ++count;
                        resultSet.updateString( "uuid", UUID.randomUUID().toString() );
                        resultSet.updateRow();
                    }
                    if ( count > 0 )
                    {
                        log.info( count + " uuids updated on " + table );
                    }

                } catch ( SQLException ex )
                {
                    Logger.getLogger( IdentityPopulator.class.getName() ).log( Level.SEVERE, null, ex );
                } finally
                {
                    statement.close();
                }
            }
        } catch ( SQLException ex )
        {
            Logger.getLogger( IdentityPopulator.class.getName() ).log( Level.SEVERE, null, ex );
            return;
        }

    }
}
