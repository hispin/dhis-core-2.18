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

package org.hisp.dhis.reportexcel.export.advance.action;

import java.util.Collection;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.PeriodColumn;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelPeriodColumnListing;
import org.hisp.dhis.reportexcel.export.AbstractGenerateExcelReportSupport;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;
import org.hisp.dhis.reportexcel.utils.ExpressionUtils;
import org.hisp.dhis.system.util.MathUtils;

/**
 * @author Chau Thu Tran
 * @author Tran Thanh Tri
 * @version $Id$
 */

public class GenerateAdvancedReportPeriodColumnListingAction
    extends AbstractGenerateExcelReportSupport
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private Integer organisationGroupId;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setOrganisationGroupId( Integer organisationGroupId )
    {
        this.organisationGroupId = organisationGroupId;
    }

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Override
    // -------------------------------------------------------------------------

    @Override
    protected void executeGenerateOutputFile( ReportExcel reportExcel, Period period )
        throws Exception
    {
        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService
            .getOrganisationUnitGroup( organisationGroupId.intValue() );

        ReportExcelPeriodColumnListing reportExcelInstance = (ReportExcelPeriodColumnListing) reportExcel;

        this.installReadTemplateFile( reportExcelInstance, period, organisationUnitGroup );

        for ( Integer sheetNo : reportService.getSheets( selectionManager.getSelectedReportId() ) )
        {
            Sheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

            Collection<ReportExcelItem> reportExcelItems = reportExcelInstance.getReportItemBySheet( sheetNo );

            this.generateOutPutFile( reportExcelInstance.getPeriodColumns(), reportExcelItems, organisationUnitGroup
                .getMembers(), sheet );

        }

        for ( Integer sheetNo : reportService.getSheets( selectionManager.getSelectedReportId() ) )
        {
            Sheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

            this.recalculatingFormula( sheet );

        }
    }

    private void generateOutPutFile( Collection<PeriodColumn> periodColumns,
        Collection<ReportExcelItem> reportExcelItems, Set<OrganisationUnit> organisationUnits, Sheet sheet )
    {
        for ( ReportExcelItem reportItem : reportExcelItems )
        {
            int i = 0;
            for ( PeriodColumn p : periodColumns )
            {
                if ( p.getPeriodType().equals( reportItem.getPeriodType() ) )
                {
                    double value = 0.0;

                    for ( OrganisationUnit organisationUnit : organisationUnits )
                    {

                        if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT ) )
                        {
                            value += MathUtils.calculateExpression( ExpressionUtils.generateExpression( reportItem, p
                                .getStartdate(), p.getEnddate(), organisationUnit, dataElementService, categoryService,
                                aggregationService ) );
                        }
                        else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.INDICATOR ) )
                        {
                            value += MathUtils.calculateExpression( ExpressionUtils.generateExpression( reportItem, p
                                .getStartdate(), p.getEnddate(), organisationUnit, dataElementService, categoryService,
                                aggregationService ) );
                        }

                    }

                    ExcelUtils.writeValueByPOI( reportItem.getRow(), p.getColumn(), String.valueOf( value ),
                        ExcelUtils.NUMBER, sheet, this.csNumber );

                    i++;
                }
            }
        }
    }
}
