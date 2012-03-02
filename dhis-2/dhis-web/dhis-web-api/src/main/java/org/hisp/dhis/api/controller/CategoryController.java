package org.hisp.dhis.api.controller;

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
import org.hisp.dhis.dataelement.DataElementCategories;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryService;
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
@RequestMapping( value = CategoryController.RESOURCE_PATH )
public class CategoryController
{
    public static final String RESOURCE_PATH = "/categories";

    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    //-------------------------------------------------------------------------------------------------------
    // GET
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getCategories( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        DataElementCategories categories = new DataElementCategories();

        if ( params.isPaging() )
        {
            int total = dataElementCategoryService.getDataElementCategoryCount();

            Pager pager = new Pager( params.getPage(), total );
            categories.setPager( pager );

            List<DataElementCategory> categoryList = new ArrayList<DataElementCategory>(
                dataElementCategoryService.getDataElementCategorysBetween( pager.getOffset(), pager.getPageSize() ) );

            categories.setCategories( categoryList );
        }
        else
        {
            categories.setCategories( new ArrayList<DataElementCategory>( dataElementCategoryService.getAllDataElementCategories() ) );
        }

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( categories );
        }

        model.addAttribute( "model", categories );

        return "categories";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getCategory( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        DataElementCategory category = dataElementCategoryService.getDataElementCategory( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( category );
        }

        model.addAttribute( "model", category );

        return "category";
    }

    //-------------------------------------------------------------------------------------------------------
    // POST
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @ResponseStatus( value = HttpStatus.CREATED )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAELEMENT_ADD')" )
    public void postCategoryXML( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @ResponseStatus( value = HttpStatus.CREATED )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAELEMENT_ADD')" )
    public void postCategoryJSON( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // PUT
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/xml, text/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAELEMENT_UPDATE')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putCategoryXML( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAELEMENT_UPDATE')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putCategoryJSON( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // DELETE
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAELEMENT_DELETE')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteCategory( @PathVariable( "uid" ) String uid ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.DELETE.toString() );
    }
}
