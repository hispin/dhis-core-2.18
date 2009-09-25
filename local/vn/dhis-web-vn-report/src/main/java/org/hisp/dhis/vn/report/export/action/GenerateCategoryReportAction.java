package org.hisp.dhis.vn.report.export.action;

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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.vn.report.DataElementOrderInGroup;
import org.hisp.dhis.vn.report.ReportExcelCategory;
import org.hisp.dhis.vn.report.ReportItem;
import org.hisp.dhis.vn.report.utils.ExcelUtils;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class GenerateCategoryReportAction
    extends ReportExportSupportAction
{

    public String execute()
        throws Exception
    {
        statementManager.initialise();

        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();
        Period period = this.periodService.getPeriod( this.periodId );
        this.installExcelFormat();
        this.installPeriod( period );

        ReportExcelCategory reportExcel = (ReportExcelCategory) this.reportService.getReport( this.reportId );

        File reportTempDir = reportLocationManager.getReportTempDirectory();

        File inputExcelTemplate = new File( reportLocationManager.getReportTemplateDirectory() + File.separator
            + reportExcel.getExcelTemplateFile() );

        Calendar calendar = Calendar.getInstance();

        File outputReportFile = new File( reportTempDir, currentUserService.getCurrentUsername()
            + this.dateformatter.format( calendar.getTime() ) + inputExcelTemplate.getName() );

        Workbook templateWorkbook = Workbook.getWorkbook( inputExcelTemplate );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( outputReportFile, templateWorkbook );

        ExcelUtils.writeValue( reportExcel.getOrganisationRow(), reportExcel.getOrganisationColumn(), organisationUnit
            .getName(), ExcelUtils.TEXT, outputReportWorkbook.getSheet( 0 ), text );

        ExcelUtils.writeValue( reportExcel.getPeriodRow(), reportExcel.getPeriodColumn(),
            format.formatPeriod( period ), ExcelUtils.TEXT, outputReportWorkbook.getSheet( 0 ), text );

        WritableSheet sheet = outputReportWorkbook.getSheet( 0 );

        for ( ReportItem reportItem : reportExcel.getReportItems() )
        {
            int rowBegin = reportItem.getRow();

            for ( DataElementOrderInGroup dataElementGroup : reportExcel.getDataElementOrders() )
            {

                int beginChapter = rowBegin;
                if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT_NAME ) )
                {
                    ExcelUtils.writeValue( rowBegin, reportItem.getColumn(),
                        dataElementGroup.getDataElementGroupName(), ExcelUtils.TEXT, sheet, this.textChapterLeft );
                }
                
                

                rowBegin++;

                for ( DataElement dataElement : dataElementGroup.getDataElements() )
                {
                    int serial = 1;
                    if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT_NAME ) )
                    {
                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), dataElement.getName(),
                            ExcelUtils.TEXT, sheet, this.textLeft );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT_CODE ) )
                    {
                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), dataElement.getCode(),
                            ExcelUtils.TEXT, sheet, this.textLeft );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.SERIAL ) )
                    {
                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), String.valueOf( serial ),
                            ExcelUtils.NUMBER, sheet, this.number );
                    }
                    else
                    {
                        ReportItem newReportItem = new ReportItem();
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

                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), String.valueOf( value ),
                            ExcelUtils.NUMBER, sheet, this.number );
                    }
                    rowBegin++;
                    serial++;
                }

                if ( reportItem.getItemType().equalsIgnoreCase( ReportItem.TYPE.DATAELEMENT ) )
                {
                    String columnName = ExcelUtils.convertColNumberToColName( reportItem.getColumn() );
                    String formula = "SUM(" + columnName + (beginChapter + 1) + ":" + columnName + (rowBegin - 1) + ")";
                    ExcelUtils.writeFormula( beginChapter, reportItem.getColumn(), formula, sheet, this.number );
                }
            }
        }

        outputReportWorkbook.write();

        outputReportWorkbook.close();

        outputXLS = outputReportFile.getName();

        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.delete();

        statementManager.destroy();

        return SUCCESS;
    }
}
