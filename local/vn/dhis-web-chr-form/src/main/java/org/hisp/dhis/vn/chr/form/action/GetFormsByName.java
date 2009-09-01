package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;

import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;

import com.opensymphony.xwork.Action;

public class GetFormsByName implements Action {

	// -----------------------------------------------------------------------------------------------
	// Dependencies
	// -----------------------------------------------------------------------------------------------

	private FormService formService;

	// -----------------------------------------------------------------------------------------------
	// Input && Output
	// -----------------------------------------------------------------------------------------------

	private String name;

	private Collection<Form> forms;

	// -----------------------------------------------------------------------------------------------
	// Getters && Setter
	// -----------------------------------------------------------------------------------------------

	public String getName() {
		return name;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Form> getForms() {
		return forms;
	}

	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}

	// -----------------------------------------------------------------------------------------------
	// Implement
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {

		forms = formService.getFormsByName(name);
		//		
		return SUCCESS;
	}

}
