package org.hisp.dhis.options.formconfiguration;

import org.hisp.dhis.options.SystemSetting;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.user.NoCurrentUserException;
import org.hisp.dhis.user.UserSettingService;

public class DefaultFormConfigurationManager implements FormConfigurationManager{

	// --------------------------------------------------------------------
    // Dependencies
    // --------------------------------------------------------------------
	private SystemSettingManager systemSettingManager;

	public void setSystemSettingManager(SystemSettingManager systemSettingManager) {
		this.systemSettingManager = systemSettingManager;
	}

	// --------------------------------------------------------------------
    // Input && Output
    // --------------------------------------------------------------------
	private static final String IMAGE_DIRECTORY_ON_SERVER = "curImageDirectoryOnServer";
	
	private static final String NUMBER_OF_RECORDS = "curNumberOfRecords";
	
	// --------------------------------------------------------------------
	// ------------------------ Implements --------------------------------
	// --------------------------------------------------------------------
	
	// Image directory on server
	public String getImageDirectoryOnServer() {
		
		return (String) systemSettingManager.getSystemSetting( IMAGE_DIRECTORY_ON_SERVER);
		
	}

	public void setImageDirectoryOnServer(String imageDirectoryOnServer) {
		
		systemSettingManager.saveSystemSetting(IMAGE_DIRECTORY_ON_SERVER, imageDirectoryOnServer );
	}
	
	//--------------------------------------------------------------------------------------------
	// Number of records
	//--------------------------------------------------------------------------------------------
	
	public String getNumberOfRecords() {
		
		return (String) systemSettingManager.getSystemSetting( NUMBER_OF_RECORDS);
	}

	public void setNumberOfRecords( String numberOfRecords ) {
		
		systemSettingManager.saveSystemSetting( NUMBER_OF_RECORDS, numberOfRecords );
		
	}
}
