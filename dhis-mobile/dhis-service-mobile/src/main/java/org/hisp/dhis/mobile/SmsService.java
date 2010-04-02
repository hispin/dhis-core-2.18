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
package org.hisp.dhis.mobile;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.hisp.dhis.messaging.api.MessageService;
import org.hisp.dhis.mobile.api.DefaultMobileImportService;
import org.hisp.dhis.user.User;
import org.smslib.AGateway.Protocols;
import org.smslib.GatewayException;
import org.smslib.IInboundMessageNotification;
import org.smslib.IOutboundMessageNotification;
import org.smslib.IQueueSendingNotification;
import org.smslib.InboundBinaryMessage;
import org.smslib.InboundMessage;
import org.smslib.Message.MessageTypes;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.smslib.modem.SerialModemGateway;

public class SmsService implements MessageService
{

    private static String CONFIG_FILE = "SMSServer.conf";

    private static Service serv;

    private static boolean gatewayLoaded;

    private boolean serviceStatus;

    private Properties props;

    private InboundNotification inboundNotification;

    private OutboundNotification outboundNotification;

    public SmsService()
    {
        serv = new Service();
        inboundNotification = new InboundNotification();
        outboundNotification = new OutboundNotification();
    }

    private Service getService()
    {
        return serv;
    }

    @Override
    public boolean getServiceStatus()
    {
        return serviceStatus;
    }

    @Override
    public void setServiceStatus( boolean serviceStatus )
    {
        this.serviceStatus = serviceStatus;
    }

    @Override
    public void startService()
    {
        if ( !getServiceStatus() )
        {
            try
            {
                loadConfiguration();
                serv.startService();
                setServiceStatus( true );
            } catch ( Exception ex )
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void stopService()
    {
        if ( getServiceStatus() )
        {
            try
            {
                serv.stopService();
                setServiceStatus( false );
            } catch ( Exception ex )
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void sendMessage( String recipient, String msg )
    {
        OutboundMessage message = new OutboundMessage( recipient, msg );
        if ( getServiceStatus() )
        {
            try
            {
                serv.sendMessage( message );
            } catch ( TimeoutException ex )
            {
                getService().getLogger().logError( "Timeout error in sending message", ex, null );
            } catch ( GatewayException ex )
            {
                getService().getLogger().logError( "Gateway Exception in sending message", ex, null );
            } catch ( IOException ex )
            {
                getService().getLogger().logError( "IO Exception in sending message", ex, null );
            } catch ( InterruptedException ex )
            {
                getService().getLogger().logError( "Interrupted Exception in sending message", ex, null );
            }
        } else
        {
            getService().getLogger().logError( "Service not running", null, null );
        }
    }

    @Override
    public void processMessage( Object message )
    {
        try
        {
            InboundBinaryMessage binaryMsg = (InboundBinaryMessage) message;
            byte[] compressedData = binaryMsg.getDataBytes();
            String unCompressedText = new String( Compressor.decompress( compressedData ), "UTF-16" );

            String sender = binaryMsg.getOriginator();
            Date sendTime = binaryMsg.getDate();
            saveData( sender, sendTime, binaryMsg.getText() );

        } catch ( UnsupportedEncodingException uneex )
        {
            getService().getLogger().logError( "Error reading encoding: ", uneex, null );
            return;
        } catch ( ClassCastException ccex )
        {
            return;
        } catch ( ArithmeticException aex )
        {
            return;
        }
    }

    @Override
    public void sendAck( String recipient, Object message, String msg )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void saveData( String mobileNumber, Date sendTime, String data )
    {
        DefaultMobileImportService dmis = new DefaultMobileImportService();
        User user = dmis.getUserInfo( mobileNumber );
        Map<String, Integer> dataValues = new HashMap<String, Integer>();
        //TODO: remaining save
    }

    private void processStatusReport( InboundMessage message )
    {
        String originator = message.getOriginator();
        getService().getLogger().logInfo( "STATUS REPORT received from: " + originator, null, null );
    }


    /*------------------------------------------------------------------
     * Internal methods
    ------------------------------------------------------------------*/
    //<editor-fold defaultstate="collapsed" desc=" Internal Methods ">
    //<editor-fold defaultstate="collapsed" desc=" Load Configuration from DHIS2 HOME ">
    private void loadConfiguration() throws Exception
    {
        CONFIG_FILE = System.getenv( "DHIS2_HOME" ) + "/SMSServer.conf";
        FileInputStream f = new FileInputStream( CONFIG_FILE );
        this.props = new Properties();
        getProperties().load( f );
        f.close();

        //<editor-fold defaultstate="collapsed" desc=" Get Balancer ">
        if ( getProperties().getProperty( "smsserver.balancer", "" ).length() > 0 )
        {
            try
            {
                Object[] args = new Object[]
                {
                    getService()
                };
                Class<?>[] argsClass = new Class[]
                {
                    Service.class
                };
                Class<?> c = Class.forName( ( getProperties().getProperty( "smsserver.balancer", "" ).indexOf( '.' ) == -1 ? "org.smslib.balancing." : "" ) + getProperties().getProperty( "smsserver.balancer", "" ) );
                Constructor<?> constructor = c.getConstructor( argsClass );
                org.smslib.balancing.LoadBalancer balancer = (org.smslib.balancing.LoadBalancer) constructor.newInstance( args );
                getService().setLoadBalancer( balancer );
                getService().getLogger().logInfo( "SMSServer: set balancer to: " + getProperties().getProperty( "smsserver.balancer", "" ), null, null );
            } catch ( Exception e )
            {
                e.printStackTrace();
                getService().getLogger().logError( "SMSServer: error setting custom balancer!", null, null );
            }
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc=" Get Router ">
        if ( getProperties().getProperty( "smsserver.router", "" ).length() > 0 )
        {
            try
            {
                Object[] args = new Object[]
                {
                    getService()
                };
                Class<?>[] argsClass = new Class[]
                {
                    Service.class
                };
                Class<?> c = Class.forName( ( getProperties().getProperty( "smsserver.router", "" ).indexOf( '.' ) == -1 ? "org.smslib.routing." : "" ) + getProperties().getProperty( "smsserver.router", "" ) );
                Constructor<?> constructor = c.getConstructor( argsClass );
                org.smslib.routing.Router router = (org.smslib.routing.Router) constructor.newInstance( args );
                getService().setRouter( router );
                getService().getLogger().logInfo( "SMSServer: set router to: " + getProperties().getProperty( "smsserver.router", "" ), null, null );
            } catch ( Exception e )
            {
                getService().getLogger().logError( "SMSServer: error setting custom balancer!", null, null );
            }
        }
        //</editor-fold>

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
                String inbound = getProperties().getProperty( modemName + ".inbound" );
                String outbound = getProperties().getProperty( modemName + ".outbound" );

                SerialModemGateway gateway = new SerialModemGateway( modemName, port, baudRate, manufacturer, model );
                gateway.setProtocol( Protocols.PDU );

                if ( inbound.equalsIgnoreCase( "yes" ) )
                {
                    gateway.setInbound( true );
                    getService().setInboundMessageNotification( inboundNotification );
                } else
                {
                    gateway.setInbound( false );
                }
                if ( outbound.equalsIgnoreCase( "yes" ) )
                {
                    gateway.setOutbound( true );
                    getService().setOutboundMessageNotification( outboundNotification );
                } else
                {
                    gateway.setOutbound( false );
                }
                if ( !gatewayLoaded )
                {
                    getService().addGateway( gateway );
                }
                getService().getLogger().logInfo( "SMSServer: added gateway " + i + " / ", null, null );
            } catch ( Exception e )
            {
                getService().getLogger().logError( "SMSServer: Unknown Gateway in configuration file!", null, null );
                e.printStackTrace();
            }
        }
        gatewayLoaded = true;
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Get Properties from file ">
    private Properties getProperties()
    {
        return this.props;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" InboundNotification Class ">
    class InboundNotification implements IInboundMessageNotification
    {

        @Override
        public void process( String gatewayId, MessageTypes msgType, InboundMessage msg )
        {
            if ( msgType == MessageTypes.INBOUND )
            {
                getService().getLogger().logInfo( "New INBOUND MESSAGE on Gateway: " + gatewayId, null, null );
                processMessage( msg );
            } else
            {
                if ( msgType == MessageTypes.STATUSREPORT )
                {
                    getService().getLogger().logInfo( "New STATUS REPORT on Gateway: " + gatewayId, null, null );
                    processStatusReport( msg );
                }
            }
            if ( getProperties().getProperty( "settings.delete_after_processing", "no" ).equalsIgnoreCase( "yes" ) )
            {
                try
                {
                    getService().deleteMessage( msg );
                } catch ( Exception e )
                {
                    getService().getLogger().logError( "Error deleting received message!", e, null );
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" OutboundNotification Class ">
    class OutboundNotification implements IOutboundMessageNotification
    {

        @Override
        public void process( String gtwId, org.smslib.OutboundMessage msg )
        {
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" QueueSendingNotification Class ">
    class QueueSendingNotification implements IQueueSendingNotification
    {

        @Override
        public void process( String gtwId, OutboundMessage msg )
        {
            getService().getLogger().logInfo( "**** >>>> Now Sending: " + msg.getRecipient(), null, gtwId );
        }
    }
    //</editor-fold>
    //</editor-fold>
    /*----------------------------------------------------------------*/
}
