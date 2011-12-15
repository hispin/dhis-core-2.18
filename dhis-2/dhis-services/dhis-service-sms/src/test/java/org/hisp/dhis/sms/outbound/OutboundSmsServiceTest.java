package org.hisp.dhis.sms.outbound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.hisp.dhis.sms.AbstractSmsTest;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.hisp.dhis.sms.outbound.OutboundSmsStatus;
import org.hisp.dhis.sms.outbound.OutboundSmsStore;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

public class OutboundSmsServiceTest
    extends AbstractSmsTest
{

    // These are only used for the integration test with store

    @Autowired
    private OutboundSmsService outboundSmsService;

    @Autowired
    private OutboundSmsStore outboundSmsStore;

    @Test
    public void testIntegrationEnabledNoTransport()
    {
        outboundSmsService.initialize( new SmsConfiguration( true ) );

        OutboundSms outboundSms = getOutboundSms();

        outboundSmsService.sendMessage( outboundSms );

        List<OutboundSms> smses = outboundSmsStore.getAll();
        assertNotNullSize( smses, 1 );

        verifySms( outboundSms, smses.iterator().next() );
    }

    // Unit testing

    @Test
    public void testNotEnabled()
    {
        OutboundSmsService tmpService = new OutboundSmsServiceImpl();
        try
        {
            tmpService.sendMessage( getOutboundSms() );
            fail("Should fail since service is not enabled");
        }
        catch ( SmsServiceException e )
        {
        }

    }

    @Test
    public void testWithTransport()
    {
        OutboundSmsServiceImpl tmpService = new OutboundSmsServiceImpl();
        tmpService.setOutboundSmsStore( mock( OutboundSmsStore.class ) );
        OutboundSmsTransportService transportService = mock( OutboundSmsTransportService.class );
        tmpService.setTransportService( transportService );

        OutboundSms outboundSms = getOutboundSms();
   
        // Service not enabled
        try
        {
            tmpService.sendMessage( outboundSms );
            fail("Should fail since service is not enabled");
        }
        catch ( SmsServiceException e )
        {
        }

        // Not sent message to transport service
        verify( transportService, never() ).sendMessage( any( OutboundSms.class ) );

        // Enable service
        tmpService.initialize( new SmsConfiguration( true ) );

        tmpService.sendMessage( outboundSms );
        verify( transportService ).sendMessage( outboundSms );
    }

    @Test
    public void testFailingTransport()
    {
        OutboundSmsServiceImpl tmpService = new OutboundSmsServiceImpl();
        OutboundSmsStore tmpStore = mock( OutboundSmsStore.class );
        tmpService.setOutboundSmsStore( tmpStore );
        OutboundSmsTransportService transportService = mock( OutboundSmsTransportService.class );
        tmpService.setTransportService( transportService );

        tmpService.initialize( new SmsConfiguration( true ) );

        OutboundSms outboundSms = getOutboundSms();

        doThrow( new SmsServiceException( "" ) ).when( transportService ).sendMessage( outboundSms );

        tmpService.sendMessage( outboundSms );

        verify( transportService ).sendMessage( outboundSms );
        ArgumentCaptor<OutboundSms> argument = ArgumentCaptor.forClass( OutboundSms.class );
        verify( tmpStore, times( 1 ) ).save( argument.capture() );

        // Is the SMS Marked with error status in store?
        // Can't test this without using hibernate or adding update on store...
        //assertEquals( OutboundSmsStatus.ERROR, argument.getValue().getStatus() );
    }

}
