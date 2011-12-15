package org.hisp.dhis.sms.outbound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.SmsServiceNotEnabledException;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.hisp.dhis.sms.outbound.OutboundSmsStatus;
import org.hisp.dhis.sms.outbound.OutboundSmsStore;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simple {@link OutboundSmsService sms service} storing the sms in a store and
 * forwards the request to a {@link OutboundSmsTransportService sms transport
 * service} for sending.
 */
public class OutboundSmsServiceImpl
    implements OutboundSmsService
{

    private static final Log log = LogFactory.getLog( OutboundSmsServiceImpl.class );

    private OutboundSmsStore outboundSmsStore;

    private OutboundSmsTransportService transportService;

    private boolean enabled;

    @Autowired
    public void setOutboundSmsStore( OutboundSmsStore outboundSmsStore )
    {
        this.outboundSmsStore = outboundSmsStore;
    }

    @Autowired( required = false )
    protected void setTransportService( OutboundSmsTransportService transportService )
    {
        this.transportService = transportService;
        log.info( "Got OutboundSmsTransportService: " + transportService.getClass().getSimpleName() );

    }

    @Override
    public void initialize( SmsConfiguration smsConfiguration )
        throws SmsServiceException
    {
        if ( smsConfiguration != null )
        {
            enabled = smsConfiguration.isEnabled();
        }
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    @Transactional
    public void sendMessage( OutboundSms sms )
        throws SmsServiceException
    {
        if ( !enabled )
        {
            throw new SmsServiceNotEnabledException();
        }

        outboundSmsStore.save( sms );

        if ( transportService != null )
        {
            sendMessageInternal( sms );
        }
    }

    private void sendMessageInternal( OutboundSms sms )
    {
        try
        {
            transportService.sendMessage( sms );
            sms.setStatus( OutboundSmsStatus.SENT );
        }
        catch ( SmsServiceException e )
        {
            log.info( "Exception sending message " + sms, e );
            sms.setStatus( OutboundSmsStatus.ERROR );
        }
    }
}
