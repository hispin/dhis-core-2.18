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
import org.hisp.dhis.api.view.JacksonUtils;
import org.hisp.dhis.api.view.Jaxb2Utils;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.attribute.Attributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = AttributeTypeController.RESOURCE_PATH )
public class AttributeTypeController
{
    public static final String RESOURCE_PATH = "/attributeTypes";

    @Autowired
    private AttributeService attributeService;

    //-------------------------------------------------------------------------------------------------------
    // GET
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getAttributeTypes( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        Attributes attributes = new Attributes();
        attributes.setAttributes( new ArrayList<Attribute>( attributeService.getAllAttributes() ) );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( attributes );
        }

        model.addAttribute( "model", attributes );

        return "attributeTypes";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getAttributeType( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        Attribute attribute = attributeService.getAttribute( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( attribute );
        }

        model.addAttribute( "model", attribute );

        return "attributeType";
    }

    //-------------------------------------------------------------------------------------------------------
    // POST
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    public void postAttributeTypeXML( HttpServletResponse response, InputStream input ) throws Exception
    {
        Attribute attribute = (Attribute) Jaxb2Utils.unmarshal( Attribute.class, input );
        postAttributeType( attribute, response );
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    public void postAttributeTypeJSON( HttpServletResponse response, InputStream input ) throws Exception
    {
        Attribute attribute = JacksonUtils.readValueAs( Attribute.class, input );
        postAttributeType( attribute, response );
    }

    public void postAttributeType( Attribute attribute, HttpServletResponse response )
    {
        if ( attribute == null )
        {
            response.setStatus( HttpServletResponse.SC_NOT_IMPLEMENTED );
        }
        else
        {
            try
            {
                attributeService.addAttribute( attribute );

                if ( attribute.getUid() == null )
                {
                    response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
                }
                else
                {
                    response.setStatus( HttpServletResponse.SC_CREATED );
                    response.setHeader( "Location", AttributeTypeController.RESOURCE_PATH + "/" + attribute.getUid() );
                }
            } catch ( Exception e )
            {
                response.setStatus( HttpServletResponse.SC_CONFLICT );
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------
    // PUT
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/xml, text/xml"} )
    public void putAttributeTypeXML( @PathVariable( "uid" ) String uid, InputStream input, HttpServletResponse response ) throws Exception
    {
        Attribute updateAttribute = (Attribute) Jaxb2Utils.unmarshal( Attribute.class, input );
        updateAttribute.setUid( uid );
        putAttributeType( updateAttribute, response );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/json"} )
    public void putAttributeTypeJSON( @PathVariable( "uid" ) String uid, InputStream input, HttpServletResponse response ) throws Exception
    {
        Attribute updateAttribute = JacksonUtils.readValueAs( Attribute.class, input );
        updateAttribute.setUid( uid );
        putAttributeType( updateAttribute, response );
    }

    public void putAttributeType( Attribute updatedAttribute, HttpServletResponse response )
    {
        Attribute attribute = attributeService.getAttribute( updatedAttribute.getUid() );

        if ( updatedAttribute == null || attribute == null )
        {
            response.setStatus( HttpServletResponse.SC_NOT_IMPLEMENTED );
        }
        else
        {
            attribute.setName( updatedAttribute.getName() );
            attribute.setCode( updatedAttribute.getCode() );
            attribute.setDataElementAttribute( updatedAttribute.isDataElementAttribute() );
            attribute.setIndicatorAttribute( updatedAttribute.isIndicatorAttribute() );
            attribute.setOrganisationUnitAttribute( updatedAttribute.isOrganisationUnitAttribute() );
            attribute.setMandatory( updatedAttribute.isMandatory() );
            attribute.setAttributeValues( updatedAttribute.getAttributeValues() );

            try
            {
                attributeService.updateAttribute( attribute );

                if ( updatedAttribute.getUid() == null )
                {
                    response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
                }
                else
                {
                    response.setStatus( HttpServletResponse.SC_NO_CONTENT );
                    response.setHeader( "Location", AttributeTypeController.RESOURCE_PATH + "/" + updatedAttribute.getUid() );
                }
            } catch ( Exception e )
            {
                response.setStatus( HttpServletResponse.SC_CONFLICT );
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------
    // DELETE
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    public void deleteAttributeType( @PathVariable( "uid" ) String uid, HttpServletResponse response ) throws Exception
    {
        Attribute attribute = attributeService.getAttribute( uid );

        if ( attribute == null )
        {
            response.setStatus( HttpServletResponse.SC_NOT_FOUND );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_NO_CONTENT );
            attributeService.deleteAttribute( attribute );
        }
    }
}
