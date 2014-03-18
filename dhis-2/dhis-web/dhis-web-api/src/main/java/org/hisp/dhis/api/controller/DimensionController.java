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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.WebUtils;
import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = DimensionController.RESOURCE_PATH)
public class DimensionController
{
    public static final String RESOURCE_PATH = "/dimensions";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DimensionService dimensionService;

    // -------------------------------------------------------------------------
    // Controller
    // -------------------------------------------------------------------------

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public String getDimension( @PathVariable("uid") String uid,
        @RequestParam( value = "links", defaultValue = "true", required = false ) Boolean links, 
        Model model )
    {
        DimensionalObject dimension = dimensionService.getDimension( uid );

        model.addAttribute( "model", dimension );
        model.addAttribute( "viewClass", "dimensional" );

        if ( links )
        {
            WebUtils.generateLinks( dimension );
        }

        return "dimension";
    }
    
    @RequestMapping(value = "/{uid}/items", method = RequestMethod.GET)
    public String getItems( @PathVariable String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response )
    {
        WebOptions options = new WebOptions( parameters );        
        List<NameableObject> items = dimensionService.getCanReadDimensionItems( uid );
                
        WebMetaData metaData = new WebMetaData();
        Collections.sort( items, IdentifiableObjectNameComparator.INSTANCE );
        
        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), items.size(), options.getPageSize() );
            metaData.setPager( pager );
            items = PagerUtils.pageCollection( items, pager );
        }
        
        metaData.setItems( items );

        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return "items";
    }

    @RequestMapping( method = RequestMethod.GET )
    public String getDimensions(
        @RequestParam( value = "links", defaultValue = "true", required = false ) Boolean links, 
        Model model )
    {
        WebMetaData metaData = new WebMetaData();

        metaData.setDimensions( dimensionService.getAllDimensions() );

        model.addAttribute( "model", metaData );

        if ( links )
        {
            WebUtils.generateLinks( metaData );
        }

        return "dimensions";
    }
}
