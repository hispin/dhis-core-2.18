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
package org.hisp.dhis.program;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.patient.Patient;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class ProgramInstance
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -1235315582356509653L;

    private int id;

    private Date dateOfIncident;

    private Date enrollmentDate;

    private Date endDate;

    private boolean completed = false;

    private Patient patient;

    private Program program;

    private Set<ProgramStageInstance> programStageInstances = new HashSet<ProgramStageInstance>();

    private Set<ProgramAttribute> attributes = new HashSet<ProgramAttribute>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramInstance()
    {
    }

    public ProgramInstance( Date enrollmentDate, Date endDate, Patient patient, Program program )
    {
        this.enrollmentDate = enrollmentDate;
        this.endDate = endDate;
        this.patient = patient;
        this.program = program;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof ProgramInstance) )
        {
            return false;
        }

        final ProgramInstance other = (ProgramInstance) o;

        return enrollmentDate.equals( other.getEnrollmentDate() ) && patient.equals( other.getPatient() )
            && program.equals( other.getProgram() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + enrollmentDate.hashCode();
        if ( patient != null )
        {
            result = result * prime + patient.hashCode();
        }
        else
        {
            result = result * prime + id;
        }
        result = result * prime + program.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId( int id )
    {
        this.id = id;
    }

    /**
     * @return the dateOfIncident
     */
    public Date getDateOfIncident()
    {
        return dateOfIncident;
    }

    /**
     * @param dateOfIncident the dateOfIncident to set
     */
    public void setDateOfIncident( Date dateOfIncident )
    {
        this.dateOfIncident = dateOfIncident;
    }

    /**
     * @return the enrollmentDate
     */
    public Date getEnrollmentDate()
    {
        return enrollmentDate;
    }

    /**
     * @param enrollmentDate the enrollmentDate to set
     */
    public void setEnrollmentDate( Date enrollmentDate )
    {
        this.enrollmentDate = enrollmentDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate()
    {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }

    /**
     * @return the completed
     */
    public boolean isCompleted()
    {
        return completed;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

    /**
     * @return the patient
     */
    public Patient getPatient()
    {
        return patient;
    }

    /**
     * @param patient the patient to set
     */
    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    /**
     * @return the program
     */
    public Program getProgram()
    {
        return program;
    }

    /**
     * @param program the program to set
     */
    public void setProgram( Program program )
    {
        this.program = program;
    }

    /**
     * @return the programStageInstances
     */
    public Set<ProgramStageInstance> getProgramStageInstances()
    {
        return programStageInstances;
    }

    /**
     * @param programStageInstances the programStageInstances to set
     */
    public void setProgramStageInstances( Set<ProgramStageInstance> programStageInstances )
    {
        this.programStageInstances = programStageInstances;
    }

    public Set<ProgramAttribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes( Set<ProgramAttribute> attributes )
    {
        this.attributes = attributes;
    }

    // -------------------------------------------------------------------------
    // Convenience method
    // -------------------------------------------------------------------------

    public ProgramStageInstance getProgramStageInstanceByStage( int stage )
    {

        int count = 1;

        for ( ProgramStageInstance programInstanceStage : programStageInstances )
        {
            if ( count == stage )
            {
                return programInstanceStage;
            }
            else
            {
                count++;
            }
        }

        return null;

    }

}
