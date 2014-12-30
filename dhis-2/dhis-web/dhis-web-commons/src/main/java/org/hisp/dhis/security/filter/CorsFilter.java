package org.hisp.dhis.security.filter;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class CorsFilter implements Filter
{
    public static final String CORS_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    public static final String CORS_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    public static final String CORS_MAX_AGE = "Access-Control-Max-Age";

    public static final String CORS_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    public static final String CORS_REQUEST_HEADERS = "Access-Control-Request-Headers";

    public static final String CORS_ALLOW_METHODS = "Access-Control-Allow-Methods";

    public static final String CORS_REQUEST_METHOD = "Access-Control-Request-Method";

    @Override
    public void doFilter( ServletRequest req, ServletResponse res, FilterChain filterChain ) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader( "Origin" );
        origin = !StringUtils.isEmpty( origin ) ? origin : "*";

        String exposeHeaders = request.getHeader( CORS_REQUEST_HEADERS );
        exposeHeaders = !StringUtils.isEmpty( exposeHeaders ) ? exposeHeaders : "accept authorization";

        String allowMethods = request.getHeader( CORS_REQUEST_METHOD );
        allowMethods = !StringUtils.isEmpty( allowMethods ) ? allowMethods : "GET, POST, PUT, DELETE, OPTIONS";

        response.addHeader( CORS_ALLOW_CREDENTIALS, "true" );
        response.addHeader( CORS_ALLOW_ORIGIN, origin );
        response.addHeader( CORS_ALLOW_METHODS, allowMethods );
        response.addHeader( CORS_MAX_AGE, "3600" );
        response.addHeader( CORS_ALLOW_HEADERS, exposeHeaders );

        if ( "OPTIONS".equals( request.getMethod() ) )
        {
            response.setStatus( HttpServletResponse.SC_OK );
            response.getWriter().print( "OK" );
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter( request, response );
    }

    @Override
    public void init( FilterConfig filterConfig ) throws ServletException
    {
    }

    @Override
    public void destroy()
    {
    }
}
