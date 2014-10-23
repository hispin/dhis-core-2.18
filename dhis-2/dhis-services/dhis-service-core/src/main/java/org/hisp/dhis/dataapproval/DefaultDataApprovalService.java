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
import org.hisp.dhis.dataapproval.exceptions.DataApprovalException;
import org.hisp.dhis.dataapproval.exceptions.DataMayNotBeAcceptedException;
import org.hisp.dhis.dataapproval.exceptions.DataMayNotBeApprovedException;
import org.hisp.dhis.dataapproval.exceptions.DataMayNotBeUnacceptedException;
import org.hisp.dhis.dataapproval.exceptions.DataMayNotBeUnapprovedException;
import org.hisp.dhis.dataapproval.exceptions.DataSetNotMarkedForApprovalException;
import org.hisp.dhis.dataapproval.exceptions.PeriodShorterThanDataSetPeriodException;
import org.hisp.dhis.dataapproval.exceptions.UserCannotAccessApprovalLevelException;
import org.hisp.dhis.dataapproval.exceptions.UserCannotApproveAttributeComboException;
import org.hisp.dhis.dataapproval.exceptions.UserMayNotAcceptDataException;
import org.hisp.dhis.dataapproval.exceptions.UserMayNotApproveDataException;
import org.hisp.dhis.dataapproval.exceptions.UserMayNotUnacceptDataException;
import org.hisp.dhis.dataapproval.exceptions.UserMayNotUnapproveDataException;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
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

import static org.hisp.dhis.system.util.CollectionUtils.asSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    public void approveData( List<DataApproval> dataApprovalList )
    {
        tracePrint( "---------------------------------------------------------------------- approveData" );
        tracePrint( "approveData ( " + dataApprovalList.size() + " items )" );

        List<DataApproval> checkedList = checkApprovalsList( dataApprovalList, null, false );

        for ( Iterator<DataApproval> it = checkedList.iterator(); it.hasNext(); )
        {
            DataApproval da = it.next();

            DataApprovalStatus status = getStatus( da );

            tracePrint("approveData( level " + da.getDataApprovalLevel().getLevel() + ", " + da.getDataSet().getName() + ", "
                    + da.getPeriod().getName() + ", " + da.getOrganisationUnit().getName() + ", " + da.getAttributeOptionCombo().getName() + " ) -> " + status.getDataApprovalState().name() );

            if ( status.getDataApprovalState().isApproved() && status.getDataApprovalLevel().getLevel() >= da.getDataApprovalLevel().getLevel() )
            {
                it.remove(); // Already approved at this level -- no action needed.
            }
            else if ( !status.getDataApprovalState().isApprovable() )
            {
                throw new DataMayNotBeApprovedException();
            }
            else if ( !mayApprove( da, status ) )
            {
                throw new UserMayNotApproveDataException();
            }
        }

        for ( DataApproval da : checkedList )
        {
            tracePrint("--> approving level " + da.getDataApprovalLevel().getLevel() + ", " + da.getDataSet().getName() + ", "
                    + da.getPeriod().getName() + ", " + da.getOrganisationUnit().getName() + ", " + da.getAttributeOptionCombo().getName() + ", accepted=" + da.isAccepted()
                    + " (" + da.getDataApprovalLevel().getId() + ", " + da.getDataSet().getId() + ", " + da.getPeriod().getId() + ", "
                    + da.getOrganisationUnit().getId() + ", " + da.getAttributeOptionCombo().getId() + ")" );

            dataApprovalStore.addDataApproval( da );
        }
    }

    @Override
    public void unapproveData( List<DataApproval> dataApprovalList )
    {
        tracePrint( "---------------------------------------------------------------------- unapproveData" );
        tracePrint( "unapproveData ( " + dataApprovalList.size() + " items )" );

        List<DataApproval> checkedList = checkApprovalsList( dataApprovalList, null, false );
        List<DataApproval> storedDataApprovals = new ArrayList<>();

        for ( DataApproval da : checkedList )
        {
            DataApprovalStatus status = getStatus( da );

            if ( status.getDataApprovalState().isApproved() )
            {
                if ( !status.getDataApprovalState().isUnapprovable() )
                {
                    throw new DataMayNotBeUnapprovedException();
                }
                else if ( !mayUnapprove( da, status ) )
                {
                    throw new UserMayNotUnapproveDataException();
                }

                storedDataApprovals.add ( status.getDataApproval() );
            }
        }

        for ( DataApproval da : storedDataApprovals )
        {
            tracePrint( "--> unapproving level " + da.getDataApprovalLevel().getLevel() + ", " + da.getDataSet().getName() + ", "
                    + da.getPeriod().getName() + ", " + da.getOrganisationUnit().getName() + ", " + da.getAttributeOptionCombo().getName() + ", accepted=" + da.isAccepted()
                    + " (" + da.getDataApprovalLevel().getId() + ", " + da.getDataSet().getId() + ", " + da.getPeriod().getId() + ", "
                    + da.getOrganisationUnit().getId() + ", " + da.getAttributeOptionCombo().getId() + ")" );

            DataApproval d = dataApprovalStore.getDataApproval( da.getDataApprovalLevel(), da.getDataSet(), da.getPeriod(), da.getOrganisationUnit(), da.getAttributeOptionCombo() );

            dataApprovalStore.deleteDataApproval( d );
        }
    }

    @Override
    public void acceptData( List<DataApproval> dataApprovalList )
    {
        tracePrint( "---------------------------------------------------------------------- acceptData" );
        tracePrint( "acceptData ( " + dataApprovalList.size() + " items )" );

        List<DataApproval> checkedList = checkApprovalsList( dataApprovalList, null, false );
        List<DataApproval> storedDataApprovals = new ArrayList<>();

        for ( DataApproval da : checkedList )
        {
            DataApprovalStatus status = getStatus( da );

            if ( !status.getDataApprovalState().isAccepted() )
            {
                if ( !status.getDataApprovalState().isAcceptable() )
                {
                    tracePrint("acceptData() state " + status.getDataApprovalState().name()
                            + " accepted " + status.getDataApprovalState().isAccepted()
                            + " acceptable " + status.getDataApprovalState().isAcceptable() );

                    throw new DataMayNotBeAcceptedException();
                }
                else if ( !mayAcceptOrUnaccept( da, status ) )
                {
                    throw new UserMayNotAcceptDataException();
                }

                storedDataApprovals.add( status.getDataApproval() );
            }
        }

        for ( DataApproval da : storedDataApprovals )
        {
            da.setAccepted( true );

            tracePrint( "--> accepting level " + da.getDataApprovalLevel().getLevel() + ", " + da.getDataSet().getName() + ", "
                    + da.getPeriod().getName() + ", " + da.getOrganisationUnit().getName() + ", " + da.getAttributeOptionCombo().getName() + ", accepted=" + da.isAccepted()
                    + " (" + da.getDataApprovalLevel().getId() + ", " + da.getDataSet().getId() + ", " + da.getPeriod().getId() + ", "
                    + da.getOrganisationUnit().getId() + ", " + da.getAttributeOptionCombo().getId() + ")" );

            DataApproval d = dataApprovalStore.getDataApproval( da.getDataApprovalLevel(), da.getDataSet(), da.getPeriod(), da.getOrganisationUnit(), da.getAttributeOptionCombo() );

            d.setAccepted( true );

            dataApprovalStore.updateDataApproval( d );
        }
    }

    @Override
    public void unacceptData( List<DataApproval> dataApprovalList )
    {
        tracePrint( "---------------------------------------------------------------------- unacceptData" );
        tracePrint( "unacceptData ( " + dataApprovalList.size() + " items )" );

        List<DataApproval> checkedList = checkApprovalsList( dataApprovalList, null, false );
        List<DataApproval> storedDataApprovals = new ArrayList<>();

        for ( DataApproval da : checkedList )
        {
            DataApprovalStatus status = getStatus( da );

            if ( status.getDataApprovalState().isAccepted() )
            {
                if ( !status.getDataApprovalState().isUnacceptable() )
                {
                    throw new DataMayNotBeUnacceptedException();
                }
                else if ( !mayAcceptOrUnaccept( da, status ) )
                {
                    throw new UserMayNotUnacceptDataException();
                }

                storedDataApprovals.add( status.getDataApproval() );
            }
        }

        for ( DataApproval da : storedDataApprovals )
        {
            da.setAccepted( false );

            tracePrint( "--> unaccepting level " + da.getDataApprovalLevel().getLevel() + ", " + da.getDataSet().getName() + ", "
                    + da.getPeriod().getName() + ", " + da.getOrganisationUnit().getName() + ", " + da.getAttributeOptionCombo().getName() + ", accepted=" + da.isAccepted()
                    + " (" + da.getDataApprovalLevel().getId() + ", " + da.getDataSet().getId() + ", " + da.getPeriod().getId() + ", "
                    + da.getOrganisationUnit().getId() + ", " + da.getAttributeOptionCombo().getId() + ")" );

            DataApproval d = dataApprovalStore.getDataApproval( da.getDataApprovalLevel(), da.getDataSet(), da.getPeriod(), da.getOrganisationUnit(), da.getAttributeOptionCombo() );

            d.setAccepted( false );

            dataApprovalStore.updateDataApproval( da );
        }
    }

    @Override
    public DataApprovalStatus getDataApprovalStatus( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        tracePrint( "---------------------------------------------------------------------- getDataApprovalStatus" );

        tracePrint( "getDataApprovalStatus( " + dataSet.getName() + ", "
                + period.getPeriodType().getName() + " " + period.getName() + " " + period + ", "
                + organisationUnit.getName() + ", "
                + ( attributeOptionCombo == null ? "(null)" : attributeOptionCombo.getName() ) + " )" );

        Set<DataElementCategoryOption> attributeCategoryOptions = ( attributeOptionCombo == null || attributeOptionCombo.equals( categoryService.getDefaultDataElementCategoryOptionCombo() ) )
                ? null : attributeOptionCombo.getCategoryOptions();

        DataApprovalStatus status;

        DataApprovalLevel dal = dataApprovalLevelService.getLowestDataApprovalLevel( organisationUnit, attributeOptionCombo );

        if ( dal == null )
        {
            status = new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null, null );
            return status;
        }

        DataApproval da = new DataApproval( dal, dataSet, period, organisationUnit, attributeOptionCombo, false, null, null );

        try
        {
            List<DataApproval> dataApprovalList = makeApprovalsList( da, null, null, attributeCategoryOptions, true );

            status = doGetDataApprovalStatus( dataApprovalList, da );
        }
        catch ( DataApprovalException ex )
        {
            status = new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null, null );
        }

        status.setDataApproval( defensiveCopy( status.getDataApproval() ) );

        return status;
    }

    @Override
    public DataApprovalStatusAndPermissions getDataApprovalStatusAndPermissions( DataSet dataSet, Period period,
                                    OrganisationUnit organisationUnit, Set<CategoryOptionGroup> categoryOptionGroups,
                                    Set<DataElementCategoryOption> attributeCategoryOptions )
    {
        tracePrint( "---------------------------------------------------------------------- getDataApprovalStatusAndPermissions" );

        tracePrint( "getDataApprovalStatusAndPermissions( " + dataSet.getName() + ", "
                + period.getPeriodType().getName() + " " + period.getName() + " " + period + ", "
                + organisationUnit.getName() + ", "
                + ( categoryOptionGroups == null ? "(null)" : categoryOptionGroups.size() + " categoryOptionGroups" )
                + ( attributeCategoryOptions == null ? "(null)" : attributeCategoryOptions.size() + " attributeCategoryOptions" ) + " )" );

        Set<CategoryOptionGroup> groups = categoryOptionGroups;

        if ( ( groups == null || groups.isEmpty() ) && attributeCategoryOptions != null )
        {
            groups = new HashSet<>();

            for ( DataElementCategoryOption option : attributeCategoryOptions )
            {
                groups.addAll( option.getGroups() );
            }
        }

        DataApprovalLevel dal = dataApprovalLevelService.getHighestDataApprovalLevel( organisationUnit, groups );

        if ( dal == null )
        {
            tracePrint( "Returning UNAPPROVABLE because no approval levels apply." );

            DataApprovalStatusAndPermissions permissions = new DataApprovalStatusAndPermissions();

            permissions.setDataApprovalStatus( new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null, null ) );

            return permissions;
        }

        DataApproval da = checkDataApproval( new DataApproval( dal, dataSet, period, organisationUnit, null, false, null, null ), true, new HashMap<OrganisationUnit, Integer>() );

        DataApprovalStatus status = doGetDataApprovalStatus( makeApprovalsList( da, asSet( dataSet ), categoryOptionGroups, attributeCategoryOptions, true ), da );

        return getPermissions( dal, status, da );
    }

    @Override
    public List<DataApprovalStatusAndPermissions> getUserDataApprovalsAndPermissions( Set<DataSet> dataSets, Period period )
    {
        tracePrint( "---------------------------------------------------------------------- getUserDataApprovalsAndPermissions" );

        List<List<DataApproval>> userDataApprovals = getUserDataApprovals( dataSets, period );

        List<DataApprovalStatusAndPermissions> userDataApprovalsAndPermissions = new ArrayList<>();

        for ( List<DataApproval> dataApprovals : userDataApprovals )
        {
            tracePrint( "getUserDataApprovalsAndPermissions: getting status for " + dataApprovals.iterator().next().getOrganisationUnit().getName()
                    + " - " + dataApprovals.iterator().next().getAttributeOptionCombo().getName() );

            DataApprovalStatus status = doGetDataApprovalStatus( dataApprovals, dataApprovals.iterator().next() );

            DataApprovalStatusAndPermissions permissions = getPermissions( status.getDataApprovalLevel(), status, status.getDataApproval() );

            userDataApprovalsAndPermissions.add( permissions );
        }
        return userDataApprovalsAndPermissions;
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

    private DataApproval defensiveCopy( DataApproval  da )
    {
        return da == null ? null : new DataApproval( da );
    }

    /**
     * Makes a list of DataApprovals from a single, prototype DataApproval
     * object by expanding over the user-specified category option groups
     * and/or category options.
     * <p>
     * If the user specified category option groups, then add every combo
     * that includes at least one option from the specified option group(s).
     * <p>
     * If the user specified category options, then add every combo that
     * includes at least one of the the specified option(s).
     *
     * @param dataApproval the prototype DataApproval object
     * @param dataSets data sets to check for approval, if any
     * @param attributeOptionGroups attribute option groups, if any
     * @param attributeOptions attribute options, if any
     * @param isGetStatus true if get, false if action
     * @return list of DataApprovals
     */
    private List<DataApproval> makeApprovalsList( DataApproval dataApproval,
                                                  Set<DataSet> dataSets,
                                                  Set<CategoryOptionGroup> attributeOptionGroups,
                                                  Set<DataElementCategoryOption> attributeOptions,
                                                  boolean isGetStatus )
    {
        if ( ( attributeOptionGroups == null || attributeOptionGroups.isEmpty() )
            && ( attributeOptions == null || attributeOptions.isEmpty() ) )
        {
            tracePrint("makeApprovalsList(1) -- calling checkApprovalsList()" );

            return checkApprovalsList( org.hisp.dhis.system.util.CollectionUtils.asList( dataApproval ), dataSets, isGetStatus );
        }

        DataApproval da = checkDataApproval( dataApproval, false, new HashMap<OrganisationUnit, Integer>() );

        tracePrint("makeApprovalsList(2) combo - " + ( da.getAttributeOptionCombo() == null ? "(null)" : da.getAttributeOptionCombo().getName() ) );

        if ( isGetStatus ) // For getStatus, patch approval level back into the (constructed) original.
        {
            dataApproval.setDataApprovalLevel( da.getDataApprovalLevel() );
        }

        Set<DataElementCategoryOption> options = optionsFromAllOptionGroups( da, attributeOptionGroups );

        if ( attributeOptions != null )
        {
            options.addAll( attributeOptions );
        }

        Set<DataElementCategoryOptionCombo> combos = new HashSet<>();

        for ( DataElementCategoryOption option : options )
        {
            combos.addAll( option.getCategoryOptionCombos() );
        }

        List<DataApproval> daList = new ArrayList<>();

        DataApproval daPrototype = new DataApproval( da ); // Defensive copy

        for ( DataElementCategoryOptionCombo combo : combos )
        {
            daPrototype.setAttributeOptionCombo( combo );

            daList.add( new DataApproval( daPrototype ) );
        }

        return expandApprovalsList( daList, dataSets );
    }

    private Set<DataElementCategoryOption> optionsFromAllOptionGroups( DataApproval dataApproval,
                                                                       Set<CategoryOptionGroup> attributeOptionGroups )
    {
        Set<DataElementCategoryOption> options = new HashSet<>();

        if ( attributeOptionGroups == null )
        {
            return options;
        }

        Iterator<CategoryOptionGroup> it = attributeOptionGroups.iterator();

        if ( it.hasNext() )
        {
            for ( DataElementCategoryOption co : it.next().getMembers() )
            {
                if ( co.includes( dataApproval.getPeriod() ) && co.includes( dataApproval.getOrganisationUnit() ) )
                {
                    options.add( co );
                }
            }
        }

        while ( it.hasNext() )
        {
            options.retainAll( it.next().getMembers() );
        }

        return options;
    }

    private List<DataApproval> checkApprovalsList( List<DataApproval> dataApprovalList, Set<DataSet> dataSets, boolean isGetStatus )
    {
        List<DataApproval> daList = new ArrayList<>();

        Map<OrganisationUnit, Integer> approvalLevelCache = new HashMap<>();

        for ( DataApproval dataApproval : dataApprovalList )
        {
            DataApproval da = checkDataApproval( dataApproval, isGetStatus, approvalLevelCache );

            tracePrint("checkApprovalsList(1) combo - " + ( da.getAttributeOptionCombo() == null ? "(null)" : da.getAttributeOptionCombo().getName() ) );

            if ( !userCanReadAny( da.getAttributeOptionCombo().getCategoryOptions() ) )
            {
                throw new UserCannotApproveAttributeComboException();
            }

            daList.add( da );
        }

        return expandApprovalsList( dataApprovalList, dataSets );
    }

    private boolean userCanReadAny ( Set<DataElementCategoryOption> options )
    {
        for ( DataElementCategoryOption option : options )
        {
            if ( securityService.canRead( option ) )
            {
                return true;
            }
        }

        return false;
    }

    private DataApproval checkDataApproval( DataApproval dataApproval, boolean includeDataViewOrgUnits, Map<OrganisationUnit, Integer> approvalLevelCache )
    {
        DataApproval da = new DataApproval ( dataApproval ); // Defensive copy so we can change it.

        if ( !da.getDataSet().isApproveData() )
        {
            throw new DataSetNotMarkedForApprovalException();
        }

        if ( da.getAttributeOptionCombo() == null )
        {
            da.setAttributeOptionCombo( categoryService.getDefaultDataElementCategoryOptionCombo() );

            tracePrint( "getDefaultDataElementCategoryOptionCombo() -> " + ( da.getAttributeOptionCombo() == null ? "(null)" : da.getAttributeOptionCombo().getName() ) );
        }

        Integer userLevel = approvalLevelCache.get( da.getOrganisationUnit() );

        if ( userLevel == null )
        {
            DataApprovalLevel dal = dataApprovalLevelService.getUserApprovalLevel( da.getOrganisationUnit(), includeDataViewOrgUnits );
            if ( dal == null )
            {
                userLevel = 99999;
            }
            else
            {
                userLevel = dal.getLevel();
            }
            approvalLevelCache.put( da.getOrganisationUnit(), userLevel );
        }

        tracePrint( "userLevel ( " + da.getOrganisationUnit().getName() + " ): " + userLevel + ", data approval level " + da.getDataApprovalLevel().getLevel() );
        log.info( "userLevel ( " + da.getOrganisationUnit().getName() + " ): " + userLevel );

        if ( userLevel > da.getDataApprovalLevel().getLevel() )
        {
            throw new UserCannotAccessApprovalLevelException();
        }

        return da;
    }

    private List<DataApproval>expandApprovalsList ( List<DataApproval> approvalsList, Set<DataSet> dataSets )
    {
        return expandPeriods( expandDataSets( approvalsList, dataSets ) );
    }

    private List<DataApproval> expandDataSets ( List<DataApproval> approvalsList, Set<DataSet> dataSets )
    {
        List<DataApproval> returnList = approvalsList;

        if ( dataSets != null )
        {
            returnList = new ArrayList<>();

            for ( DataApproval da : approvalsList )
            {
                for ( DataSet set : dataSets )
                {
                    da.setDataSet( set );

                    returnList.add( new DataApproval( da ) );
                }
            }
        }

        return returnList;
    }

    private List<DataApproval> expandPeriods ( List<DataApproval> approvalsList )
    {
        List<DataApproval> expandedApprovals = new ArrayList<>();

        for ( DataApproval da : approvalsList )
        {
            PeriodType selectedPeriodType = da.getPeriod().getPeriodType();
            PeriodType dataSetPeriodType = da.getDataSet().getPeriodType();

            if ( selectedPeriodType.equals( dataSetPeriodType ) )
            {
                expandedApprovals.add( da ); // No expansion needed.
            }
            else if ( selectedPeriodType.getFrequencyOrder() <= dataSetPeriodType.getFrequencyOrder() )
            {
                throw new PeriodShorterThanDataSetPeriodException();
            }
            else
            {
                Collection<Period> periods = periodService.getPeriodsBetweenDates(
                        dataSetPeriodType,
                        da.getPeriod().getStartDate(),
                        da.getPeriod().getEndDate() );

                for ( Period period : periods )
                {
                    DataApproval periodDataApproval = new DataApproval( da );

                    periodDataApproval.setPeriod( period );

                    expandedApprovals.add( periodDataApproval );
                }
            }
        }

        return expandedApprovals;
    }

    private boolean mayApprove( DataApproval dataApproval, DataApprovalStatus dataApprovalStatus )
    {
        DataApprovalLevel userDal = dataApprovalLevelService.getUserApprovalLevel( dataApproval.getOrganisationUnit(), false );

        if ( userDal != null )
        {
            User user = currentUserService.getCurrentUser();

            boolean mayApprove = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE );
            boolean mayApproveAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );

            if ( ( mayApprove && userDal.getLevel() == dataApprovalStatus.getDataApprovalLevel().getLevel() )
                || ( mayApproveAtLowerLevels && userDal.getLevel() < dataApprovalStatus.getDataApprovalLevel().getLevel() ) )
            {
                tracePrint("mayApprove TRUE user level " + userDal.getLevel() + ", mayApprove " + mayApprove + ", mayApproveAtLL "
                        + mayApproveAtLowerLevels + ", data level " + dataApprovalStatus.getDataApprovalLevel().getLevel() );
                return true;
            }

            tracePrint("mayApprove FALSE user level " + userDal.getLevel() + ", mayApprove " + mayApprove + ", mayApproveAtLL "
                    + mayApproveAtLowerLevels + ", data level " + dataApprovalStatus.getDataApprovalLevel().getLevel() );
        }
        else
        {
            tracePrint("mayApprove FALSE, no user level" );
        }

        return false;
    }

    private boolean mayUnapprove( DataApproval dataApproval, DataApprovalStatus dataApprovalStatus )
    {
        return mayApprove( dataApproval, dataApprovalStatus ) || mayAcceptOrUnaccept( dataApproval, dataApprovalStatus );
    }

    private boolean mayAcceptOrUnaccept( DataApproval dataApproval, DataApprovalStatus dataApprovalStatus )
    {
        DataApprovalLevel userDal = dataApprovalLevelService.getUserApprovalLevel( dataApproval.getOrganisationUnit(), false );

        if ( userDal != null )
        {
            User user = currentUserService.getCurrentUser();

            boolean mayAcceptAtLowerLevels = user.getUserCredentials().isAuthorized( DataApproval.AUTH_ACCEPT_LOWER_LEVELS )
                    || user.getUserCredentials().isAuthorized( DataApproval.AUTH_APPROVE_LOWER_LEVELS );

            if ( mayAcceptAtLowerLevels && userDal.getLevel() < dataApprovalStatus.getDataApprovalLevel().getLevel() )
            {
                return true;
            }
        }

        return false;
    }

    private DataApprovalStatus getStatus( DataApproval dataApproval )
    {
        return doGetDataApprovalStatus( org.hisp.dhis.system.util.CollectionUtils.asList( dataApproval ), dataApproval );
    }

    private DataApprovalStatus doGetDataApprovalStatus( List<DataApproval> dataApprovals, DataApproval originalDataApproval )
    {
        DataApprovalSelection dataApprovalSelection = new DataApprovalSelection( dataApprovals, originalDataApproval,
                dataApprovalStore, dataApprovalLevelService, organisationUnitService, categoryService );

        return dataApprovalSelection.getDataApprovalStatus();
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
     * Find permissions, and add to returned status.
     *
     * @param dal Data Approval Level we were looking for
     * @param status Approval Status that we found
     * @param da Original Data Approval describing what we were looking for
     * @return Permissions along with status
     */
    private DataApprovalStatusAndPermissions getPermissions( DataApprovalLevel dal, DataApprovalStatus status, DataApproval da )
    {
        DataApprovalStatusAndPermissions permissions = new DataApprovalStatusAndPermissions();

        permissions.setDataApprovalStatus( status );

        tracePrint( "getPermissions - dal " + ( dal == null ? "(null)" : dal.getName() )
                + " dataApproval null? " + ( status.getDataApproval() == null ) );

        if ( dal != null && securityService.canRead( dal ) && status.getDataApproval() != null
                && ( dal.getCategoryOptionGroupSet() == null || securityService.canRead( dal.getCategoryOptionGroupSet() ) ) )
        {
            DataApprovalState state = status.getDataApprovalState();

            tracePrint( "getPermissions - state is " + state.name() );

            permissions.setMayApprove( state.isApprovable() && mayApprove( da, status ) );
            permissions.setMayUnapprove( state.isUnapprovable() && mayUnapprove( status.getDataApproval(), status ) );
            permissions.setMayAccept( state.isAcceptable() && mayAcceptOrUnaccept( status.getDataApproval(), status ) );
            permissions.setMayUnaccept( state.isUnacceptable() && mayAcceptOrUnaccept( status.getDataApproval(), status ) );
        }

        log.debug( "Found permissions for " + da.getOrganisationUnit().getName()
                + " " + status.getDataApprovalState().name()
                + " may approve = " + permissions.isMayApprove()
                + " may unapprove = " + permissions.isMayUnapprove()
                + " may accept = " + permissions.isMayAccept()
                + " may unaccept = " + permissions.isMayUnaccept() );

        status.setDataApproval( defensiveCopy( status.getDataApproval() ) );

        return permissions;
    }

    /**
     * Returns a list of data approval object lists. Each of the inner lists
     * describes an entity that the user may approve -- a pair of category
     * option combo and organisation unit. The different members of the inner
     * list have the different data sets.
     *
     * @param dataSets
     * @param period
     * @return
     */
    private List<List<DataApproval>> getUserDataApprovals( Set<DataSet> dataSets, Period period )
    {
        Map<DataElementCategoryCombo, Set<DataSet>> categoryCombos = getCateogyCombosFromDataSets( dataSets );

        Set<OrganisationUnit> userOrgUnits = currentUserService.getCurrentUser().getOrganisationUnits();

        List<List<DataApproval>> userDataApprovals = new ArrayList<>();

        for ( Map.Entry<DataElementCategoryCombo, Set<DataSet>> entry : categoryCombos.entrySet() )
        {
            for ( DataElementCategoryOption option : entry.getKey().getCategoryOptions() )
            {
                if ( option.includesAny( userOrgUnits ) && option.includes( period ) && securityService.canRead( option ) )
                {
                    for ( DataElementCategoryOptionCombo optionCombo : option.getCategoryOptionCombos() )
                    {
                        tracePrint( "getUserDataApprovals: setting up combo " + optionCombo.getName() );

                        userDataApprovals.addAll( getOptionComboDataApprovals( optionCombo, entry.getValue(), period ) );
                    }
                }
            }
        }

        return userDataApprovals;
    }

    private List<List<DataApproval>> getOptionComboDataApprovals( DataElementCategoryOptionCombo optionCombo, Set<DataSet> dataSets, Period period )
    {
        Collection<OrganisationUnit> orgUnits = null;

        DataApprovalLevel lowestOptionLevel = null;

        for ( DataElementCategoryOption option : optionCombo.getCategoryOptions() )
        {
            if ( !CollectionUtils.isEmpty( option.getOrganisationUnits() ) )
            {
                if ( orgUnits == null )
                {
                    orgUnits = option.getOrganisationUnits();
                }
                else
                {
                    orgUnits = CollectionUtils.intersection( orgUnits, option.getOrganisationUnits() );
                }
            }

            DataApprovalLevel optionApprovalLevel = dataApprovalLevelService.getLowestOptionApprovalLevel( option );

            if ( lowestOptionLevel == null || ( optionApprovalLevel != null && optionApprovalLevel.getLevel() > lowestOptionLevel.getLevel() ) )
            {
                lowestOptionLevel = optionApprovalLevel;
            }
        }

        if ( lowestOptionLevel == null )
        {
            return new ArrayList<>();
        }

        Set<OrganisationUnit> userOrgUnitsAtLevel = getUserOrgUnitsAtLevel( lowestOptionLevel.getOrgUnitLevel() );

        orgUnits = CollectionUtils.intersection( orgUnits, userOrgUnitsAtLevel );

        List<List<DataApproval>> optionComboDataApprovals = new ArrayList<>();

        for ( OrganisationUnit orgUnit : orgUnits )
        {
            List<DataApproval> approvals = new ArrayList<>();

            for ( DataSet ds : dataSets )
            {
                DataApproval da = new DataApproval( null, ds, period, orgUnit, optionCombo, false, null, null );

                approvals.add( da );
            }

            optionComboDataApprovals.add( approvals );
        }

        return optionComboDataApprovals;
    }

    private Set<OrganisationUnit> getUserOrgUnitsAtLevel( int level )
    {
        Set<OrganisationUnit> userOrgUnitsAtLevel = new HashSet<>();

        for ( OrganisationUnit orgUnit : currentUserService.getCurrentUser().getOrganisationUnits() )
        {
            userOrgUnitsAtLevel.addAll( getOrgUnitsAtLevel( orgUnit, level ) );
        }

        return userOrgUnitsAtLevel;
    }

    private Set<OrganisationUnit> getOrgUnitsAtLevel( OrganisationUnit orgUnit, int level )
    {
        int orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );

        if ( orgUnitLevel == level )
        {
            return asSet( orgUnit );
        }
        else if ( orgUnitLevel > level )
        {
            return new HashSet<>();
        }

        Set<OrganisationUnit> orgUnitsAtLevel = new HashSet<>();

        for ( OrganisationUnit child : orgUnit.getChildren() )
        {
            orgUnitsAtLevel.addAll( getOrgUnitsAtLevel( child, level ) );
        }

        return orgUnitsAtLevel;
    }

    private Map<DataElementCategoryCombo, Set<DataSet>> getCateogyCombosFromDataSets( Set<DataSet> dataSets )
    {
        Map<DataElementCategoryCombo, Set<DataSet>> categoryCombos = new HashMap<>();

        for ( DataSet dataSet : dataSets )
        {
            Set<DataSet> catComboDataSets = categoryCombos.get( dataSet.getCategoryCombo() );

            if ( catComboDataSets == null )
            {
                catComboDataSets = new HashSet<>();
                categoryCombos.put( dataSet.getCategoryCombo(), catComboDataSets );
            }
            catComboDataSets.add( dataSet );
        }

        return categoryCombos;
    }

    //TODO: remove below if we find we don't need it.

    /**
     * Return true if there are no category option groups, or if the user
     * can read any category option group from the collection.
     *
     * @param categoryOptionGroups option groups (if any) for data selection
     * @return true if at most 1 option group and user can read, else false
     */
    /*
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
    */

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
    /*
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
    */

    /**
     * Checks to see whether a user may accept or unaccept an approval.
     * (For this, they will need access to the next higher approval level.)
     *
     * @param dataApprovalLevel This data approval level.
     * @param organisationUnit  The organisation unit to check for permission.
     * @return true if the user may accept or unaccept, otherwise false.
     */
    /*
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
    */

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
    /*
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
    */
}
