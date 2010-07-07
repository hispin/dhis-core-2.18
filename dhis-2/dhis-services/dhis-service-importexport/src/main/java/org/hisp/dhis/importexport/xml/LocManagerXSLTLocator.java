package org.hisp.dhis.importexport.xml;


import java.io.InputStream;
import org.amplecode.staxwax.framework.XPathFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;
import org.hisp.dhis.importexport.ImportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

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
/**
 * An XSLT locator based on the dhis LocationManager
 *
 * It depends on a simple XML transformers configration file
 * which maps tags to stylesheets.
 *
 * @author bobj
 * @version created 30-Apr-2010
 */
@Component("location-manager-xslt-locator")
public class LocManagerXSLTLocator implements XSLTLocator
{

    private static final Log log = LogFactory.getLog( LocManagerXSLTLocator.class );

    private static final String TRANSFORMER_MAP = "transform/transforms.xml";

    @Autowired
    private LocationManager locationManager;

    @Override
    public InputStream getTransformerByTag( String identifier ) throws ImportException
    {
        InputStream result = null;
        try
        {
            String xpath = "/transforms/transform[@tag='" + identifier + "']/xslt";
            log.debug( "xpath search: " + xpath );
            Node transformerNode = XPathFilter.findNode( locationManager.getInputStream( TRANSFORMER_MAP ),
                xpath );
            if ( transformerNode != null )
            {
                log.debug( "Node found: " + transformerNode.getTextContent() );
                log.debug( "Loading: " + transformerNode.getTextContent() );
                result = locationManager.getInputStream( "transform/" + transformerNode.getTextContent() );
            } else
            {
                throw new ImportException( "No transformer configured for this format" );
            }

        } catch ( LocationManagerException ex )
        {
            throw new ImportException( "Missing transformer for this format", ex );
        }
        return result;
    }
}
