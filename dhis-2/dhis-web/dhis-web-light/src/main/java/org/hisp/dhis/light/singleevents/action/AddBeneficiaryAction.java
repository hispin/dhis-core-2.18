/*
 * Copyright (c) 2004-2011, University of Oslo
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

package org.hisp.dhis.light.singleevents.action;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.light.dataentry.utils.FormUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.comparator.PatientAttributeGroupSortOrderComparator;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Group1 Fall 2011
 */
public class AddBeneficiaryAction implements Action  {
	
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
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    private PatientAttributeOptionService patientAttributeOptionService;
    
    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }
    
    // -------------------------------------------------------------------------
	// Input Output
	// -------------------------------------------------------------------------   
    
    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    public Integer getOrganisationUnitId(){
    	return this.organisationUnitId;
    }
    
    private Patient patient;
    
    public Patient getPatient()
    {
    	return patient;
    }

    private String fullName;
    
    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }
    
    public String getFullName(){
    	return fullName;
    }

    private String birthDate;
    
    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }
    
    public String getBirthDate()
    {
    	return birthDate;
    }

    private Character dobType;
    
    public void setDobType( Character dobType )
    {
    	this.dobType = dobType;
    }
    //
    public char getDobType(){
    	return dobType;
    }

    private String gender;
    
    public void setGender( String gender )
    {
    	this.gender = gender;
    }
    //
    public String getGender(){
    	return gender;
    }

    private String bloodGroup;
    
    public void setBloodGroup( String bloodGroup ){
    	this.bloodGroup = bloodGroup;
    }
    //
    public String getBloodGroup(){
    	return bloodGroup;
    }
    
    private String registrationDate;
    
    public void setRegistrationDate( String registrationDate ){
    	this.registrationDate = registrationDate;
    }
    
    public String getRegistrationDate()
    {
    	return registrationDate;
    }
    
    private Integer singleEventId;
    
    public void setSingleEventId( Integer singleEventId){
    	this.singleEventId = singleEventId;
    }
    
    public Integer getSingleEventId(){
    	return this.singleEventId;
    }
    
    private String eventName;
    
    public String getEventName(){
    	return this.eventName;
    }
    
    private Integer patientId;
    
    public Integer getPatientId(){
    	return this.patientId;
    }
    
    private List<PatientAttributeGroup> attributeGroups;
    
    public List<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }
    
    private Collection<PatientAttribute> noGroupAttributes;

    public Collection<PatientAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }
    
    private String dynForm[];
    
    public void setDynForm(String[] dynForm) {
    	this.dynForm = dynForm;
    }
    
    public String[] getDynForm()
    {
    	return dynForm;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
	// -------------------------------------------------------------------------
	// Validation
	// -------------------------------------------------------------------------

    private Date rD,bD;
    
    private boolean fullNameIsToLong;
    private boolean invalidFullName;
    private boolean invalidRegistrationDate;
    private boolean invalidBirthDate;
    private boolean noGender;
    private boolean noDobType;
    private boolean invalidDobType;
    private boolean invalidBloodGroup;
    private boolean invalidGender;
       
	private ArrayList<Validate> validList = new ArrayList<Validate>();
    
    public ArrayList<Validate> getValidList()
    {
    	return this.validList;
    }
    
    public boolean getFullNameIsToLong()
    {
    	return fullNameIsToLong;
    }
    
    public boolean getInvalidFullName()
    {
    	return invalidFullName;
    }
    
    public boolean getInvalidRegistrationDate()
    {
    	return invalidRegistrationDate;
    }
    
    public boolean getInvalidBirthDate()
    {
    	return invalidBirthDate;
    }
    
    public boolean getNoGender()
    {
    	return noGender;
    }
    
    public boolean getNoDobType()
    {
    	return noDobType;
    }
    
    public boolean getInvalidDobType()
    {
    	return invalidDobType;
    }
    
    public boolean getInvalidGender()
    {
    	return invalidGender;
    }
    
    public boolean getInvalidBloodGroup()
    {
    	return invalidBloodGroup;
    }
    
    private boolean validate()
    {
    	boolean valid = true;
    	
    	if(validateStringLength(fullName,7,50) == false){
    		fullNameIsToLong = true;
    		valid = false;
    	}
    	
    	if(validName(fullName) == false){
    		invalidFullName = true;
    		valid = false;
    	}
    	
    	if(validateDateNotNull(rD) == false){
    		invalidRegistrationDate = true;
    		valid = false;
    	}
    	
    	if(validateDateNotNull(bD) == false){
    		invalidBirthDate = true;
    		valid = false;
    	}
    	
    	if(validateDropDown(gender) == false){
    		noGender = true;
    		valid = false;
    	}
    	
    	if(validateDropDown(dobType) == false){
    		noDobType = true;
    		valid = false;
    	}
    	
    	if(validateDobType(dobType) == false){
    		invalidDobType = true;
    		valid = false;
    	}
    	
    	if(validateGender(gender) == false){
    		invalidGender = true;
    		valid = false;
    	}
    	
    	if(validateBloodGroup(bloodGroup) == false){
    		invalidBloodGroup = true;
    		valid = false;
    	}
    	
    	return valid;
    }
    
    private boolean validateStringLength(String s, int min, int max)
    {
    	return ((s.length() >= min) && (s.length() <= max));
    }
    
    private boolean validName(String s)
    {
    	return (s.matches("^[\\p{L}|\\s]*$"));
    }
    
    private boolean validateDateNotNull(Date d){
    	if(d == null){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    private boolean validateDropDown(String s){
    	if(s.equalsIgnoreCase("please_select")){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    private boolean validateDropDown(Character c){
    	if(c.equals('p')){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    private boolean validateDobType(Character c)
    {
    	if(c == 'D' || c == 'V'){
    		return true;
    	}else{
    		return false;
    	}
    }

    private boolean validateGender(String s)
    {
    	if(s.equals("M") || s.equals("F") || s.equals("T")){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private boolean validateBloodGroup(String s)
    {
    	if(s.matches("^\\w{1,2}\\-?\\+?$") || s.equalsIgnoreCase("please_select")){
    		return true;
    	}else{
    		return false;
    	}
    }
    

    private boolean validateDynForm(String value, PatientAttribute patientAttribute){
    	
    	boolean valid = true;
    	
    	String type = patientAttribute.getValueType();
    	Integer id = patientAttribute.getId();
    	
		if(value.isEmpty()){
			if(patientAttribute.isMandatory()){
				validList.add(new Validate(id, i18n.getString( "is_required" )));
				valid = false;
			}
		}else if(type.equals("DATE")){
			if(!FormUtils.isDate( value )) {
				validList.add(new Validate(id, value+" "+i18n.getString( "is_invalid_date" )));
				valid = false;
			}
		}else if(type.equals("TEXT")){
			if(!value.matches("^[\\p{L}|\\s|0-9]*$")) {
				validList.add(new Validate(id, value+" "+i18n.getString( "is_invalid_string" )));
				valid = false;
			}
		}else if(type.equals("NUMBER")){
			if(!FormUtils.isNumber( value )) {
				validList.add(new Validate(id, value+" "+i18n.getString( "is_invalid_number" )));
				valid = false;
			}
		}else if(type.equals("YES/NO")){
			if(!FormUtils.isBoolean( value )) {
				validList.add(new Validate(id, value+" "+i18n.getString( "is_invalid_boolean" )));
				valid = false;
			}
		}else if(type.equals("COMBO")){
			Set<PatientAttributeOption> attributeOptions = patientAttribute.getAttributeOptions();
			boolean contains = false;
			for(PatientAttributeOption attributeOption : attributeOptions){
				if(attributeOption.getId() ==  NumberUtils.toInt( value, 0 ) ){	
					contains = true;
				}
			}
			if(!contains){
				validList.add(new Validate(id, value));
			}
			valid = contains;
		}
    	
    	return valid;
    }
    
	public class Validate {	
		private Integer _id;
		private String _errormessage;
		
		public String getErrorMessage(){
			return this._errormessage;
		}
		public Integer getId(){
			return this._id;
		}
		
		public Validate(Integer id, String errormessage)
		{
			this._id = id;
			this._errormessage = errormessage;
		}
		
	}

	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------
    
	@Override
	public String execute() {
		//
	    eventName = programService.getProgram(singleEventId).getName();
	    
		fullNameIsToLong = false;
		invalidFullName = false;
	    invalidRegistrationDate = false;
	    invalidBirthDate = false;
	    noGender = false;
	    noDobType = false;
	    invalidDobType = false;
	    invalidBloodGroup = false;
	    invalidGender = false;

		Patient patient = new Patient();
		// ---------------------------------------------------------------------
        // Set FirstName, MiddleName, LastName by FullName
        // ---------------------------------------------------------------------

        fullName = fullName.trim();

        int startIndex = fullName.indexOf( ' ' );
        int endIndex = fullName.lastIndexOf( ' ' );

        String firstName = fullName.toString();
        String middleName = "";
        String lastName = "";

        if ( fullName.indexOf( ' ' ) != -1 )
        {
            firstName = fullName.substring( 0, startIndex );
            if ( startIndex == endIndex )
            {
                middleName = "";
                lastName = fullName.substring( startIndex + 1, fullName.length() );
            }
            else
            {
                middleName = fullName.substring( startIndex + 1, endIndex );
                lastName = fullName.substring( endIndex + 1, fullName.length() );
            }
        }
        patient.setFirstName( firstName );
        patient.setMiddleName( middleName );
        patient.setLastName( lastName );
        
        // ---------------------------------------------------------------------
        // Set Other information for patient
        // ---------------------------------------------------------------------
        
		OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( getOrganisationUnitId() );
		
		patient.setOrganisationUnit( organisationUnit );
		patient.setGender( gender );		
		patient.setDobType( dobType );
		patient.setIsDead( false );
		if(!bloodGroup.equalsIgnoreCase("please_select")){
			patient.setBloodGroup( bloodGroup );
		}
        birthDate = birthDate.trim();
        bD = format.parseDate( birthDate );
        patient.setBirthDate( bD );
        
        registrationDate = registrationDate.trim();
        rD = format.parseDate( registrationDate );
        patient.setRegistrationDate( rD );
        
        // ---------------------------------------------------------------------
        // Dynamic form
        // ---------------------------------------------------------------------
        
        noGroupAttributes = patientAttributeService.getPatientAttributesNotGroup();

        attributeGroups = new ArrayList<PatientAttributeGroup>( patientAttributeGroupService
            .getAllPatientAttributeGroups() );
        Collections.sort( attributeGroups, new PatientAttributeGroupSortOrderComparator() );
        
        boolean validInGroup = true;
        List<PatientAttributeValue> patientAttributeValues = new ArrayList<PatientAttributeValue>();
        PatientAttributeValue attributeValue = null;
        validList.clear();
        int i = 0;
        
        //Attributes in groups
        
        for (PatientAttributeGroup patientAttributeGroup : attributeGroups) {
        	List<PatientAttribute> patientAttributeList = patientAttributeGroup.getAttributes();
        	for(PatientAttribute patientAttribute : patientAttributeList){
        		       		
        		String value = dynForm[i];
        		
   			 	if(!validateDynForm(value, patientAttribute)){
   			 		validInGroup = false;
   			 	}
    			
    			if(validInGroup){
    				attributeValue = new PatientAttributeValue();
                    attributeValue.setPatient( patient );
                    attributeValue.setPatientAttribute( patientAttribute );
                    
                    if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttribute.getValueType() ) )
                    {
                        PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( value, 0 ) );
                        if ( option != null )
                        {
                            attributeValue.setPatientAttributeOption( option );
                            attributeValue.setValue( option.getName() );
                        }
                    }
                    else
                    {
                        attributeValue.setValue( value.trim() );
                    }
                    patientAttributeValues.add( attributeValue );
    			}
        		
        		i++;
        	}
		}
        
        //Attributes not in groups
        
        boolean validNoGroup = true;
        
        for (PatientAttribute patientAttribute : noGroupAttributes) {
			String value = dynForm[i];			
			
			 if(!validateDynForm(value, patientAttribute)){
				 validNoGroup = false;
			 }
						 
			if(validNoGroup){
				attributeValue = new PatientAttributeValue();
                attributeValue.setPatient( patient );
                attributeValue.setPatientAttribute( patientAttribute );
                
                if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttribute.getValueType() ) ){
                    PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( value, 0 ) );
                    if ( option != null ){
                        attributeValue.setPatientAttributeOption( option );
                        attributeValue.setValue( option.getName() );
                    }
                }else{
                    attributeValue.setValue( value.trim() );
                }
                patientAttributeValues.add( attributeValue );
			}
			
        	i++;
		}
        
		if((validate() == false)||(!validNoGroup)||(!validInGroup)) {
			return ERROR;
		}else{
	        patientId = patientService.createPatient( patient, 0, 0,patientAttributeValues );
	        return SUCCESS;
		}
        
	}
}
