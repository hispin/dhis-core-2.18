package org.hisp.dhis.integration.management;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.Service;
import org.apache.camel.VetoCamelContextStartException;
import org.apache.camel.spi.LifecycleStrategy;
import org.apache.camel.spi.RouteContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author bobj
 */
public class DHIS2LifecycleStrategy 
    implements LifecycleStrategy
{
    private final Log log = LogFactory.getLog( DHIS2LifecycleStrategy.class );
    
    @Override
    public void onContextStart(CamelContext context) throws VetoCamelContextStartException
    {
        log.info( "Camel context started" );
        // todo: pickup routes from dhis2_home
    }

    @Override
    public void onContextStop( CamelContext cc )
    {
        log.info( "Camel context stopped");
    }

    @Override
    public void onComponentAdd( String name, Component cmpnt )
    {
        log.info( "Camel component added: " + name);
    }

    @Override
    public void onComponentRemove( String name, Component cmpnt )
    {
        log.info( "Camel component removed: " + name);
    }

    @Override
    public void onEndpointAdd( Endpoint endpnt )
    {
        log.info( "Camel endpoint added: " + endpnt.getEndpointUri());
    }

    @Override
    public void onEndpointRemove( Endpoint endpnt )
    {
        log.info( "Camel endpoint removed: " + endpnt.getEndpointUri());
    }

    @Override
    public void onServiceAdd( CamelContext cc, Service srvc, Route route )
    {
    }

    @Override
    public void onServiceRemove( CamelContext cc, Service srvc, Route route )
    {
    }

    @Override
    public void onRoutesAdd( Collection<Route> clctn )
    {
        log.debug( "Camel routes added");
    }

    @Override
    public void onRoutesRemove( Collection<Route> clctn )
    {
        log.debug( "Camel routes removed");
    }

    @Override
    public void onRouteContextCreate( RouteContext rc )
    {
        log.debug( "Camel route context created");
    }

    @Override
    public void onErrorHandlerAdd( RouteContext rc, Processor prcsr, ErrorHandlerFactory ehf )
    {
        log.info( "Camel error handler added: " + ehf.toString());
    }

    @Override
    public void onThreadPoolAdd( CamelContext cc, ThreadPoolExecutor tpe, String string, String string1, String string2, String string3 )
    {
        log.debug( "Camel threadpool added");
    }
}
