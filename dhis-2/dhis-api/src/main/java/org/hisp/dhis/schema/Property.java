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
import com.google.common.base.Objects;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.node.annotation.NodeRoot;
import org.hisp.dhis.node.annotation.NodeSimple;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "property", namespace = DxfNamespaces.DXF_2_0 )
@NodeRoot( isWritable = false, isPersisted = false )
public class Property implements Ordered
{
    /**
     * Class for property.
     */
    @NodeSimple
    private Class<?> klass;

    /**
     * If this property is a collection, this is the class of the items inside the collection.
     */
    @NodeSimple
    private Class<?> itemKlass;

    /**
     * Direct link to getter for this property.
     */
    private Method getterMethod;

    /**
     * Direct link to setter for this property.
     */
    private Method setterMethod;

    /**
     * Name for this property, if this class is a collection, it is the name of the items -inside- the collection
     * and not the collection wrapper itself.
     */
    @NodeSimple
    private String name;

    /**
     * Name for actual field, used to persistence operations and getting setter/getter.
     */
    @NodeSimple
    private String fieldName;

    /**
     * Is this property persisted somewhere. This property will be used to create criteria queries
     * on demand (default: false)
     */
    @NodeSimple
    private boolean persisted;

    /**
     * Name of collection wrapper.
     */
    @NodeSimple
    private String collectionName;

    /**
     * If this Property is a collection, should it be wrapped with collectionName?
     */
    @NodeSimple
    private boolean collectionWrapping;

    /**
     * Description if provided, will be fetched from @Description annotation.
     *
     * @see org.hisp.dhis.common.annotation.Description
     */
    @NodeSimple
    private String description;

    /**
     * Namespace used for this property.
     */
    @NodeSimple
    private String namespace;

    /**
     * Usually only used for XML. Is this property considered an attribute.
     */
    @NodeSimple
    private boolean attribute;

    /**
     * This property is true if the type pointed to does not export any properties itself, it is then
     * assumed to be a primitive type. If collection is true, this this check is done on the generic type
     * of the collection, e.g. List<String> would set simple to be true, but List<DataElement> would set it
     * to false.
     */
    @NodeSimple
    private boolean simple;

    /**
     * This property is true if the type of this property is a sub-class of Collection.
     *
     * @see java.util.Collection
     */
    @NodeSimple
    private boolean collection;

    /**
     * If this property is a complex object or a collection, is this property considered
     * the owner of that relationship (important for imports etc).
     */
    @NodeSimple
    private boolean owner;

    /**
     * Is this class a sub-class of IdentifiableObject
     *
     * @see org.hisp.dhis.common.IdentifiableObject
     */
    @NodeSimple
    private boolean identifiableObject;

    /**
     * Is this class a sub-class of NameableObject
     *
     * @see org.hisp.dhis.common.NameableObject
     */
    @NodeSimple
    private boolean nameableObject;

    @NodeSimple
    private boolean readable;

    @NodeSimple
    private boolean writable;

    public Property()
    {
    }

    public Property( Class<?> klass )
    {
        this.klass = klass;
    }

    public Property( Class<?> klass, Method getter, Method setter )
    {
        this( klass );
        this.getterMethod = getter;
        this.setterMethod = setter;
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

    public void setGetterMethod( Method getterMethod )
    {
        this.getterMethod = getterMethod;
    }

    public Method getSetterMethod()
    {
        return setterMethod;
    }

    public void setSetterMethod( Method setterMethod )
    {
        this.setterMethod = setterMethod;
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
    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName( String fieldName )
    {
        this.fieldName = fieldName;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isPersisted()
    {
        return persisted;
    }

    public void setPersisted( boolean persisted )
    {
        this.persisted = persisted;
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
    public boolean isCollectionWrapping()
    {
        return collectionWrapping;
    }

    public void setCollectionWrapping( boolean collectionWrapping )
    {
        this.collectionWrapping = collectionWrapping;
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
    public String getNamespace()
    {
        return namespace;
    }

    public void setNamespace( String namespace )
    {
        this.namespace = namespace;
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
    public boolean isSimple()
    {
        return simple;
    }

    public void setSimple( boolean simple )
    {
        this.simple = simple;
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
    public boolean isOwner()
    {
        return owner;
    }

    public void setOwner( boolean owner )
    {
        this.owner = owner;
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

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isReadable()
    {
        return readable;
    }

    public void setReadable( boolean readable )
    {
        this.readable = readable;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isWritable()
    {
        return writable;
    }

    public void setWritable( boolean writable )
    {
        this.writable = writable;
    }

    public String key()
    {
        return isCollection() ? collectionName : name;
    }

    @Override
    public int getOrder()
    {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( klass, itemKlass, getterMethod, name, fieldName, persisted, collectionName, description,
            namespace, attribute, simple, collection, identifiableObject, nameableObject );
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null || getClass() != obj.getClass() )
        {
            return false;
        }

        final Property other = (Property) obj;

        return Objects.equal( this.klass, other.klass ) && Objects.equal( this.itemKlass, other.itemKlass )
            && Objects.equal( this.getterMethod, other.getterMethod ) && Objects.equal( this.setterMethod, other.setterMethod )
            && Objects.equal( this.name, other.name ) && Objects.equal( this.fieldName, other.fieldName )
            && Objects.equal( this.persisted, other.persisted ) && Objects.equal( this.collectionName, other.collectionName )
            && Objects.equal( this.description, other.description ) && Objects.equal( this.namespace, other.namespace )
            && Objects.equal( this.attribute, other.attribute ) && Objects.equal( this.simple, other.simple )
            && Objects.equal( this.collection, other.collection ) && Objects.equal( this.identifiableObject, other.identifiableObject )
            && Objects.equal( this.nameableObject, other.nameableObject );
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this )
            .add( "klass", klass )
            .add( "itemKlass", itemKlass )
            .add( "getterMethod", getterMethod )
            .add( "name", name )
            .add( "fieldName", fieldName )
            .add( "persisted", persisted )
            .add( "collectionName", collectionName )
            .add( "description", description )
            .add( "namespace", namespace )
            .add( "attribute", attribute )
            .add( "simple", simple )
            .add( "collection", collection )
            .add( "identifiableObject", identifiableObject )
            .add( "nameableObject", nameableObject )
            .toString();
    }
}
