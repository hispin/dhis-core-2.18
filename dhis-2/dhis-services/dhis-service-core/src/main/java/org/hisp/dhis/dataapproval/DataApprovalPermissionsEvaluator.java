package org.hisp.dhis.dataapproval;

import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import static org.hisp.dhis.setting.SystemSettingManager.KEY_ACCEPTANCE_REQUIRED_FOR_APPROVAL;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_HIDE_UNAPPROVED_DATA_IN_ANALYTICS;

/**
 * This package private class holds the context for deciding on data approval permissions.
 * The context contains both system settings and some qualities of the user.
 * <p>
 * This class is especially efficient if the settings are set once and
 * then used several times to generate ApprovalPermissions for different
 * DataApproval objects.
 *
 * @author Jim Grace
 */
class DataApprovalPermissionsEvaluator
{
    DataApprovalLevelService dataApprovalLevelService;

    User user;

    private boolean acceptanceRequiredForApproval;
    private boolean hideUnapprovedData;

    private boolean authorizedToApprove;
    private boolean authorizedToApproveAtLowerLevels;
    private boolean authorizedToAcceptAtLowerLevels;
    private boolean authorizedToViewUnapprovedData;

    int maxApprovalLevel;

    private DataApprovalPermissionsEvaluator()
    {
    }

    /**
     * Allocates and populates the context for determining user permissions
     * on one or more DataApproval objects.
     *
     * @param currentUserService Current user service
     * @param systemSettingManager System setting manager
     * @param dataApprovalLevelService Data approval level service
     * @return context for determining user permissions
     */
    static DataApprovalPermissionsEvaluator makePermissionsEvaluator( CurrentUserService currentUserService,
            SystemSettingManager systemSettingManager, DataApprovalLevelService dataApprovalLevelService )
    {
        DataApprovalPermissionsEvaluator ev = new DataApprovalPermissionsEvaluator();

        ev.dataApprovalLevelService = dataApprovalLevelService;

        ev.user = currentUserService.getCurrentUser();

        ev.acceptanceRequiredForApproval = (Boolean) systemSettingManager.getSystemSetting( KEY_ACCEPTANCE_REQUIRED_FOR_APPROVAL, false );
        ev.hideUnapprovedData = (Boolean) systemSettingManager.getSystemSetting( KEY_HIDE_UNAPPROVED_DATA_IN_ANALYTICS, false );

        ev.authorizedToApprove = ev.user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE );
        ev.authorizedToApproveAtLowerLevels = ev.user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        ev.authorizedToAcceptAtLowerLevels = ev.user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS );
        ev.authorizedToViewUnapprovedData = ev.user.getUserCredentials().isAuthorized( DataApproval.AUTH_VIEW_UNAPPROVED_DATA );

        ev.maxApprovalLevel = dataApprovalLevelService.getAllDataApprovalLevels().size();

        tracePrint( "makePermissionsEvaluator acceptanceRequiredForApproval " + ev.acceptanceRequiredForApproval
                + " hideUnapprovedData " + ev.hideUnapprovedData + " authorizedToApprove " + ev.authorizedToApprove
                + " authorizedToAcceptAtLowerLevels " + ev.authorizedToAcceptAtLowerLevels
                + " authorizedToViewUnapprovedData " + ev.authorizedToViewUnapprovedData + " maxApprovalLevel " + ev.maxApprovalLevel );

        return ev;
    }

    /**
     * Allocates and fills a data approval permissions object according to
     * the context of system settings and user information.
     * <p>
     * If there is a data permissions state, also takes this into account.
     *
     * @param da the data approval object to evaluate
     * @param status the data approval status (if any)
     * @return the data approval permissions for the object
     */
    DataApprovalPermissions getPermissions( DataApproval da, DataApprovalStatus status )
    {
        DataApprovalPermissions permissions = new DataApprovalPermissions();

        if ( da == null || da.getOrganisationUnit() == null )
        {
            return permissions; // No approval object or no org unit -> no permissions.
        }

        DataApprovalLevel userApprovalLevel = dataApprovalLevelService.getUserApprovalLevel( user, da.getOrganisationUnit(), false );

        if ( userApprovalLevel == null )
        {
            return permissions; // Can't find user approval level, so no permissions are true.
        }

        boolean isApproved = ( da.getDataApprovalLevel() != null );
        int userLevel = userApprovalLevel.getLevel();
        int dataLevel = ( isApproved ? da.getDataApprovalLevel().getLevel() : maxApprovalLevel );
        boolean isApprovable = true; // Unless the state tells us otherwise
        boolean isAccepted = da.isAccepted();

        if ( status != null && status.getState() != null )
        {
            DataApprovalState state = status.getState();

            isApproved = state.isApproved() && state.isUnapprovable(); // Maybe approved, but not here.
            isApprovable = state.isApprovable();
            isAccepted = state.isAccepted();
        }

        boolean mayApproveOrUnapprove = ( authorizedToApprove && userLevel == dataLevel && !da.isAccepted() ) ||
                        ( authorizedToApproveAtLowerLevels && userLevel < dataLevel );

        boolean mayApproveFromLowerLevel = ( userLevel + 1 == dataLevel && isApproved ) && acceptanceRequiredForApproval;

        boolean mayApprove = isApprovable && ( !isApproved || userLevel < dataLevel )
                && ( mayApproveOrUnapprove || mayApproveFromLowerLevel );

        boolean mayAcceptOrUnaccept = authorizedToAcceptAtLowerLevels && isApproved &&
                ( userLevel == dataLevel - 1 || ( userLevel < dataLevel && authorizedToApproveAtLowerLevels ) );

        boolean mayUnapprove = isApproved && ( ( mayApproveOrUnapprove && !da.isAccepted() ) || mayAcceptOrUnaccept );

        boolean mayReadData = authorizedToViewUnapprovedData || !hideUnapprovedData || mayApprove
                || userLevel >= dataLevel;

        tracePrint( "getPermissions orgUnit " + ( da.getOrganisationUnit() == null ? "(null)" : da.getOrganisationUnit().getName() )
                + " combo " + da.getAttributeOptionCombo().getName()
                + " state " + ( status == null || status.getState() == null ? "(null)" : status.getState().name() )
                + " isApproved " + isApproved + " isAccepted " + isAccepted + " userLevel " + userLevel + " dataLevel " + dataLevel
                + " mayApproveOrUnapprove " + mayApproveOrUnapprove + " mayApprove " + mayApprove + " mayUnapprove " + mayUnapprove
                + " mayAcceptOrUnaccept " + mayAcceptOrUnaccept + " mayReadData " + mayReadData );

        permissions.setMayApprove( mayApprove );
        permissions.setMayUnapprove( mayUnapprove );
        permissions.setMayAccept( mayAcceptOrUnaccept && !isAccepted );
        permissions.setMayUnaccept( mayAcceptOrUnaccept && isAccepted );
        permissions.setMayReadData( mayReadData );

        return permissions;
    }

    private static void tracePrint( String s )
    {
//        System.out.println( s );
    }
}
