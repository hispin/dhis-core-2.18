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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import static org.hisp.dhis.dataapproval.DataApprovalState.*;

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
 * @version $Id$
 */
class DataApprovalSelection
{
    private final static Log log = LogFactory.getLog( DataApprovalSelection.class );

    private final static int INDEX_NOT_FOUND = -1;

    // -------------------------------------------------------------------------
    // Data selection parameters
    // -------------------------------------------------------------------------

    private List<DataApproval> dataApprovals;

    private DataApproval originalDataApproval;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalStore dataApprovalStore;

    private DataApprovalLevelService dataApprovalLevelService;

    private OrganisationUnitService organisationUnitService;

    private DataElementCategoryService categoryService;

    // -------------------------------------------------------------------------
    // Internal instance variables
    // -------------------------------------------------------------------------

    private List<DataApprovalLevel> allApprovalLevels;

    private DataApproval daIn; // Current DataApproval being checked.

    private DataApproval daOut = null; // DataApproval returned from DB.

    private OrganisationUnit selectedOrgUnit; // Selection org unit.

    private int organisationUnitLevel; // Selection's org unit level.

    private List<OrganisationUnit> organisationUnitAndAncestors;

    private boolean dataSetFoundBelow = false;

    private Map<DataElementCategoryOptionCombo, Set<CategoryOptionGroupSet>> optionComboGroupSetCache = new HashMap<>();

    // -------------------------------------------------------------------------
    // Preconstructed Status object
    // -------------------------------------------------------------------------

    private static final DataApprovalStatus STATUS_UNAPPROVABLE = new DataApprovalStatus( UNAPPROVABLE, null, null);

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a data approval selection.
     *
     * @param dataApprovals describes the parts of the selection
     * @param originalDataApproval contains original (undivided) period, etc.
     * @param dataApprovalStore service object reference
     * @param dataApprovalLevelService service object reference
     * @param organisationUnitService service object reference
     * @param categoryService service object reference
     */
    DataApprovalSelection( List<DataApproval> dataApprovals,
                           DataApproval originalDataApproval,
                           DataApprovalStore dataApprovalStore,
                           DataApprovalLevelService dataApprovalLevelService,
                           OrganisationUnitService organisationUnitService,
                           DataElementCategoryService categoryService )
    {
        this.dataApprovals = dataApprovals;
        this.originalDataApproval = originalDataApproval;
        this.dataApprovalStore = dataApprovalStore;
        this.dataApprovalLevelService = dataApprovalLevelService;
        this.categoryService = categoryService;
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Package-private method
    // -------------------------------------------------------------------------

    /**
     * Gets the data approval status for the selection, where the selection is
     * defined by a list of approvals objects. Note that all of the approvals
     * objects in the list must have the same organisation unit and the same
     * approval level.
     * <p>
     * This is done by looping through the list of approvals objects and
     * finding the status for the data described in each approvals object.
     * Each status is combined with the previous status by means of a state
     * machine, to get the "lowest common" status for all approvals objects.
     *
     * @return data approval status (lowest common status for the selection.)
     */
    DataApprovalStatus getDataApprovalStatus()
    {
        allApprovalLevels = dataApprovalLevelService.getAllDataApprovalLevels();

        if ( allApprovalLevels.isEmpty() ) // No approval levels defined!
        {
            return new DataApprovalStatus( UNAPPROVABLE, null, null );
        }

        DataApprovalStatus status = null;

        selectedOrgUnit = originalDataApproval.getOrganisationUnit();
        organisationUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( selectedOrgUnit );
        organisationUnitAndAncestors = selectedOrgUnit.getAncestors();
        organisationUnitAndAncestors.add( selectedOrgUnit );

        tracePrint( "++++++++" );
        tracePrint( "approval level: " + ( originalDataApproval.getDataApprovalLevel() == null ? "(null)" : originalDataApproval.getDataApprovalLevel().getLevel() ) );
        tracePrint( "data set: " + originalDataApproval.getDataSet().getName() );
        tracePrint( "period: " + originalDataApproval.getPeriod().getPeriodType().getName() + " " + originalDataApproval.getPeriod().getName() );
        tracePrint( "org unit: " + selectedOrgUnit.getName() );
        tracePrint( "org unit level: " + organisationUnitLevel );
        tracePrint( "attribute category option combo: " + ( originalDataApproval.getAttributeOptionCombo() == null ? "(null)" : originalDataApproval.getAttributeOptionCombo().getName() ) );
        tracePrint( "approval count: " + dataApprovals.size() );
        tracePrint( "approval level count: " + allApprovalLevels.size() );
        tracePrint( "--------" );

        log.info( "----------------------------------------------------------------------" );
        log.info( "getDataApprovalStatus() org unit " +  selectedOrgUnit.getName()
                + " (" + organisationUnitLevel + ") "
                + ") data set " + originalDataApproval.getDataSet().getName()
                + " original period " + originalDataApproval.getPeriod().getPeriodType().getName() + " " + originalDataApproval.getPeriod().getName()
                + " approval level " + ( originalDataApproval.getDataApprovalLevel() == null ? "(null)" : originalDataApproval.getDataApprovalLevel().getLevel() )
                + " approval count " + dataApprovals.size()
                + " starting." );

        for ( DataApproval dLoop : dataApprovals )
        {
            daIn = dLoop;

            if ( daIn.getOrganisationUnit() != selectedOrgUnit )        // Should not happen.
            {
                log.info( "Mismatch org unit " + ( daIn.getOrganisationUnit() == null ? "(null)" : daIn.getOrganisationUnit().getName() )
                        + " with " + ( selectedOrgUnit == null ? "(null)" : selectedOrgUnit.getName() ) );

                return new DataApprovalStatus( UNAPPROVABLE, null, null );
            }

            status = combineStatus( status, getStatus() );
        }

        if ( status.getDataApproval() != null )
        {
            status.getDataApproval().setPeriod( originalDataApproval.getPeriod() );
        }

        if ( originalDataApproval.getDataApprovalLevel() != null )
        {
            status.setDataApprovalLevel( originalDataApproval.getDataApprovalLevel() );
        }

        tracePrint("getDataApprovalStatus returning " + status.getDataApprovalLevel().getLevel() + "-" + status.getDataApprovalState().name() );
        tracePrint( "-----------------------" );

        log.info( "getDataApprovalStatus() org unit " +  selectedOrgUnit.getName()
                + " (" + organisationUnitLevel + ") "
                + ") data set " + originalDataApproval.getDataSet().getName()
                + " original period " + originalDataApproval.getPeriod().getPeriodType().getName() + " " + originalDataApproval.getPeriod().getName()
                + " approval level " + ( originalDataApproval.getDataApprovalLevel() == null ? "(null)" : originalDataApproval.getDataApprovalLevel().getLevel() )
                + " approval count " + dataApprovals.size()
                + " returning " + logStatus( status ) );

        return status;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void tracePrint( String s ) // Temporary, for development
    {
        if ( false ) // Enable or disable.
        {
            System.out.println( s );
        }
    }

    /**
     * Combine old (existing) approval status with new approval status
     * (from testing the status from another dataApproval object), resulting
     * in a new combined status.
     *
     * @param oldStatus old (existing) approval status
     * @param newStatus new approval status
     * @return new (combined) approval status
     */
    private DataApprovalStatus combineStatus( DataApprovalStatus oldStatus, DataApprovalStatus newStatus )
    {
        DataApprovalStatus status = newStatus;

        if ( oldStatus != null )
        {
            if ( oldStatus.getDataApprovalLevel().getLevel() > newStatus.getDataApprovalLevel().getLevel() )
            {
                status = oldStatus;
            }
            else if ( oldStatus.getDataApprovalLevel().getLevel() == newStatus.getDataApprovalLevel().getLevel() )
            {
                DataApprovalState state = DataApprovalAggregator.nextState( oldStatus.getDataApprovalState(), newStatus.getDataApprovalState() );

                DataApproval da = newStatus.getDataApproval();

                if ( oldStatus != null && ( oldStatus.getDataApproval() == null ||  !oldStatus.getDataApproval().isAccepted() ) )
                {
                    da = oldStatus.getDataApproval();
                }

                if ( da != null )
                {
                    da = new DataApproval( da ); // Defensive copy.
                }

                status = new DataApprovalStatus( state, da, oldStatus.getDataApprovalLevel() );
            }
        }

        log.info( "combineStatus( " + logStatus( oldStatus ) + ", " + logStatus( newStatus ) + " ) -> " + logStatus ( status ) );

        tracePrint( "combineStatus( " + logStatus( oldStatus ) + ", " + logStatus( newStatus ) + " ) -> " + logStatus ( status ) );
        tracePrint( "oldAccepted = " + ( oldStatus == null || oldStatus.getDataApproval() == null ? "(null)" : oldStatus.getDataApproval().isAccepted() )
                + ", newAccepted = " + ( newStatus == null || newStatus.getDataApproval() == null ? "(null)" : newStatus.getDataApproval().isAccepted() )
                + ", resultAccepted = " + ( status == null || status.getDataApproval() == null ? "(null)" : status.getDataApproval().isAccepted() ) );

        return status;
    }

    /**
     * Formats a status for display in the log.
     *
     * @param status status to log
     * @return string representing approval level and state
     */
    private String logStatus( DataApprovalStatus status )
    {
        return status == null ? "(null)" :
                ( status.getDataApprovalLevel() == null ? "(null level)" : status.getDataApprovalLevel().getLevel() )
                + "-" + ( status.getDataApprovalState() == null ? "(null state)" : status.getDataApprovalState().name() )
                + " da " + ( status.getDataApproval() == null ? "(null)" : ( "level "
                        + ( status.getDataApproval().getDataApprovalLevel() == null ? "(null)" : status.getDataApproval().getDataApprovalLevel().getLevel() ) ) );
    }

    /**
     * Gets that status for the data described by an approval object.
     * <p>
     * If the input approval level is null, it means start at the highest
     * level and go down all the way to the lowest, until we find an approval
     * level where there is an approval. If we find one, return the status
     * for approving at that level.
     * <p>
     * If the input approval level is not null, it means start at the highest
     * level and go down to that level, to see if we find a level where there
     * is approval. If we find one, return the status for approving at the
     * given input approval level. If we don't find any approval down to
     * that level, then check to see if there is unapproved data at a lower
     * level (meaning not ready to approve at this level.)
     *
     * @return the approval status
     */
    private DataApprovalStatus getStatus()
    {
        int checkToLevel = ( daIn.getDataApprovalLevel() == null ? allApprovalLevels.size() : daIn.getDataApprovalLevel().getLevel() );

        DataApprovalLevel latestApplicableLevel = null;

        for ( DataApprovalLevel dal : allApprovalLevels )
        {
            if ( optionApplies( dal ) )
            {
                latestApplicableLevel = dal;

                if ( dal.getLevel() <= checkToLevel && dal.getOrgUnitLevel() <= organisationUnitLevel )
                {
                    if ( isApproved( dal, organisationUnitAndAncestors.get( dal.getOrgUnitLevel() - 1 ) ) )
                    {
                        if ( daIn.getDataApprovalLevel() == null || ( dal.getLevel() == checkToLevel && dal.getOrgUnitLevel() == organisationUnitLevel ) )
                        {
                            return new DataApprovalStatus( daOut.isAccepted() ? ACCEPTED_HERE : APPROVED_HERE, daOut, dal );
                        }
                        else // data approval level is higher (lower number) and/or organisation unit level is higher (lower number)
                        {
                            return new DataApprovalStatus( daOut.isAccepted() ? ACCEPTED_ELSEWHERE : APPROVED_ELSEWHERE, daOut, dal );
                        }
                    }
                }
                else if ( isReadyBelow( dal, selectedOrgUnit, organisationUnitLevel ) )
                {
                    if ( dataSetFoundBelow || daIn.getDataSet().getSources().contains( originalDataApproval.getOrganisationUnit() ) )
                    {
                        return new DataApprovalStatus( UNAPPROVED_READY, daIn, dal );
                    }
                    else
                    {
                        tracePrint( "getStatus returning UNAPPROVABLE because not ready below and no data set assignment found at this level or below." );

                        return new DataApprovalStatus( UNAPPROVABLE, null, dal );
                    }
                }
                else
                {
                    return new DataApprovalStatus( dal.getOrgUnitLevel() >= organisationUnitLevel ? UNAPPROVED_WAITING : UNAPPROVED_ELSEWHERE, null, dal );
                }
            }
        }

        if ( latestApplicableLevel != null && isDataSetAssignedHereOrBelow( selectedOrgUnit ) )
        {
            return new DataApprovalStatus( UNAPPROVED_READY, daIn, latestApplicableLevel );
        }
        else
        {
            tracePrint( "getStatus latestApplicableLevel " + ( latestApplicableLevel == null ? "(null)" : latestApplicableLevel.getLevel() ) );
            tracePrint( "getStatus isDataSetAssignedHereOrBelow " + isDataSetAssignedHereOrBelow( selectedOrgUnit ) );
            tracePrint( "getStatus returning UNAPPROVABLE because we couldn't find a low enough level:" );

            return new DataApprovalStatus( UNAPPROVABLE, null, allApprovalLevels.get( allApprovalLevels.size() - 1 ) );
        }
    }

    /**
     * Tests if approval level options apply to this data approval selection.
     *
     * @param dal approval level with options to test
     * @return true if this approval level applies, else false
     */
    private boolean optionApplies( DataApprovalLevel dal )
    {
        tracePrint( "optionApplies - level " + dal.getLevel() + " COGS "
                + ( dal.getCategoryOptionGroupSet() == null ? "(none)" : dal.getCategoryOptionGroupSet().getName() )
                + " combo " + daIn.getAttributeOptionCombo().getName() );

        tracePrint("optionApplies - option combo group sets " + getOptionComboGroupSets( daIn.getAttributeOptionCombo() ) );

        return dal.getCategoryOptionGroupSet() == null
                || ( !daIn.getAttributeOptionCombo().equals( categoryService.getDefaultDataElementCategoryOptionCombo() )
                && getOptionComboGroupSets( daIn.getAttributeOptionCombo() ).contains( dal.getCategoryOptionGroupSet() ) );
    }

    /**
     * Finds the category option group sets containing groups having options
     * in this combination.
     *
     * @param optionCombo attribute option combination to test
     * @return attribute option group sets containing this combo
     */
    private Set<CategoryOptionGroupSet> getOptionComboGroupSets( DataElementCategoryOptionCombo optionCombo )
    {
        Set<CategoryOptionGroupSet> groupSets = optionComboGroupSetCache.get ( optionCombo );

        if ( groupSets == null )
        {
            groupSets = new HashSet<>();

            for ( DataElementCategoryOption option : optionCombo.getCategoryOptions() )
            {
                groupSets.addAll( option.getGroupSets() );
            }

            optionComboGroupSetCache.put( optionCombo, groupSets );
        }

        return groupSets;
    }

    /**
     * Tests whether the input data approval object is found in the database
     * using a specified data approval level and organisation unit.
     * <p>
     * Also, the daOut object reference is set to the data approval object
     * found (if any).
     *
     * @param dal data approval level to test
     * @param orgUnit organisation unit to test
     * @return true if the data approval exists in the database
     */
    private boolean isApproved( DataApprovalLevel dal, OrganisationUnit orgUnit )
    {
        daOut = dataApprovalStore.getDataApproval( dal, daIn.getDataSet(), daIn.getPeriod(), orgUnit, daIn.getAttributeOptionCombo() );

        tracePrint( "getDataApproval ( level "
                + ( dal == null ? "(null)" : dal.getLevel() ) + ", "
                + ( daIn.getDataSet() == null ? "(null)" : daIn.getDataSet().getName() ) + ", "
                + daIn.getPeriod().getName() + ", '"
                + orgUnit.getName() + "', "
                + ( daIn.getAttributeOptionCombo() == null ? "(null)" : daIn.getAttributeOptionCombo().getName() )
                + " ) -> " + (daOut == null ? "unapproved" : daOut.isAccepted() ? "accepted" : "approved" )
                + ( daOut == null ? "" : ( " @" + Integer.toHexString(System.identityHashCode(daOut) ) ) ) );

        return daOut != null;
    }

    /**
     * Tests to see if we are waiting for approval at a lower level that could
     * exist, but does not yet.
     * <p>
     * Also, look to see if the data set is assigned to any descendant
     * organisation units. If there are no approval levels below us, then
     * keep looking to see if there are any data set assignments -- if not,
     * and if the main level is not approvable, then approval does not apply.
     * This means that the recursion down through org units could continue
     * even if we are not waiting for an approval -- because we want to see
     * if there is lower-level data to be entered or not for this data set.
     *
     * @param dal data approval level to test
     * @param orgUnit Organisation unit to test
     * @param orgUnitLevel The corresponding organisation unit level
     * @return true if we find an approval level and org unit for which
     * an approval object does not exist, else false
     */
    private boolean isReadyBelow( DataApprovalLevel dal, OrganisationUnit orgUnit, int orgUnitLevel )
    {
        boolean dataSetAssigned = daIn.getDataSet().getSources().contains( orgUnit );
        dataSetFoundBelow = dataSetFoundBelow || dataSetAssigned; // Org unit refers to this data set.

        log.info( "isReadyBelow( " + dal.getLevel() + ", " + orgUnit.getName() + " - " + orgUnitLevel + " ) DAL: " + dal.getLevel()
                + " dataSet: " + daIn.getDataSet().getName() + " assigned: " + dataSetAssigned + " assignedBelow: " + dataSetFoundBelow );

        tracePrint( "isReadyBelow( " + dal.getLevel() + ", " + orgUnit.getName() + " - " + orgUnitLevel
                + " ) dataSet " + daIn.getDataSet().getName() + " assigned: " + dataSetAssigned + " assignedBelow: " + dataSetFoundBelow );

        if ( orgUnitLevel == dal.getOrgUnitLevel() && isApproved( dal, orgUnit ) )
        {
            tracePrint( "isReadyBelow( " + dal.getLevel() + ", " + orgUnit.getName() + " - " + orgUnitLevel
                    + " ) returns true because approval found." );

            dataSetFoundBelow = true; // We found an approval object referring to this data set.

            return true; // OK here because there's an approval below.
        }

        if ( dataSetAssigned && orgUnitLevel >= dal.getOrgUnitLevel() )
        {
            tracePrint( "isReadyBelow( " + dal.getLevel() + ", " + orgUnit.getName() + " - " + orgUnitLevel
                    + " ) returns false because data set assignment found without approval." );

            return false; // Missing approval below.
        }

        for ( OrganisationUnit child : orgUnit.getChildren() )
        {
            tracePrint( "isReadyBelow( " + dal.getLevel() + ", " + orgUnit.getName() + " - " + orgUnitLevel
                    + " ) recursing to child " + child.getName() + "-" + ( orgUnitLevel + 1 ) );

            if ( !isReadyBelow( dal, child, orgUnitLevel + 1 ) )
            {
                tracePrint( "isReadyBelow( " + dal.getLevel() + ", " + orgUnit.getName() + " - " + orgUnitLevel
                        + " ) returns false because child is not ready below." );

                log.info( "isReadyBelow( " + dal.getLevel() + ", " + orgUnit.getName() + " - " + orgUnitLevel
                        + " ) returns false because child is not ready below." );

                return false;
            }
        }

        tracePrint( "isReadyBelow( " + dal.getLevel() + ", " + orgUnit.getName() + " - " + orgUnitLevel
                + " ) returns true at the end." );

        return true;
    }

    /**
     * Tests to see if this organisation unit, or any descendent, is
     * assigned to the selected data set (and is therefore approvable.)
     *
     * @param orgUnit organisation unit to test
     * @return true if assigned to data set, else false
     */
    private boolean isDataSetAssignedHereOrBelow( OrganisationUnit orgUnit )
    {
        if ( daIn.getDataSet().getSources().contains( orgUnit ) )
        {
            return true;
        }

        for ( OrganisationUnit child : orgUnit.getChildren() )
        {
            if ( isDataSetAssignedHereOrBelow( child ) )
            {
                return true;
            }
        }

        return false;
    }
}
