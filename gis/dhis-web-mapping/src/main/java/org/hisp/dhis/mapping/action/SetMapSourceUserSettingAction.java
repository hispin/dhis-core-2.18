package org.hisp.dhis.mapping.action;

import static org.hisp.dhis.mapping.MappingService.KEY_MAP_SOURCE;

import org.hisp.dhis.user.UserSettingService;

import com.opensymphony.xwork.Action;

public class SetMapSourceUserSettingAction
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

    private String mapSource;

    public void setMapSource( String mapSource )
    {
        this.mapSource = mapSource;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        userSettingService.saveUserSetting( KEY_MAP_SOURCE, mapSource );
        
        return SUCCESS;
    }
}
