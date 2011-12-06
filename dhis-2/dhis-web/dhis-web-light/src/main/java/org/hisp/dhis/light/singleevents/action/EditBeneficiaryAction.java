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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patient.comparator.PatientAttributeGroupSortOrderComparator;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Group1 Fall 2011
 */
public class EditBeneficiaryAction implements Action  {
	
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
    
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    // -------------------------------------------------------------------------
	// Input & Output
	// -------------------------------------------------------------------------   
    
    private Integer patientId;
    
    public void setPatientId( Integer patientId )
    {
    	this.patientId = patientId;
    }
    
    public Integer getPatientId()
    {
    	return patientId;
    }
    
    private Patient patient;
    
    public Patient getPatient(){
    	return patient;
    }
    
    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
    	return organisationUnit;
    }
    
    private Integer organisationUnitId;
    
    public Integer getOrganisationUnitId(){
    	return organisationUnitId;
    }

    private String birthDate;
    
    public String getBirthDate()
    {
        return birthDate;
    }
    
    private String registrationDate;
    
    public String getRegistrationDate(){
    	return registrationDate;
    }
    
    private Integer singleEventId;
    
    public void setSingleEventId( Integer singleEventId){
    	this.singleEventId = singleEventId;
    }
    
    public Integer getSingleEventId(){
    	return this.singleEventId;
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
        
	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------
    
	@Override
	public String execute() {
		
		patient = patientService.getPatient(patientId);
        
        organisationUnit = patient.getOrganisationUnit();
        
        organisationUnitId = organisationUnit.getId();
        
        Date date = patient.getBirthDate();
        SimpleDateFormat DFyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
        
        birthDate = DFyyyyMMdd.format(date);
        
        date = patient.getRegistrationDate();
        registrationDate = DFyyyyMMdd.format(date);
        
        noGroupAttributes = patientAttributeService.getPatientAttributesNotGroup();

        attributeGroups = new ArrayList<PatientAttributeGroup>( patientAttributeGroupService
            .getAllPatientAttributeGroups() );
        Collections.sort( attributeGroups, new PatientAttributeGroupSortOrderComparator() );
        
        int size = noGroupAttributes.size();
        
        for (PatientAttributeGroup patientAttributeGroup : attributeGroups) {
        	size += patientAttributeGroup.getAttributes().size();
        }

        dynForm = new String[size];
        
        int i = 0;
        
        for (PatientAttributeGroup patientAttributeGroup : attributeGroups) {
        	List<PatientAttribute> patientAttributeList = patientAttributeGroup.getAttributes();
        	for(PatientAttribute patientAttribute : patientAttributeList){
        		PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );

        		try{
        			if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttribute.getValueType() ) ){
        				PatientAttributeOption option = attributeValue.getPatientAttributeOption();
        				Integer id = option.getId();
        				dynForm[i] = id.toString();
        			}else if (attributeValue.getValue().equals("")){
        				dynForm[i] = "";
        			}else{
        				dynForm[i] = attributeValue.getValue();
        			}
        		}catch (NullPointerException e){
        			dynForm[i] = "";
        		}
        		i++;
        	}
        }
        
        for (PatientAttribute patientAttribute : noGroupAttributes){
    		PatientAttributeValue attributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );

    		try{
    			if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttribute.getValueType() ) ){
    				PatientAttributeOption option = attributeValue.getPatientAttributeOption();
    				Integer id = option.getId();
    				dynForm[i] = id.toString();
    			}else if (attributeValue.getValue().equals("")){
    				dynForm[i] = "";
    			}else{
    				dynForm[i] = attributeValue.getValue();
    			}
    		}catch (NullPointerException e){
    			dynForm[i] = "";
    		}
    		i++;
        }
        
        
		return SUCCESS;
	}
}
