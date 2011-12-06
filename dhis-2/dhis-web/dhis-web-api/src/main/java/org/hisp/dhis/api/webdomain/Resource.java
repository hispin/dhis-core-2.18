package org.hisp.dhis.api.webdomain;

/*
 * Copyright (c) 2004-2011, University of Oslo
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
import org.hisp.dhis.api.adapter.MediaTypeListJsonSerializer;
import org.hisp.dhis.api.adapter.MediaTypeXmlAdapter;
import org.hisp.dhis.api.adapter.RequestMethodListJsonSerializer;
import org.hisp.dhis.api.adapter.RequestMethodXmlAdapter;
import org.hisp.dhis.common.BaseLinkableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * At some point this class will be extended to show all available options
 * for a current user for this resource. For now it is only used for index page.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "resource", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Resource extends BaseLinkableObject
{
    private String name;

    private Class clazz;

    private List<RequestMethod> methods = new ArrayList<RequestMethod>();

    private List<MediaType> mediaTypes = new ArrayList<MediaType>();

    public Resource()
    {

    }

    public Resource( String name, Class clazz, List<RequestMethod> methods, List<MediaType> mediaTypes )
    {
        this.name = name;
        this.clazz = clazz;
        this.methods = methods;
        this.mediaTypes = mediaTypes;
    }

    @XmlAttribute
    @JsonProperty
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @XmlElementWrapper( name = "methods" )
    @XmlElement( name = "method" )
    @XmlJavaTypeAdapter( RequestMethodXmlAdapter.class )
    @JsonSerialize( using = RequestMethodListJsonSerializer.class )
    public List<RequestMethod> getMethods()
    {
        return methods;
    }

    public void setMethods( List<RequestMethod> methods )
    {
        this.methods = methods;
    }

    @XmlElementWrapper( name = "mediaTypes" )
    @XmlElement( name = "mediaType" )
    @XmlJavaTypeAdapter( MediaTypeXmlAdapter.class )
    @JsonSerialize( using = MediaTypeListJsonSerializer.class )
    public List<MediaType> getMediaTypes()
    {
        return mediaTypes;
    }

    public void setMediaTypes( List<MediaType> mediaTypes )
    {
        this.mediaTypes = mediaTypes;
    }

    public Class getClazz()
    {
        return clazz;
    }

    public void setClazz( Class clazz )
    {
        this.clazz = clazz;
    }
}
