package org.hisp.dhis.sms.input;

import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsService;
import org.hisp.dhis.sms.incoming.SmsMessageEncoding;
import org.hisp.dhis.sms.incoming.SmsMessageStatus;

import com.opensymphony.xwork2.Action;

/**
 * 
 * @author Christian and Magnus
 */
public class SMSInput
    implements Action
{

    private String sender, message;

    private String phone, text;

    private IncomingSmsService incomingSmsService;

    @Override
    public String execute()
        throws Exception
    {
        IncomingSms sms = new IncomingSms();

        // case 1 for sender and message
        if ( sender != null || message != null )
        {
            if ( sender == null || message == null )
            {
                setNullToAll();
                return ERROR;
            }
            else
            {
                sms.setText( message );
                sms.setOriginator( sender );
            }
        }

        // case 2 for phone and text
        if ( phone != null || text != null )
        {
            if ( phone == null || text == null )
            {
                setNullToAll();
                return ERROR;
            }
            else
            {
                sms.setText( text );
                sms.setOriginator( phone );
            }
        }

        // case 3 for all is null
        if ( sender == null && message == null && phone == null && text == null )
        {
            return ERROR;
        }
        java.util.Date rec = new java.util.Date();
        sms.setReceivedDate( rec );
        sms.setSentDate( rec );

        sms.setEncoding( SmsMessageEncoding.ENC7BIT );
        sms.setStatus( SmsMessageStatus.INCOMING );
        sms.setGatewayId( "HARDCODEDTESTGATEWAY" );

        incomingSmsService.save( sms );

        setNullToAll();

        return SUCCESS;
    }

    public void setNullToAll()
    {
        sender = null;
        message = null;
        phone = null;
        text = null;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender( String sender )
    {
        this.sender = sender;
    }

    public void setPhone( String phone )
    {
        this.phone = phone;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public void setIncomingSmsService( IncomingSmsService incomingSmsService )
    {
        this.incomingSmsService = incomingSmsService;
    }
}
