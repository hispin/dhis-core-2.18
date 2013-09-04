package org.hisp.dhis.startup;

import java.util.UUID;

import org.hisp.dhis.configuration.Configuration;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigurationPopulator
    extends AbstractStartupRoutine
{
    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void execute()
        throws Exception
    {
        Configuration config = configurationService.getConfiguration();
        
        if ( config != null && config.getSystemId() == null )
        {
            config.setSystemId( UUID.randomUUID().toString() );
            configurationService.setConfiguration( config );
        }
    }
}
