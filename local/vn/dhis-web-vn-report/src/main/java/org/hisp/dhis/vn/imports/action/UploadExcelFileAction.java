package org.hisp.dhis.vn.imports.action;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.vn.report.state.ReportLocationManager;

import com.opensymphony.xwork2.Action;

public class UploadExcelFileAction
    implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportLocationManager reportLocationManager;

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private File upload;

    private String fileName;

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        if ( organisationUnit == null )
            return SUCCESS;

        File directory = reportLocationManager.getDirectory( organisationUnit );

        if ( upload != null )
        {

            try
            {
                FileReader in = new FileReader( upload );

                FileWriter out = new FileWriter( new File( directory, fileName ) );

                int c;

                while ( (c = in.read()) != -1 )
                    out.write( c );

                in.close();
                out.close();

            }
            catch ( IOException e )
            {
                e.printStackTrace();
                return NONE;
            }

        }

        return SUCCESS;
    }
}
