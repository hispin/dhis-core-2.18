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
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.mapping.MapLayer;
import org.hisp.dhis.mapping.MapLegend;
import org.hisp.dhis.mapping.MapLegendSet;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Service
public class DefaultExportService
    implements ExportService
{
    @Autowired
    protected IdentifiableObjectManager manager;

    @Override
    public MetaData getMetaData()
    {
        return getMetaData( ExportOptions.getDefaultExportOptions() );
    }

    @Override
    public MetaData getMetaData( ExportOptions exportOptions )
    {
        MetaData dxf2 = new MetaData();

        if ( exportOptions.isAttributeTypes() )
        {
            dxf2.setAttributeTypes( new ArrayList<Attribute>( manager.getAll( Attribute.class ) ) );
        }

        if ( exportOptions.isUsers() )
        {
            dxf2.setUsers( new ArrayList<User>( manager.getAll( User.class ) ) );
        }

        if ( exportOptions.isUserAuthorityGroups() )
        {
            dxf2.setUserAuthorityGroups( new ArrayList<UserAuthorityGroup>( manager.getAll( UserAuthorityGroup.class ) ) );
        }

        if ( exportOptions.isUserGroups() )
        {
            dxf2.setUserGroups( new ArrayList<UserGroup>( manager.getAll( UserGroup.class ) ) );
        }

        if ( exportOptions.isConstants() )
        {
            dxf2.setConstants( new ArrayList<Constant>( manager.getAll( Constant.class ) ) );
        }

        if ( exportOptions.isConcepts() )
        {
            dxf2.setConcepts( new ArrayList<Concept>( manager.getAll( Concept.class ) ) );
        }

        if ( exportOptions.isDataElements() )
        {
            dxf2.setDataElements( new ArrayList<DataElement>( manager.getAll( DataElement.class ) ) );
        }

        if ( exportOptions.isOptionSets() )
        {
            dxf2.setOptionSets( new ArrayList<OptionSet>( manager.getAll( OptionSet.class ) ) );
        }

        if ( exportOptions.isDataElementGroups() )
        {
            dxf2.setDataElementGroups( new ArrayList<DataElementGroup>( manager.getAll( DataElementGroup.class ) ) );
        }

        if ( exportOptions.isDataElementGroupSets() )
        {
            dxf2.setDataElementGroupSets( new ArrayList<DataElementGroupSet>( manager.getAll( DataElementGroupSet.class ) ) );
        }

        if ( exportOptions.isCategories() )
        {
            dxf2.setCategories( new ArrayList<DataElementCategory>( manager.getAll( DataElementCategory.class ) ) );
        }

        if ( exportOptions.isCategoryOptions() )
        {
            dxf2.setCategoryOptions( new ArrayList<DataElementCategoryOption>( manager.getAll( DataElementCategoryOption.class ) ) );
        }

        if ( exportOptions.isCategoryCombos() )
        {
            dxf2.setCategoryCombos( new ArrayList<DataElementCategoryCombo>( manager.getAll( DataElementCategoryCombo.class ) ) );
        }

        if ( exportOptions.isCategoryOptionCombos() )
        {
            dxf2.setCategoryOptionCombos( new ArrayList<DataElementCategoryOptionCombo>( manager.getAll( DataElementCategoryOptionCombo.class ) ) );
        }

        if ( exportOptions.isIndicators() )
        {
            dxf2.setIndicators( new ArrayList<Indicator>( manager.getAll( Indicator.class ) ) );
        }

        if ( exportOptions.isIndicatorGroups() )
        {
            dxf2.setIndicatorGroups( new ArrayList<IndicatorGroup>( manager.getAll( IndicatorGroup.class ) ) );
        }

        if ( exportOptions.isIndicatorGroupSets() )
        {
            dxf2.setIndicatorGroupSets( new ArrayList<IndicatorGroupSet>( manager.getAll( IndicatorGroupSet.class ) ) );
        }

        if ( exportOptions.isIndicatorTypes() )
        {
            dxf2.setIndicatorTypes( new ArrayList<IndicatorType>( manager.getAll( IndicatorType.class ) ) );
        }

        if ( exportOptions.isOrganisationUnits() )
        {
            dxf2.setOrganisationUnits( new ArrayList<OrganisationUnit>( manager.getAll( OrganisationUnit.class ) ) );
        }

        if ( exportOptions.isOrganisationUnitLevels() )
        {
            dxf2.setOrganisationUnitLevels( new ArrayList<OrganisationUnitLevel>( manager.getAll( OrganisationUnitLevel.class ) ) );
        }

        if ( exportOptions.isOrganisationUnitGroups() )
        {
            dxf2.setOrganisationUnitGroups( new ArrayList<OrganisationUnitGroup>( manager.getAll( OrganisationUnitGroup.class ) ) );
        }

        if ( exportOptions.isOrganisationUnitGroupSets() )
        {
            dxf2.setOrganisationUnitGroupSets( new ArrayList<OrganisationUnitGroupSet>( manager.getAll( OrganisationUnitGroupSet.class ) ) );
        }

        if ( exportOptions.isDataSets() )
        {
            dxf2.setDataSets( new ArrayList<DataSet>( manager.getAll( DataSet.class ) ) );
        }

        if ( exportOptions.isValidationRules() )
        {
            dxf2.setValidationRules( new ArrayList<ValidationRule>( manager.getAll( ValidationRule.class ) ) );
        }

        if ( exportOptions.isValidationRuleGroups() )
        {
            dxf2.setValidationRuleGroups( new ArrayList<ValidationRuleGroup>( manager.getAll( ValidationRuleGroup.class ) ) );
        }

        if ( exportOptions.isSqlViews() )
        {
            dxf2.setSqlViews( new ArrayList<SqlView>( manager.getAll( SqlView.class ) ) );
        }

        if ( exportOptions.isDocuments() )
        {
            dxf2.setDocuments( new ArrayList<Document>( manager.getAll( Document.class ) ) );
        }

        if ( exportOptions.isReportTables() )
        {
            dxf2.setReportTables( new ArrayList<ReportTable>( manager.getAll( ReportTable.class ) ) );
        }

        if ( exportOptions.isReports() )
        {
            dxf2.setReports( new ArrayList<Report>( manager.getAll( Report.class ) ) );
        }

        if ( exportOptions.isCharts() )
        {
            dxf2.setCharts( new ArrayList<Chart>( manager.getAll( Chart.class ) ) );
        }

        if ( exportOptions.isMaps() )
        {
            dxf2.setMaps( new ArrayList<MapView>( manager.getAll( MapView.class ) ) );
        }

        if ( exportOptions.isMapLegends() )
        {
            dxf2.setMapLegends( new ArrayList<MapLegend>( manager.getAll( MapLegend.class ) ) );
        }

        if ( exportOptions.isMapLegendSets() )
        {
            dxf2.setMapLegendSets( new ArrayList<MapLegendSet>( manager.getAll( MapLegendSet.class ) ) );
        }

        if ( exportOptions.isMapLayers() )
        {
            dxf2.setMapLayers( new ArrayList<MapLayer>( manager.getAll( MapLayer.class ) ) );
        }

        if ( exportOptions.isDataDictionaries() )
        {
            dxf2.setDataDictionaries( new ArrayList<DataDictionary>( manager.getAll( DataDictionary.class ) ) );
        }

        return dxf2;
    }
}
