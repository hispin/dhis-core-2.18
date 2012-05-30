package org.hisp.dhis.api.utils;

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

import javassist.util.proxy.ProxyObject;
import org.hisp.dhis.api.webdomain.Resource;
import org.hisp.dhis.api.webdomain.Resources;
import org.hisp.dhis.common.BaseCollection;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.metadata.ExchangeClasses;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageConversations;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class WebLinkPopulator
{
    private String rootPath;

    public WebLinkPopulator( HttpServletRequest request )
    {
        rootPath = createRootPath( request );
    }

    public void addLinks( Object source )
    {
        if ( source instanceof Resources )
        {
            populateResources( (Resources) source );
        }
        else if ( source instanceof MessageConversations )
        {
            populateMessageConversations( (MessageConversations) source, true );
        }
        else if ( source instanceof MessageConversation )
        {
            populateMessageConversation( (MessageConversation) source, true );
        }

        if ( source instanceof BaseCollection )
        {
            BaseCollection baseCollection = (BaseCollection) source;

            if ( baseCollection.getPager() != null )
            {
                String basePath = getBasePath( source.getClass() );
                Pager pager = baseCollection.getPager();

                if ( pager.getPage() < pager.getPageCount() )
                {
                    pager.setNextPage( basePath + "?page=" + (pager.getPage() + 1) );
                }

                if ( pager.getPage() > 1 )
                {
                    if ( (pager.getPage() - 1) == 1 )
                    {
                        pager.setPrevPage( basePath );
                    }
                    else
                    {
                        pager.setPrevPage( basePath + "?page=" + (pager.getPage() - 1) );
                    }

                }
            }
        }
    }

    private void populateMessageConversations( MessageConversations messageConversations, boolean root )
    {
        messageConversations.setLink( getBasePath( messageConversations.getClass() ) );

        if ( root )
        {
            for ( MessageConversation messageConversation : messageConversations.getMessageConversations() )
            {
                populateMessageConversation( messageConversation, false );
            }
        }
    }

    private void populateMessageConversation( MessageConversation messageConversation, boolean root )
    {
        populateIdentifiableObject( messageConversation );

        if ( root )
        {
            handleIdentifiableObjectCollection( messageConversation.getUsers() );
        }
    }

    private void populateResources( Resources resources )
    {
        resources.setLink( getBasePath( Resources.class ) );

        for ( Resource resource : resources.getResources() )
        {
            resource.setLink( getBasePath( resource.getClazz() ) );
        }
    }

    public void handleIdentifiableObjectCollection( Collection<? extends BaseIdentifiableObject> identifiableObjects )
    {
        if ( identifiableObjects != null )
        {
            for ( BaseIdentifiableObject baseIdentifiableObject : identifiableObjects )
            {
                populateIdentifiableObject( baseIdentifiableObject );
            }
        }
    }

    private void populateIdentifiableObject( BaseIdentifiableObject baseIdentifiableObject )
    {
        if ( baseIdentifiableObject != null )
        {
            baseIdentifiableObject.setLink( getPathWithUid( baseIdentifiableObject ) );
        }
    }

    private String getPathWithUid( BaseIdentifiableObject baseIdentifiableObject )
    {
        return getBasePath( baseIdentifiableObject.getClass() ) + "/" + baseIdentifiableObject.getUid();
    }

    private String getBasePath( Class<?> clazz )
    {
        if ( ProxyObject.class.isAssignableFrom( clazz ) )
        {
            clazz = clazz.getSuperclass();
        }

        String resourcePath = getPath( clazz );

        return rootPath + "/" + resourcePath;
    }

    public static String createRootPath( HttpServletRequest request )
    {
        StringBuilder builder = new StringBuilder();
        String xForwardedProto = request.getHeader( "X-Forwarded-Proto" );
        String xForwardedPort = request.getHeader( "X-Forwarded-Port" );

        if ( xForwardedProto != null && (xForwardedProto.equalsIgnoreCase( "http" ) || xForwardedProto.equalsIgnoreCase( "https" )) )
        {
            builder.append( xForwardedProto );
        }
        else
        {
            builder.append( request.getScheme() );
        }


        builder.append( "://" ).append( request.getServerName() );

        int port;

        try
        {
            port = Integer.parseInt( xForwardedPort );
        } catch ( NumberFormatException e )
        {
            port = request.getServerPort();
        }

        if ( port != 80 && port != 443 )
        {
            builder.append( ":" ).append( port );
        }

        builder.append( request.getContextPath() );
        builder.append( request.getServletPath() );

        return builder.toString();
    }

    public static String getPath( Class<?> clazz )
    {
        return ExchangeClasses.getExportMap().get( clazz );
    }
}
