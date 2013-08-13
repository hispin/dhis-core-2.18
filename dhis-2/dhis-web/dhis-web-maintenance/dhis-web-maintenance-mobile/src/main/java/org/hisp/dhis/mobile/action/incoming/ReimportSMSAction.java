package org.hisp.dhis.mobile.action.incoming;

import java.util.List;

import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsListener;
import org.hisp.dhis.sms.incoming.IncomingSmsService;
import org.hisp.dhis.sms.incoming.SmsMessageStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ReimportSMSAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IncomingSmsService incomingSmsService;

    private List<IncomingSmsListener> listeners;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String incomingSMSId;

    private IncomingSms incomingSMS;

    @Autowired
    public void setListeners( List<IncomingSmsListener> listeners )
    {
        this.listeners = listeners;
    }

    public IncomingSmsService getIncomingSmsService()
    {
        return incomingSmsService;
    }

    public void setIncomingSmsService( IncomingSmsService incomingSmsService )
    {
        this.incomingSmsService = incomingSmsService;
    }

    public String getIncomingSMSId()
    {
        return incomingSMSId;
    }

    public void setIncomingSMSId( String incomingSMSId )
    {
        this.incomingSMSId = incomingSMSId;
    }

    public IncomingSms getIncomingSMS()
    {
        return incomingSMS;
    }

    public void setIncomingSMS( IncomingSms incomingSMS )
    {
        this.incomingSMS = incomingSMS;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        incomingSMS = incomingSmsService.findBy( Integer.parseInt( incomingSMSId ) );

        if ( incomingSMS == null )
        {
            return "error";
        }

        try
        {
            for ( IncomingSmsListener listener : listeners )
            {
                if ( listener.accept( incomingSMS ) )
                {
                    listener.receive( incomingSMS );
                    incomingSMS.setStatus( SmsMessageStatus.PROCESSED );
                    incomingSmsService.update( incomingSMS );
                    message = "SMS imported";
                    return SUCCESS;
                }
            }
            message = "No Command Found";
        }
        catch ( Exception e )
        {
            message = e.getMessage();
            return "error";
        }

        return SUCCESS;
    }

}
