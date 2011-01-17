package org.hisp.dhis.reportexcel.action;

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

import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class UpdateReportExcelAction
    implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private Integer id;

    private String name;

    private String excel;

    private Integer periodRow;

    private Integer periodCol;

    private Integer organisationRow;

    private Integer organisationCol;

    private ReportExcel report;

    private String group;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public ReportExcel getReport()
    {
        return report;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setExcel( String excel )
    {
        this.excel = excel;
    }

    public void setPeriodRow( Integer periodRow )
    {
        this.periodRow = periodRow;
    }

    public void setPeriodCol( Integer periodCol )
    {
        this.periodCol = periodCol;
    }

    public void setOrganisationRow( Integer organisationRow )
    {
        this.organisationRow = organisationRow;
    }

    public void setOrganisationCol( Integer organisationCol )
    {
        this.organisationCol = organisationCol;
    }

    // -------------------------------------------
    // Action implement
    // -------------------------------------------

    public String execute()
        throws Exception
    {
        report = reportService.getReportExcel( id );

        report.setExcelTemplateFile( excel );
        report.setGroup( group );
        report.setName( name );

        if ( periodCol == null || periodRow == null )
        {
            report.setPeriodColumn( null );
            report.setPeriodRow( null );
        }
        else
        {
            report.setPeriodColumn( periodCol );
            report.setPeriodRow( periodRow );
        }
        if ( organisationCol == null || organisationRow == null )
        {
            report.setOrganisationColumn( null );
            report.setOrganisationRow( null );
        }
        else
        {
            report.setOrganisationColumn( organisationCol );
            report.setOrganisationRow( organisationRow );
        }

        reportService.updateReportExcel( report );

        return SUCCESS;
    }
}
