package org.hisp.dhis.importexport.transform.importer;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;

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

import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.transformer.TransformerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.ImportService;
import org.hisp.dhis.importexport.dxf.converter.DXFConverter;
import org.hisp.dhis.system.process.OutputHolderState;
import org.hisp.dhis.system.util.StreamUtils;

/**
 *
 * @author bobj
 * @version created 14-Feb-2010
 */
public class DefaultTransformImportService implements ImportService {

    public static final String ROOT_NAME = "dxf";

    public static final String DXF2_NAMESPACE_URI = "http://dhis2.org/ns/schema/dxf2";

    private final Log log = LogFactory.getLog( DefaultTransformImportService.class );

    DXFConverter converter;

    public void setConverter( DXFConverter converter )
    {
        this.converter = converter;
    }
    
    @Override
    public void importData( ImportParams params, InputStream inputStream )
    {
        importData( params, inputStream, new OutputHolderState() );
    }

    @Override
    public void importData( ImportParams params, InputStream inputStream, ProcessState state )
    {
        XMLReader dxfReader;

        try
        {
            log.info( "Transform importData()" );
            state.setMessage( "Transform importData()" );

            // the InputStream carrying the XML to be imported
            InputStream xmlInStream;

            // Importing of data from xml source is a three phase process
            // Phase 1:  Get the XML stream
            // this could potentially be from  a zip, a gzip or uncompressed dsource
            BufferedInputStream bufin = new BufferedInputStream(inputStream);
            if (StreamUtils.isZip( bufin ))
            {
                // TODO: need a smart zip archive analyzer
                xmlInStream = new ZipInputStream( inputStream );
                StreamUtils.getNextZipEntry( (ZipInputStream) xmlInStream );
            } else if (StreamUtils.isGZip( bufin))
            {
                xmlInStream = new GZIPInputStream(inputStream);
            } else
            {
                // assume uncompressed xml
                xmlInStream = inputStream;
            }


            // Phase 2: get a STaX eventreader for the stream
            //   On the basis of QName of root element perform additional transformation(s)
            XMLInputFactory factory =  XMLInputFactory.newInstance();
            XMLStreamReader streamReader =  factory.createXMLStreamReader( xmlInStream );
            XMLEventReader eventReader =  factory.createXMLEventReader(streamReader);

            // look for the document root element but don't pluck it from the stream
            XMLEvent ev = eventReader.peek();
            while (!ev.isStartElement()) {
                eventReader.nextEvent();
                ev = eventReader.peek();
            }

            StartElement root = ev.asStartElement();
            QName rootName = root.getName();

            log.info("Importing "+rootName.getLocalPart()+ " from " + root.getNamespaceURI( rootName.getPrefix()));

            if (!rootName.getLocalPart().equals( ROOT_NAME)) {
                // not a dxf stream - can't do it yet ...
                // TODO: match xslt sheet to qname
                throw (new UnsupportedOperationException()); 
            }
            else
            {
                dxfReader = XMLFactory.getXMLReader( streamReader );
            }
            
            // Phase 3: pass through to dxf convertor
            
            converter.read( dxfReader, params, state );
            dxfReader.closeReader();
            StreamUtils.closeInputStream( xmlInStream );
        } catch ( Exception ex )
        {
            log.error( "XML import error: "+ex);
        }

    }

}
