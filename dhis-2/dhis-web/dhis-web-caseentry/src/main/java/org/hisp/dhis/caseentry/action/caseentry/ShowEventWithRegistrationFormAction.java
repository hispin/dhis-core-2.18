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

package org.hisp.dhis.caseentry.action.caseentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 */
public class ShowEventWithRegistrationFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programId;

    private Collection<PatientAttribute> noGroupAttributes = new HashSet<PatientAttribute>();

    private Map<PatientAttributeGroup, Collection<PatientAttribute>> attributeGroupsMap = new HashMap<PatientAttributeGroup, Collection<PatientAttribute>>();

    private Collection<PatientIdentifierType> identifierTypes;

    private OrganisationUnit organisationUnit;

    private String customDataEntryFormCode;

    private List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>();

    private ProgramStage programStage;

    private Collection<User> healthWorkers;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        Collection<PatientAttribute> patientAttributes = patientAttributeService.getAllPatientAttributes();
        Collection<Program> programs = programService.getAllPrograms();
        for ( Program program : programs )
        {
            identifierTypes.removeAll( program.getPatientIdentifierTypes() );
            patientAttributes.removeAll( program.getPatientAttributes() );
        }

        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            PatientAttributeGroup attributeGroup = patientAttribute.getPatientAttributeGroup();
            if ( attributeGroup != null )
            {
                if ( attributeGroupsMap.containsKey( attributeGroup ) )
                {
                    Collection<PatientAttribute> attributes = attributeGroupsMap.get( attributeGroup );
                    attributes.add( patientAttribute );
                }
                else
                {
                    Collection<PatientAttribute> attributes = new HashSet<PatientAttribute>();
                    attributes.add( patientAttribute );
                    attributeGroupsMap.put( attributeGroup, attributes );
                }
            }
            else
            {
                noGroupAttributes.add( patientAttribute );
            }
        }

        organisationUnit = selectionManager.getSelectedOrganisationUnit();

        // Get data entry form

        Program program = programService.getProgram( programId );

        programStage = program.getProgramStages().iterator().next();

        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        // Get health workers
        
        healthWorkers = organisationUnit.getUsers();
       
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Getter/Setter
    // -------------------------------------------------------------------------

    public Collection<User> getHealthWorkers()
    {
        return healthWorkers;
    }

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    public Map<PatientAttributeGroup, Collection<PatientAttribute>> getAttributeGroupsMap()
    {
        return attributeGroupsMap;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public Collection<PatientAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public String getCustomDataEntryFormCode()
    {
        return customDataEntryFormCode;
    }

    public List<ProgramStageDataElement> getProgramStageDataElements()
    {
        return programStageDataElements;
    }
}
