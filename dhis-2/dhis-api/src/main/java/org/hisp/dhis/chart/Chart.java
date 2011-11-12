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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.AbstractIdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.RelativePeriods;

/**
 * @author Lars Helge Overland
 */
public class Chart
    extends AbstractIdentifiableObject
{
    private static final long serialVersionUID = 2570074075484545534L;

    public static final String DIMENSION_PERIOD_INDICATOR = "period";

    public static final String DIMENSION_ORGANISATIONUNIT_INDICATOR = "organisationUnit";

    public static final String DIMENSION_INDICATOR_PERIOD = "indicator";

    public static final String DIMENSION_PERIOD_DATAELEMENT = "period_dataElement";

    public static final String DIMENSION_ORGANISATIONUNIT_DATAELEMENT = "organisationUnit_dataElement";

    public static final String DIMENSION_DATAELEMENT_PERIOD = "dataElement_period";

    public static final String DIMENSION_PERIOD_COMPLETENESS = "period_completeness";

    public static final String DIMENSION_ORGANISATIONUNIT_COMPLETENESS = "organisationUnit_completeness";

    public static final String DIMENSION_COMPLETENESS_PERIOD = "completeness_period";

    public static final String TYPE_BAR = "bar";

    public static final String TYPE_BAR3D = "bar3d";

    public static final String TYPE_STACKED_BAR = "stackedBar";

    public static final String TYPE_STACKED_BAR3D = "stackedBar3d";

    public static final String TYPE_LINE = "line";

    public static final String TYPE_LINE3D = "line3d";

    public static final String TYPE_PIE = "pie";

    public static final String TYPE_PIE3D = "pie3d";

    public static final String SIZE_NORMAL = "normal";

    public static final String SIZE_WIDE = "wide";

    public static final String SIZE_TALL = "tall";

    private int id;

    private String domainAxixLabel;

    private String rangeAxisLabel;

    private String type;

    private String size;

    private String dimension;

    private Boolean hideLegend;

    private Boolean verticalLabels;

    private Boolean horizontalPlotOrientation;

    private Boolean regression;

    private Boolean targetLine;

    private Boolean hideSubtitle;

    private Double targetLineValue;

    private String targetLineLabel;

    private Set<ChartGroup> groups = new HashSet<ChartGroup>();

    private List<Indicator> indicators = new ArrayList<Indicator>();

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    private List<Period> periods = new ArrayList<Period>();

    private List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();

    private RelativePeriods relatives;

    private Boolean userOrganisationUnit;

    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------

    private transient I18nFormat format;

    private List<Period> relativePeriods = new ArrayList<Period>();

    private List<Period> allPeriods = new ArrayList<Period>();

    private OrganisationUnit organisationUnit;

    private List<OrganisationUnit> allOrganisationUnits = new ArrayList<OrganisationUnit>();

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
        allPeriods.addAll( periods );
        allPeriods.addAll( relativePeriods );
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

    public boolean isType( String type )
    {
        return this.type != null && this.type.equals( type );
    }

    public boolean isSize( String size )
    {
        return this.size != null && this.size.equals( size );
    }

    public boolean isDimension( String dimension )
    {
        return this.dimension != null && this.dimension.equals( dimension );
    }

    public boolean isHideLegend()
    {
        return hideLegend != null && hideLegend;
    }

    public boolean isVerticalLabels()
    {
        return verticalLabels != null && verticalLabels;
    }

    public boolean isHorizontalPlotOrientation()
    {
        return horizontalPlotOrientation != null && horizontalPlotOrientation;
    }

    public boolean isRegression()
    {
        return regression != null && regression;
    }

    public boolean isTargetLine()
    {
        return targetLine != null && targetLine;
    }

    public int getWidth()
    {
        return isSize( SIZE_WIDE ) ? 1000 : 700;
    }

    public int getHeight()
    {
        return isSize( SIZE_TALL ) ? 800 : 500;
    }

    public boolean isUserOrganisationUnit()
    {
        return userOrganisationUnit != null && userOrganisationUnit;
    }

    // -------------------------------------------------------------------------
    // Importable object
    // -------------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getDomainAxixLabel()
    {
        return domainAxixLabel;
    }

    public void setDomainAxixLabel( String domainAxixLabel )
    {
        this.domainAxixLabel = domainAxixLabel;
    }

    public String getRangeAxisLabel()
    {
        return rangeAxisLabel;
    }

    public void setRangeAxisLabel( String rangeAxisLabel )
    {
        this.rangeAxisLabel = rangeAxisLabel;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getSize()
    {
        return size;
    }

    public void setSize( String size )
    {
        this.size = size;
    }

    public String getDimension()
    {
        return dimension;
    }

    public void setDimension( String dimension )
    {
        this.dimension = dimension;
    }

    public Boolean getHideLegend()
    {
        return hideLegend;
    }

    public void setHideLegend( Boolean hideLegend )
    {
        this.hideLegend = hideLegend;
    }

    public Boolean getVerticalLabels()
    {
        return verticalLabels;
    }

    public void setVerticalLabels( Boolean verticalLabels )
    {
        this.verticalLabels = verticalLabels;
    }

    public Boolean getHorizontalPlotOrientation()
    {
        return horizontalPlotOrientation;
    }

    public void setHorizontalPlotOrientation( Boolean horizontalPlotOrientation )
    {
        this.horizontalPlotOrientation = horizontalPlotOrientation;
    }

    public Boolean getRegression()
    {
        return regression;
    }

    public void setRegression( Boolean regression )
    {
        this.regression = regression;
    }

    public void setTargetLine( Boolean targetLine )
    {
        this.targetLine = targetLine;
    }

    public Boolean getTargetLine()
    {
        return targetLine;
    }

    public void setTargetLineValue( Double targetLineValue )
    {
        this.targetLineValue = targetLineValue;
    }

    public Double getTargetLineValue()
    {
        return targetLineValue;
    }

    public void setTargetLineLabel( String targetLineLabel )
    {
        this.targetLineLabel = targetLineLabel;
    }

    public String getTargetLineLabel()
    {
        if ( targetLineLabel == null || targetLineLabel.trim().length() == 0 )
        {
            targetLineLabel = "Target Line";
        }

        return targetLineLabel;
    }

    public void setHideSubtitle( Boolean hideSubtitle )
    {
        this.hideSubtitle = hideSubtitle;
    }

    public Boolean getHideSubtitle()
    {
        if ( hideSubtitle == null )
        {
            return Boolean.FALSE;
        }

        return hideSubtitle;
    }

    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( List<Indicator> indicators )
    {
        this.indicators = indicators;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    public List<Period> getPeriods()
    {
        return periods;
    }

    public void setPeriods( List<Period> periods )
    {
        this.periods = periods;
    }

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

    public RelativePeriods getRelatives()
    {
        return relatives;
    }

    public void setRelatives( RelativePeriods relatives )
    {
        this.relatives = relatives;
    }

    public Boolean getUserOrganisationUnit()
    {
        return userOrganisationUnit;
    }

    public void setUserOrganisationUnit( Boolean userOrganisationUnit )
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

    public List<Period> getAllPeriods()
    {
        return allPeriods;
    }

    public void setAllPeriods( List<Period> allPeriods )
    {
        this.allPeriods = allPeriods;
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
