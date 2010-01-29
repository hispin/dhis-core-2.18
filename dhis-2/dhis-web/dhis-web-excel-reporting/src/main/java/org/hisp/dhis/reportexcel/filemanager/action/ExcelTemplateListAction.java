package org.hisp.dhis.reportexcel.filemanager.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.state.SelectionManager;
import org.hisp.dhis.reportexcel.utils.FileUtils;

/**
 * @author Chau Thu Tran
 * @author Dang Duy Hieu
 * @version $Id
 * @since 2010-01-27
 */

public class ExcelTemplateListAction
    extends ActionSupport
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private SelectionManager selectionManager;

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------
    // Output
    // -------------------------------------------
    private Collection<File> templateFiles = new ArrayList<File>();

    // -------------------------------------------
    // Getter && Setter
    // -------------------------------------------

    private String newFileUploaded;

    public String getNewFileUploaded()
    {
        return newFileUploaded;
    }

    public Collection<File> getTemplateFiles()
    {
        return templateFiles;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------
    // Action implementation
    // -------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Get the path of newly uploaded file
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        String newUploadedPath = selectionManager.getUploadFilePath();
        
        if ( !newUploadedPath.isEmpty() && (newUploadedPath != null) )
        {
            newFileUploaded = new File( newUploadedPath ).getName();
        }

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Get the list of files
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        String templateDirectory = (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_REPORT_TEMPLATE_DIRECTORY );

        templateFiles = FileUtils.getListFile( new File( templateDirectory ) );

        return SUCCESS;
    }


}
