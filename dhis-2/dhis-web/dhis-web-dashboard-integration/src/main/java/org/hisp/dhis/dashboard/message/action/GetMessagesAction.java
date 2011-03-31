package org.hisp.dhis.dashboard.message.action;

import java.util.List;

import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.message.UserMessage;

import com.opensymphony.xwork2.Action;

public class GetMessagesAction
    implements Action
{
    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }
    
    private List<UserMessage> messages;

    public List<UserMessage> getMessages()
    {
        return messages;
    }

    @Override
    public String execute()
        throws Exception
    {
        messages = messageService.getUserMessages( 1, 1000 );
        
        return SUCCESS;
    }
}
