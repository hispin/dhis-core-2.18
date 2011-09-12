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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class Dxf
{
   // ---------------------------------------------------------------------------
   // Element and attribute names
   // ---------------------------------------------------------------------------

    public static final String NAMESPACE_20 = "http://dhis2.org/schema/dxf/2.0";

    public static final String DXFROOT = "dxf";

    public static final String ATTRIBUTE_MINOR_VERSION = "minorVersion";

    public static final String ATTRIBUTE_EXPORTED = "exported";

    public static final String DATAVALUESETS = "dataValueSets";

    public static final String DATAVALUESET = "dataValueSet";

    public static final String MINOR_VERSION_10 = "1.0";
    
    @XmlElementWrapper( name="dataSets" )
    @XmlElement( name="dataSet" )
    private List<DataSet> dataSets;

    @XmlElementWrapper( name=DATAVALUESETS )
    @XmlElement( name="dataValueSet" )
    private List<DataValueSet> dataValueSets;

    @XmlElementWrapper( name="orgUnits" )
    @XmlElement( name="orgUnit" )
    private List<OrgUnit> orgUnits;
    
    public void setDataValueSets( List<DataValueSet> dataValueSets )
    {
        this.dataValueSets = dataValueSets;
    }

    public List<DataValueSet> getDataValueSets()
    {
        return dataValueSets;
    }

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    public List<OrgUnit> getOrgUnits()
    {
        return orgUnits;
    }

    public void setOrgUnits( List<OrgUnit> orgUnits )
    {
        this.orgUnits = orgUnits;
    }
}
