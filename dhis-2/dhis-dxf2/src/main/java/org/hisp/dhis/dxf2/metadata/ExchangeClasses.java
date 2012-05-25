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
import org.hisp.dhis.report.Report;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;

import java.util.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
final public class ExchangeClasses
{
    final private static Map<String, Class<?>> exportClasses;

    final private static Map<String, Class<?>> importClasses;

    static
    {
        exportClasses = new LinkedHashMap<String, Class<?>>();

        exportClasses.put( "sqlViews", SqlView.class );
        exportClasses.put( "concepts", Concept.class );
        exportClasses.put( "constants", Constant.class );
        exportClasses.put( "documents", Document.class );
        exportClasses.put( "optionSets", OptionSet.class );
        exportClasses.put( "attributeTypes", Attribute.class );

        exportClasses.put( "organisationUnits", OrganisationUnit.class );
        exportClasses.put( "organisationUnitLevels", OrganisationUnitLevel.class );
        exportClasses.put( "organisationUnitGroups", OrganisationUnitGroup.class );
        exportClasses.put( "organisationUnitGroupSets", OrganisationUnitGroupSet.class );

        exportClasses.put( "categoryOptions", DataElementCategoryOption.class );
        exportClasses.put( "categories", DataElementCategory.class );
        exportClasses.put( "categoryCombos", DataElementCategoryCombo.class );
        exportClasses.put( "categoryOptionCombos", DataElementCategoryOptionCombo.class );

        exportClasses.put( "dataElements", DataElement.class );
        exportClasses.put( "dataElementGroups", DataElementGroup.class );
        exportClasses.put( "dataElementGroupSets", DataElementGroupSet.class );

        exportClasses.put( "indicatorTypes", IndicatorType.class );
        exportClasses.put( "indicators", Indicator.class );
        exportClasses.put( "indicatorGroups", IndicatorGroup.class );
        exportClasses.put( "indicatorGroupSets", IndicatorGroupSet.class );

        exportClasses.put( "dataDictionaries", DataDictionary.class );

        exportClasses.put( "dataSets", DataSet.class );
        exportClasses.put( "sections", Section.class );

        exportClasses.put( "reportTables", ReportTable.class );
        exportClasses.put( "reports", Report.class );
        exportClasses.put( "charts", Chart.class );

        exportClasses.put( "validationRules", ValidationRule.class );
        exportClasses.put( "validationRuleGroups", ValidationRuleGroup.class );

        exportClasses.put( "maps", MapView.class );
        exportClasses.put( "mapLegends", MapLegend.class );
        exportClasses.put( "mapLegendSets", MapLegendSet.class );
        exportClasses.put( "mapLayers", MapLayer.class );

        importClasses = exportClasses;


        // exportClasses.put( "users", User.class );
        // exportClasses.put( "userGroups", UserGroup.class );
        // exportClasses.put( "userRoles", UserAuthorityGroup.class );
        // exportClasses.put( "messageConversations", MessageConversation.class );
    }

    public static Map<String, Class<?>> getExportMap()
    {
        return Collections.unmodifiableMap( exportClasses );
    }

    public static List<Class<?>> getExportClasses()
    {
        return new ArrayList<Class<?>>( exportClasses.values() );
    }

    public static Map<String, Class<?>> getImportMap()
    {
        return Collections.unmodifiableMap( importClasses );
    }

    public static List<Class<?>> getImportClasses()
    {
        return new ArrayList<Class<?>>( importClasses.values() );
    }
}
