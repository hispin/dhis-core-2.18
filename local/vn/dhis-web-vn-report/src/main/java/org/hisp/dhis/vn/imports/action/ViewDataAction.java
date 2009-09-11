package org.hisp.dhis.vn.imports.action;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import org.hisp.dhis.vn.imports.ReportItemValue;
import org.hisp.dhis.vn.report.ReportExcelInterface;
import org.hisp.dhis.vn.report.ReportExcelService;
import org.hisp.dhis.vn.report.ReportItem;
import org.hisp.dhis.vn.report.utils.ExcelUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class ViewDataAction
    implements Action
{
    // --------------------------------------------------------------------
    // Dependencies
    // --------------------------------------------------------------------
    private ReportExcelService reportExcelService;

    public void setReportExcelService( ReportExcelService reportExcelService )
    {
        this.reportExcelService = reportExcelService;
    }

    // --------------------------------------------------------------------
    // Input && Output
    // --------------------------------------------------------------------

    private Integer reportId;

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    private String uploadFileName;

    public void setUploadFileName( String uploadFileName )
    {
        this.uploadFileName = uploadFileName;
    }

    private List<ReportItemValue> reportItemValues;

    public List<ReportItemValue> getReportItemValues()
    {
        return reportItemValues;
    }

    // --------------------------------------------------------------------
    // Action implementation
    // --------------------------------------------------------------------

    public String execute()
    {

        try
        {
            ReportExcelInterface report = reportExcelService.getReport( reportId );
            File upload = new File( uploadFileName );
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale( new Locale( "en", "EN" ) );
            Workbook templateWorkbook = Workbook.getWorkbook( upload, ws );

            Collection<ReportItem> reportItems = report.getReportItems();

            Sheet sheet = templateWorkbook.getSheet( 0 );

            reportItemValues = new ArrayList<ReportItemValue>();

            for ( ReportItem reportItem : reportItems )
            {
                if ( reportItem.getItemType().equals( ReportItem.TYPE.DATAELEMENT ) )
                {
                    String value = ExcelUtils.readValue( reportItem.getRow(), reportItem.getColumn(), sheet );
                    
                    if ( !value.isEmpty() )
                    {
                        ReportItemValue reportItemvalue = new ReportItemValue( reportItem, value );

                        reportItemValues.add( reportItemvalue );
                    } // end if value
                }// end if reportitems
            }// end for

            return SUCCESS;
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        return ERROR;
    }

}
