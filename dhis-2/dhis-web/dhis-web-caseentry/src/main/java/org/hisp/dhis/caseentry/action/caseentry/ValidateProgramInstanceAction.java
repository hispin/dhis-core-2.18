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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramValidation;
import org.hisp.dhis.program.ProgramValidationService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ ValidateProgramInstanceAction.java Apr 28, 2011 10:56:10 AM $
 */
public class ValidateProgramInstanceAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    private ProgramStageInstanceService programStageInstanceService;

    private PatientDataValueService patientDataValueService;

    private ProgramValidationService programValidationService;
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private I18n i18n;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<DataElement, String> resultDEMultiStages;

    private List<ProgramValidation> programValidations;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }
    
    public List<ProgramValidation> getProgramValidations()
    {
        return programValidations;
    }

    public void setProgramValidationService( ProgramValidationService programValidationService )
    {
        this.programValidationService = programValidationService;
    }

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    public Map<DataElement, String> getResultDEMultiStages()
    {
        return resultDEMultiStages;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public SelectedStateManager getSelectedStateManager()
    {
        return selectedStateManager;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        resultDEMultiStages = new HashMap<DataElement, String>();

        programValidations = new ArrayList<ProgramValidation>();

        // ---------------------------------------------------------------------
        // Get selected objects
        // ---------------------------------------------------------------------

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit( );

        ProgramStageInstance programStageInstance = selectedStateManager.getSelectedProgramStageInstance();

        ProgramStage programStage = programStageInstance.getProgramStage();
        
        // ---------------------------------------------------------------------
        // Get selected objects
        // ---------------------------------------------------------------------

        Set<ProgramStageDataElement> dataElements = programStage.getProgramStageDataElements();

        for ( ProgramStageDataElement psDataElement : dataElements )
        {
            DataElement dataElement = psDataElement.getDataElement();

            checkDataElementInMultiStage( programStageInstance, organisationUnit, dataElement );
        }

        // ---------------------------------------------------------------------
        // Check validations for dataelement into multi-stages
        // ---------------------------------------------------------------------

        runProgramValidation( programValidationService.getProgramValidation( programStageInstance.getProgramInstance().getProgram() ),
            programStageInstance.getProgramInstance(), organisationUnit );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Support method
    // -------------------------------------------------------------------------

    /**
     * ------------------------------------------------------------------------
     * // Check value of the dataElment into previous . // If the value exists,
     * allow users to enter data of // the dataElement into the
     * programStageInstance // Else, disable Input-field of the dataElement
     * ------------------------------------------------------------------------
     **/

    private void checkDataElementInMultiStage( ProgramStageInstance programStageInstance,
        OrganisationUnit organisationUnit, DataElement dataElement )
    {
        ProgramInstance programInstance = programStageInstance.getProgramInstance();
        List<ProgramStage> stages = new ArrayList<ProgramStage>( programInstance.getProgram().getProgramStages() );

        int index = stages.indexOf( programStageInstance.getProgramStage() );

        if ( index != -1 && index != 0 )
        {
            ProgramStage prevStage = stages.get( index - 1 );
            ProgramStageInstance prevStageInstance = programStageInstanceService.getProgramStageInstance(
                programInstance, prevStage );
            PatientDataValue prevValue = patientDataValueService.getPatientDataValue( prevStageInstance, dataElement,
                organisationUnit );

            if ( prevValue == null )
            {
                String message = i18n.getString( "selected" ) + " " + i18n.getString( "program_stage" ) + " "
                    + i18n.getString( "should" ) + " " + i18n.getString( "data_value" ) + " "
                    + i18n.getString( "is_null" );

                resultDEMultiStages.put( dataElement, message );
            }
        }

    }

    private void runProgramValidation( Collection<ProgramValidation> validations, ProgramInstance programInstance, OrganisationUnit orgunit )
    {
        if ( validations != null )
        {
            for ( ProgramValidation validation : validations )
            {
                boolean valid = programValidationService.runValidation( validation, programInstance, orgunit );

                if ( !valid )
                {
                    programValidations.add( validation );
                }
            }
        }
    }
}
