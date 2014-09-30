package org.hisp.dhis.security.migration;

import org.hisp.dhis.security.PasswordManager;

/**
 * Drop-in replacement for PasswordManager which provides access to legacy password hashing methods as
 * well as the currently used hashing methods. This is useful in implementing seamless migration to
 * a new and more secure password hashing method. In such a migration phase the system will need to
 * be able to accept login requests from users whose passwords are stored using legacy hash method
 * in order to re-hash and store the user password hash using the new (current) method (handled elsewhere).
 *
 * @author Halvdan Hoem Grelland
 */
public interface MigrationPasswordManager
    extends PasswordManager
{
    /**
     * Cryptographically hash a password using a legacy method.
     * Useful for access to the former (legacy) hash method when implementing migration to a new method.
     * @param password the password to encode.
     * @param username the username (used for seeding salt generation).
     * @return the encoded (hashed) password.
     */
    public String legacyEncodePassword( String password, String username );

    /**
     * Determines whether the supplied password equals the encoded password or not.
     * Uses the legacy hashing method to do so and is useful in implementing migration to a new method.
     * @param encodedPassword the encoded password.
     * @param password the password to match.
     * @param username the username (used for salt generation).
     * @return true if the password matches the encoded password, false otherwise.
     */
    public boolean legacyMatches( String encodedPassword, String password, String username );

    /**
     * Return the class name of the legacy password encoder.
     * @return the name of the legacy password encoder class.
     */
    public String getLegacyPasswordEncoderClassName();
}
