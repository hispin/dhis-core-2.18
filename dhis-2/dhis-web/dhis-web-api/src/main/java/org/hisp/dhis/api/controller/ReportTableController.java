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
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.i18n.I18nManagerException;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.reporttable.ReportTables;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.util.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.hisp.dhis.system.util.CodecUtils.filenameEncode;

@Controller
@RequestMapping( value = ReportTableController.RESOURCE_PATH )
public class ReportTableController
{
    public static final String RESOURCE_PATH = "/reportTables";

    @Autowired
    public ReportTableService reportTableService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private I18nManager i18nManager;

    //-------------------------------------------------------------------------------------------------------
    // GET
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getReportTables( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        ReportTables reportTables = new ReportTables();

        if ( params.hasNoPaging() )
        {
            reportTables.setReportTables( new ArrayList<ReportTable>( reportTableService.getAllReportTables() ) );
        }
        else
        {
            reportTables.setReportTables( new ArrayList<ReportTable>( reportTableService.getAllReportTables() ) );
        }

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( reportTables );
        }

        model.addAttribute( "model", reportTables );

        return "reportTables";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getReportTable( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        ReportTable reportTable = reportTableService.getReportTable( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( reportTable );
        }

        model.addAttribute( "model", reportTable );

        return "reportTable";
    }

    @RequestMapping( value = "/{uid}/data", method = RequestMethod.GET )
    public String getReportTableDATA( @PathVariable( "uid" ) String uid, Model model,
                                      @RequestParam( value = "organisationUnit", required = false ) String organisationUnitUid,
                                      @RequestParam( value = "period", required = false ) String period,
                                      HttpServletResponse response ) throws I18nManagerException, IOException
    {
        if ( organisationUnitUid == null && period == null )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            response.setContentType( "text/plain" );

            PrintWriter writer = new PrintWriter( response.getOutputStream() );
            writer.println( "GRID needs either organisationUnit or period parameter." );
            writer.flush();

            return "grid";
        }

        Date date = period != null ? DateUtils.getMediumDate( period ) : new Date();

        Grid grid = reportTableService.getReportTableGrid( uid, i18nManager.getI18nFormat(), date, organisationUnitUid );

        model.addAttribute( "model", grid );

        return "grid";
    }

    @RequestMapping( value = "/{uid}/data.pdf", method = RequestMethod.GET )
    public void getReportTablePDF( @PathVariable( "uid" ) String uid,
                                   @RequestParam( value = "organisationUnit", required = false ) String organisationUnitUid,
                                   @RequestParam( value = "period", required = false ) String period,
                                   HttpServletResponse response ) throws I18nManagerException, IOException
    {
        if ( organisationUnitUid == null && period == null )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            response.setContentType( "text/plain" );

            PrintWriter writer = new PrintWriter( response.getOutputStream() );
            writer.println( "PDF needs either organisationUnit or period parameter." );
            writer.flush();

            return;
        }

        Date date = period != null ? DateUtils.getMediumDate( period ) : new Date();

        Grid grid = reportTableService.getReportTableGrid( uid, i18nManager.getI18nFormat(), date, organisationUnitUid );

        String filename = filenameEncode( defaultIfEmpty( grid.getTitle(), "Grid" ) ) + ".pdf";
        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PDF, true, filename, false );

        GridUtils.toPdf( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/{uid}/data.xls", method = RequestMethod.GET )
    public void getReportTableXLS( @PathVariable( "uid" ) String uid,
                                   @RequestParam( value = "organisationUnit", required = false ) String organisationUnitUid,
                                   @RequestParam( value = "period", required = false ) String period,
                                   HttpServletResponse response ) throws Exception
    {
        if ( organisationUnitUid == null && period == null )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            response.setContentType( "text/plain" );

            PrintWriter writer = new PrintWriter( response.getOutputStream() );
            writer.println( "XLS needs either organisationUnit or period parameter." );
            writer.flush();

            return;
        }

        Date date = period != null ? DateUtils.getMediumDate( period ) : new Date();

        Grid grid = reportTableService.getReportTableGrid( uid, i18nManager.getI18nFormat(), date, organisationUnitUid );

        String filename = filenameEncode( defaultIfEmpty( grid.getTitle(), "Grid" ) ) + ".xls";
        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_EXCEL, true, filename, false );

        GridUtils.toXls( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/{uid}/data.csv", method = RequestMethod.GET )
    public void getReportTableCSV( @PathVariable( "uid" ) String uid,
                                   @RequestParam( value = "organisationUnit", required = false ) String organisationUnitUid,
                                   @RequestParam( value = "period", required = false ) String period,
                                   HttpServletResponse response ) throws Exception
    {
        if ( organisationUnitUid == null && period == null )
        {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            response.setContentType( "text/plain" );

            PrintWriter writer = new PrintWriter( response.getOutputStream() );
            writer.println( "CSV needs either organisationUnit or period parameter." );
            writer.flush();

            return;
        }

        Date date = period != null ? DateUtils.getMediumDate( period ) : new Date();

        Grid grid = reportTableService.getReportTableGrid( uid, i18nManager.getI18nFormat(), date, organisationUnitUid );

        String filename = filenameEncode( defaultIfEmpty( grid.getTitle(), "Grid" ) ) + ".csv";
        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSV, true, filename, false );

        GridUtils.toCsv( grid, response.getOutputStream() );
    }

    //-------------------------------------------------------------------------------------------------------
    // POST
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @ResponseStatus( value = HttpStatus.CREATED )
    public void postReportTableXML( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @ResponseStatus( value = HttpStatus.CREATED )
    public void postReportTableJSON( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // PUT
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/xml, text/xml"} )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putReportTableXML( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/json"} )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putReportTableJSON( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    //-------------------------------------------------------------------------------------------------------
    // DELETE
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteReportTable( @PathVariable( "uid" ) String uid ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.DELETE.toString() );
    }
}
