package org.hisp.dhis.mobile.service;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.api.sms.OutboundSmsService;
import org.hisp.dhis.api.sms.SmsServiceException;

public class TestOutboundSmsService
    implements OutboundSmsService
{

    private static final Log log = LogFactory.getLog( TestOutboundSmsService.class );

    @Override
    public boolean isSmsServiceAvailable()
    {
        log.info( "Is service is available?" );
        return true;
    }

    @Override
    public void sendMessage( String message, String... recipients )
        throws SmsServiceException
    {
        log.info( "Send message '" + message + "' to " + recipients);
    }

    @Override
    public void sendOtaMessage( URL url, String prompt, String... recipients )
        throws SmsServiceException
    {
        String numbers = "";
        
        for ( String recipient : recipients )
        {
            numbers += recipient + ", ";
        }
        log.info( "Send OTA message '" + prompt + "', url " + url + " to " + numbers.substring( 0, numbers.length() - 2 ) );
    }

}
