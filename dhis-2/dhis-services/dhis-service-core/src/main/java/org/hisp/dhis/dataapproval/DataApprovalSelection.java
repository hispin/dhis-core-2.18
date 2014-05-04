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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.springframework.util.CollectionUtils;

/**
 * This package-private class is used by the data approval service to
 * describe selected data from a data set, such as could appear in a data set
 * report or data approval report, to determine its data approval status.
 * <p>
 * The entire reason for this class is to make the code more readable.
 * The use of instance variables greatly reduces the need to pass parameters
 * between methods.
 *
 * @author Jim Grace
 */
class DataApprovalSelection
{
    private final static Log log = LogFactory.getLog( DataApprovalSelection.class );

    private final static int INDEX_NOT_FOUND = -1;

    // -------------------------------------------------------------------------
    // Data selection parameters
    // -------------------------------------------------------------------------

    private DataSet dataSet;

    private Period period;

    private OrganisationUnit organisationUnit;

    private Set<CategoryOptionGroup> categoryOptionGroups;

    private Set<DataElementCategoryOption> dataElementCategoryOptions;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalStore dataApprovalStore;

    private DataApprovalLevelService dataApprovalLevelService;

    private OrganisationUnitService organisationUnitService;

    private DataElementCategoryService categoryService;

    private PeriodService periodService;

    // -------------------------------------------------------------------------
    // Internal instance variables
    // -------------------------------------------------------------------------

    private int organisationUnitLevel;

    private Map<CategoryOptionGroupSet, Set<CategoryOptionGroup>> selectionGroups = null;

    private List<DataApprovalLevel> allApprovalLevels;

    private List<Set<CategoryOptionGroup>> categoryOptionGroupsByLevel;

    int thisIndex;

    int thisOrHigherIndex;

    int lowerIndex;

    boolean dataSetAssignedAtOrBelowLevel = false;

    private DataApprovalState state = null;

    private DataApproval dataApproval = null;

    private DataApprovalLevel dataApprovalLevel = null;

    private int foundThisOrHigherIndex;

    // -------------------------------------------------------------------------
    // Preconstructed Status object
    // -------------------------------------------------------------------------

    private static final DataApprovalStatus STATUS_UNAPPROVABLE = new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null, null);

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    DataApprovalSelection( DataSet dataSet,
        Period period,
        OrganisationUnit organisationUnit,
        Set<CategoryOptionGroup> categoryOptionGroups,
        Set<DataElementCategoryOption> dataElementCategoryOptions,
        DataApprovalStore dataApprovalStore,
        DataApprovalLevelService dataApprovalLevelService,
        OrganisationUnitService organisationUnitService,
        DataElementCategoryService categoryService,
        PeriodService periodService )
    {
        this.dataSet = dataSet;
        this.period = period;
        this.organisationUnit = organisationUnit;
        this.categoryOptionGroups = categoryOptionGroups;
        this.dataElementCategoryOptions = dataElementCategoryOptions;
        this.dataApprovalStore = dataApprovalStore;
        this.dataApprovalLevelService = dataApprovalLevelService;
        this.categoryService = categoryService;
        this.organisationUnitService = organisationUnitService;
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Package-private method
    // -------------------------------------------------------------------------

    DataApprovalStatus getDataApprovalStatus()
    {
        organisationUnitLevel = organisationUnit.getLevel() != 0 ?
            organisationUnit.getLevel() :
            organisationUnitService.getLevelOfOrganisationUnit( organisationUnit.getId() );

        log.debug( logSelection() + " starting." );

        if ( !dataSet.isApproveData() )
        {
            log.debug( logSelection() + " returning UNAPPROVABLE (dataSet not marked for approval)" );

            return STATUS_UNAPPROVABLE;
        }

        findCategoryOptionGroupsByLevel();

        findThisLevel();

        if ( lowerIndex == 0 )
        {
            log.debug( logSelection() + " returning UNAPPROVABLE because org unit is above all approval levels" );

            return STATUS_UNAPPROVABLE;
        }

        if ( !period.getPeriodType().equals( dataSet.getPeriodType() ) )
        {
            if ( period.getPeriodType().getFrequencyOrder() > dataSet.getPeriodType().getFrequencyOrder() )
            {
                findStatusForLongerPeriodType();
            }
            else
            {
                log.debug( logSelection() + " returning UNAPPROVABLE (period type too short)" );

                return STATUS_UNAPPROVABLE;
            }
        }
        else
        {
            state = getState();
        }

        DataApprovalStatus status = new DataApprovalStatus( state, dataApproval, dataApprovalLevel );

        log.debug( logSelection() + " returning " + state.name() );

        return status;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Formats data selection parameters for getDataApprovalStatus() tracing.
     *
     * @return data selection parameters as a string.
     */
    private String logSelection()
    {
        String categoryOptionGroupsString = "";
        String categoryOptionsString = "";

        if ( categoryOptionGroups != null )
        {
            for ( CategoryOptionGroup group : categoryOptionGroups )
            {
                categoryOptionGroupsString += ( categoryOptionGroupsString.isEmpty() ? "" : ", " ) + group.getName();
            }
        }

        if ( dataElementCategoryOptions != null )
        {
            for ( DataElementCategoryOption option : dataElementCategoryOptions )
            {
                categoryOptionsString += ( categoryOptionsString.isEmpty() ? "" : ", " ) + option.getName();
            }
        }

        return "getDataApprovalStatus( " + dataSet.getName() + ", " + period.getPeriodType().getName() + ":" + period.getShortName()
            + ", " + organisationUnit.getName() + " (level " + organisationUnitLevel + "), "
            + ( categoryOptionGroupsString.isEmpty() ? "null" : ( "[" + categoryOptionGroupsString + "]" ) ) + ", "
            + ( categoryOptionsString.isEmpty() ? "null" : ( "[" + categoryOptionsString + "]" ) ) + " )";
    }

    /**
     * Handles the case where the selected period type is longer than the
     * data set period type. The selected period is broken down into data
     * set type periods. The approval status of the selected period is
     * constructed by logic that combines the approval statuses of the
     * constituent periods.
     * <p>
     * If the data is unapproved for any time segment, returns
     * UNAPPROVED_ELSEWHERE.
     * <p>
     * If the data is accepted for all time segments, returns
     * ACCEPTED_ELSEWHERE.
     * <p>
     * If the data is approved for all time segments (and maybe accepted for
     * some but not all), returns APPROVED_ELSEWHERE.
     * <p>
     * Note that the dataApproval object always returns null.
     * <p>
     * If data is accepted and/or approved in all time periods, the
     * dataApprovalLevel object reference points to the lowest level of
     * approval among the time periods. (For each time period, we find the
     * highest level of approval, so this is effectively the "lowest of the
     * highest" level of approval among all the time periods.)
     *
     * @return status status of the longer period
     */
    private void findStatusForLongerPeriodType()
    {
        Collection<Period> testPeriods = periodService.getPeriodsBetweenDates( dataSet.getPeriodType(), period.getStartDate(), period.getEndDate() );

        DataApprovalLevel lowestApprovalLevel = null;

        for ( Period testPeriod : testPeriods )
        {
            period = testPeriod;

            DataApprovalState s = getState();

            switch ( s )
            {
                case APPROVED_HERE:
                case APPROVED_ELSEWHERE:

                    state = DataApprovalState.APPROVED_ELSEWHERE;

                    dataApproval = null;

                    if ( lowestApprovalLevel == null || dataApprovalLevel.getLevel() > lowestApprovalLevel.getLevel() )
                    {
                        lowestApprovalLevel = dataApprovalLevel;
                    }

                    break;

                case ACCEPTED_HERE:
                case ACCEPTED_ELSEWHERE:

                    if ( state == null )
                    {
                        state = DataApprovalState.ACCEPTED_ELSEWHERE;
                    }

                    dataApproval = null;

                    if ( lowestApprovalLevel == null || dataApprovalLevel.getLevel() > lowestApprovalLevel.getLevel() )
                    {
                        lowestApprovalLevel = dataApprovalLevel;
                    }

                    break;

                case UNAPPROVED_READY:
                case UNAPPROVED_WAITING:
                case UNAPPROVED_ELSEWHERE:

                    dataApproval = null;
                    dataApprovalLevel = null;

                    state = DataApprovalState.UNAPPROVED_ELSEWHERE;

                    return;

                case UNAPPROVABLE:
                default: // (Not expected)

                    state = s;

                    dataApproval = null;
                    dataApprovalLevel = null;

                    return;
            }
        }

        dataApprovalLevel = lowestApprovalLevel;
    }

    /**
     * Find the approval status from a data selection that has the same
     * period type as the data set.
     *
     * @return the approval state.
     */
    private DataApprovalState getState()
    {
        if ( isApprovedAtThisOrHigherLevel() )
        {
            log.debug( "getState() - approved at this or higher level " + foundThisOrHigherIndex + ", this index is " + thisIndex );

            if ( foundThisOrHigherIndex == thisIndex )
            {
                if ( dataApproval.isAccepted() )
                {
                    log.debug( "getState() - accepted here." );

                    return DataApprovalState.ACCEPTED_HERE;
                }
                else
                {
                    log.debug( "getState() - approved here." );

                    return DataApprovalState.APPROVED_HERE;
                }
            }

            if ( dataApproval.isAccepted() )
            {
                log.debug( "getState() - accepted for a wider selection of category options, or at higher level." );

                return DataApprovalState.ACCEPTED_ELSEWHERE;
            }
            else
            {
                log.debug( "getState() - approved for a wider selection of category options, or at higher level." );

                return DataApprovalState.APPROVED_ELSEWHERE;
            }
        }

        boolean unapprovedBelow = isUnapprovedBelow( organisationUnit, organisationUnitLevel );

        if ( thisIndex != INDEX_NOT_FOUND ) // Could be approved at this level but is not.
        {
            if ( !unapprovedBelow )
            {
                log.debug( "getState() - unapproved ready." );

                dataApprovalLevel = allApprovalLevels.get( thisIndex );

                return DataApprovalState.UNAPPROVED_READY;
            }

            log.debug( "getState() - waiting." );

            return DataApprovalState.UNAPPROVED_WAITING;
        }

        if ( dataSetAssignedAtOrBelowLevel )
        {
            log.debug( "getState() - waiting for higher-level approval at a higher level for data at or below this level." );

            return DataApprovalState.UNAPPROVED_ELSEWHERE;
        }

        log.debug( "getState() - unapprovable because not approvable at level or below, and no dataset assignment." );

        return DataApprovalState.UNAPPROVABLE;
    }

    /**
     * Compares the approval levels with the data selection, to determine how
     * the data selection might be approved at each levels.
     * <p>
     * This is done for each level by finding the category option groups
     * (if any) that satisfy both of these:
     * <ul>
     *     <li>Fall under the category option group set for this level</li>
     *     <li>Describe the data selection</li>
     * </ul>
     * For levels with a category option group set, the data selection may be
     * approved at that level only if the level's category option group set
     * contains a category option group under which the data falls.
     */
    private void findCategoryOptionGroupsByLevel()
    {
        allApprovalLevels = dataApprovalLevelService.getAllDataApprovalLevels();

        categoryOptionGroupsByLevel = new ArrayList<Set<CategoryOptionGroup>>();

        if ( allApprovalLevels != null )
        {
            for ( DataApprovalLevel level : allApprovalLevels )
            {
                if ( level.getCategoryOptionGroupSet() == null )
                {
                    log.debug( "findCategoryOptionGroupsByLevel() found level " + level.getLevel()
                        + " org unit level " + level.getOrgUnitLevel()
                        + " with no category option groups." );

                    categoryOptionGroupsByLevel.add ( null );
                }
                else
                {
                    initSelectionGroups();

                    Set<CategoryOptionGroup> groups = selectionGroups.get( level.getCategoryOptionGroupSet() );

                    categoryOptionGroupsByLevel.add ( groups );
                }
            }
        }
    }

    /**
     * Initializes the selection groups if they have not yet been initialized.
     * This is a "lazy" operation that is only done if we find approval
     * levels that contain category option group sets we need to compare with.
     * <p>
     * selectionGroups are constructed by finding all the category option groups
     * (COGs) that contain COG and/or category options of the selection. The
     * selectionGroup map is indexed by category option group set (COGS). For
     * each COGS, it contains all the COGs that describe the data selection.
     * <p>
     * We will then use this information when we encounter an approval level
     * with a COGS. The selectionGroups map will tell us which COGs, if any,
     * from the selected data set apply to the COGS of the approval level.
     */
    private void initSelectionGroups()
    {
        if ( selectionGroups == null )
        {
            selectionGroups = new HashMap<CategoryOptionGroupSet, Set<CategoryOptionGroup>>();

            if ( categoryOptionGroups != null )
            {
                for ( CategoryOptionGroup  group : categoryOptionGroups )
                {
                    if ( group.getGroupSet() != null )
                    {
                        addDataGroup( group.getGroupSet(), group );

                        log.debug( "initSelectionGroups() adding categoryOptionGroupSet "
                            + group.getGroupSet().getName()
                            + ", group " + group.getName() );
                    }
                }
            }

            if ( dataElementCategoryOptions != null )
            {
                addDataGroups();
            }
        }
    }

    /**
     * Finds the category option groups (and their group sets) referenced by the category options.
     */
    private void addDataGroups()
    {
        //TODO: Should we replace this exhaustive search with a Hibernate query?

        Collection<CategoryOptionGroup> allGroups = categoryService.getAllCategoryOptionGroups();

        for ( CategoryOptionGroup group : allGroups )
        {
            if ( group.getGroupSet() != null && CollectionUtils.containsAny( group.getMembers(), dataElementCategoryOptions ) )
            {
                addDataGroup( group.getGroupSet(), group );

                log.debug( "addDataGroups(): Adding " + group.getGroupSet().getName() + ", " + group.getName() );
            }
            else
            {
                log.debug( "addDataGroups(): Not adding " + group.getName() + " (group set "
                    + ( group.getGroupSet() == null ? "null" : group.getGroupSet().getName() ) + ")" );
            }
        }
    }

    /**
     * Adds a category option group set and associated category option group
     * to the set of these pairs referenced by the selected data.
     *
     * @param groupSet category option group set to add
     * @param group category option group to add
     */
    private void addDataGroup( CategoryOptionGroupSet groupSet, CategoryOptionGroup group )
    {
        Set<CategoryOptionGroup> groups = selectionGroups.get( groupSet );

        if ( groups == null )
        {
            groups = new HashSet<CategoryOptionGroup>();

            selectionGroups.put( groupSet, groups );
        }

        groups.add( group );
    }

    /**
     * Finds the data approval level (if any) at which this data selection would
     * be approved. Also determines the levels just above and just below where
     * this selection would be approved.
     */
    private void findThisLevel()
    {
        thisIndex = INDEX_NOT_FOUND;

        thisOrHigherIndex = INDEX_NOT_FOUND;

        lowerIndex = 0;

        for ( int i = 0; i < allApprovalLevels.size() && organisationUnitLevel >= allApprovalLevels.get( i ).getOrgUnitLevel(); i++ )
        {
            thisOrHigherIndex = i;

            lowerIndex = i + 1;

            if ( approvableAtLevel( i ) )
            {
                thisIndex = i;

                break;
            }
        }

        log.debug( "findThisLevel() - returning thisOrHigher=" + thisOrHigherIndex + ", this=" + thisIndex + ", lower=" + lowerIndex );
    }

    /**
     * Is this data selection approvable at level index i? This method is
     * called when we already know that the organisation unit level is
     * compatible between the data selection and the matching approval level
     * at index i. The job of this method is to determine whether the selected
     * category option groups and/or category options (if any) are compatible
     * with the category option group set (if any) defined for this level.
     * <p>
     * If any category options were specified, then the data is not approvable
     * at any level.
     * <p>
     * If the level contains no category option group set, then the selection
     * must contain no category option group.
     * <p>
     * If the level contains a category option group set, then the selection
     * must contain one (only) category option group. (Previous logic has
     * determined that if this is the case, the group will be a member of
     * the group set.)
     *
     * @param i the matching approval level index to test.
     * @return true if approvable at this level, otherwise false
     */
    private boolean approvableAtLevel( int i )
    {
        DataApprovalLevel level = allApprovalLevels.get( i );

        if ( organisationUnitLevel != level.getOrgUnitLevel() )
        {
            log.debug( "approvableAtLevel( " + i + " ) = false: org unit level " + organisationUnitLevel + " not at approval org unit level " + level.getOrgUnitLevel() );

            return false;
        }

        if ( dataElementCategoryOptions != null && dataElementCategoryOptions.size() != 0 )
        {
            log.debug( "approvableAtLevel( " + i + " ) = false: selection category options present." );

            return false;
        }

        if ( level.getCategoryOptionGroupSet() == null )
        {
            if ( categoryOptionGroups == null || categoryOptionGroups.size() == 0 )
            {
                log.debug( "approvableAtLevel( " + i + " ) = true: no COG in selection or COGS in level." );

                return true;
            }
            else
            {
                log.debug( "approvableAtLevel( " + i + " ) = false: COG in selection but no COGS in level." );

                return false;
            }
        }
        else
        {
            if ( categoryOptionGroups != null && categoryOptionGroups.size() == 1
                    && categoryOptionGroups.iterator().next().getGroupSet() == level.getCategoryOptionGroupSet() )
            {
                log.debug( "approvableAtLevel( " + i + " ) = true: COG in selection is a member of COGS in level." );

                return true;
            }
            else
            {
                log.debug( "approvableAtLevel( " + i + " ) = false: COGS in level, "
                    + ( categoryOptionGroups == null ? "no COG(s) in selection" :
                    ( categoryOptionGroups.size() ) + " COG(s) in selection"
                    + ( categoryOptionGroups.size() != 1 ? "" :
                    ( " selected COG: " + categoryOptionGroups.iterator().next().getGroupSet().getName() ) ) ) );

                return false;
            }
        }
    }

    /**
     * Is this data selection approved at a higher approval level?
     * (Look for the highest level at which the selection is approved.)
     *
     * @return true if approved at higher level, otherwise false
     */
    private boolean isApprovedAtThisOrHigherLevel()
    {
        foundThisOrHigherIndex = -1;

        if ( thisOrHigherIndex >= 0 )
        {
            OrganisationUnit orgUnit = organisationUnit;

            int orgLevel = organisationUnitLevel;

            for ( int i = thisOrHigherIndex; i >= 0; i-- )
            {
                while ( orgLevel > allApprovalLevels.get( i ).getOrgUnitLevel() )
                {
                    log.debug( "isApprovedAtHigherLevel() moving up from " + orgUnit.getName() + "(" + orgLevel
                        + ") to " + orgUnit.getParent().getName() + "(" + ( orgLevel - 1 ) + ") towards org unit level "
                        + allApprovalLevels.get( i ).getOrgUnitLevel() );

                    orgUnit = orgUnit.getParent();

                    orgLevel--;
                }

                DataApproval da = getDataApproval( i, orgUnit );

                if ( da != null )
                {
                    foundThisOrHigherIndex = i;

                    dataApproval = da;

                    dataApprovalLevel = allApprovalLevels.get ( i );

                    log.debug( "isApprovedAtHigherLevel() found approval at level " + dataApprovalLevel.getLevel() );

                    // (Keep looping to see if selection is also approved at a higher level.)
                }
            }
        }

        log.debug( "isApprovedAtHigherLevel() returning " + ( foundThisOrHigherIndex >= 0 ) );

        return ( foundThisOrHigherIndex >= 0 );
    }

    /**
     * Is this data selection approved at the given level index, for the
     * given organisation unit?
     * <p>
     * If we are testing for approval at the same or higher level and
     * there are selected category options, then the data is approved if
     * *any selected* category option is approved.
     * <p>
     * If we are testing for approval at a lower level and
     * there are selected category options, then the data is approved if
     * *all category option group member category options* are approved.
     *
     * @param index (matching) approval level index at which to test.
     * @param orgUnit organisation unit to test.
     * @return DataApproval if approved, otherwise null.
     */
    private DataApproval getDataApproval( int index, OrganisationUnit orgUnit )
    {
        DataApproval da = null;

        Set<CategoryOptionGroup> groups = null;

        if ( index < lowerIndex )
        {
            groups = categoryOptionGroupsByLevel.get( index );
        }
        else if ( allApprovalLevels.get( index ).getCategoryOptionGroupSet() != null )
        {
            groups = new HashSet<CategoryOptionGroup>( allApprovalLevels.get( index ).getCategoryOptionGroupSet().getMembers() );
        }

        if ( groups == null || groups.isEmpty() )
        {
            da = dataApprovalStore.getDataApproval( dataSet, period, orgUnit, null );

            log.debug( "getDataApproval( " + orgUnit.getName() + " ) = " + ( da != null ) + " (no groups)" );

            return da;
        }

        for ( CategoryOptionGroup group : groups )
        {
            da = dataApprovalStore.getDataApproval( dataSet, period, orgUnit, group );

            log.debug( "getDataApproval( " + orgUnit.getName() + " ) = " + ( da != null ) + " (group: " + group.getName() + ")" );

            if ( index < lowerIndex )
            {
                if ( da != null )
                {
                    return da;
                }
            }
            else if ( da == null )
            {
                return null;
            }
        }

        log.debug( "getDataApproval( " + orgUnit.getName() + " ) = " + ( da != null ) + " (after testing all " + groups.size() + " groups)" );

        return da;
    }

    /**
     * Test to see if we are waiting for approval below that could exist, but
     * does not yet.
     * <p>
     * Also, look to see if the data set is assigned to any descendant
     * organisation units. If there are no approval levels below us, then
     * keep looking to see if there are any data set assignments -- if not,
     * and if the main level is not approvable, then approval does not apply.
     * This means that the recursion down through org units could continue
     * even if we are not waiting for an approval -- because we want to see
     * if there is lower-level data to be entered or not for this data set.
     *
     * @param orgUnit Organisation unit to test
     * @param orgUnitLevel The corresponding organisation unit level
     * @return true if we find an approval level and org unit for which
     * an approval object does not exist, else false
     */
    private boolean isUnapprovedBelow ( OrganisationUnit orgUnit, int orgUnitLevel )
    {
        log.debug( "isUnapprovedBelow( " + orgUnit.getName() + " )" );

        if ( dataSetAssignedAtOrBelowLevel == false && orgUnit.getAllDataSets().contains( dataSet ) )
        {
            dataSetAssignedAtOrBelowLevel = true;
        }

        if ( lowerIndex < allApprovalLevels.size() )
        {
            if ( orgUnitLevel == allApprovalLevels.get( lowerIndex ).getOrgUnitLevel() )
            {
                log.debug( "isUnapprovedBelow() orgUnit level " + orgUnitLevel + " matches approval level." );

                DataApproval da = getDataApproval( lowerIndex, orgUnit );

                log.debug( "isUnapprovedBelow() returns " + ( da == null ) + " after looking for approval for this orgUnit." );

                return ( da == null );
            }
        }
        else if ( dataSetAssignedAtOrBelowLevel )
        {
            log.debug( "isUnapprovedBelow() returns false with data set assigned at or below level." );

            return false;
        }

        if ( orgUnit.getChildren() == null || orgUnit.getChildren().size() == 0 )
        {
            log.debug( "isUnapprovedBelow() returns false with no more children." );

            return false;
        }

        log.debug( "isUnapprovedBelow( " + orgUnit.getName() + " ) is recursing." );

        for ( OrganisationUnit child : orgUnit.getChildren() )
        {
            if ( isUnapprovedBelow( child, orgUnitLevel + 1 ) )
            {
                log.debug( "isUnapprovedBelow( " + orgUnit.getName() + " ) returns true because unapproved from below." );

                return true;
            }
        }

        log.debug( "isUnapprovedBelow( " + orgUnit.getName() + " ) returns false after recursing." );

        return false;
    }
}
