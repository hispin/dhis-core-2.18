package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;
import com.opensymphony.xwork.Action;


public class CreateTableByFormAction implements Action{
	
	// -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------
	
	private FormManager formManager;
	
	private FormService formService;
	
	// -----------------------------------------------------------------------------------------------
    // Input & Output
    // -----------------------------------------------------------------------------------------------
	
	private Integer id;
	
	// -----------------------------------------------------------------------------------------------
    // Getters & Setters
    // -----------------------------------------------------------------------------------------------

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setFormManager(FormManager formManager) {
		this.formManager = formManager;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}
	
	// -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {
		
		formManager.createTable(formService.getForm(id.intValue()));
		
		return SUCCESS;
	}

}
