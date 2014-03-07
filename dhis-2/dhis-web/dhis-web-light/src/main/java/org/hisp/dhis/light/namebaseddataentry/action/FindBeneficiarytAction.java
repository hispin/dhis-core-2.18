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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;

import com.opensymphony.xwork2.Action;

public class FindBeneficiarytAction
    implements Action
{
    private static final String REDIRECT = "redirect";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityInstanceService patientService;

    public void setPatientService( TrackedEntityInstanceService patientService )
    {
        this.patientService = patientService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Collection<TrackedEntityInstance> patients;

    public Collection<TrackedEntityInstance> getPatients()
    {
        return patients;
    }

    public void setPatients( Collection<TrackedEntityInstance> patients )
    {
        this.patients = patients;
    }

    private Set<TrackedEntityAttributeValue> pavSet;

    public Set<TrackedEntityAttributeValue> getPavSet()
    {
        return pavSet;
    }

    public void setPavSet( Set<TrackedEntityAttributeValue> pavSet )
    {
        this.pavSet = pavSet;
    }

    private Set<TrackedEntityAttributeValue> patientAttributes;

    public Set<TrackedEntityAttributeValue> getPatientAttributes()
    {
        return patientAttributes;
    }

    public void setPatientAttributes( Set<TrackedEntityAttributeValue> patientAttributes )
    {
        this.patientAttributes = patientAttributes;
    }

    private String keyword;

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword( String keyword )
    {
        this.keyword = keyword;
    }

    private Integer organisationUnitId;

    public Integer getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private Integer patientAttributeId;

    public Integer getPatientAttributeId()
    {
        return patientAttributeId;
    }

    public void setPatientAttributeId( Integer patientAttributeId )
    {
        this.patientAttributeId = patientAttributeId;
    }

    private Integer patientId;

    public Integer getPatientId()
    {
        return patientId;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    // Use in search related patient

    private Integer originalPatientId;

    public void setOriginalPatientId( Integer originalPatientId )
    {
        this.originalPatientId = originalPatientId;
    }

    public Integer getOriginalPatientId()
    {
        return originalPatientId;
    }

    private Integer relationshipTypeId;

    public Integer getRelationshipTypeId()
    {
        return relationshipTypeId;
    }

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }

    @Override
    public String execute()
        throws Exception
    {

        patients = patientService.searchTrackedEntityInstancesForMobile( keyword, organisationUnitId,
            patientAttributeId );

        pavSet = new HashSet<TrackedEntityAttributeValue>();

        for ( TrackedEntityInstance p : patients )
        {
            pavSet.addAll( p.getAttributeValues() );
        }

        if ( patients.size() == 1 )
        {
            TrackedEntityInstance patient = patients.iterator().next();
            patientId = patient.getId();

            return REDIRECT;
        }
        return SUCCESS;
    }

}
