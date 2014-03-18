package org.hisp.dhis.dxf2.schema;

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

import java.lang.reflect.Method;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "property", namespace = DxfNamespaces.DXF_2_0 )
public class Property
{
    private String name;

    private String description;

    private String xmlName;

    private boolean xmlAttribute;

    private String xmlCollectionName;

    private Class<?> klass;

    private Method method;

    private boolean collection;

    private boolean identifiableObject;

    public Property( Method method )
    {
        this.method = method;
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
    public String getXmlName()
    {
        return xmlName;
    }

    public void setXmlName( String xmlName )
    {
        this.xmlName = xmlName;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isXmlAttribute()
    {
        return xmlAttribute;
    }

    public void setXmlAttribute( boolean xmlAttribute )
    {
        this.xmlAttribute = xmlAttribute;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getXmlCollectionName()
    {
        return xmlCollectionName;
    }

    public void setXmlCollectionName( String xmlCollectionName )
    {
        this.xmlCollectionName = xmlCollectionName;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Class<?> getKlass()
    {
        return klass;
    }

    public void setKlass( Class<?> klass )
    {
        this.klass = klass;
    }

    public Method getMethod()
    {
        return method;
    }

    public void setMethod( Method method )
    {
        this.method = method;
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

    @Override
    public String toString()
    {
        return "Property{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", xmlName='" + xmlName + '\'' +
            ", xmlAttribute=" + xmlAttribute +
            ", xmlCollectionName='" + xmlCollectionName + '\'' +
            ", klass=" + klass +
            ", method=" + method +
            ", collection=" + collection +
            ", identifiableObject=" + identifiableObject +
            '}';
    }
}
