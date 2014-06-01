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

import org.hisp.dhis.node.Node;
import org.hisp.dhis.node.NodeHint;
import org.hisp.dhis.node.NodeSerializer;
import org.hisp.dhis.node.types.CollectionNode;
import org.hisp.dhis.node.types.ComplexNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class StAXNodeSerializer implements NodeSerializer
{
    public static final String CONTENT_TYPE = "application/xml";

    private final XMLOutputFactory xmlFactory = XMLOutputFactory.newInstance();

    @Override
    public String contentType()
    {
        return CONTENT_TYPE;
    }

    @Override
    public void serialize( RootNode rootNode, OutputStream outputStream ) throws IOException
    {
        XMLStreamWriter writer;

        try
        {
            writer = xmlFactory.createXMLStreamWriter( outputStream );
            renderRootNode( rootNode, writer );
            writer.flush();
        }
        catch ( XMLStreamException e )
        {
            throw new IOException( e.getMessage(), e.getCause() );
        }
    }

    private void renderRootNode( RootNode rootNode, XMLStreamWriter writer ) throws IOException, XMLStreamException
    {
        writer.writeStartDocument( "UTF-8", "1.0" );

        writeStartElement( rootNode, writer );

        for ( Node node : rootNode.getNodes() )
        {
            dispatcher( node, writer );
            writer.flush();
        }

        writeEndElement( writer );
        writer.writeEndDocument();
    }

    private void renderSimpleNode( SimpleNode simpleNode, XMLStreamWriter writer ) throws XMLStreamException
    {
        String value = String.format( "%s", simpleNode.getValue() );

        writeStartElement( simpleNode, writer );
        writer.writeCharacters( value );
        writeEndElement( writer );
    }

    private void renderSimpleNodeAttribute( SimpleNode simpleNode, XMLStreamWriter writer ) throws XMLStreamException
    {
        String value = String.format( "%s", simpleNode.getValue() );

        if ( simpleNode.haveHint( NodeHint.Type.XML_NAMESPACE ) )
        {
            writer.writeAttribute( "", String.valueOf( simpleNode.getHint( NodeHint.Type.XML_NAMESPACE ).getValue() ), simpleNode.getName(), value );
        }
        else
        {
            writer.writeAttribute( simpleNode.getName(), value );
        }
    }

    private void renderComplexNode( ComplexNode complexNode, XMLStreamWriter writer ) throws XMLStreamException, IOException
    {
        writeStartElement( complexNode, writer );

        for ( Node node : complexNode.getNodes() )
        {
            dispatcher( node, writer );
        }

        writeEndElement( writer );
    }

    private void renderCollectionNode( CollectionNode collectionNode, XMLStreamWriter writer, boolean useWrapping ) throws XMLStreamException, IOException
    {
        if ( useWrapping )
        {
            writeStartElement( collectionNode, writer );
        }

        for ( Node node : collectionNode.getNodes() )
        {
            dispatcher( node, writer );
        }

        if ( useWrapping )
        {
            writeEndElement( writer );
        }
    }

    private void dispatcher( Node node, XMLStreamWriter writer ) throws IOException, XMLStreamException
    {
        switch ( node.getType() )
        {
            case SIMPLE:
                if ( node.haveHint( NodeHint.Type.XML_ATTRIBUTE ) &&
                    (boolean) node.getHint( NodeHint.Type.XML_ATTRIBUTE ).getValue() )
                {
                    renderSimpleNodeAttribute( (SimpleNode) node, writer );
                }
                else
                {
                    renderSimpleNode( (SimpleNode) node, writer );
                }
                break;
            case COMPLEX:
                renderComplexNode( (ComplexNode) node, writer );
                break;
            case COLLECTION:
                boolean useWrapping = true;

                if ( node.haveHint( NodeHint.Type.XML_COLLECTION_WRAPPING ) )
                {
                    useWrapping = (boolean) node.getHint( NodeHint.Type.XML_COLLECTION_WRAPPING ).getValue();
                }

                renderCollectionNode( (CollectionNode) node, writer, useWrapping );
                break;
        }
    }

    private void writeStartElement( Node node, XMLStreamWriter writer ) throws XMLStreamException
    {
        if ( node.haveHint( NodeHint.Type.XML_NAMESPACE ) )
        {
            writer.writeStartElement( "", node.getName(), String.valueOf( node.getHint( NodeHint.Type.XML_NAMESPACE ).getValue() ) );
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
