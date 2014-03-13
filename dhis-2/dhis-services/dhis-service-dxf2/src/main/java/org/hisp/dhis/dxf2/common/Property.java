package org.hisp.dhis.dxf2.common;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.lang.reflect.Method;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class Property
{
    private String name;

    private String description;

    private String xmlName;

    private boolean xmlAttribute;

    private String xmlCollectionName;

    private Class<?> clazz;

    private Method method;

    private boolean collection;

    private boolean identifiableObject;

    private Property( Method method )
    {
        this.method = method;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @JsonProperty
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @JsonProperty
    public String getXmlName()
    {
        return xmlName;
    }

    public void setXmlName( String xmlName )
    {
        this.xmlName = xmlName;
    }

    @JsonProperty
    public boolean isXmlAttribute()
    {
        return xmlAttribute;
    }

    public void setXmlAttribute( boolean xmlAttribute )
    {
        this.xmlAttribute = xmlAttribute;
    }

    @JsonProperty
    public String getXmlCollectionName()
    {
        return xmlCollectionName;
    }

    public void setXmlCollectionName( String xmlCollectionName )
    {
        this.xmlCollectionName = xmlCollectionName;
    }

    @JsonProperty
    public Class<?> getClazz()
    {
        return clazz;
    }

    public void setClazz( Class<?> clazz )
    {
        this.clazz = clazz;
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
    public boolean isCollection()
    {
        return collection;
    }

    public void setCollection( boolean collection )
    {
        this.collection = collection;
    }

    @JsonProperty
    public boolean isIdentifiableObject()
    {
        return identifiableObject;
    }

    public void setIdentifiableObject( boolean identifiableObject )
    {
        this.identifiableObject = identifiableObject;
    }

    @Override public String toString()
    {
        return "PropertyDescriptor{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", xmlName='" + xmlName + '\'' +
            ", xmlAttribute=" + xmlAttribute +
            ", xmlCollectionName='" + xmlCollectionName + '\'' +
            ", clazz=" + clazz +
            ", method=" + method +
            ", collection=" + collection +
            ", identifiableObject=" + identifiableObject +
            '}';
    }
}
