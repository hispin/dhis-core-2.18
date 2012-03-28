package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2011, University of Oslo
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
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.datavalue.DataValueService;
import org.hisp.dhis.dxf2.datavalue.DataValues;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping( value = DataValueController.RESOURCE_PATH )
public class DataValueController
{
    public static final String RESOURCE_PATH = "/dataValues";

    private static final Log log = LogFactory.getLog( DataValueController.class );
    
    @Autowired
    private DataValueService dataValueService;

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    public void postDataValues( HttpServletResponse response, InputStream input )
        throws IOException
    {
        DataValues dataValues = JacksonUtils.fromXml( input, DataValues.class );
        
        dataValueService.saveDataValues( dataValues, IdentifiableObject.IdentifiableProperty.UID );

        log.info( "SIZE " + dataValues.getDataValues().size() );
        
        ContextUtils.okResponse( response, "Data values saved successfully" );
    }
}
