package org.hisp.dhis.node.serializers;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hisp.dhis.node.Node;
import org.hisp.dhis.node.NodeSerializer;
import org.hisp.dhis.node.types.CollectionNode;
import org.hisp.dhis.node.types.ComplexNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class JacksonJsonNodeSerializer implements NodeSerializer
{
    public static final String CONTENT_TYPE = "application/json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String contentType()
    {
        return CONTENT_TYPE;
    }

    public JacksonJsonNodeSerializer()
    {
        objectMapper.setSerializationInclusion( JsonInclude.Include.NON_NULL );
        objectMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
        objectMapper.configure( SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false );
        objectMapper.configure( SerializationFeature.WRAP_EXCEPTIONS, true );
        objectMapper.getFactory().enable( JsonGenerator.Feature.QUOTE_FIELD_NAMES );
    }

    @Override
    public void serialize( RootNode rootNode, OutputStream outputStream ) throws IOException
    {
        JsonGenerator generator = objectMapper.getFactory().createGenerator( outputStream );

        renderRootNode( rootNode, generator );
        generator.flush();
    }

    private void renderRootNode( RootNode rootNode, JsonGenerator generator ) throws IOException
    {
        generator.writeStartObject();

        for ( Node node : rootNode.getChildren() )
        {
            dispatcher( node, generator, true );
            generator.flush();
        }

        generator.writeEndObject();
    }

    private void renderSimpleNode( SimpleNode simpleNode, JsonGenerator generator, boolean writeKey ) throws IOException
    {
        if ( simpleNode.getValue() == null ) // add hint for this, exclude if null
        {
            return;
        }

        if ( writeKey )
        {
            generator.writeObjectField( simpleNode.getName(), simpleNode.getValue() );
        }
        else
        {
            generator.writeObject( simpleNode.getValue() );
        }
    }

    private void renderComplexNode( ComplexNode complexNode, JsonGenerator generator, boolean writeKey ) throws IOException
    {
        if ( writeKey )
        {
            generator.writeObjectFieldStart( complexNode.getName() );
        }
        else
        {
            generator.writeStartObject();
        }

        for ( Node node : complexNode.getChildren() )
        {
            dispatcher( node, generator, true );
        }

        generator.writeEndObject();
    }

    private void renderCollectionNode( CollectionNode collectionNode, JsonGenerator generator, boolean writeKey ) throws IOException
    {
        if ( writeKey )
        {
            generator.writeArrayFieldStart( collectionNode.getName() );
        }
        else
        {
            generator.writeStartArray();
        }

        for ( Node node : collectionNode.getChildren() )
        {
            dispatcher( node, generator, false );
        }

        generator.writeEndArray();
    }

    private void dispatcher( Node node, JsonGenerator generator, boolean writeKey ) throws IOException
    {
        switch ( node.getType() )
        {
            case SIMPLE:
                renderSimpleNode( (SimpleNode) node, generator, writeKey );
                break;
            case COMPLEX:
                renderComplexNode( (ComplexNode) node, generator, writeKey );
                break;
            case COLLECTION:
                renderCollectionNode( (CollectionNode) node, generator, writeKey );
                break;
        }
    }
}
