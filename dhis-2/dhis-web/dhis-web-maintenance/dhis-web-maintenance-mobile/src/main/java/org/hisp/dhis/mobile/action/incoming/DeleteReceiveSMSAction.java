package org.hisp.dhis.mobile.action.incoming;

import java.util.List;
import org.hisp.dhis.sms.incoming.IncomingSmsService;
import org.smslib.InboundMessage;
import org.smslib.Service;

import com.opensymphony.xwork2.Action;

public class DeleteReceiveSMSAction
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

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer[] ids;

    public void setIds( Integer[] ids )
    {
        this.ids = ids;
    }
    
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        List<InboundMessage> msgList = incomingSmsService.getMsgList();

        if ( ids != null && ids.length > 0 )
        {
            for ( Integer index : ids )
            {
                Service.getInstance().deleteMessage( msgList.get( index - 1 ) );
            }
        }
        if ( id != null )
        {
            Service.getInstance().deleteMessage( msgList.get( id - 1 ) );
        }
        msgList.clear();

        return SUCCESS;
    }

}
