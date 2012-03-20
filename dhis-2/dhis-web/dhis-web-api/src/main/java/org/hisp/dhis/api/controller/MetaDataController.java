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
import org.hisp.dhis.api.view.JacksonUtils;
import org.hisp.dhis.api.webdomain.DXF2;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.concept.ConceptService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.indicator.*;
import org.hisp.dhis.mapping.*;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.*;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.sqlview.SqlViewService;
import org.hisp.dhis.user.*;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;
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
import java.util.ArrayList;
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
    private AttributeService attributeService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private ValidationRuleService validationRuleService;

    @Autowired
    private SqlViewService sqlViewService;

    @Autowired
    private ChartService chartService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportTableService reportTableService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private DataDictionaryService dataDictionaryService;

    //-------------------------------------------------------------------------------------------------------
    // Export
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( value = MetaDataController.RESOURCE_PATH, method = RequestMethod.GET )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public String export( Model model )
    {
        DXF2 dxf2 = getExportObject();

        model.addAttribute( "model", dxf2 );
        model.addAttribute( "view", "export" );

        return "export";
    }

    @RequestMapping( value = MetaDataController.RESOURCE_PATH + ".zip", method = RequestMethod.GET, headers = {"Accept=application/xml, text/*"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedXML( HttpServletResponse response ) throws IOException, JAXBException
    {
        DXF2 dxf2 = getExportObject();

        response.setContentType( ContextUtils.CONTENT_TYPE_ZIP );
        response.addHeader( "Content-Disposition", "attachment; filename=\"export.xml.zip\"" );
        response.addHeader( "Content-Transfer-Encoding", "binary" );

        ZipOutputStream zip = new ZipOutputStream( response.getOutputStream() );
        zip.putNextEntry( new ZipEntry( "export.xml" ) );

        JacksonUtils.toXmlWithView( zip, dxf2, ExportView.class );
    }

    @RequestMapping( value = MetaDataController.RESOURCE_PATH + ".zip", method = RequestMethod.GET, headers = {"Accept=application/json"} )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportZippedJSON( HttpServletResponse response ) throws IOException, JAXBException
    {
        DXF2 dxf2 = getExportObject();

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

    private DXF2 getExportObject()
    {
        DXF2 dxf2 = new DXF2();

        dxf2.setAttributeTypes( new ArrayList<Attribute>( attributeService.getAllAttributes() ) );

        dxf2.setUsers( new ArrayList<User>( userService.getAllUsers() ) );
        dxf2.setUserAuthorityGroups( new ArrayList<UserAuthorityGroup>( userService.getAllUserAuthorityGroups() ) );
        dxf2.setUserGroups( new ArrayList<UserGroup>( userGroupService.getAllUserGroups() ) );

        dxf2.setConstants( new ArrayList<Constant>( constantService.getAllConstants() ) );
        dxf2.setConcepts( new ArrayList<Concept>( conceptService.getAllConcepts() ) );

        dxf2.setDataElements( new ArrayList<DataElement>( dataElementService.getAllDataElements() ) );
        dxf2.setOptionSets( new ArrayList<OptionSet>( optionService.getAllOptionSets() ) );
        dxf2.setDataElementGroups( new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() ) );
        dxf2.setDataElementGroupSets( new ArrayList<DataElementGroupSet>( dataElementService.getAllDataElementGroupSets() ) );

        dxf2.setCategories( new ArrayList<DataElementCategory>( dataElementCategoryService.getAllDataElementCategories() ) );
        dxf2.setCategoryOptions( new ArrayList<DataElementCategoryOption>( dataElementCategoryService.getAllDataElementCategoryOptions() ) );
        dxf2.setCategoryCombos( new ArrayList<DataElementCategoryCombo>( dataElementCategoryService.getAllDataElementCategoryCombos() ) );
        dxf2.setCategoryOptionCombos( new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryService.getAllDataElementCategoryOptionCombos() ) );

        dxf2.setIndicators( new ArrayList<Indicator>( indicatorService.getAllIndicators() ) );
        dxf2.setIndicatorGroups( new ArrayList<IndicatorGroup>( indicatorService.getAllIndicatorGroups() ) );
        dxf2.setIndicatorGroupSets( new ArrayList<IndicatorGroupSet>( indicatorService.getAllIndicatorGroupSets() ) );
        dxf2.setIndicatorTypes( new ArrayList<IndicatorType>( indicatorService.getAllIndicatorTypes() ) );

        dxf2.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() ) );
        dxf2.setOrganisationUnitLevels( new ArrayList<OrganisationUnitLevel>( organisationUnitService.getOrganisationUnitLevels() ) );
        dxf2.setOrganisationUnitGroups( new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() ) );
        dxf2.setOrganisationUnitGroupSets( new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getAllOrganisationUnitGroupSets() ) );

        dxf2.setDataSets( new ArrayList<DataSet>( dataSetService.getAllDataSets() ) );

        dxf2.setValidationRules( new ArrayList<ValidationRule>( validationRuleService.getAllValidationRules() ) );
        dxf2.setValidationRuleGroups( new ArrayList<ValidationRuleGroup>( validationRuleService.getAllValidationRuleGroups() ) );

        dxf2.setSqlViews( new ArrayList<SqlView>( sqlViewService.getAllSqlViews() ) );

        dxf2.setDocuments( new ArrayList<Document>( documentService.getAllDocuments() ) );
        dxf2.setReportTables( new ArrayList<ReportTable>( reportTableService.getAllReportTables() ) );
        dxf2.setReports( new ArrayList<Report>( reportService.getAllReports() ) );
        dxf2.setCharts( new ArrayList<Chart>( chartService.getAllCharts() ) );

        dxf2.setMaps( new ArrayList<MapView>( mappingService.getAllMapViews() ) );
        dxf2.setMapLegends( new ArrayList<MapLegend>( mappingService.getAllMapLegends() ) );
        dxf2.setMapLegendSets( new ArrayList<MapLegendSet>( mappingService.getAllMapLegendSets() ) );
        dxf2.setMapLayers( new ArrayList<MapLayer>( mappingService.getAllMapLayers() ) );

        dxf2.setDataDictionaries( new ArrayList<DataDictionary>( dataDictionaryService.getAllDataDictionaries() ) );

        return dxf2;
    }

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
