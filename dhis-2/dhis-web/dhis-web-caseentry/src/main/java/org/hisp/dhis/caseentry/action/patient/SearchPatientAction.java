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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SearchPatientAction
    extends ActionPagingSupport<Patient>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    private PatientService patientService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientAttributeService patientAttributeService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private List<String> searchText = new ArrayList<String>();

    private Boolean listAll;

    private List<Integer> searchObjectId = new ArrayList<Integer>();

    private Boolean searchBySelectedOrgunit;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Integer total;

    private Collection<Patient> patients = new ArrayList<Patient>();

    private Map<String, String> mapPatientPatientAttr = new HashMap<String, String>();

    private Map<Integer, String> mapPatientOrgunit = new HashMap<Integer, String>();

    private List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();

    // -------------------------------------------------------------------------
    // Getters/Setters
    // -------------------------------------------------------------------------

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    public Map<Integer, String> getMapPatientOrgunit()
    {
        return mapPatientOrgunit;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setSearchBySelectedOrgunit( Boolean searchBySelectedOrgunit )
    {
        this.searchBySelectedOrgunit = searchBySelectedOrgunit;
    }

    public void setSearchText( List<String> searchText )
    {
        this.searchText = searchText;
    }

    public void setListAll( Boolean listAll )
    {
        this.listAll = listAll;
    }

    public Boolean getListAll()
    {
        return listAll;
    }

    public void setSearchObjectId( List<Integer> searchObjectId )
    {
        this.searchObjectId = searchObjectId;
    }

    public Collection<Patient> getPatients()
    {
        return patients;
    }

    public Integer getTotal()
    {
        return total;
    }

    public Map<String, String> getMapPatientPatientAttr()
    {
        return mapPatientPatientAttr;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit selectOrgunit = selectedStateManager.getSelectedOrganisationUnit();

        // ---------------------------------------------------------------------
        // Get all of patients into the selected organisation unit
        // ---------------------------------------------------------------------

        if ( listAll != null && listAll )
        {
            listAllPatient( selectOrgunit );

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Search patients by attributes
        // ---------------------------------------------------------------------

        for ( Integer attributeId : searchObjectId )
        {
            if ( attributeId != null && attributeId > 0 )
            {
                patientAttributes.add( patientAttributeService.getPatientAttribute( attributeId ) );
            }
        }
        searchBySelectedOrgunit = (searchBySelectedOrgunit == null) ? false : searchBySelectedOrgunit;
        searchPatientByAttributes( searchObjectId, searchText, selectOrgunit, searchBySelectedOrgunit );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    private void listAllPatient( OrganisationUnit organisationUnit )
    {
        total = patientService.countGetPatientsByOrgUnit( organisationUnit );
        this.paging = createPaging( total );

        patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, paging.getStartPos(), paging
            .getPageSize() ) );
    }

    private void searchPatientByAttributes( List<Integer> searchingAttributeId, List<String> searchText,
        OrganisationUnit orgunit, Boolean searchBySelectedOrgunit )
    {
        if ( searchBySelectedOrgunit )
        {
            total = patientAttributeValueService.countSearchPatients( searchingAttributeId, searchText, orgunit );
            this.paging = createPaging( total );
            patients = patientAttributeValueService.searchPatients( searchingAttributeId, searchText, orgunit, paging
                .getStartPos(), paging.getPageSize() );
        }
        else
        {
            total = patientAttributeValueService.countSearchPatients( searchingAttributeId, searchText );
            this.paging = createPaging( total );
            patients = patientAttributeValueService.searchPatients( searchingAttributeId, searchText, paging
                .getStartPos(), paging.getPageSize() );
        }

        Collection<PatientAttributeValue> attributeValues = patientAttributeValueService
            .getPatientAttributeValues( patients );

        for ( Patient patient : patients )
        {
            if ( !searchBySelectedOrgunit )
            {
                mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );
            }

            for ( PatientAttributeValue attributeValue : attributeValues )
            {
                mapPatientPatientAttr.put( patient.getId() + "-" + attributeValue.getPatientAttribute().getId(),
                    attributeValue.getValue() );
            }
        }
    }

    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " / " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
}
