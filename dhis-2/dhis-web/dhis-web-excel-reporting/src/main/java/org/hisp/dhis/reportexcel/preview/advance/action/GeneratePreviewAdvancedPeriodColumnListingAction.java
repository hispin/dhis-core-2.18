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

package org.hisp.dhis.reportexcel.preview.advance.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.AscendingPeriodComparator;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelPeriodColumnListing;
import org.hisp.dhis.reportexcel.preview.action.GeneratePreviewReportExcelSupport;
import org.hisp.dhis.reportexcel.utils.DateUtils;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;
import org.hisp.dhis.system.util.MathUtils;

/**
 * @author Dang Duy Hieu
 * @author Chau Thu Tran
 * @author Tran Thanh Tri
 * @version $Id$
 * @since 2009-10-08
 */

public class GeneratePreviewAdvancedPeriodColumnListingAction
    extends GeneratePreviewReportExcelSupport
{

    // ---------------------------------------------------------------------
    // Dependency
    // ---------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    // ---------------------------------------------------------------------
    // Input && Output
    // ---------------------------------------------------------------------

    private Integer orgunitGroupId;

    // ---------------------------------------------------------------------
    // Getters && Setters
    // ---------------------------------------------------------------------

    public void setOrgunitGroupId( Integer orgunitGroupId )
    {
        this.orgunitGroupId = orgunitGroupId;
    }

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // ---------------------------------------------------------------------
    // Action implementation
    // ---------------------------------------------------------------------

    public String execute()
        throws Exception
    {       
        this.statementManager.initialise();

        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService
            .getOrganisationUnitGroup( orgunitGroupId.intValue() );

        Period period = this.selectionManager.getSelectedPeriod();
        this.installPeriod( period );

        Calendar calendar = Calendar.getInstance();

        PeriodType periodType = periodService.getPeriodTypeByClass( MonthlyPeriodType.class );

        Date firstDateOfThisYear = DateUtils.getFirstDayOfYear( calendar.get( Calendar.YEAR ) );

        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( periodType,
            firstDateOfThisYear, endDate ) );

        Collections.sort( periodList, new AscendingPeriodComparator() );

        ReportExcelPeriodColumnListing reportExcel = (ReportExcelPeriodColumnListing) reportService
            .getReportExcel( selectionManager.getSelectedReportExcelId() );

        this.installReadTemplateFile( reportExcel, period, organisationUnitGroup );

        if ( this.sheetId > 0 )
        {
            HSSFSheet sheet = this.templateWorkbook.getSheetAt( this.sheetId - 1 );

            Collection<ReportExcelItem> reportExcelItems = reportService.getReportExcelItem( this.sheetId,
                this.selectionManager.getSelectedReportExcelId() );

            this.generateOutPutFile( periodList, reportExcelItems, organisationUnitGroup.getMembers(), sheet );
        }
        else
        {
            for ( Integer sheetNo : reportService.getSheets( selectionManager.getSelectedReportExcelId() ) )
            {
                HSSFSheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

                Collection<ReportExcelItem> reportExcelItems = reportService.getReportExcelItem( sheetNo,
                    selectionManager.getSelectedReportExcelId() );

                this.generateOutPutFile( periodList, reportExcelItems, organisationUnitGroup.getMembers(), sheet );
            }
        }

        this.complete();

        this.statementManager.destroy();

        return SUCCESS;
    }

    private void generateOutPutFile( List<Period> periods, Collection<ReportExcelItem> reportExcelItems,
        Set<OrganisationUnit> organisationUnits, HSSFSheet sheet )
        throws RowsExceededException, WriteException
    {
        for ( ReportExcelItem reportItem : reportExcelItems )
        {
            int i = 0;
            for ( Period p : periods )
            {
                double value = 0.0;

                for ( OrganisationUnit organisationUnit : organisationUnits )
                {
                    if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.DATAELEMENT ) )
                    {
                        value += MathUtils.calculateExpression( generateExpression( reportItem, p.getStartDate(), p
                            .getEndDate(), organisationUnit ) );
                    }
                    else if ( reportItem.getItemType().equalsIgnoreCase( ReportExcelItem.TYPE.INDICATOR ) )
                    {
                        value += MathUtils.calculateExpression( generateExpression( reportItem, p.getStartDate(), p
                            .getEndDate(), organisationUnit ) );
                    }
                }// end of showing the orgunit

                ExcelUtils.writeValueByPOI( reportItem.getRow(), reportItem.getColumn() + i, String.valueOf( value ),
                    ExcelUtils.NUMBER, sheet, this.csNumber );
                
                i++;
            }
        }
    }
}
