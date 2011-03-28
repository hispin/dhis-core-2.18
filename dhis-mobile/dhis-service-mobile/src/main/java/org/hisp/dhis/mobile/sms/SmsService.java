package org.hisp.dhis.mobile.sms;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.hisp.dhis.mobile.sms.api.SmsInbound;
import org.smslib.AGateway;
import org.smslib.AGateway.Protocols;
import org.smslib.GatewayException;
import org.smslib.ICallNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOrphanedMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.IQueueSendingNotification;
import org.smslib.InboundMessage;
import org.smslib.Message.MessageEncodings;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage;
import org.smslib.OutboundWapSIMessage;
import org.smslib.OutboundWapSIMessage.WapSISignals;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.helper.Logger;
import org.smslib.modem.SerialModemGateway;

/*
 * This class provides services for communicating over Sms
 * @author Saptarshi Purkayastha
 */
public class SmsService
{
    
    /*------------------------------------------------------------------
     * Dependencies
    ------------------------------------------------------------------*/
    HibernateSmsInboundStore hibernateSmsInboundStore;
    
    public void setHibernateSmsStore( HibernateSmsInboundStore hibernateSmsInboundStore )
    {
        this.hibernateSmsInboundStore = hibernateSmsInboundStore;
    }
    
    /*------------------------------------------------------------------
     * Implementation
    ------------------------------------------------------------------*/
    private Properties props;

    private InboundNotification inboundNotification;

    private OutboundNotification outboundNotification;

    private CallNotification callNotification;

    private QueueSendingNotification queueSendingNotification;

    private OrphanedMessageNotification orphanedMessageNotification;

    /*
     * Constructor called when SmsService is loaded
     */
    public SmsService()
    {
        Service.getInstance().setInboundMessageNotification( inboundNotification );
        Service.getInstance().setOutboundMessageNotification( outboundNotification );
        Service.getInstance().setCallNotification( callNotification );
        Service.getInstance().setOrphanedMessageNotification( orphanedMessageNotification );
        Service.getInstance().setQueueSendingNotification( queueSendingNotification );
    }

    /*------------------------------------------------------------------
     * Service methods
    ------------------------------------------------------------------*/
    /**
     * Method to start the service, if it is stopped
     * @return The message to be displayed on UI after attempt to start the service
     */
    //<editor-fold defaultstate="collapsed" desc="startSmsService()">
    public String startSmsService()
    {
        if ( Service.getInstance().getServiceStatus() == Service.ServiceStatus.STOPPED )
        {
            try
            {
                String result = loadConfiguration();
                if ( !result.contains( "ERROR" ) )
                {
                    Service.getInstance().startService();
                }
                return result;
            } catch ( Exception ex )
            {
                return "ERROR";
            }
        } else
        {
            return "CANNOT START: SERVICE ALREADY STARTING/STARTED/STOPPING";
        }
    }
    //</editor-fold>

    /**
     * Method to stop the service, if it is started
     * @return The message to be displayed on UI after attempt to stop the service
     */
    //<editor-fold defaultstate="collapsed" desc="stopSmsService()">
    public String stopSmsService()
    {
        if ( Service.getInstance().getServiceStatus() == Service.ServiceStatus.STARTED )
        {
            try
            {
                Service.getInstance().stopService();
                return "SERVICE STOPPED";
            } catch ( Exception ex )
            {
                return "ERROR";
            }
        } else
        {
            return "SERVICE ALREADY STOPPED";
        }
    }
    //</editor-fold>

    /**
     * To check if SmsService is running or not
     * @return true is service is started
     */
    //<editor-fold defaultstate="collapsed" desc="isServiceRunning()">
    public boolean isServiceRunning()
    {
        if ( Service.getInstance().getServiceStatus() == Service.ServiceStatus.STARTED )
        {
            return true;
        } else
        {
            return false;
        }
    }
    //</editor-fold>

    /**
     * Sends the OTA (Over-the-Air) message required to download settings, application or multimedia messages
     * @param recipient The phone number of the recipient
     * @param url The download URL
     * @param prompt The message to be displayed to the recipient
     * @return The message to be displayed on UI after attempt to send OTA message
     */
    //<editor-fold defaultstate="collapsed" desc="sendOtaMessages">
    public String sendOtaMessage( String recipient, String url, String prompt )
    {
        String status = new String();
        try
        {
            OutboundWapSIMessage wapMsg = new OutboundWapSIMessage( recipient, new URL( url ), prompt );
            wapMsg.setSignal( WapSISignals.HIGH );
            if ( isServiceRunning() )
            {
                Service.getInstance().sendMessage( wapMsg );
                status = "WAP MESSAGE SENT";
            } else
            {
                status = "SERVICE IS NOT RUNNING";
            }
        } catch ( Exception e )
        {
            status = "ERROR SENDING WAP MSG";
        }
        Logger.getInstance().logInfo( status, null, null );
        return status;
    }
    //</editor-fold>

    /**
     * Sends an SMS to group of users that is a list of phone numbers of recipients
     * @param groupName The name of the group of users
     * @param recepients A List of phone numbers
     * @param msg The message to be sent to the group
     * @return The message to be displayed on UI after attempt to send SMS to a group of users
     */
    //<editor-fold defaultstate="collapsed" desc="sendMessageToGroup">
    public String sendMessageToGroup( String groupName, List<String> recipients, String msg )
    {
        if ( isServiceRunning() )
        {
            Service.getInstance().createGroup( groupName );
            for ( String recepient : recipients )
            {
                Service.getInstance().addToGroup( groupName, recepient );
            }
            OutboundMessage message = new OutboundMessage( groupName, msg );

            try
            {
                Service.getInstance().sendMessage( message );
                Logger.getInstance().logInfo( "Message Sent to Group: " + groupName, null, null );
                return "SUCCESS";
            } catch ( TimeoutException ex )
            {
                Logger.getInstance().logError( "Timeout error in sending message", ex, null );
                return "ERROR";
            } catch ( GatewayException ex )
            {
                Logger.getInstance().logError( "Gateway Exception in sending message", ex, null );
                return "ERROR";
            } catch ( IOException ex )
            {
                Logger.getInstance().logError( "IO Exception in sending message", ex, null );
                return "ERROR";
            } catch ( InterruptedException ex )
            {
                Logger.getInstance().logError( "Interrupted Exception in sending message", ex, null );
                return "ERROR";
            } finally
            {
                Service.getInstance().removeGroup( groupName );
            }
        } else
        {
            Logger.getInstance().logError( "Service not running", null, null );
            return "SERVICE NOT RUNNING";
        }
    }
    //</editor-fold>

    /*------------------------------------------------------------------
     * SMSLIB CALLBACKS NOT IMPLEMENTED - ONLY USED FOR LOGGING
    ------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc="smslib callbacks">
    class InboundNotification implements IInboundMessageNotification
    {

        @Override
        public void process( org.smslib.AGateway gateway, MessageTypes msgType, InboundMessage msg )
        {
            Logger.getInstance().logInfo( "Received new message from: " + msg.getOriginator(), null, null );
        }
    }

    class OutboundNotification implements IOutboundMessageNotification
    {

        @Override
        public void process( org.smslib.AGateway gateway, org.smslib.OutboundMessage msg )
        {
            Logger.getInstance().logInfo( "Attempting or failed to send message from queue to: " + msg.getRecipient(), null, null );
        }
    }

    class CallNotification implements ICallNotification
    {

        @Override
        public void process( org.smslib.AGateway gateway, String callerId )
        {
            Logger.getInstance().logInfo( "Receiving call from: " + callerId, null, null );
        }
    }

    class QueueSendingNotification implements IQueueSendingNotification
    {

        @Override
        public void process( org.smslib.AGateway gateway, OutboundMessage msg )
        {
            Logger.getInstance().logInfo( "**** >>>> Now Sending: " + msg.getRecipient(), null, gateway.getGatewayId() );
        }
    }

    class OrphanedMessageNotification implements IOrphanedMessageNotification
    {

        @Override
        public boolean process( org.smslib.AGateway gateway, InboundMessage msg )
        {
            System.out.println( "&&&&&&&&&&&&&&&&& ORPHANED INFO &&&&&&&&&&&&&&&&&" );
            System.out.println( msg );
            System.out.println( "&&&&&&&&&&&&&&&&& ORPHANED INFO &&&&&&&&&&&&&&&&&" );
            Logger.getInstance().logInfo( "Leaving orphaned message in queue ", null, gateway.getGatewayId() );
            // Return FALSE to leave orphaned parts in memory.
            return false;
        }
    }
    //</editor-fold>

    /*------------------------------------------------------------------
     * Helper Methods
    ------------------------------------------------------------------*/
    public Properties getProperties()
    {
        return props;
    }

    //<editor-fold defaultstate="collapsed" desc="readMessages()">
    /**
     * Read the messages from the memory location and save it in the sms_inbound
     */
    void readMessages()
    {
        List<InboundMessage> msgList = new ArrayList<InboundMessage>();
        try
        {
            // Read the messages from SIM memory location, which are Inbound messages
            Service.getInstance().readMessages( msgList, InboundMessage.MessageClasses.ALL );
            if ( msgList.size() > 0 )
            {
                for ( InboundMessage msg : msgList )
                {
                    //Creating sms to store in database
                    SmsInbound sms = new SmsInbound();
                    
                    //Set sms encoding
                    if(msg.getEncoding() == MessageEncodings.ENC7BIT)
                        sms.setEncoding( '7' );
                    else if(msg.getEncoding() == MessageEncodings.ENC8BIT)
                        sms.setEncoding( '8' );
                    else if(msg.getEncoding() == MessageEncodings.ENCUCS2)
                        sms.setEncoding( 'U' );
                    
                    sms.setGatewayId( msg.getGatewayId());
                    sms.setMessageDate( msg.getDate() );
                    sms.setOriginalReceiveDate( msg.getDate() );
                    sms.setOriginalRefNo( String.valueOf( msg.getMpRefNo() ) );
                    sms.setOriginator( msg.getOriginator() );
                    sms.setProcess( 0 );
                    sms.setReceiveDate( msg.getDate());
                    sms.setText( msg.getText() );
                    sms.setType( 'I' );
                    
                    //saving sms into database
                    hibernateSmsInboundStore.saveSms( sms );
                    
                    //Delete message based on configuration
                    if ( getProperties().getProperty( "settings.delete_after_processing", "no" ).equalsIgnoreCase( "yes" ) )
                    {
                        Service.getInstance().deleteMessage( msg );
                    }
                }
            }
        } catch ( Exception e )
        {
            Logger.getInstance().logError( "SMSServer: reading messages exception!", e, null );
        }
    }
    //</editor-fold>

    /*void sendMessages()
    {
        boolean foundOutboundGateway = false;
        for ( org.smslib.AGateway gtw : Service.getInstance().getGateways() )
        {
            if ( gtw.isOutbound() )
            {
                foundOutboundGateway = true;
                break;
            }
        }
        if ( foundOutboundGateway )
        {
            List<OutboundMessage> msgList = new ArrayList<OutboundMessage>();
            try
            {
                for ( Interface<? extends Object> inf : getInfList() )
                {
                    if ( inf.isOutbound() )
                    {
                        msgList.addAll( inf.getMessagesToSend() );
                    }
                }
            } catch ( Exception e )
            {
                Logger.getInstance().logError( "SMSServer: sending messages exception!", e, null );
            }
            if ( getProperties().getProperty( "settings.send_mode", "sync" ).equalsIgnoreCase( ( "sync" ) ) )
            {
                Logger.getInstance().logInfo( "SMSServer: sending synchronously...", null, null );
                for ( OutboundMessage msg : msgList )
                {
                    try
                    {
                        Service.getInstance().sendMessage( msg );
                        for ( Interface<? extends Object> inf : getInfList() )
                        {
                            if ( inf.isOutbound() )
                            {
                                inf.markMessage( msg );
                            }
                        }
                    } catch ( Exception e )
                    {
                        Logger.getInstance().logError( "SMSServer: sending messages exception!", e, null );
                        try
                        {
                            for ( Interface<? extends Object> inf : getInfList() )
                            {
                                if ( inf.isOutbound() )
                                {
                                    inf.markMessage( msg );
                                }
                            }
                        } catch ( Exception e1 )
                        {
                            Logger.getInstance().logError( "SMSServer: sending messages exception!", e1, null );
                        }
                    }
                }
            } else
            {
                Logger.getInstance().logInfo( "SMSServer: sending asynchronously... [" + msgList.size() + "]", null, null );
                for ( OutboundMessage msg : msgList )
                {
                    if ( !Service.getInstance().queueMessage( msg ) )
                    {
                        try
                        {
                            for ( Interface<? extends Object> inf : getInfList() )
                            {
                                if ( inf.isOutbound() )
                                {
                                    inf.markMessage( msg );
                                }
                            }
                        } catch ( Exception e )
                        {
                            Logger.getInstance().logError( "SMSServer: sending messages exception!", e, null );
                        }
                    }
                }
            }
        }
    }*/

    /**
     * Loads the configuration settings from SMSServer.conf, which should be located in the DHIS2_HOME directory
     * @return The message to be displayed on UI after loading of configuration from file is complete
     * @throws Exception 
     */
    //<editor-fold defaultstate="collapsed" desc=" Load Configuration from DHIS2 HOME ">
    private String loadConfiguration() throws Exception
    {
        String configFile = System.getenv( "DHIS2_HOME" ) + File.separator + "SMSServer.conf";

        if ( new File( configFile ).exists() )
        {
            //Remove all existing gateways
            Collection<AGateway> gateways = Service.getInstance().getGateways();
            for ( AGateway gateway : gateways )
            {
                Service.getInstance().removeGateway( gateway );
            }

            //Load properties from configuration file
            FileInputStream f = new FileInputStream( configFile );
            this.props = new Properties();
            getProperties().load( f );
            f.close();

            //Add gateway to service based on configuration file
            //<editor-fold defaultstate="collapsed" desc=" Get Gateway & Configuration ">
            for ( int i = 0; i < Integer.MAX_VALUE; i++ )
            {
                try
                {
                    String propName = "gateway." + i;
                    String propValue = getProperties().getProperty( propName, "" ).trim();
                    if ( propValue.length() == 0 )
                    {
                        break;
                    }
                    String modemName = propValue.split( "\\," )[0].trim();
                    String port = getProperties().getProperty( modemName + ".port" );
                    int baudRate = Integer.parseInt( getProperties().getProperty( modemName + ".baudrate" ) );
                    String manufacturer = getProperties().getProperty( modemName + ".manufacturer" );
                    String model = getProperties().getProperty( modemName + ".model" );
                    String protocol = getProperties().getProperty( modemName + ".protocol" );
                    String pin = getProperties().getProperty( modemName + ".pin" );
                    String inbound = getProperties().getProperty( modemName + ".inbound" );
                    String outbound = getProperties().getProperty( modemName + ".outbound" );
                    String simMemLocation = getProperties().getProperty( modemName + ".simMemLocation" );

                    // TODO: DETECT MODEM CLASS AND INSTANTIATE
                    SerialModemGateway gateway = new SerialModemGateway( modemName, port, baudRate, manufacturer, model );

                    if ( simMemLocation != null && !simMemLocation.equals( "-" ) )
                    {
                        gateway.getATHandler().setStorageLocations( simMemLocation );
                    }

                    if ( protocol != null && protocol.equalsIgnoreCase( "PDU" ) )
                    {
                        gateway.setProtocol( Protocols.PDU );
                    } else
                    {
                        if ( protocol != null && protocol.equalsIgnoreCase( "TEXT" ) )
                        {
                            gateway.setProtocol( Protocols.TEXT );

                        } else
                        {
                            gateway.setProtocol( Protocols.PDU );
                        }
                    }
                    if ( pin != null )
                    {
                        gateway.setSimPin( pin );
                    }

                    if ( inbound.equalsIgnoreCase( "yes" ) )
                    {
                        gateway.setInbound( true );
                    } else
                    {
                        gateway.setInbound( false );
                    }
                    if ( outbound.equalsIgnoreCase( "yes" ) )
                    {
                        gateway.setOutbound( true );
                    } else
                    {
                        gateway.setOutbound( false );
                    }
                    Logger.getInstance().logInfo( "SMSServer: added gateway " + i + " / ", null, null );
                } catch ( Exception e )
                {
                    Logger.getInstance().logError( "SMSServer: Unknown Gateway in configuration file!, " + e.getMessage(), null, null );
                }
            }
            //</editor-fold>
            return "SUCCESSFULLY STARTED SERVICE";
        } else
        {
            return "ERROR LOADING CONFIGURATION FILE";
        }
    }
    //</editor-fold>
}
