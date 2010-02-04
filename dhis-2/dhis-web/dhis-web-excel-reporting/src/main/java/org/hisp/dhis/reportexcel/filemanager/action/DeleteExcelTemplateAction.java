package org.hisp.dhis.reportexcel.filemanager.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import java.io.File;
import java.util.Collection;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.utils.FileUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id
 * @since 2010-01-27
 */
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

        Collection<ReportExcel> reports = reportService.getALLReportExcel();
        
        for ( ReportExcel report : reports )
        {
            String name = report.getExcelTemplateFile();
            
            if ( name.equals( fileName ) )
            {
                message += " - " + report.getName() + "<br>";
            }
        }

        if ( message.length() > 0 )
        {
            message = i18n.getString( "report_user_template" ) + "<br>" + message;
            
            return ERROR;
        }

        String templateDirectory = (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_REPORT_TEMPLATE_DIRECTORY );
        
        FileUtils.delete( templateDirectory + File.separator + fileName );

        return SUCCESS;
    }

}
