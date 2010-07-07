package org.hisp.dhis.importexport.zip;

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
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.amplecode.staxwax.framework.XPathFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.importexport.dxf.converter.DXFConverter;
import org.hisp.dhis.importexport.xml.XMLPreConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author bobj
 */
@Component("excelXImporter")
public class ExcelXImporter extends TransformablePackage 
{
    private final static Log log = LogFactory.getLog( ExcelXImporter.class );

    public static final String WORKBOOK = "xl/workbook.xml";
    public static final String WORKSHEET2 = "xl/worksheets/sheet2.xml";
    public static final String TITLESHEET = "xl/worksheets/sheet1.xml";

    private static String SHARED_STRINGS = "xl/sharedStrings.xml";

    @Autowired
    protected XMLPreConverter preConverter;
    
    @Autowired
    protected DXFConverter converter;

    /**
     * Identify the spreadsheet and lookup the associated stylesheet identifier
     * Our current working assumption is there will be a version tag on cell B3
     * of the title sheet.
     *
     * @return An InputStream with the contents of the xslt stylesheet or null
     * @throws Exception
     */
    @Override
    public String getTransformerTag(ZipFile zipFile) throws Exception
    {
        InputStream sharedStrings = zipFile.getInputStream( zipFile.getEntry( SHARED_STRINGS ) );

        InputStream titleSheet = zipFile.getInputStream( zipFile.getEntry( TITLESHEET ) );

        // look up the excel shared string code in cell B3
        String codedIdentifier = XPathFilter.findText( titleSheet, "//c[@r='B3']/v" );

        if ( codedIdentifier == null )
        {
            log.info( "Couldn't identify spreadsheet version info");
            throw new RuntimeException( "Couldn't identify spreadsheet version info" );
        }

        Integer id = Integer.parseInt( codedIdentifier) + 1;

        log.info("Shared string: "+id);
        
        // look up its string value
        String identifier = XPathFilter.findText( sharedStrings, "/sst/si["+id+"]/t");

        log.info("Excel spreadsheet identified: " + identifier);

        return identifier;

    }

    @Override
    protected Map<String, String> getXsltParams() throws Exception
    {
        return new HashMap<String,String>();
    }

    @Override
    protected InputStream getXMLDataStream(ZipFile zipFile) throws Exception
    {
        ZipEntry entry = zipFile.getEntry( WORKSHEET2 );
        InputStream stream = zipFile.getInputStream( entry );

        return stream;
    }

    @Override
    protected XMLPreConverter getXMLPreConverter()
    {
        return preConverter;
    }

    @Override
    protected DXFConverter getDXFConverter()
    {
        return converter;
    }
}
