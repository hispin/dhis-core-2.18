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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Lists;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;

import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "schema", namespace = DxfNamespaces.DXF_2_0 )
public class Schema
{
    private Class<?> klass;

    private boolean identifiableObject;

    private String singular;

    private String plural;

    private boolean shareable;

    private List<String> publicAuthorities = Lists.newArrayList();

    private List<String> privateAuthorities = Lists.newArrayList();

    private List<String> externalAuthorities = Lists.newArrayList();

    private boolean importable;

    private boolean exportable;

    private boolean deletable;

    private List<Property> properties = Lists.newArrayList();

    public Schema( Class<?> klass, String singular, String plural )
    {
        this.klass = klass;
        this.identifiableObject = IdentifiableObject.class.isAssignableFrom( klass );
        this.singular = singular;
        this.plural = plural;
    }

    public Schema( Class<?> klass, String singular, String plural, boolean importable, boolean exportable, boolean deletable )
    {
        this( klass, singular, plural );
        this.importable = importable;
        this.exportable = exportable;
        this.deletable = deletable;
    }

    @JsonProperty
    @JacksonXmlProperty( isAttribute = true )
    public Class<?> getKlass()
    {
        return klass;
    }

    public void setKlass( Class<?> klass )
    {
        this.klass = klass;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isIdentifiableObject()
    {
        return identifiableObject;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getSingular()
    {
        return singular;
    }

    public void setSingular( String singular )
    {
        this.singular = singular;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getPlural()
    {
        return plural;
    }

    public void setPlural( String plural )
    {
        this.plural = plural;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isShareable()
    {
        return shareable;
    }

    public void setShareable( boolean shareable )
    {
        this.shareable = shareable;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "publicAuthorities", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "publicAuthority", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getPublicAuthorities()
    {
        return publicAuthorities;
    }

    public void setPublicAuthorities( List<String> publicAuthorities )
    {
        this.publicAuthorities = publicAuthorities;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "privateAuthorities", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "privateAuthority", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getPrivateAuthorities()
    {
        return privateAuthorities;
    }

    public void setPrivateAuthorities( List<String> privateAuthorities )
    {
        this.privateAuthorities = privateAuthorities;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "externalAuthorities", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "externalAuthority", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getExternalAuthorities()
    {
        return externalAuthorities;
    }

    public void setExternalAuthorities( List<String> externalAuthorities )
    {
        this.externalAuthorities = externalAuthorities;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isImportable()
    {
        return importable;
    }

    public void setImportable( boolean importable )
    {
        this.importable = importable;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isExportable()
    {
        return exportable;
    }

    public void setExportable( boolean exportable )
    {
        this.exportable = exportable;
    }

    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isDeletable()
    {
        return deletable;
    }

    public void setDeletable( boolean deletable )
    {
        this.deletable = deletable;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "properties", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "property", namespace = DxfNamespaces.DXF_2_0 )
    public List<Property> getProperties()
    {
        return properties;
    }

    public void setProperties( List<Property> properties )
    {
        this.properties = properties;
    }

    @Override
    public String toString()
    {
        return "Schema{" +
            "klass=" + klass +
            ", singular='" + singular + '\'' +
            ", plural='" + plural + '\'' +
            ", importable=" + importable +
            ", exportable=" + exportable +
            ", deletable=" + deletable +
            ", properties=" + properties +
            '}';
    }
}
