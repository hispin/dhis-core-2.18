package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Jim Grace
 */
@Transactional
public class DefaultDataApprovalLevelService
    implements DataApprovalLevelService
{
    private static final Log log = LogFactory.getLog( DefaultDataApprovalLevelService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalLevelStore dataApprovalLevelStore;

    public void setDataApprovalLevelStore( DataApprovalLevelStore dataApprovalLevelStore )
    {
        this.dataApprovalLevelStore = dataApprovalLevelStore;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private SecurityService securityService;

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    // -------------------------------------------------------------------------
    // DataApprovalLevel
    // -------------------------------------------------------------------------

    @Override
    public DataApprovalLevel getDataApprovalLevel( int id )
    {
        return dataApprovalLevelStore.get( id );
    }

    @Override
    public DataApprovalLevel getDataApprovalLevel( String uid )
    {
        return dataApprovalLevelStore.getByUid( uid );
    }

    @Override
    public DataApprovalLevel getDataApprovalLevelByName( String name )
    {
        return dataApprovalLevelStore.getByName( name );
    }

    @Override
    public DataApprovalLevel getDataApprovalLevelByLevelNumber( int levelNumber )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        if ( levelNumber < 1 || levelNumber > dataApprovalLevels.size() )
        {
            return null;
        }
        else
        {
            return dataApprovalLevels.get( levelNumber - 1 );
        }
    }

    @Override
    public DataApprovalLevel getHighestDataApprovalLevel( OrganisationUnit orgUnit )
    {
        int orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );

        DataApprovalLevel levelAbove = null;

        int levelAboveOrgUnitLevel = 0;

        List<DataApprovalLevel> userApprovalLevels = getUserDataApprovalLevels();
        
        for ( DataApprovalLevel level : userApprovalLevels )
        {
            log.debug( "Get highest data approval level: " + level.getName() );

            if ( level.getOrgUnitLevel() == orgUnitLevel )
            {
                return level; // Exact match on org unit level.
            }
            else if ( level.getOrgUnitLevel() > levelAboveOrgUnitLevel )
            {
                levelAbove = level; // Must be first matching approval level for this org unit level.

                levelAboveOrgUnitLevel = level.getOrgUnitLevel();
            }
        }

        return levelAbove; // Closest ancestor above, or null if none.
    }

    @Override
    public DataApprovalLevel getLowestDataApprovalLevel( OrganisationUnit orgUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        Set<CategoryOptionGroupSet> cogSets = null;

        if ( attributeOptionCombo != null && attributeOptionCombo != categoryService.getDefaultDataElementCategoryOptionCombo()  )
        {
            cogSets = new HashSet<>();

            for ( DataElementCategoryOption option : attributeOptionCombo.getCategoryOptions() )
            {
                if ( option.getGroupSets() != null )
                {
                    cogSets.addAll( option.getGroupSets() );
                }
            }
        }

        int orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );

        List<DataApprovalLevel> approvalLevels = getDataApprovalLevelsByOrgUnitLevel( orgUnitLevel );
        
        for ( DataApprovalLevel level : Lists.reverse( approvalLevels ) )
        {
            if ( level.getCategoryOptionGroupSet() == null )
            {
                if ( cogSets == null )
                {
                    return level;
                }
            }
            else if ( cogSets != null && cogSets.contains( level.getCategoryOptionGroupSet() ) )
            {
                return level;
            }
        }

        return null;
    }

    @Override
    public List<DataApprovalLevel> getAllDataApprovalLevels()
    {
        List<DataApprovalLevel> dataApprovalLevels = dataApprovalLevelStore.getAllDataApprovalLevels();

        for ( DataApprovalLevel dataApprovalLevel : dataApprovalLevels )
        {
            int ouLevelNumber = dataApprovalLevel.getOrgUnitLevel();

            OrganisationUnitLevel ouLevel = organisationUnitService.getOrganisationUnitLevelByLevel( ouLevelNumber );

            String ouLevelName = ouLevel != null ? ouLevel.getName() : "Organisation unit level " + ouLevelNumber;

            dataApprovalLevel.setOrgUnitLevelName( ouLevelName );
        }

        return dataApprovalLevels;
    }
    
    public Map<Integer, DataApprovalLevel> getDataApprovalLevelMap()
    {
        List<DataApprovalLevel> levels = dataApprovalLevelStore.getAllDataApprovalLevels();
        
        return Maps.uniqueIndex( levels, new Function<DataApprovalLevel, Integer>()
        {
            public Integer apply( DataApprovalLevel level )
            {
                return level.getLevel();
            }            
        } );
    }

    @Override
    public List<DataApprovalLevel> getUserDataApprovalLevels()
    {
        List<DataApprovalLevel> userDataApprovalLevels = new ArrayList<>();

        User user = currentUserService.getCurrentUser();

        boolean mayApprove = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE );
        boolean mayApproveAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        boolean mayAcceptAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS );

        if ( mayApprove || mayApproveAtLowerLevels || mayAcceptAtLowerLevels )
        {
            Set<Integer> userOrgUnitLevels = new HashSet<>();

            int lowestNumberOrgUnitLevel = 99999999;

            for ( OrganisationUnit orgUnit : user.getOrganisationUnits() )
            {
                int orgUnitLevel = orgUnit.hasLevel() ?
                    orgUnit.getLevel() : organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() );

                userOrgUnitLevels.add( orgUnitLevel );

                if ( orgUnitLevel < lowestNumberOrgUnitLevel )
                {
                    lowestNumberOrgUnitLevel = orgUnitLevel;
                }
            }

            boolean assignedAtLevel = false;
            boolean approvableAtLevel = false;
            boolean approvableAtAllLowerLevels = false;

            boolean canSeeAllDimensions = CollectionUtils.isEmpty( userService.getCoDimensionConstraints( user.getUserCredentials() ) )
                && CollectionUtils.isEmpty( userService.getCogDimensionConstraints( user.getUserCredentials() ) );

            List<DataApprovalLevel> approvalLevels = getAllDataApprovalLevels();
            
            for ( DataApprovalLevel approvalLevel : approvalLevels )
            {
                CategoryOptionGroupSet cogs = approvalLevel.getCategoryOptionGroupSet();

                Boolean canReadThisLevel = ( securityService.canRead( approvalLevel ) && (
                    ( cogs == null && canSeeAllDimensions ) ||
                    ( cogs != null && securityService.canRead( cogs ) && !CollectionUtils.isEmpty( categoryService.getCategoryOptionGroups( cogs ) ) ) ) );
                
                // Test using assignedAtLevel and approvableAtLevel values from the previous (higher) level.
                
                Boolean addBecauseOfPreviousLevel = canReadThisLevel && ( approvableAtLevel // Approve at previous higher level implies unapprove at current level.
                    || ( assignedAtLevel && mayAcceptAtLowerLevels ) ); // Assigned at previous level and mayAcceptAtLowerLevels means may accept here.

                if ( assignedAtLevel && mayApproveAtLowerLevels )
                {
                    approvableAtAllLowerLevels = true;
                }
                
                // Get new values of assignedAtLevel and approvableAtLevel for the current approval level.
                
                assignedAtLevel = canReadThisLevel && ( userOrgUnitLevels.contains( approvalLevel.getOrgUnitLevel() )
                        || ( ( mayApproveAtLowerLevels || mayAcceptAtLowerLevels ) && approvalLevel.getOrgUnitLevel() > lowestNumberOrgUnitLevel ) );

                approvableAtLevel = canReadThisLevel && ( ( mayApprove && assignedAtLevel ) || approvableAtAllLowerLevels );
                
                // Test using assignedAtLevel and approvableAtLevel values from the current level.
                
                if ( approvableAtLevel || addBecauseOfPreviousLevel )
                {
                    userDataApprovalLevels.add( approvalLevel );
                }
            }
        }

        return userDataApprovalLevels;
    }
    
    @Override
    public List<DataApprovalLevel> getDataApprovalLevelsByOrgUnitLevel( int orgUnitLevel )
    {
        return dataApprovalLevelStore.getDataApprovalLevelsByOrgUnitLevel( orgUnitLevel );
    }

    @Override
    public Set<OrganisationUnitLevel> getOrganisationUnitApprovalLevels()
    {
        Set<OrganisationUnitLevel> orgUnitLevels = new HashSet<>();
        
        List<DataApprovalLevel> dataApprovalLevels = dataApprovalLevelStore.getAllDataApprovalLevels();
        
        for ( DataApprovalLevel level : dataApprovalLevels )
        {
            OrganisationUnitLevel orgUnitLevel = organisationUnitService.getOrganisationUnitLevelByLevel( level.getOrgUnitLevel() );
            
            if ( orgUnitLevel != null )
            {
                orgUnitLevels.add( orgUnitLevel );
            }
        }
        
        return orgUnitLevels;
    }

    @Override
    public boolean canDataApprovalLevelMoveDown( int level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = level - 1;

        if ( index < 0 || index + 1 >= dataApprovalLevels.size() )
        {
            return false;
        }

        DataApprovalLevel test = dataApprovalLevels.get( index );
        DataApprovalLevel next = dataApprovalLevels.get( index + 1 );

        return test.getOrgUnitLevel() == next.getOrgUnitLevel()
            && test.getCategoryOptionGroupSet() != null;
    }

    @Override
    public boolean canDataApprovalLevelMoveUp( int level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = level - 1;

        if ( index <= 0 || index >= dataApprovalLevels.size() )
        {
            return false;
        }

        DataApprovalLevel test = dataApprovalLevels.get( index );
        DataApprovalLevel previous = dataApprovalLevels.get( index - 1 );

        return test.getOrgUnitLevel() == previous.getOrgUnitLevel()
            && previous.getCategoryOptionGroupSet() != null;
    }

    @Override
    public void moveDataApprovalLevelDown( int level )
    {
        if ( canDataApprovalLevelMoveDown( level ) )
        {
            swapWithNextLevel( level );
        }
    }

    @Override
    public void moveDataApprovalLevelUp( int level )
    {
        if ( canDataApprovalLevelMoveUp( level ) )
        {
            swapWithNextLevel( level - 1 );
        }
    }

    @Override
    public boolean dataApprovalLevelExists( DataApprovalLevel level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        for ( DataApprovalLevel dataApprovalLevel : dataApprovalLevels )
        {
            if ( level.levelEquals( dataApprovalLevel ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public int addDataApprovalLevel( DataApprovalLevel level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = getInsertIndex( dataApprovalLevels, level );

        if ( index < 0 )
        {
            return -1;
        }

        dataApprovalLevels.add( index, level );

        // Move down from end to here, to avoid duplicate level in database.

        for ( int i = dataApprovalLevels.size() - 1; i > index; i-- )
        {
            update( dataApprovalLevels.get( i ), i );
        }

        level.setLevel( index + 1 );

        return dataApprovalLevelStore.save( level );
    }

    @Override
    public int addDataApprovalLevel( DataApprovalLevel approvalLevel, int level )
    {
        approvalLevel.setLevel( level );
        
        return dataApprovalLevelStore.save( approvalLevel );        
    }
    
    @Override
    public void deleteDataApprovalLevel( DataApprovalLevel dataApprovalLevel )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = dataApprovalLevel.getLevel() - 1;
        
        if ( index >= 0 && index < dataApprovalLevels.size() )
        {
            dataApprovalLevelStore.delete( dataApprovalLevel );

            dataApprovalLevels.remove( index );

            // Move up from here to end, to avoid duplicate level in database.

            for ( int i = index; i < dataApprovalLevels.size(); i++ )
            {
                update( dataApprovalLevels.get( i ), i );
            }
        }
    }

    @Override
    public DataApprovalLevel getUserApprovalLevel( OrganisationUnit orgUnit )
    {
        User user = currentUserService.getCurrentUser();
        
        return getUserApprovalLevel( user, orgUnit );
    }
    
    @Override
    public DataApprovalLevel getUserApprovalLevel( User user, OrganisationUnit orgUnit )
    {
        if ( user == null || orgUnit == null )
        {
            return null;
        }
        
        List<DataApprovalLevel> approvalLevels = getAllDataApprovalLevels();
        
        OrganisationUnit organisationUnit = null;
        
        for ( OrganisationUnit unit : user.getOrganisationUnits() )
        {
            if ( orgUnit.isDescendant( unit ) )
            {
                organisationUnit = unit;
                break;
            }
        }

        return organisationUnit != null ? getUserApprovalLevel( organisationUnit, user, approvalLevels ) : null;
    }

    @Override
    public Map<OrganisationUnit, Integer> getUserReadApprovalLevels()
    {
        Map<OrganisationUnit, Integer> map = new HashMap<>();

        User user = currentUserService.getCurrentUser();

        List<DataApprovalLevel> approvalLevels = getAllDataApprovalLevels();
        
        // ---------------------------------------------------------------------
        // Add user organisation units if authorized to approve at lower levels
        // ---------------------------------------------------------------------

        if ( user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS ) )
        {
            for ( OrganisationUnit orgUnit : user.getOrganisationUnits() )
            {
                map.put( orgUnit, APPROVAL_LEVEL_UNAPPROVED );
            }
        }
        else
        {
            for ( OrganisationUnit orgUnit : user.getOrganisationUnits() )
            {
                map.put( orgUnit, requiredApprovalLevel( orgUnit, user, approvalLevels ) );
            }
        }

        // ---------------------------------------------------------------------
        // Add data view organisation units with approval levels
        // ---------------------------------------------------------------------

        Collection<OrganisationUnit> dataViewOrgUnits = user.getDataViewOrganisationUnits();

        if ( dataViewOrgUnits == null || dataViewOrgUnits.isEmpty() )
        {
            dataViewOrgUnits = organisationUnitService.getRootOrganisationUnits();
        }

        for ( OrganisationUnit orgUnit : dataViewOrgUnits )
        {
            if ( !map.containsKey( orgUnit ) )
            {
                map.put( orgUnit, requiredApprovalLevel( orgUnit, user, approvalLevels ) );
            }
        }

        return map;
    }
    
    @Override
    public Map<OrganisationUnit, Integer> getUserReadApprovalLevels( DataApprovalLevel approvalLevel )
    {
        Map<OrganisationUnit, Integer> map = new HashMap<>();
        
        User user = currentUserService.getCurrentUser();

        Collection<OrganisationUnit> orgUnits = user.getDataViewOrganisationUnits();

        if ( orgUnits == null || orgUnits.isEmpty() )
        {
            orgUnits = organisationUnitService.getRootOrganisationUnits();
        }

        for ( OrganisationUnit orgUnit : orgUnits )
        {
            map.put( orgUnit, approvalLevel.getLevel() );
        }

        return map;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Swaps a data approval level with the next higher level.
     *
     * @param level lower level to swap.
     */
    private void swapWithNextLevel( int level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = level - 1;

        DataApprovalLevel d2 = dataApprovalLevels.get( index );
        DataApprovalLevel d1  = dataApprovalLevels.get( index + 1 );

        dataApprovalLevels.set( index, d1 );
        dataApprovalLevels.set( index + 1, d2 );

        update( d1, index );
        update( d2, index + 1 );
    }

    /**
     * Updates a data approval level object by setting the level to
     * correspond with the list index, setting the updated date to now,
     * and updating the object on disk.
     *
     * @param dataApprovalLevel data approval level to update
     * @param index index of the object (used to set the level.)
     */
    private void update( DataApprovalLevel dataApprovalLevel, int index )
    {
        dataApprovalLevel.setLevel( index + 1 );

        dataApprovalLevelStore.update( dataApprovalLevel );
    }

    /**
     * Finds the right index at which to insert a new data approval level.
     * Returns -1 if the new data approval level is a duplicate.
     *
     * @param dataApprovalLevels list of all levels.
     * @param newLevel new level to find the insertion point for.
     * @return index where the new approval level should be inserted,
     * or -1 if the new level is a duplicate.
     */
    private int getInsertIndex( List<DataApprovalLevel> dataApprovalLevels, DataApprovalLevel newLevel )
    {
        int i = dataApprovalLevels.size() - 1;

        while ( i >= 0 )
        {
            DataApprovalLevel test = dataApprovalLevels.get( i );

            int orgLevelDifference = newLevel.getOrgUnitLevel() - test.getOrgUnitLevel();

            if ( orgLevelDifference > 0 )
            {
                break;
            }

            if ( orgLevelDifference == 0 )
            {
                if ( newLevel.levelEquals( test ) )
                {
                    return -1;
                }

                if ( test.getCategoryOptionGroupSet() == null )
                {
                    break;
                }
            }

            i--;
        }
        
        return i + 1;
    }

    /**
     * Get the approval level for an organisation unit that is required
     * in order for the user to see the data, assuming user is limited to seeing
     * approved data only from lower approval levels.
     *
     * @param orgUnit organisation unit to test.
     * @param user the user.
     * @param approvalLevels all data approval levels.
     * @return required approval level for user to see the data.
     */
    private int requiredApprovalLevel( OrganisationUnit orgUnit, User user, List<DataApprovalLevel> approvalLevels )
    {
        DataApprovalLevel userLevel = getUserApprovalLevel( orgUnit, user, approvalLevels );

        int totalLevels = approvalLevels.size();
        
        return userLevel == null ? 0 : 
            userLevel.getLevel() == totalLevels ? APPROVAL_LEVEL_UNAPPROVED :
            userLevel.getLevel() + 1;
    }

    /**
     * Get the approval level for a user for a given organisation unit. It is
     * assumed that the user has access to the organisation unit (must be
     * checked elsewhere, it is not checked here.) If the organisation unit is
     * above all approval levels, returns null (no approval levels apply.)
     * <p>
     * If users are restricted to viewing approved data only, users may
     * see data from lower levels *only* if it is approved *below* this approval
     * level (higher number approval level). Or, if this method returns the
     * lowest (highest number) approval level, users may see unapproved data.
     * <p>
     * If users have approve/unapprove authority (checked elsewhere, not here),
     * the returned level is the level at which users may approve/unapprove.
     * If users have authority to approve at lower levels, they may approve
     * at levels below the returned level.
     * <p>
     * If users have accept/unaccept authority (checked elsewhere, not here),
     * users may accept/unaccept at the level just *below* this level.
     *
     * @param orgUnit organisation unit to test.
     * @param user the user.
     * @param approvalLevels app data approval levels.
     * @return approval level for user.
     */
    private DataApprovalLevel getUserApprovalLevel( OrganisationUnit orgUnit, User user, List<DataApprovalLevel> approvalLevels )
    {
        int userOrgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );

        DataApprovalLevel userLevel = null;

        for ( DataApprovalLevel level : approvalLevels )
        {
            if ( level.getOrgUnitLevel() >= userOrgUnitLevel &&
                securityService.canRead( level ) &&
                canReadCOGS( user, level.getCategoryOptionGroupSet() ) )
            {
                userLevel = level;
                break;
            }
        }

        return userLevel;
    }

    /**
     * Can the user read from this CategoryOptionGroupSet (COGS)?
     * <p>
     * If the COGS is null, then the user must have no dimension constraints.
     * (In other words, the user must be able to read across all category
     * option groups.)
     * <p>
     * If the COGS is not null, then the user must be able to read at least
     * one category option group from the category option group set.
     *
     * @param cogs The category option group set to test
     * @return true if user can read at least one category option group.
     */
    private boolean canReadCOGS( User user, CategoryOptionGroupSet cogs )
    {
        if ( cogs == null )
        {
            UserCredentials userCredentials = user.getUserCredentials();

            return CollectionUtils.isEmpty( userCredentials.getCogsDimensionConstraints() )
                && CollectionUtils.isEmpty( userCredentials.getCatDimensionConstraints() );
        }

        return !CollectionUtils.isEmpty( categoryService.getCategoryOptionGroups( cogs ) );
    }
}
