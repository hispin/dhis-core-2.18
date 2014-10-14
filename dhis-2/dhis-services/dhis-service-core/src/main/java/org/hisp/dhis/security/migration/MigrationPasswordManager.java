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
     * Determines whether encodedToken is a valid hash of token.
     * This method is a wrapper for passwordManager.matches() in order to support
     * authenticating tokens which were generated using the legacy hash implementation in addition
     * to the current hashing scheme.
     *
     * @param token the unencoded token as supplied from the user.
     * @param encodedToken the encoded token to match against.
     * @param username the username associated with the token (used for salting by the legacy password encoder).
     * @return true if the token matches for either the legacy or current hashing scheme, false otherwise.
     */
    public boolean tokenMatches( String token, String encodedToken, String username );

    /**
     * Return the class name of the legacy password encoder.
     * @return the name of the legacy password encoder class.
     */
    public String getLegacyPasswordEncoderClassName();
}
