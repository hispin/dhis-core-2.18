package org.hisp.dhis.reportexcel;

import java.util.List;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.UserAuthorityGroup;

public class ReportExcelCategory
    extends ReportExcel
{
    private List<DataElementGroupOrder> dataElementOrders;

    public ReportExcelCategory()
    {
        super();        
    }

    public ReportExcelCategory( String name, String excelTemplateFile, int periodRow, int periodColumn, int organisationRow,
        int organisationColumn, Set<ReportExcelItem> reportItems, Set<OrganisationUnit> organisationAssocitions,
        Set<UserAuthorityGroup> userRoles, String group )
    {
        super( name, excelTemplateFile, periodRow, periodColumn, organisationRow, organisationColumn, reportItems,
            organisationAssocitions, userRoles, group );        
    }

    public List<DataElementGroupOrder> getDataElementOrders()
    {
        return dataElementOrders;
    }

    public void setDataElementOrders( List<DataElementGroupOrder> dataElementOrders )
    {
        this.dataElementOrders = dataElementOrders;
    }

    @Override
    public String getReportType()
    {
        return ReportExcel.TYPE.CATEGORY;
    }

}
