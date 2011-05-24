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
package org.hisp.dhis.reportexcel.exporting.action;

import java.util.Collection;

import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.DataElementGroupOrder;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelCategory;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.exporting.AbstractGenerateExcelReportSupport;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

/**
 * @author Tran Thanh Tri
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2009-09-18
 */
public class GenerateReportCategoryAction
    extends AbstractGenerateExcelReportSupport
{
    @Override
    protected void executeGenerateOutputFile( ReportExcel exportReport, Period period )
        throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();
        
        ReportExcelCategory exportReportInstance = (ReportExcelCategory) exportReport;
        
        this.installReadTemplateFile( exportReportInstance, period, organisationUnit );

        for ( Integer sheetNo : exportReportService.getSheets( selectionManager.getSelectedReportId() ) )
        {
            Sheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

            Collection<ReportExcelItem> exportReportItems = exportReportInstance.getExportItemBySheet( sheetNo );

            this.generateOutPutFile( exportReportInstance, exportReportItems, organisationUnit, sheet );
        }

        for ( Integer sheetNo : exportReportService.getSheets( selectionManager.getSelectedReportId() ) )
        {
            Sheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

            this.recalculatingFormula( sheet );
        }
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private void generateOutPutFile( ReportExcelCategory exportReport, Collection<ReportExcelItem> exportReportItems,
        OrganisationUnit organisationUnit, Sheet sheet )
    {
        for ( ReportExcelItem reportItem : exportReportItems )
        {
            int iRow = 0;
            int iCol = 0;
            int rowBegin = reportItem.getRow();

            for ( DataElementGroupOrder dataElementGroup : exportReport.getDataElementOrders() )
            {
                int beginChapter = rowBegin;

                if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT_NAME ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), String.valueOf( dataElementGroup
                        .getName() ), ExcelUtils.TEXT, sheet, this.csText12BoldCenter );
                }
                else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT_CODE ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), String.valueOf( dataElementGroup
                        .getCode() ), ExcelUtils.TEXT, sheet, this.csText12BoldCenter );
                }

                rowBegin++;
                int serial = 1;

                for ( DataElement dataElement : dataElementGroup.getDataElements() )
                {
                    if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT_NAME ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), dataElement.getName(),
                            ExcelUtils.TEXT, sheet, this.csText10Bold );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT_CODE ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), dataElement.getCode(),
                            ExcelUtils.TEXT, sheet, this.csTextICDJustify );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.SERIAL ) )
                    {
                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), String.valueOf( serial ),
                            ExcelUtils.NUMBER, sheet, this.csTextSerial );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.FORMULA_EXCEL ) )
                    {
                        ExcelUtils.writeFormulaByPOI( rowBegin, reportItem.getColumn(), ExcelUtils
                            .checkingExcelFormula( reportItem.getExpression(), iRow, iCol ), sheet, this.csFormula );
                    }
                    else
                    {
                        ReportExcelItem newReportItem = new ReportExcelItem();
                        newReportItem.setColumn( reportItem.getColumn() );
                        newReportItem.setRow( reportItem.getRow() );
                        newReportItem.setPeriodType( reportItem.getPeriodType() );
                        newReportItem.setName( reportItem.getName() );
                        newReportItem.setSheetNo( reportItem.getSheetNo() );
                        newReportItem.setItemType( reportItem.getItemType() );

                        String expression = reportItem.getExpression();
                        expression = expression.replace( "*", String.valueOf( dataElement.getId() ) );
                        newReportItem.setExpression( expression );

                        double value = this.getDataValue( newReportItem, organisationUnit );

                        ExcelUtils.writeValueByPOI( rowBegin, reportItem.getColumn(), String.valueOf( value ),
                            ExcelUtils.NUMBER, sheet, this.csNumber );

                    }
                    rowBegin++;
                    serial++;
                    iRow++;
                }

                if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT ) )
                {
                    String columnName = ExcelUtils.convertColNumberToColName( reportItem.getColumn() );
                    String formula = "SUM(" + columnName + (beginChapter + 1) + ":" + columnName + (rowBegin - 1) + ")";

                    ExcelUtils.writeFormulaByPOI( beginChapter, reportItem.getColumn(), formula, sheet, this.csFormula );
                }
            }
        }
    }
}
