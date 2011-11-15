package org.hisp.dhis.indicator;

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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.dataset.DataSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Lars Helge Overland
 * @version $Id: Indicator.java 5540 2008-08-19 10:47:07Z larshelg $
 */
@XmlRootElement( name = "indicator" )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Indicator extends AbstractNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;

    private Boolean annualized;

    private IndicatorType indicatorType;

    private String numerator;

    private String numeratorDescription;

    private String explodedNumerator;

    private String denominator;

    private String denominatorDescription;

    private String explodedDenominator;

    private Integer sortOrder;

    private String url;

    private Date lastUpdated;

    private Set<IndicatorGroup> groups = new HashSet<IndicatorGroup>();

    private Set<DataSet> dataSets = new HashSet<DataSet>();

    /**
     * Set of the dynamic attributes values that belong to this indicator.
     */
    private Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addIndicatorGroup( IndicatorGroup group )
    {
        groups.add( group );
        group.getMembers().add( this );
    }

    public void removeIndicatorGroup( IndicatorGroup group )
    {
        groups.remove( group );
        group.getMembers().remove( this );
    }

    public void updateIndicatorGroups( Set<IndicatorGroup> updates )
    {
        for ( IndicatorGroup group : new HashSet<IndicatorGroup>( groups ) )
        {
            if ( !updates.contains( group ) )
            {
                removeIndicatorGroup( group );
            }
        }

        for ( IndicatorGroup group : updates )
        {
            addIndicatorGroup( group );
        }
    }

    public boolean isAnnualized()
    {
        return annualized != null && annualized;
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

        if ( !(o instanceof Indicator) )
        {
            return false;
        }

        final Indicator other = (Indicator) o;

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

    public Boolean getAnnualized()
    {
        return annualized;
    }

    public void setAnnualized( Boolean annualized )
    {
        this.annualized = annualized;
    }

    public IndicatorType getIndicatorType()
    {
        return indicatorType;
    }

    public void setIndicatorType( IndicatorType indicatorType )
    {
        this.indicatorType = indicatorType;
    }

    public String getNumerator()
    {
        return numerator;
    }

    public void setNumerator( String numerator )
    {
        this.numerator = numerator;
    }

    public String getNumeratorDescription()
    {
        return numeratorDescription;
    }

    public void setNumeratorDescription( String numeratorDescription )
    {
        this.numeratorDescription = numeratorDescription;
    }

    public String getExplodedNumerator()
    {
        return explodedNumerator;
    }

    public void setExplodedNumerator( String explodedNumerator )
    {
        this.explodedNumerator = explodedNumerator;
    }

    public String getDenominator()
    {
        return denominator;
    }

    public void setDenominator( String denominator )
    {
        this.denominator = denominator;
    }

    public String getDenominatorDescription()
    {
        return denominatorDescription;
    }

    public void setDenominatorDescription( String denominatorDescription )
    {
        this.denominatorDescription = denominatorDescription;
    }

    public String getExplodedDenominator()
    {
        return explodedDenominator;
    }

    public void setExplodedDenominator( String explodedDenominator )
    {
        this.explodedDenominator = explodedDenominator;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( Date lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public Set<IndicatorGroup> getGroups()
    {
        return groups;
    }

    public void setGroups( Set<IndicatorGroup> groups )
    {
        this.groups = groups;
    }

    public Set<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( Set<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    public Set<AttributeValue> getAttributeValues()
    {
        return attributeValues;
    }

    public void setAttributeValues( Set<AttributeValue> attributeValues )
    {
        this.attributeValues = attributeValues;
    }
}
