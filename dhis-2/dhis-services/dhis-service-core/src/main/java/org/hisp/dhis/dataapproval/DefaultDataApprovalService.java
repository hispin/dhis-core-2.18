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
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Jim Grace
 */
@Transactional
public class DefaultDataApprovalService
    implements DataApprovalService
{
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

    public void addDataApproval( DataApproval dataApproval )
    {
        dataApprovalStore.addDataApproval( dataApproval );
    }

    public void deleteDataApproval( DataApproval dataApproval )
    {
        dataApprovalStore.deleteDataApproval( dataApproval );

        for ( OrganisationUnit ancestor : dataApproval.getOrganisationUnit().getAncestors() )
        {
            DataApproval ancestorApproval = dataApprovalStore.getDataApproval(
                    dataApproval.getDataSet(), dataApproval.getPeriod(), ancestor, dataApproval.getCategoryOptionGroup() );

            if ( ancestorApproval != null ) {
                dataApprovalStore.deleteDataApproval ( ancestorApproval );
            }
        }
    }

    public DataApprovalStatus getDataApprovalStatus( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        return getDataApprovalStatus( dataSet, period, organisationUnit, null,
                attributeOptionCombo == null ? null : attributeOptionCombo.getCategoryOptions() );
    }

    public DataApprovalStatus getDataApprovalStatus( DataSet dataSet, Period period,
                                                     OrganisationUnit organisationUnit,
                                                     CategoryOptionGroup categoryOptionGroup,
                                                     Set<DataElementCategoryOption> dataElementCategoryOptions )
    {
        DataApprovalSelection dataApprovalSelection = new DataApprovalSelection( dataSet, period, organisationUnit,
                categoryOptionGroup, dataElementCategoryOptions,
                dataApprovalStore, dataApprovalLevelService,
                categoryService, periodService);

        return dataApprovalSelection.getDataApprovalStatus();
    }

    public DataApprovalPermissions getDataApprovalPermissions( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        return getDataApprovalPermissions( dataSet, period, organisationUnit, null,
                attributeOptionCombo == null ? null : attributeOptionCombo.getCategoryOptions() );
    }

    public DataApprovalPermissions getDataApprovalPermissions( DataSet dataSet, Period period,
                                                     OrganisationUnit organisationUnit,
                                                     CategoryOptionGroup categoryOptionGroup,
                                                     Set<DataElementCategoryOption> dataElementCategoryOptions )
    {
        DataApprovalStatus status = getDataApprovalStatus( dataSet, period,
                organisationUnit, categoryOptionGroup, dataElementCategoryOptions );

        DataApprovalPermissions permissions = new DataApprovalPermissions();

        permissions.setDataApprovalStatus( status );

        if ( canReadCategoryOptionGroups( categoryOptionGroup, dataElementCategoryOptions ) )
        {
            switch ( status.getDataApprovalState() )
            {
                case UNAPPROVED_READY:
                    permissions.setMayApprove( mayApprove( organisationUnit ) );
                    break;

                case APPROVED_HERE:
                    permissions.setMayUnapprove( mayUnapprove( status ) );
                    permissions.setMayAccept( mayAcceptOrUnaccept( status ) );
                    break;

                case ACCEPTED_HERE:
                    permissions.setMayUnapprove( mayUnapprove( status ) );
                    permissions.setMayUnaccept( mayAcceptOrUnaccept( status ) );
                    break;
            }
        }
        return permissions;
    }

    public void accept( DataApproval dataApproval )
    {
        if ( !dataApproval.isAccepted() )
        {
            dataApproval.setAccepted( true );

            dataApprovalStore.updateDataApproval( dataApproval );
        }
    }

    public void unaccept( DataApproval dataApproval )
    {
        if ( dataApproval.isAccepted() )
        {
            dataApproval.setAccepted( false );

            dataApprovalStore.updateDataApproval( dataApproval );
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Return whether the user can read the category option groups (if any)
     * in this data selection. Note that if the user cannot read these groups,
     * they should not have been able to see the data in the first place through
     * the normal webapp, so this test would never fail. So the purpose of this
     * test is to make sure that the web API is not being used to attempt an
     * operation for which the user does not have the security clearance.
     * <p>
     * If category options are specified, then the user must be able to view
     * EVERY category option. The user may view a category option if they
     * have permission to view ANY category option group to which it belongs.
     *
     * @param categoryOptionGroup option groups (if any) for data selection
     * @param dataElementCategoryOptions category options (if any) for data selection
     * @return true if user can read the option groups, else false
     */
    boolean canReadCategoryOptionGroups( CategoryOptionGroup categoryOptionGroup, Set<DataElementCategoryOption> dataElementCategoryOptions)
    {
        if ( categoryOptionGroup != null && !securityService.canRead( categoryOptionGroup ) )
        {
            return false;
        }

        if ( dataElementCategoryOptions != null )
        {
            for ( DataElementCategoryOption option : dataElementCategoryOptions )
            {
                boolean canReadGroup = false;

                for ( CategoryOptionGroup group : option.getGroups() )
                {
                    if ( securityService.canRead( group ) )
                    {
                        canReadGroup = true;

                        break;
                    }

                }

                if ( !canReadGroup )
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks to see whether a user may approve data for a given
     * organisation unit.
     *
     * @param organisationUnit The organisation unit to check for permission.
     * @return true if the user may approve, otherwise false
     */
    private boolean mayApprove( OrganisationUnit organisationUnit )
    {
        User user = currentUserService.getCurrentUser();

        if ( user != null )
        {
            boolean mayApprove = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE );

            if ( mayApprove && user.getOrganisationUnits().contains( organisationUnit ) )
            {
                return true;
            }

            boolean mayApproveAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );

            if ( mayApproveAtLowerLevels && CollectionUtils.containsAny( user.getOrganisationUnits(),
                    organisationUnit.getAncestors() ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to see whether a user may unapprove a given data approval.
     * <p>
     * A user may unapprove data for organisation unit A if they have the
     * authority to approve data for organisation unit B, and B is an
     * ancestor of A.
     * <p>
     * A user may also unapprove data for organisation unit A if they have
     * the authority to approve data for organisation unit A, and A has no
     * ancestors.
     * <p>
     * But a user may not unapprove data for an organisation unit if the data
     * has been approved already at a higher level for the same period and
     * data set, and the user is not authorized to remove that approval as well.
     *
     * @param status The data approval status to check for permission.
     */
    private boolean mayUnapprove( DataApprovalStatus status )
    {
        DataApproval dataApproval = status.getDataApproval();

        if ( dataApproval != null && isAuthorizedToUnapprove( dataApproval.getOrganisationUnit() ) )
        {
            if ( !dataApproval.isAccepted() || mayAcceptOrUnaccept( status ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to see whether a user may accept or unaccept a given data approval.
     *
     * @param status The data approval status to check for permission.
     * @return true if the user may accept or unaccept it, otherwise false.
     */
    private boolean mayAcceptOrUnaccept ( DataApprovalStatus status )
    {
        User user = currentUserService.getCurrentUser();

        DataApproval dataApproval = status.getDataApproval();

        if ( user != null && dataApproval != null )
        {
            boolean mayAcceptAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS );

            if ( mayAcceptAtLowerLevels && CollectionUtils.containsAny( user.getOrganisationUnits(),
                    dataApproval.getOrganisationUnit().getAncestors() ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Tests whether the user is authorized to unapprove for this organisation
     * unit.
     * <p>
     * Whether the user actually may unapprove an existing approval depends
     * also on whether there are higher-level approvals that the user is
     * authorized to unapprove.
     *
     * @param organisationUnit OrganisationUnit to check for approval.
     * @return true if the user may approve, otherwise false
     */
    private boolean isAuthorizedToUnapprove( OrganisationUnit organisationUnit )
    {
        if ( mayApprove( organisationUnit ) )
        {
            return true;
        }

        for ( OrganisationUnit ancestor : organisationUnit.getAncestors() )
        {
            if ( mayApprove( ancestor ) )
            {
                return true;
            }
        }

        return false;
    }
}
