package org.hisp.dhis.i18n.interceptor;

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

import java.lang.reflect.Method;
import org.hisp.dhis.i18n.I18nService;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author Torgeir Lorange Ostby
 * @author Lars Helge Overland
 * @version $Id: I18nObjectInterceptor.java 5992 2008-10-19 11:52:20Z larshelg $
 */
public class I18nObjectInterceptor
{
    private static final String ADD = "add";

    private static final String UPDATE = "update";

    private static final String SAVE = "save";

    private static final String DELETE = "delete";

    private static final String GET_ID_METHOD = "getId";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // MethodInterceptor implementation
    // -------------------------------------------------------------------------

    public void intercept( ProceedingJoinPoint joinPoint )
        throws Throwable
    {
        String methodName = joinPoint.getSignature().toShortString();

        if ( joinPoint.getArgs() != null && joinPoint.getArgs().length > 0 )
        {
            Object object = joinPoint.getArgs()[0];
        
            if ( methodName.startsWith( ADD ) )
            {
                joinPoint.proceed();

                i18nService.addObject( object );
            }
            else if ( methodName.startsWith( UPDATE ) )
            {
                i18nService.verify( object );

                joinPoint.proceed();
            }
            else if ( methodName.startsWith( DELETE ) )
            {
                joinPoint.proceed();

                i18nService.removeObject( object );
            }
            else if ( methodName.startsWith( SAVE ) )
            {
                Method getIdMethod = object.getClass().getMethod( GET_ID_METHOD, new Class[0] );

                int id = (Integer) getIdMethod.invoke( object, new Object[0] );

                if ( id == 0 )
                {
                    joinPoint.proceed();

                    i18nService.addObject( object );
                }
                else
                {
                    i18nService.verify( object );

                    joinPoint.proceed();
                }
            }
        }
        else
        {
            joinPoint.proceed();
        }
    }
}
