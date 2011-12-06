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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.light.dataentry.utils.FormUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;

import com.opensymphony.xwork2.Action;

/**
 * @author Group1 Fall 2011
 */
public class AddSingleEventAction implements Action  {
	
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------
	
	private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
	private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
    	this.patientService = patientService;
    }
    
    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private FormUtils formUtils;

    public void setFormUtils( FormUtils formUtils )
    {
        this.formUtils = formUtils;
    }

    public FormUtils getFormUtils()
    {
        return formUtils;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    // -------------------------------------------------------------------------
	// Input Output
	// -------------------------------------------------------------------------
    
    private Integer singleEventId;
    
    public void setSingleEventId( Integer singleEventId ){
    	this.singleEventId = singleEventId;
    }
    
    public Integer getSingleEventId(){
    	return singleEventId;
    }
    
    private Integer patientId;
    
    public void  setPatientId( Integer patientId ){
    	this.patientId = patientId;
    }
    
    public Integer getPatientId(){
    	return this.patientId;
    }
 
    private Patient patient;
    public Patient getPatient()
    {
    	return patient;
    }
    
    private String eventName;
    
    public String getEventName(){
    	return this.eventName;
    }
    
    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    public Integer getOrganisationUnitId(){
    	return this.organisationUnitId;
    }
    
    private boolean update;
    
    public void setUpdate( boolean update )
    {
    	this.update = update;
    }
    
    public boolean getUpdate()
    {
    	return this.update;
    }
    
    private Integer instId;
    
    public void setInstId( Integer instId )
    {
    	this.instId = instId;
    }
    
    public Integer getInstId()
    {
    	return this.instId;
    }
    
    private List<String> dynForm = new ArrayList<String>() ;
    
    public void setDynForm(List<String> dynForm) {
    	this.dynForm = dynForm;
    }
    
    public List<String> getDynForm()
    {
    	return dynForm;
    }
    
    private String resultString;
    
    public void setResultString(String resultString){
    	this.resultString = resultString;
    }
    
    public String getResultString(){
    	return this.resultString;
    }
    
    private ArrayList<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>();
    
    public ArrayList<ProgramStageDataElement> getProgramStageDataElements(){
    	return this.programStageDataElements;
    }
    
	 static final Comparator<ProgramStageDataElement> OrderBySortOrder =
		 	new Comparator<ProgramStageDataElement>() {
		 public int compare(ProgramStageDataElement i1, ProgramStageDataElement i2) {
			 return i1.getSortOrder().compareTo(i2.getSortOrder());
		 }
	};
	 
	// -------------------------------------------------------------------------
	// Validation
	// -------------------------------------------------------------------------
	
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
	
	private ArrayList<Validate> validList = new ArrayList<Validate>();
    
    public ArrayList<Validate> getValidList()
    {
    	return this.validList;
    }
    
	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------
    
	@Override
	public String execute() {
		
		Program program = programService.getProgram(singleEventId);
		eventName = program.getName();
		
		Patient patient = patientService.getPatient(patientId);
		ProgramStage programStage = program.getProgramStages().iterator().next();
		OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(organisationUnitId);

		programStageDataElements = new ArrayList<ProgramStageDataElement>(programStage.getProgramStageDataElements());
		Collections.sort(programStageDataElements, OrderBySortOrder);
        
		// -------------------------------------------------------------------------
		// Validation
		// -------------------------------------------------------------------------
		
		boolean valid = true;
		validList.clear();
        int i = 0;
		for (ProgramStageDataElement programStageDataElement : programStageDataElements) {
			
			DataElement dataElement = programStageDataElement.getDataElement();
			String value = dynForm.get(i).trim();
			String type = dataElement.getType();
			String numbertype = dataElement.getNumberType();
			
			if(value.isEmpty()) {
					validList.add(new Validate(dataElement.getId(),i18n.getString( "is_required" )));
					valid = false;
				
			} else if(type.equals( DataElement.VALUE_TYPE_DATE)) {
				if(!FormUtils.isDate( value )) {
					validList.add(new Validate(dataElement.getId(), value+ " " +i18n.getString( "is_invalid_date" )));
					valid = false;
				}
				
			} else if(type.equals( DataElement.VALUE_TYPE_STRING)) {
				if(!value.matches("^[\\p{L}|\\s|0-9]*$")) {
					validList.add(new Validate(dataElement.getId(), value+" "+i18n.getString( "is_invalid_string" )));
					valid = false;
				}
				
			} else if(type.equals( DataElement.VALUE_TYPE_INT)) {
				
				if(numbertype.equals(DataElement.VALUE_TYPE_POSITIVE_INT))
				{
					if(!FormUtils.isPositiveInteger( value )) {
						validList.add(new Validate(dataElement.getId(), value+" "+i18n.getString( "is_invalid_positive_integer" )));
						valid = false;
					}
				}
				
				if(numbertype.equals(DataElement.VALUE_TYPE_NEGATIVE_INT))
				{
					if(!FormUtils.isNegativeInteger( value )) {
						validList.add(new Validate(dataElement.getId(), value+" "+i18n.getString( "is_invalid_negative_integer" )));
						valid = false;
					}
				}
				
				if(numbertype.equals(DataElement.VALUE_TYPE_INT))
				{
					if(!FormUtils.isInteger( value )) {
						validList.add(new Validate(dataElement.getId(), value+" "+i18n.getString( "is_invalid_integer" )));
						valid = false;
					}
				}
				
				if(numbertype.equals(DataElement.VALUE_TYPE_NUMBER))
				{
					if(!FormUtils.isNumber( value )) {
						validList.add(new Validate(dataElement.getId(), value+" "+i18n.getString( "is_invalid_number" )));
						valid = false;
					}
				}
			
			} else if(type.equals( DataElement.VALUE_TYPE_BOOL)) {
				if(!FormUtils.isBoolean( value )) {
					validList.add(new Validate(dataElement.getId(), value+" "+i18n.getString( "is_invalid_boolean" )));
					valid = false;
				}
			}
			i++;
		}
		
		if(valid) {

	        if(!update)
	        {
		        ProgramInstance programInstance = new ProgramInstance();
		        programInstance.setEnrollmentDate( new Date() );
		        programInstance.setDateOfIncident( new Date() );
		        programInstance.setProgram( program );
		        programInstance.setPatient( patient );
		        programInstance.setCompleted( false );
	        	programInstanceService.addProgramInstance( programInstance );
	        		
		        ProgramStageInstance programStageInstance = new ProgramStageInstance();
		        programStageInstance.setProgramInstance(programInstance);
		        programStageInstance.setProgramStage(programStage);
		        programStageInstance.setDueDate(new Date());
		        programStageInstance.setExecutionDate(new Date());
		        programStageInstance.setCompleted(false);
				programStageInstanceService.addProgramStageInstance(programStageInstance);
				
				i = 0;
				for (ProgramStageDataElement programStageDataElement : programStageDataElements) {
					DataElement dataElement = programStageDataElement.getDataElement();
				
					PatientDataValue patientDataValue = new PatientDataValue();
					patientDataValue.setDataElement(dataElement);
					patientDataValue.setProgramStageInstance(programStageInstance);
					patientDataValue.setOrganisationUnit(organisationUnit);
					patientDataValue.setValue(dynForm.get(i).trim());
					patientDataValueService.savePatientDataValue(patientDataValue);
					i++;
				}
	        }
	        else
	        {
				ProgramInstance programInstance = programInstanceService.getProgramInstance(instId);
				programStage = program.getProgramStages().iterator().next(); // Fetch first, There exists only 1!
				ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance(programInstance, programStage);
	        	
				i = 0;
				for (ProgramStageDataElement programStageDataElement : programStageDataElements) {
					PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue(programStageInstance, programStageDataElement.getDataElement(), organisationUnit);
					patientDataValue.setValue(dynForm.get(i).trim());
					patientDataValueService.updatePatientDataValue(patientDataValue);
					i++;
				}
	        }

	        if(update){
	        	setResultString("updateSingleEvent");
	        }else{
	        	setResultString("newSingleEvent");
	        }	        	
			return SUCCESS;
		} else {
			return ERROR;
		}
	}
}
