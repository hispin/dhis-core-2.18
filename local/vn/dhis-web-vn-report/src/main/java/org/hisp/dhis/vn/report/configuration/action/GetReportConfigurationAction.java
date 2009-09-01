package org.hisp.dhis.vn.report.configuration.action;

import org.hisp.dhis.options.SystemSettingManager;

import com.opensymphony.xwork.Action;

public class GetReportConfigurationAction
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
    
    public String getTemplateDirectory()
    {
        return templateDirectory;
    }



    public String execute()
        throws Exception
    {
        templateDirectory = (String) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_REPORT_TEMPLATE_DIRECTORY );
        
        return SUCCESS;
    }

}
