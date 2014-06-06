package org.hisp.dhis.schema;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;

import java.lang.reflect.Method;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "property", namespace = DxfNamespaces.DXF_2_0 )
public class Property
{
    /**
     * Class for property.
     */
    private Class<?> klass;

    /**
     * If this property is a collection, this is the class of the items inside the collection.
     */
    private Class<?> itemKlass;

    /**
     * Direct link to getter for this property.
     */
    private Method getterMethod;

    /**
     * Name for this property, if this class is a collection, it is the name of the items -inside- the collection
     * and not the collection wrapper itself.
     */
    private String name;

    /**
     * Name of collection wrapper.
     */
    private String collectionName;

    /**
     * Description if provided, will be fetched from @Description annotation.
     *
     * @see org.hisp.dhis.common.annotation.Description
     */
    private String description;

    /**
     * XML-Namespace used for this property.
     */
    private String namespaceURI;

    /**
     * Usually only used for XML. Is this property considered an attribute.
     */
    private boolean attribute;

    /**
     * Is this a Collection sub-class.
     *
     * @see java.util.Collection
     */
    private boolean collection;

    /**
     * Is this class a sub-class of IdentifiableObject
     *
     * @see org.hisp.dhis.common.IdentifiableObject
     */
    private boolean identifiableObject;

    /**
     * Is this class a sub-class of NameableObject
     *
     * @see org.hisp.dhis.common.NameableObject
     */
    private boolean nameableObject;

    public Property()
    {
    }

    public Property( Method getterMethod )
    {
        this.getterMethod = getterMethod;
    }

    public Property( Method getterMethod, Class<?> klass )
    {
        this.getterMethod = getterMethod;
        setKlass( klass );
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Class<?> getKlass()
    {
        return klass;
    }

    public void setKlass( Class<?> klass )
    {
        this.identifiableObject = IdentifiableObject.class.isAssignableFrom( klass );
        this.nameableObject = NameableObject.class.isAssignableFrom( klass );
        this.klass = klass;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Class<?> getItemKlass()
    {
        return itemKlass;
    }

    public void setItemKlass( Class<?> itemKlass )
    {
        this.itemKlass = itemKlass;
    }

    public Method getGetterMethod()
    {
        return getterMethod;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getCollectionName()
    {
        return collectionName == null ? name : collectionName;
    }

    public void setCollectionName( String collectionName )
    {
        this.collectionName = collectionName;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getNamespaceURI()
    {
        return namespaceURI;
    }

    public void setNamespaceURI( String namespaceURI )
    {
        this.namespaceURI = namespaceURI;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isAttribute()
    {
        return attribute;
    }

    public void setAttribute( boolean attribute )
    {
        this.attribute = attribute;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isCollection()
    {
        return collection;
    }

    public void setCollection( boolean collection )
    {
        this.collection = collection;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isIdentifiableObject()
    {
        return identifiableObject;
    }

    public void setIdentifiableObject( boolean identifiableObject )
    {
        this.identifiableObject = identifiableObject;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isNameableObject()
    {
        return nameableObject;
    }

    public void setNameableObject( boolean nameableObject )
    {
        this.nameableObject = nameableObject;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Property property = (Property) o;

        if ( attribute != property.attribute ) return false;
        if ( collection != property.collection ) return false;
        if ( identifiableObject != property.identifiableObject ) return false;
        if ( nameableObject != property.nameableObject ) return false;
        if ( collectionName != null ? !collectionName.equals( property.collectionName ) : property.collectionName != null ) return false;
        if ( description != null ? !description.equals( property.description ) : property.description != null ) return false;
        if ( getterMethod != null ? !getterMethod.equals( property.getterMethod ) : property.getterMethod != null ) return false;
        if ( klass != null ? !klass.equals( property.klass ) : property.klass != null ) return false;
        if ( name != null ? !name.equals( property.name ) : property.name != null ) return false;
        if ( namespaceURI != null ? !namespaceURI.equals( property.namespaceURI ) : property.namespaceURI != null ) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = klass != null ? klass.hashCode() : 0;
        result = 31 * result + (getterMethod != null ? getterMethod.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (collectionName != null ? collectionName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (namespaceURI != null ? namespaceURI.hashCode() : 0);
        result = 31 * result + (attribute ? 1 : 0);
        result = 31 * result + (collection ? 1 : 0);
        result = 31 * result + (identifiableObject ? 1 : 0);
        result = 31 * result + (nameableObject ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Property{" +
            "klass=" + klass +
            ", getterMethod=" + getterMethod +
            ", name='" + name + '\'' +
            ", collectionName='" + collectionName + '\'' +
            ", description='" + description + '\'' +
            ", namespaceURI='" + namespaceURI + '\'' +
            ", attribute=" + attribute +
            ", collection=" + collection +
            ", identifiableObject=" + identifiableObject +
            ", nameableObject=" + nameableObject +
            '}';
    }
}
