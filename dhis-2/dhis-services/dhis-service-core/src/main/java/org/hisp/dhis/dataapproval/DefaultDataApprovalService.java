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
import org.hisp.dhis.common.ListMap;
import org.hisp.dhis.dataapproval.exceptions.DataMayNotBeAcceptedException;
import org.hisp.dhis.dataapproval.exceptions.DataMayNotBeApprovedException;
import org.hisp.dhis.dataapproval.exceptions.DataMayNotBeUnacceptedException;
import org.hisp.dhis.dataapproval.exceptions.DataMayNotBeUnapprovedException;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.util.CollectionUtils;
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
        log.info( "- approveData ( " + dataApprovalList.size() + " items )" );

        Map<DataApproval, DataApprovalStatus> statusMap = getStatusMap( dataApprovalList );

        for ( DataApproval da : dataApprovalList )
        {
            DataApprovalStatus status = getStatus( da, statusMap );

            if ( status == null || !status.getPermissions().isMayApprove() )
            {
                log.warn( "approveData: data may not be approved, state " + ( status == null ? "(null)" : status.getState().name() ) + " " + da );

                throw new DataMayNotBeApprovedException();
            }

            if ( status.getDataApproval().getDataApprovalLevel() != null )
            {
                da.setDataApprovalLevel( status.getDataApproval().getDataApprovalLevel() );
            }
        }

        for ( DataApproval da : dataApprovalList )
        {
            log.info("-> approving " + da );

            dataApprovalStore.addDataApproval( da );
        }
        
        log.info( "Approvals saved: " + dataApprovalList.size() );
    }

    @Override
    public void unapproveData( List<DataApproval> dataApprovalList )
    {
        log.debug( "- unapproveData ( " + dataApprovalList.size() + " items )" );

        Map<DataApproval, DataApprovalStatus> statusMap = getStatusMap( dataApprovalList );

        for ( DataApproval da : dataApprovalList )
        {
            DataApprovalStatus status = getStatus( da, statusMap );

            if ( status == null || !status.getPermissions().isMayUnapprove() )
            {
                log.warn( "unapproveData: data may not be unapproved, state " + ( status == null ? "(null)" : status.getState().name() ) + " " + da );

                throw new DataMayNotBeUnapprovedException();
            }

            if ( status.getDataApproval().getDataApprovalLevel() != null )
            {
                da.setDataApprovalLevel( status.getDataApproval().getDataApprovalLevel() );
            }

            da.setDataApprovalLevel( status.getDataApproval().getDataApprovalLevel() );
        }

        for ( DataApproval da : dataApprovalList )
        {
            log.debug( "- unapproving " + da );

            DataApproval d = dataApprovalStore.getDataApproval( da.getDataApprovalLevel(), da.getDataSet(), da.getPeriod(), da.getOrganisationUnit(), da.getAttributeOptionCombo() );

            dataApprovalStore.deleteDataApproval( d );
        }
        
        log.info( "Approvals deleted: " + dataApprovalList.size() );
    }

    @Override
    public void acceptData( List<DataApproval> dataApprovalList )
    {
        log.debug( "- acceptData ( " + dataApprovalList.size() + " items )" );

        Map<DataApproval, DataApprovalStatus> statusMap = getStatusMap( dataApprovalList );

        for ( DataApproval da : dataApprovalList )
        {
            DataApprovalStatus status = getStatus( da, statusMap );

            if ( !status.getPermissions().isMayAccept() )
            {
                log.warn( "acceptData: data may not be accepted, state " + ( status == null ? "(null)" : status.getState().name() ) + " " + da );

                throw new DataMayNotBeAcceptedException();
            }

            if ( status.getDataApproval().getDataApprovalLevel() != null )
            {
                da.setDataApprovalLevel( status.getDataApproval().getDataApprovalLevel() );
            }

            da.setDataApprovalLevel( status.getDataApproval().getDataApprovalLevel() );
        }

        for ( DataApproval da : dataApprovalList )
        {
            da.setAccepted( true );

            log.debug( "- accepting " + da );

            DataApproval d = dataApprovalStore.getDataApproval( da.getDataApprovalLevel(), da.getDataSet(), da.getPeriod(), da.getOrganisationUnit(), da.getAttributeOptionCombo() );

            d.setAccepted( true );

            dataApprovalStore.updateDataApproval( d );
        }
        
        log.info( "Accepts saved: " + dataApprovalList.size() );
    }

    @Override
    public void unacceptData( List<DataApproval> dataApprovalList )
    {
        log.debug( "- unacceptData ( " + dataApprovalList.size() + " items )" );

        Map<DataApproval, DataApprovalStatus> statusMap = getStatusMap( dataApprovalList );

        for ( DataApproval da : dataApprovalList )
        {
            DataApprovalStatus status = getStatus( da, statusMap );

            if ( !status.getPermissions().isMayAccept() )
            {
                log.warn( "unacceptData: data may not be unaccepted, state " + ( status == null ? "(null)" : status.getState().name() ) + " " + da );

                throw new DataMayNotBeUnacceptedException();
            }

            if ( status.getDataApproval().getDataApprovalLevel() != null )
            {
                da.setDataApprovalLevel( status.getDataApproval().getDataApprovalLevel() );
            }

            da.setDataApprovalLevel( status.getDataApproval().getDataApprovalLevel() );
        }

        for ( DataApproval da : dataApprovalList )
        {
            log.debug( "- unaccepting " + da );

            DataApproval d = dataApprovalStore.getDataApproval( da.getDataApprovalLevel(), da.getDataSet(), da.getPeriod(), da.getOrganisationUnit(), da.getAttributeOptionCombo() );

            d.setAccepted( false );

            dataApprovalStore.updateDataApproval( da );
        }
        
        log.info( "Accepts deleted: " + dataApprovalList.size() );
    }

    @Override
    public DataApprovalStatus getDataApprovalStatus( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        log.debug( "- getDataApprovalStatus( " + dataSet.getName() + ", "
                + period.getPeriodType().getName() + " " + period.getName() + " " + period + ", "
                + organisationUnit.getName() + ", "
                + ( attributeOptionCombo == null ? "(null)" : attributeOptionCombo.getName() ) + " )" );

        period = periodService.reloadPeriod( period );

        if ( attributeOptionCombo == null )
        {
            attributeOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }

        DataApprovalLevel dal = dataApprovalLevelService.getLowestDataApprovalLevel( organisationUnit, attributeOptionCombo );

        if ( dal == null )
        {
            return new DataApprovalStatus( DataApprovalState.UNAPPROVABLE, null, null, null );
        }

        List<DataApprovalStatus> statuses = dataApprovalStore.getDataApprovals( organisationUnit, CollectionUtils.asSet( dataSet ), period, attributeOptionCombo );

        if ( statuses != null && !statuses.isEmpty() )
        {
            return statuses.get( 0 );
        }

        return null;
    }

    @Override
    public DataApprovalStatus getDataApprovalStatusAndPermissions( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        DataApprovalStatus status = getDataApprovalStatus( dataSet, period, organisationUnit, attributeOptionCombo );

        status.setPermissions( makePermissionsEvaluator().getPermissions( status ) );

        return status;
    }

    @Override
    public List<DataApprovalStatus> getUserDataApprovalsAndPermissions( Set<DataSet> dataSets, Period period, OrganisationUnit orgUnit )
    {
        DataApprovalPermissionsEvaluator permissionsEvaluator = makePermissionsEvaluator();

        List<DataApprovalStatus> statusList = dataApprovalStore.getDataApprovals( orgUnit, dataSets, period, null );
        
        for ( DataApprovalStatus status : statusList )
        {
            status.setPermissions( permissionsEvaluator.getPermissions( status ) );
        }

        return statusList;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private DataApprovalStatus getStatus( DataApproval da, Map<DataApproval, DataApprovalStatus> statusMap )
    {
        return statusMap.get( new DataApproval( null, da.getDataSet(), da.getPeriod(), da.getOrganisationUnit(), da.getAttributeOptionCombo(), false, null, null ) );
    }

    private Map<DataApproval, DataApprovalStatus> getStatusMap( List<DataApproval> dataApprovalList )
    {
        Map<DataApproval, DataApprovalStatus> statusMap = new HashMap<>();

        DataApprovalPermissionsEvaluator evaluator = makePermissionsEvaluator();

        for ( Map.Entry<String, List<DataApproval>> entry : getIndexedMapList( dataApprovalList ).entrySet() )
        {
            Set<DataSet> dataSets = new HashSet<>();

            Period period = entry.getValue().get(0).getPeriod();

            OrganisationUnit orgUnit = entry.getValue().get(0).getOrganisationUnit();

            for ( DataApproval da : entry.getValue() )
            {
                dataSets.add( da.getDataSet() );
            }

            List<DataApprovalStatus> statuses = dataApprovalStore.getDataApprovals( orgUnit, dataSets, period, null );

            for ( DataApprovalStatus status : statuses )
            {
                status.setPermissions( evaluator.getPermissions( status ) );

                DataApproval da = status.getDataApproval();

                for ( DataSet ds : dataSets )
                {
                    statusMap.put( new DataApproval( null, ds, da.getPeriod(), da.getOrganisationUnit(), da.getAttributeOptionCombo(), false, null, null ), status );
                }
            }
        }

        return statusMap;
    }

    private ListMap<String, DataApproval> getIndexedMapList( List<DataApproval> dataApprovalList )
    {
        ListMap<String, DataApproval> map = new ListMap<>();

        for ( DataApproval approval : dataApprovalList )
        {
            String key = approval != null ? approval.getOrganisationUnit().getId() + "-" + approval.getPeriod().getId() : null;
            map.putValue( key, approval );
        }

        return map;
    }

    private DataApprovalPermissionsEvaluator makePermissionsEvaluator()
    {
        return DataApprovalPermissionsEvaluator.makePermissionsEvaluator(
                currentUserService, systemSettingManager, dataApprovalLevelService );
    }
}
