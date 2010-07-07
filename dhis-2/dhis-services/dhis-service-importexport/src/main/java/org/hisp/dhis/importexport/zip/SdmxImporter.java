package org.hisp.dhis.importexport.zip;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.hisp.dhis.importexport.dxf.converter.DXFConverter;
import org.hisp.dhis.importexport.xml.XMLPreConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * @author bobj
 */
@Component("sdmxImporter")
public class SdmxImporter extends TransformablePackage 
{
    @Autowired
    protected XMLPreConverter preConverter;

    @Autowired
    protected DXFConverter converter;

    public static final String CROSS_SECTIONAL_DATA = "Data_CROSS.xml";

    public static final String SDMX_CSD_XSLT_TAG = "SDMX_CSD";

    public static final String TIMESTAMP = "timestamp";

    public static final String METADATA_PARAM = "dxf_url";

    public static final String METADATA = "metadata/Export.xml";

    @Override
    protected String getTransformerTag(ZipFile zipFile) throws Exception
    {
        return SDMX_CSD_XSLT_TAG;
    }

    @Override
    protected Map<String, String> getXsltParams() throws Exception
    {
         HashMap<String,String> xsltParams = new HashMap<String,String>();
         Date now = new Date();
         DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
         xsltParams.put( TIMESTAMP, dfm.format( now ));
         xsltParams.put( METADATA_PARAM, METADATA );

         return xsltParams;
    }

    @Override
    protected InputStream getXMLDataStream(ZipFile zipFile) throws Exception
    {
        // just supporting cross-sectional data messages for now
        ZipEntry entry = zipFile.getEntry( CROSS_SECTIONAL_DATA );
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
