package org.hisp.dhis.importexport.csv.exporter;

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

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hisp.dhis.importexport.CSVConverter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.system.process.OpenSessionThread;
import org.hisp.dhis.system.util.StreamUtils;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CSVExportPipeThread
    extends OpenSessionThread
{
    private static final Log log = LogFactory.getLog( CSVExportPipeThread.class );

    private BufferedWriter writer;

    public void setWriter( BufferedWriter writer )
    {
        this.writer = writer;
    }

    private ExportParams params;

    public void setParams( ExportParams params )
    {
        this.params = params;
    }
    
    private List<CSVConverter> converters = new ArrayList<CSVConverter>();
        
    public void registerCSVConverter( CSVConverter converter )
    {
        this.converters.add( converter );
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    
    public CSVExportPipeThread( SessionFactory sessionFactory )
    {   
        super( sessionFactory );
    }
    
    // -------------------------------------------------------------------------
    // Thread implementation
    // -------------------------------------------------------------------------
    
    public void doRun()
    {
        try
        {
            log.info( "Export started" );
            
            for ( CSVConverter converter : converters )
            {
                converter.write( writer, params );
            }
            
            log.info( "Export finished" );
        }
        finally
        {
            StreamUtils.closeWriter( writer );
        }
    }
}
