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
package org.hisp.dhis.caseentry.action.patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramAttribute;
import org.hisp.dhis.program.ProgramAttributeOption;
import org.hisp.dhis.program.ProgramAttributeOptionService;
import org.hisp.dhis.program.ProgramAttributeService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.programattributevalue.ProgramAttributeValue;
import org.hisp.dhis.programattributevalue.ProgramAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class RemoveEnrollmentAction
    implements Action
{
    public static final String PREFIX_ATTRIBUTE = "attr";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    private ProgramInstanceService programInstanceService;

    private ProgramAttributeService programAttributeService;

    private ProgramAttributeOptionService programAttributeOptionService;

    private ProgramAttributeValueService programAttributeValueService;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programInstanceId;

    private Collection<Program> programs = new ArrayList<Program>();

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    public void setProgramAttributeService( ProgramAttributeService programAttributeService )
    {
        this.programAttributeService = programAttributeService;
    }

    public void setProgramAttributeOptionService( ProgramAttributeOptionService programAttributeOptionService )
    {
        this.programAttributeOptionService = programAttributeOptionService;
    }

    public void setProgramAttributeValueService( ProgramAttributeValueService programAttributeValueService )
    {
        this.programAttributeValueService = programAttributeValueService;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );

        Patient patient = programInstance.getPatient();

        Program program = programInstance.getProgram();

        // ---------------------------------------------------------------------
        // Update Information of programInstance
        // ---------------------------------------------------------------------

        programInstance.setEndDate( new Date() );
        programInstance.setCompleted( true );

        programInstanceService.updateProgramInstance( programInstance );

        patient.getPrograms().remove( program );
        patientService.updatePatient( patient );

        // ---------------------------------------------------------------------
        // Save Program Attributes
        // ---------------------------------------------------------------------

        HttpServletRequest request = ServletActionContext.getRequest();

        Collection<ProgramAttribute> attributes = programAttributeService.getAllProgramAttributes();

        Set<ProgramAttribute> programAttributes = new HashSet<ProgramAttribute>();

        // ---------------------------------------------------------------------
        // End-user inputs attribute value for DEAD-attribute
        // ---------------------------------------------------------------------
        
        boolean flag = false;

        if ( attributes != null && attributes.size() > 0 )
        {
            programInstance.getAttributes().clear();

            // Save other attributes
            for ( ProgramAttribute attribute : attributes )
            {
                String value = request.getParameter( RemoveEnrollmentAction.PREFIX_ATTRIBUTE + attribute.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    programAttributes.add( attribute );

                    ProgramAttributeValue attributeValue = programAttributeValueService.getProgramAttributeValue(
                        programInstance, attribute );

                    if ( attributeValue == null )
                    {
                        attributeValue = new ProgramAttributeValue();
                        attributeValue.setProgramInstance( programInstance );
                        attributeValue.setProgramAttribute( attribute );

                        // DEAD program-attribute
                        if ( attribute.getName().equalsIgnoreCase( ProgramAttribute.DEAD_NAME )
                            && attribute.getValueType().equalsIgnoreCase( ProgramAttribute.TYPE_BOOL ) )
                        {
                            attributeValue.setValue( value.trim() );
                            patient.setIsDead( Boolean.parseBoolean( value.trim() ) );
                            patientService.updatePatient( patient );
                            flag = true;
                        }
                        else if ( ProgramAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            ProgramAttributeOption option = programAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setProgramAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }

                        // CLOSED-DATE program-attribute
                        if ( attribute.getName().equalsIgnoreCase( ProgramAttribute.CLOSED_DATE )
                            && attribute.getValueType().equalsIgnoreCase( ProgramAttribute.TYPE_DATE ) && flag )
                        {
                            patient.setDeathDate( format.parseDate( value.trim() ) );
                            patientService.updatePatient( patient );
                        }

                        programAttributeValueService.saveProgramAttributeValue( attributeValue );
                    }
                    else
                    {
                        if ( ProgramAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            ProgramAttributeOption option = programAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setProgramAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }
                    }

                    programAttributeValueService.updateProgramAttributeValue( attributeValue );
                }
            }
        }

        programInstance.setAttributes( programAttributes );

        programInstanceService.updateProgramInstance( programInstance );

        return SUCCESS;
    }
}
