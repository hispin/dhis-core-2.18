package org.amplecode.staxwax.framework;


import java.io.InputStream;
import java.net.URI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
 * A simple utility class for evaluating xpath expressions on xml streams
 *
 * @author bobj
 * @version created 16-Feb-2010
 */
public class XPathFilter
{

    private static final Log log = LogFactory.getLog( XPathFilter.class );

    /**
     * Find at most one Node from stream
     *
     * @param in
     * @param xpathExpr
     * @return
     */
    public static synchronized Node findNode( InputStream in, String xpathExpr )
    {

        Node result = null;

        try
        {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            XPathExpression expr = xpath.compile( xpathExpr );
            result = (Node) expr.evaluate( new InputSource( in ), XPathConstants.NODE );

        } catch ( Exception ex )
        {
            log.info( ex );
        }
        return result;
    }

    /**
     * Find set of nodes in stream
     *
     * @param in
     * @param xpathExpr
     * @return
     */
    public static synchronized NodeList findNodes( InputStream in, String xpathExpr )
    {

        NodeList result = null;

        try
        {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            XPathExpression expr = xpath.compile( xpathExpr );
            result = (NodeList) expr.evaluate( new InputSource( in ), XPathConstants.NODESET );

        } catch ( Exception ex )
        {
            log.info( ex );
        }
        return result;
    }

    /**
     * Find text data in stream
     *
     * @param in
     * @param xpathExpr
     * @return
     */
    public static synchronized String findText( InputStream in, String xpathExpr )
    {

        String result = null;

        try
        {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            XPathExpression expr = xpath.compile( xpathExpr );
            result = (String) expr.evaluate( new InputSource( in ), XPathConstants.STRING );

        } catch ( Exception ex )
        {
            log.info( ex );
        }
        return result;
    }
}
