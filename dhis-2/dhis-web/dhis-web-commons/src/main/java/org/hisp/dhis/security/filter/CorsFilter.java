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
import org.springframework.web.bind.annotation.RequestMethod;

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

    public static final String CORS_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    public static final String CORS_REQUEST_HEADERS = "Access-Control-Request-Headers";

    public static final String CORS_ALLOW_METHODS = "Access-Control-Allow-Methods";

    public static final String CORS_REQUEST_METHOD = "Access-Control-Request-Method";

    public static final String CORS_ORIGIN = "Origin";

    private static final String ALLOWED_METHODS = "GET, OPTIONS";

    private static final String ALLOWED_HEADERS = "Accept, Content-Type, Authorization, X-Requested-With";

    private static final Integer MAX_AGE = 60 * 60; // 1hr max-age

    @Override
    public void doFilter( ServletRequest req, ServletResponse res, FilterChain filterChain ) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader( CORS_ORIGIN );
        origin = !StringUtils.isEmpty( origin ) ? origin : "*";

        response.addHeader( CORS_ALLOW_CREDENTIALS, "true" );
        response.addHeader( CORS_ALLOW_ORIGIN, origin );

        if ( isPreflight( request ) )
        {
            response.addHeader( CORS_ALLOW_METHODS, ALLOWED_METHODS );
            response.addHeader( CORS_ALLOW_HEADERS, ALLOWED_HEADERS );
            response.addHeader( CORS_MAX_AGE, String.valueOf( MAX_AGE ) );

            response.setStatus( HttpServletResponse.SC_NO_CONTENT );
            return; // CORS preflight requires a 2xx status code, so we need to short-circuit the filter chain here
        }

        filterChain.doFilter( request, response );
    }

    private boolean isPreflight( HttpServletRequest request )
    {
        return RequestMethod.OPTIONS.toString().equals( request.getMethod() )
            && !StringUtils.isEmpty( request.getHeader( CORS_ORIGIN ) )
            && !StringUtils.isEmpty( request.getHeader( CORS_REQUEST_METHOD ) );
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
