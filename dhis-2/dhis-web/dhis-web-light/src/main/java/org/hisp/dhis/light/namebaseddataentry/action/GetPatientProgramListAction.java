package org.hisp.dhis.light.namebaseddataentry.action;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.light.utils.NamebasedUtils;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

public class GetPatientProgramListAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private TrackedEntityAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( TrackedEntityAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private TrackedEntityInstanceService patientService;

    public void setPatientService( TrackedEntityInstanceService patientService )
    {
        this.patientService = patientService;
    }

    private NamebasedUtils util;

    public NamebasedUtils getUtil()
    {
        return util;
    }

    public void setUtil( NamebasedUtils util )
    {
        this.util = util;
    }

    private RelationshipService relationshipService;

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    public RelationshipTypeService relationshipTypeService;

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer patientId;

    public Integer getPatientId()
    {
        return patientId;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    private Set<ProgramInstance> programInstances = new HashSet<ProgramInstance>();

    public Set<ProgramInstance> getProgramInstances()
    {
        return programInstances;
    }

    public void setProgramInstances( Set<ProgramInstance> programInstances )
    {
        this.programInstances = programInstances;
    }

    private TrackedEntityInstance patient;

    public TrackedEntityInstance getPatient()
    {
        return patient;
    }

    public void setPatient( TrackedEntityInstance patient )
    {
        this.patient = patient;
    }

    private List<Program> enrollmentProgramList;

    public List<Program> getEnrollmentProgramList()
    {
        return enrollmentProgramList;
    }

    public void setEnrollmentProgramList( List<Program> enrollmentProgramList )
    {
        this.enrollmentProgramList = enrollmentProgramList;
    }

    private Map<Relationship, TrackedEntityInstance> relatedPeople;

    public Map<Relationship, TrackedEntityInstance> getRelatedPeople()
    {
        return relatedPeople;
    }

    public void setRelatedPeople( Map<Relationship, TrackedEntityInstance> relatedPeople )
    {
        this.relatedPeople = relatedPeople;
    }

    private Collection<RelationshipType> relationshipTypes;

    public Collection<RelationshipType> getRelationshipTypes()
    {
        return relationshipTypes;
    }

    public void setRelationshipTypes( Collection<RelationshipType> relationshipTypes )
    {
        this.relationshipTypes = relationshipTypes;
    }

    private Boolean validated;

    public Boolean getValidated()
    {
        return validated;
    }

    public void setValidated( Boolean validated )
    {
        this.validated = validated;
    }

    private Collection<TrackedEntityAttributeValue> patientAttributeValues;

    public void setPatientAttributeValues( Collection<TrackedEntityAttributeValue> patientAttributeValues )
    {
        this.patientAttributeValues = patientAttributeValues;
    }

    public Collection<TrackedEntityAttributeValue> getPatientAttributeValues()
    {
        return patientAttributeValues;
    }

    private List<ProgramInstance> listOfCompletedProgram;

    public List<ProgramInstance> getListOfCompletedProgram()
    {
        return listOfCompletedProgram;
    }

    private User user;

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }

    @Override
    public String execute()
        throws Exception
    {
        user = currentUserService.getCurrentUser();
        programInstances.clear();
        relatedPeople = new HashMap<Relationship, TrackedEntityInstance>();

        patient = patientService.getTrackedEntityInstance( patientId );
        Collection<Program> programByCurrentUser = programService.getProgramsByCurrentUser();
        for ( ProgramInstance programInstance : patient.getProgramInstances() )
        {
            if ( programInstance.getStatus() == ProgramInstance.STATUS_ACTIVE
                && programByCurrentUser.contains( programInstance.getProgram() ) )
            {
                programInstances.add( programInstance );
            }
        }

        enrollmentProgramList = this.generateEnrollmentProgramList();
        Collection<Relationship> relationships = relationshipService.getRelationshipsForTrackedEntityInstance( patient );

        for ( Relationship relationship : relationships )
        {
            if ( relationship.getEntityInstanceA().getId() != patient.getId() )
            {
                relatedPeople.put( relationship, relationship.getEntityInstanceA() );
            }

            if ( relationship.getEntityInstanceB().getId() != patient.getId() )
            {
                relatedPeople.put( relationship, relationship.getEntityInstanceB() );
            }
        }

        relationshipTypes = relationshipTypeService.getAllRelationshipTypes();

        patientAttributeValues = patientAttributeValueService.getTrackedEntityAttributeValues( patient );

        Collection<ProgramInstance> listOfProgramInstance = patient.getProgramInstances();

        this.listOfCompletedProgram = new ArrayList<ProgramInstance>();

        for ( ProgramInstance each : listOfProgramInstance )
        {
            if ( each.getStatus() == ProgramInstance.STATUS_COMPLETED )
            {
                this.listOfCompletedProgram.add( each );
            }
        }

        return SUCCESS;
    }

    private List<Program> generateEnrollmentProgramList()
    {
        List<Program> programs = new ArrayList<Program>();
        for ( Program program : programService.getProgramsByCurrentUser() )

        {
            if ( program.isSingleEvent() && program.isRegistration() )
            {
                if ( programInstanceService.getProgramInstances( patient, program ).size() == 0 )
                {
                    programs.add( program );
                }
            }
            else if ( !program.isSingleEvent() )
            {
                if ( programInstanceService.getProgramInstances( patient, program, ProgramInstance.STATUS_ACTIVE )
                    .size() == 0 )
                {
                    programs.add( program );
                }
                else if ( programInstanceService.getProgramInstances( patient, program ).size() > 0
                    && !program.getOnlyEnrollOnce()
                    && programInstanceService.getProgramInstances( patient, program, ProgramInstance.STATUS_ACTIVE )
                        .size() == 0 )
                {
                    programs.add( program );
                }
            }
        }
        return programs;
    }
}
