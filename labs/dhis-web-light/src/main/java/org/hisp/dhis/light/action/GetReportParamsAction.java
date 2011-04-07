package org.hisp.dhis.light.action;

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

import java.util.Calendar;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportParams;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class GetReportParamsAction
    implements Action
{
    private static final int AVAILABLE_REPORTING_MONTHS = 24;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportTableService reportTableService;

    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }
    
    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }
        
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private ReportParams reportParams;

    public ReportParams getReportParams()
    {
        return reportParams;
    }
        
    private SortedMap<Integer, String> reportingPeriods = new TreeMap<Integer, String>();

    public SortedMap<Integer, String> getReportingPeriods()
    {
        return reportingPeriods;
    }
    
    private Report report;

    public Report getReport()
    {
        return report;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        selectionTreeManager.setCurrentUserOrganisationUnitAsSelected();
        
        if ( id != null )
        {
            ReportTable reportTable = reportTableService.getReportTable( id, ReportTableService.MODE_REPORT_TABLE );
            
            if ( reportTable != null )
            {
                reportParams = reportTable.getReportParams();
                                
                if ( reportParams.isParamReportingMonth() )
                {
                    MonthlyPeriodType periodType = new MonthlyPeriodType();
                    
                    Calendar cal = PeriodType.createCalendarInstance();
                    
                    for ( int i = 0; i < AVAILABLE_REPORTING_MONTHS; i++ )
                    {
                        int month = i + 1;    
                        cal.add( Calendar.MONTH, -1 );                    
                        Period period = periodType.createPeriod( cal.getTime() );                    
                        String periodName = format.formatPeriod( period );
                        
                        reportingPeriods.put( month, periodName );
                    }                
                }
            }
        }
        
        return SUCCESS;
    }
}
