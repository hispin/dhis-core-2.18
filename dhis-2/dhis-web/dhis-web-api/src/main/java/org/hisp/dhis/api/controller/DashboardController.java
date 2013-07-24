package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.dashboard.Dashboard;
import org.hisp.dhis.dashboard.DashboardItem;
import org.hisp.dhis.dashboard.DashboardSearchResult;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = DashboardController.RESOURCE_PATH )
public class DashboardController
    extends AbstractCrudController<Dashboard>
{
    public static final String RESOURCE_PATH = "/dashboards";
    
    @Autowired
    private DashboardService dashboardService;
    
    @RequestMapping( value = "/q/{query}", method = RequestMethod.GET )
    public String search( @PathVariable String query, Model model, HttpServletResponse response ) throws Exception
    {
        DashboardSearchResult result = dashboardService.search( query );
        
        model.addAttribute( "model", result );
        
        return "dashboardSearchResult";
    }
    
    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        Dashboard dashboard = JacksonUtils.fromJson( input, Dashboard.class );
        
        dashboardService.mergeDashboard( dashboard );
        
        dashboardService.saveDashboard( dashboard );
        
        ContextUtils.createdResponse( response, "Dashboard created", RESOURCE_PATH + "/" + dashboard.getUid() );
    }
    
    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, consumes = "application/json" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putJsonObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        Dashboard dashboard = dashboardService.getDashboard( uid );
        
        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + uid );
            return;
        }
        
        Dashboard newDashboard = JacksonUtils.fromJson( input, Dashboard.class );

        dashboard.setName( newDashboard.getName() ); // TODO Name only for now
        
        dashboardService.updateDashboard( dashboard );
    }

    @Override
    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteObject( HttpServletResponse response, HttpServletRequest request, @PathVariable( "uid" ) String uid ) throws Exception
    {
        Dashboard dashboard = dashboardService.getDashboard( uid );

        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + uid );
            return;
        }
        
        dashboardService.deleteDashboard( dashboard );
        
        ContextUtils.okResponse( response, "Dashboard deleted" );
    }
    
    @RequestMapping( value = "/{uid}/items", method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonItem( HttpServletResponse response, HttpServletRequest request, 
        InputStream input, @PathVariable String uid ) throws Exception
    {
        Dashboard dashboard = dashboardService.getDashboard( uid );

        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + uid );
            return;
        }
        
        DashboardItem item = JacksonUtils.fromJson( input, DashboardItem.class );
        
        dashboardService.mergeDashboardItem( item );
        
        dashboard.getItems().add( 0, item );
        
        dashboardService.updateDashboard( dashboard );
        
        ContextUtils.createdResponse( response, "Dashboard item created", item.getUid() );
    }
    
    @RequestMapping( value = "/{dashboardUid}/items/content", method = RequestMethod.POST )
    public void postJsonItemContent( HttpServletResponse response, HttpServletRequest request, 
        @PathVariable String dashboardUid, @RequestParam String type, @RequestParam( "id" ) String contentUid ) throws Exception
    {
        dashboardService.addItemContent( dashboardUid, type, contentUid );
        
        ContextUtils.okResponse( response, "Dashboard item added" );
    }
    
    @RequestMapping( value = "/{dashboardUid}/items/{itemUid}/move", method = RequestMethod.PUT, consumes = "application/json" )
    public void moveItem( HttpServletResponse response, HttpServletRequest request,
        @PathVariable String dashboardUid, @PathVariable String itemUid, @RequestParam int position ) throws Exception
    {
        Dashboard dashboard = dashboardService.getDashboard( dashboardUid );

        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + dashboardUid );
            return;
        }
        
        if ( dashboard.moveItem( itemUid, position ) )
        {        
            dashboardService.updateDashboard( dashboard );
            
            ContextUtils.okResponse( response, "Dashboard item moved" );
        }
    }
    
    @RequestMapping( value = "/{dashboardUid}/items/{itemUid}", method = RequestMethod.DELETE )
    public void deleteItem( HttpServletResponse response, HttpServletRequest request,
        @PathVariable String dashboardUid, @PathVariable String itemUid )
    {
        Dashboard dashboard = dashboardService.getDashboard( dashboardUid );

        if ( dashboard == null )
        {
            ContextUtils.notFoundResponse( response, "Dashboard does not exist: " + dashboardUid );
            return;
        }
        
        if ( dashboard.removeItem( itemUid ) )
        {
            dashboardService.updateDashboard( dashboard );
            
            ContextUtils.okResponse( response, "Dashboard item removed" );
        }        
    }
}
