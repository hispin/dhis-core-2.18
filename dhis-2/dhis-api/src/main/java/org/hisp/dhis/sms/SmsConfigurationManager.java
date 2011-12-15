package org.hisp.dhis.sms;

import org.hisp.dhis.sms.config.SmsConfiguration;

public interface SmsConfigurationManager
{

    public SmsConfiguration getSmsConfiguration();

    public void updateSmsConfiguration( SmsConfiguration config );

}