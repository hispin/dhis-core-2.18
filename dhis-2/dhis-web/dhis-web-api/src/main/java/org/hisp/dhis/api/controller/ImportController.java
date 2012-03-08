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

import org.hisp.dhis.api.view.JacksonUtils;
import org.hisp.dhis.api.view.Jaxb2Utils;
import org.hisp.dhis.api.webdomain.DXF2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
public class ImportController
{
    public static final String RESOURCE_PATH = "/import";

    @RequestMapping( value = ImportController.RESOURCE_PATH, method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/*"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_IMPORT')" )
    public void importXml( HttpServletResponse response, HttpServletRequest request ) throws JAXBException, IOException
    {
        DXF2 dxf2 = Jaxb2Utils.unmarshal( DXF2.class, request.getInputStream() );
        print( dxf2 );
    }

    @RequestMapping( value = ImportController.RESOURCE_PATH, method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_IMPORT')" )
    public void importJson( HttpServletResponse response, HttpServletRequest request ) throws IOException
    {
        DXF2 dxf2 = JacksonUtils.readValueAs( DXF2.class, request.getInputStream() );
        print( dxf2 );
    }

    @RequestMapping( value = ImportController.RESOURCE_PATH + ".zip", method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_IMPORT')" )
    public void importZippedXml( HttpServletResponse response, HttpServletRequest request ) throws JAXBException, IOException
    {
        ZipInputStream zip = new ZipInputStream( new BufferedInputStream( request.getInputStream() ) );
        ZipEntry entry = zip.getNextEntry();

        System.err.println( "(xml) Reading from file : " + entry.getName() );

        DXF2 dxf2 = Jaxb2Utils.unmarshal( DXF2.class, zip );
        print( dxf2 );
    }

    @RequestMapping( value = ImportController.RESOURCE_PATH + ".zip", method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_IMPORT')" )
    public void importZippedJson( HttpServletResponse response, HttpServletRequest request ) throws IOException
    {
        ZipInputStream zip = new ZipInputStream( request.getInputStream() );
        ZipEntry entry = zip.getNextEntry();

        System.err.println( "(json) Reading from file : " + entry.getName() );
        DXF2 dxf2 = JacksonUtils.readValueAs( DXF2.class, zip );

        print( dxf2 );
    }

    //-------------------------------------------------------------------------------------------------------
    // Helpers
    //-------------------------------------------------------------------------------------------------------

    // just for debug..
    private void print( DXF2 dxf2 )
    {
        System.err.println( "DataElements: " + dxf2.getDataElements().size() );
        System.err.println( "DataElementGroups: " + dxf2.getDataElementGroups().size() );
        System.err.println( "DataElementGroupSets: " + dxf2.getDataElementGroupSets().size() );

        System.err.println( "DataSets: " + dxf2.getDataSets().size() );

        System.err.println( "Indicators:" + dxf2.getIndicators().size() );
        System.err.println( "IndicatorGroups:" + dxf2.getIndicatorGroups().size() );
        System.err.println( "IndicatorGroupSets:" + dxf2.getIndicatorGroupSets().size() );

        System.err.println( "OrganisationUnits: " + dxf2.getOrganisationUnits().size() );
        System.err.println( "OrganisationUnitGroups: " + dxf2.getOrganisationUnitGroups().size() );
        System.err.println( "OrganisationUnitGroupSets: " + dxf2.getOrganisationUnitGroupSets().size() );
    }
}
