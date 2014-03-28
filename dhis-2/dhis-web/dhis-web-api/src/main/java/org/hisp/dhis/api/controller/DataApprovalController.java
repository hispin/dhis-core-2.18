package org.hisp.dhis.api.controller;

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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.InputUtils;
import org.hisp.dhis.dataapproval.*;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping(value = DataApprovalController.RESOURCE_PATH)
public class DataApprovalController
{
    public static final String RESOURCE_PATH = "/dataApprovals";
    public static final String ACCEPTANCES_PATH = "/acceptances";

    private static final String APPROVAL_STATE = "state";
    private static final String APPROVAL_MAY_APPROVE = "mayApprove";
    private static final String APPROVAL_MAY_UNAPPROVE = "mayUnapprove";
    private static final String APPROVAL_MAY_ACCEPT = "mayAccept";
    private static final String APPROVAL_MAY_UNACCEPT = "mayUnaccept";

    @Autowired
    private DataApprovalService dataApprovalService;

    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataElementCategoryService categoryService;
    
    @Autowired
    private CurrentUserService currentUserService;
    
    @Autowired
    private InputUtils inputUtils;

    @RequestMapping( method = RequestMethod.GET, produces = ContextUtils.CONTENT_TYPE_JSON )
    public void getApprovalState(
        @RequestParam String ds,
        @RequestParam String pe,
        @RequestParam String ou,
        @RequestParam( required = false ) String cc, 
        @RequestParam( required = false ) String cp, HttpServletResponse response ) throws IOException
    {
        DataSet dataSet = dataSetService.getDataSet( ds );
        
        if ( dataSet == null )
        {
            ContextUtils.conflictResponse( response, "Illegal data set identifier: " + ds );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Illegal organisation unit identifier: " + ou );
            return;
        }
        
        DataElementCategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( response, cc, cp );
        
        if ( attributeOptionCombo == null )
        {
            return;
        }
        
        DataApprovalPermissions permissions = dataApprovalService.getDataApprovalPermissions( dataSet, period, organisationUnit, attributeOptionCombo );

        Map<String, Object> approvalState = new HashMap<String, Object>();
        approvalState.put( APPROVAL_STATE, permissions.getDataApprovalStatus().getDataApprovalState().toString() );
        approvalState.put( APPROVAL_MAY_APPROVE, permissions.isMayApprove() );
        approvalState.put( APPROVAL_MAY_UNAPPROVE, permissions.isMayUnapprove() );
        approvalState.put( APPROVAL_MAY_ACCEPT, permissions.isMayAccept() );
        approvalState.put( APPROVAL_MAY_UNACCEPT, permissions.isMayUnaccept() );

        JacksonUtils.toJson( response.getOutputStream(), approvalState );
    }
    
    @PreAuthorize( "hasRole('ALL') or hasRole('F_APPROVE_DATA') or hasRole('F_APPROVE_DATA_LOWER_LEVELS')" )
    @RequestMapping( method = RequestMethod.POST )
    public void saveApproval(
        @RequestParam String ds,
        @RequestParam String pe,
        @RequestParam String ou,
        @RequestParam( required = false ) String cc, 
        @RequestParam( required = false ) String cp, HttpServletResponse response )
    {
        DataSet dataSet = dataSetService.getDataSet( ds );
        
        if ( dataSet == null )
        {
            ContextUtils.conflictResponse( response, "Illegal data set identifier: " + ds );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Illegal organisation unit identifier: " + ou );
            return;
        }
        
        DataElementCategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( response, cc, cp );
        
        if ( attributeOptionCombo == null )
        {
            return;
        }
        
        DataApprovalPermissions permissions = dataApprovalService.getDataApprovalPermissions( dataSet, period, organisationUnit, attributeOptionCombo );
        
        if ( !DataApprovalState.UNAPPROVED_READY.equals( permissions.getDataApprovalStatus().getDataApprovalState() ) )
        {
            ContextUtils.conflictResponse( response, "Data is not ready for approval, current state is: " + permissions.getDataApprovalStatus().getDataApprovalState().name() );
            return;
        }

        if ( !permissions.isMayApprove() )
        {
            ContextUtils.conflictResponse( response, "Current user is not authorized to approve for organisation unit: " + ou );
            return;
        }

        User user = currentUserService.getCurrentUser();

        //TODO: FIX. We need to know what CategoryOptionGroup if any was selected, to use when constructing the data approval object.

        //TODO: FIX. DataApproval approval = new DataApproval( dataSet, period, organisationUnit, attributeOptionGroup, new Date(), user );

        //TODO: FIX. dataApprovalService.addDataApproval( approval );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_APPROVE_DATA') or hasRole('F_APPROVE_DATA_LOWER_LEVELS')" )
    @RequestMapping( method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void removeApproval(
        @RequestParam String ds,
        @RequestParam String pe,
        @RequestParam String ou,
        @RequestParam( required = false ) String cc, 
        @RequestParam( required = false ) String cp, HttpServletResponse response )
    {
        DataSet dataSet = dataSetService.getDataSet( ds );
        
        if ( dataSet == null )
        {
            ContextUtils.conflictResponse( response, "Illegal data set identifier: " + ds );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Illegal organisation unit identifier: " + ou );
            return;
        }
        
        DataElementCategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( response, cc, cp );
        
        if ( attributeOptionCombo == null )
        {
            return;
        }

        DataApprovalPermissions permissions = dataApprovalService.getDataApprovalPermissions( dataSet, period, organisationUnit, attributeOptionCombo );

        if ( !DataApprovalState.APPROVED_HERE.equals( permissions.getDataApprovalStatus().getDataApprovalState() ) )
        {
            ContextUtils.conflictResponse( response, "Data is not approved here and cannot be unapproved" );
            return;
        }

        if ( !permissions.isMayUnapprove() )
        {
            ContextUtils.conflictResponse( response, "Current user is not authorized to unapprove for "
                    + approvalParameters( dataSet, period, organisationUnit, attributeOptionCombo ) );
            return;
        }
        
        dataApprovalService.deleteDataApproval( permissions.getDataApprovalStatus().getDataApproval() );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_ACCEPT_DATA_LOWER_LEVELS')" )
    @RequestMapping( value = ACCEPTANCES_PATH, method = RequestMethod.POST )
    public void acceptApproval(
            @RequestParam String ds,
            @RequestParam String pe,
            @RequestParam String ou,
            @RequestParam( required = false ) String cc,
            @RequestParam( required = false ) String cp, HttpServletResponse response )
    {
        DataSet dataSet = dataSetService.getDataSet( ds );

        if ( dataSet == null )
        {
            ContextUtils.conflictResponse( response, "Illegal data set identifier: " + ds );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Illegal organisation unit identifier: " + ou );
            return;
        }

        DataElementCategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( response, cc, cp );

        if ( attributeOptionCombo == null )
        {
            return;
        }

        DataApprovalPermissions permissions = dataApprovalService.getDataApprovalPermissions( dataSet, period, organisationUnit, attributeOptionCombo );

        if ( !DataApprovalState.APPROVED_HERE.equals( permissions.getDataApprovalStatus().getDataApprovalState() ) )
        {
            ContextUtils.conflictResponse( response, "Data is not approved here, current state is: " + permissions.getDataApprovalStatus().getDataApprovalState().name() );
            return;
        }

        if ( !permissions.isMayAccept() )
        {
            ContextUtils.conflictResponse( response, "Current user is not authorized to accept approval for "
                    + approvalParameters( dataSet, period, organisationUnit, attributeOptionCombo ) );
            return;
        }

        dataApprovalService.accept( permissions.getDataApprovalStatus().getDataApproval() );
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_ACCEPT_DATA_LOWER_LEVELS')" )
    @RequestMapping( value = ACCEPTANCES_PATH, method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void unacceptApproval(
            @RequestParam String ds,
            @RequestParam String pe,
            @RequestParam String ou,
            @RequestParam( required = false ) String cc,
            @RequestParam( required = false ) String cp, HttpServletResponse response )
    {
        DataSet dataSet = dataSetService.getDataSet( ds );

        if ( dataSet == null )
        {
            ContextUtils.conflictResponse( response, "Illegal data set identifier: " + ds );
            return;
        }

        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );

        if ( organisationUnit == null )
        {
            ContextUtils.conflictResponse( response, "Illegal organisation unit identifier: " + ou );
            return;
        }

        DataElementCategoryOptionCombo attributeOptionCombo = inputUtils.getAttributeOptionCombo( response, cc, cp );

        if ( attributeOptionCombo == null )
        {
            return;
        }

        DataApprovalPermissions permissions = dataApprovalService.getDataApprovalPermissions( dataSet, period, organisationUnit, attributeOptionCombo );

        if ( !DataApprovalState.ACCEPTED_HERE.equals( permissions.getDataApprovalStatus().getDataApprovalState() ) )
        {
            ContextUtils.conflictResponse( response, "Data is not approved here, current state is: " + permissions.getDataApprovalStatus().getDataApprovalState().name() );
            return;
        }

        if ( !permissions.isMayUnaccept() )
        {
            ContextUtils.conflictResponse( response, "Current user is not authorized to unaccept approval for "
                    + approvalParameters( dataSet, period, organisationUnit, attributeOptionCombo ) );
            return;
        }

        dataApprovalService.unaccept( permissions.getDataApprovalStatus().getDataApproval() );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String approvalParameters( DataSet dataSet, Period period, OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        return "dataSet " + dataSet.getName()
                + ", period " + period.getName()
                + ", org unit " + organisationUnit.getName() + "(" + organisationUnit.getLevel() + ")"
                + ", attributeOptionCombo " + ( attributeOptionCombo == null ? "null" : attributeOptionCombo.getName() );
    }
}
