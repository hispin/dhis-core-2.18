package org.hisp.dhis.reportexcel.configuration.action;

import org.hisp.dhis.options.SystemSettingManager;

import com.opensymphony.xwork2.Action;

public class SetReportConfigurationAction
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
    // Output
    // -------------------------------------------------------------------------

    private String templateDirectory;

    public void setTemplateDirectory( String templateDirectory )
    {
        this.templateDirectory = templateDirectory;
    }

    public String execute()
        throws Exception
    {
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_REPORT_TEMPLATE_DIRECTORY, templateDirectory );

        return SUCCESS;
    }
}
