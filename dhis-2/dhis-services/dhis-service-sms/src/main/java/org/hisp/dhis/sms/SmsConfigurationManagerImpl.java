package org.hisp.dhis.sms;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.sms.SmsConfigurationManager;
import org.hisp.dhis.sms.config.SmsConfigurable;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Manages the {@link SmsConfiguration} for the DHIS instance.
 * <p>
 * The manager looks up all beans implementing {@link SmsConfigurable} in the
 * context, initializing them on startup and on any sms configuration changes.
 * 
 */
public class SmsConfigurationManagerImpl implements SmsConfigurationManager
{

    private static final Log log = LogFactory.getLog( SmsConfigurationManagerImpl.class );

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired( required = false )
    private List<SmsConfigurable> smsConfigurables;

    @PostConstruct
    public void initializeSmsConfigurables()
    {
        if ( smsConfigurables == null )
        {
            return;
        }

        SmsConfiguration smsConfiguration = getSmsConfiguration();

        if ( smsConfiguration == null )
        {
            return;
        }
        
        for ( SmsConfigurable smsConfigurable : smsConfigurables )
        {

            try
            {
                smsConfigurable.initialize( smsConfiguration );
                log.debug( "Initialized " + smsConfigurable);
            }
            catch ( Throwable t )
            {
                // TODO: Need to make these problems available in GUI!
                log.warn( "Unable to initialize service " + smsConfigurable.getClass().getSimpleName()
                    + "with configuration " + smsConfiguration, t );
            }
        }

    }

    @Override
    public SmsConfiguration getSmsConfiguration()
    {
        return (SmsConfiguration) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_SMS_CONFIG );
    }

    @Override
    public void updateSmsConfiguration( SmsConfiguration config )
    {
        systemSettingManager.saveSystemSetting( SystemSettingManager.KEY_SMS_CONFIG, config );

        // Reinitialize components relying on sms config.
        initializeSmsConfigurables();
    }
}
