package org.hisp.dhis.vn.chr.form.action;

import java.util.Collection;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import com.opensymphony.xwork.Action;

public class GetVisibleFormsAction implements Action{

	// -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

	private FormService formService;
	
	// -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

	private Collection<Form> visibleforms;
	
	// -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------

	public Collection<Form> getVisibleforms() {
		return visibleforms;
	}

	public void setVisibleforms(Collection<Form> visibleforms) {
		this.visibleforms = visibleforms;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	// -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

	public String execute() throws Exception{
		
		visibleforms = formService.getVisibleForms("yes");
		
		return SUCCESS;
	}
}
