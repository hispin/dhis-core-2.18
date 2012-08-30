package org.hisp.dhis.sms.input;

import java.text.ParseException;

import org.exolab.castor.types.Date;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsStore;
import org.hisp.dhis.sms.incoming.SmsMessageEncoding;
import org.hisp.dhis.sms.incoming.SmsMessageStatus;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.hisp.dhis.sms.parse.ParserManager;
import org.hisp.dhis.sms.parse.SMSParserException;

import com.opensymphony.xwork2.Action;

/**
 * 
 * @author Christian and Magnus
 */
public class SMSInput
    implements Action
{

    private ParserManager smsParserManager;

    private String msisdn, sender, message, dca, reffering_batch, network_id, concat_reference, concat_num_segments,
        concat_seq_num, received_time;

    private String source_id; // Probably like message id and should be an int

    private int msg_id; // unique for each sms

    private IncomingSms sms;

    private IncomingSmsStore smsStore;

    private OutboundSmsService outboundSmsService;

    public void setOutboundSmsService( OutboundSmsService outboundSmsService )
    {
        this.outboundSmsService = outboundSmsService;
    }

    public SMSInput()
    {
    }

    @Override
    public String execute()
        throws Exception
    {

        System.out.println( "Sender: " + sender + ", Message: " + message );
        IncomingSms sms = new IncomingSms();
        sms.setText( message );
        sms.setOriginator( sender );

        java.util.Date rec = null;
        try
        {
            Date received = Date.parseDate( received_time );
            rec = received.toDate();
        }
        catch ( ParseException pe )
        {
            System.out.println( "ERROR: No received_time input" );
            return ERROR;
        }
        sms.setReceivedDate( rec );
        sms.setSentDate( rec ); // This should probably be removed from incoming
                                // SMS entirely. Though other gateways may use
                                // it?
        sms.setEncoding( SmsMessageEncoding.ENC7BIT );
        sms.setStatus( SmsMessageStatus.INCOMING );
        sms.setId( msg_id );
        sms.setGatewayId( "HARDCODEDTESTGATEWAY" );

        smsStore.save( sms );

        try
        {
            smsParserManager.parse( sender, message );
        }
        catch ( SMSParserException e )
        {
            sendSMS( e.getMessage() );
            return ERROR;
        }

        sendSMS( "SMS successfully received" );
        // TODO DataEntry stuff
        return SUCCESS;
    }

    private void sendSMS( String message )
    {
        if ( outboundSmsService != null )
        {
            outboundSmsService.sendMessage( new OutboundSms( message, sender ), null );
        }
        else
        {
            // Just for testing
            System.out.println( "\n\n\n SMS: " + message + "\n\n\n" );
        }
    }

    public void setSmsStore( IncomingSmsStore smsStore )
    {
        System.out.println( "Setting SMSStore: " + smsStore );
        this.smsStore = smsStore;
    }

    public String getConcat_num_segments()
    {
        return concat_num_segments;
    }

    public void setConcat_num_segments( String concat_num_segments )
    {
        this.concat_num_segments = concat_num_segments;
    }

    public String getConcat_reference()
    {
        return concat_reference;
    }

    public void setConcat_reference( String concat_reference )
    {
        this.concat_reference = concat_reference;
    }

    public String getConcat_seq_num()
    {
        return concat_seq_num;
    }

    public void setConcat_seq_num( String concat_seq_num )
    {
        this.concat_seq_num = concat_seq_num;
    }

    public String getDca()
    {
        return dca;
    }

    public void setDca( String dca )
    {
        this.dca = dca;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public int getMsg_id()
    {
        return msg_id;
    }

    public void setMsg_id( int msg_id )
    {
        this.msg_id = msg_id;
    }

    public String getMsisdn()
    {
        return msisdn;
    }

    public void setMsisdn( String msisdn )
    {
        this.msisdn = msisdn;
    }

    public String getNetwork_id()
    {
        return network_id;
    }

    public void setNetwork_id( String network_id )
    {
        this.network_id = network_id;
    }

    public String getReceived_time()
    {
        return received_time;
    }

    public void setReceived_time( String received_time )
    {
        this.received_time = received_time;
    }

    public String getReffering_batch()
    {
        return reffering_batch;
    }

    public void setReffering_batch( String reffering_batch )
    {
        this.reffering_batch = reffering_batch;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender( String sender )
    {
        this.sender = sender;
    }

    public IncomingSms getSms()
    {
        return sms;
    }

    public void setSms( IncomingSms sms )
    {
        this.sms = sms;
    }

    public String getSource_id()
    {
        return source_id;
    }

    public void setSource_id( String source_id )
    {
        this.source_id = source_id;
    }

    public void setSmsParserManager( ParserManager smsParserManager )
    {
        this.smsParserManager = smsParserManager;
    }

}
