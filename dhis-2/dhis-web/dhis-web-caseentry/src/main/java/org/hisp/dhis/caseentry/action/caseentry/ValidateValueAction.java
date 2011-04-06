/*
 * Copyright (c) 2004-2010, University of Oslo
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
import java.util.List;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageDataElementValidation;
import org.hisp.dhis.program.ProgramStageDataElementValidationService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version ValidateValueAction.java May 11, 2010 2:13:52 PM
 */
public class ValidateValueAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    private DataElementService dataElementService;

    private ProgramInstanceService programInstanceService;

    private ProgramStageInstanceService programStageInstanceService;

    private ProgramStageDataElementValidationService validationService;

    private PatientDataValueService patientDataValueService;

    private PatientService patientService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private boolean providedByAnotherFacility;

    private String value;

    private int dataElementId;

    private int statusCode;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<ProgramStageDataElementValidation> resultValidation;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    public void setValidationService( ProgramStageDataElementValidationService validationService )
    {
        this.validationService = validationService;
    }

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public int getDataElementId()
    {
        return dataElementId;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public void setStatusCode( int statusCode )
    {
        this.statusCode = statusCode;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public void setProvidedByAnotherFacility( boolean providedByAnotherFacility )
    {
        this.providedByAnotherFacility = providedByAnotherFacility;
    }

    public void setDataElementId( int dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    public SelectedStateManager getSelectedStateManager()
    {
        return selectedStateManager;
    }

    public ProgramStageDataElementValidationService getValidationService()
    {
        return validationService;
    }

    public boolean isProvidedByAnotherFacility()
    {
        return providedByAnotherFacility;
    }

    public String getValue()
    {
        return value;
    }

    public List<ProgramStageDataElementValidation> getResultValidation()
    {
        return resultValidation;
    }

    public void setResultValidation( List<ProgramStageDataElementValidation> resultValidation )
    {
        this.resultValidation = resultValidation;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        Patient patient = selectedStateManager.getSelectedPatient();

        Program program = selectedStateManager.getSelectedProgram();

        ProgramStage programStage = selectedStateManager.getSelectedProgramStage();

        Collection<ProgramInstance> progamInstances = programInstanceService.getProgramInstances( patient, program,
            false );

        ProgramInstance programInstance = progamInstances.iterator().next();

        DataElement dataElement = dataElementService.getDataElement( dataElementId );

        // Check validation for dataElement into the Stage

        ProgramStageDataElement element = new ProgramStageDataElement( programStage, dataElement, false );

        Collection<ProgramStageDataElementValidation> validations = validationService
            .getProgramStageDataElementValidations( element );

        resultValidation = validation( validations, organisationUnit, programInstance, value, programStage, dataElement );

        if ( resultValidation.size() > 0 )
        {
            statusCode = 1;

            return INPUT;
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    private List<ProgramStageDataElementValidation> validation(
        Collection<ProgramStageDataElementValidation> validations, OrganisationUnit organisationUnit,
        ProgramInstance programInstance, String value, ProgramStage programStage, DataElement dataElement )
        throws Exception
    {
        List<ProgramStageDataElementValidation> result = new ArrayList<ProgramStageDataElementValidation>();

        if ( validations != null )
        {

            for ( ProgramStageDataElementValidation validation : validations )
            {
                // Get left-side
                ProgramStageDataElement leftSide = validation.getLeftProgramStageDataElement();
                // Get right-side
                ProgramStageDataElement rightSide = validation.getRightProgramStageDataElement();

                // Get stageInstance to compare
                ProgramStageInstance comparerogramStageInstance = null;

                int i = 2;
                if ( leftSide.getProgramStage().equals( programStage )
                    && leftSide.getDataElement().equals( dataElement ) )
                {
                    // get left-side value
                    Object objectValue = patientService.getObjectValue( leftSide.getDataElement().getType(), value, format );

                    // get program-stage of right-side
                    comparerogramStageInstance = programStageInstanceService.getProgramStageInstance( programInstance,
                        rightSide.getProgramStage() );

                    // Get value into right-right to compare;
                    PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue(
                        comparerogramStageInstance, rightSide.getDataElement(), organisationUnit );

                    if ( patientDataValue != null )
                    {
                        String dbValue = patientDataValue.getValue();
                        Object compareValue = patientService.getObjectValue( rightSide.getDataElement().getType(),
                            dbValue, format );

                        i = ((Comparable<Object>) objectValue).compareTo( (Comparable<Object>) compareValue );
                    }

                }
                else
                {
                    // get right-side value
                    Object objectValue = patientService.getObjectValue( rightSide.getDataElement().getType(), value, format );

                    comparerogramStageInstance = programStageInstanceService.getProgramStageInstance( programInstance,
                        leftSide.getProgramStage() );

                    // Get value to compare;
                    PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue(
                        comparerogramStageInstance, leftSide.getDataElement(), organisationUnit );
                    if ( patientDataValue != null )
                    {
                        String dbValue = patientDataValue.getValue();
                        Object compareValue = patientService.getObjectValue( leftSide.getDataElement().getType(),
                            dbValue, format );

                        i = ((Comparable<Object>) compareValue).compareTo( (Comparable<Object>) objectValue );
                    }
                }

                if ( i != validation.getOperator() && i != 2 )
                {
                    result.add( validation );
                }

            }
        }

        return result;
    }

}
