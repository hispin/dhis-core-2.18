package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.EgroupService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class DeleteEgroupAction extends ActionSupport {
	// -----------------------------------------------------------------------------------------------
	// Dependency
	// -----------------------------------------------------------------------------------------------

	private EgroupService egroupService;

	// -----------------------------------------------------------------------------------------------
	// Input && Output
	// -----------------------------------------------------------------------------------------------

	private Integer id;

	// -----------------------------------------------------------------------------------------------
	// Getters && Setters
	// -----------------------------------------------------------------------------------------------

	public void setEgroupService(EgroupService egroupService) {
		this.egroupService = egroupService;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	// -----------------------------------------------------------------------------------------------
	// Implement
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {

		try {

			egroupService.deleteEgroup(id.intValue());

			message = i18n.getString("success");

			return SUCCESS;

		} catch (Exception ex) {

			message = i18n.getString("error_delete");

			ex.printStackTrace();
		}

		return ERROR;

	}

}
