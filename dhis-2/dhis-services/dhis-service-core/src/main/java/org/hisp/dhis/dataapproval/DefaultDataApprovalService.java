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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Jim Grace
 */
@Transactional
public class DefaultDataApprovalService
    implements DataApprovalService
{
    private final static Log log = LogFactory.getLog( DefaultDataApprovalService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalStore dataApprovalStore;

    public void setDataApprovalStore( DataApprovalStore dataApprovalStore )
    {
        this.dataApprovalStore = dataApprovalStore;
    }

    private DataApprovalLevelService dataApprovalLevelService;

    public void setDataApprovalLevelService( DataApprovalLevelService dataApprovalLevelService )
    {
        this.dataApprovalLevelService = dataApprovalLevelService;
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

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private SecurityService securityService;

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    // -------------------------------------------------------------------------
    // DataApproval
    // -------------------------------------------------------------------------

    @Override
    public void addAllDataApprovals( List<DataApproval> dataApprovalList )
    {
        for ( DataApproval dataApproval : dataApprovalList )
        {
            addDataApproval( dataApproval );
        }
    }

    public void addDataApproval( DataApproval dataApproval )
    {
        if ( (dataApproval.getCategoryOptionGroup() == null || securityService.canRead( dataApproval.getCategoryOptionGroup() ))
            && mayApprove( dataApproval.getDataApprovalLevel(), dataApproval.getOrganisationUnit() ) )
        {
            PeriodType selectionPeriodType = dataApproval.getPeriod().getPeriodType();
            PeriodType dataSetPeriodType = dataApproval.getDataSet().getPeriodType();

            if ( selectionPeriodType.equals( dataSetPeriodType ) )
            {
                dataApprovalStore.addDataApproval( dataApproval );
            }
            else if ( selectionPeriodType.getFrequencyOrder() <= dataSetPeriodType.getFrequencyOrder() )
            {
                log.warn( "Attempted data approval for period " + dataApproval.getPeriod().getIsoDate()
                    + " is incompatible with data set period type " + dataSetPeriodType.getName() + "." );
            }
            else
            {
                approveCompositePeriod( dataApproval );
            }
        }
        else
        {
            warnNotPermitted( dataApproval, "approve",
                mayApprove( dataApproval.getDataApprovalLevel(), dataApproval.getOrganisationUnit() ) );
        }
    }

    public void deleteDataApprovals( List<DataApproval> dataApprovalList )
    {
        for ( DataApproval dataApproval : dataApprovalList )
        {
            deleteDataApproval( dataApproval );
        }
    }

    public void deleteDataApproval( DataApproval dataApproval )
    {
        boolean mayUnapprove = mayUnapprove( dataApproval.getDataApprovalLevel(), dataApproval.getOrganisationUnit(), dataApproval.isAccepted() )
            || mayAcceptOrUnaccept( dataApproval.getDataApprovalLevel(), dataApproval.getOrganisationUnit() );

        if ( (dataApproval.getCategoryOptionGroup() == null || securityService.canRead( dataApproval.getCategoryOptionGroup() ))
            && mayUnapprove )
        {
            PeriodType selectionPeriodType = dataApproval.getPeriod().getPeriodType();
            PeriodType dataSetPeriodType = dataApproval.getDataSet().getPeriodType();

            if ( selectionPeriodType.equals( dataSetPeriodType ) )
            {
                dataApprovalStore.deleteDataApproval( dataApproval );

                for ( OrganisationUnit ancestor : dataApproval.getOrganisationUnit().getAncestors() )
                {
                    DataApproval ancestorApproval = dataApprovalStore.getDataApproval(
                        dataApproval.getDataSet(), dataApproval.getPeriod(), ancestor, dataApproval.getCategoryOptionGroup() );

                    if ( ancestorApproval != null )
                    {
                        dataApprovalStore.deleteDataApproval( ancestorApproval );
                    }
                }
            }
            else if ( selectionPeriodType.getFrequencyOrder() <= dataSetPeriodType.getFrequencyOrder() )
            {
                log.warn( "Attempted data unapproval for period " + dataApproval.getPeriod().getIsoDate()
                    + " is incompatible with data set period type " + dataSetPeriodType.getName() + "." );
            }
            else
            {
                unapproveCompositePeriod( dataApproval );
            }
        }
        else
        {
            warnNotPermitted( dataApproval, "unapprove", mayUnapprove );
        }
    }

    public DataApprovalStatus getDataApprovalStatus( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        Set<DataElementCategoryOption> categoryOptions =
            attributeOptionCombo == null || attributeOptionCombo.equals( categoryService.getDefaultDataElementCategoryOptionCombo() ) ?
                null : attributeOptionCombo.getCategoryOptions();

        return getDataApprovalStatus( dataSet, period, organisationUnit, null, categoryOptions );
    }

    public DataApprovalStatus getDataApprovalStatus( DataSet dataSet, Period period, OrganisationUnit organisationUnit,
        Set<CategoryOptionGroup> categoryOptionGroups, Set<DataElementCategoryOption> dataElementCategoryOptions )
    {
        DataApprovalSelection dataApprovalSelection = new DataApprovalSelection( dataSet, period, organisationUnit,
            categoryOptionGroups, dataElementCategoryOptions,
            dataApprovalStore, dataApprovalLevelService,
            organisationUnitService, categoryService, periodService );

        return dataApprovalSelection.getDataApprovalStatus();
    }

    public DataApprovalPermissions getDataApprovalPermissions( DataSet dataSet, Period period,
        OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        Set<DataElementCategoryOption> categoryOptions =
            attributeOptionCombo == null || attributeOptionCombo.equals( categoryService.getDefaultDataElementCategoryOptionCombo() ) ?
                null : attributeOptionCombo.getCategoryOptions();

        return getDataApprovalPermissions( dataSet, period, organisationUnit, null, categoryOptions );
    }

    public DataApprovalPermissions getDataApprovalPermissions( DataSet dataSet, Period period,
        OrganisationUnit organisationUnit, Set<CategoryOptionGroup> categoryOptionGroups, Set<DataElementCategoryOption> dataElementCategoryOptions )
    {
        DataApprovalStatus status = getDataApprovalStatus( dataSet, period,
            organisationUnit, categoryOptionGroups, dataElementCategoryOptions );

        DataApprovalPermissions permissions = new DataApprovalPermissions();

        log.debug( "getDataApprovalPermissions() getting permissions." );

        permissions.setDataApprovalStatus( status );

        DataApprovalLevel dataApprovalLevel = status.getDataApprovalLevel();

        if ( dataApprovalLevel != null && securityService.canRead( dataApprovalLevel )
            && (dataApprovalLevel.getCategoryOptionGroupSet() == null || securityService.canRead( dataApprovalLevel.getCategoryOptionGroupSet() ))
            && canReadOneCategoryOptionGroup( categoryOptionGroups ) )
        {
            switch ( status.getDataApprovalState() )
            {
                case PARTIALLY_ACCEPTED_HERE:
                case ACCEPTED_HERE:
                case PARTIALLY_APPROVED_HERE:
                case APPROVED_HERE:
                case UNAPPROVED_READY:
                    permissions.setMayApprove( mayApprove( status.getDataApprovalLevel(), organisationUnit ) );
                    permissions.setMayUnapprove( mayUnapprove( status.getDataApprovalLevel(), organisationUnit, status.getDataApprovalState().isAccepted() ) );
                    permissions.setMayAccept( mayAcceptOrUnaccept( status.getDataApprovalLevel(), organisationUnit ) );
                    permissions.setMayUnaccept( permissions.isMayAccept() );

                    if ( permissions.isMayUnaccept() )
                    {
                        permissions.setMayUnapprove( true );
                    }

                    break;
            }
        }

        log.debug( "Returning permissions for " + organisationUnit.getName()
            + " " + status.getDataApprovalState().name()
            + " may approve = " + permissions.isMayApprove()
            + " may unapprove = " + permissions.isMayUnapprove()
            + " may accept = " + permissions.isMayAccept()
            + " may unaccept = " + permissions.isMayUnaccept() );

        return permissions;
    }

    public void accept( DataApproval dataApproval )
    {
        acceptOrUnaccept( dataApproval, true );
    }

    public void unaccept( DataApproval dataApproval )
    {
        acceptOrUnaccept( dataApproval, false );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Accept or unaccept a data approval.
     *
     * @param dataApproval the data approval object.
     * @param accepted     true to accept, false to unaccept.
     */
    public void acceptOrUnaccept( DataApproval dataApproval, boolean accepted )
    {
        if ( (dataApproval.getCategoryOptionGroup() == null || securityService.canRead( dataApproval.getCategoryOptionGroup() ))
            && mayAcceptOrUnaccept( dataApproval.getDataApprovalLevel(), dataApproval.getOrganisationUnit() ) )
        {
            PeriodType selectionPeriodType = dataApproval.getPeriod().getPeriodType();
            PeriodType dataSetPeriodType = dataApproval.getDataSet().getPeriodType();

            if ( selectionPeriodType.equals( dataSetPeriodType ) )
            {
                dataApproval.setAccepted( accepted );
                dataApprovalStore.updateDataApproval( dataApproval );
            }
            else if ( selectionPeriodType.getFrequencyOrder() <= dataSetPeriodType.getFrequencyOrder() )
            {
                log.warn( "Attempted data approval for period " + dataApproval.getPeriod().getIsoDate()
                    + " is incompatible with data set period type " + dataSetPeriodType.getName() + "." );
            }
            else
            {
                acceptOrUnacceptCompositePeriod( dataApproval, accepted );
            }
        }
        else
        {
            warnNotPermitted( dataApproval, accepted ? "accept" : "unaccept",
                mayAcceptOrUnaccept( dataApproval.getDataApprovalLevel(), dataApproval.getOrganisationUnit() ) );
        }
    }

    /**
     * Approves data for a longer period that contains multiple data approval
     * periods. When individual periods are already approved, no action is
     * necessary. (It's possible that they could be accepted as well.)
     *
     * @param da data approval object describing the longer period.
     */
    private void approveCompositePeriod( DataApproval da )
    {
        Collection<Period> periods = periodService.getPeriodsBetweenDates(
            da.getDataSet().getPeriodType(),
            da.getPeriod().getStartDate(),
            da.getPeriod().getEndDate() );

        for ( Period period : periods )
        {
            DataApprovalStatus status = getDataApprovalStatus( da.getDataSet(), period, da.getOrganisationUnit(),
                da.getCategoryOptionGroup() == null ? null : org.hisp.dhis.system.util.CollectionUtils.asSet( da.getCategoryOptionGroup() ), null );

            if ( status.getDataApprovalState().isReady() && !status.getDataApprovalState().isApproved() )
            {
                DataApproval dataApproval = new DataApproval( da );
                dataApproval.setPeriod( period );

                dataApprovalStore.addDataApproval( dataApproval );
            }
        }
    }

    /**
     * Unapproves data for a longer period that contains multiple data approval
     * periods. When individual periods are already unapproved, no action is
     * necessary.
     * <p/>
     * Note that when we delete approval for a period, we also need to make
     * sure that approval is removed for any ancestors at higher levels of
     * approval. For this reason, we go back through the main deleteDataApproval
     * method. (It won't call back here, becuase it's only for one period.)
     *
     * @param da data approval object describing the longer period.
     */
    void unapproveCompositePeriod( DataApproval da )
    {
        Collection<Period> periods = periodService.getPeriodsBetweenDates(
            da.getDataSet().getPeriodType(),
            da.getPeriod().getStartDate(),
            da.getPeriod().getEndDate() );

        for ( Period period : periods )
        {
            DataApprovalStatus status = getDataApprovalStatus( da.getDataSet(), period, da.getOrganisationUnit(),
                da.getCategoryOptionGroup() == null ? null : org.hisp.dhis.system.util.CollectionUtils.asSet( da.getCategoryOptionGroup() ), null );

            if ( status.getDataApprovalState().isApproved() )
            {
                deleteDataApproval( status.getDataApproval() );
            }
        }
    }

    /**
     * Accepts or unaccepts data for a longer period that contains multiple
     * data approval periods. When individual periods are already at the
     * desired accptance state, no action is necessary.
     *
     * @param da       data approval object describing the longer period.
     * @param accepted true to accept, false to unaccept.
     */
    private void acceptOrUnacceptCompositePeriod( DataApproval da, boolean accepted )
    {
        Collection<Period> periods = periodService.getPeriodsBetweenDates(
            da.getDataSet().getPeriodType(),
            da.getPeriod().getStartDate(),
            da.getPeriod().getEndDate() );

        for ( Period period : periods )
        {
            DataApprovalStatus status = getDataApprovalStatus( da.getDataSet(), period, da.getOrganisationUnit(),
                da.getCategoryOptionGroup() == null ? null : org.hisp.dhis.system.util.CollectionUtils.asSet( da.getCategoryOptionGroup() ), null );

            if ( status.getDataApprovalState().isApprovable() && status.getDataApprovalState().isAccepted() != accepted )
            {
                status.getDataApproval().setAccepted( accepted );
                dataApprovalStore.updateDataApproval( status.getDataApproval() );
            }
        }
    }

    /**
     * Return true if there are no category option groups, or if there is
     * one and the user can read it.
     *
     * @param categoryOptionGroups option groups (if any) for data selection
     * @return true if at most 1 option group and user can read, else false
     */
    boolean canReadOneCategoryOptionGroup( Collection<CategoryOptionGroup> categoryOptionGroups )
    {
        if ( categoryOptionGroups == null || categoryOptionGroups.size() == 0 )
        {
            return true;
        }

        if ( categoryOptionGroups.size() != 1 )
        {
            return false;
        }

        return (securityService.canRead( (CategoryOptionGroup) categoryOptionGroups.toArray()[0] ));
    }

    /**
     * Return true if there are no category option groups, or if the user
     * can read any category option group from the collection.
     *
     * @param categoryOptionGroups option groups (if any) for data selection
     * @return true if at most 1 option group and user can read, else false
     */
    boolean canReadSomeCategoryOptionGroup( Collection<CategoryOptionGroup> categoryOptionGroups )
    {
        if ( categoryOptionGroups == null )
        {
            return true;
        }

        for ( CategoryOptionGroup cog : categoryOptionGroups )
        {
            if ( securityService.canRead( cog ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see whether a user may approve data for a given
     * organisation unit.
     *
     * @param dataApprovalLevel This data approval level.
     * @param organisationUnit  The organisation unit to check for permission.
     * @return true if the user may approve, otherwise false
     */
    private boolean mayApprove( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit )
    {
        User user = currentUserService.getCurrentUser();

        if ( user != null )
        {
            boolean mayApprove = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE );
            boolean mayApproveAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );

            if ( mayApprove && user.getOrganisationUnits().contains( organisationUnit ) )
            {
                if ( !mayApproveAtLowerLevels && mayApproveAtNextHigherLevelOnly( dataApprovalLevel, organisationUnit ) )
                {
                    log.debug( "mayApprove = false because user may approve at the next higher level from approval level "
                        + dataApprovalLevel.getLevel() + " with " + organisationUnit.getName() );

                    return false;
                }

                log.debug( "mayApprove = true because organisation unit " + organisationUnit.getName()
                    + " is assigned to user and user may approve at same level." );

                return true;
            }

            if ( mayApproveAtLowerLevels && CollectionUtils.containsAny( user.getOrganisationUnits(),
                organisationUnit.getAncestors() ) )
            {
                log.debug( "mayApprove = true because organisation unit " + organisationUnit.getName()
                    + " is under user and user may approve at lower levels." );

                return true;
            }
        }

        log.debug( "mayApprove = false for organisation unit " + organisationUnit.getName() );

        return false;
    }

    /**
     * Checks to see whether a user may approve data at the next higher
     * approval level for this orgnaisation unit -- because they can
     * approve only at that next higher level (and not at lower levels.)
     * <p/>
     * It is assumed that the user has the authority to approve at their
     * level -- and not the authority to approve at lower levels.
     *
     * @param dataApprovalLevel This data approval level.
     * @param organisationUnit  The organisation unit to check for permission.
     * @return true if the user may approve at the next higher level.
     */
    private boolean mayApproveAtNextHigherLevelOnly( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit )
    {
        if ( dataApprovalLevel.getLevel() > 1 )
        {
            DataApprovalLevel nextLevel = dataApprovalLevelService.getDataApprovalLevelByLevelNumber( dataApprovalLevel.getLevel() - 1 );

            if ( securityService.canRead( nextLevel )
                && (nextLevel.getCategoryOptionGroupSet() == null ||
                (securityService.canRead( nextLevel.getCategoryOptionGroupSet() )
                    && canReadSomeCategoryOptionGroup( nextLevel.getCategoryOptionGroupSet().getMembers() ))) )
            {
                OrganisationUnit acceptOrgUnit = organisationUnit;
                for ( int i = nextLevel.getOrgUnitLevel(); i < dataApprovalLevel.getOrgUnitLevel(); i++ )
                {
                    acceptOrgUnit = acceptOrgUnit.getParent();
                }

                if ( currentUserService.getCurrentUser().getOrganisationUnits().contains( acceptOrgUnit ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks to see whether a user may unapprove a data approval.
     * <p/>
     * A user may unapprove data for organisation unit A if they have the
     * authority to approve data for organisation unit B, and B is an
     * ancestor of A.
     * <p/>
     * A user may also unapprove data for organisation unit A if they have
     * the authority to approve data for organisation unit A, and A has no
     * ancestors.
     * <p/>
     * But a user may not unapprove data for an organisation unit if the data
     * has been approved already at a higher level for the same period and
     * data set, and the user is not authorized to remove that approval as well.
     *
     * @param dataApproval the data approval object for the attempted operation.
     * @return true if the user may unapprove, otherwise false
     */
    private boolean mayUnapprove( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit, boolean isAccepted )
    {
        if ( isAuthorizedToUnapprove( dataApprovalLevel, organisationUnit ) )
        {
            if ( !isAccepted || mayAcceptOrUnaccept( dataApprovalLevel, organisationUnit ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to see whether a user may accept or unaccept an approval.
     * (For this, they will need access to the next higher approval level.)
     *
     * @param dataApproval The approval to check for permission.
     * @return true if the user may accept or unaccept, otherwise false.
     */
    private boolean mayAcceptOrUnaccept( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit )
    {
        User user = currentUserService.getCurrentUser();

        if ( dataApprovalLevel != null && user != null )
        {
            boolean mayAcceptAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS );

            if ( mayAcceptAtLowerLevels && mayAccessNextHigherLevel( dataApprovalLevel, organisationUnit ) )
            {
                log.debug( "User may accept or unaccept for organisation unit " + organisationUnit.getName()
                    + " and approval level " + dataApprovalLevel.getLevel() );

                return true;
            }
        }

        log.debug( "User with AUTH_ACCEPT_LOWER_LEVELS " + user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS )
            + " with " + user.getOrganisationUnits().size() + " org units"
            + " may not accept or unaccept for organisation unit "
            + (organisationUnit == null ? "(null)" : organisationUnit.getName()) );

        return false;
    }

    /**
     * Checks to see whether a user may access the next higher approval
     * level to see if they can accept at this approval level.
     * <p/>
     * It is assumed that the user has the authority to accept at lower levels.
     *
     * @param dataApprovalLevel This data approval level.
     * @param organisationUnit  The organisation unit to check for permission.
     * @return true if the user may approve at the next higher level.
     */
    private boolean mayAccessNextHigherLevel( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit )
    {
        if ( dataApprovalLevel.getLevel() > 1 )
        {
            DataApprovalLevel nextLevel = dataApprovalLevelService.getDataApprovalLevelByLevelNumber( dataApprovalLevel.getLevel() - 1 );

            if ( securityService.canRead( nextLevel )
                && (nextLevel.getCategoryOptionGroupSet() == null ||
                (securityService.canRead( nextLevel.getCategoryOptionGroupSet() )
                    && canReadSomeCategoryOptionGroup( nextLevel.getCategoryOptionGroupSet().getMembers() ))) )
            {
                OrganisationUnit acceptOrgUnit = organisationUnit;
                for ( int i = nextLevel.getOrgUnitLevel(); i < dataApprovalLevel.getOrgUnitLevel(); i++ )
                {
                    acceptOrgUnit = acceptOrgUnit.getParent();
                }

                User user = currentUserService.getCurrentUser();

                if ( user.getOrganisationUnits().contains( acceptOrgUnit ) ||
                    CollectionUtils.containsAny( user.getOrganisationUnits(), acceptOrgUnit.getAncestors() ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Tests whether the user is authorized to unapprove for this organisation
     * unit.
     * <p/>
     * Whether the user actually may unapprove an existing approval depends
     * also on whether there are higher-level approvals that the user is
     * authorized to unapprove.
     *
     * @param organisationUnit OrganisationUnit to check for approval.
     * @return true if the user may approve, otherwise false
     */
    private boolean isAuthorizedToUnapprove( DataApprovalLevel dataApprovalLevel, OrganisationUnit organisationUnit )
    {
        log.debug( "isAuthorizedToUnapprove( " + organisationUnit.getName() + ")" );

        if ( mayApprove( dataApprovalLevel, organisationUnit ) )
        {
            log.debug( "User may unapprove at " + organisationUnit.getName() );

            return true;
        }

        for ( OrganisationUnit ancestor : organisationUnit.getAncestors() )
        {
            if ( mayApprove( dataApprovalLevel, ancestor ) )
            {
                log.debug( "User may unapprove at " + ancestor.getName() );

                return true;
            }
        }

        log.debug( "User may not unapprove at " + organisationUnit.getName() );

        return false;
    }

    /**
     * Warns if the user is not permitted to make a data approval operation.
     * If the UI is working correctly, the user should never be able to choose
     * an operation for which they are not permitted. So this should happen
     * only if there is a programming error, or if the user is trying to perform
     * an operation that the UI would not normally offer.
     *
     * @param dataApproval the data approval object for the attempted operation.
     * @param operation    the name of the operation attempted.
     * @param mayOperate   whether the user may perform this operation.
     */
    private void warnNotPermitted( DataApproval dataApproval, String operation, boolean mayOperate )
    {
        String warning = "User " + currentUserService.getCurrentUsername() + " tried to " + operation
            + " data for (org unit " + dataApproval.getOrganisationUnit().getName()
            + ", period " + dataApproval.getPeriod().getName()
            + ", data set " + dataApproval.getDataSet().getName()
            + ", COG " + (dataApproval.getCategoryOptionGroup() == null ? "[null]" : dataApproval.getCategoryOptionGroup().getName())
            + ")";

        if ( dataApproval.getCategoryOptionGroup() != null && !securityService.canRead( dataApproval.getCategoryOptionGroup() ) )
        {
            warning += " but couldn't read COG";
        }

        if ( !mayOperate )
        {
            warning += " but couldn't " + operation + " for " + dataApproval.getOrganisationUnit().getName();
        }

        log.warn( warning + "." );
    }
}
