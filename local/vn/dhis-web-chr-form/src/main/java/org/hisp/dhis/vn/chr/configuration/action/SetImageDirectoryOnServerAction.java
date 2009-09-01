package org.hisp.dhis.vn.chr.configuration.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.options.formconfiguration.FormConfigurationManager;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class SetImageDirectoryOnServerAction extends ActionSupport {

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private FormConfigurationManager formConfigurationManager;

	// -------------------------------------------------------------------------
	// Input && Output
	// -------------------------------------------------------------------------
	private String imageDirectoryOnServer;

	// -------------------------------------------------------------------------
	// Getters && Setters
	// -------------------------------------------------------------------------

	public void setImageDirectoryOnServer(String imageDirectoryOnServer) {
		this.imageDirectoryOnServer = imageDirectoryOnServer;
	}

	public String getImageDirectoryOnServer() {
		return imageDirectoryOnServer;
	}

	public void setFormConfigurationManager(
			FormConfigurationManager formConfigurationManager) {
		this.formConfigurationManager = formConfigurationManager;
	}

	// --------------------------------------------------------------------
	// Implements
	// --------------------------------------------------------------------

	public String execute() throws Exception {
		formConfigurationManager
				.setImageDirectoryOnServer(imageDirectoryOnServer);

		message = i18n.getString("success");

		return SUCCESS;
	}

}
