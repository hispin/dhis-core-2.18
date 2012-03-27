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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.comparator.PatientAttributeGroupSortOrderComparator;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.system.util.DateUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SaveProgramEnrollmentAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void setIdentifierTypeService( PatientIdentifierTypeService identifierTypeService )
    {
        this.identifierTypeService = identifierTypeService;
    }

    public void setProgramStageInstances( Collection<ProgramStageInstance> programStageInstances )
    {
        this.programStageInstances = programStageInstances;
    }

    private PatientIdentifierService patientIdentifierService;

    private PatientIdentifierTypeService identifierTypeService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeGroupService patientAttributeGroupService;

    private PatientAttributeValueService patientAttributeValueService;
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Collection<PatientIdentifierType> identifierTypes;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    private Map<Integer, String> identiferMap;

    private Integer patientId;

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private ProgramInstance programInstance;

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    private String enrollmentDate;

    public void setEnrollmentDate( String enrollmentDate )
    {
        this.enrollmentDate = enrollmentDate;
    }

    private String dateOfIncident;

    public void setDateOfIncident( String dateOfIncident )
    {
        this.dateOfIncident = dateOfIncident;
    }

    private Collection<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

    public Collection<ProgramStageInstance> getProgramStageInstances()
    {
        return programStageInstances;
    }

    private Collection<PatientAttribute> noGroupAttributes;

    public Collection<PatientAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }

    private List<PatientAttributeGroup> attributeGroups;

    public List<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        patient = patientService.getPatient( patientId );

        program = programService.getProgram( programId );

        if( dateOfIncident == null )
        {
            dateOfIncident = enrollmentDate;
        }
        
        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( patient, program,
            false );

        if ( programInstances.iterator().hasNext() )
        {
            programInstance = programInstances.iterator().next();
        }

        if ( programInstance == null )
        {
            programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( format.parseDate( enrollmentDate ) );
            programInstance.setDateOfIncident( format.parseDate( dateOfIncident ) );
            programInstance.setProgram( program );
            programInstance.setPatient( patient );
            programInstance.setCompleted( false );

            programInstanceService.addProgramInstance( programInstance );

            patient.getPrograms().add( program );
            patientService.updatePatient( patient );

            for ( ProgramStage programStage : program.getProgramStages() )
            {
                ProgramStageInstance programStageInstance = new ProgramStageInstance();
                programStageInstance.setProgramInstance( programInstance );
                programStageInstance.setProgramStage( programStage );
                programStageInstance.setStageInProgram( programStage.getStageInProgram() );

                Date dueDate = DateUtils.getDateAfterAddition( format.parseDate( dateOfIncident ), programStage
                    .getMinDaysFromStart() );

                programStageInstance.setDueDate( dueDate );

                programStageInstanceService.addProgramStageInstance( programStageInstance );

                programStageInstances.add( programStageInstance );
            }
        }

        else
        {
            programInstance.setEnrollmentDate( format.parseDate( enrollmentDate ) );
            programInstance.setDateOfIncident( format.parseDate( dateOfIncident ) );

            programInstanceService.updateProgramInstance( programInstance );

            for ( ProgramStageInstance programStageInstance : programInstance.getProgramStageInstances() )
            {
                Date dueDate = DateUtils.getDateAfterAddition( format.parseDate( dateOfIncident ), programStageInstance
                    .getProgramStage().getMinDaysFromStart() );

                programStageInstance.setDueDate( dueDate );

                programStageInstanceService.updateProgramStageInstance( programStageInstance );

                programStageInstances.add( programStageInstance );
            }
        }

     // ---------------------------------------------------------------------
        // Load identifier types of the selected program
        // ---------------------------------------------------------------------

        identifierTypes = identifierTypeService.getPatientIdentifierTypes( program );
        identiferMap = new HashMap<Integer, String>();

        if ( identifierTypes != null && identifierTypes.size() > 0 )
        {
            Collection<PatientIdentifier> patientIdentifiers = patientIdentifierService.getPatientIdentifiers(
                identifierTypes, patient );

            for ( PatientIdentifier identifier : patientIdentifiers )
            {
                identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
            }
        }

        // ---------------------------------------------------------------------
        // Load patient-attributes of the selected program
        // ---------------------------------------------------------------------

        attributeGroups = new ArrayList<PatientAttributeGroup>( patientAttributeGroupService
            .getPatientAttributeGroups( program ) );
        Collections.sort( attributeGroups, new PatientAttributeGroupSortOrderComparator() );

        noGroupAttributes = patientAttributeService.getPatientAttributes( program, null );

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
            .getPatientAttributeValues( patient );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute()
                .getValueType() ) )
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getPatientAttributeOption().getName() );
            }
            else
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getValue() );
            }
        }
        
        return SUCCESS;
    }
}
