package org.hisp.dhis.common;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@XmlRootElement( name = "collection", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class BaseCollection implements LinkableObject
{
    private Pager pager;

    private LinkableObject linkableObject;

    public BaseCollection()
    {
        // TODO this will be made more dynamic for 2.7
        linkableObject = new BaseLinkableObject();
    }

    //-------------------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------------------

    @JsonProperty
    public Pager getPager()
    {
        return pager;
    }

    public void setPager( Pager pager )
    {
        this.pager = pager;
    }

    public LinkableObject getLinkableObject()
    {
        return linkableObject;
    }

    public void setLinkableObject( LinkableObject linkableObject )
    {
        this.linkableObject = linkableObject;
    }

    //-------------------------------------------------------------------------------------
    // Serializable fields
    //-------------------------------------------------------------------------------------

    @XmlAttribute
    @JsonProperty
    public String getLink()
    {
        if ( linkableObject == null )
        {
            return null;
        }

        return linkableObject.getLink();
    }

    /**
     * Set link for collection. This will be replaced for 2.7 with a real linkableObject.
     *
     * @param link
     */
    public void setLink( String link )
    {
        if ( linkableObject != null )
        {
            linkableObject.setLink( link );
        }
    }

    @XmlAttribute
    public Integer getPage()
    {
        if ( pager == null )
        {
            return null;
        }

        return pager.getPage();
    }

    @XmlAttribute
    public Integer getTotal()
    {
        if ( pager == null )
        {
            return null;
        }

        return pager.getTotal();
    }

    @XmlAttribute
    public Integer getPageSize()
    {
        if ( pager == null )
        {
            return null;
        }

        return pager.getPageSize();
    }

    @XmlAttribute
    public Integer getPageCount()
    {
        if ( pager == null )
        {
            return null;
        }

        return pager.getPageCount();
    }
}
