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

package org.hisp.dhis.patient.action.patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.state.SelectedStateManager;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;

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

    private OrganisationUnitSelectionManager selectionManager;

    private SelectedStateManager selectedStateManager;

    private PatientService patientService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeValueService patientAttributeValueService;

    private RelationshipService relationshipService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String searchText;

    private Boolean listAll;

    private Integer searchingAttributeId;

    private Integer sortPatientAttributeId;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Integer total;

    private Collection<PatientAttribute> patientAttributes;

    private Collection<Patient> patients = new ArrayList<Patient>();

    private Map<Integer, Collection<Relationship>> mapRelationShip = new HashMap<Integer, Collection<Relationship>>();

    private Map<Patient, String> mapPatientPatientAttr = new HashMap<Patient, String>();

    private Map<Patient, String> mapPatientOrgunit = new HashMap<Patient, String>();

    private PatientAttribute sortingPatientAttribute = null;

    private PatientAttribute searchingPatientAttribute = null;

    // -------------------------------------------------------------------------
    // Getters/Setters
    // -------------------------------------------------------------------------

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public Map<Patient, String> getMapPatientOrgunit()
    {
        return mapPatientOrgunit;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    public void setSearchText( String searchText )
    {
        this.searchText = searchText;
    }

    public String getSearchText()
    {
        return searchText;
    }

    public void setListAll( Boolean listAll )
    {
        this.listAll = listAll;
    }

    public Integer getSearchingAttributeId()
    {
        return searchingAttributeId;
    }

    public void setSearchingAttributeId( Integer searchingAttributeId )
    {
        this.searchingAttributeId = searchingAttributeId;
    }

    public Collection<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    public Collection<Patient> getPatients()
    {
        return patients;
    }

    public Integer getTotal()
    {
        return total;
    }

    public Map<Integer, Collection<Relationship>> getMapRelationShip()
    {
        return mapRelationShip;
    }

    public Integer getSortPatientAttributeId()
    {
        return sortPatientAttributeId;
    }

    public void setSortPatientAttributeId( Integer sortPatientAttributeId )
    {
        this.sortPatientAttributeId = sortPatientAttributeId;
    }

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    public Map<Patient, String> getMapPatientPatientAttr()
    {
        return mapPatientPatientAttr;
    }

    public void setMapPatientPatientAttr( Map<Patient, String> mapPatientPatientAttr )
    {
        this.mapPatientPatientAttr = mapPatientPatientAttr;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();

        // ---------------------------------------------------------------------
        // Get all of Patient-Attributes
        // ---------------------------------------------------------------------

        patientAttributes = patientAttributeService.getAllPatientAttributes();

        getParamsToSearch();

        // ---------------------------------------------------------------------
        // Get all of patient into the selected organisation unit
        // ---------------------------------------------------------------------

        if ( listAll != null && listAll )
        {
            selectedStateManager.clearSearchingAttributeId();
            selectedStateManager.clearSortingAttributeId();
            selectedStateManager.clearSearchText();
            selectedStateManager.setListAll( listAll );

            searchText = "list_all_patients";

            listAllPatient( organisationUnit, sortingPatientAttribute );

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Get patients by searchingAttributeId and searchText
        // and sort result by sortingAttributeId
        // ---------------------------------------------------------------------
        
        if ( searchingPatientAttribute != null && searchText != null )
        {
            selectedStateManager.clearListAll();
            selectedStateManager.setSearchingAttributeId( searchingAttributeId );
            
            if( sortPatientAttributeId != null )
            {
                selectedStateManager.setSortingAttributeId( sortPatientAttributeId );
            }else
            {
                selectedStateManager.clearSortingAttributeId();
            }
            
            selectedStateManager.setSearchText( searchText );

            searchPatientByAttribute( searchingPatientAttribute, searchText, sortingPatientAttribute );

            return SUCCESS;
        }

        if ( searchingPatientAttribute == null && searchText != null )
        {
            selectedStateManager.clearListAll();
            
            selectedStateManager.clearSearchingAttributeId();
            
            if( sortPatientAttributeId != null )
            {
                selectedStateManager.setSortingAttributeId( sortPatientAttributeId );
            }else
            {
                selectedStateManager.clearSortingAttributeId();
            }
            
           
            selectedStateManager.setSearchText( searchText );
            
            searchPatientByAttribute( searchText, sortingPatientAttribute );
            
            return SUCCESS;
        }
        
        // ---------------------------------------------------------------------
        // Search patients by values into section
        // ---------------------------------------------------------------------

        listAll = selectedStateManager.getListAll();

        if ( listAll )
        {
        	listAllPatient( organisationUnit, sortingPatientAttribute );

            searchText = "list_all_patients";

            return SUCCESS;

        }

        searchingAttributeId = selectedStateManager.getSearchingAttributeId();
        sortPatientAttributeId = selectedStateManager.getSortAttributeId();
        searchText = selectedStateManager.getSearchText();
        
        getParamsToSearch();

        if ( searchingAttributeId != null && searchText != null )
        {
            searchPatientByAttribute( searchText, sortingPatientAttribute );

            return SUCCESS;
        }

        if ( searchingAttributeId == null && searchText != null )
        {
            searchPatientByAttribute( searchText, sortingPatientAttribute );
            return SUCCESS;
        }
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    private void getParamsToSearch()
    {
        // ---------------------------------------------------------------------
        // Get sorting patient-attribute
        // ---------------------------------------------------------------------

        if ( sortPatientAttributeId != null )
        {
            sortingPatientAttribute = patientAttributeService.getPatientAttribute( sortPatientAttributeId );
        }

        // ---------------------------------------------------------------------
        // Get and searching patient-attribute
        // ---------------------------------------------------------------------

        if ( searchingAttributeId != null )
        {
            searchingPatientAttribute = patientAttributeService.getPatientAttribute( searchingAttributeId );
        }
    }

    private void listAllPatient( OrganisationUnit organisationUnit, PatientAttribute sortingPatientAttribute )
    {
        // Get patients by the selected organisation - unit

        total = patientService.countGetPatientsByOrgUnit( organisationUnit );

        this.paging = createPaging( total );

        patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, paging.getStartPos(), paging
            .getPageSize() ) );

        if ( patients != null && patients.size() > 0 )
        {
            for ( Patient patient : patients )
            {
                mapRelationShip.put( patient.getId(), relationshipService.getRelationshipsForPatient( patient ) );

                // Get patient-attribute-values
                if ( sortingPatientAttribute != null )
                {
                    PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue(
                        patient, sortingPatientAttribute );
                    String value = (attributeValue == null) ? "" : attributeValue.getValue();

                    mapPatientPatientAttr.put( patient, value );
                }
            }
        }
    }

    private void searchPatientByAttribute( PatientAttribute searchingPatientAttribute, String searchText,
        PatientAttribute sortingPatientAttribute )
    {
        total = patientAttributeValueService.countSearchPatientAttributeValue( searchingPatientAttribute, searchText );
        this.paging = createPaging( total );

        patients = patientAttributeValueService.searchPatients( searchingPatientAttribute, searchText, paging
            .getStartPos(), paging.getPageSize() );

        if ( patients != null && patients.size() > 0 )
        {
            if ( sortingPatientAttribute != null )
            {
                patients = patientService.sortPatientsByAttribute( patients, sortingPatientAttribute );
            }

            for ( Patient patient : patients )
            {
                mapRelationShip.put( patient.getId(), relationshipService.getRelationshipsForPatient( patient ) );

                // -------------------------------------------------------------
                // Get hierarchy organisation unit
                // -------------------------------------------------------------

                mapPatientOrgunit.put( patient, getHierarchyOrgunit( patient.getOrganisationUnit() ) );

                // -------------------------------------------------------------
                // Sort patients
                // -------------------------------------------------------------

                if ( sortingPatientAttribute != null )
                {
                    PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue(
                        patient, sortingPatientAttribute );
                    String value = (attributeValue == null) ? "" : attributeValue.getValue();

                    mapPatientPatientAttr.put( patient, value );
                }
            }
        }

    }

    private void searchPatientByAttribute( String searchText, PatientAttribute sortingPatientAttribute )
    {
        total = patientService.countGetPatients( searchText );

        this.paging = createPaging( total );
        patients = patientService.getPatients( searchText, paging.getStartPos(), paging.getPageSize() );

        if ( patients != null && patients.size() > 0 )
        {
            if ( sortingPatientAttribute != null )
            {
                patients = patientService.sortPatientsByAttribute( patients, sortingPatientAttribute );
            }
            for ( Patient patient : patients )
            {
                // -------------------------------------------------------------
                // Get hierarchy organisation unit
                // -------------------------------------------------------------

                mapPatientOrgunit.put( patient, getHierarchyOrgunit( patient.getOrganisationUnit() ) );

                // -------------------------------------------------------------
                // Sort patients
                // -------------------------------------------------------------

                if ( sortingPatientAttribute != null )
                {
                    PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue(
                        patient, sortingPatientAttribute );
                    String value = (attributeValue == null) ? "" : attributeValue.getValue();

                    mapPatientPatientAttr.put( patient, value );
                }
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
