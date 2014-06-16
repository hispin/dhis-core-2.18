package org.hisp.dhis.webapi.utils;

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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 */

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import javassist.util.proxy.ProxyFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.collection.spi.PersistentCollection;
import org.hisp.dhis.schema.Property;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Service
public class DefaultLinkService implements LinkService
{
    private static final Log log = LogFactory.getLog( DefaultLinkService.class );

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private ContextService contextService;

    @Override
    public <T> void generateLinks( T object )
    {
        generateLinks( object, contextService.getServletPath() );
    }

    @Override
    public <T> void generateLinks( T object, String hrefBase )
    {
        if ( Collection.class.isInstance( object ) )
        {
            Collection<?> collection = (Collection<?>) object;

            for ( Object collectionObject : collection )
            {
                generateLink( collectionObject, hrefBase, false );
            }
        }
        else
        {
            generateLink( object, hrefBase, true );
        }
    }

    private <T> void generateLink( T object, String hrefBase, boolean deepScan )
    {
        Schema schema = schemaService.getDynamicSchema( object.getClass() );

        if ( schema == null )
        {
            log.warn( "Could not find schema for object of type " + object.getClass().getName() + "." );
            return;
        }

        generateHref( object, hrefBase );

        if ( !deepScan )
        {
            return;
        }

        for ( Property property : schema.getProperties() )
        {
            try
            {
                // TODO should we support non-idObjects?
                if ( property.isIdentifiableObject() )
                {
                    Object propertyObject = property.getGetterMethod().invoke( object );

                    if ( propertyObject == null )
                    {
                        continue;
                    }

                    // unwrap hibernate PersistentCollection
                    if ( PersistentCollection.class.isAssignableFrom( propertyObject.getClass() ) )
                    {
                        PersistentCollection collection = (PersistentCollection) propertyObject;
                        propertyObject = collection.getValue();
                    }

                    if ( !property.isCollection() )
                    {
                        generateHref( propertyObject, hrefBase );
                    }
                    else
                    {
                        Collection<?> collection = (Collection<?>) propertyObject;

                        for ( Object collectionObject : collection )
                        {
                            generateHref( collectionObject, hrefBase );
                        }
                    }

                }
            }
            catch ( InvocationTargetException | IllegalAccessException ignored )
            {
            }
        }
    }

    private <T> void generateHref( T object, String hrefBase )
    {
        if ( object == null )
        {
            return;
        }

        Class<?> klass = object.getClass();

        if ( ProxyFactory.isProxyClass( klass ) )
        {
            klass = klass.getSuperclass();
        }

        Schema schema = schemaService.getDynamicSchema( klass );

        if ( !schema.haveEndpoint() )
        {
            return;
        }

        try
        {
            Method getUid = object.getClass().getMethod( "getUid" );
            Object value = getUid.invoke( object );

            if ( !String.class.isInstance( value ) )
            {
                log.warn( "getUid on object of type " + object.getClass().getName() + " does not return a String." );
                return;
            }

            Method setHref = object.getClass().getMethod( "setHref", String.class );
            setHref.invoke( object, hrefBase + schema.getApiEndpoint() + "/" + value );
        }
        catch ( NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored )
        {
        }
    }
}
