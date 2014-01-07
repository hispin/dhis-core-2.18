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

/**
 * @author Chau Thu Tran
 *
 * @version $ ProgramPatientAttributeService.java Jan 7, 2014 9:33:20 AM $
 */
public interface ProgramPatientAttributeService
{
    String ID = ProgramPatientAttributeService.class.getName();

    /**
     * Adds an {@link ProgramPatientAttribute}
     * 
     * @param programPatientAttribute The to ProgramPatientAttribute add.
     * 
     * @return A generated unique id of the added
     *         {@link ProgramPatientAttribute}.
     */
    void addProgramPatientAttribute( ProgramPatientAttribute programPatientAttribute );

    /**
     * Updates an {@link ProgramPatientAttribute}.
     * 
     * @param programPatientAttribute the ProgramPatientAttribute to update.
     */
    void updateProgramPatientAttribute( ProgramPatientAttribute programPatientAttribute );

    /**
     * Deletes a {@link ProgramPatientAttribute}.
     * 
     * @param programPatientAttribute the ProgramPatientAttribute to delete.
     */
    void deleteProgramPatientAttribute( ProgramPatientAttribute programPatientAttribute );

    /**
     * Retrieve ProgramPatientAttribute list on a program  and a data
     * element
     * 
     * @param program Program
     * @param patientAttribute PatientAttribute
     * 
     * @return ProgramPatientAttribute
     */
    ProgramPatientAttribute get( Program program, PatientAttribute patientAttribute );

    /**
     * Returns all {@link ProgramPatientAttribute}
     * 
     * @return a collection of all ProgramPatientAttribute, or an empty
     *         collection if there are no ProgramPatientAttributes.
     */
    Collection<ProgramPatientAttribute> getAllProgramPatientAttributes();

    /**
     * Retrieve ProgramPatientAttribute list on a program
     * 
     * @param program Program
     * 
     * @return ProgramPatientAttribute list
     */
    Collection<ProgramPatientAttribute> get( Program program );

    /**
     * Retrieve PatientAttribute list on a program
     * 
     * @param program Program
     * 
     * @return ProgramPatientAttribute list
     */
    Collection<PatientAttribute> getListPatientAttribute( Program program );
    

    /**
     * Retrieve PatientAttribute list on a program
     * 
     * @return ProgramPatientAttribute list
     */
    Collection<PatientAttribute> getPatientAttributes();

}