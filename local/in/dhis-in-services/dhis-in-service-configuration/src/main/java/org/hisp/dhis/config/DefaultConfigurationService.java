package org.hisp.dhis.config;


public class DefaultConfigurationService
    implements ConfigurationService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ConfigurationStore configurationStore;

    public void setConfigurationStore( ConfigurationStore configurationStore )
    {
        this.configurationStore = configurationStore;
    }
    
    // -------------------------------------------------------------------------
    // Config
    // -------------------------------------------------------------------------

    public int addConfiguration( Configuration_IN con )
    {        
        
        return configurationStore.addConfiguration( con );    
    }
    
    public void updateConfiguration( Configuration_IN con )
    {
        configurationStore.updateConfiguration( con );
    }

    public void deleteConfiguration( Configuration_IN con )
    {
        configurationStore.deleteConfiguration( con );
    }
    
    public Configuration_IN getConfiguration( int id )
    {
        return configurationStore.getConfiguration( id );       
    }
    
    public Configuration_IN getConfigurationByKey( String ckey )
    {        
        return configurationStore.getConfigurationByKey( ckey );
    }

}
