package org.hisp.dhis.analytics.table;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AnalyticsTableManager;
import org.hisp.dhis.analytics.AnalyticsTableService;
import org.hisp.dhis.system.util.Clock;
import org.hisp.dhis.system.util.ConcurrentUtils;
import org.hisp.dhis.system.util.PaginatedList;
import org.hisp.dhis.system.util.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

public class DefaultAnalyticsTableService
    implements AnalyticsTableService
{
    private static final Log log = LogFactory.getLog( DefaultAnalyticsTableService.class );
    
    @Autowired
    private AnalyticsTableManager tableManager;

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    //TODO generateOrganisationUnitStructures
    //TODO generateOrganisationUnitGroupSetTable
    //TODO generatePeriodStructure
    
    @Async
    public Future<?> update()
    {
        Clock clock = new Clock().startClock().logTime( "Starting update..." );
        
        tableManager.dropTable();
        clock.logTime( "Dropped analytics table" );
        
        tableManager.createTable();
        clock.logTime( "Created analytics table" );
        
        tableManager.populateTable();
        clock.logTime( "Populated analytics table" );
        
        createIndexes();
        clock.logTime( "Created all indexes, update done" );
        
        return null;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
  
    private void createIndexes()
    {
        int pages = Math.max( ( SystemUtils.getCpuCores() - 1 ), 1 );
        
        log.info( "No of pages: " + pages );
        
        List<Future<?>> futures = new ArrayList<Future<?>>();

        List<List<String>> columnPages = new PaginatedList<String>( tableManager.getDimensionColumnNames() ).setNumberOfPages( pages ).getPages();
        
        for ( List<String> columnPage : columnPages )
        {
            futures.add( tableManager.createIndexesAsync( columnPage ) );
        }
        
        ConcurrentUtils.waitForCompletion( futures );        
    }
}
