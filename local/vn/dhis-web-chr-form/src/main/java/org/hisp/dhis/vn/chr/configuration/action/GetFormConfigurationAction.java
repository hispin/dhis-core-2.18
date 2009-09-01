package org.hisp.dhis.vn.chr.configuration.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.options.formconfiguration.FormConfigurationManager;

import com.opensymphony.xwork.Action;

public class GetFormConfigurationAction implements Action {

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------
	private FormConfigurationManager formConfigurationManager;

	// -------------------------------------------------------------------------
	// Input && Output
	// -------------------------------------------------------------------------

	private String imageDirectoryOnServer;

	private String numberOfRecords;

	// -------------------------------------------------------------------------
	// Getters && Setters
	// -------------------------------------------------------------------------

	public void setFormConfigurationManager(
			FormConfigurationManager formConfigurationManager) {
		this.formConfigurationManager = formConfigurationManager;
	}

	public String getImageDirectoryOnServer() {
		return imageDirectoryOnServer;
	}

	public void setImageDirectoryOnServer(String imageDirectoryOnServer) {
		this.imageDirectoryOnServer = imageDirectoryOnServer;
	}

	public String getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(String numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	// -------------------------------------------------------------------------
	// Action implementation
	// -------------------------------------------------------------------------

	public String execute() throws Exception {
		
		imageDirectoryOnServer = formConfigurationManager
				.getImageDirectoryOnServer();

		numberOfRecords = formConfigurationManager.getNumberOfRecords();

		return SUCCESS;
	}

}
