package org.hisp.dhis.dashboard.action;

import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

import com.opensymphony.xwork.Action;

public class RemoveMapViewAction
    implements Action
{
 // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private UserStore userStore;
    
    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }
    
    private MappingService mappingService;

    public void setMappingService( MappingService mappingService )
    {
        this.mappingService = mappingService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        User user = currentUserService.getCurrentUser();
        
        if ( user != null )
        {
            UserCredentials credentials = userStore.getUserCredentials( user );
            
            MapView mapView = mappingService.getMapView( id );
            
            if ( credentials.getDashboardMapViews().remove( mapView ) )
            {
                userStore.updateUserCredentials( credentials );
            }
        }
        
        return SUCCESS;
    }
}
