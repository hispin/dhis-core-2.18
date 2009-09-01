package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import com.opensymphony.xwork.Action;

public class GetFormById implements Action {

	// -----------------------------------------------------------------------------------------------
	// Dependencies
	// -----------------------------------------------------------------------------------------------

	private FormService formService;

	// -----------------------------------------------------------------------------------------------
	// Input && Output
	// -----------------------------------------------------------------------------------------------

	private Form form;

	private Integer id;

	// -----------------------------------------------------------------------------------------------
	// Getters && Setter
	// -----------------------------------------------------------------------------------------------

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public FormService getFormService() {
		return formService;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public Form getForm() {
		return form;
	}

	// -----------------------------------------------------------------------------------------------
	// Implement
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {

		form = formService.getForm(id.intValue());
		System.out.print("\n\n\n Form : " + form.getElements() + " with ID = "
				+ id.intValue());
		return SUCCESS;
	}

}
