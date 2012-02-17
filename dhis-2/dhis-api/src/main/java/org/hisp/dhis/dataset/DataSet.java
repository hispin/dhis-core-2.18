package org.hisp.dhis.dataset;

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
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.DataElementXmlAdapter;
import org.hisp.dhis.common.adapter.IndicatorXmlAdapter;
import org.hisp.dhis.common.adapter.OrganisationUnitXmlAdapter;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used for defining the standardized DataSets. A DataSet consists
 * of a collection of DataElements.
 *
 * @author Kristian Nordal
 */
@XmlRootElement( name = "dataSet", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataSet
    extends BaseNameableObject
{
    public static final String TYPE_DEFAULT = "default";
    public static final String TYPE_SECTION = "section";
    public static final String TYPE_CUSTOM = "custom";

    public static final int NO_EXPIRY = 0; 
    
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -2466830446144115499L;

    /**
     * The PeriodType indicating the frequency that this DataSet should be used
     */
    private PeriodType periodType;

    /**
     * All DataElements associated with this DataSet.
     */
    private Set<DataElement> dataElements = new HashSet<DataElement>();

    /**
     * Indicators associated with this data set. Indicators are used for view and
     * output purposes, such as calculated fields in forms and reports.
     */
    private Set<Indicator> indicators = new HashSet<Indicator>();

    /**
     * The DataElementOperands for which data must be entered in order for the
     * DataSet to be considered as complete.
     */
    private Set<DataElementOperand> compulsoryDataElementOperands = new HashSet<DataElementOperand>();

    /**
     * All Sources that register data with this DataSet.
     */
    private Set<OrganisationUnit> sources = new HashSet<OrganisationUnit>();

    /**
     * The Sections associated with the DataSet.
     */
    private Set<Section> sections = new HashSet<Section>();

    /**
     * Indicating position in the custom sort order.
     */
    private Integer sortOrder;

    /**
     * Property indicating if the dataset could be collected using mobile data entry.
     */
    private boolean mobile;

    /**
     * Indicating custom data entry form.
     */
    private DataEntryForm dataEntryForm;

    /**
     * Indicating version number.
     */
    private Integer version;

    /**
     * How many days after period is over will this dataSet auto-lock
     */
    private int expiryDays;

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public DataSet()
    {
    }

    public DataSet( String name )
    {
        this.name = name;
    }

    public DataSet( String name, PeriodType periodType )
    {
        this.name = name;
        this.periodType = periodType;
    }

    public DataSet( String name, String shortName, PeriodType periodType )
    {
        this.name = name;
        this.shortName = shortName;
        this.periodType = periodType;
    }

    public DataSet( String name, String shortName, String code, PeriodType periodType )
    {
        this.name = name;
        this.shortName = shortName;
        this.code = code;
        this.periodType = periodType;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addOrganisationUnit( OrganisationUnit unit )
    {
        sources.add( unit );
        unit.getDataSets().add( this );
    }

    public void removeOrganisationUnit( OrganisationUnit unit )
    {
        sources.remove( unit );
        unit.getDataSets().remove( this );
    }

    public void updateOrganisationUnits( Set<OrganisationUnit> updates )
    {
        for ( OrganisationUnit unit : new HashSet<OrganisationUnit>( sources ) )
        {
            if ( !updates.contains( unit ) )
            {
                removeOrganisationUnit( unit );
            }
        }

        for ( OrganisationUnit unit : updates )
        {
            addOrganisationUnit( unit );
        }
    }

    public void addDataElement( DataElement dataElement )
    {
        dataElements.add( dataElement );
        dataElement.getDataSets().add( this );
    }

    public void removeDataElement( DataElement dataElement )
    {
        dataElements.remove( dataElement );
        dataElement.getDataSets().remove( dataElement );
    }

    public void updateDataElements( Set<DataElement> updates )
    {
        for ( DataElement dataElement : new HashSet<DataElement>( dataElements ) )
        {
            if ( !updates.contains( dataElement ) )
            {
                removeDataElement( dataElement );
            }
        }

        for ( DataElement dataElement : updates )
        {
            addDataElement( dataElement );
        }
    }

    public boolean hasDataEntryForm()
    {
        return dataEntryForm != null;
    }

    public boolean hasSections()
    {
        return sections != null && sections.size() > 0;
    }

    public String getDataSetType()
    {
        if ( hasDataEntryForm() )
        {
            return TYPE_CUSTOM;
        }

        if ( hasSections() )
        {
            return TYPE_SECTION;
        }

        return TYPE_DEFAULT;
    }

    public Set<DataElement> getDataElementsInSections()
    {
        Set<DataElement> dataElements = new HashSet<DataElement>();

        for ( Section section : sections )
        {
            dataElements.addAll( section.getDataElements() );
        }

        return dataElements;
    }

    public DataSet increaseVersion()
    {
        version = version != null ? version + 1 : 1;
        return this;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof DataSet) )
        {
            return false;
        }

        final DataSet other = (DataSet) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }

    public void setDataEntryForm( DataEntryForm dataEntryForm )
    {
        this.dataEntryForm = dataEntryForm;
    }

    @XmlElementWrapper( name = "dataElements" )
    @XmlElement( name = "dataElement" )
    @XmlJavaTypeAdapter( DataElementXmlAdapter.class )
    @JsonProperty( value = "dataElements" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public Set<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( Set<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    @XmlElementWrapper( name = "indicators" )
    @XmlElement( name = "indicator" )
    @XmlJavaTypeAdapter( IndicatorXmlAdapter.class )
    @JsonProperty( value = "indicators" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public Set<Indicator> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( Set<Indicator> indicators )
    {
        this.indicators = indicators;
    }

    @XmlElement
    @JsonProperty
    public Set<DataElementOperand> getCompulsoryDataElementOperands()
    {
        return compulsoryDataElementOperands;
    }

    public void setCompulsoryDataElementOperands( Set<DataElementOperand> compulsoryDataElementOperands )
    {
        this.compulsoryDataElementOperands = compulsoryDataElementOperands;
    }

    @XmlElementWrapper( name = "organisationUnits" )
    @XmlElement( name = "organisationUnit" )
    @XmlJavaTypeAdapter( OrganisationUnitXmlAdapter.class )
    @JsonProperty( value = "organisationUnits" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public Set<OrganisationUnit> getSources()
    {
        return sources;
    }

    public void setSources( Set<OrganisationUnit> sources )
    {
        this.sources = sources;
    }

    @XmlElement
    @JsonProperty
    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    @XmlElementWrapper( name = "sections" )
    @XmlElement( name = "section" )
    @JsonProperty
    public Set<Section> getSections()
    {
        return sections;
    }

    public void setSections( Set<Section> sections )
    {
        this.sections = sections;
    }

    @XmlElement
    @JsonProperty
    public boolean isMobile()
    {
        return mobile;
    }

    public void setMobile( boolean mobile )
    {
        this.mobile = mobile;
    }

    @XmlElement
    @JsonProperty
    public Integer getVersion()
    {
        return version;
    }

    public void setVersion( Integer version )
    {
        this.version = version;
    }

    @XmlElement
    @JsonProperty
    public int getExpiryDays()
    {
        return expiryDays;
    }

    public void setExpiryDays( int expiryDays )
    {
        this.expiryDays = expiryDays;
    }
}
