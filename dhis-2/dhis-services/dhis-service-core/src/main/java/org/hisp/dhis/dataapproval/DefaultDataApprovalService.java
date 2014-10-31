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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.transaction.annotation.Transactional;


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

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
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

        tracePrint( "checkedList ( " + checkedList.size() + " items )" );

        DataApprovalPermissionsEvaluator permissionsEvaluator = makePermissionsEvaluator();

        for ( Iterator<DataApproval> it = checkedList.iterator(); it.hasNext(); )
        {
            DataApproval da = it.next();

            DataApprovalStatus status = getStatus( da );

            tracePrint("approveData " + da + " -> status " + status.getState().name()
                    + " level " + ( status.getDataApprovalLevel() == null ? "(null)" : status.getDataApprovalLevel().getLevel() ) );

            if ( status.getState().isApproved() && status.getDataApprovalLevel().getLevel() >= da.getDataApprovalLevel().getLevel() )
            {
                tracePrint( "approveData: data already approved." );

                it.remove(); // Already approved at this level, no action needed
            }
            else if ( !status.getState().isApprovable() )
            {
                log.warn("approveData: data is not approvable, state " + status.getState().name() + " " + da );

                throw new DataMayNotBeApprovedException();
            }
//            else if ( !permissionsEvaluator.getPermissions( da, status ).isMayApprove() )
//            {
//                log.warn("approveData: user may not approve data, state " + status.getState().name() + " " + da );
//
//                throw new UserMayNotApproveDataException();
//            }
        }

        for ( DataApproval da : checkedList )
        {
            tracePrint("--> approving level " + da.getDataApprovalLevel().getLevel() + ", " + da.getDataSet().getName() + ", "
                    + da.getPeriod().getName() + ", " + da.getOrganisationUnit().getName() + ", " + da.getAttributeOptionCombo().getName() + ", accepted=" + da.isAccepted()
                    + " (" + da.getDataApprovalLevel().getId() + ", " + da.getDataSet().getId() + ", " + da.getPeriod().getId() + ", "
                    + da.getOrganisationUnit().getId() + ", " + da.getAttributeOptionCombo().getId() + ")" );

            dataApprovalStore.addDataApproval( da );
        }
        
        log.info( "Approvals saved: " + dataApprovalList.size() );
    }

    @Override
    public void unapproveData( List<DataApproval> dataApprovalList )
    {
        tracePrint( "---------------------------------------------------------------------- unapproveData" );
        tracePrint( "unapproveData ( " + dataApprovalList.size() + " items )" );

        List<DataApproval> checkedList = checkApprovalsList( dataApprovalList, null, false );
        List<DataApproval> storedDataApprovals = new ArrayList<>();

        DataApprovalPermissionsEvaluator permissionsEvaluator = makePermissionsEvaluator();

        for ( DataApproval da : checkedList )
        {
            DataApprovalStatus status = getStatus( da );

            tracePrint("unapproveData " + da + " -> status " + status.getState().name()
                    + " level " + ( status.getDataApprovalLevel() == null ? "(null)" : status.getDataApprovalLevel().getLevel() ) );

//            if ( status.getState().isApproved() )
//            {
//                if ( !status.getState().isUnapprovable() )
//                {
//                    log.warn( "unapproveData: data may not be unapproved " + da );
//
//                    throw new DataMayNotBeUnapprovedException();
//                }
//                else if ( !permissionsEvaluator.getPermissions( da, status ).isMayUnapprove() )
//                {
//                    log.warn( "unapproveData: user may not unapprove the data " + da );
//
//                    throw new UserMayNotUnapproveDataException();
//                }

                storedDataApprovals.add ( status.getDataApproval() );
//            }
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
        
        log.info( "Approvals deleted: " + dataApprovalList.size() );
    }

    @Override
    public void acceptData( List<DataApproval> dataApprovalList )
    {
        tracePrint( "---------------------------------------------------------------------- acceptData" );
        tracePrint( "acceptData ( " + dataApprovalList.size() + " items )" );

        List<DataApproval> checkedList = checkApprovalsList( dataApprovalList, null, false );
        List<DataApproval> storedDataApprovals = new ArrayList<>();

        DataApprovalPermissionsEvaluator permissionsEvaluator = makePermissionsEvaluator();

        for ( DataApproval da : checkedList )
        {
            DataApprovalStatus status = getStatus( da );

            tracePrint("acceptData " + da + " -> status " + status.getState().name()
                    + " level " + ( status.getDataApprovalLevel() == null ? "(null)" : status.getDataApprovalLevel().getLevel() ) );

//            if ( !status.getState().isAccepted() )
//            {
//                if ( !status.getState().isAcceptable() )
//                {
//                    log.warn("acceptData: state " + status.getState().name()
//                            + " accepted " + status.getState().isAccepted()
//                            + " acceptable " + status.getState().isAcceptable()
//                            + " " + da );
//
//                    throw new DataMayNotBeAcceptedException();
//                }
//                else if ( !permissionsEvaluator.getPermissions( da, status ).isMayAccept() )
//                {
//                    log.warn( "acceptData: user may not accept the data " + da );
//
//                    throw new UserMayNotAcceptDataException();
//                }

                storedDataApprovals.add( status.getDataApproval() );
//            }
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
        
        log.info( "Accepts saved: " + dataApprovalList.size() );
    }

    @Override
    public void unacceptData( List<DataApproval> dataApprovalList )
    {
        tracePrint( "---------------------------------------------------------------------- unacceptData" );
        tracePrint( "unacceptData ( " + dataApprovalList.size() + " items )" );

        List<DataApproval> checkedList = checkApprovalsList( dataApprovalList, null, false );
        List<DataApproval> storedDataApprovals = new ArrayList<>();

        DataApprovalPermissionsEvaluator permissionsEvaluator = makePermissionsEvaluator();

        for ( DataApproval da : checkedList )
        {
            DataApprovalStatus status = getStatus( da );

            tracePrint("unacceptData " + da + " -> status " + status.getState().name()
                    + " level " + ( status.getDataApprovalLevel() == null ? "(null)" : status.getDataApprovalLevel().getLevel() ) );

//            if ( status.getState().isAccepted() )
//            {
//                if ( !status.getState().isUnacceptable() )
//                {
//                    log.warn("acceptData: state " + status.getState().name()
//                            + " accepted " + status.getState().isAccepted()
//                            + " unacceptable " + status.getState().isUnacceptable()
//                            + " " + da);
//
//                    throw new DataMayNotBeUnacceptedException();
//                }
//                else if ( !permissionsEvaluator.getPermissions( da, status ).isMayUnaccept() )
//                {
//                    log.warn( "unacceptData: user may not unaccept the data " + da );
//
//                    throw new UserMayNotUnacceptDataException();
//                }

                storedDataApprovals.add( status.getDataApproval() );
//            }
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
        
        log.info( "Accepts deleted: " + dataApprovalList.size() );
    }

    @Override
    public DataApprovalStatus getDataApprovalStatus( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        tracePrint( "---------------------------------------------------------------------- getDataApprovalStatus" );

        period = periodService.reloadPeriod( period );
        
        tracePrint( "getDataApprovalStatus( " + dataSet.getName() + ", "
                + period.getPeriodType().getName() + " " + period.getName() + " " + period + ", "
                + organisationUnit.getName() + ", "
                + ( attributeOptionCombo == null ? "(null)" : attributeOptionCombo.getName() ) + " )" );

        if ( attributeOptionCombo == null )
        {
            attributeOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }

        Set<DataElementCategoryOption> attributeCategoryOptions = ( attributeOptionCombo == null || attributeOptionCombo.equals( categoryService.getDefaultDataElementCategoryOptionCombo() ) )
                ? null : attributeOptionCombo.getCategoryOptions();

        DataApprovalStatus status;

        DataApprovalLevel dal = dataApprovalLevelService.getLowestDataApprovalLevel( organisationUnit, attributeOptionCombo );

        if ( dal == null )
        {
            status = new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null, null, null );
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
            status = new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null, null, null );
        }

        status.setDataApproval( defensiveCopy( status.getDataApproval() ) );

        return status;
    }

    @Override
    public DataApprovalStatus getDataApprovalStatusAndPermissions( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        DataApprovalStatus status = getDataApprovalStatus( dataSet, period, organisationUnit, attributeOptionCombo );

        DataApprovalPermissionsEvaluator permissionsEvaluator = makePermissionsEvaluator();

        DataApproval da = ( status.getDataApproval() != null ? status.getDataApproval() :
                new DataApproval( null, dataSet, period, organisationUnit, attributeOptionCombo, false, null, null ) );

        status.setPermissions( permissionsEvaluator.getPermissions( da, status ) );

        return status;
    }

    @Override
    public List<DataApprovalStatus> getUserDataApprovalsAndPermissions( Set<DataSet> dataSets, Period period, OrganisationUnit orgUnit )
    {
        DataApprovalPermissionsEvaluator permissionsEvaluator = makePermissionsEvaluator();

        List<DataApprovalStatus> statusList = dataApprovalStore.getUserDataApprovals( orgUnit, dataSets, period );
        
        for ( DataApprovalStatus status : statusList )
        {
            status.setPermissions( permissionsEvaluator.getPermissions( status.getDataApproval(), status ) );
        }

        return statusList;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void tracePrint( String s ) // Temporary, for development
    {
//        System.out.println( s );
    }

    private DataApprovalPermissionsEvaluator makePermissionsEvaluator()
    {
        return DataApprovalPermissionsEvaluator.makePermissionsEvaluator(
                currentUserService, systemSettingManager, dataApprovalLevelService );
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
        Set<DataSet> dataSets, Set<CategoryOptionGroup> attributeOptionGroups,
        Set<DataElementCategoryOption> attributeOptions, boolean isGetStatus )
    {
        dataApproval.setPeriod( periodService.reloadPeriod( dataApproval.getPeriod() ) );
        
        if ( ( attributeOptionGroups == null || attributeOptionGroups.isEmpty() )
            && ( attributeOptions == null || attributeOptions.isEmpty() ) )
        {
            tracePrint("makeApprovalsList(1) -- calling checkApprovalsList()" );

            return checkApprovalsList( org.hisp.dhis.system.util.CollectionUtils.asList( dataApproval ), dataSets, isGetStatus );
        }

        DataApproval da = checkDataApproval( dataApproval, false );

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

        tracePrint( "checkApprovalsList checking " + dataApprovalList.size() + " items." );

        for ( DataApproval dataApproval : dataApprovalList )
        {
            DataApproval da = checkDataApproval( dataApproval, isGetStatus );

            tracePrint("checkApprovalsList checking " + da );

            if ( !userCanReadAny( da.getAttributeOptionCombo().getCategoryOptions() ) )
            {
                log.warn("checkApprovalsList - user cannot read attribute options from combo " + da );

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

    private DataApproval checkDataApproval( DataApproval dataApproval, boolean includeDataViewOrgUnits )
    {
        DataApproval da = new DataApproval ( dataApproval ); // Defensive copy so we can change it.

        if ( !da.getDataSet().isApproveData() )
        {
            log.warn("checkDataApproval - data set '" + da.getDataSet().getName() + "' is not marked for approval." );

            throw new DataSetNotMarkedForApprovalException();
        }

        if ( da.getAttributeOptionCombo() == null )
        {
            da.setAttributeOptionCombo( categoryService.getDefaultDataElementCategoryOptionCombo() );
        }

        tracePrint( "getDefaultDataElementCategoryOptionCombo() -> " + ( da.getAttributeOptionCombo() == null ? "(null)" : da.getAttributeOptionCombo().getName() ) );

        DataApprovalLevel dal = dataApprovalLevelService.getUserApprovalLevel( da.getOrganisationUnit(), includeDataViewOrgUnits );

        int userLevel = ( dal == null ? 99999 : dal.getLevel() );

        tracePrint( "userLevel ( " + da.getOrganisationUnit().getName() + " ): " + userLevel + ", data approval level " + da.getDataApprovalLevel().getLevel() );

        if ( userLevel > da.getDataApprovalLevel().getLevel() )
        {
            log.warn( "User level " + userLevel + " cannot access approvalLevel "
                    + da.getDataApprovalLevel().getLevel() + " " + da );

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
                log.warn("Period " + da.getPeriod() + " shorter than data set "
                        + da.getDataSet().getName() + " period type " + dataSetPeriodType );

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
}
