package org.hisp.dhis.reportexcel.export.advance.action;

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
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import jxl.write.WritableSheet;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelNormal;
import org.hisp.dhis.reportexcel.export.action.GenerateReportExcelSupport;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public class GenerateAdvancedReportExcelNormalAction
    extends GenerateReportExcelSupport
{

    // ---------------------------------------------------------------------
    // Dependency
    // ---------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    // ---------------------------------------------------------------------
    // Input && Output
    // ---------------------------------------------------------------------

    private Integer organisationGroupId;

    // ---------------------------------------------------------------------
    // Getters && Setters
    // ---------------------------------------------------------------------

    public void setOrganisationGroupId( Integer organisationGroupId )
    {
        this.organisationGroupId = organisationGroupId;
    }

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // ---------------------------------------------------------------------
    // Action implementation
    // ---------------------------------------------------------------------

    public String execute()
    {
        try
        {
            Period period = selectionManager.getSelectedPeriod();

            this.installExcelFormat();

            this.installPeriod( period );

            OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService
                .getOrganisationUnitGroup( organisationGroupId.intValue() );

            Set<OrganisationUnit> organisationList = organisationUnitGroup.getMembers();

            statementManager.initialise();
            
            ReportExcelNormal reportExcel = (ReportExcelNormal) reportService.getReportExcel( selectionManager
                .getSelectedReportExcelId() );

            Collection<ReportExcelItem> reportExcelItems = reportExcel.getReportExcelItems();

            this.installReadTemplateFile( reportExcel, period, organisationUnitGroup );

            for ( ReportExcelItem reportItem : reportExcelItems )
            {
                double value = 0;

                Iterator<OrganisationUnit> iter = organisationList.iterator();

                while ( iter.hasNext() )
                {

                    OrganisationUnit organisationUnit = iter.next();

                    value += getDataValue( reportItem, organisationUnit );
                }

                WritableSheet sheet = outputReportWorkbook.getSheet( reportItem.getSheetNo() - 1 );

                ExcelUtils.writeValue( reportItem.getRow(), reportItem.getColumn(), String.valueOf( value ),
                    ExcelUtils.NUMBER, sheet, number );

            }

            outputReportWorkbook.write();

            outputReportWorkbook.close();

            outputXLS = outputReportFile.getName();

            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

            outputReportFile.delete();

            statementManager.destroy();

            return SUCCESS;
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        return ERROR;
    }

}
