package org.hisp.dhis.dxf2.render;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Default implementation that uses Jackson to serialize/deserialize
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultRenderService implements RenderService
{
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public DefaultRenderService()
    {
        configureObjectMapper();
    }

    private void configureObjectMapper()
    {
        jsonMapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );
        jsonMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
        jsonMapper.configure( SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false );
        jsonMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
        jsonMapper.configure( SerializationFeature.WRAP_EXCEPTIONS, true );

        jsonMapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        jsonMapper.configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true );
        jsonMapper.configure( DeserializationFeature.WRAP_EXCEPTIONS, true );

        jsonMapper.disable( MapperFeature.AUTO_DETECT_FIELDS );
        jsonMapper.disable( MapperFeature.AUTO_DETECT_CREATORS );
        jsonMapper.disable( MapperFeature.AUTO_DETECT_GETTERS );
        jsonMapper.disable( MapperFeature.AUTO_DETECT_SETTERS );
        jsonMapper.disable( MapperFeature.AUTO_DETECT_IS_GETTERS );

        jsonMapper.getJsonFactory().enable( JsonGenerator.Feature.QUOTE_FIELD_NAMES );
    }

    @Override
    public void toJson( OutputStream output, Object value ) throws IOException
    {
        jsonMapper.writeValue( output, value );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> T fromJson( InputStream input, Class<?> clazz ) throws IOException
    {
        return (T) jsonMapper.readValue( input, clazz );
    }
}
