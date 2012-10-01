package org.hisp.dhis.web.mobile.controller;

/*
 * Copyright (c) 2012, University of Oslo
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

import org.hisp.dhis.api.utils.ContextUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
public class MobileController
{
    @RequestMapping( value = "/dhis-web-mobile" )
    public String base()
    {
        return "redirect:/dhis-web-mobile/index";
    }

    @RequestMapping( value = "/" )
    public String baseWithSlash()
    {
        return "redirect:/dhis-web-mobile/index";
    }

    @RequestMapping( value = "/index" )
    public String index( Model model, HttpServletRequest request )
    {
        model.addAttribute( "baseUrl", ContextUtils.getRootPath( request ) );
        model.addAttribute( "page", "index.vm" );

        return "base";
    }

    @RequestMapping( value = "/messages" )
    public String messages( Model model, HttpServletRequest request )
    {
        model.addAttribute( "baseUrl", ContextUtils.getRootPath( request ) );
        model.addAttribute( "page", "messages.vm" );

        return "base";
    }

    @RequestMapping( value = "/messages/new-message" )
    public String newMessage( Model model, HttpServletRequest request )
    {
        model.addAttribute( "baseUrl", ContextUtils.getRootPath( request ) );
        model.addAttribute( "page", "new-message.vm" );

        return "base";
    }

    @RequestMapping( value = "/messages/{uid}" )
    public String message( @PathVariable( "uid" ) String uid, Model model, HttpServletRequest request )
    {
        model.addAttribute( "baseUrl", ContextUtils.getRootPath( request ) );
        model.addAttribute( "page", "message.vm" );
        model.addAttribute( "messageId", uid );

        return "base";
    }

    @RequestMapping( value = "/interpretations" )
    public String interpretations( Model model, HttpServletRequest request )
    {
        model.addAttribute( "baseUrl", ContextUtils.getRootPath( request ) );
        model.addAttribute( "page", "interpretations.vm" );

        return "base";
    }

    @RequestMapping( value = "/settings" )
    public String settings( Model model, HttpServletRequest request )
    {
        model.addAttribute( "baseUrl", ContextUtils.getRootPath( request ) );
        model.addAttribute( "page", "settings.vm" );

        return "base";
    }
}
