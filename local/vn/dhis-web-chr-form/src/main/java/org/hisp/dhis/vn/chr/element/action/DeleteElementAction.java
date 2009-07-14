package org.hisp.dhis.vn.chr.element.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class DeleteElementAction extends ActionSupport {

	// -----------------------------------------------------------------------------------------------
	// Dependency
	// -----------------------------------------------------------------------------------------------

	private ElementService elementService;

	// -----------------------------------------------------------------------------------------------
	// Input && Output
	// -----------------------------------------------------------------------------------------------

	private Integer id;

	// -----------------------------------------------------------------------------------------------
	// Getters && Setters
	// -----------------------------------------------------------------------------------------------

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	// -----------------------------------------------------------------------------------------------
	// Implement
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {
		
		elementService.deleteElement(id.intValue());

		message = i18n.getString("success");
		
		return SUCCESS;
	}

}
