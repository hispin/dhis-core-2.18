package org.hisp.dhis.api.utils;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.api.controller.WebMetaData;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.user.UserCredentials;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.system.util.PredicateUtils.alwaysTrue;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class WebUtils
{
    private static final Log log = LogFactory.getLog( WebUtils.class );

    public static void generateLinks( WebMetaData metaData )
    {
        generateLinks( metaData, true );
    }

    public static void generateLinks( WebMetaData metaData, boolean deep )
    {
        Class<?> baseType = null;
        Collection<Field> fields = ReflectionUtils.collectFields( metaData.getClass(), alwaysTrue );

        for ( Field field : fields )
        {
            if ( ReflectionUtils.isCollection( field.getName(), metaData, IdentifiableObject.class ) ||
                ReflectionUtils.isCollection( field.getName(), metaData, DimensionalObject.class ) )
            {
                List<Object> objects = new ArrayList<Object>( (Collection<?>) ReflectionUtils.getFieldObject( field, metaData ) );

                if ( !objects.isEmpty() )
                {
                    if ( baseType != null )
                    {
                        log.warn( "baseType already set, overwriting" );
                    }

                    baseType = objects.get( 0 ).getClass();

                    for ( Object object : objects )
                    {
                        generateLinks( object, deep );
                    }
                }
            }
        }

        if ( baseType == null )
        {
            log.warn( "baseType was not found, returning." );
            return;
        }

        if ( metaData.getPager() != null )
        {
            String basePath = ContextUtils.getPath( baseType );
            Pager pager = metaData.getPager();

            if ( pager.getPage() < pager.getPageCount() )
            {
                String nextPath = basePath + "?page=" + (pager.getPage() + 1);
                nextPath += pager.pageSizeIsDefault() ? "" : "&pageSize=" + pager.getPageSize();

                pager.setNextPage( nextPath );
            }

            if ( pager.getPage() > 1 )
            {
                if ( (pager.getPage() - 1) == 1 )
                {
                    String prevPath = pager.pageSizeIsDefault() ? basePath : basePath + "?pageSize=" + pager.getPageSize();
                    pager.setPrevPage( prevPath );
                }
                else
                {
                    String prevPath = basePath + "?page=" + (pager.getPage() - 1);
                    prevPath += pager.pageSizeIsDefault() ? "" : "&pageSize=" + pager.getPageSize();

                    pager.setPrevPage( prevPath );
                }
            }
        }
    }

    public static void generateLinks( Object object )
    {
        generateLinks( object, true );
    }

    @SuppressWarnings( "unchecked" )
    public static void generateLinks( Object object, boolean deep )
    {
        if ( object == null )
        {
            return;
        }

        if ( IdentifiableObject.class.isAssignableFrom( object.getClass() ) )
        {
            IdentifiableObject identifiableObject = (IdentifiableObject) object;
            identifiableObject.setHref( ContextUtils.getPathWithUid( identifiableObject ) );
        }

        List<Field> fields = new ArrayList<Field>();
        fields.addAll( ReflectionUtils.collectFields( object.getClass() ) );

        if ( !deep )
        {
            return;
        }

        for ( Field field : fields )
        {
            if ( IdentifiableObject.class.isAssignableFrom( field.getType() ) )
            {
                Object fieldObject = ReflectionUtils.getFieldObject( field, object );

                if ( fieldObject != null && !UserCredentials.class.isAssignableFrom( fieldObject.getClass() ) )
                {
                    IdentifiableObject idObject = (IdentifiableObject) fieldObject;
                    idObject.setHref( ContextUtils.getPathWithUid( idObject ) );
                }
            }
            else if ( ReflectionUtils.isCollection( field.getName(), object, IdentifiableObject.class ) )
            {
                Object collection = ReflectionUtils.getFieldObject( field, object );

                if ( collection != null )
                {
                    Collection<IdentifiableObject> collectionObjects = (Collection<IdentifiableObject>) collection;

                    for ( IdentifiableObject collectionObject : collectionObjects )
                    {
                        if ( collectionObject != null )
                        {
                            collectionObject.setHref( ContextUtils.getPathWithUid( collectionObject ) );
                        }
                    }
                }
            }
        }
    }

    public static <T extends IdentifiableObject> List<Object> filterFields( List<T> entityList, String fields )
    {
        List<Object> objects = Lists.newArrayList();

        if ( entityList.isEmpty() || fields == null )
        {
            return objects;
        }

        Map<String, Method> classMap = ReflectionUtils.getJacksonClassMap( entityList.get( 0 ).getClass() );
        List<String> parsedFields = parseFieldExpression( fields );

        for ( T object : entityList )
        {
            Map<String, Object> objMap = Maps.newLinkedHashMap();

            for ( String field : parsedFields )
            {
                if ( classMap.containsKey( field ) )
                {
                    Object o = ReflectionUtils.invokeMethod( object, classMap.get( field ) );

                    if ( o == null )
                    {
                        continue;
                    }

                    if ( !ReflectionUtils.isCollection( o ) )
                    {
                        if ( IdentifiableObject.class.isInstance( o ) )
                        {
                            objMap.put( field, getIdentifiableObjectProperties( (IdentifiableObject) o ) );
                        }
                        else
                        {
                            objMap.put( field, o );
                        }
                    }
                    else
                    {
                        objMap.put( field, getIdentifiableObjectCollectionProperties( o ) );
                    }
                }
            }

            objects.add( objMap );
        }

        return objects;
    }

    @SuppressWarnings( "unchecked" )
    private static Object getIdentifiableObjectCollectionProperties( Object o )
    {
        List<Map<String, Object>> idPropertiesList = Lists.newArrayList();
        Collection<IdentifiableObject> identifiableObjects;

        try
        {
            identifiableObjects = (Collection<IdentifiableObject>) o;
        }
        catch ( ClassCastException ex )
        {
            return o;
        }

        for ( IdentifiableObject identifiableObject : identifiableObjects )
        {
            Map<String, Object> idProps = getIdentifiableObjectProperties( identifiableObject );
            idPropertiesList.add( idProps );
        }

        return idPropertiesList;
    }

    private static Map<String, Object> getIdentifiableObjectProperties( IdentifiableObject identifiableObject )
    {
        Map<String, Object> idProps = Maps.newLinkedHashMap();

        idProps.put( "id", identifiableObject.getUid() );
        idProps.put( "name", identifiableObject.getDisplayName() );

        if ( identifiableObject.getCode() != null )
        {
            idProps.put( "code", identifiableObject.getCode() );
        }

        idProps.put( "created", identifiableObject.getCreated() );
        idProps.put( "lastUpdated", identifiableObject.getLastUpdated() );

        return idProps;
    }

    private static List<String> parseFieldExpression( String fields )
    {
        List<String> splitFields = Lists.newArrayList();

        StringBuilder builder = new StringBuilder();
        ArrayList<String> prefixList = Lists.newArrayList();

        for ( String c : fields.split( "" ) )
        {
            if ( c.equals( "," ) )
            {
                splitFields.add( joinedWithPrefix( builder, prefixList ) );
                builder = new StringBuilder();
                continue;
            }

            if ( c.equals( "[" ) )
            {
                prefixList.add( builder.toString() );
                builder = new StringBuilder();
                continue;
            }

            if ( c.equals( "]" ) )
            {
                if ( !builder.toString().isEmpty() )
                {
                    splitFields.add( joinedWithPrefix( builder, prefixList ) );
                }

                prefixList.remove( prefixList.size() - 1 );
                builder = new StringBuilder();
                continue;
            }

            if ( StringUtils.isAlpha( c ) )
            {
                builder.append( c );
            }
        }

        if ( !builder.toString().isEmpty() )
        {
            splitFields.add( joinedWithPrefix( builder, prefixList ) );
        }

        return splitFields;
    }

    private static String joinedWithPrefix( StringBuilder builder, List<String> prefixList )
    {
        String output = StringUtils.join( prefixList, "." );
        output = output.isEmpty() ? builder.toString() : (output + "." + builder.toString());
        return output;
    }
}
