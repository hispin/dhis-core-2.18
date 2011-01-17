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
package org.hisp.dhis.reportexcel.item.action;

import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id GetReportExcelItemAction.java 2010-03-25 02:25:20Z Chau Thu Tran
 *          $
 */

public class GetReportExcelItemAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportExcelService reportService;
    
    private ExpressionService expressionService;

    private DataElementCategoryService dataElementCategoryService;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer id;

    private ReportExcelItem reportItem;

    private String textualFormula;

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    public String getTextualFormula()
    {
        return textualFormula;
    }

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public ReportExcelItem getReportItem()
    {
        return reportItem;
    }

    public String execute()
        throws Exception
    {
        reportItem = reportService.getReportExcelItem( id );

        if ( !reportItem.getReportExcel().getReportType().equals( ReportExcel.TYPE.CATEGORY ) )
        {
            textualFormula = expressionService.getExpressionDescription( reportItem.getExpression() );
        }
        else
        {
            String formula = reportItem.getExpression().replaceAll( "[\\[\\]]", "" );
            int categoryOptionComboId = Integer.parseInt( formula.substring( formula.indexOf( "." ) + 1, formula
                .length() ) );
            textualFormula = "*."
                + dataElementCategoryService.getDataElementCategoryOptionCombo( categoryOptionComboId ).getName();
        }
        return SUCCESS;
    }
}
