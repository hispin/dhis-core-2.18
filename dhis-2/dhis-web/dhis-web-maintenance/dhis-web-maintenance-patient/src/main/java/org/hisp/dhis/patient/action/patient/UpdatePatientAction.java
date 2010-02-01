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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class UpdatePatientAction
    implements Action
{
    private static final Log LOG = LogFactory.getLog( UpdatePatientAction.class );

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

    public PatientAttributeService patientAttributeService;

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
    // Input - Id
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
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
        throws Exception
    {

        // ---------------------------------------------------------------------
        // Update patient
        // ---------------------------------------------------------------------

        patient = patientService.getPatient( id );
        patient.setFirstName( firstName );
        patient.setMiddleName( middleName );
        patient.setLastName( lastName );
        patient.setGender( gender );
        patient.setBirthDate( format.parseDate( birthDate ) );
        patient.setBirthDateEstimated( birthDateEstimated );
        patient.setRegistrationDate( new Date() );

        patientService.updatePatient( patient );

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
                
                updatePatientAttributeValue(patientAttribute, value);
            }
        }

        patientService.updatePatient( patient );
        
        return SUCCESS;
    }

    private void updatePatientAttributeValue(PatientAttribute patientAttribute, String value)
    {

        PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient,
            patientAttribute );

        if ( patientAttributeValue == null )
        {
                LOG.debug( "Adding PatientAttributeValue, value added" );

                patientAttributeValue = new PatientAttributeValue( patientAttribute, patient, value );

                patientAttributeValueService.savePatientAttributeValue( patientAttributeValue );
        }
        else
        {
            LOG.debug( "Updating PatientAttributeValue, value added/changed" );

            patientAttributeValue.setValue( value );

            patientAttributeValueService.updatePatientAttributeValue( patientAttributeValue );
        }
    }
}
