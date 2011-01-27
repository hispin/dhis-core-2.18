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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ContextUtils
{
    public static final String CONTENT_TYPE_PDF = "application/pdf";
    public static final String CONTENT_TYPE_ZIP = "application/zip";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_CSV = "application/csv";
    public static final String CONTENT_TYPE_PNG = "image/png";
    public static final String CONTENT_TYPE_EXCEL = "application/vnd.ms-excel";
    
    private static final String SEPARATOR = "/";
    private static final String PORT_SEPARATOR = ":";    
    private static final String PROTOCOL = "http://";
    
    @SuppressWarnings( "unchecked" )
    public static Map<String, String> getParameterMap( HttpServletRequest request )
    {
        Enumeration<String> enumeration = request.getParameterNames();
        
        Map<String, String> params = new HashMap<String, String>();
        
        while ( enumeration.hasMoreElements() )
        {
            String name = enumeration.nextElement();
            
            params.put( name, request.getParameter( name ) );
        }
        
        return params;
    }
    
    public static String getBaseUrl( HttpServletRequest request )
    {
        String server = request.getServerName();
        
        int port = request.getServerPort();
        
        String baseUrl = PROTOCOL + server + PORT_SEPARATOR + port + SEPARATOR;
        
        return baseUrl;
    }
    
    public static void configureResponse( HttpServletResponse response, String contentType, boolean disallowCache, String filename, boolean attachment )
    {
        if ( contentType != null )
        {
            response.setContentType( contentType );
        }
        
        if ( disallowCache )
        {   
            response.addHeader( "Cache-Control", "no-cache" );
            response.addHeader( "Expires", DateUtils.getExpiredHttpDateString() );
        } 

        if ( filename != null )
        {
            String type = attachment ? "attachment" : "inline";
            
            response.addHeader( "Content-Disposition", type + "; filename=\"" + filename + "\"" );
        }
    }
}
