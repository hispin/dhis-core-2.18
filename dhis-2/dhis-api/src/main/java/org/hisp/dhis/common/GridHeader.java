package org.hisp.dhis.common;

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

import org.codehaus.jackson.annotate.JsonProperty;

import javax.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
@XmlRootElement( name = "header", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class GridHeader
{
    private static final List<String> NUMERIC_TYPES = Arrays.asList( Float.class.getName(), Double.class.getName(), Long.class.getName(), Integer.class.getName() );

    private String name;

    private String column;

    private String type;

    private boolean hidden;

    private boolean meta;

    public GridHeader()
    {
    }

    /**
     * Sets the column property to the name value. Sets the type property to String.
     *
     * @param name   name
     * @param hidden hidden
     * @param meta   meta
     */
    public GridHeader( String name, boolean hidden, boolean meta )
    {
        this.name = name;
        this.column = name;
        this.type = String.class.getName();
        this.hidden = hidden;
        this.meta = meta;
    }

    /**
     * @param name   name
     * @param column column
     * @param type   type
     * @param hidden hidden
     * @param meta   meta
     */
    public GridHeader( String name, String column, String type, boolean hidden, boolean meta )
    {
        this.name = name;
        this.column = column;
        this.type = type;
        this.hidden = hidden;
        this.meta = meta;
    }

    @XmlElement
    @JsonProperty
    public boolean isNumeric()
    {
        return type != null && NUMERIC_TYPES.contains( type );
    }

    @XmlAttribute( namespace = Dxf2Namespace.NAMESPACE )
    @JsonProperty
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @XmlElement
    @JsonProperty
    public String getColumn()
    {
        return column;
    }

    public void setColumn( String column )
    {
        this.column = column;
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

    @XmlElement
    @JsonProperty
    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden( boolean hidden )
    {
        this.hidden = hidden;
    }

    @XmlElement
    @JsonProperty
    public boolean isMeta()
    {
        return meta;
    }

    public void setMeta( boolean meta )
    {
        this.meta = meta;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final GridHeader other = (GridHeader) object;

        return name.equals( other.name );
    }
}
