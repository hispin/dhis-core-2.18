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
package org.hisp.dhis.reportexcel.action;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.ReportLocationManager;
import org.hisp.dhis.reportexcel.state.SelectionManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class BackupReportExcelAction
    implements Action
{
    private static final String xml = ".xml";

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportExcelService;

    private ReportLocationManager locationManager;

    private SelectionManager selectionManager;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private Integer id;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setReportExcelService( ReportExcelService reportExcelService )
    {
        this.reportExcelService = reportExcelService;
    }

    public void setLocationManager( ReportLocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String execute()
        throws Exception
    {
        ReportExcel reportExcel = reportExcelService.getReportExcel( id );

        Source source = new DOMSource( reportExcel.createDocument() );

        File file = new File( locationManager.getReportExcelTempDirectory() + File.separator + (Math.random() * id)
            + xml );
        Result result = new StreamResult( file );

        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform( source, result );

        selectionManager.setDownloadFilePath( file.getAbsolutePath() );

        return SUCCESS;
    }

}
