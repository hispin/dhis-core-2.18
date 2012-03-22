package org.hisp.dhis.api.controller.indicator;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import org.hisp.dhis.api.utils.IdentifiableObjectParams;
import org.hisp.dhis.api.utils.WebLinkPopulator;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroups;
import org.hisp.dhis.indicator.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = IndicatorGroupController.RESOURCE_PATH )
public class IndicatorGroupController
{
    public static final String RESOURCE_PATH = "/indicatorGroups";

    @Autowired
    private IndicatorService indicatorService;

    //-------------------------------------------------------------------------------------------------------
    // GET
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getIndicatorGroups( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        IndicatorGroups indicatorGroups = new IndicatorGroups();

        if ( params.isPaging() )
        {
            int total = indicatorService.getIndicatorGroupCount();

            Pager pager = new Pager( params.getPage(), total );
            indicatorGroups.setPager( pager );

            List<IndicatorGroup> indicatorGroupList = new ArrayList<IndicatorGroup>(
                indicatorService.getIndicatorGroupsBetween( pager.getOffset(), pager.getPageSize() ) );

            indicatorGroups.setIndicatorGroups( indicatorGroupList );
        }
        else
        {
            indicatorGroups.setIndicatorGroups( new ArrayList<IndicatorGroup>( indicatorService.getAllIndicatorGroups() ) );
        }

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( indicatorGroups );
        }

        model.addAttribute( "model", indicatorGroups );

        return "indicatorGroups";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getIndicatorGroup( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        IndicatorGroup indicatorGroup = indicatorService.getIndicatorGroup( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( indicatorGroup );
        }

        model.addAttribute( "model", indicatorGroup );
        model.addAttribute( "view", "detailed" );

        return "indicatorGroup";
    }

    //-------------------------------------------------------------------------------------------------------
    // POST
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_INDICATORGROUP_ADD')" )
    @ResponseStatus( value = HttpStatus.CREATED )
    public void postIndicatorGroupXML( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_INDICATORGROUP_ADD')" )
    @ResponseStatus( value = HttpStatus.CREATED )
    public void postIndicatorGroupJSON( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // PUT
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/xml, text/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_INDICATORGROUP_UPDATE')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putIndicatorGroupXML( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_INDICATORGROUP_UPDATE')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putIndicatorGroupJSON( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // DELETE
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_INDICATORGROUP_DELETE')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteIndicatorGroup( @PathVariable( "uid" ) String uid ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.DELETE.toString() );
    }
}
