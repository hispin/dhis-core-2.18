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
package org.hisp.dhis.reportexcel.item.action;

import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.action.ActionSupport;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ValidateUpdateReportExcelItemAction
    extends ActionSupport
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------
    private Integer reportId;

    private Integer reportItemId;

    private Integer sheetNo;

    private String name;

    private String expression;

    private Integer row;

    private Integer column;

    public void setSheetNo( Integer sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    public void setReportItemId( Integer reportItemId )
    {
        this.reportItemId = reportItemId;
    }

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    public void setRow( Integer row )
    {
        this.row = row;
    }

    public void setColumn( Integer column )
    {
        this.column = column;
    }

    public String execute()
        throws Exception
    {
        if ( name == null )
        {
            message = i18n.getString( "name_is_null" );
            return ERROR;
        }
        if ( name.trim().length() == 0 )
        {
            message = i18n.getString( "name_is_null" );
            return ERROR;
        }

        if ( expression == null )
        {
            message = i18n.getString( "expression_is_null" );
            return ERROR;
        }

        if ( expression.trim().length() == 0 )
        {
            message = i18n.getString( "expression_is_null" );
            return ERROR;
        }

        if ( sheetNo == null )
        {
            message = i18n.getString( "please_enter_sheet_no" );
            return ERROR;
        }

        ReportExcel reportExcel = reportService.getReportExcel( reportId );

        ReportExcelItem reportItem = reportExcel.getReportExcelItem( name, sheetNo );

        ReportExcelItem temp = reportService.getReportExcelItem( reportItemId );   

        
        if ( reportItem!=null && !reportItem.equals( temp ))
        {     
            message = i18n.getString( "name_ready_exist" );
            return ERROR;
        }

        if ( row == null )
        {
            message = i18n.getString( "row_is_null" );
            return ERROR;
        }

        if ( column == null )
        {
            message = i18n.getString( "column_is_null" );
            return ERROR;
        }

        return SUCCESS;
    }

}
