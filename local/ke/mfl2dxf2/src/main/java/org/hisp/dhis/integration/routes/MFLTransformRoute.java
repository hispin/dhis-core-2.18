package org.hisp.dhis.integration.routes;

import org.apache.camel.builder.RouteBuilder;

import java.io.InputStream;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class MFLTransformRoute
    extends RouteBuilder
{
    @Override
    public void configure() throws Exception
    {
        // from( "file:/Users/mortenoh/GDrive/HISP/ke/mfl/data/inbox?consumer.initialDelay=10000&consumer.delay=5000" ).
        from( "quartz://every1minute?cron=0+0/1+*+*+*+%3F" ).
            to( "http://api.ehealth.or.ke/api/facilities?lastApproved=1/1/2011&paging=off&scheme=on" ).
            to( "xslt:transform/mfl2dxf2.xsl" ).
            convertBodyTo( InputStream.class ).inOut( "dhis2:metadata" ).
            to( "log:org.hisp.dhis.camel?level=INFO" );
    }
}
