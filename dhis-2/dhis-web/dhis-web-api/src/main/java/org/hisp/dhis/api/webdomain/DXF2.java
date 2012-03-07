package org.hisp.dhis.api.webdomain;

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

import org.codehaus.jackson.annotate.JsonProperty;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "DXF2", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DXF2
{
    private List<DataElement> dataElements = new ArrayList<DataElement>();

    private List<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>();

    private List<DataElementGroupSet> dataElementGroupSets = new ArrayList<DataElementGroupSet>();

    private List<DataElementCategory> categories = new ArrayList<DataElementCategory>();

    private List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();

    private List<DataElementCategoryCombo> categoryCombos = new ArrayList<DataElementCategoryCombo>();

    private List<DataElementCategoryOptionCombo> categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();

    private List<Indicator> indicators = new ArrayList<Indicator>();

    private List<IndicatorGroup> indicatorGroups = new ArrayList<IndicatorGroup>();

    private List<IndicatorGroupSet> indicatorGroupSets = new ArrayList<IndicatorGroupSet>();

    private List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();

    private List<OrganisationUnitGroup> organisationUnitGroups = new ArrayList<OrganisationUnitGroup>();

    private List<OrganisationUnitGroupSet> organisationUnitGroupSets = new ArrayList<OrganisationUnitGroupSet>();

    private List<OrganisationUnitLevel> organisationUnitLevels = new ArrayList<OrganisationUnitLevel>();

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    private List<ValidationRule> validationRules = new ArrayList<ValidationRule>();

    private List<ValidationRuleGroup> validationRuleGroups = new ArrayList<ValidationRuleGroup>();

    @XmlElementWrapper( name = "dataElements" )
    @XmlElement( name = "dataElement" )
    @JsonProperty( value = "dataElements" )
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    @XmlElementWrapper( name = "dataElementGroups" )
    @XmlElement( name = "dataElementGroup" )
    @JsonProperty( value = "dataElementGroups" )
    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    public void setDataElementGroups( List<DataElementGroup> dataElementGroups )
    {
        this.dataElementGroups = dataElementGroups;
    }

    @XmlElementWrapper( name = "dataElementGroupSets" )
    @XmlElement( name = "dataElementGroupSet" )
    @JsonProperty( value = "dataElementGroupSets" )
    public List<DataElementGroupSet> getDataElementGroupSets()
    {
        return dataElementGroupSets;
    }

    public void setDataElementGroupSets( List<DataElementGroupSet> dataElementGroupSets )
    {
        this.dataElementGroupSets = dataElementGroupSets;
    }

    @XmlElementWrapper( name = "categories" )
    @XmlElement( name = "category" )
    @JsonProperty( value = "categories" )
    public List<DataElementCategory> getCategories()
    {
        return categories;
    }

    public void setCategories( List<DataElementCategory> categories )
    {
        this.categories = categories;
    }

    @XmlElementWrapper( name = "categoryOptions" )
    @XmlElement( name = "categoryOption" )
    @JsonProperty( value = "categoryOptions" )
    public List<DataElementCategoryOption> getCategoryOptions()
    {
        return categoryOptions;
    }

    public void setCategoryOptions( List<DataElementCategoryOption> categoryOptions )
    {
        this.categoryOptions = categoryOptions;
    }

    @XmlElementWrapper( name = "categoryCombos" )
    @XmlElement( name = "categoryCombo" )
    @JsonProperty( value = "categoryCombos" )
    public List<DataElementCategoryCombo> getCategoryCombos()
    {
        return categoryCombos;
    }

    public void setCategoryCombos( List<DataElementCategoryCombo> categoryCombos )
    {
        this.categoryCombos = categoryCombos;
    }

    @XmlElementWrapper( name = "categoryOptionCombos" )
    @XmlElement( name = "categoryOptionCombo" )
    @JsonProperty( value = "categoryOptionCombos" )
    public List<DataElementCategoryOptionCombo> getCategoryOptionCombos()
    {
        return categoryOptionCombos;
    }

    public void setCategoryOptionCombos( List<DataElementCategoryOptionCombo> categoryOptionCombos )
    {
        this.categoryOptionCombos = categoryOptionCombos;
    }

    @XmlElementWrapper( name = "indicators" )
    @XmlElement( name = "indicator" )
    @JsonProperty( value = "indicators" )
    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( List<Indicator> indicators )
    {
        this.indicators = indicators;
    }

    @XmlElementWrapper( name = "indicatorGroups" )
    @XmlElement( name = "indicatorGroup" )
    @JsonProperty( value = "indicatorGroups" )
    public List<IndicatorGroup> getIndicatorGroups()
    {
        return indicatorGroups;
    }

    public void setIndicatorGroups( List<IndicatorGroup> indicatorGroups )
    {
        this.indicatorGroups = indicatorGroups;
    }

    @XmlElementWrapper( name = "indicatorGroupSets" )
    @XmlElement( name = "indicatorGroupSet" )
    @JsonProperty( value = "indicatorGroupSets" )
    public List<IndicatorGroupSet> getIndicatorGroupSets()
    {
        return indicatorGroupSets;
    }

    public void setIndicatorGroupSets( List<IndicatorGroupSet> indicatorGroupSets )
    {
        this.indicatorGroupSets = indicatorGroupSets;
    }

    @XmlElementWrapper( name = "organisationUnits" )
    @XmlElement( name = "organisationUnit" )
    @JsonProperty( value = "organisationUnits" )
    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( List<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    @XmlElementWrapper( name = "organisationUnitGroups" )
    @XmlElement( name = "organisationUnitGroup" )
    @JsonProperty( value = "organisationUnitGroups" )
    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    public void setOrganisationUnitGroups( List<OrganisationUnitGroup> organisationUnitGroups )
    {
        this.organisationUnitGroups = organisationUnitGroups;
    }

    @XmlElementWrapper( name = "organisationUnitGroupSets" )
    @XmlElement( name = "organisationUnitGroupSet" )
    @JsonProperty( value = "organisationUnitGroupSets" )
    public List<OrganisationUnitGroupSet> getOrganisationUnitGroupSets()
    {
        return organisationUnitGroupSets;
    }

    public void setOrganisationUnitGroupSets( List<OrganisationUnitGroupSet> organisationUnitGroupSets )
    {
        this.organisationUnitGroupSets = organisationUnitGroupSets;
    }

    @XmlElementWrapper( name = "organisationUnitLevels" )
    @XmlElement( name = "organisationUnitLevel" )
    @JsonProperty( value = "organisationUnitLevels" )
    public List<OrganisationUnitLevel> getOrganisationUnitLevels()
    {
        return organisationUnitLevels;
    }

    public void setOrganisationUnitLevels( List<OrganisationUnitLevel> organisationUnitLevels )
    {
        this.organisationUnitLevels = organisationUnitLevels;
    }

    @XmlElementWrapper( name = "dataSets" )
    @XmlElement( name = "dataSet" )
    @JsonProperty( value = "dataSets" )
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    @XmlElementWrapper( name = "validationRules" )
    @XmlElement( name = "validationRule" )
    @JsonProperty( value = "validationRules" )
    public List<ValidationRule> getValidationRules()
    {
        return validationRules;
    }

    public void setValidationRules( List<ValidationRule> validationRules )
    {
        this.validationRules = validationRules;
    }

    @XmlElementWrapper( name = "validationRuleGroups" )
    @XmlElement( name = "validationRuleGroup" )
    @JsonProperty( value = "validationRuleGroups" )
    public List<ValidationRuleGroup> getValidationRuleGroups()
    {
        return validationRuleGroups;
    }

    public void setValidationRuleGroups( List<ValidationRuleGroup> validationRuleGroups )
    {
        this.validationRuleGroups = validationRuleGroups;
    }
}
