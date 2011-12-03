package org.hisp.dhis.organisationunit;

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

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.*;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.user.User;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kristian Nordal
 */
@XmlRootElement( name = "organisationUnit", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class OrganisationUnit extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1228298379303894619L;

    public static final String FEATURETYPE_NONE = "None";

    public static final String FEATURETYPE_MULTIPOLYGON = "MultiPolygon";

    public static final String FEATURETYPE_POLYGON = "Polygon";

    public static final String FEATURETYPE_POINT = "Point";

    public static final String RESULTTYPE_SYMBOL = "Symbol";

    private static final Comparator<OrganisationUnit> COMPARATOR = new OrganisationUnitNameComparator();

    private static final Pattern JSON_COORDINATE_PATTERN = Pattern.compile( "(\\[{3}.*?\\]{3})" );

    private static final Pattern COORDINATE_PATTERN = Pattern.compile( "([\\-0-9.]+,[\\-0-9.]+)" );

    private static final String NAME_SEPARATOR = " - ";

    private Set<OrganisationUnit> children = new HashSet<OrganisationUnit>();

    private OrganisationUnit parent;

    private Date openingDate;

    private Date closedDate;

    private boolean active;

    private String comment;

    private String geoCode;

    private String featureType;

    private String coordinates;

    private String url;

    private Set<OrganisationUnitGroup> groups = new HashSet<OrganisationUnitGroup>();

    private Set<DataSet> dataSets = new HashSet<DataSet>();

    private Set<User> users = new HashSet<User>();

    private String contactPerson;

    private String address;

    private String email;

    private String phoneNumber;

    private boolean hasPatients;

    private transient int level;

    private transient boolean currentParent;

    private transient String type;

    private transient String[] groupNames;

    /**
     * Set of the dynamic attributes values that belong to this
     * organisationUnit.
     */
    private Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public OrganisationUnit()
    {
    }

    public OrganisationUnit( String name )
    {
        this.name = name;
    }

    /**
     * @param name
     * @param shortName
     * @param openingDate
     * @param closedDate
     * @param active
     * @param comment
     */
    public OrganisationUnit( String name, String shortName, String code, Date openingDate, Date closedDate,
                             boolean active, String comment )
    {
        this.name = name;
        this.shortName = shortName;
        this.code = code;
        this.openingDate = openingDate;
        this.closedDate = closedDate;
        this.active = active;
        this.comment = comment;
    }

    /**
     * @param name
     * @param parent
     * @param shortName
     * @param openingDate
     * @param closedDate
     * @param active
     * @param comment
     */
    public OrganisationUnit( String name, OrganisationUnit parent, String shortName, String code, Date openingDate,
                             Date closedDate, boolean active, String comment )
    {
        this.name = name;
        this.parent = parent;
        this.shortName = shortName;
        this.code = code;
        this.openingDate = openingDate;
        this.closedDate = closedDate;
        this.active = active;
        this.comment = comment;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addDataSet( DataSet dataSet )
    {
        dataSets.add( dataSet );
        dataSet.getSources().add( this );
    }

    public void removeDataSet( DataSet dataSet )
    {
        dataSets.remove( dataSet );
        dataSet.getSources().remove( this );
    }

    public void updateDataSets( Set<DataSet> updates )
    {
        for ( DataSet dataSet : new HashSet<DataSet>( dataSets ) )
        {
            if ( !updates.contains( dataSet ) )
            {
                removeDataSet( dataSet );
            }
        }

        for ( DataSet dataSet : updates )
        {
            addDataSet( dataSet );
        }
    }

    public List<OrganisationUnit> getSortedChildren()
    {
        List<OrganisationUnit> sortedChildren = new ArrayList<OrganisationUnit>( children );

        Collections.sort( sortedChildren, COMPARATOR );

        return sortedChildren;
    }

    public Set<OrganisationUnit> getGrandChildren()
    {
        Set<OrganisationUnit> grandChildren = new HashSet<OrganisationUnit>();

        for ( OrganisationUnit child : children )
        {
            grandChildren.addAll( child.getChildren() );
        }

        return grandChildren;
    }

    public boolean hasChild()
    {
        return !this.children.isEmpty();
    }

    public boolean hasChildrenWithCoordinates()
    {
        for ( OrganisationUnit child : children )
        {
            if ( child.hasCoordinates() )
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasCoordinates()
    {
        return coordinates != null && coordinates.trim().length() > 0;
    }

    public List<CoordinatesTuple> getCoordinatesAsList()
    {
        List<CoordinatesTuple> list = new ArrayList<CoordinatesTuple>();

        if ( coordinates != null && !coordinates.trim().isEmpty() )
        {
            Matcher jsonMatcher = JSON_COORDINATE_PATTERN.matcher( coordinates );

            while ( jsonMatcher.find() )
            {
                CoordinatesTuple tuple = new CoordinatesTuple();

                Matcher matcher = COORDINATE_PATTERN.matcher( jsonMatcher.group() );

                while ( matcher.find() )
                {
                    tuple.addCoordinates( matcher.group() );
                }

                list.add( tuple );
            }
        }

        return list;
    }

    public void setMultiPolygonCoordinatesFromList( List<CoordinatesTuple> list )
    {
        StringBuilder builder = new StringBuilder();

        if ( CoordinatesTuple.hasCoordinates( list ) )
        {
            builder.append( "[" );

            for ( CoordinatesTuple tuple : list )
            {
                if ( tuple.hasCoordinates() )
                {
                    builder.append( "[[" );

                    for ( String coordinates : tuple.getCoordinatesTuple() )
                    {
                        builder.append( "[" + coordinates + "]," );
                    }

                    builder.deleteCharAt( builder.lastIndexOf( "," ) );
                    builder.append( "]]," );
                }
            }

            builder.deleteCharAt( builder.lastIndexOf( "," ) );
            builder.append( "]" );
        }

        this.coordinates = StringUtils.trimToNull( builder.toString() );
    }

    public void setPointCoordinatesFromList( List<CoordinatesTuple> list )
    {
        StringBuilder builder = new StringBuilder();

        if ( list != null && list.size() > 0 )
        {
            for ( CoordinatesTuple tuple : list )
            {
                for ( String coordinates : tuple.getCoordinatesTuple() )
                {
                    builder.append( "[" + coordinates + "]" );
                }
            }
        }

        this.coordinates = StringUtils.trimToNull( builder.toString() );
    }

    public String getChildrenFeatureType()
    {
        for ( OrganisationUnit child : children )
        {
            if ( child.getFeatureType() != null )
            {
                return child.getFeatureType();
            }
        }

        return FEATURETYPE_NONE;
    }

    public String getValidCoordinates()
    {
        return coordinates != null && !coordinates.isEmpty() ? coordinates : "[]";
    }

    public OrganisationUnitGroup getGroupInGroupSet( OrganisationUnitGroupSet groupSet )
    {
        if ( groupSet != null )
        {
            for ( OrganisationUnitGroup group : groups )
            {
                if ( groupSet.getOrganisationUnitGroups().contains( group ) )
                {
                    return group;
                }
            }
        }

        return null;
    }

    public Integer getGroupIdInGroupSet( OrganisationUnitGroupSet groupSet )
    {
        final OrganisationUnitGroup group = getGroupInGroupSet( groupSet );

        return group != null ? group.getId() : null;
    }

    public String getGroupNameInGroupSet( OrganisationUnitGroupSet groupSet )
    {
        final OrganisationUnitGroup group = getGroupInGroupSet( groupSet );

        return group != null ? group.getName() : null;
    }

    public String getAncestorNames()
    {
        StringBuilder builder = new StringBuilder( name );

        OrganisationUnit unit = parent;

        while ( unit != null )
        {
            builder.append( NAME_SEPARATOR ).append( unit.getName() );
            unit = unit.getParent();
        }

        return builder.toString();
    }

    public Set<DataElement> getDataElementsInDataSets()
    {
        Set<DataElement> dataElements = new HashSet<DataElement>();

        for ( DataSet dataSet : dataSets )
        {
            dataElements.addAll( dataSet.getDataElements() );
        }

        return dataElements;
    }

    public void updateParent( OrganisationUnit newParent )
    {
        if ( this.parent != null && this.parent.getChildren() != null )
        {
            this.parent.getChildren().remove( this );
        }

        this.parent = newParent;

        newParent.getChildren().add( this );
    }

    public Set<OrganisationUnit> getChildrenThisIfEmpty()
    {
        Set<OrganisationUnit> set = new HashSet<OrganisationUnit>();

        if ( hasChild() )
        {
            set = children;
        }
        else
        {
            set.add( this );
        }

        return set;
    }

    public boolean isPolygon()
    {
        return (featureType.equals( FEATURETYPE_MULTIPOLYGON ) || featureType.equals( FEATURETYPE_POLYGON ));
    }

    public boolean isPoint()
    {
        return featureType.equals( FEATURETYPE_POINT );
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

        if ( !(o instanceof OrganisationUnit) )
        {
            return false;
        }

        final OrganisationUnit other = (OrganisationUnit) o;

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

    public Set<OrganisationUnit> getChildren()
    {
        return children;
    }

    public void setChildren( Set<OrganisationUnit> children )
    {
        this.children = children;
    }

    @XmlElement
    @XmlJavaTypeAdapter( BaseNameableObjectXmlAdapter.class )
    @JsonSerialize( using = JsonNameableObjectSerializer.class )
    public OrganisationUnit getParent()
    {
        return parent;
    }

    public void setParent( OrganisationUnit parent )
    {
        this.parent = parent;
    }

    public String getAlternativeName()
    {
        return getShortName();
    }

    public void setAlternativeName( String alternativeName )
    {
        throw new UnsupportedOperationException( "Cannot set alternativename on OrganisationUnit: " + alternativeName );
    }

    @XmlElement
    @JsonProperty
    @JsonSerialize( using = JsonDateSerializer.class )
    public Date getOpeningDate()
    {
        return openingDate;
    }

    public void setOpeningDate( Date openingDate )
    {
        this.openingDate = openingDate;
    }

    @XmlElement
    @JsonProperty
    @JsonSerialize( using = JsonDateSerializer.class )
    public Date getClosedDate()
    {
        return closedDate;
    }

    public void setClosedDate( Date closedDate )
    {
        this.closedDate = closedDate;
    }

    @XmlElement
    @JsonProperty
    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }

    @XmlElement
    @JsonProperty
    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    @XmlElement
    @JsonProperty
    public String getGeoCode()
    {
        return geoCode;
    }

    public void setGeoCode( String geoCode )
    {
        this.geoCode = geoCode;
    }

    @XmlElement
    @JsonProperty
    public String getFeatureType()
    {
        return featureType;
    }

    public void setFeatureType( String featureType )
    {
        this.featureType = featureType;
    }

    @XmlElement
    @JsonProperty
    public String getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates( String coordinates )
    {
        this.coordinates = coordinates;
    }

    @XmlElement
    @JsonProperty
    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    @XmlElementWrapper( name = "groups" )
    @XmlElement( name = "group" )
    @XmlJavaTypeAdapter( BaseIdentifiableObjectXmlAdapter.class )
    @JsonSerialize( using = JsonIdentifiableObjectSetSerializer.class )
    public Set<OrganisationUnitGroup> getGroups()
    {
        return groups;
    }

    public void setGroups( Set<OrganisationUnitGroup> groups )
    {
        this.groups = groups;
    }

    @XmlElementWrapper( name = "dataSets" )
    @XmlElement( name = "dataSet" )
    @XmlJavaTypeAdapter( BaseNameableObjectXmlAdapter.class )
    @JsonSerialize( using = JsonNameableObjectSetSerializer.class )
    public Set<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( Set<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    // TODO expose this when marshalling?
    public Set<User> getUsers()
    {
        return users;
    }

    public void setUsers( Set<User> users )
    {
        this.users = users;
    }

    @XmlElement
    @JsonProperty
    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson( String contactPerson )
    {
        this.contactPerson = contactPerson;
    }

    @XmlElement
    @JsonProperty
    public String getAddress()
    {
        return address;
    }

    public void setAddress( String address )
    {
        this.address = address;
    }

    @XmlElement
    @JsonProperty
    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    @XmlElement
    @JsonProperty
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    @XmlElement
    @JsonProperty
    public boolean isHasPatients()
    {
        return hasPatients;
    }

    public void setHasPatients( boolean hasPatients )
    {
        this.hasPatients = hasPatients;
    }

    @XmlElement
    @JsonProperty
    public int getLevel()
    {
        return level;
    }

    public void setLevel( int level )
    {
        this.level = level;
    }

    @XmlElement
    @JsonProperty
    public boolean isCurrentParent()
    {
        return currentParent;
    }

    public void setCurrentParent( boolean currentParent )
    {
        this.currentParent = currentParent;
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

    public Set<AttributeValue> getAttributeValues()
    {
        return attributeValues;
    }

    public void setAttributeValues( Set<AttributeValue> attributeValues )
    {
        this.attributeValues = attributeValues;
    }

    public String[] getGroupNames()
    {
        return groupNames;
    }

    public void setGroupNames( String[] groupNames )
    {
        this.groupNames = groupNames;
    }
}
