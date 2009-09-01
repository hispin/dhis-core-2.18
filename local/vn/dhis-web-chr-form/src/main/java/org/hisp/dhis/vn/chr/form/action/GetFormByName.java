package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;

import com.opensymphony.xwork.Action;

public class GetFormByName implements Action {

	// -----------------------------------------------------------------------------------------------
	// Dependencies
	// -----------------------------------------------------------------------------------------------

	private FormService formService;

	// -----------------------------------------------------------------------------------------------
	// Input && Output
	// -----------------------------------------------------------------------------------------------

	private String name;

	private Form form;

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

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	// -----------------------------------------------------------------------------------------------
	// Implement
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {

		form = formService.getFormByName(name);

		return SUCCESS;
	}

}
