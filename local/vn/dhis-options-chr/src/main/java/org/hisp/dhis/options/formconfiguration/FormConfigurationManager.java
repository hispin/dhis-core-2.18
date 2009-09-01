package org.hisp.dhis.options.formconfiguration;

/**
 * @author Chau Thu Tran
 * 
 */

public interface FormConfigurationManager {
	 
	 public void setImageDirectoryOnServer( String imageDirectoryOnServer );
	 
	 public String getImageDirectoryOnServer();
	 
	 public void setNumberOfRecords( String numberOfRecords );
	 
	 public String getNumberOfRecords();
}
