package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import com.opensymphony.xwork.Action;

public class ListFormAction implements Action{


	// -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

	private FormService formService;
	
	// -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

	private Collection<Form> forms;
	
	// -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

	public Collection<Form> getForms() {
		return forms;
	}

	public void setForms(Collection<Form> forms) {
		this.forms = forms;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}
	
	// -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

	public String execute() throws Exception{
		
		forms = formService.getAllForms();
		
		return SUCCESS;
	}
}
