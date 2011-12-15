package org.hisp.dhis.sms.config;

import org.hisp.dhis.sms.SmsServiceException;

/**
 * Interface for any service requiring an {@link SmsConfiguration}.
 */
public interface SmsConfigurable
{

    /**
     * Initialize the service with the provided configuration.
     * <p>
     * Services implementing this interface are also expected to be able to
     * reinitialize based on these setting in a safe way when running.
     * 
     * @param smsConfiguration The SMS configuration
     * @throws SmsServiceException if the service cannot be initialized with the
     *         provided {@link SmsConfiguration}
     */
    public void initialize( SmsConfiguration smsConfiguration )
        throws SmsServiceException;

}
