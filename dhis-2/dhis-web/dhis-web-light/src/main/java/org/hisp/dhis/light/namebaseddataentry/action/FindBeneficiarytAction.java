package org.hisp.dhis.light.namebaseddataentry.action;

/*
 * Copyright (c) 2004-2014, University of Oslo
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


import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.OrganisationUnitSelectionMode;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.common.QueryOperator;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams;
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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private TrackedEntityAttributeService trackedEntityAttributeService;

    public void setTrackedEntityAttributeService( TrackedEntityAttributeService trackedEntityAttributeService )
    {
        this.trackedEntityAttributeService = trackedEntityAttributeService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<List<Object>> trackedEntityList;

    public List<List<Object>> getTrackedEntityList()
    {
        return trackedEntityList;
    }

    public void setTrackedEntityList( List<List<Object>> trackedEntityList )
    {
        this.trackedEntityList = trackedEntityList;
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

    private String patientUID;

    public String getPatientUID()
    {
        return patientUID;
    }

    public void setPatientUID( String patientUID )
    {
        this.patientUID = patientUID;
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
        TrackedEntityInstanceQueryParams param = new TrackedEntityInstanceQueryParams();
        QueryItem queryItem = new QueryItem(
            trackedEntityAttributeService.getTrackedEntityAttribute( patientAttributeId ), QueryOperator.EQ, keyword,
            false );

        if ( organisationUnitId == null || organisationUnitId == 0 )
        {
            param.setOrganisationUnitMode( OrganisationUnitSelectionMode.ALL );
        }
        else
        {
            param.addOrganisationUnit( organisationUnitService.getOrganisationUnit( organisationUnitId ) );
        }

        param.addAttribute( queryItem );

        Grid trackedEntityGrid = patientService.getTrackedEntityInstances( param );
        trackedEntityList = trackedEntityGrid.getRows();


        if ( trackedEntityList.size() == 1 )
        {
            List<Object> firstRow = trackedEntityList.iterator().next();
            patientUID = firstRow.get( 0 ).toString();

            return REDIRECT;
        }
        return SUCCESS;
    }

}
