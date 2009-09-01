package org.hisp.dhis.vn.chr.configuration.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.options.formconfiguration.FormConfigurationManager;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class SetNumberOfRecordsAction extends ActionSupport {

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------
	private FormConfigurationManager formConfigurationManager;

	// -------------------------------------------------------------------------
	// Input && Output
	// -------------------------------------------------------------------------
	private String numberOfRecords;

	// -------------------------------------------------------------------------
	// Getters && Setters
	// -------------------------------------------------------------------------

	public String getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(String numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	public void setFormConfigurationManager(
			FormConfigurationManager formConfigurationManager) {
		this.formConfigurationManager = formConfigurationManager;
	}

	// --------------------------------------------------------------------
	// Implements
	// --------------------------------------------------------------------

	public String execute() throws Exception {
		formConfigurationManager.setNumberOfRecords(numberOfRecords);

		message = i18n.getString("success");

		return SUCCESS;
	}

}
