package org.hisp.dhis.mapping.action;

import static org.hisp.dhis.mapping.MappingService.KEY_MAP_SOURCE_TYPE;

import org.hisp.dhis.user.UserSettingService;

import com.opensymphony.xwork2.Action;

public class SetMapSourceTypeUserSettingAction
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
    // Input
    // -------------------------------------------------------------------------

    private String mapSourceType;

    public void setMapSourceType( String mapSourceType )
    {
        this.mapSourceType = mapSourceType;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        userSettingService.saveUserSetting( KEY_MAP_SOURCE_TYPE, mapSourceType );
        
        return SUCCESS;
    }
}
