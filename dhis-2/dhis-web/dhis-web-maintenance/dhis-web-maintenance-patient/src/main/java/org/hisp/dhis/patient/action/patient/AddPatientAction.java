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

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.state.SelectedStateManager;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class AddPatientAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    // -------------------------------------------------------------------------
    // Input - identifier
    // -------------------------------------------------------------------------

    private String identifier;

    public void setIdentifier( String identifier )
    {
        this.identifier = identifier;
    }

    // -------------------------------------------------------------------------
    // Input - name
    // -------------------------------------------------------------------------

    private String firstName;

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    private String middleName;

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    private String lastName;

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    // -------------------------------------------------------------------------
    // Input - demographics
    // -------------------------------------------------------------------------

    private String birthDate;

    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    private Integer age;

    public void setAge( Integer age )
    {
        this.age = age;
    }

    private boolean birthDateEstimated;

    public void setBirthDateEstimated( boolean birthDateEstimated )
    {
        this.birthDateEstimated = birthDateEstimated;
    }

    private String gender;

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    // -------------------------------------------------------------------------
    // Output - making the patient available so that its attributes can be
    // edited
    // -------------------------------------------------------------------------

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();

        patient = new Patient();

        patient.setFirstName( firstName );
        patient.setMiddleName( middleName );
        patient.setLastName( lastName );
        patient.setGender( gender );

        if ( birthDate != null )
        {

            birthDate = birthDate.trim();

            if ( birthDate.length() != 0 )
            {
                patient.setBirthDate( format.parseDate( birthDate ) );
                patient.setBirthDateEstimated( birthDateEstimated );
            }
            else
            {
                if ( age != null )
                {
                    patient.setBirthDateFromAge( age.intValue() );
                }
            }
        }
        else
        {
            if ( age != null )
            {
                patient.setBirthDateFromAge( age.intValue() );
            }
        }

        patient.setRegistrationDate( new Date() );

        patientService.savePatient( patient );

        PatientIdentifier patientIdentifier = new PatientIdentifier();
        patientIdentifier.setIdentifier( identifier );
        patientIdentifier.setOrganisationUnit( organisationUnit );
        patientIdentifier.setPatient( patient );
        patientIdentifier.setPreferred( true );

        patientIdentifierService.savePatientIdentifier( patientIdentifier );

        selectedStateManager.clearListAll();
        selectedStateManager.clearSearchingAttributeId();
        selectedStateManager.setSearchText( patientIdentifier.getIdentifier() );

        // add attribute value
        Collection<PatientAttribute> patientAttributes = patientAttributeService.getPatientAttributesByMandatory( true );

        HttpServletRequest request = ServletActionContext.getRequest();
         
        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            int patientAttributeId = patientAttribute.getId();
            String value = request.getParameterValues( patientAttributeId + "" )[0].trim();
            if ( value.length() > 0 )
            {
                if ( !patient.getAttributes().contains( patientAttribute ) )
                {
                    patient.getAttributes().add( patientAttribute );
                }
                
                PatientAttributeValue patientAttributeValue = new PatientAttributeValue( patientAttribute, patient, value );

                patientAttributeValueService.savePatientAttributeValue( patientAttributeValue );
            }
        }
        
        patientService.updatePatient( patient );
        
        return SUCCESS;
    }
}
