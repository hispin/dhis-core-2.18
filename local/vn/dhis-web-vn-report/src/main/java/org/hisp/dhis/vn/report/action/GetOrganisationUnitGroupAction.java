package org.hisp.dhis.vn.report.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitGroupNameComparator;
import org.hisp.dhis.vn.report.ReportExcelGroupListing;
import org.hisp.dhis.vn.report.ReportExcelInterface;

import com.opensymphony.xwork2.Action;

import edu.emory.mathcs.backport.java.util.Collections;

public class GetOrganisationUnitGroupAction
    implements Action
{

    private OrganisationUnitGroupService organisationUnitGroupService;

    private List<OrganisationUnitGroup> organisationUnitGroups;

    private List<OrganisationUnitGroup> reportOrganisationUnitGroups;

    private ReportExcelInterface report;

    public ReportExcelInterface getReport()
    {
        return report;
    }

    public void setReport( ReportExcelInterface report )
    {
        this.report = report;
    }

    public Collection<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    public List<OrganisationUnitGroup> getReportOrganisationUnitGroups()
    {
        return reportOrganisationUnitGroups;
    }

    public String execute()
        throws Exception
    {
        organisationUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService
            .getAllOrganisationUnitGroups() );

        reportOrganisationUnitGroups = ((ReportExcelGroupListing) report).getOrganisationUnitGroups();

        organisationUnitGroups.removeAll( reportOrganisationUnitGroups );

        Collections.sort( organisationUnitGroups, new OrganisationUnitGroupNameComparator() );

        return SUCCESS;
    }
}
