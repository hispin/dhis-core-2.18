/*
 * Copyright (c) 2004-2012, University of Oslo
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
package org.hisp.dhis.caseentry.action.patient;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;

/**
 * @author Chau Thu Tran
 * @version $ID : GetPatientsByNameAction.java Dec 23, 2010 9:14:34 AM $
 */
public class GetPatientsByNameAction
    extends ActionPagingSupport<Patient>
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private PatientService patientService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String fullName;

    private List<Patient> patients;

    private Integer total;

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }

    public List<Patient> getPatients()
    {
        return patients;
    }

    public Integer getTotal()
    {
        return total;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        fullName = fullName.trim();
        
        total = patientService.countGetPatientsByName( fullName );
        this.paging = createPaging( total );

        patients = new ArrayList<Patient>( patientService.getPatientsByNames( fullName, paging.getStartPos(), paging
            .getPageSize() ) );

        return SUCCESS;
    }

}
