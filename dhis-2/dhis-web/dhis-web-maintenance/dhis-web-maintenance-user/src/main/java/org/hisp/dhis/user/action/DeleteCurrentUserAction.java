/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.user.action;


import com.opensymphony.xwork2.Action;
import java.util.Collection;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.user.UserSetting;
import org.hisp.dhis.user.UserStore;

/**
 *
 * @author Administrator
 */
public class DeleteCurrentUserAction implements Action
{

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private PasswordManager passwordManager;

    public void setPasswordManager( PasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private String username;

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    private String oldPassword;

    public String getOldPassword()
    {
        return oldPassword;
    }

    public void setOldPassword( String oldPassword )
    {
        this.oldPassword = oldPassword;
    }

    @Override
    public String execute() throws Exception
    {
        message = " ";
        User user = userStore.getUser( currentUserService.getCurrentUser().getId() );

        UserCredentials userCredentials = userStore.getUserCredentials( user );

        username = userCredentials.getUsername();
        String oldPasswordFromDB = userCredentials.getPassword();

        if ( oldPassword == null )
        {
            return INPUT;
        }

        oldPassword = oldPassword.trim();

        if ( oldPassword.length() == 0 )
        {
            return INPUT;
        }
        //System.out.println( "oldPasswordFromDB  = " + oldPasswordFromDB + " encoded old password = " + passwordManager.encodePassword( userCredentials.getUsername(), oldPassword ) );
        String oldEncodedPassword = passwordManager.encodePassword( userCredentials.getUsername(), oldPassword ) ;
        if ( !oldEncodedPassword.equals( oldPasswordFromDB ) )
        {
            message = i18n.getString( "wrong_password" );
            return INPUT;
        }
        else
        {
            Collection<UserSetting> userSettings = userStore.getAllUserSettings( user );

            for ( UserSetting userSetting : userSettings )
            {
                userStore.deleteUserSetting( userSetting );
            }

            if ( userService.isLastSuperUser( userCredentials ) )
            {
                message = i18n.getString( "can_not_remove_last_super_user" );
                return INPUT;
            } else
            {
                userStore.deleteUserCredentials( userStore.getUserCredentials( user ) );
                userStore.deleteUser( user );
            }

            
            return "logout";
        }
    }
}
