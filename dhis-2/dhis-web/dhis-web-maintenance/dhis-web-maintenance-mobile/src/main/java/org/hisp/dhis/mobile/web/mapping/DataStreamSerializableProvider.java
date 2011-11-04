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


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.hisp.dhis.api.mobile.model.DataStreamSerializable;
import org.hisp.dhis.mobile.web.resources.MobileMediaTypes;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import com.sun.jersey.spi.resource.Singleton;

@Provider
@Singleton
@Produces( MobileMediaTypes.MOBILE_SERIALIZED )
public class DataStreamSerializableProvider
    implements MessageBodyWriter<DataStreamSerializable>
{

    @Override
    public boolean isWriteable( Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType )
    {
        return DataStreamSerializable.class.isAssignableFrom( type );
    }

    @Override
    public long getSize( DataStreamSerializable t, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType )
    {
        return -1;
    }

    @Override
    public void writeTo( DataStreamSerializable t, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream )
        throws IOException, WebApplicationException
    {
        ByteArrayOutputStream baos = serializePersistent( t );
        ZOutputStream gzip = new ZOutputStream( entityStream, JZlib.Z_BEST_COMPRESSION );
        DataOutputStream dos = new DataOutputStream( gzip );

        try
        {
            byte[] res = baos.toByteArray();
            dos.write( res );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            dos.flush();
            gzip.finish();
        }

    }

    public ByteArrayOutputStream serializePersistent( DataStreamSerializable entity )
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( baos );
        entity.serialize( out );
        out.flush();
        return baos;
    }

}
