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
package org.hisp.dhis.patient.action.patientattribute;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SavePatientAttributeValueAction
    implements Action
{

    private static final Log LOG = LogFactory.getLog( SavePatientAttributeValueAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
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
    
    private PatientAttributeOptionService patientAttributeOptionService;

    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }

    private int patientId;

    public void setPatientId( int patientId )
    {
        this.patientId = patientId;
    }

    private int patientAttributeId;

    public void setPatientAttributeId( int patientAttributeId )
    {
        this.patientAttributeId = patientAttributeId;
    }

    private int statusCode;

    public int getStatusCode()
    {
        return statusCode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Patient patient = patientService.getPatient( patientId );

        PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( patientAttributeId );

        if ( !patient.getAttributes().contains( patientAttribute ) )
        {
            patient.getAttributes().add( patientAttribute );
            patientService.updatePatient( patient );
        }

        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient,
            patientAttribute );

        if ( patientAttributeValue == null )
        {

            if ( value != null )
            {

                LOG.debug( "Adding PatientAttributeValue, value added" );
                
                if( patientAttribute.getValueType().equalsIgnoreCase( PatientAttribute.TYPE_COMBO ) )
                {
                    PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( value,0 ) );
                    if( option != null )
                    {
                        patientAttributeValue = new PatientAttributeValue( patientAttribute, patient );
                        patientAttributeValue.setPatientAttributeOption( option );
                        patientAttributeValue.setValue( option.getName() );
                    }
                }else
                {
                    patientAttributeValue = new PatientAttributeValue( patientAttribute, patient, value );
                }
                patientAttributeValueService.savePatientAttributeValue( patientAttributeValue );
            }
        }
        else
        {
            LOG.debug( "Updating PatientAttributeValue, value added/changed" );

            if( patientAttribute.getValueType().equalsIgnoreCase( PatientAttribute.TYPE_COMBO ) )
            {
                PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( value, 0 ) );
                if( option != null )
                {
                    patientAttributeValue.setPatientAttributeOption( option );
                    patientAttributeValue.setValue( option.getName() );
                }
            }else
            {
                patientAttributeValue.setValue( value );
            }

            patientAttributeValueService.updatePatientAttributeValue( patientAttributeValue );
        }

        return SUCCESS;
    }

}
