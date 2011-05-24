package org.hisp.dhis.reportexcel.exporting.advance.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelOganiztionGroupListing;
import org.hisp.dhis.reportexcel.exporting.AbstractGenerateExcelReportSupport;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

/**
 * @author Chau Thu Tran
 * @author Tran Thanh Tri
 * @since 2009-09-18
 */
public class GenerateAdvancedReportOrgGroupListingAction
    extends AbstractGenerateExcelReportSupport
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private Integer organisationGroupId;

    public void setOrganisationGroupId( Integer organisationGroupId )
    {
        this.organisationGroupId = organisationGroupId;
    }

    // -------------------------------------------------------------------------
    // Override
    // -------------------------------------------------------------------------

    @Override
    protected void executeGenerateOutputFile( ReportExcel exportReport, Period period )
        throws Exception
    {
        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService
            .getOrganisationUnitGroup( organisationGroupId );

        ReportExcelOganiztionGroupListing exportReportInstance = (ReportExcelOganiztionGroupListing) exportReport;

        this.installReadTemplateFile( exportReportInstance, period, organisationUnitGroup );

        for ( Integer sheetNo : exportReportService.getSheets( selectionManager.getSelectedReportId() ) )
        {
            Sheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

            Collection<ReportExcelItem> exportItems = exportReportInstance.getExportItemBySheet( sheetNo );

            List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitGroup
                .getMembers() );

            Collections.sort( organisationUnits, new OrganisationUnitNameComparator() );

            this.generateOutPutFile( exportReportInstance, exportItems, organisationUnits, sheet );

        }

        for ( Integer sheetNo : exportReportService.getSheets( selectionManager.getSelectedReportId() ) )
        {
            Sheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

            this.recalculatingFormula( sheet );

        }
    }

    private void generateOutPutFile( ReportExcel exportReport, Collection<ReportExcelItem> exportItems,
        List<OrganisationUnit> organisationUnits, Sheet sheet )
    {
        for ( ReportExcelItem exportItem : exportItems )
        {
            int iRow = 0;
            int iCol = 0;
            int chapperNo = 0;
            int rowBegin = exportItem.getRow();
            int beginChapter = rowBegin;

            chapperNo++;
            rowBegin++;
            int serial = 1;

            for ( OrganisationUnit o : organisationUnits )
            {
                if ( exportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.ORGANISATION ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), o.getName(), ExcelUtils.TEXT, sheet,
                        this.csText10Bold );
                }
                else if ( exportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.SERIAL ) )
                {
                    ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), String.valueOf( serial ),
                        ExcelUtils.NUMBER, sheet, this.csTextSerial );
                }
                else if ( exportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT ) )
                {
                    double value = this.getDataValue( exportItem, o );

                    ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), String.valueOf( value ),
                        ExcelUtils.NUMBER, sheet, this.csNumber );
                }
                else if ( exportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.INDICATOR ) )
                {
                    double value = this.getIndicatorValue( exportItem, o );

                    ExcelUtils.writeValueByPOI( rowBegin, exportItem.getColumn(), String.valueOf( value ),
                        ExcelUtils.NUMBER, sheet, this.csNumber );
                }
                else if ( exportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.FORMULA_EXCEL ) )
                {
                    ExcelUtils.writeFormulaByPOI( rowBegin, exportItem.getColumn(), ExcelUtils.checkingExcelFormula(
                        exportItem.getExpression(), iRow, iCol ), sheet, this.csFormula );
                }

                rowBegin++;
                serial++;
                iRow++;
            }

            if ( exportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT )
                && (!organisationUnits.isEmpty()) )
            {
                String columnName = ExcelUtils.convertColNumberToColName( exportItem.getColumn() );
                String formula = "SUM(" + columnName + (beginChapter + 1) + ":" + columnName + (rowBegin - 1) + ")";
                ExcelUtils.writeFormulaByPOI( beginChapter, exportItem.getColumn(), formula, sheet, this.csFormula );
            }
        }
    }
}
