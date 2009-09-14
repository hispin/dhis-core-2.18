package org.hisp.dhis.reportexcel;

import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.UserAuthorityGroup;

public class ReportExcelNormal
    extends ReportExcel
{
    public ReportExcelNormal()
    {
        super();
    }

    public ReportExcelNormal( String name, String excelTemplateFile, int periodRow, int periodColumn, int organisationRow,
        int organisationColumn, Set<ReportExcelItem> reportItems, Set<OrganisationUnit> organisationAssocitions,
        Set<UserAuthorityGroup> userRoles, String group )
    {
        super( name, excelTemplateFile, periodRow, periodColumn, organisationRow, organisationColumn, reportItems,
            organisationAssocitions, userRoles, group );
    }

    @Override
    public String getReportType()
    {       
        return ReportExcel.TYPE.NORMAL;
    }
}
