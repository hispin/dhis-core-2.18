package org.hisp.dhis.caseentry.action.patient;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 */
public class ValidatePatientAction
    implements Action
{
    public static final String PATIENT_DUPLICATE = "duplicate";

    public static final String PREFIX_ATTRIBUTE = "attr";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    private ProgramService programService;

    private PatientAttributeService attributeService;

    private OrganisationUnitSelectionManager selectionManager;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    private Integer programId;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    private Map<String, String> patientAttributeValueMap = new HashMap<String, String>();

    private Collection<Patient> patients;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        OrganisationUnit orgunit = selectionManager.getSelectedOrganisationUnit();
        Program program = null;
        if ( programId != null )
        {
            program = programService.getProgram( programId );
        }

        Patient patient = null;
        if ( id != null )
        {
            patient = patientService.getPatient( id );
        }
        else
        {
            patient = new Patient();
        }

        patient.setOrganisationUnit( orgunit );

        HttpServletRequest request = ServletActionContext.getRequest();

        Collection<PatientAttribute> attributes = attributeService.getAllPatientAttributes();

        if ( attributes != null && attributes.size() > 0 )
        {
            String value = null;

            Set<PatientAttributeValue> attributeValues = new HashSet<PatientAttributeValue>();

            for ( PatientAttribute attribute : attributes )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
                if ( StringUtils.isNotBlank( value ) )
                {
                    PatientAttributeValue attributeValue = new PatientAttributeValue();
                    attributeValue.setPatient( patient );
                    attributeValue.setPatientAttribute( attribute );
                    attributeValue.setValue( value );

                    attributeValues.add( attributeValue );
                }
            }

            patient.setAttributeValues( attributeValues );
        }

        // ---------------------------------------------------------------------
        // Validate patient
        // ---------------------------------------------------------------------

        int errorCode = patientService.validatePatient( patient, program );

        message = errorCode + "";

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Getter/Setter
    // -------------------------------------------------------------------------

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public Collection<Patient> getPatients()
    {
        return patients;
    }

    public void setAttributeService( PatientAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public String getMessage()
    {
        return message;
    }

    public Map<String, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

}
