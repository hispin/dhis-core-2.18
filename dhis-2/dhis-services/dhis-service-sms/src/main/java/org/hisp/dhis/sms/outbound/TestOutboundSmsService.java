package org.hisp.dhis.sms.outbound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.SmsServiceNotEnabledException;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;

/**
 * Simple {@link OutboundSmsService} just logging invocations, only to be used for test purposes
 * 
 * <p>Has the property enabled, defaulting to true, which is configured using {@link TestOutboundSmsService#initialize(SmsConfiguration)}
 */
public class TestOutboundSmsService
    implements OutboundSmsTransportService
{
    private static final Log log = LogFactory.getLog( TestOutboundSmsService.class );

    private boolean enabled = true;

    @Override
    public void sendMessage( OutboundSms sms, String gatewayId )
        throws SmsServiceException
    {
        if (!enabled)
            throw new SmsServiceNotEnabledException();
            
        log.debug( "Send message: " + sms );
    }

    @Override
    public void initialize(SmsConfiguration config)
        throws SmsServiceException
    {
        this.enabled = config.isEnabled();
        log.debug( "initialize()" );
    }

    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }

}
