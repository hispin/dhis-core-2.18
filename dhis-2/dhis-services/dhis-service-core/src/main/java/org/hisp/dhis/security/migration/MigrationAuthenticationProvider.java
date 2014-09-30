package org.hisp.dhis.security.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

/**
 * Implements migration of legacy user password hashes on user login.
 *
 * The procedure to do so works by preceding the ordinary authentication check
 * (which is performed using the current password hashing method) with an authentication
 * procedure using the legacy password hashing method.
 *
 * If the currently stored hash and the legacyHash(suppliedPassword, usernameSalt) matches
 * the password is hashed again using the current method and replaces the stored hash for the user.
 * The user is now migrated to the current password hashing scheme and will on next logon not
 * authenticate using the legacy hash method but the current one.
 *
 * In either case the call is followed by the authentication procedure in DaoAuthenticationProvider
 * which performs the final authentication (using the current method).
 *
 * @author Halvdan Hoem Grelland
 */
public class MigrationAuthenticationProvider
    extends DaoAuthenticationProvider
{
    private static final Log log = LogFactory.getLog( MigrationAuthenticationProvider.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private MigrationPasswordManager passwordManager;

    public void setPasswordManager( MigrationPasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    // -------------------------------------------------------------------------
    // Pre-auth check-and-switch for legacy password hash match
    // -------------------------------------------------------------------------

    @Override
    protected void additionalAuthenticationChecks( UserDetails userDetails,
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken )
        throws AuthenticationException
    {
        String password = (String) usernamePasswordAuthenticationToken.getCredentials();
        String username = userDetails.getUsername();

        // If legacyHash(password, username) matches stored hash, re-hash password with current method and switch with stored hash
        if( passwordManager.legacyMatches( userDetails.getPassword(), password, username ) )
        {
            UserCredentials userCredentials = userService.getUserCredentialsByUsername( username );

            if ( userCredentials != null )
            {
                userCredentials.setPassword( passwordManager.encodePassword( password ) );
                userCredentials.setPasswordLastUpdated( new Date() );
                userService.updateUser( userCredentials.getUser() );

                log.info( "User " + userCredentials.getUsername() + " was migrated from " + passwordManager.getLegacyPasswordEncoderClassName() +
                    " to " + passwordManager.getPasswordEncoderClassName() + " based password hashing on login." );

                userDetails = getUserDetailsService().loadUserByUsername( username );
            }
        }
        super.additionalAuthenticationChecks( userDetails, usernamePasswordAuthenticationToken );
    }
}
