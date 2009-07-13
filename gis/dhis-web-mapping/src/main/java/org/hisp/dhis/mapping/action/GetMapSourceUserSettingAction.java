package org.hisp.dhis.mapping.action;

import static org.hisp.dhis.mapping.MappingService.KEY_MAP_SOURCE;
import static org.hisp.dhis.mapping.MappingService.MAP_SOURCE_DATABASE;

import org.hisp.dhis.user.UserSettingService;

import com.opensymphony.xwork.Action;

public class GetMapSourceUserSettingAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private String object;

    public String getObject()
    {
        return object;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        object = (String) userSettingService.getUserSetting( KEY_MAP_SOURCE, MAP_SOURCE_DATABASE );
        
        return SUCCESS;
    }

}
