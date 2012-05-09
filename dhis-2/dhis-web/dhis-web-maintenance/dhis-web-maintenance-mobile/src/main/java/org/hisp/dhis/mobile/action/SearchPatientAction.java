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

package org.hisp.dhis.mobile.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.mobile.caseentry.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private SelectedStateManager selectedStateManager;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientAttributeValueService patientAttributeValueService;

    @Autowired
    private PatientAttributeService patientAttributeService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private List<String> searchText = new ArrayList<String>();

    private Boolean listAll;

    private List<Integer> searchingAttributeId = new ArrayList<Integer>();

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

    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    public Map<Integer, String> getMapPatientOrgunit()
    {
        return mapPatientOrgunit;
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

    public void setSearchingAttributeId( List<Integer> searchingAttributeId )
    {
        this.searchingAttributeId = searchingAttributeId;
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
        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        // ---------------------------------------------------------------------
        // Get all of patients into the selected organisation unit
        // ---------------------------------------------------------------------

        if ( listAll != null && listAll )
        {
            listAllPatient( organisationUnit );

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Search patients by attributes
        // ---------------------------------------------------------------------

        for ( Integer attributeId : searchingAttributeId )
        {
            if ( attributeId != null && attributeId > 0 )
            {
                patientAttributes.add( patientAttributeService.getPatientAttribute( attributeId ) );
            }
        }

        searchPatientByAttributes( searchingAttributeId, searchText );

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

    private void searchPatientByAttributes( List<Integer> searchingAttributeId, List<String> searchText )
    {
        total = patientAttributeValueService.countSearchPatients( searchingAttributeId, searchText );

        this.paging = createPaging( total );

        patients = patientAttributeValueService.searchPatients( searchingAttributeId, searchText, paging.getStartPos(),
            paging.getPageSize() );

        Collection<PatientAttributeValue> attributeValues = patientAttributeValueService
            .getPatientAttributeValues( patients );

        for ( Patient patient : patients )
        {
            mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );

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
