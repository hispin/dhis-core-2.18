package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2013, University of Oslo
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
 * report, and to determine its data approval status.
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

    private List<DataApprovalLevel> matchingApprovalLevels;

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
                organisationUnitService.getLevelOfOrganisationUnit( organisationUnit.getUid() );

        log.info( logSelection() + " starting." );

        if ( !dataSet.isApproveData() )
        {
            log.info( logSelection() + " returning UNAPPROVABLE (dataSet not marked for approval)" );

            return STATUS_UNAPPROVABLE;
        }

        findMatchingApprovalLevels();

        if ( matchingApprovalLevels.size() == 0 )
        {
            log.info( logSelection() + " returning UNAPPROVABLE (no matching approval levels)" );

            return STATUS_UNAPPROVABLE;
        }

        findThisLevel();

        if ( lowerIndex == 0 )
        {
            log.info( logSelection() + " returning UNAPPROVABLE because org unit is above all approval levels" );

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
                log.info( logSelection() + " returning UNAPPROVABLE (period type too short)" );

                return STATUS_UNAPPROVABLE;
            }
        }
        else
        {
            state = getState();
        }

        DataApprovalStatus status = new DataApprovalStatus( state, dataApproval, dataApprovalLevel );

        log.info( logSelection() + " returning " + state.name() );

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
                + ( categoryOptionGroupsString.isEmpty() ? "null" : ( "[" + categoryOptionGroupsString + "]" ) ) + " )"
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
                default: // (Not expected.)

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
            log.info( "getState() - approved at this or higher level " + foundThisOrHigherIndex + ", this index is " + thisIndex );

            if ( foundThisOrHigherIndex == thisIndex )
            {
                if ( dataApproval.isAccepted() )
                {
                    log.info( "getState() - accepted here." );

                    return DataApprovalState.ACCEPTED_HERE;
                }
                else
                {
                    log.info( "getState() - approved here." );

                    return DataApprovalState.APPROVED_HERE;
                }
            }

            if ( dataApproval.isAccepted() )
            {
                log.info( "getState() - accepted for a wider selection of category options, or at higher level." );

                return DataApprovalState.ACCEPTED_ELSEWHERE;
            }
            else
            {
                log.info( "getState() - approved for a wider selection of category options, or at higher level." );

                return DataApprovalState.APPROVED_ELSEWHERE;
            }
        }

        boolean unapprovedBelow = isUnapprovedBelow( organisationUnit, organisationUnitLevel );

        if ( thisIndex != INDEX_NOT_FOUND ) // Could be approved at this level but is not.
        {
            if ( !unapprovedBelow )
            {
                log.info( "getState() - not unapproved below." );

                return DataApprovalState.UNAPPROVED_READY;
            }

            log.info( "getState() - waiting." );

            return DataApprovalState.UNAPPROVED_WAITING;
        }

        if ( dataSetAssignedAtOrBelowLevel )
        {
            log.info( "getState() - waiting for higher-level approval at a higher level for data at or below this level." );

            return DataApprovalState.UNAPPROVED_ELSEWHERE;
        }

        log.info( "getState() - unapprovable because not approvable at level or below, and no dataset assignment." );

        return DataApprovalState.UNAPPROVABLE;
    }

    /**
     * Find approval levels that apply to this selection, based on the
     * approval level's category option groups. Approval levels without
     * category option groups always apply. Approval levels with category
     * option groups only apply if the category option group contains
     * category options that apply to the selected data.
     * <p>
     * For each matching approval level, also remember the category
     * option groups (if any) that apply to this data selection and
     * match this level's category option group set.
     */
    private void findMatchingApprovalLevels()
    {
        matchingApprovalLevels = new ArrayList<DataApprovalLevel>();

        categoryOptionGroupsByLevel = new ArrayList<Set<CategoryOptionGroup>>();

        List<DataApprovalLevel> allDataApprovalLevels = dataApprovalLevelService.getAllDataApprovalLevels();

        if ( allDataApprovalLevels != null )
        {
            for ( DataApprovalLevel level : allDataApprovalLevels )
            {
                if ( level.getCategoryOptionGroupSet() == null )
                {
                    log.info( "findMatchingApprovalLevels() adding org unit level "
                            + level.getOrgUnitLevel()
                            + " with no category option groups." );

                    matchingApprovalLevels.add( level );

                    categoryOptionGroupsByLevel.add ( null );
                }
                else
                {
                    initSelectionGroups();

                    Set<CategoryOptionGroup> groups = selectionGroups.get( level.getCategoryOptionGroupSet() );

                    if ( groups != null )
                    {
                        matchingApprovalLevels.add( level );

                        categoryOptionGroupsByLevel.add ( groups );
                    }
                }
            }
        }

        log.info( "findMatchingApprovalLevels() " + allDataApprovalLevels.size() + " -> " +  matchingApprovalLevels.size() );
    }

    /**
     * Initializes the selection groups if they have not yet been initialized.
     * This is a "lazy" operation that is only done if we find approval
     * levels that contain category option group sets we need to compare with.
     *
     * selectionGroups are constructed by finding all the category option groups
     * (COGs) that contain COG and/or category options of the selection. The
     * selectionGroup map is indexed by category option group set (COGS). For
     * each COGS, it contains all the COGs that describe the data selection.
     *
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

                        log.info( "initSelectionGroups() adding categoryOptionGroupSet "
                                + group.getGroupSet().getName()
                                + ", group " + group.getName() );
                    }
                }
            }

            if ( dataElementCategoryOptions != null )
            {
                addDataGroups();
            }

            if ( log.isInfoEnabled() )
            {
                log.info("initSelectionGroups() returning " + selectionGroups.size() + " group sets:");

                for ( Map.Entry<CategoryOptionGroupSet,Set<CategoryOptionGroup>> entry : selectionGroups.entrySet() )
                {
                    String s = "";

                    if ( entry.getValue() != null )
                    {
                        for ( CategoryOptionGroup group : entry.getValue() )
                        {
                            s += ": " + group.getName();
                        }

                        log.info( "Group set " + entry.getKey().getName() + " (" + + entry.getValue().size() + ")" + s );
                    }
                }
            }
        }
    }

    /**
     * Finds the category option groups (and their group sets) referenced by the category options.
     */
    private void addDataGroups()
    {
        //TODO: Should we replace this exhaustive search with a Hibernate query?

        if ( log.isInfoEnabled() )
        {
            String s = "";

            for (DataElementCategoryOption option : dataElementCategoryOptions )
            {
                s += (s.isEmpty() ? "" : ", ") + option.getName();
            }

            log.info( "addDataGroups() looking for options " + s );
        }

        Collection<CategoryOptionGroup> allGroups = categoryService.getAllCategoryOptionGroups();

        for ( CategoryOptionGroup group : allGroups )
        {
            if ( log.isInfoEnabled() )
            {
                String s = "";

                for (DataElementCategoryOption option : group.getMembers() )
                {
                    s += (s.isEmpty() ? "" : ", ") + option.getName();
                }

                log.info( "addDataGroups() looking in group " + group.getName() + ", options " + s );
            }

            if ( group.getGroupSet() != null && CollectionUtils.containsAny( group.getMembers(), dataElementCategoryOptions ) )
            {
                addDataGroup( group.getGroupSet(), group );

                log.info( "addDataGroups(): Adding " + group.getGroupSet().getName() + ", " + group.getName() );
            }

            else log.info( "addDataGroups(): Not adding " + group.getName() + " (group set "
                    + ( group.getGroupSet() == null ? "null" : group.getGroupSet().getName() ) + ")" );
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
        log.info( "findThisLevel() - matchingApprovalLevels.size() = " + matchingApprovalLevels.size() );

        for ( int i = matchingApprovalLevels.size() - 1; i >= 0; i-- )
        {
            log.info( "findThisLevel() - testing index " + i
                    + " org level " + organisationUnitLevel
                    + " approval level " + matchingApprovalLevels.get( i ).getOrgUnitLevel() );

            if ( organisationUnitLevel == matchingApprovalLevels.get( i ).getOrgUnitLevel() )
            {
                if ( approvableAtLevel( i ) )
                {
                    thisIndex = i;
                }
                else
                {
                    thisIndex = INDEX_NOT_FOUND;
                }

                thisOrHigherIndex = i;
                lowerIndex = i + 1;

                log.info( "findThisLevel() - approvable at " + thisIndex );

                return;
            }
            else if ( organisationUnitLevel > matchingApprovalLevels.get( i ).getOrgUnitLevel() )
            {
                thisIndex = INDEX_NOT_FOUND;
                thisOrHigherIndex = i;
                lowerIndex = i+1;

                log.info( "findThisLevel() - org lower than level, thisOrHigher=" + thisOrHigherIndex + ", lower=" + lowerIndex );

                return;
            }
        }

        thisIndex = INDEX_NOT_FOUND;
        thisOrHigherIndex = INDEX_NOT_FOUND;
        lowerIndex = 0;

        log.info( "findThisLevel() - org higher than all levels, thisOrHigher=" + thisOrHigherIndex + ", lower=" + lowerIndex );
    }

    /**
     * Is this data selection approvable at level index i? This method is
     * called when we already know that the organisation unit level is
     * compatible between the data selection and the matching approval level
     * at index i. The job of this method is to determine whether the selected
     * category option groups and/or category options (if any) are compatible
     * with the category option group set (if any) defined for this level.
     *
     * If any category options were specified, then the data is not approvable
     * at any level.
     *
     * If the level contains no category option group set, then the selection
     * must contain no category option group.
     *
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
        if ( dataElementCategoryOptions != null && dataElementCategoryOptions.size() != 0 )
        {
            return false;
        }

        if ( matchingApprovalLevels.get( i ).getCategoryOptionGroupSet() == null )
        {
            return ( categoryOptionGroups == null || categoryOptionGroups.size() == 0 );
        }
        else
        {
            return ( categoryOptionGroups.size() == 1 );
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

            for (int i = thisOrHigherIndex; i >= 0; i-- )
            {
                while ( orgLevel > matchingApprovalLevels.get( i ).getOrgUnitLevel() )
                {
                    log.info( "isApprovedAtHigherLevel() moving up from " + orgUnit.getName() + " " + orgLevel
                            + " to " + orgUnit.getParent().getName() + " " + ( orgLevel - 1 ) + " towards "
                            + matchingApprovalLevels.get( i ).getOrgUnitLevel() );

                    orgUnit = orgUnit.getParent();

                    orgLevel--;
                }

                DataApproval da = getDataApproval( i, orgUnit );

                if ( da != null )
                {
                    foundThisOrHigherIndex = i;

                    dataApproval = da;

                    dataApprovalLevel = matchingApprovalLevels.get ( i );

                    log.info( "isApprovedAtHigherLevel() found approval at level " + dataApprovalLevel.getLevel() );

                    // (Keep looping to see if selection is also approved at a higher level.)
                }
            }
        }

        log.info( "isApprovedAtHigherLevel() returning " + ( foundThisOrHigherIndex >= 0 ) );

        return ( foundThisOrHigherIndex >= 0 );
    }

    /**
     * Is this data selection approved at the given level index, for the
     * given organisation unit and category option group(s)?
     *
     * @param index (matching) approval level index at which to test
     * @param orgUnit organisation unit to tes.
     * @return true if approved, otherwise false.
     */
    private DataApproval getDataApproval( int index, OrganisationUnit orgUnit )
    {
        Set<CategoryOptionGroup> groups = categoryOptionGroupsByLevel.get( index );

        if ( groups == null )
        {
            DataApproval d = dataApprovalStore.getDataApproval( dataSet, period, orgUnit, null );

            log.info("getDataApproval( " + orgUnit.getName() + " ) = " + ( d != null ) + " (no groups)" );

            return d;
        }

        for ( CategoryOptionGroup group : groups )
        {
            DataApproval d = dataApprovalStore.getDataApproval( dataSet, period, orgUnit, group );

            log.info("getDataApproval( " + orgUnit.getName() + " ) = " + ( d != null ) + " (group: " + group.getName() + ")" );

            if ( d != null )
            {
                return d;
            }
        }

        log.info("getDataApproval( " + orgUnit.getName() + " ) = false (none of " + groups.size() + " groups matched)" );

        return null;
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
        log.info( "isUnapprovedBelow( " + orgUnit.getName() + " )" );

        if ( dataSetAssignedAtOrBelowLevel == false && orgUnit.getAllDataSets().contains( dataSet ) )
        {
            dataSetAssignedAtOrBelowLevel = true;
        }

        if ( lowerIndex < matchingApprovalLevels.size() )
        {
            if ( orgUnitLevel == matchingApprovalLevels.get( lowerIndex ).getLevel() )
            {
                log.info( "isUnapprovedBelow() orgUnit level " + orgUnitLevel + " matches approval level." );

                DataApproval d = getDataApproval( lowerIndex, orgUnit );

                log.info( "isUnapprovedBelow() returns " + ( d == null ) + " after looking for approval for this orgUnit." );

                return ( d == null );
            }
        }
        else if ( dataSetAssignedAtOrBelowLevel )
        {
            log.info( "isUnapprovedBelow() returns false with data set assigned at or below level." );

            return false;
        }

        if ( orgUnit.getChildren() == null || orgUnit.getChildren().size() == 0 )
        {
            log.info( "isUnapprovedBelow() returns false with no more children." );

            return false;
        }

        log.info( "+++ isUnapprovedBelow( " + orgUnit.getName() + " ) is recursing..." );

        for ( OrganisationUnit child : orgUnit.getChildren() )
        {
            if ( isUnapprovedBelow( child, orgUnitLevel + 1 ) )
            {
                log.info( "--- isUnapprovedBelow( " + orgUnit.getName() + " ) returns true because unapproved from below." );

                return true;
            }
        }

        log.info( "--- isUnapprovedBelow( " + orgUnit.getName() + " ) returns false after recursing" );

        return false;
    }
}
