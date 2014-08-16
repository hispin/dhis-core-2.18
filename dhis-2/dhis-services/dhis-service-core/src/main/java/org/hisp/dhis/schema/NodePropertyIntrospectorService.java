package org.hisp.dhis.schema;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.node.annotation.NodeAnnotation;
import org.hisp.dhis.node.annotation.NodeCollection;
import org.hisp.dhis.node.annotation.NodeComplex;
import org.hisp.dhis.node.annotation.NodeSimple;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class NodePropertyIntrospectorService extends AbstractPropertyIntrospectorService
{
    @Override
    protected Map<String, Property> scanClass( Class<?> klass )
    {
        Map<String, Property> propertyMap = Maps.newHashMap();

        for ( Field field : ReflectionUtils.getAllFields( klass ) )
        {
            Property property = null;

            for ( Annotation annotation : field.getAnnotations() )
            {
                // search for and add all annotations that meta-annotated with NodeAnnotation
                if ( annotation.annotationType().isAnnotationPresent( NodeAnnotation.class ) )
                {
                    Method getter = getGetter( klass, field );
                    Method setter = getSetter( klass, field );

                    property = new Property( Primitives.wrap( field.getType() ), getter, setter );
                    property.setName( field.getName() );
                    property.setFieldName( field.getName() );

                    if ( Collection.class.isAssignableFrom( field.getType() ) )
                    {
                        property.setCollection( true );
                        property.setCollectionName( field.getName() );

                        Type type = field.getGenericType();

                        if ( ParameterizedType.class.isInstance( type ) )
                        {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            Class<?> itemKlass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            property.setItemKlass( itemKlass );

                            if ( IdentifiableObject.class.isAssignableFrom( itemKlass ) )
                            {
                                property.setIdentifiableObject( true );

                                if ( NameableObject.class.isAssignableFrom( itemKlass ) )
                                {
                                    property.setNameableObject( true );
                                }
                            }
                        }
                    }

                    break;
                }
            }

            if ( property == null )
            {
                continue;
            }

            if ( field.isAnnotationPresent( NodeSimple.class ) )
            {
                NodeSimple nodeSimple = field.getAnnotation( NodeSimple.class );
                property.setSimple( true );
                property.setAttribute( nodeSimple.isAttribute() );
                property.setPersisted( nodeSimple.isPersisted() );
                property.setNamespace( nodeSimple.namespace() );
                property.setWritable( nodeSimple.isWritable() );
                property.setReadable( nodeSimple.isReadable() );

                if ( !StringUtils.isEmpty( nodeSimple.value() ) )
                {
                    property.setName( nodeSimple.value() );
                }
            }
            else if ( field.isAnnotationPresent( NodeComplex.class ) )
            {
                NodeComplex nodeComplex = field.getAnnotation( NodeComplex.class );
                property.setSimple( false );
                property.setNamespace( nodeComplex.namespace() );
                property.setWritable( nodeComplex.isWritable() );
                property.setReadable( nodeComplex.isReadable() );

                if ( !StringUtils.isEmpty( nodeComplex.value() ) )
                {
                    property.setName( nodeComplex.value() );
                }
            }
            else if ( field.isAnnotationPresent( NodeCollection.class ) )
            {
                NodeCollection nodeCollection = field.getAnnotation( NodeCollection.class );
                property.setCollectionWrapping( nodeCollection.useWrapping() );
                property.setWritable( nodeCollection.isWritable() );
                property.setReadable( nodeCollection.isReadable() );

                if ( !StringUtils.isEmpty( nodeCollection.value() ) )
                {
                    property.setCollectionName( nodeCollection.value() );
                }

                if ( !StringUtils.isEmpty( nodeCollection.itemName() ) )
                {
                    property.setName( nodeCollection.itemName() );
                }
            }

            propertyMap.put( property.getName(), property );
        }

        return propertyMap;
    }

    private Method getGetter( Class<?> klass, Field field )
    {
        return getMethodWithPrefix( klass, field, Lists.newArrayList( "get", "is", "has" ), false );
    }

    private Method getSetter( Class<?> klass, Field field )
    {
        return getMethodWithPrefix( klass, field, Lists.newArrayList( "set" ), true );
    }

    private Method getMethodWithPrefix( Class<?> klass, Field field, List<String> prefixes, boolean includeType )
    {
        String name = StringUtils.capitalize( field.getName() );

        for ( String prefix : prefixes )
        {
            try
            {
                Method method = includeType ? klass.getMethod( prefix + name, field.getType() ) : klass.getMethod( prefix + name );

                if ( method != null )
                {
                    return method;
                }
            }
            catch ( NoSuchMethodException ignored )
            {
            }
        }

        return null;
    }
}
