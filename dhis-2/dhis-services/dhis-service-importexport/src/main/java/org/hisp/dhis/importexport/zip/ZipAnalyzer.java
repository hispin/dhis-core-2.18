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

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.importexport.ImportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author bobj
 */
@Component("zipAnalyzer")
public class ZipAnalyzer
{
    private final static Log log = LogFactory.getLog( ZipAnalyzer.class );

    @Autowired
    private SingleStreamImporter singleStreamImporter;

    @Autowired
    private ExcelXImporter excelXImporter;

    @Autowired
    private OdfImporter odfImporter;

    @Autowired
    private SdmxImporter sdmxImporter;
    
    /**
     * Zip package types we know and/or care about
     */
    public static enum PkgType
    {

        SINGLE_ZIPPED_STREAM,
        SDMX_CROSS_DATA,
        EXCEL_OOXML,
        CALC_ODF,
        UNKNOWN
    };

    /**
     * Saves the incoming stream into a ZipFile and create typed ZipPackage
     *
     * @param theZip
     * @return 
     * @throws ImportException
     */
    public ZipImporter getZipImporter( ZipFile theZip)
        throws ImportException
    {

        ZipImporter importer = null;


        PkgType pkgType = getPackageType( theZip );

        switch ( pkgType )
        {
            case SINGLE_ZIPPED_STREAM:
                log.debug( "Detected single zipped stream" );
                importer = singleStreamImporter;
                break;
            case SDMX_CROSS_DATA:
                log.debug( "Detected SDMX-HD cross sectional data package" );
                importer = sdmxImporter;
                break;
            case EXCEL_OOXML:
                log.debug( "Detected OOXML Excel package" );
                importer = excelXImporter;
                break;
            case CALC_ODF:
                log.debug( "Detected ODF calc package" );
                importer = odfImporter;
                break;
            default:
                log.debug( "Unknown zip package" );
        }

        if ( importer == null )
        {
            throw new ImportException( "Unknown zip file type" );
        }

        return importer;

    }

    /**
     * Determine the package type from a zip file
     * @param zipFile
     * @return
     */
    public static PkgType getPackageType( ZipFile zipFile )
    {
        // this is the simplest case - eg dxf
        if ( zipFile.size() == 1 )
        {
            return PkgType.SINGLE_ZIPPED_STREAM;
        }

        Enumeration<?> entries = zipFile.entries();

        // loop through the zip entries looking for clues
        while ( entries.hasMoreElements() )
        {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if ( entry.getName().equals( "xl/worksheets/sheet1.xml" ) )
            {
                return PkgType.EXCEL_OOXML;
            }
            if ( entry.getName().equals( "Data_CROSS.xml" ) )
            {
                return PkgType.SDMX_CROSS_DATA;
            }
        }

        return PkgType.UNKNOWN;
    }
}
