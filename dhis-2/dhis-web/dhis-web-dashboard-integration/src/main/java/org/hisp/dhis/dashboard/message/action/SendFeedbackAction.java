package org.hisp.dhis.dashboard.message.action;

import org.hisp.dhis.message.Message;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

public class SendFeedbackAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String subject;
    
    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    private String text;

    public void setText( String text )
    {
        this.text = text;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        User sender = currentUserService.getCurrentUser();

        Message message = new Message( subject, text, sender );
        
        messageService.sendFeedback( message );
        
        return SUCCESS;
    }
}
