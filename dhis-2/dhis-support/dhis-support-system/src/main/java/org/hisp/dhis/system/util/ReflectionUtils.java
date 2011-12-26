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

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author Lars Helge Overland
 */
public class ReflectionUtils
{
    /**
     * Invokes method getId() for this object and returns the return value. An int
     * return type is expected. If the operation fails -1 is returned.
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
        }
        catch ( Exception ex )
        {
            return -1;
        }
    }

    /**
     * Fetch a property off the object. Returns null if the operation fails.
     *
     * @param object the object.
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
        }
        catch ( Exception ex )
        {            
            return null;
        }
    }

    /**
     * Sets a property for the supplied object. Throws an UnsupportedOperationException
     * if the operation fails.
     *
     * @param object Object to modify
     * @param name   Name of property to set
     * @param value  Value the property will be set to
     */
    public static void setProperty( Object object, String name, String value )
    {
        Object[] arguments = new Object[] { value };

        Class<?>[] parameterTypes = new Class<?>[] { String.class };

        if ( name.length() > 0 )
        {
            name = "set" + name.substring( 0, 1 ).toUpperCase() + name.substring( 1, name.length() );

            try
            {
                Method concatMethod = object.getClass().getMethod( name, parameterTypes );
    
                concatMethod.invoke( object, arguments );
            }
            catch ( Exception ex )
            {
                throw new UnsupportedOperationException( "Failed to set property", ex );
            }
        }
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
}
