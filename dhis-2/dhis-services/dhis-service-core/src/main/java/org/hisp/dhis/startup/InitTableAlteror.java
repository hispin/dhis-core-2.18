package org.hisp.dhis.startup;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
public class InitTableAlteror
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( InitTableAlteror.class );

    @Autowired
    private StatementManager statementManager;

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    @Transactional
    public void execute()
    {
        // domain type

        executeSql( "update dataelement set domaintype='AGGREGATE' where domaintype='aggregate' or domaintype is null;" );
        executeSql( "update dataelement set domaintype='TRACKER' where domaintype='patient';" );
        executeSql( "alter table dataelement alter column domaintype set not null;" );
        executeSql( "alter table programstageinstance alter column  status  type varchar(25);" );
        executeSql( "UPDATE programstageinstance SET status='ACTIVE' WHERE status='0';" );
        executeSql( "UPDATE programstageinstance SET status='COMPLETED' WHERE status='1';" );
        executeSql( "UPDATE programstageinstance SET status='SKIPPED' WHERE status='5';" );
        executeSql( "ALTER TABLE programstage_dataelements RENAME COLUMN allowDateInFuture to allowFutureDate;" );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private int executeSql( String sql )
    {
        try
        {
            // TODO use jdbcTemplate

            return statementManager.getHolder().executeUpdate( sql );
        }
        catch ( Exception ex )
        {
            log.debug( ex );

            return -1;
        }
    }
}
