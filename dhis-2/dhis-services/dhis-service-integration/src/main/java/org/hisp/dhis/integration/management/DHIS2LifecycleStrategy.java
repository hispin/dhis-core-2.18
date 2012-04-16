/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.integration.management;


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
 *
 * @author bobj
 */
public class DHIS2LifecycleStrategy implements LifecycleStrategy
{
    private final Log log = LogFactory.getLog( DHIS2LifecycleStrategy.class );
    
    @Override
    public void onContextStart(CamelContext context) throws VetoCamelContextStartException
    {
        log.info( "Camel context started");
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
