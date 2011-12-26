package org.hisp.dhis.mapping;

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
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.*;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.User;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
@XmlRootElement( name = "map", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( XmlAccessType.NONE )
public class MapView
    extends BaseIdentifiableObject
{
    private static final long serialVersionUID = 1866358818802275436L;

    private User user;

    private String mapValueType;

    private IndicatorGroup indicatorGroup;

    private Indicator indicator;

    private DataElementGroup dataElementGroup;

    private DataElement dataElement;

    private String mapDateType;

    private PeriodType periodType;

    private Period period;

    private OrganisationUnit parentOrganisationUnit;

    private OrganisationUnitLevel organisationUnitLevel;

    private String mapLegendType;

    private Integer method;

    private Integer classes;

    private String bounds;

    private String colorLow;

    private String colorHigh;

    private MapLegendSet mapLegendSet;

    private Integer radiusLow;

    private Integer radiusHigh;

    private String longitude;

    private String latitude;

    private Integer zoom;

    public MapView()
    {
    }

    public MapView( String name, User user, String mapValueType, IndicatorGroup indicatorGroup, Indicator indicator,
                    DataElementGroup dataElementGroup, DataElement dataElement, String mapDateType, PeriodType periodType,
                    Period period, OrganisationUnit parentOrganisationUnit, OrganisationUnitLevel organisationUnitLevel,
                    String mapLegendType, Integer method, Integer classes, String bounds, String colorLow, String colorHigh,
                    MapLegendSet mapLegendSet, Integer radiusLow, Integer radiusHigh, String longitude, String latitude, int zoom )
    {
        this.name = name;
        this.user = user;
        this.mapValueType = mapValueType;
        this.indicatorGroup = indicatorGroup;
        this.indicator = indicator;
        this.dataElementGroup = dataElementGroup;
        this.dataElement = dataElement;
        this.mapDateType = mapDateType;
        this.periodType = periodType;
        this.period = period;
        this.parentOrganisationUnit = parentOrganisationUnit;
        this.organisationUnitLevel = organisationUnitLevel;
        this.mapLegendType = mapLegendType;
        this.method = method;
        this.classes = classes;
        this.bounds = bounds;
        this.colorLow = colorLow;
        this.colorHigh = colorHigh;
        this.mapLegendSet = mapLegendSet;
        this.radiusLow = radiusLow;
        this.radiusHigh = radiusHigh;
        this.longitude = longitude;
        this.latitude = latitude;
        this.zoom = zoom;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
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

        final MapView other = (MapView) object;

        return name.equals( other.name );
    }
    
    @Override
    public String toString()
    {
        return "[Name: " + name + ", indicator: " + indicator + ", org unit: " + 
            parentOrganisationUnit + ", period: " + period + ", value type: " + mapValueType + "]";
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public String getMapDateTypeNullSafe()
    {
        return mapDateType != null ? mapDateType : MappingService.MAP_DATE_TYPE_FIXED;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @XmlElement
    @JsonProperty
    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }

    @XmlElement
    @JsonProperty
    public String getMapValueType()
    {
        return mapValueType;
    }

    public void setMapValueType( String mapValueType )
    {
        this.mapValueType = mapValueType;
    }

    @XmlElement
    @XmlJavaTypeAdapter( IndicatorGroupXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    public IndicatorGroup getIndicatorGroup()
    {
        return indicatorGroup;
    }

    public void setIndicatorGroup( IndicatorGroup indicatorGroup )
    {
        this.indicatorGroup = indicatorGroup;
    }

    @XmlElement
    @XmlJavaTypeAdapter( IndicatorXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    public Indicator getIndicator()
    {
        return indicator;
    }

    public void setIndicator( Indicator indicator )
    {
        this.indicator = indicator;
    }

    @XmlElement
    @XmlJavaTypeAdapter( DataElementGroupXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    public DataElementGroup getDataElementGroup()
    {
        return dataElementGroup;
    }

    public void setDataElementGroup( DataElementGroup dataElementGroup )
    {
        this.dataElementGroup = dataElementGroup;
    }

    @XmlElement
    @XmlJavaTypeAdapter( DataElementXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    @XmlElement
    @JsonProperty
    public String getMapDateType()
    {
        return mapDateType;
    }

    public void setMapDateType( String mapDateType )
    {
        this.mapDateType = mapDateType;
    }

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    @XmlElement
    @XmlJavaTypeAdapter( BaseIdentifiableObjectXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    @XmlElement
    @XmlJavaTypeAdapter( OrganisationUnitXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    public OrganisationUnit getParentOrganisationUnit()
    {
        return parentOrganisationUnit;
    }

    public void setParentOrganisationUnit( OrganisationUnit parentOrganisationUnit )
    {
        this.parentOrganisationUnit = parentOrganisationUnit;
    }

    @XmlElement
    @XmlJavaTypeAdapter( OrganisationUnitLevelXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    public OrganisationUnitLevel getOrganisationUnitLevel()
    {
        return organisationUnitLevel;
    }

    public void setOrganisationUnitLevel( OrganisationUnitLevel organisationUnitLevel )
    {
        this.organisationUnitLevel = organisationUnitLevel;
    }

    @XmlElement
    @JsonProperty
    public String getMapLegendType()
    {
        return mapLegendType;
    }

    public void setMapLegendType( String mapLegendType )
    {
        this.mapLegendType = mapLegendType;
    }

    @XmlElement
    @JsonProperty
    public Integer getMethod()
    {
        return method;
    }

    public void setMethod( Integer method )
    {
        this.method = method;
    }

    @XmlElement
    @JsonProperty
    public Integer getClasses()
    {
        return classes;
    }

    public void setClasses( Integer classes )
    {
        this.classes = classes;
    }

    @XmlElement
    @JsonProperty
    public String getBounds()
    {
        return bounds;
    }

    public void setBounds( String bounds )
    {
        this.bounds = bounds;
    }

    @XmlElement
    @JsonProperty
    public String getColorLow()
    {
        return colorLow;
    }

    public void setColorLow( String colorLow )
    {
        this.colorLow = colorLow;
    }

    @XmlElement
    @JsonProperty
    public String getColorHigh()
    {
        return colorHigh;
    }

    public void setColorHigh( String colorHigh )
    {
        this.colorHigh = colorHigh;
    }

    @XmlElement
    @JsonProperty
    public MapLegendSet getMapLegendSet()
    {
        return mapLegendSet;
    }

    public void setMapLegendSet( MapLegendSet mapLegendSet )
    {
        this.mapLegendSet = mapLegendSet;
    }

    @XmlElement
    @JsonProperty
    public Integer getRadiusLow()
    {
        return radiusLow;
    }

    public void setRadiusLow( Integer radiusLow )
    {
        this.radiusLow = radiusLow;
    }

    @XmlElement
    @JsonProperty
    public Integer getRadiusHigh()
    {
        return radiusHigh;
    }

    public void setRadiusHigh( Integer radiusHigh )
    {
        this.radiusHigh = radiusHigh;
    }

    @XmlElement
    @JsonProperty
    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude( String longitude )
    {
        this.longitude = longitude;
    }

    @XmlElement
    @JsonProperty
    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude( String latitude )
    {
        this.latitude = latitude;
    }

    @XmlElement
    @JsonProperty
    public Integer getZoom()
    {
        return zoom;
    }

    public void setZoom( Integer zoom )
    {
        this.zoom = zoom;
    }
}
