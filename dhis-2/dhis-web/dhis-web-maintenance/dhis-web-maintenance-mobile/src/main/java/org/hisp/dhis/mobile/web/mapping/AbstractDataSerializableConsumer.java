package org.hisp.dhis.mobile.web.mapping;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import org.hisp.dhis.mobile.api.model.DataStreamSerializable;


/**
 * An abstract class mapping from the {@link DhisMediaType.MOBILE_SERIALIZED} to
 * implementations of {@link DataStreamSerializable}
 * 
 * <p>
 * Implementations only need to define T and specify the relevant annotations
 * 
 * @param <T> The concrete implementation of {@link DataStreamSerializable} this
 *        consumer creates and populates
 */
public abstract class AbstractDataSerializableConsumer<T extends DataStreamSerializable>
    implements MessageBodyReader<T>
{
    @Override
    public boolean isReadable( Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType )
    {
        return DataStreamSerializable.class.isAssignableFrom( type );
    }

    @Override
    public T readFrom( Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
        MultivaluedMap<String, String> httpHeaders, InputStream entityStream )
        throws IOException, WebApplicationException
    {
        T t;
        try
        {
            t = type.newInstance();
            t.deSerialize( new DataInputStream( entityStream ) );
            return t;
        }
        catch ( InstantiationException e )
        {
            throw new IOException( "Can't instantiate class " + type.getName(), e );
        }
        catch ( IllegalAccessException e )
        {
            throw new IOException( "Not allowed to instantiate class " + type.getName(), e );
        }
    }
}
