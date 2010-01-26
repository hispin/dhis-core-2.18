package org.hisp.dhis.reportexcel.filemanager.action;

import java.io.File;
import java.util.Collection;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.utils.FileUtils;

import com.opensymphony.xwork2.Action;

public class DeleteExcelTemplateAction
    implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private SystemSettingManager systemSettingManager;
    
    private ReportExcelService reportService;

    // -------------------------------------------
    // Input
    // -------------------------------------------

    private String fileName;
    
    // -------------------------------------------
    // Output
    // -------------------------------------------

    private String message;
    
    private I18n i18n;

    // -------------------------------------------
    // Getter && Setter
    // -------------------------------------------

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }
    
    // -------------------------------------------
    // Action implementation
    // -------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        message = "";
        
        Collection<ReportExcel> reports =  reportService.getALLReportExcel();
        for(ReportExcel report : reports){
            String name = report.getExcelTemplateFile();
            if(name.equals( fileName )){
                message += " - " + report.getName() + "<br>";
            }
        }
        
        if(message.length()>0){
            message = i18n.getString( "report_user_template" ) + "<br>" + message;
            return ERROR;
        }
        
        String templateDirectory = (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_REPORT_TEMPLATE_DIRECTORY );

 System.out.println("\n\n\n === File : " + templateDirectory + File.separator + fileName);       
        FileUtils.delete(  templateDirectory + File.separator + fileName );

        return SUCCESS;
    }

}
