package org.hisp.dhis.importexport.xml.importer;


import java.io.BufferedInputStream;

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
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.framework.XMLPipe;
import org.amplecode.staxwax.framework.XPathFilter;
import org.amplecode.staxwax.reader.DefaultXMLEventReader;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.transformer.TransformerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;
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
public class DefaultXMLImportService implements ImportService
{

    public static final String DXF_ROOT = "dxf";

    public static final String TRANSFORMERS_CONFIG = "transform/transforms.xml";

    private final Log log = LogFactory.getLog( DefaultXMLImportService.class );

    protected LocationManager locationManager;

    /**
     * Set the value of locationManager
     *
     * @param locationManager new value of locationManager
     */
    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    protected URIResolver dhisResolver;

    /**
     * Set the value of dhisResolver
     * This is used to resolve href uris in the xslt stylesheet
     *
     * @param dhisResolver new value of dhisResolver
     */
    public void setDhisResolver( URIResolver dhisResolver )
    {
        this.dhisResolver = dhisResolver;
    }

    DXFConverter converter;

    /**
     * Set the value of converter
     *
     * @param converter
     */
    public void setConverter( DXFConverter converter )
    {
        this.converter = converter;
    }

    @Override
    public void importData( ImportParams params, InputStream inputStream )
    {
        importData( params, inputStream, new OutputHolderState() );
    }

    /**
     * Open a stylesheet to transform xml with the provided root element
     * 
     * @param root
     * @return open InputStream or null
     */
    private InputStream getStyleSheetForRoot( StartElement root )
    {
        InputStream result = null;
        QName rootName = root.getName();

        String localpart = rootName.getLocalPart();
        String namespaceURI = rootName.getNamespaceURI();
        
        // Sdmx hd hack - this is a special case 'cos the transform will be dependent on KeyFamily ns
        // Its fragile because the CrossSectionalData element is not obliged to declare the KeyFamily ns.
        // But all current implementations do.
        // TODO: handle this more elegantly and robustly
        if ( localpart.equals( "CrossSectionalData" ) )
        {
            log.info("SDMX cross sectional data file");
            // we might have it.  Depends if the DataSet namespace is declared
            Iterator otherNamespaces = root.getNamespaces();
            while ( otherNamespaces.hasNext() )
            {
                Namespace ns = (Namespace) otherNamespaces.next();
                if ( ns.getNamespaceURI().contains( "KeyFamily" ) )
                {
                    localpart = "DataSet";
                    namespaceURI = ns.getNamespaceURI();
                    log.info("KeyFamily = "+namespaceURI);
                }
            }
        }

        if (namespaceURI==null) {
            namespaceURI="";
        }

        try
        {
            // look up the stylesheet from transformers.xml
            InputStream transformers = locationManager.getInputStream( TRANSFORMERS_CONFIG );
            String xpath =
                "/transforms/transform[(@root='" + localpart + "') and (@ns='" + namespaceURI + "')]/xslt";
            String stylesheet = "transform/"+XPathFilter.findText(transformers , xpath );
            transformers.close();

            if (stylesheet != null) {
                result = locationManager.getInputStream( stylesheet );
            }
        } catch ( Exception ex )
        {
            log.info( ex );
        }
        return result;
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
            BufferedInputStream bufin = new BufferedInputStream( inputStream );

            if ( StreamUtils.isZip( bufin ) )
            {
                // TODO: need a smart zip archive analyzer
                xmlInStream = new ZipInputStream( bufin );
                StreamUtils.getNextZipEntry( (ZipInputStream) xmlInStream );
            } else
            {
                if ( StreamUtils.isGZip( bufin ) )
                {
                    xmlInStream = new GZIPInputStream( bufin );
                } else
                {
                    // assume uncompressed xml
                    xmlInStream = bufin;
                }
            }

            // Phase 2: get a STaX eventreader for the stream
            //   On the basis of QName of root element perform additional transformation(s)
            XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory.newInstance();
            XMLStreamReader2 streamReader = (XMLStreamReader2) factory.createXMLStreamReader( xmlInStream );
            XMLEventReader2 eventReader = (XMLEventReader2) factory.createXMLEventReader( streamReader );

            // look for the document root element but don't pluck it from the stream
            while ( !eventReader.peek().isStartElement() )
            {
                eventReader.nextEvent();
            }

            StartElement root = eventReader.peek().asStartElement();
            QName rootName = root.getName();

            log.info( "Importing " + rootName.getLocalPart() + " from " + root.getNamespaceURI( rootName.getPrefix() ) );

            if ( rootName.getLocalPart().equals( DXF_ROOT ) )
            {
                // native dxf stream - no transform required
                dxfReader = XMLFactory.getXMLReader( streamReader );
            } else
            {
                InputStream sheetStream = getStyleSheetForRoot( root );
                if ( sheetStream == null )
                {
                    throw new Exception( "no stylesheet for " + rootName );
                }

                Source sheet = new StreamSource( sheetStream );
                // rewind stream to reclaim root element
                bufin.reset();
                Source source = new StreamSource( bufin );
                TransformerTask tt = new TransformerTask( sheet, null );

                // make a pipe to capture output of transform
                XMLPipe pipe = new XMLPipe();
                XMLEventWriter pipeinput = pipe.getInput();
                XMLEventReader2 pipeoutput = pipe.getOutput();

                // set result of transform to input of pipe
                StAXResult result = new StAXResult( pipeinput );
                tt.transform( source, result, dhisResolver );
                log.info( "transform successful - importing dxf" );
                
                // set dxfReader to output of pipe
                dxfReader = new DefaultXMLEventReader( (XMLEventReader2) pipeoutput );
            }
            // Phase 3: pass through to dxf convertor

            converter.read( dxfReader, params, state );
            dxfReader.closeReader();
            StreamUtils.closeInputStream( xmlInStream );
            state.setMessage( "import_process_done" );

        } catch ( Exception ex )
        {
            log.error( "XML import error: " + ex );
            state.setMessage( "import_process_failed" ); // see log for details
        }
    }
}
