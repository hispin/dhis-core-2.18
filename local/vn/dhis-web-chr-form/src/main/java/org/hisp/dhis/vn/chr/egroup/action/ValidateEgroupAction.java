package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class ValidateEgroupAction extends ActionSupport {

	// -------------------------------------------
	// Input & Output
	// -------------------------------------------

	private String name;

	private Integer sortOrder;

	// -------------------------------------------
	// Getter & Setter
	// -------------------------------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
			message = i18n.getString("name") + " " + i18n.getString("not_null");
			return ERROR;
		}

		if (sortOrder == null || sortOrder.intValue() == 0) {
			message = i18n.getString("sortOrder") + " "
					+ i18n.getString("not_null");
			return ERROR;
		}

		return SUCCESS;
	}
}
