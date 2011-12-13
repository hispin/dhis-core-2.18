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

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.IdentifiableObjectParams;
import org.hisp.dhis.api.utils.WebLinkPopulator;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.chart.Charts;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.i18n.I18nManagerException;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = ChartController.RESOURCE_PATH )
public class ChartController
{
    public static final String RESOURCE_PATH = "/charts";

    @Autowired
    private ChartService chartService;

    @Autowired
    private I18nManager i18nManager;

    //-------------------------------------------------------------------------------------------------------
    // GET
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getCharts( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        Charts charts = new Charts();
        charts.setCharts( new ArrayList<Chart>( chartService.getAllCharts() ) );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( charts );
        }

        model.addAttribute( "model", charts );

        return "charts";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getChart( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        Chart chart = chartService.getChart( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( chart );
        }

        model.addAttribute( "model", chart );

        return "chart";
    }

    @RequestMapping( value = "/{uid}.png", method = RequestMethod.GET )
    public void getChartPNG( @PathVariable( "uid" ) String uid, @RequestParam( value = "width", defaultValue = "700", required = false ) int width,
                             @RequestParam( value = "height", defaultValue = "500", required = false ) int height,
                             HttpServletResponse response ) throws IOException, I18nManagerException
    {
        JFreeChart chart = chartService.getJFreeChart( uid, i18nManager.getI18nFormat() );

        response.setContentType( "image/png" );
        ChartUtilities.writeChartAsPNG( response.getOutputStream(), chart, width, height );
    }

    @RequestMapping( value = "/{uid}.jpg", method = RequestMethod.GET )
    public void getChartJPG( @PathVariable( "uid" ) String uid, @RequestParam( value = "width", defaultValue = "700", required = false ) int width,
                             @RequestParam( value = "height", defaultValue = "500", required = false ) int height,
                             HttpServletResponse response ) throws IOException, I18nManagerException
    {
        JFreeChart chart = chartService.getJFreeChart( uid, i18nManager.getI18nFormat() );

        response.setContentType( "image/jpg" );
        ChartUtilities.writeChartAsJPEG( response.getOutputStream(), chart, width, height );
    }
}
