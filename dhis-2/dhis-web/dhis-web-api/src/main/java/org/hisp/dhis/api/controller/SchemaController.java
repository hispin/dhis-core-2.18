package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.schema.Schema;
import org.hisp.dhis.dxf2.schema.SchemaService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "/schemas", method = RequestMethod.GET )
public class SchemaController
{
    @Autowired
    private SchemaService schemaService;

    @RequestMapping( value = "", method = RequestMethod.GET, produces = { "*/*" } )
    public void getSchemasJson( HttpServletResponse response ) throws IOException
    {
        List<Schema> schemas = schemaService.getSchemas();
        MetaData metaData = new MetaData();
        metaData.setSchemas( schemas );

        JacksonUtils.toJson( response.getOutputStream(), schemas );
    }

    @RequestMapping( value = "/{type}", method = RequestMethod.GET, produces = { "*/*" } )
    public void getSchemaJson( @PathVariable String type, HttpServletResponse response ) throws IOException
    {
        Schema schema = schemaService.getSchemaBySingularName( type );
        JacksonUtils.toJson( response.getOutputStream(), schema );
    }

    @RequestMapping( value = "", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE } )
    public void getSchemasXml( HttpServletResponse response ) throws IOException
    {
        List<Schema> schemas = schemaService.getSchemas();
        MetaData metaData = new MetaData();
        metaData.setSchemas( schemas );

        JacksonUtils.toXml( response.getOutputStream(), metaData );
    }

    @RequestMapping( value = "/{type}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE } )
    public void getSchemaXml( @PathVariable String type, HttpServletResponse response ) throws IOException
    {
        Schema schema = schemaService.getSchemaBySingularName( type );
        JacksonUtils.toXml( response.getOutputStream(), schema );
    }
}
