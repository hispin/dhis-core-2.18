/*
 * Copyright (c) 2004-2013, University of Oslo
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

package org.hisp.dhis.program;

import java.io.Serializable;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifierType;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ProgramPatientProperty.java Sep 20, 2013 10:39:33 AM $
 */
public class ProgramPatientProperty
    implements Serializable
{
    private static final long serialVersionUID = 8089655298024075223L;

    private int id;

    private Program program;

    private PatientIdentifierType patientIdentifierType;

    private PatientAttribute patientAttribute;

    private String propertyName;

    private String defaultValue;

    private boolean hidden;

    private int sortOrder;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramPatientProperty()
    {

    }

    public ProgramPatientProperty( Program program, String propertyName, String defaultValue, boolean hidden,
        int sortOrder )
    {
        this.program = program;
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.hidden = hidden;
    }

    public ProgramPatientProperty( Program program, PatientIdentifierType patientIdentifierType, String defaultValue,
        boolean hidden, int sortOrder )
    {
        this.program = program;
        this.patientIdentifierType = patientIdentifierType;
        this.defaultValue = defaultValue;
        this.hidden = hidden;
    }

    public ProgramPatientProperty( Program prorgam, PatientAttribute patientAttribute, String defaultValue,
        boolean hidden, int sortOrder )
    {
        this.program = prorgam;
        this.patientAttribute = patientAttribute;
        this.hidden = hidden;
        this.defaultValue = defaultValue;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((patientAttribute == null) ? 0 : patientAttribute.hashCode());
        result = prime * result + ((patientIdentifierType == null) ? 0 : patientIdentifierType.hashCode());
        result = prime * result + ((program == null) ? 0 : program.hashCode());
        result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
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
        ProgramPatientProperty other = (ProgramPatientProperty) obj;
        if ( patientAttribute == null )
        {
            if ( other.patientAttribute != null )
                return false;
        }
        else if ( !patientAttribute.equals( other.patientAttribute ) )
            return false;
        if ( patientIdentifierType == null )
        {
            if ( other.patientIdentifierType != null )
                return false;
        }
        else if ( !patientIdentifierType.equals( other.patientIdentifierType ) )
            return false;
        if ( program == null )
        {
            if ( other.program != null )
                return false;
        }
        else if ( !program.equals( other.program ) )
            return false;
        if ( propertyName == null )
        {
            if ( other.propertyName != null )
                return false;
        }
        else if ( !propertyName.equals( other.propertyName ) )
            return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public PatientAttribute getPatientAttribute()
    {
        return patientAttribute;
    }

    public void setPatientAttribute( PatientAttribute patientAttribute )
    {
        this.patientAttribute = patientAttribute;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden( boolean hidden )
    {
        this.hidden = hidden;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue( String defaultValue )
    {
        this.defaultValue = defaultValue;
    }

    public PatientIdentifierType getPatientIdentifierType()
    {
        return patientIdentifierType;
    }

    public void setPatientIdentifierType( PatientIdentifierType patientIdentifierType )
    {
        this.patientIdentifierType = patientIdentifierType;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public void setPropertyName( String propertyName )
    {
        this.propertyName = propertyName;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public int getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( int sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public boolean isAttribute()
    {
        return (patientAttribute != null) ? true : false;
    }

    public boolean isIdentifierType()
    {
        return (patientIdentifierType != null) ? true : false;
    }

    public boolean isProperty()
    {
        return (propertyName != null) ? true : false;
    }

}
