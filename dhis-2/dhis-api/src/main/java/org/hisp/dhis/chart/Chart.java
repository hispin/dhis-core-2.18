package org.hisp.dhis.chart;

/*
 * Copyright (c) 2004-2010, University of Oslo
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
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.adapter.DataElementXmlAdapter;
import org.hisp.dhis.common.adapter.DataSetXmlAdapter;
import org.hisp.dhis.common.adapter.IndicatorXmlAdapter;
import org.hisp.dhis.common.adapter.OrganisationUnitXmlAdapter;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.RelativePeriods;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lars Helge Overland
 */
@XmlRootElement( name = "chart", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Chart
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = 2570074075484545534L;

    public static final String DIMENSION_PERIOD_INDICATOR = "period";
    public static final String DIMENSION_ORGANISATIONUNIT_INDICATOR = "organisationUnit";
    public static final String DIMENSION_INDICATOR_PERIOD = "indicator";
    public static final String DIMENSION_PERIOD_DATAELEMENT = "period_dataElement";
    public static final String DIMENSION_ORGANISATIONUNIT_DATAELEMENT = "organisationUnit_dataElement";
    public static final String DIMENSION_DATAELEMENT_PERIOD = "dataElement_period";

    public static final String SIZE_NORMAL = "normal";
    public static final String SIZE_WIDE = "wide";
    public static final String SIZE_TALL = "tall";

    public static final String TYPE_COLUMN = "COLUMN";
    public static final String TYPE_STACKED_COLUMN = "STACKEDCOLUMN";
    public static final String TYPE_BAR = "BAR";
    public static final String TYPE_STACKED_BAR = "STACKEDBAR";
    public static final String TYPE_LINE = "LINE";
    public static final String TYPE_AREA = "AREA";
    public static final String TYPE_PIE = "PIE";

    public static final String DIMENSION_DATA = "DATA";
    public static final String DIMENSION_PERIOD = "PERIOD";
    public static final String DIMENSION_ORGANISATIONUNIT = "ORGANISATIONUNIT";

    private String domainAxixLabel;

    private String rangeAxisLabel;

    private String type;

    private String series;

    private String category;

    private String filter;

    private boolean hideLegend;

    private boolean regression;

    private boolean targetLine;

    private boolean hideSubtitle;

    private Double targetLineValue;

    private String targetLineLabel;

    private Set<ChartGroup> groups = new HashSet<ChartGroup>();

    private List<Indicator> indicators = new ArrayList<Indicator>();

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    private List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();

    private RelativePeriods relatives;

    private boolean userOrganisationUnit;

    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------

    private transient I18nFormat format;

    private transient List<Period> relativePeriods = new ArrayList<Period>();

    private transient OrganisationUnit organisationUnit;

    private transient List<OrganisationUnit> allOrganisationUnits = new ArrayList<OrganisationUnit>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Chart()
    {
    }

    public Chart( String name )
    {
        this.name = name;
    }

    public void init()
    {
        allOrganisationUnits.addAll( organisationUnits );

        if ( organisationUnit != null )
        {
            allOrganisationUnits.add( organisationUnit );
        }
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addChartGroup( ChartGroup group )
    {
        groups.add( group );
        group.getMembers().add( this );
    }

    public void removeChartGroup( ChartGroup group )
    {
        groups.remove( group );
        group.getMembers().remove( this );
    }

    public void updateChartGroups( Set<ChartGroup> updates )
    {
        for ( ChartGroup group : new HashSet<ChartGroup>( groups ) )
        {
            if ( !updates.contains( group ) )
            {
                removeChartGroup( group );
            }
        }

        for ( ChartGroup group : updates )
        {
            addChartGroup( group );
        }
    }
    
    public List<NameableObject> series()
    {
        return dimensionToList( series );
    }
    
    public List<NameableObject> category()
    {
        return dimensionToList( category );
    }
    
    public NameableObject filter()
    {
        List<NameableObject> list = dimensionToList( filter );
        
        return list != null && !list.isEmpty() ? list.iterator().next() : null;
    }

    public String getTitle()
    {
        if ( DIMENSION_PERIOD.equals( filter ) )
        {
            return format.formatPeriod( getRelativePeriods().get( 0 ) );
        }
        
        return filter().getName();
    }
    
    private List<NameableObject> dimensionToList( String dimension )
    {
        List<NameableObject> list = new ArrayList<NameableObject>();
        
        if ( DIMENSION_DATA.equals( dimension ) )
        {
            list.addAll( getDataElements() );
            list.addAll( getIndicators() );
        }
        else if ( DIMENSION_PERIOD.equals( dimension ) )
        {
            namePeriods( getRelativePeriods(), format );
            
            list.addAll( getRelativePeriods() );
        }
        else if ( DIMENSION_ORGANISATIONUNIT.equals( dimension ) )
        {
            list.addAll( getAllOrganisationUnits() );
        }
        
        return list;
    }
    
    private void namePeriods( List<Period> periods, I18nFormat format )
    {
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
            period.setShortName( format.formatPeriod( period ) );
        }
    }
    
    /**
     * TODO This method is a temporary hack while we phase out the old chart UI.
     */
    public String getDimension()
    {
        if ( DIMENSION_DATA.equals( series ) && DIMENSION_PERIOD.equals( category ) && !indicators.isEmpty() )
        {
            return DIMENSION_PERIOD_INDICATOR;
        }
        else if ( DIMENSION_DATA.equals( series ) && DIMENSION_ORGANISATIONUNIT.equals( category ) && !indicators.isEmpty() )
        {
            return DIMENSION_ORGANISATIONUNIT_INDICATOR;
        }
        else if ( DIMENSION_PERIOD.equals( series ) && DIMENSION_DATA.equals( category ) && !indicators.isEmpty() )
        {
            return DIMENSION_INDICATOR_PERIOD;
        }
        else if ( DIMENSION_DATA.equals( series ) && DIMENSION_PERIOD.equals( category ) )
        {
            return DIMENSION_PERIOD_DATAELEMENT;
        }
        else if ( DIMENSION_DATA.equals( series ) && DIMENSION_ORGANISATIONUNIT.equals( category ) )
        {
            return DIMENSION_ORGANISATIONUNIT_DATAELEMENT;
        }
        else if ( DIMENSION_PERIOD.equals( series ) && DIMENSION_DATA.equals( category ) )
        {
            return DIMENSION_DATAELEMENT_PERIOD;
        }
        
        return null;
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals, toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        Chart other = (Chart) object;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Sets all dimensions for this chart.
     * 
     * @param series the series dimension.
     * @param category the category dimension.
     * @param filter the filter dimension.
     */
    public void setDimensions( String series, String category, String filter )
    {
        this.series = series;
        this.category = category;
        this.filter = filter;
    }
    
    public boolean hasIndicators()
    {
        return indicators != null && indicators.size() > 0;
    }
    
    public boolean hasDataElements()
    {
        return dataElements != null && dataElements.size() > 0;
    }
    
    public boolean isType( String type )
    {
        return this.type != null && this.type.equals( type );
    }

    public int getWidth()
    {
        return 700;
    }

    public int getHeight()
    {
        return 500;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @XmlElement( name = "domainAxisLabel" )
    @JsonProperty( value = "domainAxisLabel" )
    public String getDomainAxixLabel()
    {
        return domainAxixLabel;
    }

    public void setDomainAxixLabel( String domainAxixLabel )
    {
        this.domainAxixLabel = domainAxixLabel;
    }

    @XmlElement
    @JsonProperty
    public String getRangeAxisLabel()
    {
        return rangeAxisLabel;
    }

    public void setRangeAxisLabel( String rangeAxisLabel )
    {
        this.rangeAxisLabel = rangeAxisLabel;
    }

    @XmlElement
    @JsonProperty
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    @XmlElement
    @JsonProperty
    public String getSeries()
    {
        return series;
    }

    public void setSeries( String series )
    {
        this.series = series;
    }

    @XmlElement
    @JsonProperty
    public String getCategory()
    {
        return category;
    }

    public void setCategory( String category )
    {
        this.category = category;
    }

    @XmlElement
    @JsonProperty
    public String getFilter()
    {
        return filter;
    }

    public void setFilter( String filter )
    {
        this.filter = filter;
    }

    @XmlElement
    @JsonProperty
    public boolean isHideLegend()
    {
        return hideLegend;
    }

    public void setHideLegend( boolean hideLegend )
    {
        this.hideLegend = hideLegend;
    }

    @XmlElement
    @JsonProperty
    public boolean isRegression()
    {
        return regression;
    }

    public void setRegression( boolean regression )
    {
        this.regression = regression;
    }

    @XmlElement
    @JsonProperty
    public boolean isTargetLine()
    {
        return targetLine;
    }

    public void setTargetLine( boolean targetLine )
    {
        this.targetLine = targetLine;
    }

    @XmlElement
    @JsonProperty
    public Double getTargetLineValue()
    {
        return targetLineValue;
    }

    public void setTargetLineValue( Double targetLineValue )
    {
        this.targetLineValue = targetLineValue;
    }

    @XmlElement
    @JsonProperty
    public void setTargetLineLabel( String targetLineLabel )
    {
        this.targetLineLabel = targetLineLabel;
    }

    public String getTargetLineLabel()
    {
        return targetLineLabel;
    }

    @XmlElement
    @JsonProperty
    public boolean isHideSubtitle()
    {
        return hideSubtitle;
    }

    public void setHideSubtitle( Boolean hideSubtitle )
    {
        this.hideSubtitle = hideSubtitle;
    }

    @XmlElementWrapper( name = "indicators" )
    @XmlElement( name = "indicator" )
    @XmlJavaTypeAdapter( IndicatorXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( contentAs = BaseNameableObject.class )
    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( List<Indicator> indicators )
    {
        this.indicators = indicators;
    }

    @XmlElementWrapper( name = "dataElements" )
    @XmlElement( name = "dataElement" )
    @XmlJavaTypeAdapter( DataElementXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( contentAs = BaseNameableObject.class )
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    @XmlElementWrapper( name = "dataSets" )
    @XmlElement( name = "dataSet" )
    @XmlJavaTypeAdapter( DataSetXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( contentAs = BaseNameableObject.class )
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    @XmlElementWrapper( name = "organisationUnits" )
    @XmlElement( name = "organisationUnit" )
    @XmlJavaTypeAdapter( OrganisationUnitXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( contentAs = BaseNameableObject.class )
    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( List<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    public I18nFormat getFormat()
    {
        return format;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    @XmlElement( name = "relativePeriods" )
    @JsonProperty( value = "relativePeriods" )
    public RelativePeriods getRelatives()
    {
        return relatives;
    }

    public void setRelatives( RelativePeriods relatives )
    {
        this.relatives = relatives;
    }

    @XmlElement
    @JsonProperty
    public boolean isUserOrganisationUnit()
    {
        return userOrganisationUnit;
    }

    public void setUserOrganisationUnit( boolean userOrganisationUnit )
    {
        this.userOrganisationUnit = userOrganisationUnit;
    }

    public List<Period> getRelativePeriods()
    {
        return relativePeriods;
    }

    public void setRelativePeriods( List<Period> relativePeriods )
    {
        this.relativePeriods = relativePeriods;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public List<OrganisationUnit> getAllOrganisationUnits()
    {
        return allOrganisationUnits;
    }

    public void setAllOrganisationUnits( List<OrganisationUnit> allOrganisationUnits )
    {
        this.allOrganisationUnits = allOrganisationUnits;
    }

    public Set<ChartGroup> getGroups()
    {
        return groups;
    }

    public void setGroups( Set<ChartGroup> groups )
    {
        this.groups = groups;
    }
}
