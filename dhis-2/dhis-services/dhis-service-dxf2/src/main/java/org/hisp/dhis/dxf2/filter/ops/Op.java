package org.hisp.dhis.dxf2.filter.ops;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class Op
{
    private String value;

    private static SimpleDateFormat[] simpleDateFormats = new SimpleDateFormat[]{
        new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" ),
        new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" ),
        new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm" ),
        new SimpleDateFormat( "yyyy-MM-dd'T'HH" ),
        new SimpleDateFormat( "yyyyMMdd" ),
        new SimpleDateFormat( "yyyyMM" ),
        new SimpleDateFormat( "yyyy" )
    };

    public boolean wantValue()
    {
        return true;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    @SuppressWarnings( "unchecked" )
    public <T> T getValue( Class<?> klass )
    {
        if ( klass.isInstance( value ) )
        {
            return (T) value;
        }

        if ( Boolean.class.isAssignableFrom( klass ) )
        {
            try
            {
                return (T) Boolean.valueOf( value );
            }
            catch ( Exception ignored )
            {
            }
        }
        else if ( Integer.class.isAssignableFrom( klass ) )
        {
            try
            {
                return (T) Integer.valueOf( value );
            }
            catch ( Exception ignored )
            {
            }
        }
        else if ( Float.class.isAssignableFrom( klass ) )
        {
            try
            {
                return (T) Float.valueOf( value );
            }
            catch ( Exception ignored )
            {
            }
        }
        else if ( Date.class.isAssignableFrom( klass ) )
        {
            for ( SimpleDateFormat simpleDateFormat : simpleDateFormats )
            {
                try
                {
                    return (T) simpleDateFormat.parse( value );
                }
                catch ( ParseException ignored )
                {
                }
            }
        }

        return null;
    }

    public abstract OpStatus evaluate( Object object );
}
