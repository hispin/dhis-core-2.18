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

package org.hisp.dhis.caseentry.action.caseentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.comparator.ProgramStageInstanceDueDateComparator;

public class GetDataRecordsAction
    extends ActionPagingSupport<Patient>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private boolean listAll;

    public void setListAll( boolean listAll )
    {
        this.listAll = listAll;
    }
    
    private Boolean searchBySelectedOrgunit;
    
    public void setSearchBySelectedOrgunit( Boolean searchBySelectedOrgunit )
    {
        this.searchBySelectedOrgunit = searchBySelectedOrgunit;
    }
    
    private List<String> searchTexts = new ArrayList<String>();

    public void setSearchTexts( List<String> searchTexts )
    {
        this.searchTexts = searchTexts;
    }

    private Integer total;

    public Integer getTotal()
    {
        return total;
    }

    private Collection<ProgramInstance> programInstances = new ArrayList<ProgramInstance>();

    public Collection<ProgramInstance> getProgramInstances()
    {
        return programInstances;
    }

    private Map<ProgramInstance, List<ProgramStageInstance>> programStageInstanceMap = new HashMap<ProgramInstance, List<ProgramStageInstance>>();

    public Map<ProgramInstance, List<ProgramStageInstance>> getProgramStageInstanceMap()
    {
        return programStageInstanceMap;
    }

    private Map<Integer, Integer> statusMap = new HashMap<Integer, Integer>();

    public Map<Integer, Integer> getStatusMap()
    {
        return statusMap;
    }

    private Map<Patient, ProgramInstance> programInstanceMap = new HashMap<Patient, ProgramInstance>();

    public Map<Patient, ProgramInstance> getProgramInstanceMap()
    {
        return programInstanceMap;
    }

    private Collection<Patient> patients;

    public Collection<Patient> getPatients()
    {
        return patients;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit orgunit = selectedStateManager.getSelectedOrganisationUnit();

        Program program = programService.getProgram( programId );

        // ---------------------------------------------------------------------
        // Program instances for the selected program
        // ---------------------------------------------------------------------
        
        // List all patients
        if ( listAll )
        {
            total = patientService.countGetPatientsByOrgUnit( orgunit );
            this.paging = createPaging( total );

            patients = new ArrayList<Patient>( patientService.getPatients( orgunit, program, paging.getStartPos(),
                paging.getPageSize() ) );

        }
        // search patients
        else if ( searchTexts.size() > 0 )
        {
            orgunit = (searchBySelectedOrgunit) ? orgunit : null;
            
            total = patientService.countSearchPatients( searchTexts, orgunit );
            this.paging = createPaging( total );
            patients = patientService.searchPatients( searchTexts, orgunit, paging.getStartPos(),
                paging.getPageSize() );
        }

        Collection<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

        for ( Patient patient : patients )
        {
            Collection<ProgramInstance> _programInstances = programInstanceService.getProgramInstances( patient,
                program, false );

            if ( _programInstances == null || _programInstances.size() == 0 )
            {
                programInstanceMap.put( patient, null );
            }
            else
            {
                for ( ProgramInstance programInstance : _programInstances )
                {
                    programInstanceMap.put( patient, programInstance );
                    programInstances.add( programInstance );

                    List<ProgramStageInstance> programStageInstanceList = new ArrayList<ProgramStageInstance>(
                        programInstance.getProgramStageInstances() );
                    Collections.sort( programStageInstanceList, new ProgramStageInstanceDueDateComparator() );

                    programStageInstanceMap.put( programInstance, programStageInstanceList );
                    programStageInstances.addAll( programStageInstanceList );
                }
            }
        }

        statusMap = programStageInstanceService.statusProgramStageInstances( programStageInstances );

        return SUCCESS;
    }
}
