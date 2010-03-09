package org.hisp.dhis.patient.paging;
/*
 * Copyright (c) 2004-2009, University of Oslo
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

public class RequestUtil
{
    public RequestUtil()
    {
    }

    public static String getBaseLink( HttpServletRequest request )
    {
        String baseLink = (new StringBuilder( String.valueOf( request.getScheme() ) )).append( "://" ).append(
            request.getServerName() ).append(
            request.getServerPort() == 80 ? "" : (new StringBuilder( ":" )).append( request.getServerPort() )
                .toString() ).append( StringUtils.isBlank( request.getContextPath() ) ? "/" : request.getContextPath() )
            .append( request.getServletPath() ).append( "/" ).toString();
        return baseLink;
    }

    public static String getRootLink( HttpServletRequest request )
    {
        String baseLink = (new StringBuilder( String.valueOf( request.getScheme() ) )).append( "://" ).append(
            request.getServerName() ).append(
            request.getServerPort() == 80 ? "" : (new StringBuilder( ":" )).append( request.getServerPort() )
                .toString() ).append( StringUtils.isBlank( request.getContextPath() ) ? "/" : request.getContextPath() )
            .append( "/" ).toString();
        return baseLink;
    }

    public static String getCurrentLink( HttpServletRequest request )
    {
        return request.getServletPath();
    }

    public static String getPathInfo( HttpServletRequest request )
    {
        return request.getPathInfo().substring( 1 );
    }

    public static String getSessionString( HttpServletRequest request, String attributeName )
    {
        HttpSession session = request.getSession();
        if ( session.getAttribute( attributeName ) == null )
            return null;
        else
            return session.getAttribute( attributeName ).toString();
    }

    public static Object getSessionObject( HttpServletRequest request, String attributeName, Class clazz )
    {
        HttpSession session = request.getSession();
        if ( session != null )
        {
            Object object = session.getAttribute( attributeName );
            if ( object != null )
                try
                {
                    return object;
                }
                catch ( Exception exception )
                {
                }
        }
        return null;
    }

    public static void removeAttribute( HttpServletRequest request, String attributeName )
    {
        HttpSession session = request.getSession( false );
        if ( session != null )
            session.removeAttribute( attributeName );
    }

    public static String getSessionStringAndRemove( HttpServletRequest request, String attributeName )
    {
        String value = getSessionString( request, attributeName );
        removeAttribute( request, attributeName );
        return value;
    }

    public static String unescapeUrl( String url )
    {
        url = StringUtils.trimToNull( url );
        if ( StringUtils.isBlank( url ) )
            return null;
        else
            return url.replaceAll( "%3A", ":" ).replaceAll( "%3F", "?" ).replaceAll( "%3D", "=" ).replaceAll( "%26",
                "&" );
    }
}
