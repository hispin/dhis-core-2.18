package org.hisp.dhis.reportexcel.action;

import java.io.File;

import org.hisp.dhis.reportexcel.ReportLocationManager;
import org.hisp.dhis.reportexcel.state.SelectionManager;
import org.hisp.dhis.system.util.StreamUtils;
import com.opensymphony.xwork2.Action;

public class UploadFileAction
    implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportLocationManager reportLocationManager;

    public void setReportLocationManager( ReportLocationManager reportLocationManager )
    {
        this.reportLocationManager = reportLocationManager;
    }

    private SelectionManager selectionManager;

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private String fileName;

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private File upload;

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    public String execute()
        throws Exception
    {
        File directory = reportLocationManager.getReportExcelTempDirectory();

        File output = new File( directory, (Math.random() * 1000) + fileName );

        selectionManager.setUploadFilePath( output.getAbsolutePath() );

        StreamUtils.write( upload, output );

        return SUCCESS;
    }
}
