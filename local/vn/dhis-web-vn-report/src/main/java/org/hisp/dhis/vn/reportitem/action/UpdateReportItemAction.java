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
package org.hisp.dhis.vn.reportitem.action;

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.report.ReportExcelService;
import org.hisp.dhis.vn.report.ReportItem;

import com.opensymphony.xwork2.Action;
/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class UpdateReportItemAction
    implements Action
{

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private String name;

    private String itemType;

    private String expression;

    private String periodType;

    private Integer row;

    private Integer column;

    private Integer id;

    private ReportItem reportItem;

    private Integer reportId;
    
    private Integer sheetNo;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------
    
    

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public void setSheetNo( Integer sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    public ReportItem getReportItem()
    {
        return reportItem;
    }

    public void setItemType( String itemType )
    {
        this.itemType = itemType;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    public void setRow( Integer row )
    {
        this.row = row;
    }

    public void setColumn( Integer column )
    {
        this.column = column;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getReportId()
    {
        return reportId;
    }

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    public String execute()
        throws Exception
    {
        reportItem = reportService.getReportItem( id.intValue() );
        reportItem.setName( CodecUtils.unescape( name ) );
        reportItem.setItemType( itemType.trim() );
        reportItem.setRow( row );
        reportItem.setColumn( column );
        reportItem.setExpression(  expression.trim() );
        reportItem.setPeriodType( periodType.trim() );
        reportItem.setSheetNo( (sheetNo==null?0:sheetNo) );        

        reportService.updateReportItem( reportItem );

        return SUCCESS;
    }

}
