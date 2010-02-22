package org.amplecode.staxwax.transformer;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import org.amplecode.staxwax.framework.InputPort;
import org.amplecode.staxwax.framework.OutputPort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author bobj
 * @version created 14-Dec-2009
 */
public class TransformerTask
{
    private static final Log log = LogFactory.getLog( TransformerTask.class );

    /**
     * The source xml stream
     */
    protected InputPort source;

    /**
     * The xslt stylesheet
     */
    protected InputPort stylesheet;

    /**
     * The transformation result
     */
    protected OutputPort result;

    /**
     * The stylesheet parameters
     */
    protected Map<String, String> params;

    /**
     * The compiled stylesheet
     */
    private Templates templates;

    /**
     * Constructor
     * @param stylesheet the xslt stylesheet
     * @param params the xslt parameters
     */
    public TransformerTask( Source stylesheet, Map<String, String> params )
    {
        this.stylesheet = new InputPort( "Stylesheet", stylesheet );
        this.params = params;
        this.templates = null;
    }

    /**
     * pre-compile styleshhet to Templates
     * @throws TransformerConfigurationException
     */
    public void compile() throws TransformerConfigurationException
    {
        TransformerFactory factory = TransformerFactory.newInstance();
        templates = factory.newTemplates( stylesheet.getSource() );
    }

    public void transform( Source source, Result result, URIResolver resolver ) throws TransformerConfigurationException, TransformerException
    {
        log.info( "Transformer running" );

        if ( templates == null )
        {
            compile();
        }

        this.source = new InputPort( "Source", source );
        this.result = new OutputPort( "Result", result );

        Transformer t = templates.newTransformer();

        t.setErrorListener( new LoggingErrorListener() );
        
        if ( resolver != null )
        {
            t.setURIResolver( resolver );
        }

        if ( params != null )
        {
            Iterator<Entry<String, String>> it = params.entrySet().iterator();
            
            while ( it.hasNext() )
            {
                Entry<String, String> pairs = it.next();
                
                t.setParameter( pairs.getKey(), pairs.getValue() );
            }
        }

        t.transform( this.source.getSource(), this.result.getResult() );
    }
}
