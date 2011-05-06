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
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SearchPatientAction
    extends ActionPagingSupport<Patient>
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -2815128850665795197L;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    private SelectedStateManager selectedStateManager;

    private PatientService patientService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeValueService patientAttributeValueService;

    private ProgramService programService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String searchText;

    private Boolean listAll;

    private Integer searchingAttributeId;

    private Integer sortPatientAttributeId;

    private Integer programId;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Integer total;

    private Collection<Patient> patients = new ArrayList<Patient>();

    private Map<Integer, String> mapPatientPatientAttr = new HashMap<Integer, String>();

    private Map<Integer, String> mapPatientOrgunit = new HashMap<Integer, String>();

    private PatientAttribute sortingPatientAttribute = null;

    private PatientAttribute searchingPatientAttribute = null;
 
    private Program program;
    
    // -------------------------------------------------------------------------
    // Getters/Setters
    // -------------------------------------------------------------------------

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public Map<Integer, String> getMapPatientOrgunit()
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

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public void setSearchingAttributeId( Integer searchingAttributeId )
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

    public Map<Integer, String> getMapPatientPatientAttr()
    {
        return mapPatientPatientAttr;
    }

    public void setMapPatientPatientAttr( Map<Integer, String> mapPatientPatientAttr )
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
        
        setParamsToSearch();      

        // ---------------------------------------------------------------------
        // Get all of patient into the selected organisation unit
        // ---------------------------------------------------------------------
        
        if ( listAll != null && listAll )
        {
            selectedStateManager.clearSearchingAttributeId();
            selectedStateManager.clearSortingAttributeId();
            selectedStateManager.clearSearchText();
            selectedStateManager.clearSelectedProgram();
            selectedStateManager.setListAll( listAll );

            listAllPatient( organisationUnit, sortingPatientAttribute );

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Get patients by the selected program
        // ---------------------------------------------------------------------
        
        if ( searchingAttributeId != null && searchingAttributeId == 0 && programId != null )
        {
            program = programService.getProgram( programId );

            if ( sortPatientAttributeId != null )
            {
                selectedStateManager.setSortingAttributeId( sortPatientAttributeId );
            }
            else
            {
                selectedStateManager.clearSortingAttributeId();
            }
            
            selectedStateManager.setSelectedProgram( program );
            selectedStateManager.setSearchingAttributeId( searchingAttributeId );
            
            searchPatientByProgram( organisationUnit, program, sortingPatientAttribute );

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

            if ( sortPatientAttributeId != null )
            {
                selectedStateManager.setSortingAttributeId( sortPatientAttributeId );
            }
            else
            {
                selectedStateManager.clearSortingAttributeId();
            }
            
            if ( programId != null )
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

            if ( sortPatientAttributeId != null )
            {
                selectedStateManager.setSortingAttributeId( sortPatientAttributeId );
            }
            else
            {
                selectedStateManager.clearSortingAttributeId();
            }

            if ( programId != null )
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
        searchingAttributeId = selectedStateManager.getSearchingAttributeId();
        sortPatientAttributeId = selectedStateManager.getSortAttributeId();
        searchText = selectedStateManager.getSearchText();
        program = selectedStateManager.getSelectedProgram();

        setParamsToSearch();

        if ( listAll )
        {
            listAllPatient( organisationUnit, sortingPatientAttribute );
            return SUCCESS;
        }

        if ( searchingAttributeId != null && searchingAttributeId == 0 && program != null )
        {
            searchPatientByProgram( organisationUnit, program, sortingPatientAttribute );
            return SUCCESS;
        }

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
    // Supporting methods
    // -------------------------------------------------------------------------

    private void setParamsToSearch()
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
        total = patientService.countGetPatientsByOrgUnit( organisationUnit );
        this.paging = createPaging( total );
        
        patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, paging.getStartPos(), paging
            .getPageSize() ) );

        if ( patients != null && patients.size() > 0 )
        {
            for ( Patient patient : patients )
            {
                if ( sortingPatientAttribute != null )
                {
                    PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue(
                        patient, sortingPatientAttribute );
                    String value = (attributeValue == null) ? "" : attributeValue.getValue();

                    mapPatientPatientAttr.put( patient.getId(), value );
                }
            }
        }
    }

    private void searchPatientByProgram( OrganisationUnit organisationUnit, Program program,
        PatientAttribute sortingPatientAttribute )
    {
        total = patientService.countGetPatientsByOrgUnitProgram( organisationUnit, program );
        this.paging = createPaging( total );

        patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, program, paging.getStartPos(),
            paging.getPageSize() ) );

        if ( patients != null && patients.size() > 0 )
        {
            for ( Patient patient : patients )
            {
                // mapRelationShip.put( patient.getId(), relationshipService.getRelationshipsForPatient( patient ) );

                if ( sortingPatientAttribute != null )
                {
                    PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue(
                        patient, sortingPatientAttribute );
                    String value = (attributeValue == null) ? "" : attributeValue.getValue();

                    mapPatientPatientAttr.put( patient.getId(), value );
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
                // -------------------------------------------------------------
                // Get hierarchy organisation unit
                // -------------------------------------------------------------

                mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );

                // -------------------------------------------------------------
                // Sort patients
                // -------------------------------------------------------------

                if ( sortingPatientAttribute != null )
                {
                    PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue(
                        patient, sortingPatientAttribute );
                    String value = (attributeValue == null) ? "" : attributeValue.getValue();

                    mapPatientPatientAttr.put( patient.getId(), value );
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

                mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );

                // -------------------------------------------------------------
                // Sort patients
                // -------------------------------------------------------------

                if ( sortingPatientAttribute != null )
                {
                    PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue(
                        patient, sortingPatientAttribute );
                    String value = (attributeValue == null) ? "" : attributeValue.getValue();

                    mapPatientPatientAttr.put( patient.getId(), value );
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
