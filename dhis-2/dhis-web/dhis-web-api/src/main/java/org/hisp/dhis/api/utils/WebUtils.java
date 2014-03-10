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
import org.hisp.dhis.api.utils.ops.FilterOps;
import org.hisp.dhis.api.utils.ops.Filters;
import org.hisp.dhis.api.utils.ops.Op;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.hisp.dhis.user.UserCredentials;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.system.util.PredicateUtils.alwaysTrue;

/**
 * TODO too many inner classes, need to be split up
 *
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

    @SuppressWarnings( "unchecked" )
    private static void putInMap( Map<String, Map> map, String path )
    {
        for ( String p : path.split( "\\." ) )
        {
            if ( map.get( p ) == null )
            {
                map.put( p, Maps.newHashMap() );
            }

            map = (Map<String, Map>) map.get( p );
        }
    }

    public static Map<String, Map> parseFieldExpression( String fields )
    {
        List<String> prefixList = Lists.newArrayList();
        Map<String, Map> parsed = Maps.newHashMap();

        StringBuilder builder = new StringBuilder();

        for ( String c : fields.split( "" ) )
        {
            if ( c.equals( "," ) )
            {
                putInMap( parsed, joinedWithPrefix( builder, prefixList ) );
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
                    putInMap( parsed, joinedWithPrefix( builder, prefixList ) );
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
            putInMap( parsed, joinedWithPrefix( builder, prefixList ) );
        }

        return parsed;
    }

    private static String joinedWithPrefix( StringBuilder builder, List<String> prefixList )
    {
        String prefixes = StringUtils.join( prefixList, "." );
        prefixes = prefixes.isEmpty() ? builder.toString() : (prefixes + "." + builder.toString());
        return prefixes;
    }

    public static <T extends IdentifiableObject> List<Object> filterFields( List<T> objects, String include, String exclude )
    {
        List<Object> output = Lists.newArrayList();

        if ( objects.isEmpty() )
        {
            return output;
        }

        Map<String, Map> fieldMap = Maps.newHashMap();

        if ( include == null && exclude == null )
        {
            Map<String, ReflectionUtils.PropertyDescriptor> classMap = ReflectionUtils.getJacksonClassMap( objects.get( 0 ).getClass() );

            for ( String key : classMap.keySet() )
            {
                fieldMap.put( key, Maps.newHashMap() );
            }
        }
        else if ( include == null )
        {
            Map<String, ReflectionUtils.PropertyDescriptor> classMap = ReflectionUtils.getJacksonClassMap( objects.get( 0 ).getClass() );
            Map<String, Map> excludeMap = parseFieldExpression( exclude );

            for ( String key : classMap.keySet() )
            {
                if ( !excludeMap.containsKey( key ) )
                {
                    fieldMap.put( key, Maps.newHashMap() );
                }
            }
        }
        else
        {
            fieldMap = parseFieldExpression( include );
        }

        for ( Object object : objects )
        {
            output.add( buildObjectOutput( object, fieldMap ) );
        }

        return output;
    }

    @SuppressWarnings( "unchecked" )
    private static Map<String, Object> buildObjectOutput( Object object, Map<String, Map> fieldMap )
    {
        if ( object == null )
        {
            return null;
        }

        Map<String, Object> output = Maps.newHashMap();
        Map<String, ReflectionUtils.PropertyDescriptor> classMap = ReflectionUtils.getJacksonClassMap( object.getClass() );

        for ( String key : fieldMap.keySet() )
        {
            if ( !classMap.containsKey( key ) )
            {
                continue;
            }

            Map value = fieldMap.get( key );
            ReflectionUtils.PropertyDescriptor descriptor = classMap.get( key );
            Object returned = ReflectionUtils.invokeMethod( object, descriptor.getMethod() );

            if ( returned == null )
            {
                continue;
            }

            if ( value.isEmpty() )
            {
                if ( !descriptor.isCollection() && !descriptor.isIdentifiableObject() )
                {
                    output.put( key, returned );
                }
                else if ( descriptor.isIdentifiableObject() )
                {
                    if ( descriptor.isCollection() )
                    {
                        List<Map<String, Object>> properties = getIdentifiableObjectCollectionProperties( returned );
                        output.put( key, properties );
                    }
                    else
                    {
                        Map<String, Object> properties = getIdentifiableObjectProperties( returned );
                        output.put( key, properties );
                    }
                }
            }
            else
            {
                if ( descriptor.isCollection() )
                {
                    Collection<IdentifiableObject> objects = (Collection<IdentifiableObject>) returned;
                    ArrayList<Object> arrayList = Lists.newArrayList();
                    output.put( key, arrayList );

                    for ( IdentifiableObject identifiableObject : objects )
                    {
                        Map<String, Object> properties = buildObjectOutput( identifiableObject, value );
                        arrayList.add( properties );
                    }
                }
                else
                {
                    Map<String, Object> properties = buildObjectOutput( returned, value );
                    output.put( key, properties );
                }
            }
        }

        return output;
    }

    private static List<Map<String, Object>> getIdentifiableObjectCollectionProperties( Object object )
    {
        List<String> fields = Lists.newArrayList( "id", "name", "code", "created", "lastUpdated" );
        return getIdentifiableObjectCollectionProperties( object, fields );
    }

    @SuppressWarnings( "unchecked" )
    private static List<Map<String, Object>> getIdentifiableObjectCollectionProperties( Object object, List<String> fields )
    {
        List<Map<String, Object>> output = Lists.newArrayList();
        Collection<IdentifiableObject> identifiableObjects;

        try
        {
            identifiableObjects = (Collection<IdentifiableObject>) object;
        }
        catch ( ClassCastException ex )
        {
            ex.printStackTrace();
            return output;
        }

        for ( IdentifiableObject identifiableObject : identifiableObjects )
        {
            Map<String, Object> properties = getIdentifiableObjectProperties( identifiableObject, fields );
            output.add( properties );
        }

        return output;
    }

    private static Map<String, Object> getIdentifiableObjectProperties( Object object )
    {
        List<String> fields = Lists.newArrayList( "id", "name", "code", "created", "lastUpdated" );
        return getIdentifiableObjectProperties( object, fields );
    }

    private static Map<String, Object> getIdentifiableObjectProperties( Object object, List<String> fields )
    {
        Map<String, Object> idProps = Maps.newLinkedHashMap();
        Map<String, ReflectionUtils.PropertyDescriptor> classMap = ReflectionUtils.getJacksonClassMap( object.getClass() );

        for ( String field : fields )
        {
            ReflectionUtils.PropertyDescriptor descriptor = classMap.get( field );

            if ( descriptor == null )
            {
                continue;
            }

            Object o = ReflectionUtils.invokeMethod( object, descriptor.getMethod() );

            if ( o != null )
            {
                idProps.put( field, o );
            }
        }

        return idProps;
    }

    public static <T extends IdentifiableObject> List<T> filterObjects( List<T> entityList, List<String> filters )
    {
        if ( entityList == null || entityList.isEmpty() )
        {
            return Lists.newArrayList();
        }

        Filters parsed = parseFilters( filters );

        List<T> list = Lists.newArrayList();

        for ( T object : entityList )
        {
            if ( evaluateWithFilters( object, parsed ) )
            {
                list.add( object );
            }
        }

        return list;
    }

    @SuppressWarnings( "unchecked" )
    private static <T extends IdentifiableObject> boolean evaluateWithFilters( T object, Filters filters )
    {
        Map<String, ReflectionUtils.PropertyDescriptor> classMap = ReflectionUtils.getJacksonClassMap( object.getClass() );

        for ( String field : filters.getFilters().keySet() )
        {
            if ( !classMap.containsKey( field ) )
            {
                System.err.println( "Skipping non-existent field: " + field );
                continue;
            }

            ReflectionUtils.PropertyDescriptor descriptor = classMap.get( field );

            Object value = ReflectionUtils.invokeMethod( object, descriptor.getMethod() );

            Object filter = filters.getFilters().get( field );

            if ( FilterOps.class.isInstance( filter ) )
            {
                if ( evaluateFilterOps( value, (FilterOps) filter ) )
                {
                    return false;
                }
            }
            else
            {
                Map<String, Object> map = (Map<String, Object>) filters.getFilters().get( field );
                Filters f = new Filters();
                f.setFilters( map );

                if ( map.containsKey( "__self__" ) )
                {
                    if ( evaluateFilterOps( value, (FilterOps) map.get( "__self__" ) ) )
                    {
                        return false;
                    }

                    map.remove( "__self__" );
                }

                if ( descriptor.isIdentifiableObject() && !descriptor.isCollection() )
                {
                    if ( !evaluateWithFilters( (IdentifiableObject) value, f ) )
                    {
                        return false;
                    }
                }
                else if ( descriptor.isIdentifiableObject() && descriptor.isCollection() )
                {
                    Collection<?> idObjectCollection = (Collection<?>) value;

                    if ( idObjectCollection.isEmpty() )
                    {
                        return false;
                    }

                    for ( Object idObject : idObjectCollection )
                    {
                        if ( !evaluateWithFilters( (IdentifiableObject) idObject, f ) )
                        {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private static boolean evaluateFilterOps( Object value, FilterOps filterOps )
    {
        // filter through every operator treating multiple of same operator as OR
        for ( String operator : filterOps.getFilters().keySet() )
        {
            boolean include = false;

            List<Op> ops = filterOps.getFilters().get( operator );

            for ( Op op : ops )
            {
                switch ( op.evaluate( value ) )
                {
                    case INCLUDE:
                    {
                        include = true;
                    }
                }
            }

            if ( !include )
            {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings( "unchecked" )
    private static Filters parseFilters( List<String> filters )
    {
        Filters parsed = new Filters();

        for ( String filter : filters )
        {
            String[] split = filter.split( ":" );

            if ( !(split.length >= 2) )
            {
                continue;
            }

            if ( split.length >= 3 )
            {
                parsed.addFilter( split[0], split[1], split[2] );
            }
            else
            {
                parsed.addFilter( split[0], split[1], null );
            }
        }

        return parsed;
    }
}
