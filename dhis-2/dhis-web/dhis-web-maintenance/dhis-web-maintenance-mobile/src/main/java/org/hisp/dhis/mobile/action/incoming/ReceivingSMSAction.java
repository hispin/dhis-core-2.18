package org.hisp.dhis.mobile.action.incoming;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.sms.config.ModemGatewayConfig;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsService;

import com.opensymphony.xwork2.Action;

public class ReceivingSMSAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IncomingSmsService incomingSmsService;

    public void setIncomingSmsService( IncomingSmsService incomingSmsService )
    {
        this.incomingSmsService = incomingSmsService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<IncomingSms> listIncomingSms = new ArrayList<IncomingSms>();

    public List<IncomingSms> getListIncomingSms()
    {
        return listIncomingSms;
    }

    private int currentMessages;

    public int getCurrentMessages()
    {
        return currentMessages;
    }

    public void setCurrentMessages( int currentMessages )
    {
        this.currentMessages = currentMessages;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        listIncomingSms = incomingSmsService.listAllMessage();

        if ( listIncomingSms.size() > currentMessages )
        {
            message = i18n.getString( "new_message" );

            currentMessages = listIncomingSms.size();
        }

        return SUCCESS;
    }

}
