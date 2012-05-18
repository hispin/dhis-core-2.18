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

import static org.hisp.dhis.system.util.CodecUtils.filenameEncode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.api.utils.IdentifiableObjectParams;
import org.hisp.dhis.api.utils.WebLinkPopulator;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.reporttable.ReportTables;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.system.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = ReportTableController.RESOURCE_PATH )
public class ReportTableController
{
    public static final String RESOURCE_PATH = "/reportTables";
    
    private static final String DATA_NAME = "data";

    @Autowired
    public ReportTableService reportTableService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private IndicatorService indicatorService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private I18nManager i18nManager;

    @Autowired
    private ContextUtils contextUtils;

    //--------------------------------------------------------------------------
    // GET
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getReportTables( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        ReportTables reportTables = new ReportTables();

        if ( params.isPaging() )
        {
            int total = reportTableService.getReportTableCount();

            Pager pager = new Pager( params.getPage(), total );
            reportTables.setPager( pager );

            List<ReportTable> reportTableList = new ArrayList<ReportTable>(
                reportTableService.getReportTablesBetween( pager.getOffset(), pager.getPageSize() ) );

            reportTables.setReportTables( reportTableList );
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
        model.addAttribute( "view", "detailed" );

        return "reportTable";
    }

    //--------------------------------------------------------------------------
    // GET - Dynamic data
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/data", method = RequestMethod.GET )
    public String getReportTableDynamicData( @RequestParam(required=false, value="in") List<String> indicators,
                                             @RequestParam(required=false, value="de") List<String> dataElements,
                                             @RequestParam(required=false, value="ds") List<String> dataSets,
                                             @RequestParam(value="ou") List<String> orgUnits,
                                             @RequestParam(required=false, value="crosstab") List<String> crossTab,
                                             @RequestParam(required=false) boolean orgUnitIsParent,
                                             @RequestParam(required=false) boolean minimal,
                                             RelativePeriods relatives,
                                             Model model, 
                                             HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableDynamicGrid( indicators, dataElements, dataSets, 
            orgUnits, crossTab, orgUnitIsParent, minimal, relatives, response );
        
        model.addAttribute( "model", grid );
        model.addAttribute( "view", "detailed" );
        
        return grid != null ? "reportTableData" : null;
    }

    @RequestMapping( value = "/data.xml", method = RequestMethod.GET )
    public void getReportTableDynamicDataXml( @RequestParam(required=false, value="in") List<String> indicators,
                                             @RequestParam(required=false, value="de") List<String> dataElements,
                                             @RequestParam(required=false, value="ds") List<String> dataSets,
                                             @RequestParam(value="ou") List<String> orgUnits,
                                             @RequestParam(required=false, value="crosstab") List<String> crossTab,
                                             @RequestParam(required=false) boolean orgUnitIsParent,
                                             @RequestParam(required=false) boolean minimal,
                                             RelativePeriods relatives,
                                             Model model, 
                                             HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableDynamicGrid( indicators, dataElements, dataSets, 
            orgUnits, crossTab, orgUnitIsParent, minimal, relatives, response );

        String filename = DATA_NAME + ".xml";
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, false );

        GridUtils.toXml( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/data.html", method = RequestMethod.GET )
    public void getReportTableDynamicDataHtml( @RequestParam(required=false, value="in") List<String> indicators,
                                             @RequestParam(required=false, value="de") List<String> dataElements,
                                             @RequestParam(required=false, value="ds") List<String> dataSets,
                                             @RequestParam(value="ou") List<String> orgUnits,
                                             @RequestParam(required=false, value="crosstab") List<String> crossTab,
                                             @RequestParam(required=false) boolean orgUnitIsParent,
                                             @RequestParam(required=false) boolean minimal,
                                             RelativePeriods relatives,
                                             Model model, 
                                             HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableDynamicGrid( indicators, dataElements, dataSets, 
            orgUnits, crossTab, orgUnitIsParent, minimal, relatives, response );

        String filename = DATA_NAME + ".html";
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_HTML, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, false );

        GridUtils.toHtml( grid, response.getWriter() );
    }

    @RequestMapping( value = "/data.pdf", method = RequestMethod.GET )
    public void getReportTableDynamicDataPdf( @RequestParam(required=false, value="in") List<String> indicators,
                                             @RequestParam(required=false, value="de") List<String> dataElements,
                                             @RequestParam(required=false, value="ds") List<String> dataSets,
                                             @RequestParam(value="ou") List<String> orgUnits,
                                             @RequestParam(required=false, value="crosstab") List<String> crossTab,
                                             @RequestParam(required=false) boolean orgUnitIsParent,
                                             @RequestParam(required=false) boolean minimal,
                                             RelativePeriods relatives,
                                             Model model, 
                                             HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableDynamicGrid( indicators, dataElements, dataSets, 
            orgUnits, crossTab, orgUnitIsParent, minimal, relatives, response );

        String filename = DATA_NAME + ".pdf";
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PDF, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, false );

        GridUtils.toPdf( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/data.xls", method = RequestMethod.GET )
    public void getReportTableDynamicDataXls( @RequestParam(required=false, value="in") List<String> indicators,
                                             @RequestParam(required=false, value="de") List<String> dataElements,
                                             @RequestParam(required=false, value="ds") List<String> dataSets,
                                             @RequestParam(value="ou") List<String> orgUnits,
                                             @RequestParam(required=false, value="crosstab") List<String> crossTab,
                                             @RequestParam(required=false) boolean orgUnitIsParent,
                                             @RequestParam(required=false) boolean minimal,
                                             RelativePeriods relatives,
                                             Model model, 
                                             HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableDynamicGrid( indicators, dataElements, dataSets, 
            orgUnits, crossTab, orgUnitIsParent, minimal, relatives, response );
        
        String filename = DATA_NAME + ".xls";
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_EXCEL, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, true );

        GridUtils.toXls( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/data.csv", method = RequestMethod.GET )
    public void getReportTableDynamicDataCsv( @RequestParam(required=false, value="in") List<String> indicators,
                                             @RequestParam(required=false, value="de") List<String> dataElements,
                                             @RequestParam(required=false, value="ds") List<String> dataSets,
                                             @RequestParam(value="ou") List<String> orgUnits,
                                             @RequestParam(required=false, value="crosstab") List<String> crossTab,
                                             @RequestParam(required=false) boolean orgUnitIsParent,
                                             @RequestParam(required=false) boolean minimal,
                                             RelativePeriods relatives,
                                             Model model, 
                                             HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableDynamicGrid( indicators, dataElements, dataSets, 
            orgUnits, crossTab, orgUnitIsParent, minimal, relatives, response );
        
        String filename = DATA_NAME + ".csv";
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSV, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, true );

        GridUtils.toCsv( grid, response.getOutputStream() );
    }

    private Grid getReportTableDynamicGrid( List<String> indicators, List<String> dataElements, List<String> dataSets, 
        List<String> orgUnits, List<String> crossTab, boolean orgUnitIsParent, boolean minimal, RelativePeriods relatives, HttpServletResponse response )  throws Exception
    {
        List<Indicator> indicators_ = indicatorService.getIndicatorsByUid( indicators );
        List<DataElement> dataElements_ = dataElementService.getDataElementsByUid( dataElements );
        List<DataSet> dataSets_ = dataSetService.getDataSetsByUid( dataSets );
        List<OrganisationUnit> organisationUnits = organisationUnitService.getOrganisationUnitsByUid( orgUnits );
        
        if ( indicators_.isEmpty() && dataElements_.isEmpty() && dataSets_.isEmpty() )
        {
            ContextUtils.conflictResponse( response, "No valid indicators, data elements or data sets specified" );
            return null;
        }
        
        if ( orgUnitIsParent )
        {
            List<OrganisationUnit> childUnits = new ArrayList<OrganisationUnit>();
            
            for ( OrganisationUnit unit : organisationUnits )
            {
                childUnits.addAll( unit.getChildren() );
            }
            
            organisationUnits = childUnits;
        }
        
        if ( organisationUnits.isEmpty() )
        {
            ContextUtils.conflictResponse( response, "No valid organisation units specified" );
            return null;
        }
        
        ReportTable table = new ReportTable();
        
        table.setIndicators( indicators_ );
        table.setDataElements( dataElements_ );
        table.setUnits( organisationUnits );
        
        table.setDoIndicators( crossTab != null && crossTab.contains( "data" ) );
        table.setDoPeriods( crossTab != null && crossTab.contains( "periods" ) );
        table.setDoUnits( crossTab != null && crossTab.contains( "orgunits" ) );
        
        table.setRelatives( relatives );
        
        Grid grid = reportTableService.getReportTableGrid( table, i18nManager.getI18nFormat(), new Date(), null, minimal );
        
        return grid;
    }
    
    //--------------------------------------------------------------------------
    // GET - Report table data
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}/data", method = RequestMethod.GET )
    public String getReportTableData( @PathVariable( "uid" ) String uid, Model model,
                                      @RequestParam( value = "ou", required = false ) String organisationUnitUid,
                                      @RequestParam( value = "pe", required = false ) String period,
                                      HttpServletResponse response ) throws Exception
    {
        model.addAttribute( "model", getReportTableGrid( uid, organisationUnitUid, period ) );
        model.addAttribute( "view", "detailed" );
        
        return "grid";
    }
    
    @RequestMapping( value = "/{uid}/data.html", method = RequestMethod.GET )
    public void getReportTableHtml( @PathVariable( "uid" ) String uid,
                                   @RequestParam( value = "ou", required = false ) String organisationUnitUid,
                                   @RequestParam( value = "pe", required = false ) String period,
                                   HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableGrid( uid, organisationUnitUid, period );

        String filename = filenameEncode( grid.getTitle() ) + ".html";
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_HTML, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, false );

        GridUtils.toHtml( grid, response.getWriter() );
    }

    @RequestMapping( value = "/{uid}/data.xml", method = RequestMethod.GET )
    public void getReportTableXml( @PathVariable( "uid" ) String uid,
                                   @RequestParam( value = "ou", required = false ) String organisationUnitUid,
                                   @RequestParam( value = "pe", required = false ) String period,
                                   HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableGrid( uid, organisationUnitUid, period );
        
        String filename = filenameEncode( grid.getTitle() ) + ".xml";
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, false );

        GridUtils.toXml( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/{uid}/data.pdf", method = RequestMethod.GET )
    public void getReportTablePdf( @PathVariable( "uid" ) String uid,
                                   @RequestParam( value = "ou", required = false ) String organisationUnitUid,
                                   @RequestParam( value = "pe", required = false ) String period,
                                   HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableGrid( uid, organisationUnitUid, period );

        String filename = filenameEncode( grid.getTitle() ) + ".pdf";
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PDF, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, false );

        GridUtils.toPdf( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/{uid}/data.xls", method = RequestMethod.GET )
    public void getReportTableXls( @PathVariable( "uid" ) String uid,
                                   @RequestParam( value = "ou", required = false ) String organisationUnitUid,
                                   @RequestParam( value = "pe", required = false ) String period,
                                   HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableGrid( uid, organisationUnitUid, period );

        String filename = filenameEncode( grid.getTitle() ) + ".xls";
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_EXCEL, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, true );

        GridUtils.toXls( grid, response.getOutputStream() );
    }

    @RequestMapping( value = "/{uid}/data.csv", method = RequestMethod.GET )
    public void getReportTableCsv( @PathVariable( "uid" ) String uid,
                                   @RequestParam( value = "ou", required = false ) String organisationUnitUid,
                                   @RequestParam( value = "pe", required = false ) String period,
                                   HttpServletResponse response ) throws Exception
    {
        Grid grid = getReportTableGrid( uid, organisationUnitUid, period );

        String filename = filenameEncode( grid.getTitle() ) + ".csv";
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSV, CacheStrategy.RESPECT_SYSTEM_SETTING, filename, true );

        GridUtils.toCsv( grid, response.getOutputStream() );
    }

    private Grid getReportTableGrid( String uid, String organisationUnitUid, String period )
        throws Exception
    {
        ReportTable reportTable = reportTableService.getReportTable( uid );

        if ( organisationUnitUid == null && reportTable.hasReportParams() && reportTable.getReportParams().isOrganisationUnitSet() )
        {
            organisationUnitUid = organisationUnitService.getRootOrganisationUnits().iterator().next().getUid();
        }

        Date date = period != null ? DateUtils.getMediumDate( period ) : new Cal().now().subtract( Calendar.MONTH, 1 ).time();

        Grid grid = reportTableService.getReportTableGrid( uid, i18nManager.getI18nFormat(), date, organisationUnitUid );

        return grid;
    }

    //--------------------------------------------------------------------------
    // POST
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_REPORTTABLE_ADD')" )
    @ResponseStatus( value = HttpStatus.CREATED )
    public void postReportTableXML( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    @RequestMapping( method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_REPORTTABLE_ADD')" )
    @ResponseStatus( value = HttpStatus.CREATED )
    public void postReportTableJSON( HttpServletResponse response, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.POST.toString() );
    }

    //--------------------------------------------------------------------------
    // PUT
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/xml, text/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_REPORTTABLE_ADD')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putReportTableXML( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.PUT, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_REPORTTABLE_ADD')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void putReportTableJSON( @PathVariable( "uid" ) String uid, InputStream input ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.PUT.toString() );
    }

    //--------------------------------------------------------------------------
    // DELETE
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_REPORTTABLE_DELETE')" )
    @ResponseStatus( value = HttpStatus.NO_CONTENT )
    public void deleteReportTable( @PathVariable( "uid" ) String uid ) throws Exception
    {
        throw new HttpRequestMethodNotSupportedException( RequestMethod.DELETE.toString() );
    }
}
