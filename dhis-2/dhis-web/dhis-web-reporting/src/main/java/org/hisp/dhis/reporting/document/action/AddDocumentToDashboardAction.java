package org.hisp.dhis.reporting.document.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dashboard.DashboardContent;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork.Action;

public class AddDocumentToDashboardAction
    implements Action
{
    private static final Log log = LogFactory.getLog( AddDocumentToDashboardAction.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DashboardService dashboardService;

    public void setDashboardService( DashboardService dashboardService )
    {
        this.dashboardService = dashboardService;
    }
    
    private DocumentService documentService;

    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
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
            DashboardContent content = dashboardService.getDashboardContent( user );
        
            Document document = documentService.getDocument( id );
            
            content.addDocument( document );
            
            dashboardService.saveDashboardContent( content );
            
            log.info( "Added document '" + document.getName() + "' to dashboard for user '" + user.getName() + "'" );
        }
        else
        {
            log.warn( "Could not add report to dashboard, no current user" );
        }
        
        return SUCCESS;
    }
}
