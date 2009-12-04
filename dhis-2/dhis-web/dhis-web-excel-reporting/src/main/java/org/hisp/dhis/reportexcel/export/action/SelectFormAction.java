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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.ReportLocationManager;
import org.hisp.dhis.reportexcel.period.db.PeriodDatabaseService;
import org.hisp.dhis.system.util.CodecUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2009-11-26
 */
public class SelectFormAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ReportExcelService reportService;

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    private ReportLocationManager reportLocationManager;

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    private PeriodDatabaseService periodDatabaseService;

    public void setPeriodDatabaseService( PeriodDatabaseService periodDatabaseService )
    {
        this.periodDatabaseService = periodDatabaseService;
    }

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Period> periods;

    private OrganisationUnit organisationUnit;

    private List<String> groups;

    private String reportGroup;

    private Integer reportId;

    private HSSFWorkbook templateWorkbook;

    private FileInputStream inputStreamExcelTemplate;

    private Map<Integer, String> mapSheets = new HashMap<Integer, String>();

    private Collection<Integer> collectSheets = new HashSet<Integer>();

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public List<String> getGroups()
    {
        return groups;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public Integer getReportId()
    {
        return reportId;
    }

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    public String getReportGroup()
    {
        return reportGroup;
    }

    public void setReportGroup( String reportGroup )
    {
        this.reportGroup = reportGroup;
    }

    public Collection<Integer> getCollectSheets()
    {
        return collectSheets;
    }

    public Map<Integer, String> getMapSheets()
    {
        return mapSheets;
    }

    public List<Period> getPeriods()
    {
        return periods;
    }

    private File fileExcel;

    public File getFileExcel()
    {
        return fileExcel;
    }

    public void setFileExcel( File fileExcel )
    {
        this.fileExcel = fileExcel;
    }

    public String execute()
        throws Exception
    {
        organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        if ( organisationUnit == null )
        {
            return SUCCESS;
        }

        periodDatabaseService.setSelectedPeriodTypeName( MonthlyPeriodType.NAME );

        periods = periodDatabaseService.getPeriodList();

        groups = new ArrayList<String>( reportService.getReportExcelGroups() );

        Collections.sort( groups );

        // ---------------------------------------------------------------------
        // Processing Tabs for Previewing excel report
        // ---------------------------------------------------------------------

        if ( reportGroup != null )
        {
            reportGroup = CodecUtils.unescape( reportGroup );
        }

        if ( reportId != null )
        {
            inputStreamExcelTemplate = new FileInputStream( reportLocationManager.getReportExcelTemplateDirectory()
                + File.separator + reportService.getReportExcel( reportId ).getExcelTemplateFile() );

            templateWorkbook = new HSSFWorkbook( inputStreamExcelTemplate );

            collectSheets = reportService.getSheets( reportId );

            for ( Integer sheetId : collectSheets )
            {
                mapSheets.put( sheetId, CodecUtils.unescape( templateWorkbook.getSheetName( sheetId.intValue() ) ) );
            }
        }
        else
        {
            collectSheets.add( 0 );
            mapSheets.put( 0, "" );
        }

        return SUCCESS;
    }

}
