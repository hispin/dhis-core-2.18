package org.hisp.dhis.importexport.csv.converter;

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

import static org.hisp.dhis.importexport.csv.util.CsvUtil.SEPARATOR;
import static org.hisp.dhis.importexport.csv.util.CsvUtil.csvEncode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedMap;

import org.hisp.dhis.importexport.CSVConverter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.reporttable.ReportTableData;
import org.hisp.dhis.reporttable.ReportTableService;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportTableDataConverter
    implements CSVConverter
{
    private ReportTableService reportTableService;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     * 
     * @param reportTableService the reportTableService to use.
     */
    public ReportTableDataConverter( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }

    // -------------------------------------------------------------------------
    // CSVConverter implementation
    // -------------------------------------------------------------------------

    public void write( BufferedWriter writer, ExportParams params )
    {
        try
        {
            for ( Integer id : params.getReportTables() ) //TODO more than one?
            {
                ReportTableData data = reportTableService.getReportTableData( id );
                
                Iterator<String> columns = data.getPrettyPrintColumns().iterator();
                
                while ( columns.hasNext() )
                {
                    writer.write( csvEncode( columns.next() ) );
                    
                    if ( columns.hasNext() )
                    {
                        writer.write( SEPARATOR );
                    }
                }
                
                writer.newLine();
                
                for ( SortedMap<Integer, String> row : data.getRows() )
                {
                    Iterator<String> values = row.values().iterator();
                    
                    while ( values.hasNext() )
                    {
                        writer.write( csvEncode( values.next() ) );
                        
                        if ( values.hasNext() )
                        {
                            writer.write( SEPARATOR );
                        }
                    }
                    
                    writer.newLine();
                }
            }
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to write ReportTableData CSV", ex );
        }
    }
    
    public void read( BufferedReader reader, ImportParams params )
    {
        // Not implemented
    }
}
