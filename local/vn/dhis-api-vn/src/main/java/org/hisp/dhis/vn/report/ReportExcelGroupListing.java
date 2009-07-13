/**
 * @author Chau Thu Tran
 */

package org.hisp.dhis.vn.report;

import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.vn.report.ReportExcel;

public class ReportExcelGroupListing
    extends ReportExcel
{
    private List<OrganisationUnitGroup> organisationUnitGroups;

    public ReportExcelGroupListing()
    {
        super();
    }

    public ReportExcelGroupListing( String name, String excelTemplateFile, int periodRow, int periodColumn,
        int organisationRow, int organisationColumn )
    {
        super( name, excelTemplateFile, periodRow, periodColumn, organisationRow, organisationColumn );

    }

    public String getReportType()
    {
        return ReportExcel.TYPE.GROUP_LISTING;
    }

    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroups( List<OrganisationUnitGroup> organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }

}
