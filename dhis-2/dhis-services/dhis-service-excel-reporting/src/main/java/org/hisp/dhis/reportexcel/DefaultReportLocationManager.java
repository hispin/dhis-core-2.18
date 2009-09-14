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
package org.hisp.dhis.reportexcel;

import java.io.File;
import java.util.List;

import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */

public class DefaultReportLocationManager
    implements ReportLocationManager
{
    private File REPORT;

    private File REPORT_TEMP;

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private LocationManager locationManager;

    private SystemSettingManager systemSettingManager;

    // -------------------------------------------
    // Setter
    // -------------------------------------------

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------
    // Init
    // -------------------------------------------

    void init()
    {
        try
        {
            REPORT = locationManager.buildDirectory( ReportLocationManager.REPORT_DIR);
            REPORT_TEMP = locationManager.buildDirectory( ReportLocationManager.REPORT_DIR, ReportLocationManager.REPORT_TEMP_DIR );
           
        }
        catch ( LocationManagerException e )
        {
            e.printStackTrace();
        }

    }

    // -------------------------------------------
    // Impletemented
    // -------------------------------------------

    public List<File> getListFileInOrganisationDirectory( OrganisationUnit arg0 )
    {
        return null;
    }

    public File getOrganisationDirectory( OrganisationUnit organisationUnit )
    {        
        try
        {
            return locationManager.buildDirectory(ReportLocationManager.REPORT_DIR, String.valueOf( organisationUnit.getId() ));
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }

    public File getReportExcelDirectory()
    {     
        return this.REPORT;
    }

    public File getReportExcelTempDirectory()
    {       
        return this.REPORT_TEMP;
    }

    public File getReportExcelTemplateDirectory()
    {
        return new File( (String) systemSettingManager
            .getSystemSetting( SystemSettingManager.KEY_REPORT_TEMPLATE_DIRECTORY ) );
    }

}
