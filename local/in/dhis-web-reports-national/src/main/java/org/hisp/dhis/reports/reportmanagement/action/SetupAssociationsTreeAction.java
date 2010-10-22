package org.hisp.dhis.reports.reportmanagement.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.source.Source;

import com.opensymphony.xwork2.Action;

public class SetupAssociationsTreeAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Input/Output Getters & setters
    // -------------------------------------------------------------------------

    private int reportId;

    public int getReportId()
    {
        return reportId;
    }

    public void setReportId( int reportId )
    {
        this.reportId = reportId;
    }

    private Report_in report;

    public Report_in getReport()
    {
        return report;
    }

    // -------------------------------------------------------------------------
    // ActionSupport implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        report = reportService.getReport( reportId );

        selectionTreeManager.setSelectedOrganisationUnits( convert( report.getSources() ) );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Set<OrganisationUnit> convert( Collection<Source> sources )
    {
        Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

        for ( Source source : sources )
        {
            organisationUnits.add( (OrganisationUnit) source );
        }

        return organisationUnits;
    }
}

