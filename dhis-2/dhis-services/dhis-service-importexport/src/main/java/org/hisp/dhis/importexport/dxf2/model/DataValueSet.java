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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class DataValueSet
{
   // ---------------------------------------------------------------------------
   // Element and attribute names
   // ---------------------------------------------------------------------------

    public static final String ATTR_COMMENT = "comment";
    public static final String ATTR_DATASET = "dataSet";
    public static final String ATTR_IDSCHEME = "idScheme";
    public static final String ATTR_ORGUNIT = "orgUnit";
    public static final String ATTR_PERIOD = "period";
    public static final String ATTR_COMPLETE = "period";

    public static final String DATAVALUE = "dataValue";

    public enum IdentificationStrategy { INTERNAL, UID, CODE  };

    /**
     *  A default strategy to use.
     */
    public static final IdentificationStrategy DEFAULT_STRATEGY = IdentificationStrategy.UID;

    @XmlAttribute( name = ATTR_DATASET )
    private String dataSetIdentifier;

    @XmlAttribute( name = ATTR_ORGUNIT )
    private String organisationUnitIdentifier;

    @XmlAttribute( name = ATTR_PERIOD, required = true )
    private String periodIsoDate;

    @XmlAttribute( name = ATTR_COMPLETE )
    private String completeDate;

    @XmlAttribute( name = ATTR_IDSCHEME )
    private IdentificationStrategy idScheme;

    @XmlElement( name = DATAVALUE )
    private List<DataValue> dataValues;

    public IdentificationStrategy getIdScheme()
    {
        return (idScheme != null) ? idScheme : DEFAULT_STRATEGY;
    }

    public void setIdScheme( IdentificationStrategy idScheme )
    {
        this.idScheme = idScheme;
    }

    public String getDataSetIdentifier()
    {
        return dataSetIdentifier;
    }

    public void setDataSetIdentifier( String dataSetId )
    {
        this.dataSetIdentifier = dataSetId;
    }

    public String getOrganisationUnitIdentifier()
    {
        return organisationUnitIdentifier;
    }

    public void setOrganisationUnitIdentifier( String organisationUnitId )
    {
        this.organisationUnitIdentifier = organisationUnitId;
    }

    public String getPeriodIsoDate()
    {
        return periodIsoDate;
    }

    public void setPeriodIsoDate( String periodIsoDate )
    {
        this.periodIsoDate = periodIsoDate;
    }

    public String getCompleteDate()
    {
        return completeDate;
    }

    public void setCompleteDate( String completeDate )
    {
        this.completeDate = completeDate;
    }

    public List<DataValue> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( List<DataValue> dataValues )
    {
        this.dataValues = dataValues;
    }
}
