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

package org.hisp.dhis.caseentry.action.patient;

import org.apache.commons.lang.math.NumberUtils;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $SavePatientAttributeValueAction.java Mar 26, 2012 4:06:46 PM$
 */
public class SavePatientAttributeValueAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeOptionService patientAttributeOptionService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer patientId;

    private Integer attributeId;

    private String value;

    private Integer statusCode;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------


    public Integer getStatusCode()
    {
        return statusCode;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    public void setAttributeId( Integer attributeId )
    {
        this.attributeId = attributeId;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        Patient patient = patientService.getPatient( patientId );
        
        PatientAttribute attribute = patientAttributeService.getPatientAttribute( attributeId );
        
        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue( patient,
            attribute );

        if ( value != null )
        {
            attributeValue = patientAttributeValueService.getPatientAttributeValue( patient, attribute );

            if ( !patient.getAttributes().contains( attribute ) )
            {
                patient.getAttributes().add( attribute );
            }

            if ( attributeValue == null )
            {
                attributeValue = new PatientAttributeValue();
                attributeValue.setPatient( patient );
                attributeValue.setPatientAttribute( attribute );
                if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                {
                    PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( value, 0 ) );
                    if ( option != null )
                    {
                        attributeValue.setPatientAttributeOption( option );
                        attributeValue.setValue( option.getName() );
                    }
                    else
                    {
                        // This option was deleted ???
                    }
                }
                else
                {
                    attributeValue.setValue( value.trim() );
                }
                patientAttributeValueService.savePatientAttributeValue( attributeValue );
            }
            else
            {
                if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                {
                    PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( value, 0 ) );
                    if ( option != null )
                    {
                        attributeValue.setPatientAttributeOption( option );
                        attributeValue.setValue( option.getName() );
                    }
                    else
                    {
                        // This option was deleted ???
                    }
                }
                else
                {
                    attributeValue.setValue( value.trim() );
                }
                patientAttributeValueService.updatePatientAttributeValue( attributeValue );
            }
        }
        else if ( attributeValue != null )
        {
            patientAttributeValueService.deletePatientAttributeValue( attributeValue );
            patient.getAttributes().remove( attribute );
            patientService.updatePatient( patient );
        }
        
        statusCode = 0;

        return SUCCESS;
    }

}
