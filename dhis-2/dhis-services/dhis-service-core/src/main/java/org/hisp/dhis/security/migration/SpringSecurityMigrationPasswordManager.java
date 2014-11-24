package org.hisp.dhis.security.migration;

import org.hisp.dhis.security.UsernameSaltSource;
import org.hisp.dhis.security.spring.SpringSecurityPasswordManager;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 * @author Halvdan Hoem Grelland
 */
public class SpringSecurityMigrationPasswordManager
    extends SpringSecurityPasswordManager
    implements MigrationPasswordManager
{
    public static String legacyPasswordEncoderClassName;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PasswordEncoder legacyPasswordEncoder;

    public void setLegacyPasswordEncoder( PasswordEncoder legacyPasswordEncoder )
    {
        this.legacyPasswordEncoder = legacyPasswordEncoder;
        legacyPasswordEncoderClassName = legacyPasswordEncoder.getClass().getName();
    }

    private UsernameSaltSource usernameSaltSource;

    public void setUsernameSaltSource( UsernameSaltSource usernameSaltSource )
    {
        this.usernameSaltSource = usernameSaltSource;
    }

    // -------------------------------------------------------------------------
    // MigrationPasswordManager implementation
    // -------------------------------------------------------------------------

    @Override
    public final String legacyEncode( String password, String username )
    {
        return legacyPasswordEncoder.encodePassword( password, usernameSaltSource.getSalt( username ) );
    }

    @Override
    public boolean legacyMatches( String rawPassword, String encodedPassword, String username )
    {
        return legacyPasswordEncoder.isPasswordValid( encodedPassword, rawPassword, usernameSaltSource.getSalt( username ) );
    }

    @Override
    public boolean legacyOrCurrentMatches( String rawPassword, String encodedPassword, String username )
    {
        return legacyMatches( rawPassword, encodedPassword, username ) || super.matches( rawPassword, encodedPassword );
    }

    @Override
    public String getLegacyPasswordEncoderClassName()
    {
        return legacyPasswordEncoder.getClass().getName();
    }
}
