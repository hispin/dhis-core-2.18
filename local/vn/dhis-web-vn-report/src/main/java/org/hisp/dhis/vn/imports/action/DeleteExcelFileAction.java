package org.hisp.dhis.vn.imports.action;

import java.io.File;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.vn.report.state.ReportLocationManager;

import com.opensymphony.xwork2.Action;

public class DeleteExcelFileAction implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportLocationManager reportLocationManager;

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    // -------------------------------------------
    // Input
    // -------------------------------------------

    private String fileName;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }

    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();
        
        File dir = reportLocationManager.getDirectory( organisationUnit );
        
        File excel = new File(dir,this.fileName);
        
        excel.delete();
        
        return SUCCESS;
    }

}
