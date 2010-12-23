package org.hisp.dhis.user.action;

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.user.UserGroupService;

import com.opensymphony.xwork2.Action;

public class RemoveUserGroupAction
    implements Action
{

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
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
    // Output
    // -------------------------------------------------------------------------

  

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        System.out.println("\n\n ===== \n userGroupService : " + userGroupService);
        System.out.println("\n\n userGroupId : " + id);
        System.out.println("\n\n userGroupService.getUserGroup( userGroupId ) : " + userGroupService.getUserGroup( id ));
        try
        {
            userGroupService.deleteUserGroup( userGroupService.getUserGroup( id ) );
        }
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getClassName();

                return ERROR;
            }
        }

        return SUCCESS;
    }
}
