package org.hisp.dhis.importexport.dxf2.model;

/*
 * Copyright (c) 2011, University of Oslo
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

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.namespace.QName;

@XmlAccessorType( XmlAccessType.FIELD )
public class DataValue
{
   // ---------------------------------------------------------------------------
   // Element and attribute names
   // ---------------------------------------------------------------------------

    public static final String ATTR_DATAELEMENT = "dataElement";
    public static final String ATTR_VALUE = "value";
    public static final String ATTR_CATEGORY_OPTION_COMBO = "categoryOptionCombo";
    public static final String ATTR_ORGUNIT = "orgUnit";

    @XmlAttribute( name = ATTR_DATAELEMENT, required = true )
    private String dataElementIdentifier;

    @XmlAttribute( name = ATTR_VALUE, required = true)
    private String value;

    /**
     * optional - defaults to default
     */
    @XmlAttribute( name = ATTR_CATEGORY_OPTION_COMBO )
    private String categoryOptionComboIdentifier;

    /**
     * optional - defaults to orgUnit id from datavalueset
     */
    @XmlAttribute( name = ATTR_ORGUNIT )
    private String organisationUnitIdentifier;

    /**
     * Arbitrary attributes identifying dimensions by concept name
     */
    @XmlAnyAttribute
    private Map<QName,Object> dimensions;

    public String getDataElementIdentifier()
    {
        return dataElementIdentifier;
    }

    public void setDataElementIdentifier( String dataElementId )
    {
        this.dataElementIdentifier = dataElementId;
    }

    public String getCategoryOptionComboIdentifier()
    {
        return categoryOptionComboIdentifier;
    }

    public void setCategoryOptionComboIdentifier( String categoryOptionComboId )
    {
        this.categoryOptionComboIdentifier = categoryOptionComboId;
    }

    public String getOrganisationUnitIdentifier()
    {
        return organisationUnitIdentifier;
    }

    public void setOrganisationUnitIdentifier( String organisationUnitIdentifier )
    {
        this.organisationUnitIdentifier = organisationUnitIdentifier;
    }

    public Map<QName, Object> getDimensions()
    {
        if ( dimensions == null )
        {
            dimensions = new HashMap<QName,Object>();
        }
        
        return dimensions;
    }

    public void setDimensions( Map<QName, Object> dimensions )
    {
        this.dimensions = dimensions;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
}
