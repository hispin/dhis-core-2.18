package org.hisp.dhis.vn.chr.configuration.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.options.SystemSettingManager;

import com.opensymphony.xwork2.Action;

public class GetFormConfigurationAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private String imageDirectoryOnServer;

    public String getImageDirectoryOnServer()
    {
        return imageDirectoryOnServer;
    }

    private String numberOfRecords;

    public String getNumberOfRecords()
    {
        return numberOfRecords;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        imageDirectoryOnServer = (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_CHR_IMAGE_DIRECTORY );

        numberOfRecords = (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_CHR_NUMBER_OF_RECORDS );

        return SUCCESS;
    }
}
