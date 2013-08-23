package org.hisp.dhis.patient.action.dataentryform;

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
import java.util.List;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientRegistrationForm;
import org.hisp.dhis.patient.PatientRegistrationFormService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $ SaveDefaultPatientRegistrationFormAcion.java Jul 3, 2013 11:05:02
 *          AM $
 */
public class SaveDefaultPatientRegistrationFormAcion
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private PatientRegistrationFormService patientRegistrationFormService;

    public void setPatientRegistrationFormService( PatientRegistrationFormService patientRegistrationFormService )
    {
        this.patientRegistrationFormService = patientRegistrationFormService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientIdentifierTypeService identifierTypeService;

    public void setIdentifierTypeService( PatientIdentifierTypeService identifierTypeService )
    {
        this.identifierTypeService = identifierTypeService;
    }

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------
    
    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private List<String> fixedAttributes;

    public void setFixedAttributes( List<String> fixedAttributes )
    {
        this.fixedAttributes = fixedAttributes;
    }

    private List<Integer> dynamicAttributeIds;

    public void setDynamicAttributeIds( List<Integer> dynamicAttributeIds )
    {
        this.dynamicAttributeIds = dynamicAttributeIds;
    }

    private List<Integer> identifierTypeIds;

    public void setIdentifierTypeIds( List<Integer> identifierTypeIds )
    {
        this.identifierTypeIds = identifierTypeIds;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        List<PatientAttribute> dynamicAttributes = new ArrayList<PatientAttribute>();

        for ( Integer dynamicAttributeId : dynamicAttributeIds )
        {
            dynamicAttributes.add( patientAttributeService.getPatientAttribute( dynamicAttributeId ) );
        }

        List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();

        for ( Integer identifierTypeId : identifierTypeIds )
        {
            identifierTypes.add( identifierTypeService.getPatientIdentifierType( identifierTypeId ) );
        }

        PatientRegistrationForm registrationForm = null;

        Program program = null;

        if ( programId == null )
        {
            registrationForm = patientRegistrationFormService.getCommonPatientRegistrationForm();
        }
        else
        {
            program = programService.getProgram( programId );
            registrationForm = patientRegistrationFormService.getPatientRegistrationForm( program );
        }

        // ---------------------------------------------------------------------
        // Save data-entry-form
        // ---------------------------------------------------------------------

        if ( registrationForm == null )
        {
            registrationForm = new PatientRegistrationForm();
            registrationForm.setName( name );
            registrationForm.setFixedAttributes( fixedAttributes );
            registrationForm.setDynamicAttributes( dynamicAttributes );
            registrationForm.setIdentifierTypes( identifierTypes );
            registrationForm.setProgram( program );
            patientRegistrationFormService.savePatientRegistrationForm( registrationForm );
        }
        else
        {
            registrationForm.setName( name );
            registrationForm.setFixedAttributes( fixedAttributes );
            registrationForm.setDynamicAttributes( dynamicAttributes );
            registrationForm.setIdentifierTypes( identifierTypes );
            patientRegistrationFormService.updatePatientRegistrationForm( registrationForm );
        }

        return SUCCESS;
    }
}
