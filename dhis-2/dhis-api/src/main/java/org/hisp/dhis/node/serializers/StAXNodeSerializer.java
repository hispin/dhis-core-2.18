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

import com.google.common.collect.Lists;
import org.hisp.dhis.node.Node;
import org.hisp.dhis.node.NodeSerializer;
import org.hisp.dhis.node.types.CollectionNode;
import org.hisp.dhis.node.types.ComplexNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class StAXNodeSerializer implements NodeSerializer
{
    public static final String CONTENT_TYPE = "application/xml";

    private final XMLOutputFactory xmlFactory = XMLOutputFactory.newInstance();

    @Override
    public List<String> contentTypes()
    {
        return Lists.newArrayList( CONTENT_TYPE );
    }

    @Override
    public void serialize( RootNode rootNode, OutputStream outputStream ) throws IOException
    {
        XMLStreamWriter writer;

        try
        {
            writer = xmlFactory.createXMLStreamWriter( outputStream );
            writeRootNode( rootNode, writer );
            writer.flush();
        }
        catch ( XMLStreamException e )
        {
            throw new IOException( e.getMessage(), e.getCause() );
        }
    }

    private void writeRootNode( RootNode rootNode, XMLStreamWriter writer ) throws IOException, XMLStreamException
    {
        writer.writeStartDocument( "UTF-8", "1.0" );

        if ( !StringUtils.isEmpty( rootNode.getComment() ) )
        {
            writer.writeComment( rootNode.getComment() );
        }

        writeStartElement( rootNode, writer );

        for ( Node node : rootNode.getChildren() )
        {
            dispatcher( node, writer );
            writer.flush();
        }

        writeEndElement( writer );
        writer.writeEndDocument();
    }

    private void writeSimpleNode( SimpleNode simpleNode, XMLStreamWriter writer ) throws XMLStreamException
    {
        if ( simpleNode.getValue() == null ) // TODO include null or not?
        {
            return;
        }

        String value = String.format( "%s", simpleNode.getValue() );

        if ( simpleNode.isAttribute() )
        {
            if ( !StringUtils.isEmpty( simpleNode.getNamespace() ) )
            {
                writer.writeAttribute( "", simpleNode.getNamespace(), simpleNode.getName(), value );
            }
            else
            {
                writer.writeAttribute( simpleNode.getName(), value );
            }
        }
        else
        {
            writeStartElement( simpleNode, writer );
            writer.writeCharacters( value );
            writeEndElement( writer );
        }
    }

    private void writeComplexNode( ComplexNode complexNode, XMLStreamWriter writer ) throws XMLStreamException, IOException
    {
        writeStartElement( complexNode, writer );

        for ( Node node : complexNode.getChildren() )
        {
            dispatcher( node, writer );
        }

        writeEndElement( writer );
    }

    private void writeCollectionNode( CollectionNode collectionNode, XMLStreamWriter writer ) throws XMLStreamException, IOException
    {
        if ( collectionNode.isWrapping() )
        {
            writeStartElement( collectionNode, writer );
        }

        for ( Node node : collectionNode.getChildren() )
        {
            dispatcher( node, writer );
        }

        if ( collectionNode.isWrapping() )
        {
            writeEndElement( writer );
        }
    }

    private void dispatcher( Node node, XMLStreamWriter writer ) throws IOException, XMLStreamException
    {
        if ( !StringUtils.isEmpty( node.getComment() ) )
        {
            writer.writeComment( node.getComment() );
        }

        switch ( node.getType() )
        {
            case SIMPLE:
                writeSimpleNode( (SimpleNode) node, writer );
                break;
            case COMPLEX:
                writeComplexNode( (ComplexNode) node, writer );
                break;
            case COLLECTION:
                writeCollectionNode( (CollectionNode) node, writer );
                break;
        }
    }

    private void writeStartElement( Node node, XMLStreamWriter writer ) throws XMLStreamException
    {
        if ( !StringUtils.isEmpty( node.getNamespace() ) )
        {
            writer.writeStartElement( "", node.getName(), node.getNamespace() );
        }
        else
        {
            writer.writeStartElement( node.getName() );
        }
    }

    private void writeEndElement( XMLStreamWriter writer ) throws XMLStreamException
    {
        writer.writeEndElement();
    }
}
