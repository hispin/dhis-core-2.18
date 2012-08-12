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

package org.hisp.dhis.caseentry.action.patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.comparator.ProgramStageInstanceDueDateComparator;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version PatientDashboardAction.java 1:30:29 PM Aug 10, 2012 $
 */
public class PatientDashboardAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    private PatientAttributeValueService patientAttributeValueService;

    private RelationshipService relationshipService;

    private ProgramInstanceService programInstanceService;

    private ProgramStageInstanceService programStageInstanceService;

    // -------------------------------------------------------------------------
    // Input && Output
    // -------------------------------------------------------------------------

    private Integer patientId;

    private Patient patient;

    private Set<PatientIdentifier> identifiers;

    private Collection<PatientAttributeValue> attributeValues;

    private Collection<Relationship> relationship;

    private Collection<ProgramInstance> activeProgramInstances;
    
    private Collection<ProgramInstance> completedProgramInstances;

    private Map<Integer, Integer> statusMap = new HashMap<Integer, Integer>();

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public Collection<ProgramInstance> getActiveProgramInstances()
    {
        return activeProgramInstances;
    }

    public Collection<ProgramInstance> getCompletedProgramInstances()
    {
        return completedProgramInstances;
    }

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public Set<PatientIdentifier> getIdentifiers()
    {
        return identifiers;
    }

    public Collection<PatientAttributeValue> getAttributeValues()
    {
        return attributeValues;
    }

    public Collection<Relationship> getRelationship()
    {
        return relationship;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    public Map<Integer, Integer> getStatusMap()
    {
        return statusMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        patient = patientService.getPatient( patientId );

        identifiers = patient.getIdentifiers();

        attributeValues = patientAttributeValueService.getPatientAttributeValues( patient );

        relationship = relationshipService.getRelationshipsForPatient( patient );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( patient );

        activeProgramInstances = new HashSet<ProgramInstance>();
        
        completedProgramInstances = new HashSet<ProgramInstance>();
        
        for ( ProgramInstance programInstance : programInstances )
        {
            if( programInstance.isCompleted() )
            {
                completedProgramInstances.add( programInstance );
            }
            else
            {
                activeProgramInstances.add( programInstance );
            }
            statusMap.putAll( programStageInstanceService.statusProgramStageInstances( programInstance
                .getProgramStageInstances() ) );
        }

        return SUCCESS;
    }

}
