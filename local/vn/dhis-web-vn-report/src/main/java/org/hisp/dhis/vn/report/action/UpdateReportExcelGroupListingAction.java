package org.hisp.dhis.vn.report.action;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.vn.report.ReportExcelGroupListing;
import org.hisp.dhis.vn.report.ReportExcelService;

import com.opensymphony.xwork.Action;

public class UpdateReportExcelGroupListingAction
    implements Action
{

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportService;

    private OrganisationUnitGroupService organisationUnitGroupService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private int id;

    private Collection<String> selectedGroups;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public ReportExcelService getReportService()
    {
        return reportService;
    }

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Collection<String> getSelectedGroups()
    {
        return selectedGroups;
    }

    public void setSelectedGroups( Collection<String> selectedGroups )
    {
        this.selectedGroups = selectedGroups;
    }

    public OrganisationUnitGroupService getOrganisationUnitGroupService()
    {
        return organisationUnitGroupService;
    }

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------
    // execute method
    // -------------------------------------------

    public String execute()
        throws Exception
    {

        // System.out.print("\n\n\n ID : " + id);

        ReportExcelGroupListing report = (ReportExcelGroupListing) reportService.getReport( id );

        List<OrganisationUnitGroup> organisationUnitGroups = new Vector<OrganisationUnitGroup>();// report.getOrganisationUnitGroups();

        for ( String group : selectedGroups )
        {
            organisationUnitGroups.add( organisationUnitGroupService
                .getOrganisationUnitGroup( Integer.valueOf( group ) ) );
        }

        report.setOrganisationUnitGroups( organisationUnitGroups );

        reportService.updateReport( report );

        return SUCCESS;
    }

}
