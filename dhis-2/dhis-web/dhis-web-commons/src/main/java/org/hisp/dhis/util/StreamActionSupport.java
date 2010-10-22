package org.hisp.dhis.util;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.system.util.StreamUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public abstract class StreamActionSupport
    extends ActionSupport
{
    protected static final String CONTENT_TYPE_PDF = "application/pdf";
    protected static final String CONTENT_TYPE_ZIP = "application/zip";
    protected static final String CONTENT_TYPE_JSON = "application/json";
    protected static final String CONTENT_TYPE_HTML = "text/html";
    protected static final String CONTENT_TYPE_TEXT = "text/plain";
    protected static final String CONTENT_TYPE_XML = "text/xml";
    protected static final String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
    
    // -------------------------------------------------------------------------
    // ActionSupport implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OutputStream out = null;
        
        HttpServletResponse response = ServletActionContext.getResponse();
        
        if ( getContentType() != null )
        {
            response.setContentType( getContentType() );
        }
        
        if ( getFilename() != null )
        {
            response.addHeader( "Content-Disposition", "attachment; filename=\"" + getFilename() + "\"" );
        }
        
        if ( disallowCache() )
        {
            response.addHeader( "Cache-Control", "no-cache" );
        }
        
        try
        {
            out = response.getOutputStream();
            
            return execute( response, out );
        }
        finally
        {
            StreamUtils.closeOutputStream( out );
        }
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract String execute( HttpServletResponse response, OutputStream out )
        throws Exception;
    
    protected abstract String getContentType();
    
    protected abstract String getFilename();
    
    protected boolean disallowCache()
    {
        return false;
    }
}
