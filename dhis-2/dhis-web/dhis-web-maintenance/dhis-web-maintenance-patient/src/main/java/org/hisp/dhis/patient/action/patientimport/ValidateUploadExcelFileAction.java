/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.patient.action.patientimport;

import java.io.File;

import org.hisp.dhis.i18n.I18n;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version ValidateUploadExcelFileAction.java Nov 16, 2010 1:19:57 PM
 */
public class ValidateUploadExcelFileAction
    implements Action
{
    private static final String CONTENT_TYPE_DEFAULT = "application/vnd.ms-excel";

    private static final String CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String uploadContentType;

    public void setUploadContentType( String uploadContentType )
    {
        this.uploadContentType = uploadContentType;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private File upload;

    public File getUpload()
    {
        return upload;
    }

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( upload == null || !upload.exists() )
        {
            message = i18n.getString( "upload_file_null" );

            return ERROR;
        }

        if ( !CONTENT_TYPE_DEFAULT.equals( uploadContentType ) && !CONTENT_TYPE_OCTET_STREAM.equals( uploadContentType ) )
        {
            message = i18n.getString( "file_type_not_supported" );

            return ERROR;
        }

        return SUCCESS;
    }
}
