package org.hisp.dhis.sms.inbound;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsService;
import org.smslib.InboundMessage;
import org.smslib.Service;

public class DefaultInboundSmsService
    implements IncomingSmsService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private List<InboundMessage> msgList = new ArrayList<InboundMessage>();

    public void setMsgList( List<InboundMessage> msgList )
    {
        this.msgList = msgList;
    }

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public List<IncomingSms> listAllMessage()
    {
        List<IncomingSms> result = new ArrayList<IncomingSms>();

        try
        {
            Service.getInstance().readMessages( msgList, InboundMessage.MessageClasses.ALL );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        if ( msgList.size() > 0 )
        {
            for ( InboundMessage each : msgList )
            {
                IncomingSms incomingSms = new IncomingSms();

                incomingSms.setGatewayId( each.getGatewayId() );

                incomingSms.setOriginator( each.getOriginator() );

                incomingSms.setText( each.getText() );

                result.add( incomingSms );
            }

            msgList.clear();
        }

        return result;
    }

    @Override
    public List<InboundMessage> getMsgList()
    {
        try
        {
            Service.getInstance().readMessages( msgList, InboundMessage.MessageClasses.ALL );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return msgList;
    }

    @Override
    public IncomingSms getNextUnprocessed()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IncomingSms get( int id )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void update( IncomingSms sms )
    {
        // TODO Auto-generated method stub

    }

}
