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
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.Section;
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
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitComparator;
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

import java.util.ArrayList;
import java.util.Collections;

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
        MetaData metaData = new MetaData();

        if ( exportOptions.isAttributeTypes() )
        {
            metaData.setAttributeTypes( new ArrayList<Attribute>( manager.getAll( Attribute.class ) ) );
        }

        if ( exportOptions.isUsers() )
        {
            metaData.setUsers( new ArrayList<User>( manager.getAll( User.class ) ) );
        }

        if ( exportOptions.isUserAuthorityGroups() )
        {
            metaData.setUserAuthorityGroups( new ArrayList<UserAuthorityGroup>( manager.getAll( UserAuthorityGroup.class ) ) );
        }

        if ( exportOptions.isUserGroups() )
        {
            metaData.setUserGroups( new ArrayList<UserGroup>( manager.getAll( UserGroup.class ) ) );
        }

        if ( exportOptions.isConstants() )
        {
            metaData.setConstants( new ArrayList<Constant>( manager.getAll( Constant.class ) ) );
        }

        if ( exportOptions.isConcepts() )
        {
            metaData.setConcepts( new ArrayList<Concept>( manager.getAll( Concept.class ) ) );
        }

        if ( exportOptions.isDataElements() )
        {
            metaData.setDataElements( new ArrayList<DataElement>( manager.getAll( DataElement.class ) ) );
        }

        if ( exportOptions.isOptionSets() )
        {
            metaData.setOptionSets( new ArrayList<OptionSet>( manager.getAll( OptionSet.class ) ) );
        }

        if ( exportOptions.isDataElementGroups() )
        {
            metaData.setDataElementGroups( new ArrayList<DataElementGroup>( manager.getAll( DataElementGroup.class ) ) );
        }

        if ( exportOptions.isDataElementGroupSets() )
        {
            metaData.setDataElementGroupSets( new ArrayList<DataElementGroupSet>( manager.getAll( DataElementGroupSet.class ) ) );
        }

        if ( exportOptions.isCategories() )
        {
            metaData.setCategories( new ArrayList<DataElementCategory>( manager.getAll( DataElementCategory.class ) ) );
        }

        if ( exportOptions.isCategoryOptions() )
        {
            metaData.setCategoryOptions( new ArrayList<DataElementCategoryOption>( manager.getAll( DataElementCategoryOption.class ) ) );
        }

        if ( exportOptions.isCategoryCombos() )
        {
            metaData.setCategoryCombos( new ArrayList<DataElementCategoryCombo>( manager.getAll( DataElementCategoryCombo.class ) ) );
        }

        if ( exportOptions.isCategoryOptionCombos() )
        {
            metaData.setCategoryOptionCombos( new ArrayList<DataElementCategoryOptionCombo>( manager.getAll( DataElementCategoryOptionCombo.class ) ) );
        }

        if ( exportOptions.isIndicators() )
        {
            metaData.setIndicators( new ArrayList<Indicator>( manager.getAll( Indicator.class ) ) );
        }

        if ( exportOptions.isIndicatorGroups() )
        {
            metaData.setIndicatorGroups( new ArrayList<IndicatorGroup>( manager.getAll( IndicatorGroup.class ) ) );
        }

        if ( exportOptions.isIndicatorGroupSets() )
        {
            metaData.setIndicatorGroupSets( new ArrayList<IndicatorGroupSet>( manager.getAll( IndicatorGroupSet.class ) ) );
        }

        if ( exportOptions.isIndicatorTypes() )
        {
            metaData.setIndicatorTypes( new ArrayList<IndicatorType>( manager.getAll( IndicatorType.class ) ) );
        }

        if ( exportOptions.isOrganisationUnits() )
        {
            metaData.setOrganisationUnits( new ArrayList<OrganisationUnit>( manager.getAll( OrganisationUnit.class ) ) );

            // sort according to level
            Collections.sort( metaData.getOrganisationUnits(), new OrganisationUnitComparator() );
        }

        if ( exportOptions.isOrganisationUnitLevels() )
        {
            metaData.setOrganisationUnitLevels( new ArrayList<OrganisationUnitLevel>( manager.getAll( OrganisationUnitLevel.class ) ) );
        }

        if ( exportOptions.isOrganisationUnitGroups() )
        {
            metaData.setOrganisationUnitGroups( new ArrayList<OrganisationUnitGroup>( manager.getAll( OrganisationUnitGroup.class ) ) );
        }

        if ( exportOptions.isOrganisationUnitGroupSets() )
        {
            metaData.setOrganisationUnitGroupSets( new ArrayList<OrganisationUnitGroupSet>( manager.getAll( OrganisationUnitGroupSet.class ) ) );
        }

        if ( exportOptions.isSections() )
        {
            metaData.setSections( new ArrayList<Section>( manager.getAll( Section.class ) ) );
        }

        if ( exportOptions.isDataSets() )
        {
            metaData.setDataSets( new ArrayList<DataSet>( manager.getAll( DataSet.class ) ) );
        }

        if ( exportOptions.isValidationRules() )
        {
            metaData.setValidationRules( new ArrayList<ValidationRule>( manager.getAll( ValidationRule.class ) ) );
        }

        if ( exportOptions.isValidationRuleGroups() )
        {
            metaData.setValidationRuleGroups( new ArrayList<ValidationRuleGroup>( manager.getAll( ValidationRuleGroup.class ) ) );
        }

        if ( exportOptions.isSqlViews() )
        {
            metaData.setSqlViews( new ArrayList<SqlView>( manager.getAll( SqlView.class ) ) );
        }

        if ( exportOptions.isDocuments() )
        {
            metaData.setDocuments( new ArrayList<Document>( manager.getAll( Document.class ) ) );
        }

        if ( exportOptions.isReportTables() )
        {
            metaData.setReportTables( new ArrayList<ReportTable>( manager.getAll( ReportTable.class ) ) );
        }

        if ( exportOptions.isReports() )
        {
            metaData.setReports( new ArrayList<Report>( manager.getAll( Report.class ) ) );
        }

        if ( exportOptions.isCharts() )
        {
            metaData.setCharts( new ArrayList<Chart>( manager.getAll( Chart.class ) ) );
        }

        if ( exportOptions.isMaps() )
        {
            metaData.setMaps( new ArrayList<MapView>( manager.getAll( MapView.class ) ) );
        }

        if ( exportOptions.isMapLegends() )
        {
            metaData.setMapLegends( new ArrayList<MapLegend>( manager.getAll( MapLegend.class ) ) );
        }

        if ( exportOptions.isMapLegendSets() )
        {
            metaData.setMapLegendSets( new ArrayList<MapLegendSet>( manager.getAll( MapLegendSet.class ) ) );
        }

        if ( exportOptions.isMapLayers() )
        {
            metaData.setMapLayers( new ArrayList<MapLayer>( manager.getAll( MapLayer.class ) ) );
        }

        if ( exportOptions.isDataDictionaries() )
        {
            metaData.setDataDictionaries( new ArrayList<DataDictionary>( manager.getAll( DataDictionary.class ) ) );
        }

        return metaData;
    }
}
