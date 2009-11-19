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
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class Program
    implements Serializable
{
    private int id;

    private String name;

    private String description;

    private Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

    private Set<ProgramInstance> programInstances = new HashSet<ProgramInstance>();

    private Set<ProgramStage> programStages = new HashSet<ProgramStage>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Program()
    {
    }

    public Program( String name, String description )
    {
        this.name = name;
        this.description = description;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

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

        if ( !(o instanceof Program) )
        {
            return false;
        }

        final Program other = (Program) o;

        return name.equals( other.getName() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setOrganisationUnits( Set<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    public Set<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setProgramInstances( Set<ProgramInstance> programInstances )
    {
        this.programInstances = programInstances;
    }

    public Set<ProgramInstance> getProgramInstances()
    {
        return programInstances;
    }

    public void setProgramStages( Set<ProgramStage> programStages )
    {
        this.programStages = programStages;
    }

    public Set<ProgramStage> getProgramStages()
    {
        return programStages;
    }

    // -------------------------------------------------------------------------
    // Convenience method
    // -------------------------------------------------------------------------

    public ProgramStage getProgramStageByStage( int stage )
    {

        int count = 1;

        for ( ProgramStage programStage : programStages )
        {
            if ( count == stage )
            {
                return programStage;
            }
            else
            {
                count++;
            }

        }

        return null;

    }

}
