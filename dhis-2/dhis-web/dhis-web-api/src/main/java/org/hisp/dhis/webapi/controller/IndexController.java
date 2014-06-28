package org.hisp.dhis.webapi.controller;

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

import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.Resource;
import org.hisp.dhis.webapi.webdomain.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
public class IndexController
{
    @Autowired
    private SchemaService schemaService;

    //--------------------------------------------------------------------------
    // GET
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/api", method = RequestMethod.GET )
    public void getIndex( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        String location = response.encodeRedirectURL( "/" );
        response.sendRedirect( ContextUtils.getRootPath( request ) + location );
    }

    @RequestMapping( value = "/", method = RequestMethod.GET )
    public String getResources( Model model, HttpServletRequest request )
    {
        Resources resources = new Resources();

        for ( Schema schema : schemaService.getSchemas() )
        {
            if ( schema.haveApiEndpoint() )
            {
                Resource resource = new Resource();
                resource.setSingular( schema.getSingular() );
                resource.setPlural( schema.getPlural() );
                resource.setHref( ContextUtils.getRootPath( request ) + schema.getApiEndpoint() );

                resources.getResources().add( resource );
            }
        }

        model.addAttribute( "model", resources );

        return "resources";
    }
}
