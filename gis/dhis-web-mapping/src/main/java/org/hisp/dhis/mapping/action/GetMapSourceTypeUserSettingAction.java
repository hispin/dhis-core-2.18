package org.hisp.dhis.mapping.action;

import static org.hisp.dhis.mapping.MappingService.KEY_MAP_SOURCE_TYPE;
import static org.hisp.dhis.mapping.MappingService.MAP_SOURCE_TYPE_DATABASE;

import org.hisp.dhis.user.UserSettingService;

import com.opensymphony.xwork2.Action;

public class GetMapSourceTypeUserSettingAction
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
        object = (String) userSettingService.getUserSetting( KEY_MAP_SOURCE_TYPE, MAP_SOURCE_TYPE_DATABASE );
        
        return SUCCESS;
    }

}
