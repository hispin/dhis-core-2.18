package org.hisp.dhis.light.beneficiaryregistration.action;

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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.StrutsStatics;
import org.hisp.dhis.light.utils.ValueUtils;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeOption;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeOptionService;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public class SaveBeneficiaryAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityInstanceService patientService;

    public TrackedEntityInstanceService getPatientService()
    {
        return patientService;
    }

    public void setPatientService( TrackedEntityInstanceService patientService )
    {
        this.patientService = patientService;
    }

    private OrganisationUnitService organisationUnitService;

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private TrackedEntityAttributeService patientAttributeService;

    public TrackedEntityAttributeService getPatientAttributeService()
    {
        return patientAttributeService;
    }

    public void setPatientAttributeService( TrackedEntityAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private TrackedEntityAttributeOptionService patientAttributeOptionService;

    public TrackedEntityAttributeOptionService getPatientAttributeOptionService()
    {
        return patientAttributeOptionService;
    }

    public void setPatientAttributeOptionService( TrackedEntityAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }

    private ProgramService programService;

    public ProgramService getProgramService()
    {
        return programService;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer orgUnitId;

    public Integer getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String patientFullName;

    public String getPatientFullName()
    {
        return patientFullName;
    }

    public void setPatientFullName( String patientFullName )
    {
        this.patientFullName = patientFullName;
    }

    private String gender;

    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    private String dateOfBirth;

    public String getDateOfBirth()
    {
        return dateOfBirth;
    }

    public void setDateOfBirth( String dateOfBirth )
    {
        this.dateOfBirth = dateOfBirth;
    }

    public boolean validated;

    public boolean isValidated()
    {
        return validated;
    }

    public void setValidated( boolean validated )
    {
        this.validated = validated;
    }

    public Map<String, String> validationMap = new HashMap<String, String>();

    public Map<String, String> getValidationMap()
    {
        return validationMap;
    }

    public void setValidationMap( Map<String, String> validationMap )
    {
        this.validationMap = validationMap;
    }

    public Map<String, String> previousValues = new HashMap<String, String>();

    public Map<String, String> getPreviousValues()
    {
        return previousValues;
    }

    public void setPreviousValues( Map<String, String> previousValues )
    {
        this.previousValues = previousValues;
    }

    private String dobType;

    public String getDobType()
    {
        return dobType;
    }

    public void setDobType( String dobType )
    {
        this.dobType = dobType;
    }

    private Integer patientId;

    public Integer getPatientId()
    {
        return patientId;
    }

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    private Collection<TrackedEntityAttribute> patientAttributes;

    public Collection<TrackedEntityAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    public void setPatientAttributes( Collection<TrackedEntityAttribute> patientAttributes )
    {
        this.patientAttributes = patientAttributes;
    }

    private String phoneNumber;

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    // Register patient on-the-fly

    private Integer originalPatientId;

    public Integer getOriginalPatientId()
    {
        return originalPatientId;
    }

    public void setOriginalPatientId( Integer originalPatientId )
    {
        this.originalPatientId = originalPatientId;
    }

    private Integer relationshipTypeId;

    public Integer getRelationshipTypeId()
    {
        return relationshipTypeId;
    }

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }

    @Override
    public String execute()
        throws Exception
    {
        TrackedEntityInstance patient = new TrackedEntityInstance();
        Set<TrackedEntityAttribute> patientAttributeSet = new HashSet<TrackedEntityAttribute>();
        Set<TrackedEntityAttributeValue> patientAttributeValues = new HashSet<TrackedEntityAttributeValue>();

        patientAttributes = patientAttributeService.getAllTrackedEntityAttributes();
        Collection<Program> programs = programService.getAllPrograms();

        for ( Program program : programs )
        {
            patientAttributes.removeAll( program.getAttributes() );
        }

        patient.setOrganisationUnit( organisationUnitService.getOrganisationUnit( orgUnitId ) );

        if ( this.patientFullName.trim().length() < 7 )
        {
            validationMap.put( "fullName", "is_invalid_name_length" );
        }
        else
        {
            patient.setName( patientFullName.trim() );
        }

        HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get( StrutsStatics.HTTP_REQUEST );
        Map<String, String> parameterMap = ContextUtils.getParameterMap( request );

        // Add Attributes
       Collection<TrackedEntityAttribute> patientAttributes = patientAttributeService.getAllTrackedEntityAttributes();

        for ( Program program : programs )
        {
            patientAttributes.removeAll( program.getAttributes() );
        }


        for ( TrackedEntityAttribute patientAttribute : patientAttributes )
        {
            patientAttributeSet.add( patientAttribute );

            String key = "AT" + patientAttribute.getId();
            String value = parameterMap.get( key ).trim();

            if ( value != null )
            {
                if ( patientAttribute.isMandatory() && value.trim().equals( "" ) )
                {
                    this.validationMap.put( key, "is_mandatory" );
                }
                else if ( value.trim().length() > 0
                    && patientAttribute.getValueType().equals( TrackedEntityAttribute.TYPE_INT )
                    && !MathUtils.isInteger( value ) )
                {
                    this.validationMap.put( key, "is_invalid_number" );
                }
                else if ( value.trim().length() > 0
                    && patientAttribute.getValueType().equals( TrackedEntityAttribute.TYPE_DATE )
                    && !ValueUtils.isDate( value ) )
                {
                    this.validationMap.put( key, "is_invalid_date" );
                }
                else
                {
                    TrackedEntityAttributeValue patientAttributeValue = new TrackedEntityAttributeValue();

                    if ( TrackedEntityAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttribute.getValueType() ) )
                    {
                        TrackedEntityAttributeOption option = patientAttributeOptionService
                            .get( NumberUtils.toInt( value, 0 ) );

                        if ( option != null )
                        {
                            patientAttributeValue.setAttributeOption( option );
                        }
                    }

                    patientAttributeValue.setEntityInstance( patient );
                    patientAttributeValue.setAttribute( patientAttribute );
                    patientAttributeValue.setValue( value.trim() );
                    patientAttributeValues.add( patientAttributeValue );
                }

                this.previousValues.put( key, value );
            }
        }

        if ( this.validationMap.size() > 0 )
        {
            this.validated = false;
            this.previousValues.put( "fullName", this.patientFullName );
            this.previousValues.put( "gender", this.gender );
            this.previousValues.put( "dob", this.dateOfBirth );
            this.previousValues.put( "dobType", this.dobType );
            this.previousValues.put( "phoneNumber", this.phoneNumber );
            return ERROR;
        }

        patientId = patientService.createTrackedEntityInstance( patient, null, null, patientAttributeValues );
        validated = true;

        if ( this.originalPatientId != null )
        {
            return "redirect";
        }

        return SUCCESS;
    }
}
