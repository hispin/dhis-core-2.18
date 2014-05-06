package org.hisp.dhis.light.message.action;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.Action;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class AddRecipientAction
    implements Action
{
    private static final Log log = LogFactory.getLog( AddRecipientAction.class );

    public AddRecipientAction()
    {
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    public UserService getUserService()
    {
        return userService;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String recipientCheckBox;
    public String getRecipientCheckBox()
    {
        return recipientCheckBox;
    }

    public void setRecipientCheckBox( String recipientCheckBox )
    {
        this.recipientCheckBox = recipientCheckBox;
    }

    private Set<User> recipient;
    public Set<User> getRecipient()
    {
        return recipient;
    }

    public void setRecipients( Set<User> recipient )
    {
        this.recipient = recipient;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        updateRecipients(recipientCheckBox);
        return SUCCESS;
    }

    /**
     * 
     * @param recipientCheckBox
     */
    private void updateRecipients(String recipientCheckBox)
    {
        recipient = new HashSet<User>();
        
        if ( recipientCheckBox != null )
        {
            String rcbArray[] = recipientCheckBox.split( "," );

            for ( int i = 0; i < rcbArray.length; i++ )
            {
                rcbArray[i] = rcbArray[i].trim();
                User u = userService.getUser( Integer.parseInt( rcbArray[i] ) );
                recipient.add( u );
            }
        }
    }
}