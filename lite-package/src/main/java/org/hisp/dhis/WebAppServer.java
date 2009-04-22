/*
 * Copyright (c) 2004-2009, University of Oslo
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.component.LifeCycle;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 *
 * @author bobj
 */
public class WebAppServer 
{
  public static final String DHIS_DIR = "/webapps/dhis";
  public static final String BIRT_DIR = "/webapps/birt";
  public static final String BIRT_CONTEXT_PATH = "/birt";
  
  private static final Log log = LogFactory.getLog( WebAppServer.class );
    
  protected Server server;
  protected Connector connector;

  public WebAppServer() {
    server = new Server();
    connector = new SelectChannelConnector();
  }

  public void init(String installDir, LifeCycle.Listener serverListener)
    throws Exception
  {
    connector.setPort(Integer.getInteger("jetty.port",8080).intValue());
    server.setConnectors(new Connector[]{connector});

    WebAppContext dhisWebApp = new WebAppContext();
    dhisWebApp.setWar(installDir + DHIS_DIR);
    log.info("Setting DHIS 2 web app context to: "+ installDir + DHIS_DIR);
    
    WebAppContext birtWebApp = new WebAppContext();
    birtWebApp.setContextPath(BIRT_CONTEXT_PATH);
    birtWebApp.setWar(installDir + BIRT_DIR);
    log.info("Setting BIRT web app context to: "+ installDir + BIRT_DIR);
    
    ContextHandlerCollection handlers = new ContextHandlerCollection();
    handlers.addHandler(dhisWebApp);
    handlers.addHandler(birtWebApp);    
    
    server.setHandler(handlers);
    server.addLifeCycleListener(serverListener);
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
}
