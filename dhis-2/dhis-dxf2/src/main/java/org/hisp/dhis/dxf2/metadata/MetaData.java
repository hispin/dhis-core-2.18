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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "metaData", namespace = Dxf2Namespace.NAMESPACE )
public class MetaData
{
    private List<Attribute> attributeList = new ArrayList<Attribute>();

    private List<Document> documentList = new ArrayList<Document>();

    private List<Constant> constantList = new ArrayList<Constant>();

    private List<Concept> conceptList = new ArrayList<Concept>();

    private List<User> userList = new ArrayList<User>();

    private List<UserAuthorityGroup> userAuthorityGroupList = new ArrayList<UserAuthorityGroup>();

    private List<UserGroup> userGroupList = new ArrayList<UserGroup>();

    private List<MessageConversation> messageConversationList = new ArrayList<MessageConversation>();

    private List<OptionSet> optionSetList = new ArrayList<OptionSet>();

    private List<DataElementCategory> dataElementCategoryList = new ArrayList<DataElementCategory>();

    private List<DataElementCategoryOption> dataElementCategoryOptionList = new ArrayList<DataElementCategoryOption>();

    private List<DataElementCategoryCombo> dataElementCategoryComboList = new ArrayList<DataElementCategoryCombo>();

    private List<DataElementCategoryOptionCombo> dataElementCategoryOptionComboList = new ArrayList<DataElementCategoryOptionCombo>();

    private List<DataElement> dataElementList = new ArrayList<DataElement>();

    private List<DataElementGroup> dataElementGroupList = new ArrayList<DataElementGroup>();

    private List<DataElementGroupSet> dataElementGroupSetList = new ArrayList<DataElementGroupSet>();

    private List<Indicator> indicatorList = new ArrayList<Indicator>();

    private List<IndicatorGroup> indicatorGroupList = new ArrayList<IndicatorGroup>();

    private List<IndicatorGroupSet> indicatorGroupSetList = new ArrayList<IndicatorGroupSet>();

    private List<IndicatorType> indicatorTypeList = new ArrayList<IndicatorType>();

    private List<OrganisationUnit> organisationUnitList = new ArrayList<OrganisationUnit>();

    private List<OrganisationUnitGroup> organisationUnitGroupList = new ArrayList<OrganisationUnitGroup>();

    private List<OrganisationUnitGroupSet> organisationUnitGroupSetList = new ArrayList<OrganisationUnitGroupSet>();

    private List<OrganisationUnitLevel> organisationUnitLevelList = new ArrayList<OrganisationUnitLevel>();

    private List<ValidationRule> validationRuleList = new ArrayList<ValidationRule>();

    private List<ValidationRuleGroup> validationRuleGroupList = new ArrayList<ValidationRuleGroup>();

    private List<SqlView> sqlViewList = new ArrayList<SqlView>();

    private List<Chart> chartList = new ArrayList<Chart>();

    private List<Report> reportList = new ArrayList<Report>();

    private List<ReportTable> reportTableList = new ArrayList<ReportTable>();

    private List<MapView> mapViewList = new ArrayList<MapView>();

    private List<MapLegend> mapLegendList = new ArrayList<MapLegend>();

    private List<MapLegendSet> mapLegendSetList = new ArrayList<MapLegendSet>();

    private List<MapLayer> mapLayerList = new ArrayList<MapLayer>();

    private List<DataDictionary> dataDictionaryList = new ArrayList<DataDictionary>();

    private List<Section> sectionList = new ArrayList<Section>();

    private List<DataSet> dataSetList = new ArrayList<DataSet>();

    public MetaData()
    {
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "attributeTypes", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "attributeType", namespace = Dxf2Namespace.NAMESPACE )
    public List<Attribute> getAttributeList()
    {
        return attributeList;
    }

    public void setAttributeList( List<Attribute> attributeList )
    {
        this.attributeList = attributeList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "users", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "user", namespace = Dxf2Namespace.NAMESPACE )
    public List<User> getUserList()
    {
        return userList;
    }

    public void setUserList( List<User> userList )
    {
        this.userList = userList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "userAuthorityGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "userAuthorityGroup", namespace = Dxf2Namespace.NAMESPACE )
    public List<UserAuthorityGroup> getUserAuthorityGroupList()
    {
        return userAuthorityGroupList;
    }

    public void setUserAuthorityGroupList( List<UserAuthorityGroup> userAuthorityGroupList )
    {
        this.userAuthorityGroupList = userAuthorityGroupList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "userGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "userGroup", namespace = Dxf2Namespace.NAMESPACE )
    public List<UserGroup> getUserGroupList()
    {
        return userGroupList;
    }

    public void setUserGroupList( List<UserGroup> userGroupList )
    {
        this.userGroupList = userGroupList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "messageConversations", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "messageConversation", namespace = Dxf2Namespace.NAMESPACE )
    public List<MessageConversation> getMessageConversationList()
    {
        return messageConversationList;
    }

    public void setMessageConversationList( List<MessageConversation> messageConversationList )
    {
        this.messageConversationList = messageConversationList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataElements", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataElement", namespace = Dxf2Namespace.NAMESPACE )
    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }

    public void setDataElementList( List<DataElement> dataElementList )
    {
        this.dataElementList = dataElementList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "optionSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "optionSet", namespace = Dxf2Namespace.NAMESPACE )
    public List<OptionSet> getOptionSetList()
    {
        return optionSetList;
    }

    public void setOptionSetList( List<OptionSet> optionSetList )
    {
        this.optionSetList = optionSetList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataElementGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataElementGroup", namespace = Dxf2Namespace.NAMESPACE )
    public List<DataElementGroup> getDataElementGroupList()
    {
        return dataElementGroupList;
    }

    public void setDataElementGroupList( List<DataElementGroup> dataElementGroupList )
    {
        this.dataElementGroupList = dataElementGroupList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataElementGroupSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataElementGroupSet", namespace = Dxf2Namespace.NAMESPACE )
    public List<DataElementGroupSet> getDataElementGroupSetList()
    {
        return dataElementGroupSetList;
    }

    public void setDataElementGroupSetList( List<DataElementGroupSet> dataElementGroupSetList )
    {
        this.dataElementGroupSetList = dataElementGroupSetList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "concepts", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "concept", namespace = Dxf2Namespace.NAMESPACE )
    public List<Concept> getConceptList()
    {
        return conceptList;
    }

    public void setConceptList( List<Concept> conceptList )
    {
        this.conceptList = conceptList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categories", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "category", namespace = Dxf2Namespace.NAMESPACE )
    public List<DataElementCategory> getDataElementCategoryList()
    {
        return dataElementCategoryList;
    }

    public void setDataElementCategoryList( List<DataElementCategory> dataElementCategoryList )
    {
        this.dataElementCategoryList = dataElementCategoryList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categoryOptions", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "categoryOption", namespace = Dxf2Namespace.NAMESPACE )
    public List<DataElementCategoryOption> getDataElementCategoryOptionList()
    {
        return dataElementCategoryOptionList;
    }

    public void setDataElementCategoryOptionList( List<DataElementCategoryOption> dataElementCategoryOptionList )
    {
        this.dataElementCategoryOptionList = dataElementCategoryOptionList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categoryCombos", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "categoryCombo", namespace = Dxf2Namespace.NAMESPACE )
    public List<DataElementCategoryCombo> getDataElementCategoryComboList()
    {
        return dataElementCategoryComboList;
    }

    public void setDataElementCategoryComboList( List<DataElementCategoryCombo> dataElementCategoryComboList )
    {
        this.dataElementCategoryComboList = dataElementCategoryComboList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "categoryOptionCombos", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "categoryOptionCombo", namespace = Dxf2Namespace.NAMESPACE )
    public List<DataElementCategoryOptionCombo> getDataElementCategoryOptionComboList()
    {
        return dataElementCategoryOptionComboList;
    }

    public void setDataElementCategoryOptionComboList( List<DataElementCategoryOptionCombo> dataElementCategoryOptionComboList )
    {
        this.dataElementCategoryOptionComboList = dataElementCategoryOptionComboList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicators", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "indicator", namespace = Dxf2Namespace.NAMESPACE )
    public List<Indicator> getIndicatorList()
    {
        return indicatorList;
    }

    public void setIndicatorList( List<Indicator> indicatorList )
    {
        this.indicatorList = indicatorList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicatorGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "indicatorGroup", namespace = Dxf2Namespace.NAMESPACE )
    public List<IndicatorGroup> getIndicatorGroupList()
    {
        return indicatorGroupList;
    }

    public void setIndicatorGroupList( List<IndicatorGroup> indicatorGroupList )
    {
        this.indicatorGroupList = indicatorGroupList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicatorGroupSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "indicatorGroupSet", namespace = Dxf2Namespace.NAMESPACE )
    public List<IndicatorGroupSet> getIndicatorGroupSetList()
    {
        return indicatorGroupSetList;
    }

    public void setIndicatorGroupSetList( List<IndicatorGroupSet> indicatorGroupSetList )
    {
        this.indicatorGroupSetList = indicatorGroupSetList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "indicatorTypes", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "indicatorType", namespace = Dxf2Namespace.NAMESPACE )
    public List<IndicatorType> getIndicatorTypeList()
    {
        return indicatorTypeList;
    }

    public void setIndicatorTypeList( List<IndicatorType> indicatorTypeList )
    {
        this.indicatorTypeList = indicatorTypeList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnits", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "organisationUnit", namespace = Dxf2Namespace.NAMESPACE )
    public List<OrganisationUnit> getOrganisationUnitList()
    {
        return organisationUnitList;
    }

    public void setOrganisationUnitList( List<OrganisationUnit> organisationUnitList )
    {
        this.organisationUnitList = organisationUnitList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnitGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "organisationUnitGroup", namespace = Dxf2Namespace.NAMESPACE )
    public List<OrganisationUnitGroup> getOrganisationUnitGroupList()
    {
        return organisationUnitGroupList;
    }

    public void setOrganisationUnitGroupList( List<OrganisationUnitGroup> organisationUnitGroupList )
    {
        this.organisationUnitGroupList = organisationUnitGroupList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnitGroupSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "organisationUnitGroupSet", namespace = Dxf2Namespace.NAMESPACE )
    public List<OrganisationUnitGroupSet> getOrganisationUnitGroupSetList()
    {
        return organisationUnitGroupSetList;
    }

    public void setOrganisationUnitGroupSetList( List<OrganisationUnitGroupSet> organisationUnitGroupSetList )
    {
        this.organisationUnitGroupSetList = organisationUnitGroupSetList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "organisationUnitLevels", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "organisationUnitLevel", namespace = Dxf2Namespace.NAMESPACE )
    public List<OrganisationUnitLevel> getOrganisationUnitLevelList()
    {
        return organisationUnitLevelList;
    }

    public void setOrganisationUnitLevelList( List<OrganisationUnitLevel> organisationUnitLevelList )
    {
        this.organisationUnitLevelList = organisationUnitLevelList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "sections", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "section", namespace = Dxf2Namespace.NAMESPACE )
    public List<Section> getSectionList()
    {
        return sectionList;
    }

    public void setSectionList( List<Section> sectionList )
    {
        this.sectionList = sectionList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataSet", namespace = Dxf2Namespace.NAMESPACE )
    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }

    public void setDataSetList( List<DataSet> dataSetList )
    {
        this.dataSetList = dataSetList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "validationRules", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "validationRule", namespace = Dxf2Namespace.NAMESPACE )
    public List<ValidationRule> getValidationRuleList()
    {
        return validationRuleList;
    }

    public void setValidationRuleList( List<ValidationRule> validationRuleList )
    {
        this.validationRuleList = validationRuleList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "validationRuleGroups", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "validationRuleGroup", namespace = Dxf2Namespace.NAMESPACE )
    public List<ValidationRuleGroup> getValidationRuleGroupList()
    {
        return validationRuleGroupList;
    }

    public void setValidationRuleGroupList( List<ValidationRuleGroup> validationRuleGroupList )
    {
        this.validationRuleGroupList = validationRuleGroupList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "sqlViews", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "sqlView", namespace = Dxf2Namespace.NAMESPACE )
    public List<SqlView> getSqlViewList()
    {
        return sqlViewList;
    }

    public void setSqlViewList( List<SqlView> sqlViewList )
    {
        this.sqlViewList = sqlViewList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "charts", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "chart", namespace = Dxf2Namespace.NAMESPACE )
    public List<Chart> getChartList()
    {
        return chartList;
    }

    public void setChartList( List<Chart> chartList )
    {
        this.chartList = chartList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "reports", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "report", namespace = Dxf2Namespace.NAMESPACE )
    public List<Report> getReportList()
    {
        return reportList;
    }

    public void setReportList( List<Report> reportList )
    {
        this.reportList = reportList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "reportTables", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "reportTable", namespace = Dxf2Namespace.NAMESPACE )
    public List<ReportTable> getReportTableList()
    {
        return reportTableList;
    }

    public void setReportTableList( List<ReportTable> reportTableList )
    {
        this.reportTableList = reportTableList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "documents", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "document", namespace = Dxf2Namespace.NAMESPACE )
    public List<Document> getDocumentList()
    {
        return documentList;
    }

    public void setDocumentList( List<Document> documentList )
    {
        this.documentList = documentList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "constants", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "constant", namespace = Dxf2Namespace.NAMESPACE )
    public List<Constant> getConstantList()
    {
        return constantList;
    }

    public void setConstantList( List<Constant> constantList )
    {
        this.constantList = constantList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "maps", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "map", namespace = Dxf2Namespace.NAMESPACE )
    public List<MapView> getMapViewList()
    {
        return mapViewList;
    }

    public void setMapViewList( List<MapView> mapViewList )
    {
        this.mapViewList = mapViewList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "mapLegends", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "mapLegend", namespace = Dxf2Namespace.NAMESPACE )
    public List<MapLegend> getMapLegendList()
    {
        return mapLegendList;
    }

    public void setMapLegendList( List<MapLegend> mapLegendList )
    {
        this.mapLegendList = mapLegendList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "mapLegendSets", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "mapLegendSet", namespace = Dxf2Namespace.NAMESPACE )
    public List<MapLegendSet> getMapLegendSetList()
    {
        return mapLegendSetList;
    }

    public void setMapLegendSetList( List<MapLegendSet> mapLegendSetList )
    {
        this.mapLegendSetList = mapLegendSetList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "mapLayers", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "mapLayer", namespace = Dxf2Namespace.NAMESPACE )
    public List<MapLayer> getMapLayerList()
    {
        return mapLayerList;
    }

    public void setMapLayerList( List<MapLayer> mapLayerList )
    {
        this.mapLayerList = mapLayerList;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "dataDictionaries", namespace = Dxf2Namespace.NAMESPACE )
    @JacksonXmlProperty( localName = "dataDictionary", namespace = Dxf2Namespace.NAMESPACE )
    public List<DataDictionary> getDataDictionaryList()
    {
        return dataDictionaryList;
    }

    public void setDataDictionaryList( List<DataDictionary> dataDictionaryList )
    {
        this.dataDictionaryList = dataDictionaryList;
    }

    @Override
    public String toString()
    {
        return "MetaData{" +
            "attributeTypes=" + attributeList.size() +
            ", users=" + userList.size() +
            ", userAuthorityGroups=" + userAuthorityGroupList.size() +
            ", userGroups=" + userGroupList.size() +
            ", messageConversations=" + messageConversationList.size() +
            ", dataElements=" + dataElementList.size() +
            ", optionSets=" + optionSetList.size() +
            ", dataElementGroups=" + dataElementGroupList.size() +
            ", dataElementGroupSets=" + dataElementGroupSetList.size() +
            ", concepts=" + conceptList.size() +
            ", categories=" + dataElementCategoryList.size() +
            ", categoryOptions=" + dataElementCategoryOptionList.size() +
            ", categoryCombos=" + dataElementCategoryComboList.size() +
            ", categoryOptionCombos=" + dataElementCategoryOptionComboList.size() +
            ", indicators=" + indicatorList.size() +
            ", indicatorGroups=" + indicatorGroupList.size() +
            ", indicatorGroupSets=" + indicatorGroupSetList.size() +
            ", indicatorTypes=" + indicatorTypeList.size() +
            ", organisationUnits=" + organisationUnitList.size() +
            ", organisationUnitGroups=" + organisationUnitGroupList.size() +
            ", organisationUnitGroupSets=" + organisationUnitGroupSetList.size() +
            ", organisationUnitLevels=" + organisationUnitLevelList.size() +
            ", sections=" + sectionList.size() +
            ", dataSets=" + dataSetList.size() +
            ", validationRules=" + validationRuleList.size() +
            ", validationRuleGroups=" + validationRuleGroupList.size() +
            ", sqlViews=" + sqlViewList.size() +
            ", charts=" + chartList.size() +
            ", reports=" + reportList.size() +
            ", reportTables=" + reportTableList.size() +
            ", documents=" + documentList.size() +
            ", constants=" + constantList.size() +
            ", maps=" + mapViewList.size() +
            ", mapLegends=" + mapLegendList.size() +
            ", mapLegendSets=" + mapLegendSetList.size() +
            ", mapLayers=" + mapLayerList.size() +
            ", dataDictionaries=" + dataDictionaryList.size() +
            '}';
    }
}
