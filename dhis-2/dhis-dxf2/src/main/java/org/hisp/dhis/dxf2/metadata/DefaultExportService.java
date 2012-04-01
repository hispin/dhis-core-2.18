package org.hisp.dhis.dxf2.metadata;

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

import java.util.ArrayList;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.concept.ConceptService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.datadictionary.DataDictionaryService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.mapping.MapLayer;
import org.hisp.dhis.mapping.MapLegend;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.sqlview.SqlViewService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class DefaultExportService
    implements ExportService
{
    @Autowired
    private AttributeService attributeService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupService userGroupService;

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

    @Override
    public DXF2 getMetaData()
    {
        return getMetaDataWithExportOptions( ExportOptions.getDefaultExportOptions() );
    }

    @Override
    public DXF2 getMetaDataWithExportOptions( ExportOptions exportOptions )
    {
        DXF2 dxf2 = new DXF2();

        if ( exportOptions.isAttributeTypes() )
        {
            dxf2.setAttributeTypes( new ArrayList<Attribute>( attributeService.getAllAttributes() ) );
        }

        if ( exportOptions.isUsers() )
        {
            dxf2.setUsers( new ArrayList<User>( userService.getAllUsers() ) );
        }

        if ( exportOptions.isUserAuthorityGroups() )
        {
            dxf2.setUserAuthorityGroups( new ArrayList<UserAuthorityGroup>( userService.getAllUserAuthorityGroups() ) );
        }

        if ( exportOptions.isUserGroups() )
        {
            dxf2.setUserGroups( new ArrayList<UserGroup>( userGroupService.getAllUserGroups() ) );
        }

        if ( exportOptions.isConstants() )
        {
            dxf2.setConstants( new ArrayList<Constant>( constantService.getAllConstants() ) );
        }

        if ( exportOptions.isConcepts() )
        {
            dxf2.setConcepts( new ArrayList<Concept>( conceptService.getAllConcepts() ) );
        }

        if ( exportOptions.isDataElements() )
        {
            dxf2.setDataElements( new ArrayList<DataElement>( dataElementService.getAllDataElements() ) );
        }

        if ( exportOptions.isOptionSets() )
        {
            dxf2.setOptionSets( new ArrayList<OptionSet>( optionService.getAllOptionSets() ) );
        }

        if ( exportOptions.isDataElementGroups() )
        {
            dxf2.setDataElementGroups( new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() ) );
        }

        if ( exportOptions.isDataElementGroupSets() )
        {
            dxf2.setDataElementGroupSets( new ArrayList<DataElementGroupSet>( dataElementService.getAllDataElementGroupSets() ) );
        }

        if ( exportOptions.isCategories() )
        {
            dxf2.setCategories( new ArrayList<DataElementCategory>( dataElementCategoryService.getAllDataElementCategories() ) );
        }

        if ( exportOptions.isCategoryOptions() )
        {
            dxf2.setCategoryOptions( new ArrayList<DataElementCategoryOption>( dataElementCategoryService.getAllDataElementCategoryOptions() ) );
        }

        if ( exportOptions.isCategoryCombos() )
        {
            dxf2.setCategoryCombos( new ArrayList<DataElementCategoryCombo>( dataElementCategoryService.getAllDataElementCategoryCombos() ) );
        }

        if ( exportOptions.isCategoryOptionCombos() )
        {
            dxf2.setCategoryOptionCombos( new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryService.getAllDataElementCategoryOptionCombos() ) );
        }

        if ( exportOptions.isIndicators() )
        {
            dxf2.setIndicators( new ArrayList<Indicator>( indicatorService.getAllIndicators() ) );
        }

        if ( exportOptions.isIndicatorGroups() )
        {
            dxf2.setIndicatorGroups( new ArrayList<IndicatorGroup>( indicatorService.getAllIndicatorGroups() ) );
        }

        if ( exportOptions.isIndicatorGroupSets() )
        {
            dxf2.setIndicatorGroupSets( new ArrayList<IndicatorGroupSet>( indicatorService.getAllIndicatorGroupSets() ) );
        }

        if ( exportOptions.isIndicatorTypes() )
        {
            dxf2.setIndicatorTypes( new ArrayList<IndicatorType>( indicatorService.getAllIndicatorTypes() ) );
        }

        if ( exportOptions.isOrganisationUnits() )
        {
            dxf2.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() ) );
        }

        if ( exportOptions.isOrganisationUnitLevels() )
        {
            dxf2.setOrganisationUnitLevels( new ArrayList<OrganisationUnitLevel>( organisationUnitService.getOrganisationUnitLevels() ) );
        }

        if ( exportOptions.isOrganisationUnitGroups() )
        {
            dxf2.setOrganisationUnitGroups( new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() ) );
        }

        if ( exportOptions.isOrganisationUnitGroupSets() )
        {
            dxf2.setOrganisationUnitGroupSets( new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getAllOrganisationUnitGroupSets() ) );
        }

        if ( exportOptions.isDataSets() )
        {
            dxf2.setDataSets( new ArrayList<DataSet>( dataSetService.getAllDataSets() ) );
        }

        if ( exportOptions.isValidationRules() )
        {
            dxf2.setValidationRules( new ArrayList<ValidationRule>( validationRuleService.getAllValidationRules() ) );
        }

        if ( exportOptions.isValidationRuleGroups() )
        {
            dxf2.setValidationRuleGroups( new ArrayList<ValidationRuleGroup>( validationRuleService.getAllValidationRuleGroups() ) );
        }

        if ( exportOptions.isSqlViews() )
        {
            dxf2.setSqlViews( new ArrayList<SqlView>( sqlViewService.getAllSqlViews() ) );
        }

        if ( exportOptions.isDocuments() )
        {
            dxf2.setDocuments( new ArrayList<Document>( documentService.getAllDocuments() ) );
        }

        if ( exportOptions.isReportTables() )
        {
            dxf2.setReportTables( new ArrayList<ReportTable>( reportTableService.getAllReportTables() ) );
        }

        if ( exportOptions.isReports() )
        {
            dxf2.setReports( new ArrayList<Report>( reportService.getAllReports() ) );
        }

        if ( exportOptions.isCharts() )
        {
            dxf2.setCharts( new ArrayList<Chart>( chartService.getAllCharts() ) );
        }

        if ( exportOptions.isMaps() )
        {
            dxf2.setMaps( new ArrayList<MapView>( mappingService.getAllMapViews() ) );
        }

        if ( exportOptions.isMapLegends() )
        {
            dxf2.setMapLegends( new ArrayList<MapLegend>( mappingService.getAllMapLegends() ) );
        }

        if ( exportOptions.isMapLegendSets() )
        {
            dxf2.setMapLegendSets( new ArrayList<MapLegendSet>( mappingService.getAllMapLegendSets() ) );
        }

        if ( exportOptions.isMapLayers() )
        {
            dxf2.setMapLayers( new ArrayList<MapLayer>( mappingService.getAllMapLayers() ) );
        }

        if ( exportOptions.isDataDictionaries() )
        {
            dxf2.setDataDictionaries( new ArrayList<DataDictionary>( dataDictionaryService.getAllDataDictionaries() ) );
        }

        return dxf2;
    }
}
