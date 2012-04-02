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
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.indicator.*;
import org.hisp.dhis.mapping.*;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Service
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
            dxf2.setAttributeTypes( new HashSet<Attribute>( attributeService.getAllAttributes() ) );
        }

        if ( exportOptions.isUsers() )
        {
            dxf2.setUsers( new HashSet<User>( userService.getAllUsers() ) );
        }

        if ( exportOptions.isUserAuthorityGroups() )
        {
            dxf2.setUserAuthorityGroups( new HashSet<UserAuthorityGroup>( userService.getAllUserAuthorityGroups() ) );
        }

        if ( exportOptions.isUserGroups() )
        {
            dxf2.setUserGroups( new HashSet<UserGroup>( userGroupService.getAllUserGroups() ) );
        }

        if ( exportOptions.isConstants() )
        {
            dxf2.setConstants( new HashSet<Constant>( constantService.getAllConstants() ) );
        }

        if ( exportOptions.isConcepts() )
        {
            dxf2.setConcepts( new HashSet<Concept>( conceptService.getAllConcepts() ) );
        }

        if ( exportOptions.isDataElements() )
        {
            dxf2.setDataElements( new HashSet<DataElement>( dataElementService.getAllDataElements() ) );
        }

        if ( exportOptions.isOptionSets() )
        {
            dxf2.setOptionSets( new HashSet<OptionSet>( optionService.getAllOptionSets() ) );
        }

        if ( exportOptions.isDataElementGroups() )
        {
            dxf2.setDataElementGroups( new HashSet<DataElementGroup>( dataElementService.getAllDataElementGroups() ) );
        }

        if ( exportOptions.isDataElementGroupSets() )
        {
            dxf2.setDataElementGroupSets( new HashSet<DataElementGroupSet>( dataElementService.getAllDataElementGroupSets() ) );
        }

        if ( exportOptions.isCategories() )
        {
            dxf2.setCategories( new HashSet<DataElementCategory>( dataElementCategoryService.getAllDataElementCategories() ) );
        }

        if ( exportOptions.isCategoryOptions() )
        {
            dxf2.setCategoryOptions( new HashSet<DataElementCategoryOption>( dataElementCategoryService.getAllDataElementCategoryOptions() ) );
        }

        if ( exportOptions.isCategoryCombos() )
        {
            dxf2.setCategoryCombos( new HashSet<DataElementCategoryCombo>( dataElementCategoryService.getAllDataElementCategoryCombos() ) );
        }

        if ( exportOptions.isCategoryOptionCombos() )
        {
            dxf2.setCategoryOptionCombos( new HashSet<DataElementCategoryOptionCombo>( dataElementCategoryService.getAllDataElementCategoryOptionCombos() ) );
        }

        if ( exportOptions.isIndicators() )
        {
            dxf2.setIndicators( new HashSet<Indicator>( indicatorService.getAllIndicators() ) );
        }

        if ( exportOptions.isIndicatorGroups() )
        {
            dxf2.setIndicatorGroups( new HashSet<IndicatorGroup>( indicatorService.getAllIndicatorGroups() ) );
        }

        if ( exportOptions.isIndicatorGroupSets() )
        {
            dxf2.setIndicatorGroupSets( new HashSet<IndicatorGroupSet>( indicatorService.getAllIndicatorGroupSets() ) );
        }

        if ( exportOptions.isIndicatorTypes() )
        {
            dxf2.setIndicatorTypes( new HashSet<IndicatorType>( indicatorService.getAllIndicatorTypes() ) );
        }

        if ( exportOptions.isOrganisationUnits() )
        {
            dxf2.setOrganisationUnits( new HashSet<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() ) );
        }

        if ( exportOptions.isOrganisationUnitLevels() )
        {
            dxf2.setOrganisationUnitLevels( new HashSet<OrganisationUnitLevel>( organisationUnitService.getOrganisationUnitLevels() ) );
        }

        if ( exportOptions.isOrganisationUnitGroups() )
        {
            dxf2.setOrganisationUnitGroups( new HashSet<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() ) );
        }

        if ( exportOptions.isOrganisationUnitGroupSets() )
        {
            dxf2.setOrganisationUnitGroupSets( new HashSet<OrganisationUnitGroupSet>( organisationUnitGroupService.getAllOrganisationUnitGroupSets() ) );
        }

        if ( exportOptions.isDataSets() )
        {
            dxf2.setDataSets( new HashSet<DataSet>( dataSetService.getAllDataSets() ) );
        }

        if ( exportOptions.isValidationRules() )
        {
            dxf2.setValidationRules( new HashSet<ValidationRule>( validationRuleService.getAllValidationRules() ) );
        }

        if ( exportOptions.isValidationRuleGroups() )
        {
            dxf2.setValidationRuleGroups( new HashSet<ValidationRuleGroup>( validationRuleService.getAllValidationRuleGroups() ) );
        }

        if ( exportOptions.isSqlViews() )
        {
            dxf2.setSqlViews( new HashSet<SqlView>( sqlViewService.getAllSqlViews() ) );
        }

        if ( exportOptions.isDocuments() )
        {
            dxf2.setDocuments( new HashSet<Document>( documentService.getAllDocuments() ) );
        }

        if ( exportOptions.isReportTables() )
        {
            dxf2.setReportTables( new HashSet<ReportTable>( reportTableService.getAllReportTables() ) );
        }

        if ( exportOptions.isReports() )
        {
            dxf2.setReports( new HashSet<Report>( reportService.getAllReports() ) );
        }

        if ( exportOptions.isCharts() )
        {
            dxf2.setCharts( new HashSet<Chart>( chartService.getAllCharts() ) );
        }

        if ( exportOptions.isMaps() )
        {
            dxf2.setMaps( new HashSet<MapView>( mappingService.getAllMapViews() ) );
        }

        if ( exportOptions.isMapLegends() )
        {
            dxf2.setMapLegends( new HashSet<MapLegend>( mappingService.getAllMapLegends() ) );
        }

        if ( exportOptions.isMapLegendSets() )
        {
            dxf2.setMapLegendSets( new HashSet<MapLegendSet>( mappingService.getAllMapLegendSets() ) );
        }

        if ( exportOptions.isMapLayers() )
        {
            dxf2.setMapLayers( new HashSet<MapLayer>( mappingService.getAllMapLayers() ) );
        }

        if ( exportOptions.isDataDictionaries() )
        {
            dxf2.setDataDictionaries( new HashSet<DataDictionary>( dataDictionaryService.getAllDataDictionaries() ) );
        }

        return dxf2;
    }
}
