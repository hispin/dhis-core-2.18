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

package org.hisp.dhis.caseentry.action.caseentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.caseentry.screen.DataEntryScreenManager;
import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;

import com.opensymphony.xwork2.Action;

public class ProgramStageCustomDataEntryAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private DataEntryScreenManager dataEntryScreenManager;

    public void setDataEntryScreenManager( DataEntryScreenManager dataEntryScreenManager )
    {
        this.dataEntryScreenManager = dataEntryScreenManager;
    }

//    private MinMaxDataElementService minMaxDataElementService;
//
//    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
//    {
//        this.minMaxDataElementService = minMaxDataElementService;
//    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String customDataEntryFormCode = null;

    public String getCustomDataEntryFormCode()
    {
        return this.customDataEntryFormCode;
    }

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    private Integer programStageInstanceId;

    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    private PatientIdentifier patientIdentifier;

    public PatientIdentifier getPatientIdentifier()
    {
        return patientIdentifier;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private ProgramStage programStage;

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    private Collection<DataElement> dataElements = new ArrayList<DataElement>();

    public Collection<DataElement> getDataElements()
    {
        return dataElements;
    }

    private Map<Integer, Collection<DataElementCategoryOptionCombo>> optionMap = new HashMap<Integer, Collection<DataElementCategoryOptionCombo>>();

    public Map<Integer, Collection<DataElementCategoryOptionCombo>> getOptionMap()
    {
        return optionMap;
    }

    private Map<Integer, PatientDataValue> patientDataValueMap;

    public Map<Integer, PatientDataValue> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private DataEntryForm dataEntryForm;

    public DataEntryForm getDataEntryForm()
    {
        return this.dataEntryForm;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private boolean customDataEntryFormExists;

    public boolean isCustomDataEntryFormExists()
    {
        return customDataEntryFormExists;
    }

    public void setCustomDataEntryFormExists( boolean customDataEntryFormExists )
    {
        this.customDataEntryFormExists = customDataEntryFormExists;
    }

    private String useDefaultForm;

    public String getUseDefaultForm()
    {
        return useDefaultForm;
    }

    public void setUseDefaultForm( String useDefaultForm )
    {
        this.useDefaultForm = useDefaultForm;
    }
    
    public ProgramStageInstance programStageInstance;
    
    public int getProgramStageInstanceId()
    {
           return programStageInstance.getId();
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
//        // ---------------------------------------------------------------------
//        // Get the min/max values
//        // ---------------------------------------------------------------------
//
//        Collection<MinMaxDataElement> minMaxDataElements = minMaxDataElementService.getMinMaxDataElements(
//            organisationUnit, dataElements );
//
//        Map<Integer, MinMaxDataElement> minMaxMap = new HashMap<Integer, MinMaxDataElement>( minMaxDataElements.size() );
//
//        for ( MinMaxDataElement minMaxDataElement : minMaxDataElements )
//        {
//            minMaxMap.put( minMaxDataElement.getDataElement().getId(), minMaxDataElement );
//        }

        // ---------------------------------------------------------------------
        // Get Orgunit & Program, ProgramStage 
        // ---------------------------------------------------------------------
        
        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
        
        programStage = programStageInstance.getProgramStage();
        
        program = programStage.getProgram();
        
        patient = programStageInstance.getProgramInstance().getPatient();

        patientIdentifier = patientIdentifierService.getPatientIdentifier( patient );
        
        selectedStateManager.setSelectedPatient( patient );
        
        selectedStateManager.setSelectedProgram( program );

        selectedStateManager.setSelectedProgramStage( programStage );


        Collection<PatientDataValue> patientDataValues = patientDataValueService.getPatientDataValues(
             programStageInstance );

        DataEntryForm dataEntryForm = programStage.getDataEntryForm();
        if ( dataEntryForm == null )
        {
            return SUCCESS;
        }

        boolean cdeFormExists = (dataEntryForm != null);

        String disabled = "";
        if ( cdeFormExists )
        {
            customDataEntryFormExists = true;
            
            customDataEntryFormCode = dataEntryScreenManager.populateCustomDataEntryScreenForMultiDimensional(
                dataEntryForm.getHtmlCode(), patientDataValues, disabled,
                i18n, programStage, programStageInstance, organisationUnit );
        }
        
        return SUCCESS;
    }

}
