package org.hisp.dhis.api.utils;

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

import javassist.util.proxy.ProxyObject;
import org.hisp.dhis.api.webdomain.Resource;
import org.hisp.dhis.api.webdomain.Resources;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.attribute.Attributes;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.Charts;
import org.hisp.dhis.common.*;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.Constants;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSets;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.Documents;
import org.hisp.dhis.indicator.*;
import org.hisp.dhis.mapping.*;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageConversations;
import org.hisp.dhis.organisationunit.*;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.Reports;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTables;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.sqlview.SqlViews;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroups;
import org.hisp.dhis.user.Users;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleGroups;
import org.hisp.dhis.validation.ValidationRules;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class WebLinkPopulator
{
    /**
     * Custom linkable object -> path mappings
     */
    private static Map<Class<? extends LinkableObject>, String> resourcePaths = new HashMap<Class<? extends LinkableObject>, String>();

    static
    {
        resourcePaths.put( Attributes.class, "attributeTypes" );
        resourcePaths.put( Attribute.class, "attributeTypes" );

        resourcePaths.put( DataElementCategories.class, "categories" );
        resourcePaths.put( DataElementCategory.class, "categories" );

        resourcePaths.put( DataElementCategoryOptions.class, "categoryOptions" );
        resourcePaths.put( DataElementCategoryOption.class, "categoryOptions" );

        resourcePaths.put( DataElementCategoryCombos.class, "categoryCombos" );
        resourcePaths.put( DataElementCategoryCombo.class, "categoryCombos" );

        resourcePaths.put( DataElementCategoryOptionCombos.class, "categoryOptionCombos" );
        resourcePaths.put( DataElementCategoryOptionCombo.class, "categoryOptionCombos" );

        resourcePaths.put( MapView.class, "maps" );
    }

    private String rootPath;

    public WebLinkPopulator( HttpServletRequest request )
    {
        rootPath = createRootPath( request );
    }

    public void addLinks( Object source )
    {
        if ( source instanceof Resources )
        {
            populateResources( (Resources) source );
        }
        else if ( source instanceof Charts )
        {
            populateCharts( (Charts) source, true );
        }
        else if ( source instanceof Chart )
        {
            populateChart( (Chart) source, true );
        }
        else if ( source instanceof DataSets )
        {
            populateDataSets( (DataSets) source, true );
        }
        else if ( source instanceof DataSet )
        {
            populateDataSet( (DataSet) source, true );
        }
        else if ( source instanceof OrganisationUnits )
        {
            populateOrganisationUnits( (OrganisationUnits) source, true );
        }
        else if ( source instanceof OrganisationUnit )
        {
            populateOrganisationUnit( (OrganisationUnit) source, true );
        }
        else if ( source instanceof OrganisationUnitGroups )
        {
            populateOrganisationUnitGroups( (OrganisationUnitGroups) source, true );
        }
        else if ( source instanceof OrganisationUnitGroup )
        {
            populateOrganisationUnitGroup( (OrganisationUnitGroup) source, true );
        }
        else if ( source instanceof OrganisationUnitGroupSets )
        {
            populateOrganisationUnitGroupSets( (OrganisationUnitGroupSets) source, true );
        }
        else if ( source instanceof OrganisationUnitGroupSet )
        {
            populateOrganisationUnitGroupSet( (OrganisationUnitGroupSet) source, true );
        }
        else if ( source instanceof Indicators )
        {
            populateIndicators( (Indicators) source, true );
        }
        else if ( source instanceof Indicator )
        {
            populateIndicator( (Indicator) source, true );
        }
        else if ( source instanceof IndicatorGroups )
        {
            populateIndicatorGroups( (IndicatorGroups) source, true );
        }
        else if ( source instanceof IndicatorGroup )
        {
            populateIndicatorGroup( (IndicatorGroup) source, true );
        }
        else if ( source instanceof IndicatorGroupSets )
        {
            populateIndicatorGroupSets( (IndicatorGroupSets) source, true );
        }
        else if ( source instanceof IndicatorType )
        {
            populateIndicatorType( (IndicatorType) source, true );
        }
        else if ( source instanceof IndicatorTypes )
        {
            populateIndicatorTypes( (IndicatorTypes) source, true );
        }
        else if ( source instanceof IndicatorGroupSet )
        {
            populateIndicatorGroupSet( (IndicatorGroupSet) source, true );
        }
        else if ( source instanceof DataElements )
        {
            populateDataElements( (DataElements) source, true );
        }
        else if ( source instanceof DataElement )
        {
            populateDataElement( (DataElement) source, true );
        }
        else if ( source instanceof DataElementGroups )
        {
            populateDataElementGroups( (DataElementGroups) source, true );
        }
        else if ( source instanceof DataElementGroup )
        {
            populateDataElementGroup( (DataElementGroup) source, true );
        }
        else if ( source instanceof DataElementGroupSets )
        {
            populateDataElementGroupSets( (DataElementGroupSets) source, true );
        }
        else if ( source instanceof DataElementGroupSet )
        {
            populateDataElementGroupSet( (DataElementGroupSet) source, true );
        }
        else if ( source instanceof DataElementCategories )
        {
            populateDataElementCategories( (DataElementCategories) source, true );
        }
        else if ( source instanceof DataElementCategory )
        {
            populateDataElementCategory( (DataElementCategory) source, true );
        }
        else if ( source instanceof DataElementCategoryCombos )
        {
            populateDataElementCategoryCombos( (DataElementCategoryCombos) source, true );
        }
        else if ( source instanceof DataElementCategoryCombo )
        {
            populateDataElementCategoryCombo( (DataElementCategoryCombo) source, true );
        }
        else if ( source instanceof DataElementCategoryOptions )
        {
            populateDataElementCategoryOptions( (DataElementCategoryOptions) source, true );
        }
        else if ( source instanceof DataElementCategoryOption )
        {
            populateDataElementCategoryOption( (DataElementCategoryOption) source, true );
        }
        else if ( source instanceof DataElementCategoryOptionCombos )
        {
            populateDataElementCategoryOptionCombos( (DataElementCategoryOptionCombos) source, true );
        }
        else if ( source instanceof DataElementCategoryOptionCombo )
        {
            populateDataElementCategoryOptionCombo( (DataElementCategoryOptionCombo) source, true );
        }
        else if ( source instanceof Attributes )
        {
            populateAttributes( (Attributes) source, true );
        }
        else if ( source instanceof Attribute )
        {
            populateAttribute( (Attribute) source, true );
        }
        else if ( source instanceof Maps )
        {
            populateMaps( (Maps) source, true );
        }
        else if ( source instanceof MapView )
        {
            populateMap( (MapView) source, true );
        }
        else if ( source instanceof MapLegends )
        {
            populateMapLegends( (MapLegends) source, true );
        }
        else if ( source instanceof MapLegend )
        {
            populateMapLegend( (MapLegend) source, true );
        }
        else if ( source instanceof MapLegendSets )
        {
            populateMapLegendSets( (MapLegendSets) source, true );
        }
        else if ( source instanceof MapLegendSet )
        {
            populateMapLegendSet( (MapLegendSet) source, true );
        }
        else if ( source instanceof Documents )
        {
            populateDocuments( (Documents) source, true );
        }
        else if ( source instanceof Document )
        {
            populateDocument( (Document) source, true );
        }
        else if ( source instanceof ValidationRules )
        {
            populateValidationRules( (ValidationRules) source, true );
        }
        else if ( source instanceof ValidationRule )
        {
            populateValidationRule( (ValidationRule) source, true );
        }
        else if ( source instanceof ValidationRuleGroups )
        {
            populateValidationRuleGroups( (ValidationRuleGroups) source, true );
        }
        else if ( source instanceof ValidationRuleGroup )
        {
            populateValidationRuleGroup( (ValidationRuleGroup) source, true );
        }
        else if ( source instanceof Constants )
        {
            populateConstants( (Constants) source, true );
        }
        else if ( source instanceof Constant )
        {
            populateConstant( (Constant) source, true );
        }
        else if ( source instanceof SqlViews )
        {
            populateSqlViews( (SqlViews) source, true );
        }
        else if ( source instanceof SqlView )
        {
            populateSqlView( (SqlView) source, true );
        }
        else if ( source instanceof Reports )
        {
            populateReports( (Reports) source, true );
        }
        else if ( source instanceof Report )
        {
            populateReport( (Report) source, true );
        }
        else if ( source instanceof Users )
        {
            populateUsers( (Users) source, true );
        }
        else if ( source instanceof User )
        {
            populateUser( (User) source, true );
        }
        else if ( source instanceof UserGroups )
        {
            populateUserGroups( (UserGroups) source, true );
        }
        else if ( source instanceof UserGroup )
        {
            populateUserGroup( (UserGroup) source, true );
        }
        else if ( source instanceof ReportTables )
        {
            populateReportTables( (ReportTables) source, true );
        }
        else if ( source instanceof ReportTable )
        {
            populateReportTable( (ReportTable) source, true );
        }
        else if ( source instanceof MessageConversations )
        {
            populateMessageConversations( (MessageConversations) source, true );
        }
        else if ( source instanceof MessageConversation )
        {
            populateMessageConversation( (MessageConversation) source, true );
        }

        if ( source instanceof BaseCollection )
        {
            BaseCollection baseCollection = (BaseCollection) source;

            if ( baseCollection.getPager() != null )
            {
                String basePath = getBasePath( source.getClass() );
                Pager pager = baseCollection.getPager();

                if ( pager.getPage() < pager.getPageCount() )
                {
                    pager.setNextPage( basePath + "?page=" + (pager.getPage() + 1) );
                }

                if ( pager.getPage() > 1 )
                {
                    if ( (pager.getPage() - 1) == 1 )
                    {
                        pager.setPrevPage( basePath );
                    }
                    else
                    {
                        pager.setPrevPage( basePath + "?page=" + (pager.getPage() - 1) );
                    }

                }
            }
        }
    }

    private void populateMessageConversations( MessageConversations messageConversations, boolean root )
    {
        messageConversations.setLink( getBasePath( messageConversations.getClass() ) );

        if ( root )
        {
            for ( MessageConversation messageConversation : messageConversations.getMessageConversations() )
            {
                populateMessageConversation( messageConversation, false );
            }
        }
    }

    private void populateMessageConversation( MessageConversation messageConversation, boolean root )
    {
        populateIdentifiableObject( messageConversation );

        if ( root )
        {
            handleIdentifiableObjectCollection( messageConversation.getUsers() );
        }
    }

    private void populateReportTables( ReportTables reportTables, boolean root )
    {
        reportTables.setLink( getBasePath( reportTables.getClass() ) );

        if ( root )
        {
            for ( ReportTable reportTable : reportTables.getReportTables() )
            {
                populateReportTable( reportTable, false );
            }
        }
    }

    private void populateReportTable( ReportTable reportTable, boolean root )
    {
        populateIdentifiableObject( reportTable );

        if ( root )
        {
            handleIdentifiableObjectCollection( reportTable.getPeriods() );
            handleIdentifiableObjectCollection( reportTable.getCategoryOptionCombos() );
            handleIdentifiableObjectCollection( reportTable.getDataElements() );
            handleIdentifiableObjectCollection( reportTable.getIndicators() );
            handleIdentifiableObjectCollection( reportTable.getDataSets() );
        }
    }

    private void populateUsers( Users users, boolean root )
    {
        users.setLink( getBasePath( users.getClass() ) );

        if ( root )
        {
            for ( User user : users.getUsers() )
            {
                populateUser( user, false );
            }
        }
    }

    private void populateUser( User user, boolean root )
    {
        populateIdentifiableObject( user );

        if ( root )
        {
            handleIdentifiableObjectCollection( user.getOrganisationUnits() );
        }
    }

    private void populateUserGroups( UserGroups userGroups, boolean root )
    {
        userGroups.setLink( getBasePath( userGroups.getClass() ) );

        if ( root )
        {
            for ( UserGroup userGroup : userGroups.getUserGroups() )
            {
                populateUserGroup( userGroup, false );
            }
        }
    }

    private void populateUserGroup( UserGroup userGroup, boolean root )
    {
        populateIdentifiableObject( userGroup );

        if ( root )
        {
            handleIdentifiableObjectCollection( userGroup.getMembers() );
        }
    }

    private void populateSqlViews( SqlViews sqlViews, boolean root )
    {
        sqlViews.setLink( getBasePath( sqlViews.getClass() ) );

        if ( root )
        {
            for ( SqlView sqlView : sqlViews.getSqlViews() )
            {
                populateSqlView( sqlView, false );
            }
        }
    }

    private void populateSqlView( SqlView sqlView, boolean root )
    {
        populateIdentifiableObject( sqlView );

        if ( root )
        {

        }
    }

    private void populateConstants( Constants constants, boolean root )
    {
        constants.setLink( getBasePath( constants.getClass() ) );

        if ( root )
        {
            for ( Constant constant : constants.getConstants() )
            {
                populateConstant( constant, false );
            }
        }
    }

    private void populateConstant( Constant constant, boolean root )
    {
        populateIdentifiableObject( constant );

        if ( root )
        {

        }
    }

    private void populateDocuments( Documents documents, boolean root )
    {
        documents.setLink( getBasePath( documents.getClass() ) );

        if ( root )
        {
            for ( Document document : documents.getDocuments() )
            {
                populateDocument( document, false );
            }
        }
    }

    private void populateDocument( Document document, boolean root )
    {
        populateIdentifiableObject( document );

        if ( root )
        {

        }
    }

    private void populateValidationRules( ValidationRules validationRules, boolean root )
    {
        validationRules.setLink( getBasePath( validationRules.getClass() ) );

        if ( root )
        {
            for ( ValidationRule validationRule : validationRules.getValidationRules() )
            {
                populateValidationRule( validationRule, false );
            }
        }
    }

    private void populateValidationRule( ValidationRule validationRule, boolean root )
    {
        populateIdentifiableObject( validationRule );

        if ( root )
        {
            handleIdentifiableObjectCollection( validationRule.getGroups() );
        }
    }

    private void populateValidationRuleGroups( ValidationRuleGroups validationRuleGroups, boolean root )
    {
        validationRuleGroups.setLink( getBasePath( validationRuleGroups.getClass() ) );

        if ( root )
        {
            for ( ValidationRuleGroup validationRuleGroup : validationRuleGroups.getValidationRuleGroups() )
            {
                populateValidationRuleGroup( validationRuleGroup, false );
            }
        }
    }

    private void populateValidationRuleGroup( ValidationRuleGroup validationRuleGroup, boolean root )
    {
        populateIdentifiableObject( validationRuleGroup );

        if ( root )
        {
            handleIdentifiableObjectCollection( validationRuleGroup.getMembers() );
        }
    }

    private void populateIndicatorTypes( IndicatorTypes indicatorTypes, boolean root )
    {
        indicatorTypes.setLink( getBasePath( indicatorTypes.getClass() ) );

        if ( root )
        {
            for ( IndicatorType indicatorType : indicatorTypes.getIndicatorTypes() )
            {
                populateIndicatorType( indicatorType, false );
            }
        }
    }

    private void populateIndicatorType( IndicatorType indicatorType, boolean root )
    {
        populateIdentifiableObject( indicatorType );

        if ( root )
        {

        }
    }

    private void populateMaps( Maps maps, boolean root )
    {
        maps.setLink( getBasePath( maps.getClass() ) );

        if ( root )
        {
            for ( MapView map : maps.getMaps() )
            {
                populateMap( map, false );
            }
        }
    }

    private void populateMap( MapView map, boolean root )
    {
        populateIdentifiableObject( map );

        if ( root )
        {
            populateIdentifiableObject( map.getDataElement() );
            populateIdentifiableObject( map.getDataElementGroup() );
            populateIdentifiableObject( map.getIndicator() );
            populateIdentifiableObject( map.getIndicatorGroup() );
            populateIdentifiableObject( map.getOrganisationUnitLevel() );
            populateIdentifiableObject( map.getParentOrganisationUnit() );
            populateIdentifiableObject( map.getPeriod() );
        }
    }

    private void populateMapLegends( MapLegends mapLegends, boolean root )
    {
        mapLegends.setLink( getBasePath( mapLegends.getClass() ) );

        if ( root )
        {
            for ( MapLegend mapLegend : mapLegends.getMapLegends() )
            {
                populateMapLegend( mapLegend, false );
            }
        }
    }

    private void populateMapLegend( MapLegend mapLegend, boolean root )
    {
        populateIdentifiableObject( mapLegend );

        if ( root )
        {
        }
    }

    private void populateMapLegendSets( MapLegendSets mapLegendSets, boolean root )
    {
        mapLegendSets.setLink( getBasePath( mapLegendSets.getClass() ) );

        if ( root )
        {
            for ( MapLegendSet mapLegendSet : mapLegendSets.getMapLegendSets() )
            {
                populateMapLegendSet( mapLegendSet, false );
            }
        }
    }

    private void populateMapLegendSet( MapLegendSet mapLegendSet, boolean root )
    {
        populateIdentifiableObject( mapLegendSet );

        if ( root )
        {
            handleIdentifiableObjectCollection( mapLegendSet.getIndicators() );
            handleIdentifiableObjectCollection( mapLegendSet.getDataElements() );
            handleIdentifiableObjectCollection( mapLegendSet.getMapLegends() );
        }
    }

    private void populateResources( Resources resources )
    {
        resources.setLink( getBasePath( Resources.class ) );

        for ( Resource resource : resources.getResources() )
        {
            resource.setLink( getBasePath( resource.getClazz() ) );
        }
    }

    private void populateAttributes( Attributes attributes, boolean root )
    {
        attributes.setLink( getBasePath( Attributes.class ) );

        if ( root )
        {
            for ( Attribute attribute : attributes.getAttributes() )
            {
                populateAttribute( attribute, false );
            }
        }
    }

    private void populateAttribute( Attribute attribute, boolean root )
    {
        attribute.setLink( getPathWithUid( attribute ) );

        if ( root )
        {

        }
    }

    private void populateDataElementCategories( DataElementCategories dataElementCategories, boolean root )
    {
        dataElementCategories.setLink( getBasePath( DataElementCategories.class ) );

        if ( root )
        {
            for ( DataElementCategory dataElementCategory : dataElementCategories.getCategories() )
            {
                populateDataElementCategory( dataElementCategory, false );
            }
        }
    }

    private void populateDataElementCategory( DataElementCategory dataElementCategory, boolean root )
    {
        dataElementCategory.setLink( getPathWithUid( dataElementCategory ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( dataElementCategory.getCategoryOptions() );
        }
    }

    private void populateDataElementCategoryCombos( DataElementCategoryCombos dataElementCategoryCombos, boolean root )
    {
        dataElementCategoryCombos.setLink( getBasePath( DataElementCategoryCombos.class ) );

        if ( root )
        {
            for ( DataElementCategoryCombo dataElementCategoryCombo : dataElementCategoryCombos
                .getCategoryCombos() )
            {
                populateDataElementCategoryCombo( dataElementCategoryCombo, false );
            }
        }
    }

    private void populateDataElementCategoryCombo( DataElementCategoryCombo dataElementCategoryCombo, boolean root )
    {
        dataElementCategoryCombo.setLink( getPathWithUid( dataElementCategoryCombo ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( dataElementCategoryCombo.getOptionCombos() );
            handleIdentifiableObjectCollection( dataElementCategoryCombo.getCategories() );
        }
    }

    private void populateDataElementCategoryOptions( DataElementCategoryOptions dataElementCategoryOptions, boolean root )
    {
        dataElementCategoryOptions.setLink( getBasePath( DataElementCategoryOptions.class ) );

        if ( root )
        {
            for ( DataElementCategoryOption dataElementCategoryOption : dataElementCategoryOptions.getCategoryOptions() )
            {
                populateDataElementCategoryOption( dataElementCategoryOption, false );
            }
        }
    }

    private void populateDataElementCategoryOption( DataElementCategoryOption dataElementCategoryOption, boolean root )
    {
        dataElementCategoryOption.setLink( getPathWithUid( dataElementCategoryOption ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( dataElementCategoryOption.getCategoryOptionCombos() );
            populateIdentifiableObject( dataElementCategoryOption.getCategory() );
        }
    }

    private void populateDataElementCategoryOptionCombos(
        DataElementCategoryOptionCombos dataElementCategoryOptionCombos, boolean root )
    {
        dataElementCategoryOptionCombos.setLink( getBasePath( DataElementCategoryOptionCombos.class ) );

        if ( root )
        {
            for ( DataElementCategoryOptionCombo dataElementCategoryOptionCombo : dataElementCategoryOptionCombos
                .getCategoryOptionCombos() )
            {
                populateDataElementCategoryOptionCombo( dataElementCategoryOptionCombo, false );
            }
        }
    }

    private void populateDataElementCategoryOptionCombo( DataElementCategoryOptionCombo dataElementCategoryOptionCombo,
                                                         boolean root )
    {
        dataElementCategoryOptionCombo.setLink( getPathWithUid( dataElementCategoryOptionCombo ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( dataElementCategoryOptionCombo.getCategoryOptions() );
            populateIdentifiableObject( dataElementCategoryOptionCombo.getCategoryCombo() );
        }
    }

    private void populateDataElements( DataElements dataElements, boolean root )
    {
        dataElements.setLink( getBasePath( DataElements.class ) );

        if ( root )
        {
            for ( DataElement dataElement : dataElements.getDataElements() )
            {
                populateDataElement( dataElement, false );
            }
        }
    }

    private void populateDataElement( DataElement dataElement, boolean root )
    {
        dataElement.setLink( getPathWithUid( dataElement ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( dataElement.getGroups() );
            handleIdentifiableObjectCollection( dataElement.getDataSets() );
            handleAttributeValueCollection( dataElement.getAttributeValues() );
            populateIdentifiableObject( dataElement.getCategoryCombo() );
        }
    }

    private void populateDataElementGroups( DataElementGroups dataElementGroups, boolean root )
    {
        dataElementGroups.setLink( getBasePath( DataElementGroups.class ) );

        if ( root )
        {
            for ( DataElementGroup dataElementGroup : dataElementGroups.getDataElementGroups() )
            {
                populateDataElementGroup( dataElementGroup, false );
            }
        }
    }

    private void populateDataElementGroup( DataElementGroup dataElementGroup, boolean root )
    {
        dataElementGroup.setLink( getPathWithUid( dataElementGroup ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( dataElementGroup.getMembers() );
            populateIdentifiableObject( dataElementGroup.getGroupSet() );
        }
    }

    private void populateDataElementGroupSets( DataElementGroupSets dataElementGroupSets, boolean root )
    {
        dataElementGroupSets.setLink( getBasePath( DataElementGroupSets.class ) );

        if ( root )
        {
            for ( DataElementGroupSet dataElementGroupSet : dataElementGroupSets.getDataElementGroupSets() )
            {
                populateDataElementGroupSet( dataElementGroupSet, false );
            }
        }
    }

    private void populateDataElementGroupSet( DataElementGroupSet dataElementGroupSet, boolean root )
    {
        dataElementGroupSet.setLink( getPathWithUid( dataElementGroupSet ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( dataElementGroupSet.getMembers() );
        }
    }

    private void populateIndicators( Indicators indicators, boolean root )
    {
        indicators.setLink( getBasePath( Indicators.class ) );

        if ( root )
        {
            for ( Indicator indicator : indicators.getIndicators() )
            {
                populateIndicator( indicator, false );
            }
        }
    }

    private void populateIndicator( Indicator indicator, boolean root )
    {
        indicator.setLink( getPathWithUid( indicator ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( indicator.getGroups() );
            handleIdentifiableObjectCollection( indicator.getDataSets() );
            handleAttributeValueCollection( indicator.getAttributeValues() );
        }
    }

    private void populateIndicatorGroups( IndicatorGroups indicatorGroups, boolean root )
    {
        indicatorGroups.setLink( getBasePath( IndicatorGroups.class ) );

        if ( root )
        {
            for ( IndicatorGroup indicatorGroup : indicatorGroups.getIndicatorGroups() )
            {
                populateIndicatorGroup( indicatorGroup, false );
            }
        }
    }

    private void populateIndicatorGroup( IndicatorGroup indicatorGroup, boolean root )
    {
        indicatorGroup.setLink( getPathWithUid( indicatorGroup ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( indicatorGroup.getMembers() );
            populateIdentifiableObject( indicatorGroup.getGroupSet() );
        }
    }

    private void populateIndicatorGroupSets( IndicatorGroupSets indicatorGroupSets, boolean root )
    {
        indicatorGroupSets.setLink( getBasePath( IndicatorGroupSets.class ) );

        if ( root )
        {
            for ( IndicatorGroupSet indicatorGroupSet : indicatorGroupSets.getIndicatorGroupSets() )
            {
                populateIndicatorGroupSet( indicatorGroupSet, false );
            }
        }
    }

    private void populateIndicatorGroupSet( IndicatorGroupSet indicatorGroupSet, boolean root )
    {
        indicatorGroupSet.setLink( getPathWithUid( indicatorGroupSet ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( indicatorGroupSet.getMembers() );
        }
    }

    private void populateOrganisationUnitGroups( OrganisationUnitGroups organisationUnitGroups, boolean root )
    {
        organisationUnitGroups.setLink( getBasePath( OrganisationUnitGroups.class ) );

        if ( root )
        {
            for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitGroups.getOrganisationUnitGroups() )
            {
                populateOrganisationUnitGroup( organisationUnitGroup, false );
            }
        }
    }

    private void populateOrganisationUnitGroup( OrganisationUnitGroup organisationUnitGroup, boolean root )
    {
        organisationUnitGroup.setLink( getPathWithUid( organisationUnitGroup ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( organisationUnitGroup.getMembers() );
            populateIdentifiableObject( organisationUnitGroup.getGroupSet() );
        }
    }

    private void populateOrganisationUnitGroupSets( OrganisationUnitGroupSets organisationUnitGroupSets, boolean root )
    {
        organisationUnitGroupSets.setLink( getBasePath( OrganisationUnitGroupSets.class ) );

        if ( root )
        {
            for ( OrganisationUnitGroupSet organisationUnitGroupSet : organisationUnitGroupSets
                .getOrganisationUnitGroupSets() )
            {
                populateOrganisationUnitGroupSet( organisationUnitGroupSet, false );
            }
        }
    }

    private void populateOrganisationUnitGroupSet( OrganisationUnitGroupSet organisationUnitGroupSet, boolean root )
    {
        organisationUnitGroupSet.setLink( getPathWithUid( organisationUnitGroupSet ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( organisationUnitGroupSet.getOrganisationUnitGroups() );
        }
    }

    private void populateOrganisationUnits( OrganisationUnits organisationUnits, boolean root )
    {
        organisationUnits.setLink( getBasePath( OrganisationUnits.class ) );

        if ( root )
        {
            for ( OrganisationUnit organisationUnit : organisationUnits.getOrganisationUnits() )
            {
                populateOrganisationUnit( organisationUnit, false );
            }
        }
    }

    private void populateOrganisationUnit( OrganisationUnit organisationUnit, boolean root )
    {
        organisationUnit.setLink( getPathWithUid( organisationUnit ) );

        if ( root )
        {
            populateIdentifiableObject( organisationUnit.getParent() );
            handleIdentifiableObjectCollection( organisationUnit.getDataSets() );
            handleIdentifiableObjectCollection( organisationUnit.getGroups() );
            handleAttributeValueCollection( organisationUnit.getAttributeValues() );
        }
    }

    private void populateDataSets( DataSets dataSets, boolean root )
    {
        dataSets.setLink( getBasePath( DataSets.class ) );

        if ( root )
        {
            for ( DataSet dataSet : dataSets.getDataSets() )
            {
                populateDataSet( dataSet, false );
            }
        }
    }

    private void populateDataSet( DataSet dataSet, boolean root )
    {
        dataSet.setLink( getPathWithUid( dataSet ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( dataSet.getDataElements() );
            handleIdentifiableObjectCollection( dataSet.getIndicators() );
            handleIdentifiableObjectCollection( dataSet.getSources() );
        }
    }

    private void populateCharts( Charts charts, boolean root )
    {
        charts.setLink( getBasePath( Chart.class ) );

        if ( root )
        {
            for ( Chart chart : charts.getCharts() )
            {
                populateChart( chart, false );
            }
        }
    }

    private void populateChart( Chart chart, boolean root )
    {
        chart.setLink( getPathWithUid( chart ) );

        if ( root )
        {
            handleIdentifiableObjectCollection( chart.getIndicators() );
            handleIdentifiableObjectCollection( chart.getDataElements() );
            handleIdentifiableObjectCollection( chart.getOrganisationUnits() );
            handleIdentifiableObjectCollection( chart.getDataSets() );
        }
    }

    private void handleAttributeValueCollection( Set<AttributeValue> attributeValues )
    {
        if ( attributeValues != null )
        {
            for ( AttributeValue attributeValue : attributeValues )
            {
                populateIdentifiableObject( attributeValue.getAttribute() );
            }
        }
    }

    private void populateReports( Reports reports, boolean root )
    {
        reports.setLink( getBasePath( Report.class ) );

        if ( root )
        {
            for ( Report report : reports.getReports() )
            {
                populateReport( report, false );
            }
        }
    }

    private void populateReport( Report report, boolean root )
    {
        report.setLink( getPathWithUid( report ) );

        if ( root )
        {
            populateIdentifiableObject( report.getReportTable() );
        }
    }

    public void handleIdentifiableObjectCollection( Collection<? extends BaseIdentifiableObject> identifiableObjects )
    {
        if ( identifiableObjects != null )
        {
            for ( BaseIdentifiableObject baseIdentifiableObject : identifiableObjects )
            {
                populateIdentifiableObject( baseIdentifiableObject );
            }
        }
    }

    private void populateIdentifiableObject( BaseIdentifiableObject baseIdentifiableObject )
    {
        if ( baseIdentifiableObject != null )
        {
            baseIdentifiableObject.setLink( getPathWithUid( baseIdentifiableObject ) );
        }
    }

    private String getPathWithUid( BaseIdentifiableObject baseIdentifiableObject )
    {
        return getBasePath( baseIdentifiableObject.getClass() ) + "/" + baseIdentifiableObject.getUid();
    }

    private String getBasePath( Class<?> clazz )
    {
        if ( ProxyObject.class.isAssignableFrom( clazz ) )
        {
            clazz = clazz.getSuperclass();
        }

        String resourcePath = resourcePaths.get( clazz );

//        // in some cases, the class is a dynamic subclass (usually subclassed
//        // with javaassist), so
//        // we need to fetch the superClass instead.
//        if ( resourcePath == null )
//        {
//            resourcePath = resourcePaths.get( clazz.getSuperclass() );
//        }

        if ( resourcePath == null )
        {
            resourcePath = getPath( clazz );
        }

        return rootPath + "/" + resourcePath;
    }

    public static String createRootPath( HttpServletRequest request )
    {
        StringBuilder builder = new StringBuilder();
        String xForwardedProto = request.getHeader( "X-Forwarded-Proto" );
        String xForwardedPort = request.getHeader( "X-Forwarded-Port" );

        if ( xForwardedProto != null && (xForwardedProto.equalsIgnoreCase( "http" ) || xForwardedProto.equalsIgnoreCase( "https" )) )
        {
            builder.append( xForwardedProto );
        }
        else
        {
            builder.append( request.getScheme() );
        }


        builder.append( "://" ).append( request.getServerName() );

        int port;

        try
        {
            port = Integer.parseInt( xForwardedPort );
        } catch ( NumberFormatException e )
        {
            port = request.getServerPort();
        }

        if ( port != 80 && port != 443 )
        {
            builder.append( ":" ).append( port );
        }

        builder.append( request.getContextPath() );
        builder.append( request.getServletPath() );

        return builder.toString();
    }

    public static String getPath( Class<?> clazz )
    {
        String path = clazz.getSimpleName();

        path = path.substring( 0, 1 ).toLowerCase() + path.substring( 1 );

        if ( IdentifiableObject.class.isAssignableFrom( clazz ) )
        {
            path += "s";
        }
        return path;
    }

}
