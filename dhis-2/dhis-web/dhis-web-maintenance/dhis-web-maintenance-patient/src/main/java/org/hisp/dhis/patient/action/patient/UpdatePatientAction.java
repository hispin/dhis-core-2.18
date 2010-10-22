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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
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
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nFormat format;

    private PatientService patientService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientIdentifierService patientIdentifierService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    private OrganisationUnitSelectionManager selectionManager;

    private PatientAttributeOptionService patientAttributeOptionService;

    // -------------------------------------------------------------------------
    // Input - Id
    // -------------------------------------------------------------------------
    private Integer id;

    // -------------------------------------------------------------------------
    // Input - name
    // -------------------------------------------------------------------------
    private String firstName;

    private String middleName;

    private String lastName;

    // -------------------------------------------------------------------------
    // Input - demographics
    // -------------------------------------------------------------------------
    private String birthDate;

    private Integer age;

    private boolean birthDateEstimated;

    private String gender;

    private boolean underAge;

    private Integer representativeId;

    private Integer relationshipTypeId;

    // -------------------------------------------------------------------------
    // Output - making the patient available so that its attributes can be
    // edited
    // -------------------------------------------------------------------------

    private Patient patient;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();

        // ---------------------------------------------------------------------
        // Update patient
        // ---------------------------------------------------------------------

        patient = patientService.getPatient( id );
        patient.setFirstName( firstName );
        patient.setMiddleName( middleName );
        patient.setLastName( lastName );
        patient.setGender( gender );
        patient.setUnderAge( underAge );
        patient.setOrganisationUnit( organisationUnit );

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

        // -------------------------------------------------------------------------------------
        // Save PatientIdentifier
        // -------------------------------------------------------------------------------------
        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;

        Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        PatientIdentifier identifier = null;

        if ( identifierTypes != null && identifierTypes.size() > 0 )
        {
            for ( PatientIdentifierType identifierType : identifierTypes )
            {
                if ( identifierType.getFormat().equals( "State Format" ) )
                {
                    value = request.getParameter( "progcode" ) + request.getParameter( "yearcode" )
                        + request.getParameter( "benicode" );
                }
                else
                {
                    value = request.getParameter( AddPatientAction.PREFIX_IDENTIFIER + identifierType.getId() );
                }
                // value = request.getParameter(
                // AddPatientAction.PREFIX_IDENTIFIER + identifierType.getId()
                // );

                if ( StringUtils.isNotBlank( value ) )
                {
                    value = value.trim();

                    identifier = patientIdentifierService.getPatientIdentifier( identifierType, patient );

                    if ( identifier == null )
                    {
                        identifier = new PatientIdentifier();
                        identifier.setIdentifierType( identifierType );
                        identifier.setPatient( patient );
                        identifier.setIdentifier( value );
                        patient.getIdentifiers().add( identifier );
                    }
                    else
                    {
                        identifier.setIdentifier( value );
                        patient.getIdentifiers().add( identifier );
                    }
                }
            }
        }

        // --------------------------------------------------------------------------------------------------------
        // Save Patient Attributes
        // -----------------------------------------------------------------------------------------------------

        Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();

        List<PatientAttributeValue> valuesForSave = new ArrayList<PatientAttributeValue>();
        List<PatientAttributeValue> valuesForUpdate = new ArrayList<PatientAttributeValue>();
        Collection<PatientAttributeValue> valuesForDelete = null;

        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            patient.getAttributes().clear();
            valuesForDelete = patientAttributeValueService.getPatientAttributeValues( patient );

            // Save other attributes

            for ( PatientAttribute attribute : attributes )
            {
                value = request.getParameter( AddPatientAction.PREFIX_ATTRIBUTE + attribute.getId() );

                if ( StringUtils.isNotBlank( value ) )
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
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
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
                        valuesForSave.add( attributeValue );
                    }
                    else
                    {
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
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
                        valuesForUpdate.add( attributeValue );
                        valuesForDelete.remove( attributeValue );
                    }
                }
            }
        }

        patientService.updatePatient( patient, representativeId, relationshipTypeId, valuesForSave, valuesForUpdate,
            valuesForDelete );

        return SUCCESS;
    }

    // -----------------------------------------------------------------------------
    // Getter/Setter
    // -----------------------------------------------------------------------------

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    public void setBirthDateEstimated( boolean birthDateEstimated )
    {
        this.birthDateEstimated = birthDateEstimated;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setAge( Integer age )
    {
        this.age = age;
    }

    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }

    public void setUnderAge( boolean underAge )
    {
        this.underAge = underAge;
    }

    public void setRepresentativeId( Integer representativeId )
    {
        this.representativeId = representativeId;
    }

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }
}
