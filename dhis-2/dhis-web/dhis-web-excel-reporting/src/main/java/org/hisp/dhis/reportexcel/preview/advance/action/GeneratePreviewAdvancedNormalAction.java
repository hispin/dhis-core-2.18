package org.hisp.dhis.reportexcel.preview.advance.action;

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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelNormal;
import org.hisp.dhis.reportexcel.preview.action.GeneratePreviewReportExcelSupport;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

/**
 * @author Dang Duy Hieu
 * @author Chau Thu Tran
 * @version $Id$
 * @since 2009-10-08
 */
public class GeneratePreviewAdvancedNormalAction
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

        Period period = this.selectionManager.getSelectedPeriod();
        this.installPeriod( period );

        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService
            .getOrganisationUnitGroup( orgunitGroupId );

        Set<OrganisationUnit> organisationUnits = organisationUnitGroup.getMembers();

        ReportExcelNormal reportExcel = (ReportExcelNormal) reportService.getReportExcel( this.selectionManager
            .getSelectedReportExcelId() );

        this.installReadTemplateFile( reportExcel, period, organisationUnitGroup );

        if ( this.sheetId > 0 )
        {
            HSSFSheet sheet = this.templateWorkbook.getSheetAt( this.sheetId - 1 );

            Collection<ReportExcelItem> reportExcelItems = reportService.getReportExcelItem( this.sheetId,
                this.selectionManager.getSelectedReportExcelId() );

            this.generateOutPutFile( organisationUnits, reportExcelItems, sheet );
        }
        else
        {
            for ( Integer sheetNo : reportService.getSheets( selectionManager.getSelectedReportExcelId() ) )
            {
                HSSFSheet sheet = this.templateWorkbook.getSheetAt( sheetNo - 1 );

                Collection<ReportExcelItem> reportExcelItems = reportService.getReportExcelItem( sheetNo,
                    this.selectionManager.getSelectedReportExcelId() );

                this.generateOutPutFile( organisationUnits, reportExcelItems, sheet );
            }
        }

        this.complete();

        this.statementManager.destroy();

        return SUCCESS;

    }

    private void generateOutPutFile( Set<OrganisationUnit> organisationList,
        Collection<ReportExcelItem> reportExcelItems, HSSFSheet sheet )
    {
        for ( ReportExcelItem reportItem : reportExcelItems )
        {
            double value = 0;

            Iterator<OrganisationUnit> iter = organisationList.iterator();

            while ( iter.hasNext() )
            {
                OrganisationUnit organisationUnit = iter.next();

                value += getDataValue( reportItem, organisationUnit );
            }

            ExcelUtils.writeValueByPOI( reportItem.getRow(), reportItem.getColumn(), String.valueOf( value ),
                ExcelUtils.NUMBER, sheet, this.csNumber );
        }
    }
}
