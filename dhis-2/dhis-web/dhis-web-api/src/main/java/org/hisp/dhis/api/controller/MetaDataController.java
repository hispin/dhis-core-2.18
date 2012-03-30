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

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dxf2.metadata.DXF2;
import org.hisp.dhis.dxf2.metadata.ExportOptions;
import org.hisp.dhis.dxf2.metadata.ExportService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
public class MetaDataController
{
    public static final String RESOURCE_PATH = "/metaData";

    @Autowired
    private ExportService exportService;

    //-------------------------------------------------------------------------------------------------------
    // Export
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = MetaDataController.RESOURCE_PATH, method = RequestMethod.GET )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public String export( ExportOptions exportOptions, Model model )
    {
        DXF2 dxf2 = exportService.getMetaDataWithExportOptions( exportOptions );

        model.addAttribute( "model", dxf2 );
        model.addAttribute( "view", "export" );

        return "export";
    }

    @RequestMapping( value = MetaDataController.RESOURCE_PATH + ".zip", method = RequestMethod.GET, headers = {"Accept=application/xml, text/*"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedXML( ExportOptions exportOptions, HttpServletResponse response ) throws IOException, JAXBException
    {
        DXF2 dxf2 = exportService.getMetaDataWithExportOptions( exportOptions );

        response.setContentType( ContextUtils.CONTENT_TYPE_ZIP );
        response.addHeader( "Content-Disposition", "attachment; filename=\"export.xml.zip\"" );
        response.addHeader( "Content-Transfer-Encoding", "binary" );

        ZipOutputStream zip = new ZipOutputStream( response.getOutputStream() );
        zip.putNextEntry( new ZipEntry( "export.xml" ) );

        JacksonUtils.toXmlWithView( zip, dxf2, ExportView.class );
    }

    @RequestMapping( value = MetaDataController.RESOURCE_PATH + ".zip", method = RequestMethod.GET, headers = {"Accept=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedJSON( ExportOptions exportOptions, HttpServletResponse response ) throws IOException, JAXBException
    {
        DXF2 dxf2 = exportService.getMetaDataWithExportOptions( exportOptions );

        response.setContentType( ContextUtils.CONTENT_TYPE_ZIP );
        response.addHeader( "Content-Disposition", "attachment; filename=\"export.json.zip\"" );
        response.addHeader( "Content-Transfer-Encoding", "binary" );

        ZipOutputStream zip = new ZipOutputStream( response.getOutputStream() );
        zip.putNextEntry( new ZipEntry( "export.json" ) );

        JacksonUtils.toJsonWithView( zip, dxf2, ExportView.class );
    }

    //-------------------------------------------------------------------------------------------------------
    // Import
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = MetaDataController.RESOURCE_PATH, method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/*"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_IMPORT')" )
    public void importXml( HttpServletResponse response, HttpServletRequest request ) throws JAXBException, IOException
    {
        DXF2 dxf2 = JacksonUtils.fromXml( request.getInputStream(), DXF2.class );

        print( dxf2 );
    }

    @RequestMapping( value = MetaDataController.RESOURCE_PATH, method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_IMPORT')" )
    public void importJson( HttpServletResponse response, HttpServletRequest request ) throws IOException
    {
        DXF2 dxf2 = JacksonUtils.fromJson( request.getInputStream(), DXF2.class );

        print( dxf2 );
    }

    @RequestMapping( value = MetaDataController.RESOURCE_PATH + ".zip", method = RequestMethod.POST, headers = {"Content-Type=application/xml, text/xml"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_IMPORT')" )
    public void importZippedXml( HttpServletResponse response, HttpServletRequest request ) throws JAXBException, IOException
    {
        ZipInputStream zip = new ZipInputStream( new BufferedInputStream( request.getInputStream() ) );
        ZipEntry entry = zip.getNextEntry();

        System.err.println( "(xml) Reading from file : " + entry.getName() );

        DXF2 dxf2 = JacksonUtils.fromXml( zip, DXF2.class );

        print( dxf2 );
    }

    @RequestMapping( value = MetaDataController.RESOURCE_PATH + ".zip", method = RequestMethod.POST, headers = {"Content-Type=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_IMPORT')" )
    public void importZippedJson( HttpServletResponse response, HttpServletRequest request ) throws IOException
    {
        ZipInputStream zip = new ZipInputStream( request.getInputStream() );
        ZipEntry entry = zip.getNextEntry();

        System.err.println( "(json) Reading from file : " + entry.getName() );
        DXF2 dxf2 = JacksonUtils.fromJson( zip, DXF2.class );

        print( dxf2 );
    }


    //-------------------------------------------------------------------------------------------------------
    // Helpers
    //-------------------------------------------------------------------------------------------------------

    private void print( DXF2 dxf2 )
    {
        System.err.println( "AttributeTypes: " + dxf2.getAttributeTypes().size() );

        System.err.println( "Users: " + dxf2.getUsers().size() );
        System.err.println( "UserGroups: " + dxf2.getUserGroups().size() );
        System.err.println( "UserAuthorityGroups: " + dxf2.getUserAuthorityGroups().size() );

        System.err.println( "Documents: " + dxf2.getDocuments().size() );
        System.err.println( "Reports: " + dxf2.getReports().size() );
        System.err.println( "ReportTables: " + dxf2.getReportTables().size() );
        System.err.println( "Charts: " + dxf2.getCharts().size() );

        System.err.println( "Maps: " + dxf2.getMaps().size() );
        System.err.println( "MapLegends: " + dxf2.getMapLegends().size() );
        System.err.println( "MapLegendSets: " + dxf2.getMapLegendSets().size() );
        System.err.println( "MapLayers: " + dxf2.getMapLayers().size() );

        System.err.println( "Constants: " + dxf2.getConstants().size() );
        System.err.println( "Concepts: " + dxf2.getConcepts().size() );

        System.err.println( "SqlViews: " + dxf2.getSqlViews().size() );

        System.err.println( "DataElements: " + dxf2.getDataElements().size() );
        System.err.println( "OptionSets: " + dxf2.getOptionSets().size() );
        System.err.println( "DataElementGroups: " + dxf2.getDataElementGroups().size() );
        System.err.println( "DataElementGroupSets: " + dxf2.getDataElementGroupSets().size() );

        System.err.println( "Categories: " + dxf2.getCategories().size() );
        System.err.println( "CategoryOptions: " + dxf2.getCategoryOptions().size() );
        System.err.println( "CategoryCombos: " + dxf2.getCategoryCombos().size() );
        System.err.println( "CategoryOptionCombos: " + dxf2.getCategoryOptionCombos().size() );

        System.err.println( "DataSets: " + dxf2.getDataSets().size() );

        System.err.println( "Indicators:" + dxf2.getIndicators().size() );
        System.err.println( "IndicatorGroups:" + dxf2.getIndicatorGroups().size() );
        System.err.println( "IndicatorGroupSets:" + dxf2.getIndicatorGroupSets().size() );
        System.err.println( "IndicatorTypes:" + dxf2.getIndicatorTypes().size() );

        System.err.println( "OrganisationUnits: " + dxf2.getOrganisationUnits().size() );
        System.err.println( "OrganisationUnitGroups: " + dxf2.getOrganisationUnitGroups().size() );
        System.err.println( "OrganisationUnitGroupSets: " + dxf2.getOrganisationUnitGroupSets().size() );
        System.err.println( "OrganisationUnitLevels: " + dxf2.getOrganisationUnitLevels().size() );

        System.err.println( "ValidationRules: " + dxf2.getValidationRules().size() );
        System.err.println( "ValidationRuleGroups: " + dxf2.getValidationRuleGroups().size() );

        System.err.println( "DataDictionaries: " + dxf2.getDataDictionaries().size() );
    }
}
