package org.hisp.dhis.security;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests assume (and enforce) the passwordManager to use non-static salts.
 * @author Halvdan Hoem Grelland
 */
public class PasswordManagerTest
    extends DhisSpringTest
{
    @Autowired
    private PasswordManager passwordManager;

    @Test
    public void testEncodeValidatePassword()
    {
        String password = "district";

        String encodedPassword1 = passwordManager.encodePassword( password );
        String encodedPassword2 = passwordManager.encodePassword( password );

        assertFalse( encodedPassword1.equals( encodedPassword2 ) );
        assertFalse( password.equals( encodedPassword1 ) );

        assertTrue( passwordManager.matches( password, encodedPassword1 ));
        assertTrue( passwordManager.matches( password, encodedPassword2 ));

        assertFalse( passwordManager.matches( password, "anotherPassword" ) );
    }
}
