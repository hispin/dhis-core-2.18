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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.state.SelectedStateManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramAttribute;
import org.hisp.dhis.program.ProgramAttributeOption;
import org.hisp.dhis.program.ProgramAttributeOptionService;
import org.hisp.dhis.program.ProgramAttributeService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.programattributevalue.ProgramAttributeValue;
import org.hisp.dhis.programattributevalue.ProgramAttributeValueService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Level;
import org.apache.struts2.ServletActionContext;

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

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private ProgramAttributeService programAttributeService;

    public void setProgramAttributeService( ProgramAttributeService programAttributeService )
    {
        this.programAttributeService = programAttributeService;
    }

    private ProgramAttributeOptionService programAttributeOptionService;

    public void setProgramAttributeOptionService( ProgramAttributeOptionService programAttributeOptionService )
    {
        this.programAttributeOptionService = programAttributeOptionService;
    }

    private ProgramAttributeValueService programAttributeValueService;

    public void setProgramAttributeValueService( ProgramAttributeValueService programAttributeValueService )
    {
        this.programAttributeValueService = programAttributeValueService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
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

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    private ProgramInstance programInstance;

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    private Integer programInstanceId;

    public Integer getProgramInstanceId()
    {
        return programInstanceId;
    }

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }

    private Collection<Program> programs = new ArrayList<Program>();

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        patient = selectedStateManager.getSelectedPatient();

        Program program = selectedStateManager.getSelectedProgram();

        programs = programService.getAllPrograms();

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( patient, program,
            false );

        if ( programInstances.iterator().hasNext() )
        {
            programInstance = programInstances.iterator().next();
        }

        if ( programInstance != null )
        {
            programInstance.setEndDate( new Date() );
            programInstance.setCompleted( true );

            programInstanceService.updateProgramInstance( programInstance );

            patient.getPrograms().remove( program );
            patientService.updatePatient( patient );

            selectedStateManager.clearSelectedProgram();
        }

        // --------------------------------------------------------------------------------------------------------
        // Save Program Attributes
        // -----------------------------------------------------------------------------------------------------

        HttpServletRequest request = ServletActionContext.getRequest();
        System.out.println( "\n\n ++++++++++++ request : " + request );

        Collection<ProgramAttribute> attributes = programAttributeService.getAllProgramAttributes();

        Set<ProgramAttribute> programAttributes = new HashSet<ProgramAttribute>();

        if ( attributes != null && attributes.size() > 0 )
        {
            programInstance.getAttributes().clear();

            // Save other attributes
            for ( ProgramAttribute attribute : attributes )
            {
                String value = request.getParameter( RemoveEnrollmentAction.PREFIX_ATTRIBUTE + attribute.getId() );
                System.out.println( "\n\n ++++++++++++ attr : " + RemoveEnrollmentAction.PREFIX_ATTRIBUTE
                    + attribute.getId() );
                System.out.println( "\n\n value : " + value );

                if ( StringUtils.isNotBlank( value ) )
                {
                    programAttributes.add( attribute );

                    ProgramAttributeValue attributeValue = programAttributeValueService.getProgramAttributeValue(
                        programInstance, attribute );

                    // attributeValue is not exist
                    if ( attributeValue == null )
                    {
                        attributeValue = new ProgramAttributeValue();
                        attributeValue.setProgramInstance( programInstance );
                        attributeValue.setProgramAttribute( attribute );
                        
                        if ( ProgramAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            ProgramAttributeOption option = programAttributeOptionService.get( NumberUtils.toInt( value, 0 ) );
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

                        // save values
                        programAttributeValueService.saveProgramAttributeValue( attributeValue );
                        
                    }
                    // attributeValue is exist
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

                    // update values
                    programAttributeValueService.updateProgramAttributeValue( attributeValue );
                }
            }
        }

        programInstance.setAttributes( programAttributes );

        programInstanceService.updateProgramInstance( programInstance );

        return SUCCESS;
    }
}
