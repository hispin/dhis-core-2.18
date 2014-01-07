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

import org.hisp.dhis.patient.PatientIdentifierType;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ProgramPatientIdentifierType.java Jan 7, 2014 3:37:02 PM $
 */
public class ProgramPatientIdentifierType
    implements Serializable
{
    private static final long serialVersionUID = -2420475559273198337L;

    private Program program;

    private PatientIdentifierType patientIdentifierType;

    private boolean displayedInList;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramPatientIdentifierType()
    {
    }

    public ProgramPatientIdentifierType( Program program, PatientIdentifierType patientIdentifierType,
        boolean displayedInList )
    {
        this.program = program;
        this.patientIdentifierType = patientIdentifierType;
        this.displayedInList = displayedInList;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + program.hashCode();
        result = result * prime + patientIdentifierType.hashCode();

        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final ProgramPatientIdentifierType other = (ProgramPatientIdentifierType) object;

        return program.getId() == other.getProgram().getId()
            && patientIdentifierType.getId() == other.getPatientIdentifierType().getId();
    }

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public PatientIdentifierType getPatientIdentifierType()
    {
        return patientIdentifierType;
    }

    public void setPatientIdentifierType( PatientIdentifierType patientIdentifierType )
    {
        this.patientIdentifierType = patientIdentifierType;
    }

    public Boolean getDisplayedInList()
    {
        return displayedInList;
    }

    public void setDisplayedInList( Boolean displayedInList )
    {
        this.displayedInList = displayedInList;
    }
    
}