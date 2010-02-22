package org.amplecode.staxwax.framework;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.stax2.XMLEventReader2;

/**
 * An XML pipe is useful when you want to decouple reader/writer operations, for
 * example using different threads for each.
 * 
 * The output of the pipe looks like an XMLEventReader and can be used as a
 * Source for a transformation.
 * 
 * The input of the pipe looks like an XMLEventWriter and can be used as a
 * Result of a transformation.
 * 
 * @author bobj
 * @version created 08-Dec-2009
 */
public class XMLPipe
{
    private final Log log = LogFactory.getLog( XMLPipe.class );

    protected XMLEventReader2 output;

    protected XMLEventWriter input;

    public XMLEventWriter getInput()
    {
        return input;
    }

    public XMLEventReader2 getOutput()
    {
        return output;
    }

    public boolean inputClosed()
    {
        return ((PipeReader) input).isClosed();
    }

    public boolean outputClosed()
    {
        return ((PipeWriter) input).isClosed();
    }

    public int getEventCount()
    {
        return eventQ.size();
    }

    /**
     * Storage for XMLEvents in pipeline
     */
    protected LinkedBlockingQueue<XMLEvent> eventQ;

    public XMLPipe()
    {
        eventQ = new LinkedBlockingQueue<XMLEvent>();
        output = new PipeReader();
        input = new PipeWriter();
    }

    private class PipeReader
        implements XMLEventReader2
    {
        protected boolean closed;

        public boolean isClosed()
        {
            return closed;
        }

        PipeReader()
        {
            closed = false;
        }

        // ------------------- XMLEventReader methods ------------------
        @Override
        public XMLEvent nextEvent()
            throws XMLStreamException
        {
            XMLEvent result;
            try
            {
                // non-blocking poll()
                // return eventQ.poll();

                // do beware - this will block if q is empty
                result = eventQ.take();
                return result;

            }
            catch ( InterruptedException ex )
            {
                log.warn( "XMLpipe read interrupted : " + ex );
                throw new XMLStreamException( ex.toString() );
            }
        }

        @Override
        public boolean hasNext()
        {
            return (eventQ.size() != 0);
        }

        @Override
        public XMLEvent peek()
            throws XMLStreamException
        {
            return eventQ.peek();
        }

        @Override
        public String getElementText()
            throws XMLStreamException
        {
            try
            {
                // get the text
                String result = nextEvent().asCharacters().getData();
                // pop (and test caste) the end element
                nextEvent().asEndElement();

                return result;

            }
            catch ( Exception ex )
            {
                log.warn( "XMLpipe getElementText problem : " + ex );
                throw new XMLStreamException( ex.toString() );
            }
        }

        @Override
        public XMLEvent nextTag()
            throws XMLStreamException
        {
            XMLEvent ev = null;
            while ( !ev.isEndElement() && !ev.isStartElement() )
            {
                ev = nextEvent();
                if ( ev.getEventType() != XMLEvent.SPACE )
                {
                    throw new XMLStreamException( "XMLPipe nextTag() problem" );
                }
            }
            return ev;
        }

        @Override
        public Object getProperty( String name )
            throws IllegalArgumentException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void close()
            throws XMLStreamException
        {
            closed = true;
            // TODO: think about emptying eventq?
            return;
        }

        @Override
        public Object next()
        {
            if ( !this.hasNext() )
            {
                throw new NoSuchElementException();
            }
            else
            {
                return this.next();
            }
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public boolean hasNextEvent()
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public boolean isPropertySupported( String string )
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public boolean setProperty( String string, Object o )
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    };

    private class PipeWriter
        implements XMLEventWriter
    {

        protected boolean closed;

        public boolean isClosed()
        {
            return closed;
        }

        PipeWriter()
        {
            closed = false;
        }

        // ---------------------------------------------------------------------
        // XMLEventWriter methods
        // ---------------------------------------------------------------------
        
        @Override
        public void flush()
            throws XMLStreamException
        {
            // nothing cached to flush?
            return;
        }

        @Override
        public void add( XMLEvent event )
            throws XMLStreamException
        {
            eventQ.add( event );
        }

        @Override
        public void add( XMLEventReader reader )
            throws XMLStreamException
        {
            while ( reader.hasNext() )
            {
                eventQ.add( reader.nextEvent() );
            }
        }

        @Override
        public String getPrefix( String uri )
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void setPrefix( String prefix, String uri )
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void setDefaultNamespace( String uri )
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void setNamespaceContext( NamespaceContext context )
            throws XMLStreamException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public NamespaceContext getNamespaceContext()
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public void close()
            throws XMLStreamException
        {
            closed = true;
            return;
        }
    };
}
