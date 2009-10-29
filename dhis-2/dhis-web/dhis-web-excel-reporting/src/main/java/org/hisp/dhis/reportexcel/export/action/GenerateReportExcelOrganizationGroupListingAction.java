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
package org.hisp.dhis.reportexcel.export.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelOganiztionGroupListing;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class GenerateReportExcelOrganizationGroupListingAction
    extends GenerateReportExcelSupport
{

    public String execute()
        throws Exception
    {
    	
        statementManager.initialise();

        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();
        Period period = selectionManager.getSelectedPeriod();
        this.installExcelFormat();
        this.installPeriod( period );

        ReportExcelOganiztionGroupListing reportExcel = (ReportExcelOganiztionGroupListing) reportService
            .getReportExcel( selectionManager.getSelectedReportExcelId() );

        this.installReadTemplateFile( reportExcel, period, organisationUnit );
        
        for ( Integer sheetNo : reportService.getSheets( selectionManager.getSelectedReportExcelId() ) )
        {
            WritableSheet sheet = outputReportWorkbook.getSheet( sheetNo - 1 );

            Collection<ReportExcelItem> reportExcelItems = reportService.getReportExcelItem( sheetNo, selectionManager
                .getSelectedReportExcelId() );

            this.generateOutPutFile( reportExcel, reportExcelItems, organisationUnit, sheet );

        }      

        this.complete();
        
        statementManager.destroy();
        
        return SUCCESS;
    }

    private void generateOutPutFile( ReportExcelOganiztionGroupListing reportExcel,
        Collection<ReportExcelItem> reportExcelItems, OrganisationUnit organisationUnit, WritableSheet sheet )
        throws RowsExceededException, WriteException
    {
        for ( ReportExcelItem reportItem : reportExcelItems )
        {
            int rowBegin = reportItem.getRow();
            int chapperNo = 0;

            for ( OrganisationUnitGroup organisationUnitGroup : reportExcel.getOrganisationUnitGroups() )
            {
                List<OrganisationUnit> childrenOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnit
                    .getChildren() );

                Collection<OrganisationUnit> organisationUnits = organisationUnitGroup.getMembers();

                organisationUnits.retainAll( childrenOrganisationUnits );

                int beginChapter = rowBegin;
                if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.ORGANISATION )
                    && (!organisationUnits.isEmpty()) )
                {
                    ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), organisationUnitGroup.getName(),
                        ExcelUtils.TEXT, sheet, this.textChapterLeft );
                }
                else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.SERIAL )
                    && (!organisationUnits.isEmpty()) )
                {
                    ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), chappter[chapperNo], ExcelUtils.TEXT,
                        sheet, this.textChapterLeft );
                }
                chapperNo++;
                rowBegin++;
                int serial = 1;

                for ( OrganisationUnit o : organisationUnits )
                {
                    if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.ORGANISATION ) )
                    {
                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), o.getName(), ExcelUtils.TEXT, sheet,
                            this.textLeft );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.SERIAL ) )
                    {
                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), String.valueOf( serial ),
                            ExcelUtils.NUMBER, sheet, this.number );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT ) )
                    {
                        double value = this.getDataValue( reportItem, o );

                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), String.valueOf( value ),
                            ExcelUtils.NUMBER, sheet, this.number );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.INDICATOR ) )
                    {
                        double value = this.getIndicatorValue( reportItem, o );

                        ExcelUtils.writeValue( rowBegin, reportItem.getColumn(), String.valueOf( value ),
                            ExcelUtils.NUMBER, sheet, this.number );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.FORMULA_EXCEL ) )
                    {
                        ExcelUtils.writeFormula( rowBegin, reportItem.getColumn(), reportItem.getExpression(), sheet,
                            this.number );
                    }

                    rowBegin++;
                    serial++;
                }

                if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT )
                    && (!organisationUnits.isEmpty()) )
                {
                    String columnName = ExcelUtils.convertColNumberToColName( reportItem.getColumn() );
                    String formula = "SUM(" + columnName + (beginChapter + 1) + ":" + columnName + (rowBegin - 1) + ")";
                    ExcelUtils.writeFormula( beginChapter, reportItem.getColumn(), formula, sheet, this.number );
                }

            }

        }

    }

}
