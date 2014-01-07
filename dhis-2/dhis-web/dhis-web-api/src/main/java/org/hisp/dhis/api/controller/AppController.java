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

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import org.hisp.dhis.api.controller.exception.NotFoundException;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.appmanager.App;
import org.hisp.dhis.appmanager.AppManager;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lars Helge Overland
 */
@Controller
public class AppController
{
    public static final String RESOURCE_PATH = "/apps";

    @Autowired
    private AppManager appManager;

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @RequestMapping( value = RESOURCE_PATH, method = RequestMethod.GET )
    public String getApps( Model model )
    {
        List<App> apps = appManager.getApps();

        model.addAttribute( "model", apps );

        return "apps";
    }

    @RequestMapping( value = "/apps/{app}/**", method = RequestMethod.GET )
    public void renderApp( @PathVariable( "app" ) String app, HttpServletRequest request, HttpServletResponse response ) throws IOException, NotFoundException
    {
        Iterable<Resource> locations = Lists.newArrayList(
            resourceLoader.getResource( "file:" + appManager.getAppFolderPath() + "/" + app + "/" ),
            resourceLoader.getResource( "classpath*:/apps/" + app + "/" )
        );

        Resource manifest = findResource( locations, "manifest.webapp" );

        if ( manifest == null )
        {
            throw new NotFoundException();
        }

        Map<String, Object> manifestMap = JacksonUtils.getJsonMapper().readValue( manifest.getInputStream(), new TypeReference<HashMap<String, Object>>()
        {
        } );

        String defaultPage = (String) manifestMap.get( "launch_path" );
        String pageName = findPage( request.getPathInfo(), app );

        Resource page = findResource( locations, pageName );

        if ( page == null )
        {
            page = findResource( locations, defaultPage );

            if ( page == null )
            {
                throw new NotFoundException();
            }
        }

        String mimeType = request.getSession().getServletContext().getMimeType( page.getFilename() );
        response.setContentType( mimeType );

        StreamUtils.copy( page.getInputStream(), response.getOutputStream() );
    }

    @RequestMapping( value = "/apps/{app}", method = RequestMethod.DELETE )
    public void deleteApp( @PathVariable( "app" ) String app, HttpServletRequest request, HttpServletResponse response ) throws NotFoundException
    {
        if ( !appManager.exists( app ) )
        {
            ContextUtils.notFoundResponse( response, "App does not exist: " + app );
        }

        if ( !appManager.deleteApp( app ) )
        {
            ContextUtils.conflictResponse( response, "There was an error deleting app: " + app );
        }
    }

    //--------------------------------------------------------------------------
    // Helpers
    //--------------------------------------------------------------------------

    private Resource findResource( Iterable<Resource> locations, String resourceName ) throws IOException
    {
        for ( Resource location : locations )
        {
            Resource resource = location.createRelative( resourceName );

            if ( resource.exists() && resource.isReadable() )
            {
                return resource;
            }
        }

        return null;
    }

    private String findPage( String path, String app )
    {
        String prefix = RESOURCE_PATH + "/" + app + "/";

        if ( path.startsWith( prefix ) )
        {
            path = path.substring( prefix.length() );
        }

        return path;
    }
}
