package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jim Grace
 */
@Transactional
public class DefaultDataApprovalLevelService
    implements DataApprovalLevelService
{
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

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private SecurityService securityService;

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    // -------------------------------------------------------------------------
    // DataApprovalLevel
    // -------------------------------------------------------------------------

    public DataApprovalLevel getDataApprovalLevel( int id )
    {
        return dataApprovalLevelStore.get( id );
    }

    public DataApprovalLevel getDataApprovalLevelByName( String name )
    {
        return dataApprovalLevelStore.getByName( name );
    }

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

    public List<DataApprovalLevel> getUserDataApprovalLevels()
    {
        List<DataApprovalLevel> userDataApprovalLevels = new ArrayList<DataApprovalLevel>();

        User user = currentUserService.getCurrentUser();

        boolean mayApprove = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE );
        boolean mayApproveAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        boolean mayAcceptAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS );

        if ( mayApprove || mayApproveAtLowerLevels || mayAcceptAtLowerLevels )
        {
            Set<Integer> userOrgUnitLevels = new HashSet<Integer>();

            for ( OrganisationUnit orgUnit : user.getOrganisationUnits() )
            {
                int orgUnitLevel = orgUnit.hasLevel() ?
                    orgUnit.getLevel() : organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getUid() );

                userOrgUnitLevels.add( orgUnitLevel );
            }

            boolean assignedAtLevel = false;
            boolean approvableAtLevel = false;
            boolean approvableAtAllLowerLevels = false;

            for ( DataApprovalLevel approvalLevel : getAllDataApprovalLevels() )
            {
                Boolean canReadThisLevel = ( securityService.canRead( approvalLevel ) &&
                    ( !approvalLevel.hasCategoryOptionGroupSet() || securityService.canRead( approvalLevel.getCategoryOptionGroupSet() ) ) );

                //
                // Test using assignedAtLevel and approvableAtLevel values from the previous (higher) level.
                //
                Boolean addBecauseOfPreviousLevel = false;

                if ( canReadThisLevel && ( approvableAtLevel // Approve at previous higher level implies unapprove at current level.
                    || ( assignedAtLevel && mayAcceptAtLowerLevels ) ) ) // Assigned at previous level and mayAcceptAtLowerLevels means may accept here.
                {
                    addBecauseOfPreviousLevel = true;
                }

                if ( assignedAtLevel && mayApproveAtLowerLevels )
                {
                    approvableAtAllLowerLevels = true;
                }

                //
                // Get new values of assignedAtLevel and approvableAtLevel for the current approval level.
                //
                assignedAtLevel = canReadThisLevel && userOrgUnitLevels.contains( approvalLevel.getOrgUnitLevel() );

                approvableAtLevel = canReadThisLevel && ( ( mayApprove && assignedAtLevel ) || approvableAtAllLowerLevels );

                //
                // Test using assignedAtLevel and approvableAtLevel values from the current level.
                //
                if ( approvableAtLevel || addBecauseOfPreviousLevel )
                {
                    userDataApprovalLevels.add( approvalLevel );
                }
            }
        }

        return userDataApprovalLevels;
    }
    
    public List<DataApprovalLevel> getDataApprovalLevelsByOrgUnitLevel( int orgUnitLevel )
    {
        return dataApprovalLevelStore.getDataApprovalLevelsByOrgUnitLevel( orgUnitLevel );
    }

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

    public void moveDataApprovalLevelDown( int level )
    {
        if ( canDataApprovalLevelMoveDown( level ) )
        {
            swapWithNextLevel( level );
        }
    }

    public void moveDataApprovalLevelUp( int level )
    {
        if ( canDataApprovalLevelMoveUp( level ) )
        {
            swapWithNextLevel( level - 1 );
        }
    }

    public boolean dataApprovalLevelExists( DataApprovalLevel level )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        for ( DataApprovalLevel dataApprovalLevel : dataApprovalLevels )
        {
            if ( level.getOrgUnitLevel() == dataApprovalLevel.getOrgUnitLevel()
                && level.getCategoryOptionGroupSet() == dataApprovalLevel.getCategoryOptionGroupSet() )
            {
                return true;
            }
        }

        return false;
    }

    public int addDataApprovalLevel( DataApprovalLevel newLevel )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        if ( newLevel.getOrgUnitLevel() <= 0 )
        {
            return -1;
        }

        int index = getInsertIndex( dataApprovalLevels, newLevel );

        if ( index < 0 )
        {
            return -1;
        }

        dataApprovalLevels.add( index, newLevel );

        // Move down from end to here, to avoid duplicate level in database.

        for ( int i = dataApprovalLevels.size() - 1; i > index; i-- )
        {
            update( dataApprovalLevels.get( i ), i );
        }

        newLevel.setLevel( index + 1 );
        newLevel.setCreated( new Date() );

        return dataApprovalLevelStore.save( newLevel );
    }
    
    public void deleteDataApprovalLevel( DataApprovalLevel dataApprovalLevel )
    {
        List<DataApprovalLevel> dataApprovalLevels = getAllDataApprovalLevels();

        int index = dataApprovalLevel.getLevel() - 1;
        
        if ( index >= 0 & index < dataApprovalLevels.size() )
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

    public Map<OrganisationUnit, Integer> getUserReadApprovalLevels()
    {
        Map<OrganisationUnit, Integer> map = new HashMap<OrganisationUnit, Integer>();

        User user = currentUserService.getCurrentUser();

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
                map.put( orgUnit, requiredApprovalLevel( orgUnit ) );
            }
        }

        Collection<OrganisationUnit> dataViewOrgUnits = user.getDataViewOrganisationUnits();

        if ( dataViewOrgUnits == null || dataViewOrgUnits.isEmpty() )
        {
            dataViewOrgUnits = organisationUnitService.getRootOrganisationUnits();
        }

        for ( OrganisationUnit orgUnit : dataViewOrgUnits )
        {
            if ( !map.containsKey( orgUnit ) )
            {
                map.put(orgUnit, requiredApprovalLevel( orgUnit ) );
            }
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

        dataApprovalLevel.setCreated( new Date() );

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
                if ( newLevel.getCategoryOptionGroupSet() == test.getCategoryOptionGroupSet() )
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
     * in order for the user to see the data -- if user is limited to seeing
     * approved data only from lower approval levels.
     *
     * @param orgUnit organisation unit to test.
     * @return required approval level for user to see the data.
     */
    private int requiredApprovalLevel( OrganisationUnit orgUnit )
    {
        int orgUnitLevel = orgUnit.getLevel() != 0 ?
                orgUnit.getLevel() :
                organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getUid() );

        int required = APPROVAL_LEVEL_UNAPPROVED;

        for ( DataApprovalLevel level : getAllDataApprovalLevels() )
        {
            if ( level.getOrgUnitLevel() >= orgUnitLevel
                    && securityService.canRead( level )
                    && ( level.getCategoryOptionGroupSet() == null || canReadSomeCategory( level.getCategoryOptionGroupSet() ) )
                    && level.getLevel() < getAllDataApprovalLevels().size() )
            {
                required = level.getLevel() + 1;
                break;
            }
        }

        return required;
    }

    /**
     * Can the user read at least one category from inside a category option
     * group set?
     *
     * @param cogs The category option group set to test
     * @return true if user can read at least one category option group.
     */
    private boolean canReadSomeCategory( CategoryOptionGroupSet cogs )
    {
        for ( CategoryOptionGroup cog : cogs.getMembers() )
        {
            if ( securityService.canRead(cog) )
            {
                return true;
            }
        }
        return false;
    }

}
