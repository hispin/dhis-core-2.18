package org.hisp.dhis.analytics.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AnalyticsSecurityManager;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.dataapproval.DataApprovalLevelService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultAnalyticsSecurityManager
    implements AnalyticsSecurityManager
{
    private static final Log log = LogFactory.getLog( DefaultAnalyticsSecurityManager.class );
    
    @Autowired
    private CurrentUserService currentUserService;
    
    @Autowired
    private DataApprovalLevelService approvalLevelService;
    
    @Autowired
    private SystemSettingManager systemSettingManager;
    
    @Autowired
    private DimensionService dimensionService;

    // -------------------------------------------------------------------------
    // AnalyticsSecurityManager implementation
    // -------------------------------------------------------------------------

    public void decideAccess( DataQueryParams params )
    {
        // ---------------------------------------------------------------------
        // Check current user data view access to org units
        // ---------------------------------------------------------------------
        
        User user = currentUserService.getCurrentUser();
        
        List<NameableObject> queryOrgUnits = params.getDimensionOrFilter( DimensionalObject.ORGUNIT_DIM_ID );
        
        if ( queryOrgUnits == null || user == null || !user.hasDataViewOrganisationUnit() )
        {
            return;
        }
        
        Set<OrganisationUnit> viewOrgUnits = user.getDataViewOrganisationUnits();
        
        for ( NameableObject object : queryOrgUnits )
        {
            OrganisationUnit queryOrgUnit = (OrganisationUnit) object;
            
            if ( !queryOrgUnit.isEqualOrChildOf( viewOrgUnits ) )
            {
                throw new IllegalQueryException( "Org unit is not viewable for current user: " + queryOrgUnit.getUid() );
            }
        }
    }
    
    public void applyDataApprovalConstraints( DataQueryParams params )
    {
        boolean approval = (Boolean) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_HIDE_UNAPPROVED_DATA_IN_ANALYTICS, false );

        User user = currentUserService.getCurrentUser();
        
        if ( approval && user != null )
        {
            Map<OrganisationUnit, Integer> approvalLevels = approvalLevelService.getUserReadApprovalLevels();
            
            if ( approvalLevels != null && !approvalLevels.isEmpty() )
            {
                params.setApprovalLevels( approvalLevels );
            
                log.info( "User: " + user.getUsername() + " constrained by data approval levels: " + approvalLevels.values() );
            }
        }
    }
    
    public void applyDimensionConstraints( DataQueryParams params )
    {
        applyOrganisationUnitConstraint( params );
        applyUserConstraints( params );
    }

    private void applyOrganisationUnitConstraint( DataQueryParams params )
    {
        User user = currentUserService.getCurrentUser();

        // ---------------------------------------------------------------------
        // Check if current user has data view organisation units
        // ---------------------------------------------------------------------

        if ( params == null || user == null || !user.hasDataViewOrganisationUnit() )
        {
            return;
        }

        // ---------------------------------------------------------------------
        // Check if request already has organisation units specified
        // ---------------------------------------------------------------------

        if ( params.hasDimensionOrFilterWithItems( DimensionalObject.ORGUNIT_DIM_ID ) )
        {
            return;
        }
        
        // -----------------------------------------------------------------
        // Apply constraint as filter, and remove potential all-dimension
        // -----------------------------------------------------------------

        params.removeDimensionOrFilter( DimensionalObject.ORGUNIT_DIM_ID );

        List<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>( user.getDataViewOrganisationUnits() );

        DimensionalObject constraint = new BaseDimensionalObject( DimensionalObject.ORGUNIT_DIM_ID, DimensionType.ORGANISATIONUNIT, orgUnits );
        
        params.getFilters().add( constraint );

        log.info( "User: " + user.getUsername() + " constrained by data view organisation units" );        
    }
    
    private void applyUserConstraints( DataQueryParams params )
    {
        User user = currentUserService.getCurrentUser();

        // ---------------------------------------------------------------------
        // Check if current user has dimension constraints
        // ---------------------------------------------------------------------

        if ( params == null || user == null || user.getUserCredentials() == null || !user.getUserCredentials().hasDimensionConstraints() )
        {
            return;
        }
                
        Set<DimensionalObject> dimensionConstraints = user.getUserCredentials().getDimensionConstraints();
        
        for ( DimensionalObject dimension : dimensionConstraints )
        {
            // -----------------------------------------------------------------
            // Check if constraint already is specified with items
            // -----------------------------------------------------------------

            if ( params.hasDimensionOrFilterWithItems( dimension.getUid() ) )
            {
                continue;
            }

            List<NameableObject> canReadItems = dimensionService.getCanReadDimensionItems( dimension.getDimension() );

            // -----------------------------------------------------------------
            // Check if current user has access to any items from constraint
            // -----------------------------------------------------------------

            if ( canReadItems == null || canReadItems.isEmpty() )
            {
                throw new IllegalQueryException( "Current user is constrained by a dimension but has access to no associated dimension items: " + dimension.getDimension() );
            }

            // -----------------------------------------------------------------
            // Apply constraint as filter, and remove potential all-dimension
            // -----------------------------------------------------------------

            params.removeDimensionOrFilter( dimension.getDimension() );
            
            DimensionalObject constraint = new BaseDimensionalObject( dimension.getDimension(), 
                dimension.getDimensionType(), null, dimension.getDisplayName(), canReadItems );
            
            params.getFilters().add( constraint );

            log.info( "User: " + user.getUsername() + " constrained by dimension: " + constraint.getDimension() );
        }        
    }
}
