package org.hisp.dhis.dxf2.metadata;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.concept.Concept;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.datadictionary.DataDictionary;
import org.hisp.dhis.dataelement.*;
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
import org.hisp.dhis.message.MessageConversation;
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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "dxf2", namespace = Dxf2Namespace.NAMESPACE )
public class DXF2
{
    private Set<Attribute> attributeTypes = new HashSet<Attribute>();

    private Set<User> users = new HashSet<User>();

    private Set<UserAuthorityGroup> userAuthorityGroups = new HashSet<UserAuthorityGroup>();

    private Set<UserGroup> userGroups = new HashSet<UserGroup>();

    private Set<MessageConversation> messageConversations = new HashSet<MessageConversation>();

    private Set<DataElement> dataElements = new HashSet<DataElement>();

    private Set<OptionSet> optionSets = new HashSet<OptionSet>();

    private Set<DataElementGroup> dataElementGroups = new HashSet<DataElementGroup>();

    private Set<DataElementGroupSet> dataElementGroupSets = new HashSet<DataElementGroupSet>();

    private Set<Concept> concepts = new HashSet<Concept>();

    private Set<DataElementCategory> categories = new HashSet<DataElementCategory>();

    private Set<DataElementCategoryOption> categoryOptions = new HashSet<DataElementCategoryOption>();

    private Set<DataElementCategoryCombo> categoryCombos = new HashSet<DataElementCategoryCombo>();

    private Set<DataElementCategoryOptionCombo> categoryOptionCombos = new HashSet<DataElementCategoryOptionCombo>();

    private Set<Indicator> indicators = new HashSet<Indicator>();

    private Set<IndicatorGroup> indicatorGroups = new HashSet<IndicatorGroup>();

    private Set<IndicatorGroupSet> indicatorGroupSets = new HashSet<IndicatorGroupSet>();

    private Set<IndicatorType> indicatorTypes = new HashSet<IndicatorType>();

    private Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

    private Set<OrganisationUnitGroup> organisationUnitGroups = new HashSet<OrganisationUnitGroup>();

    private Set<OrganisationUnitGroupSet> organisationUnitGroupSets = new HashSet<OrganisationUnitGroupSet>();

    private Set<OrganisationUnitLevel> organisationUnitLevels = new HashSet<OrganisationUnitLevel>();

    private Set<DataSet> dataSets = new HashSet<DataSet>();

    private Set<ValidationRule> validationRules = new HashSet<ValidationRule>();

    private Set<ValidationRuleGroup> validationRuleGroups = new HashSet<ValidationRuleGroup>();

    private Set<SqlView> sqlViews = new HashSet<SqlView>();

    private Set<Chart> charts = new HashSet<Chart>();

    private Set<Report> reports = new HashSet<Report>();

    private Set<ReportTable> reportTables = new HashSet<ReportTable>();

    private Set<Document> documents = new HashSet<Document>();

    private Set<Constant> constants = new HashSet<Constant>();

    private Set<MapView> maps = new HashSet<MapView>();

    private Set<MapLegend> mapLegends = new HashSet<MapLegend>();

    private Set<MapLegendSet> mapLegendSets = new HashSet<MapLegendSet>();

    private Set<MapLayer> mapLayers = new HashSet<MapLayer>();

    private Set<DataDictionary> dataDictionaries = new HashSet<DataDictionary>();

    public DXF2()
    {
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "attributeTypes", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "attributeType", namespace = Dxf2Namespace.NAMESPACE )
    public Set<Attribute> getAttributeTypes()
    {
        return attributeTypes;
    }

    public void setAttributeTypes( Set<Attribute> attributeTypes )
    {
        this.attributeTypes = attributeTypes;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "users", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "user", namespace = Dxf2Namespace.NAMESPACE )
    public Set<User> getUsers()
    {
        return users;
    }

    public void setUsers( Set<User> users )
    {
        this.users = users;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "userAuthorityGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "userAuthorityGroup", namespace = Dxf2Namespace.NAMESPACE )
    public Set<UserAuthorityGroup> getUserAuthorityGroups()
    {
        return userAuthorityGroups;
    }

    public void setUserAuthorityGroups( Set<UserAuthorityGroup> userAuthorityGroups )
    {
        this.userAuthorityGroups = userAuthorityGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "userGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "userGroup", namespace = Dxf2Namespace.NAMESPACE )
    public Set<UserGroup> getUserGroups()
    {
        return userGroups;
    }

    public void setUserGroups( Set<UserGroup> userGroups )
    {
        this.userGroups = userGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "messageConversations", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "messageConversation", namespace = Dxf2Namespace.NAMESPACE )
    public Set<MessageConversation> getMessageConversations()
    {
        return messageConversations;
    }

    public void setMessageConversations( Set<MessageConversation> messageConversations )
    {
        this.messageConversations = messageConversations;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataElements", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataElement", namespace = Dxf2Namespace.NAMESPACE )
    public Set<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( Set<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "optionSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "optionSet", namespace = Dxf2Namespace.NAMESPACE )
    public Set<OptionSet> getOptionSets()
    {
        return optionSets;
    }

    public void setOptionSets( Set<OptionSet> optionSets )
    {
        this.optionSets = optionSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataElementGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataElementGroup", namespace = Dxf2Namespace.NAMESPACE )
    public Set<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    public void setDataElementGroups( Set<DataElementGroup> dataElementGroups )
    {
        this.dataElementGroups = dataElementGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataElementGroupSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataElementGroupSet", namespace = Dxf2Namespace.NAMESPACE )
    public Set<DataElementGroupSet> getDataElementGroupSets()
    {
        return dataElementGroupSets;
    }

    public void setDataElementGroupSets( Set<DataElementGroupSet> dataElementGroupSets )
    {
        this.dataElementGroupSets = dataElementGroupSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "concepts", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "concept", namespace = Dxf2Namespace.NAMESPACE )
    public Set<Concept> getConcepts()
    {
        return concepts;
    }

    public void setConcepts( Set<Concept> concepts )
    {
        this.concepts = concepts;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categories", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "category", namespace = Dxf2Namespace.NAMESPACE )
    public Set<DataElementCategory> getCategories()
    {
        return categories;
    }

    public void setCategories( Set<DataElementCategory> categories )
    {
        this.categories = categories;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categoryOptions", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "categoryOption", namespace = Dxf2Namespace.NAMESPACE )
    public Set<DataElementCategoryOption> getCategoryOptions()
    {
        return categoryOptions;
    }

    public void setCategoryOptions( Set<DataElementCategoryOption> categoryOptions )
    {
        this.categoryOptions = categoryOptions;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categoryCombos", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "categoryCombo", namespace = Dxf2Namespace.NAMESPACE )
    public Set<DataElementCategoryCombo> getCategoryCombos()
    {
        return categoryCombos;
    }

    public void setCategoryCombos( Set<DataElementCategoryCombo> categoryCombos )
    {
        this.categoryCombos = categoryCombos;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categoryOptionCombos", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "categoryOptionCombo", namespace = Dxf2Namespace.NAMESPACE )
    public Set<DataElementCategoryOptionCombo> getCategoryOptionCombos()
    {
        return categoryOptionCombos;
    }

    public void setCategoryOptionCombos( Set<DataElementCategoryOptionCombo> categoryOptionCombos )
    {
        this.categoryOptionCombos = categoryOptionCombos;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicators", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "indicator", namespace = Dxf2Namespace.NAMESPACE )
    public Set<Indicator> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( Set<Indicator> indicators )
    {
        this.indicators = indicators;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicatorGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "indicatorGroup", namespace = Dxf2Namespace.NAMESPACE )
    public Set<IndicatorGroup> getIndicatorGroups()
    {
        return indicatorGroups;
    }

    public void setIndicatorGroups( Set<IndicatorGroup> indicatorGroups )
    {
        this.indicatorGroups = indicatorGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicatorGroupSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "indicatorGroupSet", namespace = Dxf2Namespace.NAMESPACE )
    public Set<IndicatorGroupSet> getIndicatorGroupSets()
    {
        return indicatorGroupSets;
    }

    public void setIndicatorGroupSets( Set<IndicatorGroupSet> indicatorGroupSets )
    {
        this.indicatorGroupSets = indicatorGroupSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicatorTypes", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "indicatorType", namespace = Dxf2Namespace.NAMESPACE )
    public Set<IndicatorType> getIndicatorTypes()
    {
        return indicatorTypes;
    }

    public void setIndicatorTypes( Set<IndicatorType> indicatorTypes )
    {
        this.indicatorTypes = indicatorTypes;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnits", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "organisationUnit", namespace = Dxf2Namespace.NAMESPACE )
    public Set<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( Set<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnitGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "organisationUnitGroup", namespace = Dxf2Namespace.NAMESPACE )
    public Set<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroups( Set<OrganisationUnitGroup> organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnitGroupSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "organisationUnitGroupSet", namespace = Dxf2Namespace.NAMESPACE )
    public Set<OrganisationUnitGroupSet> getOrganisationUnitGroupSets()
    {
        return organisationUnitGroupSets;
    }

    public void setOrganisationUnitGroupSets( Set<OrganisationUnitGroupSet> organisationUnitGroupSets )
    {
        this.organisationUnitGroupSets = organisationUnitGroupSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnitLevels", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "organisationUnitLevel", namespace = Dxf2Namespace.NAMESPACE )
    public Set<OrganisationUnitLevel> getOrganisationUnitLevels()
    {
        return organisationUnitLevels;
    }

    public void setOrganisationUnitLevels( Set<OrganisationUnitLevel> organisationUnitLevels )
    {
        this.organisationUnitLevels = organisationUnitLevels;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataSet", namespace = Dxf2Namespace.NAMESPACE )
    public Set<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( Set<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "validationRules", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "validationRule", namespace = Dxf2Namespace.NAMESPACE )
    public Set<ValidationRule> getValidationRules()
    {
        return validationRules;
    }

    public void setValidationRules( Set<ValidationRule> validationRules )
    {
        this.validationRules = validationRules;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "validationRuleGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "validationRuleGroup", namespace = Dxf2Namespace.NAMESPACE )
    public Set<ValidationRuleGroup> getValidationRuleGroups()
    {
        return validationRuleGroups;
    }

    public void setValidationRuleGroups( Set<ValidationRuleGroup> validationRuleGroups )
    {
        this.validationRuleGroups = validationRuleGroups;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "sqlViews", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "sqlView", namespace = Dxf2Namespace.NAMESPACE )
    public Set<SqlView> getSqlViews()
    {
        return sqlViews;
    }

    public void setSqlViews( Set<SqlView> sqlViews )
    {
        this.sqlViews = sqlViews;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "charts", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "chart", namespace = Dxf2Namespace.NAMESPACE )
    public Set<Chart> getCharts()
    {
        return charts;
    }

    public void setCharts( Set<Chart> charts )
    {
        this.charts = charts;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "reports", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "report", namespace = Dxf2Namespace.NAMESPACE )
    public Set<Report> getReports()
    {
        return reports;
    }

    public void setReports( Set<Report> reports )
    {
        this.reports = reports;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "reportTables", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "reportTable", namespace = Dxf2Namespace.NAMESPACE )
    public Set<ReportTable> getReportTables()
    {
        return reportTables;
    }

    public void setReportTables( Set<ReportTable> reportTables )
    {
        this.reportTables = reportTables;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "documents", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "document", namespace = Dxf2Namespace.NAMESPACE )
    public Set<Document> getDocuments()
    {
        return documents;
    }

    public void setDocuments( Set<Document> documents )
    {
        this.documents = documents;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "constants", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "constant", namespace = Dxf2Namespace.NAMESPACE )
    public Set<Constant> getConstants()
    {
        return constants;
    }

    public void setConstants( Set<Constant> constants )
    {
        this.constants = constants;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "maps", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "map", namespace = Dxf2Namespace.NAMESPACE )
    public Set<MapView> getMaps()
    {
        return maps;
    }

    public void setMaps( Set<MapView> maps )
    {
        this.maps = maps;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "mapLegends", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "mapLegend", namespace = Dxf2Namespace.NAMESPACE )
    public Set<MapLegend> getMapLegends()
    {
        return mapLegends;
    }

    public void setMapLegends( Set<MapLegend> mapLegends )
    {
        this.mapLegends = mapLegends;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "mapLegendSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "mapLegendSet", namespace = Dxf2Namespace.NAMESPACE )
    public Set<MapLegendSet> getMapLegendSets()
    {
        return mapLegendSets;
    }

    public void setMapLegendSets( Set<MapLegendSet> mapLegendSets )
    {
        this.mapLegendSets = mapLegendSets;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "mapLayers", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "mapLayer", namespace = Dxf2Namespace.NAMESPACE )
    public Set<MapLayer> getMapLayers()
    {
        return mapLayers;
    }

    public void setMapLayers( Set<MapLayer> mapLayers )
    {
        this.mapLayers = mapLayers;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataDictionaries", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataDictionary", namespace = Dxf2Namespace.NAMESPACE )
    public Set<DataDictionary> getDataDictionaries()
    {
        return dataDictionaries;
    }

    public void setDataDictionaries( Set<DataDictionary> dataDictionaries )
    {
        this.dataDictionaries = dataDictionaries;
    }

    @Override
    public String toString()
    {
        return "DXF2{" +
            "attributeTypes=" + attributeTypes.size() +
            ", users=" + users.size() +
            ", userAuthorityGroups=" + userAuthorityGroups.size() +
            ", userGroups=" + userGroups.size() +
            ", messageConversations=" + messageConversations.size() +
            ", dataElements=" + dataElements.size() +
            ", optionSets=" + optionSets.size() +
            ", dataElementGroups=" + dataElementGroups.size() +
            ", dataElementGroupSets=" + dataElementGroupSets.size() +
            ", concepts=" + concepts.size() +
            ", categories=" + categories.size() +
            ", categoryOptions=" + categoryOptions.size() +
            ", categoryCombos=" + categoryCombos.size() +
            ", categoryOptionCombos=" + categoryOptionCombos.size() +
            ", indicators=" + indicators.size() +
            ", indicatorGroups=" + indicatorGroups.size() +
            ", indicatorGroupSets=" + indicatorGroupSets.size() +
            ", indicatorTypes=" + indicatorTypes.size() +
            ", organisationUnits=" + organisationUnits.size() +
            ", organisationUnitGroups=" + organisationUnitGroups.size() +
            ", organisationUnitGroupSets=" + organisationUnitGroupSets.size() +
            ", organisationUnitLevels=" + organisationUnitLevels.size() +
            ", dataSets=" + dataSets.size() +
            ", validationRules=" + validationRules.size() +
            ", validationRuleGroups=" + validationRuleGroups.size() +
            ", sqlViews=" + sqlViews.size() +
            ", charts=" + charts.size() +
            ", reports=" + reports.size() +
            ", reportTables=" + reportTables.size() +
            ", documents=" + documents.size() +
            ", constants=" + constants.size() +
            ", maps=" + maps.size() +
            ", mapLegends=" + mapLegends.size() +
            ", mapLegendSets=" + mapLegendSets.size() +
            ", mapLayers=" + mapLayers.size() +
            ", dataDictionaries=" + dataDictionaries.size() +
            '}';
    }
}
