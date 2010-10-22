package org.hisp.dhis.dataset.action.dataentryform;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.system.util.StreamUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ExportDataEntryFormAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    @Override
    public String execute()
        throws Exception
    {
        fileName = "data_entry.txt";

        DataSet dataset = dataSetService.getDataSet( id );

        DataEntryForm dataEntryForm = dataset.getDataEntryForm();

        PipedOutputStream out = new PipedOutputStream();

        PipedInputStream in = new PipedInputStream( out );

        BufferedOutputStream os = new BufferedOutputStream( out );

        os.write( dataEntryForm.getHtmlCode().getBytes() );

        inputStream = new BufferedInputStream( in );

        // in.close();

        // os.close();

        /*
         * 
         * ZipOutputStream zipOut = new ZipOutputStream( new
         * BufferedOutputStream( out ) );
         * 
         * zipOut.putNextEntry( new ZipEntry( "Data Entry Form" + id + ".txt" )
         * );
         * 
         * System.out.println( "put zip entry");
         * 
         * zipOut.write( dataEntryForm.getHtmlCode().getBytes() );
         * 
         * System.out.println( "write");
         * 
         * zipOut.closeEntry();
         * 
         * System.out.println( "Close entry");
         * 
         * zipOut.flush();
         * 
         * System.out.println( "close zip");
         * 
         * inputStream = new BufferedInputStream( in );
         * 
         * out.flush();
         * 
         * out.close();
         * 
         * System.out.println( "close out");
         * 
         * 
         * 
         * System.out.println( "SUCCESS");
         */

        return SUCCESS;
    }
}
