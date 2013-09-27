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

import java.util.Collection;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version $ DefaultProgramPatientPropertyService.java Sep 26, 2013 4:30:49 PM
 *          $
 */
@Transactional
public class DefaultProgramPatientPropertyService
    implements ProgramPatientPropertyService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramPatientPropertyStore programPatientPropertyStore;

    public void setProgramPatientPropertyStore( ProgramPatientPropertyStore programPatientPropertyStore )
    {
        this.programPatientPropertyStore = programPatientPropertyStore;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public void addProgramPatientProperty( ProgramPatientProperty programPatientProperty )
    {
        programPatientPropertyStore.save( programPatientProperty );
    }

    @Override
    public void deleteProgramPatientProperty( ProgramPatientProperty programPatientProperty )
    {
        programPatientPropertyStore.delete( programPatientProperty );
    }

    @Override
    public void updateProgramPatientProperty( ProgramPatientProperty programPatientProperty )
    {
        programPatientPropertyStore.update( programPatientProperty );
    }

    @Override
    public ProgramPatientProperty getProgramPatientProperty( Program program,
        PatientIdentifierType patientIdentifierType )
    {
        return programPatientPropertyStore.get( program, patientIdentifierType );
    }

    @Override
    public Collection<ProgramPatientProperty> getProgramPatientIdentifierTypes( Program program )
    {
        return programPatientPropertyStore.getProgramPatientIdentifierTypes( program );
    }

    @Override
    public Collection<PatientIdentifierType> getPatientIdentifierTypes( Program program )
    {
        return programPatientPropertyStore.getPatientIdentifierTypes( program );
    }

    @Override
    public ProgramPatientProperty getProgramPatientProperty( Program program, PatientAttribute patientAttribute )
    {
        return programPatientPropertyStore.get( program, patientAttribute );
    }

    @Override
    public Collection<PatientAttribute> getPatientAttributes( Program program )
    {
        return programPatientPropertyStore.getPatientAttributes( program );
    }

    @Override
    public ProgramPatientProperty getProgramPatientProperty( Program program, String propertyName )
    {
        return programPatientPropertyStore.get( program, propertyName );
    }

    @Override
    public Collection<ProgramPatientProperty> getProgramPatientProperties( Program program )
    {
        return programPatientPropertyStore.getProgramPatientProperties( program );
    }

    @Override
    public Collection<String> getPatientProperties( Program program )
    {
        return programPatientPropertyStore.getPatientProperties( program );
    }

    @Override
    public Collection<ProgramPatientProperty> getProgramPatientAttributes( Program program )
    {
        return programPatientPropertyStore.getProgramPatientAttributes( program );
    }

}
