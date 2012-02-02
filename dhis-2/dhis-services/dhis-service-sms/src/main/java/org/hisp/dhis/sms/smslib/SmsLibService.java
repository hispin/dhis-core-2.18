package org.hisp.dhis.sms.smslib;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.hisp.dhis.sms.config.SmsGatewayConfig;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;
import org.smslib.SMSLibException;
import org.smslib.Service;
import org.smslib.Service.ServiceStatus;

public class SmsLibService
    implements OutboundSmsTransportService
{

    private static final Log log = LogFactory.getLog( SmsLibService.class );

    private GateWayFactory gatewayFactory = new GateWayFactory();

    private SmsConfiguration config;

    @Override
    public boolean isEnabled()
    {
        return config != null && config.isEnabled();
    }

    @Override
    public void sendMessage( OutboundSms sms )
        throws SmsServiceException
    {
        String recipient;

        Set<String> recipients = sms.getRecipients();

        if ( recipients.size() == 0 )
        {
            log.warn( "Trying to send sms without recipients: " + sms );
            return;
        }
        else if ( recipients.size() == 1 )
        {
            recipient = recipients.iterator().next();
        }
        else
        {
            recipient = createTmpGroup( recipients );
        }

        OutboundMessage message = new OutboundMessage( recipient, sms.getMessage() );
        String longNumber = config.getLongNumber();
        if ( longNumber != null && !longNumber.isEmpty() )
        {
            message.setFrom( longNumber );
        }

        boolean sent = false;

        try
        {
            log.debug( "Sending message " + sms );
            sent = getService().sendMessage( message );
        }
        catch ( SMSLibException e )
        {
            log.warn( "Unable to send message: " + sms, e );
            throw new SmsServiceException( "Unable to send message: " + sms, e );
        }
        catch ( IOException e )
        {
            log.warn( "Unable to send message: " + sms, e );
            throw new SmsServiceException( "Unable to send message: " + sms, e );
        }
        catch ( InterruptedException e )
        {
            log.warn( "Unable to send message: " + sms, e );
            throw new SmsServiceException( "Unable to send message: " + sms, e );
        }
        finally
        {
            if ( recipients.size() > 1 )
            {
                // Make sure we delete tmp. group
                removeGroup( recipient );
            }
        }

        if ( !sent )
        {

        }

    }

    @Override
    public void initialize( SmsConfiguration smsConfiguration )
        throws SmsServiceException
    {
        // FIXME: Implement a decent equals..
        // if (smsConfiguration.equals( config )) {
        // // nothing to do
        // return;
        // }

        log.debug( "Initializing SmsLib" );

        this.config = smsConfiguration;

        ServiceStatus status = getService().getServiceStatus();
        if ( status == ServiceStatus.STARTED || status == ServiceStatus.STARTING )
        {
            log.debug( "Stopping SmsLib" );
            stopService();
        }

        log.debug( "Loading configuration" );
        reloadConfig();

        if ( config.isEnabled() )
        {
            log.debug( "Starting SmsLib" );
            startService();
        }
        else
        {
            log.debug( "Sms not enabled, won't start service" );
        }
    }

    private String createTmpGroup( Set<String> recipients )
    {
        String groupName = Thread.currentThread().getName();

        getService().createGroup( groupName );
        for ( String recepient : recipients )
        {
            getService().addToGroup( groupName, recepient );
        }
        return groupName;
    }

    private void removeGroup( String groupName )
    {
        getService().removeGroup( groupName );
    }

    private void startService()
    {
        try
        {
            getService().startService();
        }
        catch ( SMSLibException e )
        {
            log.warn( "Unable to start smsLib service", e );
            throw new SmsServiceException( "Unable to start smsLib service", e );
        }
        catch ( IOException e )
        {
            log.warn( "Unable to start smsLib service", e );
            throw new SmsServiceException( "Unable to start smsLib service", e );
        }
        catch ( InterruptedException e )
        {
            log.warn( "Unable to start smsLib service", e );
            throw new SmsServiceException( "Unable to start smsLib service", e );
        }
    }

    private void stopService()
    {
        try
        {
            getService().stopService();
        }
        catch ( SMSLibException e )
        {
            log.warn( "Unable to stop smsLib service", e );
            throw new SmsServiceException( "Unable to stop smsLib service", e );
        }
        catch ( IOException e )
        {
            log.warn( "Unable to stop smsLib service", e );
            throw new SmsServiceException( "Unable to stop smsLib service", e );
        }
        catch ( InterruptedException e )
        {
            log.warn( "Unable to stop smsLib service", e );
            throw new SmsServiceException( "Unable to stop smsLib service", e );
        }
    }

    private Service getService()
    {
        return Service.getInstance();
    }

    private void reloadConfig()
        throws SmsServiceException
    {
        Service service = Service.getInstance();

        service.setOutboundMessageNotification( new OutboundNotification() );

        service.getGateways().clear();

        // Add gateways
        for ( SmsGatewayConfig gatewayConfig : config.getGateways() )
        {
            try
            {
                service.addGateway( gatewayFactory.create( gatewayConfig ) );
                log.debug( "Added gateway " + gatewayConfig.getName() );
            }
            catch ( GatewayException e )
            {
                log.warn( "Unable to load gateway " + gatewayConfig.getName(), e );
                throw new SmsServiceException( "Unable to load gateway" + gatewayConfig.getName(), e );
            }
        }
    }

    public class OutboundNotification
        implements IOutboundMessageNotification
    {

        @Override
        public void process( AGateway gateway, OutboundMessage msg )
        {
            log.debug( "Sent message through gateway " + gateway.getGatewayId() + ": " + msg );

        }
    }

}
