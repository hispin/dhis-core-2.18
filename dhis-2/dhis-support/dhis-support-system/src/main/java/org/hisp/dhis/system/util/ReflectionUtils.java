package org.hisp.dhis.system.util;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.*;
import java.util.Collection;

/**
 * @author Lars Helge Overland
 */
public class ReflectionUtils
{
    private static final Log log = LogFactory.getLog( ReflectionUtils.class );

    /**
     * Invokes method getId() for this object and returns the return value. An
     * int return type is expected. If the operation fails -1 is returned.
     *
     * @param object object to call getId() on.
     * @return The identifier.
     */
    public static int getId( Object object )
    {
        try
        {
            Method method = object.getClass().getMethod( "getId" );

            return (Integer) method.invoke( object );
        } catch ( Exception ex )
        {
            return -1;
        }
    }

    /**
     * Fetch a property off the object. Returns null if the operation fails.
     *
     * @param object   the object.
     * @param property name of the property to get.
     * @return the value of the property or null.
     */
    public static String getProperty( Object object, String property )
    {
        try
        {
            property = property.substring( 0, 1 ).toUpperCase() + property.substring( 1, property.length() );

            Method method = object.getClass().getMethod( "get" + property );

            return (String) method.invoke( object );
        } catch ( Exception ex )
        {
            return null;
        }
    }

    /**
     * Sets a property for the supplied object. Throws an
     * UnsupportedOperationException if the operation fails.
     *
     * @param object Object to modify
     * @param name   Name of property to set
     * @param value  Value the property will be set to
     */
    public static void setProperty( Object object, String name, String value )
    {
        Object[] arguments = new Object[]{value};

        Class<?>[] parameterTypes = new Class<?>[]{String.class};

        if ( name.length() > 0 )
        {
            name = "set" + name.substring( 0, 1 ).toUpperCase() + name.substring( 1, name.length() );

            try
            {
                Method concatMethod = object.getClass().getMethod( name, parameterTypes );

                concatMethod.invoke( object, arguments );
            } catch ( Exception ex )
            {
                throw new UnsupportedOperationException( "Failed to set property", ex );
            }
        }
    }

    /**
     * Sets a property for the supplied object. Throws an
     * UnsupportedOperationException if the operation fails.
     *
     * @param object     Object to modify
     * @param namePrefix prefix of the property name to set
     * @param name       Name of property to set
     * @param value      Value the property will be set to
     */
    public static void setProperty( Object object, String namePrefix, String name, String value )
    {
        String prefixed = namePrefix + name.substring( 0, 1 ).toUpperCase() + name.substring( 1, name.length() );

        setProperty( object, prefixed, value );
    }

    /**
     * Returns the name of the class that the object is an instance of
     * org.hisp.dhis.indicator.Indicactor returns Indicator.
     *
     * @param object object to determine className for.
     * @return String containing the class name.
     */
    public static String getClassName( Object object )
    {
        return object.getClass().getSimpleName();
    }

    /**
     * Test whether the object is an array or a Collection.
     *
     * @param value the object.
     * @return true if the object is an array or a Collection, false otherwise.
     */
    public static boolean isCollection( Object value )
    {
        if ( value != null )
        {
            if ( value.getClass().isArray() || value instanceof Collection<?> )
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isCollection( String fieldName, Object object, Class<?> type )
    {
        Field field;

        try
        {
            field = object.getClass().getDeclaredField( fieldName );
        } catch ( NoSuchFieldException e )
        {
            return false;
        }

        try
        {
            if ( Collection.class.isAssignableFrom( field.getType() ) )
            {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

                if ( actualTypeArguments.length > 0 )
                {
                    if ( type.isAssignableFrom( (Class<?>) actualTypeArguments[0] ) )
                    {
                        return true;
                    }
                }
            }

        } catch ( ClassCastException e )
        {
            return false;
        }

        return false;
    }

    public static Method findGetterMethod( String fieldName, Object object, Class<?>... classes )
    {
        try
        {
            return object.getClass().getMethod( "get" + StringUtils.capitalize( fieldName ), classes );
        } catch ( NoSuchMethodException e )
        {
            log.info( "Getter method was not found for fieldName: " + fieldName );
            return null;
        }
    }

    public static Method findSetterMethod( String fieldName, Object object, Class<?>... classes )
    {
        Method method = null;

        try
        {
            method = object.getClass().getMethod( "set" + StringUtils.capitalize( fieldName ), classes );
        } catch ( NoSuchMethodException e )
        {
        }

        // if parameter classes was not given, we will retry using the type of the field
        if ( method == null && classes.length == 0 )
        {
            try
            {
                Field field = object.getClass().getDeclaredField( fieldName );
                method = findSetterMethod( fieldName, object, field.getType() );
            } catch ( NoSuchFieldException e )
            {
            }
        }

        if ( method == null )
        {
            log.info( "Setter method was not found for fieldName: " + fieldName );
        }

        return method;
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T invokeGetterMethod( String fieldName, Object object )
    {
        Method method = findGetterMethod( fieldName, object );
        log.info( method );

        if ( method == null )
        {
            return null;
        }

        try
        {
            return (T) method.invoke( object );
        } catch ( InvocationTargetException e )
        {
            log.info( "InvocationTargetException for fieldName: " + fieldName );
            return null;
        } catch ( IllegalAccessException e )
        {
            log.info( "IllegalAccessException for fieldName: " + fieldName );
            return null;
        }
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T invokeSetterMethod( String fieldName, Object object, Object... objects )
    {
        Method method = findSetterMethod( fieldName, object );
        log.info( method );

        if ( method == null )
        {
            return null;
        }

        try
        {
            return (T) method.invoke( object, objects );
        } catch ( InvocationTargetException e )
        {
            log.info( "InvocationTargetException for fieldName: " + fieldName );
            return null;
        } catch ( IllegalAccessException e )
        {
            log.info( "IllegalAccessException for fieldName: " + fieldName );
            return null;
        }
    }

    public static boolean isType( Field field, Class<?> clazz )
    {
        Class<?> type = field.getType();

        if ( clazz.isAssignableFrom( type ) )
        {
            return true;
        }

        return false;
    }
}
