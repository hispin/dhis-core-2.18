package org.hisp.dhis.vn.chr.egroup.action;

import java.util.Collection;

import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import com.opensymphony.xwork2.Action;

public class ListEgroupAction implements Action {

	// -----------------------------------------------------------------------------------------------
	// Dependencies
	// -----------------------------------------------------------------------------------------------

	private FormService formService;

	// -----------------------------------------------------------------------------------------------
	// Input && Output
	// -----------------------------------------------------------------------------------------------

	private Integer formID;

	private Form form;

	private Collection<Egroup> egroups;

	// -----------------------------------------------------------------------------------------------
	// Getter && Setter
	// -----------------------------------------------------------------------------------------------

	public void setFormID(Integer formID) {
		this.formID = formID;
	}

	public Collection<Egroup> getEgroups() {
		return this.egroups;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public Form getForm() {
		return form;
	}

	// -----------------------------------------------------------------------------------------------
	// Implement
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {

		form = formService.getForm(formID.intValue());

		egroups = form.getEgroups();

		return SUCCESS;
	}

}
