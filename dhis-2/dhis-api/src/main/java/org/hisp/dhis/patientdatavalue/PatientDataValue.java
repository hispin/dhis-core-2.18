package org.hisp.dhis.patientdatavalue;
/*
 * Copyright (c) 2004-2009, University of Oslo
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

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.ProgramStageInstance;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class PatientDataValue
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 8538519573273769587L;

    private DataElement dataElement;

    private ProgramStageInstance programStageInstance;

    private OrganisationUnit organisationUnit;

    private Date timestamp;

    private String value;

    private boolean providedByAnotherFacility = false;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PatientDataValue()
    {
    }

    public PatientDataValue( ProgramStageInstance programStageInstance, DataElement dataElement,
        OrganisationUnit organisationUnit )
    {
        this.programStageInstance = programStageInstance;
        this.dataElement = dataElement;
        this.organisationUnit = organisationUnit;
    }

    public PatientDataValue( ProgramStageInstance programStageInstance, DataElement dataElement,
        OrganisationUnit organisationUnit, Date timeStamp )
    {
        this.programStageInstance = programStageInstance;
        this.dataElement = dataElement;
        this.organisationUnit = organisationUnit;
        this.timestamp = timeStamp;
    }

    public PatientDataValue( ProgramStageInstance programStageInstance, DataElement dataElement,
        OrganisationUnit organisationUnit, Date timeStamp, String value )
    {
        this.programStageInstance = programStageInstance;
        this.dataElement = dataElement;
        this.organisationUnit = organisationUnit;
        this.timestamp = timeStamp;
        this.value = value;
    }

    public PatientDataValue( ProgramStageInstance programStageInstance, DataElement dataElement,
        OrganisationUnit organisationUnit, Date timeStamp, String value,
        boolean providedByAnotherFacility )
    {
        this.programStageInstance = programStageInstance;
        this.dataElement = dataElement;
        this.organisationUnit = organisationUnit;
        this.timestamp = timeStamp;
        this.value = value;
        this.providedByAnotherFacility = providedByAnotherFacility;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataElement == null) ? 0 : dataElement.hashCode());
        result = prime * result + ((organisationUnit == null) ? 0 : organisationUnit.hashCode());
        result = prime * result + ((programStageInstance == null) ? 0 : programStageInstance.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        PatientDataValue other = (PatientDataValue) obj;
        if ( dataElement == null )
        {
            if ( other.dataElement != null )
                return false;
        }
        else if ( !dataElement.equals( other.dataElement ) )
            return false;
        if ( organisationUnit == null )
        {
            if ( other.organisationUnit != null )
                return false;
        }
        else if ( !organisationUnit.equals( other.organisationUnit ) )
            return false;
        if ( programStageInstance == null )
        {
            if ( other.programStageInstance != null )
                return false;
        }
        else if ( !programStageInstance.equals( other.programStageInstance ) )
            return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public void setProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        this.programStageInstance = programStageInstance;
    }

    public ProgramStageInstance getProgramStageInstance()
    {
        return programStageInstance;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setProvidedByAnotherFacility( boolean providedByAnotherFacility )
    {
        this.providedByAnotherFacility = providedByAnotherFacility;
    }

    public boolean isProvidedByAnotherFacility()
    {
        return providedByAnotherFacility;
    }
}
