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

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class ExportOptions
{
    //--------------------------------------------------------------------------
    // Static helpers
    //--------------------------------------------------------------------------

    static private final ExportOptions defaultExportOptions = new ExportOptions();

    static public ExportOptions getDefaultExportOptions()
    {
        return defaultExportOptions;
    }

    //--------------------------------------------------------------------------
    // Fields for enabling/disabling options. This is very static ATM, but it is 
    // meant to be used by systems that auto-set beans, like Spring-MVC, which 
    // means it can be used as a simple argument in controller methods.
    //--------------------------------------------------------------------------

    private boolean attributeTypes = true;

    private boolean users = false;

    private boolean userAuthorityGroups = false;

    private boolean userGroups = false;

    private boolean messageConversations = false;

    private boolean dataElements = true;

    private boolean optionSets = true;

    private boolean dataElementGroups = true;

    private boolean dataElementGroupSets = true;

    private boolean concepts = true;

    private boolean categories = true;

    private boolean categoryOptions = true;

    private boolean categoryCombos = true;

    private boolean categoryOptionCombos = true;

    private boolean indicators = true;

    private boolean indicatorGroups = true;

    private boolean indicatorGroupSets = true;

    private boolean indicatorTypes = true;

    private boolean organisationUnits = true;

    private boolean organisationUnitGroups = true;

    private boolean organisationUnitGroupSets = true;

    private boolean organisationUnitLevels = true;

    private boolean dataSets = true;

    private boolean validationRules = true;

    private boolean validationRuleGroups = true;

    private boolean sqlViews = true;

    private boolean charts = true;

    private boolean reports = true;

    private boolean reportTables = true;

    private boolean documents = false;

    private boolean constants = true;

    private boolean maps = true;

    private boolean mapLegends = true;

    private boolean mapLegendSets = true;

    private boolean mapLayers = true;

    private boolean dataDictionaries = false;

    //---------------------------------------------------------------------------------------------------
    // Getters and setters for option
    //---------------------------------------------------------------------------------------------------

    public boolean isAttributeTypes()
    {
        return attributeTypes;
    }

    public void setAttributeTypes( boolean attributeTypes )
    {
        this.attributeTypes = attributeTypes;
    }

    public boolean isUsers()
    {
        return users;
    }

    public void setUsers( boolean users )
    {
        this.users = users;
    }

    public boolean isUserAuthorityGroups()
    {
        return userAuthorityGroups;
    }

    public void setUserAuthorityGroups( boolean userAuthorityGroups )
    {
        this.userAuthorityGroups = userAuthorityGroups;
    }

    public boolean isUserGroups()
    {
        return userGroups;
    }

    public void setUserGroups( boolean userGroups )
    {
        this.userGroups = userGroups;
    }

    public boolean isMessageConversations()
    {
        return messageConversations;
    }

    public void setMessageConversations( boolean messageConversations )
    {
        this.messageConversations = messageConversations;
    }

    public boolean isDataElements()
    {
        return dataElements;
    }

    public void setDataElements( boolean dataElements )
    {
        this.dataElements = dataElements;
    }

    public boolean isOptionSets()
    {
        return optionSets;
    }

    public void setOptionSets( boolean optionSets )
    {
        this.optionSets = optionSets;
    }

    public boolean isDataElementGroups()
    {
        return dataElementGroups;
    }

    public void setDataElementGroups( boolean dataElementGroups )
    {
        this.dataElementGroups = dataElementGroups;
    }

    public boolean isDataElementGroupSets()
    {
        return dataElementGroupSets;
    }

    public void setDataElementGroupSets( boolean dataElementGroupSets )
    {
        this.dataElementGroupSets = dataElementGroupSets;
    }

    public boolean isConcepts()
    {
        return concepts;
    }

    public void setConcepts( boolean concepts )
    {
        this.concepts = concepts;
    }

    public boolean isCategories()
    {
        return categories;
    }

    public void setCategories( boolean categories )
    {
        this.categories = categories;
    }

    public boolean isCategoryOptions()
    {
        return categoryOptions;
    }

    public void setCategoryOptions( boolean categoryOptions )
    {
        this.categoryOptions = categoryOptions;
    }

    public boolean isCategoryCombos()
    {
        return categoryCombos;
    }

    public void setCategoryCombos( boolean categoryCombos )
    {
        this.categoryCombos = categoryCombos;
    }

    public boolean isCategoryOptionCombos()
    {
        return categoryOptionCombos;
    }

    public void setCategoryOptionCombos( boolean categoryOptionCombos )
    {
        this.categoryOptionCombos = categoryOptionCombos;
    }

    public boolean isIndicators()
    {
        return indicators;
    }

    public void setIndicators( boolean indicators )
    {
        this.indicators = indicators;
    }

    public boolean isIndicatorGroups()
    {
        return indicatorGroups;
    }

    public void setIndicatorGroups( boolean indicatorGroups )
    {
        this.indicatorGroups = indicatorGroups;
    }

    public boolean isIndicatorGroupSets()
    {
        return indicatorGroupSets;
    }

    public void setIndicatorGroupSets( boolean indicatorGroupSets )
    {
        this.indicatorGroupSets = indicatorGroupSets;
    }

    public boolean isIndicatorTypes()
    {
        return indicatorTypes;
    }

    public void setIndicatorTypes( boolean indicatorTypes )
    {
        this.indicatorTypes = indicatorTypes;
    }

    public boolean isOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( boolean organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    public boolean isOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroups( boolean organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }

    public boolean isOrganisationUnitGroupSets()
    {
        return organisationUnitGroupSets;
    }

    public void setOrganisationUnitGroupSets( boolean organisationUnitGroupSets )
    {
        this.organisationUnitGroupSets = organisationUnitGroupSets;
    }

    public boolean isOrganisationUnitLevels()
    {
        return organisationUnitLevels;
    }

    public void setOrganisationUnitLevels( boolean organisationUnitLevels )
    {
        this.organisationUnitLevels = organisationUnitLevels;
    }

    public boolean isDataSets()
    {
        return dataSets;
    }

    public void setDataSets( boolean dataSets )
    {
        this.dataSets = dataSets;
    }

    public boolean isValidationRules()
    {
        return validationRules;
    }

    public void setValidationRules( boolean validationRules )
    {
        this.validationRules = validationRules;
    }

    public boolean isValidationRuleGroups()
    {
        return validationRuleGroups;
    }

    public void setValidationRuleGroups( boolean validationRuleGroups )
    {
        this.validationRuleGroups = validationRuleGroups;
    }

    public boolean isSqlViews()
    {
        return sqlViews;
    }

    public void setSqlViews( boolean sqlViews )
    {
        this.sqlViews = sqlViews;
    }

    public boolean isCharts()
    {
        return charts;
    }

    public void setCharts( boolean charts )
    {
        this.charts = charts;
    }

    public boolean isReports()
    {
        return reports;
    }

    public void setReports( boolean reports )
    {
        this.reports = reports;
    }

    public boolean isReportTables()
    {
        return reportTables;
    }

    public void setReportTables( boolean reportTables )
    {
        this.reportTables = reportTables;
    }

    public boolean isDocuments()
    {
        return documents;
    }

    public void setDocuments( boolean documents )
    {
        this.documents = documents;
    }

    public boolean isConstants()
    {
        return constants;
    }

    public void setConstants( boolean constants )
    {
        this.constants = constants;
    }

    public boolean isMaps()
    {
        return maps;
    }

    public void setMaps( boolean maps )
    {
        this.maps = maps;
    }

    public boolean isMapLegends()
    {
        return mapLegends;
    }

    public void setMapLegends( boolean mapLegends )
    {
        this.mapLegends = mapLegends;
    }

    public boolean isMapLegendSets()
    {
        return mapLegendSets;
    }

    public void setMapLegendSets( boolean mapLegendSets )
    {
        this.mapLegendSets = mapLegendSets;
    }

    public boolean isMapLayers()
    {
        return mapLayers;
    }

    public void setMapLayers( boolean mapLayers )
    {
        this.mapLayers = mapLayers;
    }

    public boolean isDataDictionaries()
    {
        return dataDictionaries;
    }

    public void setDataDictionaries( boolean dataDictionaries )
    {
        this.dataDictionaries = dataDictionaries;
    }
}
