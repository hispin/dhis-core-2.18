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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.dxf2.datavalueset.DataValueSet;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.dxf2.datavalueset.DataValueSets;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping( value = DataValueSetController.RESOURCE_PATH )
public class DataValueSetController
{
    public static final String RESOURCE_PATH = "/dataValueSets";

    private static final Log log = LogFactory.getLog( DataValueSetController.class );

    @Autowired
    private DataValueSetService dataValueSetService;

    @RequestMapping( method = RequestMethod.GET )
    public String getDataValueSet( Model model ) throws Exception
    {
        DataValueSets dataValueSets = new DataValueSets();
        dataValueSets.getDataValueSets().add( new DataValueSet() );

        model.addAttribute( "model", dataValueSets );

        return "dataValueSets";
    }
    
    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    public void postDataValueSet( HttpServletResponse response, InputStream input )
        throws IOException
    {
        DataValueSet dataValueSet = JacksonUtils.fromXml( input, DataValueSet.class );
        
        dataValueSetService.saveDataValueSet( dataValueSet );

        log.debug( "Saved data value set for data set: " + dataValueSet.getDataSetIdentifier() +
            ", org unit: " + dataValueSet.getOrganisationUnitIdentifier() + ", period: " + dataValueSet.getPeriodIsoDate() );
        
        ContextUtils.okResponse( response, "Saved data value set succesfully" );
    }

    @ExceptionHandler( IllegalArgumentException.class )
    public void handleException( HttpServletResponse response, IllegalArgumentException ex )
        throws IOException
    {
        response.sendError( HttpServletResponse.SC_CONFLICT, ex.getMessage() );
    }

    /*
    @RequestMapping( value = "/test",  method = RequestMethod.GET )
    public String getDataValueSetTest( Model model ) throws Exception
    {
        DataValueSets dataValueSets = new DataValueSets();
        
        DataValue v1 = new DataValue();
        v1.setDataElement( "de" );
        v1.setValue( "va" );

        DataValue v2 = new DataValue();
        v2.setDataElement( "de" );
        v2.setValue( "va" );
        
        DataValueSet d = new DataValueSet();
        d.setDataSetIdentifier( "ds" );
        d.setOrganisationUnitIdentifier( "ou" );
        d.setPeriodIsoDate( "pe" );
        d.getDataValues().add( v1 );
        d.getDataValues().add( v2 );        
        dataValueSets.getDataValueSets().add( d );

        model.addAttribute( "model", dataValueSets );

        return "dataValueSets";
    }*/    
}
