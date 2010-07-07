package org.hisp.dhis.importexport.zip;

import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipFile;
import javax.xml.stream.XMLInputFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.dxf.converter.DXFConverter;
import org.hisp.dhis.importexport.xml.XMLPreConverter;

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
 * TransformablePackage
 *
 * Implementation of template-hook pattern for zip packages which can
 * be imported via xslt transform.
 *
 * @author bobj
 * @version created 05-May-2010
 */
public abstract class TransformablePackage implements ZipImporter
{
    private static final String URL_PARAM = "zip_url";

    /**
     * Template method for any package imported via xslt transform
     *
     * @param params 
     * @param zipFile
     * @param state
     * @throws Exception
     */
    @Override
    public void importData( ImportParams params, ProcessState state, ZipFile zipFile ) throws Exception
    {
        XMLPreConverter preConverter = getXMLPreConverter();
        DXFConverter converter = getDXFConverter();

        Map<String, String> xsltParams = getXsltParams();
        xsltParams.put( URL_PARAM, zipFile.getName() );
        String xsltIdTag = getTransformerTag(zipFile);

        InputStream xmlDataStream = getXMLDataStream(zipFile);
        XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory.newInstance();
        XMLStreamReader2 streamReader = (XMLStreamReader2) factory.createXMLStreamReader( xmlDataStream );

        XMLReader dxfReader = preConverter.transform( streamReader, params, state, xsltParams, xsltIdTag );
        converter.read( dxfReader, params, state );
    }

    // hook methods
    protected abstract String getTransformerTag(ZipFile zipFile) throws Exception;

    protected abstract Map<String, String> getXsltParams() throws Exception;

    protected abstract InputStream getXMLDataStream(ZipFile zipFile) throws Exception;

    protected abstract XMLPreConverter getXMLPreConverter();

    protected abstract DXFConverter getDXFConverter();
}
