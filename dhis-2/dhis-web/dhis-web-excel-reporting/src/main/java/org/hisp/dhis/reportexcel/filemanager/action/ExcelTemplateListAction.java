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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.reportexcel.ReportLocationManager;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.state.SelectionManager;
import org.hisp.dhis.reportexcel.utils.ExcelFileFilter;
import org.hisp.dhis.reportexcel.utils.FileUtils;
import org.hisp.dhis.system.comparator.FileNameComparator;

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
    // Output
    // -------------------------------------------

    private List<File> templateFiles = new ArrayList<File>();

    // -------------------------------------------
    // Getter && Setter
    // -------------------------------------------

    private String newFileUploaded;

    public String getNewFileUploaded()
    {
        return newFileUploaded;
    }

    public List<File> getTemplateFiles()
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

        File templateDirectory = reportLocationManager.getReportExcelTemplateDirectory();

        if ( !templateDirectory.exists() )
        {
            return SUCCESS;
        }

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Get the path of newly uploaded file
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        String newUploadedPath = selectionManager.getUploadFilePath();

        if ( (newUploadedPath != "") && (newUploadedPath != null) )
        {
            newFileUploaded = new File( newUploadedPath ).getName();
        }

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // Get the list of files
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        templateFiles = FileUtils.getListFile( templateDirectory, new ExcelFileFilter() );

        Collections.sort( templateFiles, new FileNameComparator() );

        return SUCCESS;
    }

}
