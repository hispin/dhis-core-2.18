package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;


public class ValidateEgroupAction extends ActionSupport {
	
	// -------------------------------------------
	// Input & Output
	// -------------------------------------------

	private String name;

	private Integer sortOrder;

	private String message;

	// -------------------------------------------
	// Getter & Setter
	// -------------------------------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}


	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	// -------------------------------------------
	// Implement
	// -------------------------------------------

	public String execute() throws Exception {

		if (name == null || name.trim().length() == 0) {
			message = i18n.getString("name_is_null");
			return ERROR;
		}
		
		if (sortOrder == null || sortOrder.intValue() == 0) {
			message = i18n.getString("sortOrder_is_null");
			return ERROR;
		}
		
		return SUCCESS;
	}
}
