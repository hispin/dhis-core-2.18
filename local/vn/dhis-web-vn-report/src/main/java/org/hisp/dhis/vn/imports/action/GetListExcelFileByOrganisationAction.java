package org.hisp.dhis.vn.imports.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.vn.report.action.ActionSupport;
import org.hisp.dhis.vn.report.state.ReportLocationManager;
import org.hisp.dhis.vn.report.utils.MyFile;

public class GetListExcelFileByOrganisationAction
    extends ActionSupport
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportLocationManager reportLocationManager;

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    // -------------------------------------------
    // Output
    // -------------------------------------------

    private List<MyFile> files;

    private OrganisationUnit organisationUnit;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public List<MyFile> getFiles()
    {
        return files;
    }

    public String execute()
        throws Exception
    {
        organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        if ( organisationUnit != null )
        {
            files = new ArrayList<MyFile>();

            Calendar calendar = Calendar.getInstance();

            for ( File f : reportLocationManager.getListFile( organisationUnit ) )
            {
                calendar.setTimeInMillis( f.lastModified() );
                double size = f.length() / 1024;
                MyFile m = new MyFile( f.getName(), format.formatDateTime( calendar.getTime() ), size );
                files.add( m );
            }
        }

        return SUCCESS;
    }
}
