package org.hisp.dhis.importexport.dxf.importer;

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

import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.dxf.converter.DXFConverter;
import org.hisp.dhis.system.process.OutputHolderState;
import org.hisp.dhis.system.util.StreamUtils;

/**
 * @author Lars Helge Overland
 */
public class DefaultDXFImportService
    implements ImportService
{

    public static final String ROOT_NAME = "dxf";

    public static final String DXF2_NAMESPACE_URI = "http://dhis2.org/ns/schema/dxf2";

    private final Log log = LogFactory.getLog( DefaultDXFImportService.class );

    private DXFConverter converter;

    public void setConverter( DXFConverter converter )
    {
        this.converter = converter;
    }

    // -------------------------------------------------------------------------
    // ImportService implementation
    // -------------------------------------------------------------------------
    
    public void importData( ImportParams params, InputStream inputStream )
    {
        importData( params, inputStream, new OutputHolderState() );
    }

    public void importData( ImportParams params, InputStream inputStream, ProcessState state )
    {
        log.info( "DXF importData()" );
        state.setMessage( "DXF importData()" );

        // ---------------------------------------------------------------------
        // Importing of data from xml source is a three phase process.
        // Phase 1:  Get the XML stream.
        // This could potentially be from  a zip, a gzip or uncompressed source.
        // ---------------------------------------------------------------------
                
        ZipInputStream zipIn = new ZipInputStream( inputStream );
        StreamUtils.getNextZipEntry( zipIn );

        // ---------------------------------------------------------------------
        // Phase 2: get a STaX eventreader for the stream.
        // On the basis of QName of root element perform additional transforms.
        // ---------------------------------------------------------------------
        
        XMLReader reader = XMLFactory.getXMLEventReader( zipIn );

        // ---------------------------------------------------------------------
        // We should really peek to find the the <dxf> element.
        // Assuming <dxf> no further transformation necessary.
        // Phase 3: pass through to dxf convertor.
        // ---------------------------------------------------------------------
        
        converter.read( reader, params, state );

        reader.closeReader();
        StreamUtils.closeInputStream( zipIn );
    }
}
