package org.hisp.dhis.importexport;

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;
import org.amplecode.staxwax.reader.XMLReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.importexport.dxf.converter.DXFConverter;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.importexport.xml.XMLPreConverter;
import org.hisp.dhis.importexport.zip.ZipAnalyzer;
import org.hisp.dhis.importexport.zip.ZipImporter;
import org.hisp.dhis.system.process.OutputHolderState;
import org.hisp.dhis.system.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author bobj
 */
public class DefaultImportService
    implements ImportService
{

    private final Log log = LogFactory.getLog( DefaultImportService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private XMLPreConverter preConverter;

    @Autowired
    private DXFConverter converter;

    @Autowired
    private ZipAnalyzer zipAnalyzer;

    // -------------------------------------------------------------------------
    // ImportService implementation
    // -------------------------------------------------------------------------
    @Override
    public void importData( ImportParams params, InputStream inputStream )
        throws ImportException
    {
        importData( params, inputStream, new OutputHolderState() );
    }

    @Override
    public void importData( ImportParams params, InputStream inputStream, ProcessState state )
        throws ImportException
    {

        log.info( "Importing stream" );

        state.setMessage( "Importing stream" );

        // ---------------------------------------------------------------------
        // Phase 1: Get the XML stream. This could potentially be from a ZIP, a 
        // GZIP or uncompressed source
        // ---------------------------------------------------------------------

        BufferedInputStream bufin = new BufferedInputStream( inputStream );

        if ( StreamUtils.isZip( bufin ) )
        {
            // if it's a zip file we must figure out what kind of package it is
            log.info( "Zip file detected" );
            File tempFile = null;
            try
            {
                tempFile = File.createTempFile( "IMPORT_", "_ZIP" );
                // save it to disk
                BufferedOutputStream ostream =
                    new BufferedOutputStream( new FileOutputStream( tempFile ) );

                StreamUtils.streamcopy( bufin, ostream );

                log.info( "Saved zipstream to file: " + tempFile.getAbsolutePath());

                ZipFile zipFile = new ZipFile(tempFile);

                ZipImporter importer = zipAnalyzer.getZipImporter( zipFile );
                
                // delegate task to zipPackage
                importer.importData( params, state, zipFile );

                zipFile.close();

            } catch ( Exception ex )
            {
                throw new ImportException( "Failed to import data in zip package", ex );
            } finally
            {
                if (tempFile != null) {
                    StreamUtils.delete( tempFile.getAbsolutePath() );
                }
            }
        } else
        {

            InputStream xmlDataStream = null; // The InputStream carrying the XML to be imported

            if ( StreamUtils.isGZip( bufin ) )
            {
                try
                {
                    // pass through the uncompressed stream
                    xmlDataStream = new BufferedInputStream( new GZIPInputStream( bufin ) );
                } catch ( IOException ex )
                {
                    throw new ImportException("Corrupt gzip stream", ex);
                }
            } else
            {
                // assume uncompressed xml and keep moving
                xmlDataStream = bufin;
            }

            XMLReader dxfReader = preConverter.processStream( xmlDataStream, params, state );

            converter.read( dxfReader, params, state );

        }
    }
}
