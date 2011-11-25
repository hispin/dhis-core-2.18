package org.hisp.dhis.common.adapter;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.hisp.dhis.common.NameableObject;

import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JsonNameableObjectSerializer extends JsonSerializer<NameableObject>
{
    /**
     * Jackson doesn't seem to see the downcasted object, so we need to manually write the values.
     * TODO fix this.
     */
    @Override
    public void serialize( NameableObject nameableObject, JsonGenerator jgen, SerializerProvider provider ) throws IOException, JsonProcessingException
    {
        if ( nameableObject != null )
        {
            jgen.writeStartObject();

            jgen.writeNumberField( "id", nameableObject.getId() );
            jgen.writeStringField( "uid", nameableObject.getUid() );
            jgen.writeStringField( "name", nameableObject.getName() );
            jgen.writeStringField( "code", nameableObject.getCode() );

            jgen.writeFieldName( "lastUpdated" );

            JsonDateSerializer jsonDateSerializer = new JsonDateSerializer();
            jsonDateSerializer.serialize( nameableObject.getLastUpdated(), jgen, provider );

            jgen.writeStringField( "shortName", nameableObject.getShortName() );
            jgen.writeStringField( "alternativeName", nameableObject.getAlternativeName() );
            jgen.writeStringField( "description", nameableObject.getDescription() );

            jgen.writeEndObject();
        }
        else
        {
            jgen.writeNull();
        }
    }
}
