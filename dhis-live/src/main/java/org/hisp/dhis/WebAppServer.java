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
/**
 *
 * @author Bob Jolliffe
 * @version $$Id$$
 */
package org.hisp.dhis;


import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * @author Bob Jolliffe
 */
public class WebAppServer
{

    public static final String DHIS_DIR = "/webapps/dhis";

    public static final String BIRT_DIR = "/webapps/birt";

    public static final String BIRT_CONTEXT_PATH = "/birt";

    public static final String JETTY_PORT_CONF = "/conf/jetty.port";

    public static final int DEFAULT_JETTY_PORT = 8080;

    private static final Log log = LogFactory.getLog( WebAppServer.class );

    protected Server server;

    protected Connector connector;

    public WebAppServer()
    {
        server = new Server();
        connector = new SelectChannelConnector();
    }

    public void init()
        throws Exception
    {
        String installDir = TrayApp.installDir;
        try
        {
            int portFromConfig = this.getPortFromConfig();
            connector.setPort( portFromConfig );
            log.info( "Loading DHIS 2 on port: " + portFromConfig );
        } catch ( Exception ex )
        {
            log.info( "Couldn't load port number from " + installDir + JETTY_PORT_CONF );
            connector.setPort( DEFAULT_JETTY_PORT );
            log.info( "Loading DHIS 2 on port: " + DEFAULT_JETTY_PORT );
        }

        server.setConnectors( new Connector[]
            {
                connector
            } );

        ContextHandlerCollection handlers = new ContextHandlerCollection();

        WebAppContext dhisWebApp = new WebAppContext();
        dhisWebApp.setWar( installDir + DHIS_DIR );
        handlers.addHandler( dhisWebApp );
        log.info( "Setting DHIS 2 web app context to: " + installDir + DHIS_DIR );

        if ( new File( installDir, BIRT_DIR ).exists() )
        {
            WebAppContext birtWebApp = new WebAppContext();
            birtWebApp.setContextPath( BIRT_CONTEXT_PATH );
            birtWebApp.setWar( installDir + BIRT_DIR );
            handlers.addHandler( birtWebApp );
            log.info( "Setting BIRT web app context to: " + installDir + BIRT_DIR );
        }

        server.setHandler( handlers );
        server.addLifeCycleListener( TrayApp.getInstance() );
    }

    public void start()
        throws Exception
    {
        server.start();
        server.join();
    }

    public void stop()
        throws Exception
    {
        server.stop();
    }

    public int getConnectorPort()
    {
        return connector.getPort();
    }

    private int getPortFromConfig()
    {
        return TrayApp.getInstance().getConfig().getPort();
    }
}
