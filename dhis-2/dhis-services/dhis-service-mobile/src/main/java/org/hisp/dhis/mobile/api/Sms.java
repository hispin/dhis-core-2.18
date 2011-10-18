package org.hisp.dhis.mobile.api;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class Sms
    implements Serializable
{

    private static final long serialVersionUID = 5717326947146473324L;

    private Integer id;

    /*
     * The originator of the received message.
     */
    private String originator;

    private SmsMessageType type;

    private SmsMessageEncoding encoding;

    /*
     * The message date (retrieved by the message headers).
     */
    private Date messageDate;

    /*
     * The datetime when message was received.
     */
    private Date receiveDate;

    /*
     * The body of the message.
     */
    private String text;

    /*
     * Available only for status report messages: refers to the RefNo of the
     * original outbound message.
     */
    private String originalRefNo;

    /*
     * Available only for status report messages: refers to the receive date of
     * the original outbound message.
     */
    private Date originalReceiveDate;

    /*
     * The ID of the gateway from which the message was received.
     */
    private String gatewayId;

    private byte[] bytes;

    private SmsMessageStatus status = SmsMessageStatus.INCOMING;

    private String statusMessage;

    public boolean isBinary()
    {
        return bytes != null;
    }

    // -------------------------------------------------------------------------
    // Getter-Setters
    // -------------------------------------------------------------------------

    public SmsMessageEncoding getEncoding()
    {
        return encoding;
    }

    public void setEncoding( SmsMessageEncoding encoding )
    {
        this.encoding = encoding;
    }

    public String getGatewayId()
    {
        return gatewayId;
    }

    public void setGatewayId( String gatewayId )
    {
        this.gatewayId = gatewayId;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Date getMessageDate()
    {
        return messageDate;
    }

    public void setMessageDate( Date messageDate )
    {
        this.messageDate = messageDate;
    }

    public Date getOriginalReceiveDate()
    {
        return originalReceiveDate;
    }

    public void setOriginalReceiveDate( Date originalReceiveDate )
    {
        this.originalReceiveDate = originalReceiveDate;
    }

    public String getOriginalRefNo()
    {
        return originalRefNo;
    }

    public void setOriginalRefNo( String originalRefNo )
    {
        this.originalRefNo = originalRefNo;
    }

    public String getOriginator()
    {
        return originator;
    }

    public void setOriginator( String originator )
    {
        this.originator = originator;
    }

    public Date getReceiveDate()
    {
        return receiveDate;
    }

    public void setReceiveDate( Date receiveDate )
    {
        this.receiveDate = receiveDate;
    }

    public String getText()
    {
        return text;
    }

    public void setText( String text )
    {
        this.text = text;
    }

    public SmsMessageType getType()
    {
        return type;
    }

    public void setType( SmsMessageType type )
    {
        this.type = type;
    }

    public byte[] getBytes()
    {
        return bytes;
    }

    public void setBytes( byte[] bytes )
    {
        this.bytes = bytes;
    }

    public SmsMessageStatus getStatus()
    {
        return status;
    }

    public void setStatus( SmsMessageStatus status )
    {
        this.status = status;
    }

    public String getStatusMessage()
    {
        return statusMessage;
    }

    public void setStatusMessage( String statusMessage )
    {
        this.statusMessage = statusMessage;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    public int hashCode()
    {
        return this.hashCode();
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }
        if ( !(o instanceof Sms) )
        {
            return false;
        }

        final Sms other = (Sms) o;
        return o.equals( other );
    }

    @Override
    public String toString()
    {
        return "Sms [id=" + id + ", originator=" + originator + ", type=" + type + ", encoding=" + encoding
            + ", messageDate=" + messageDate + ", receiveDate=" + receiveDate + ", text=" + text + ", originalRefNo="
            + originalRefNo + ", originalReceiveDate=" + originalReceiveDate + ", gatewayId=" + gatewayId + ", bytes="
            + Arrays.toString( bytes ) + ", status=" + status + ", statusMessage=" + statusMessage +"]";
    }
}