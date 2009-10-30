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


import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.bind.*;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.XMLFilterImpl;

import org.hisp.dhis.importexport.dxf.v2object.*;

/**
 * @author Bob Jolliffe
 */


public class V2Parser {
	
    protected JAXBContext metadata_ctx;
    Metadata metadata;
    
    protected JAXBContext datavalues_ctx;
    DataValues datavalues;
    
    public V2Parser() throws javax.xml.bind.JAXBException {
	metadata_ctx = JAXBContext.newInstance(new Class[] {Metadata.class});
	datavalues_ctx =  JAXBContext.newInstance(new Class[] {DataValues.class});		
    }
    
    public void getMetadata(XMLStreamReader reader) throws javax.xml.bind.JAXBException {
        Unmarshaller um = metadata_ctx.createUnmarshaller();
        metadata = (Metadata) um.unmarshal(reader);        
        // ... process metadata
    }

    // just pulling in one bite for now - will do them one at a time later
    public void getDataValues(XMLStreamReader reader) throws javax.xml.bind.JAXBException {
        Unmarshaller um = datavalues_ctx.createUnmarshaller();
        datavalues = (DataValues) um.unmarshal(reader);
        // ... process datavalues
    }
}