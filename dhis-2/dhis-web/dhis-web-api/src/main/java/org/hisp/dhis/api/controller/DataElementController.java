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
import org.hisp.dhis.api.utils.WebLinkPopulatorListener;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.DataElements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "/dataElements" )
public class DataElementController
{
    @Autowired
    private DataElementService dataElementService;

    @RequestMapping( method = RequestMethod.GET )
    public String getDataElements( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        DataElements dataElements = new DataElements();
        dataElements.setDataElements( new ArrayList<DataElement>( dataElementService.getAllActiveDataElements() ) );

        if ( params.hasLinks() )
        {
            WebLinkPopulatorListener listener = new WebLinkPopulatorListener( request );
            listener.beforeMarshal( dataElements );
        }

        model.addAttribute( "model", dataElements );

        return "dataElements";
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @ResponseStatus( value = HttpStatus.CREATED )
    public void postDataElementXML( HttpServletResponse response, InputStream input ) throws Exception
    {
        System.err.println( "POST request on DataElement using XML." );

        // response.setHeader("Location", "/spittles/" + spittle.getId());
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @ResponseStatus( value = HttpStatus.CREATED )
    public void postDataElementJSON( HttpServletResponse response, InputStream input ) throws Exception
    {
        System.err.println( "POST request on DataElement using JSON." );

        // response.setHeader("Location", "/spittles/" + spittle.getId());
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getDataElement( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        DataElement dataElement = dataElementService.getDataElement( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulatorListener listener = new WebLinkPopulatorListener( request );
            listener.beforeMarshal( dataElement );
        }

        model.addAttribute( "model", dataElement );

        return "dataElement";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteDataElement( @PathVariable( "uid" ) String uid )
    {
        System.err.println( "DELETE request on DataElement with UID = " + uid );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/xml, text/xml"} )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putDataElementXML( @PathVariable( "uid" ) String uid, InputStream input )
    {
        System.err.println( "PUT request on DataElement with UID = " + uid + " using XML." );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/json"} )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putDataElementJSON( @PathVariable( "uid" ) String uid, InputStream input )
    {
        System.err.println( "PUT request on DataElement with UID = " + uid + " using JSON." );
    }
}
