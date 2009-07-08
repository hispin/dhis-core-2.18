package org.hisp.dhis.sandbox.mobileimport.api;

import java.util.List;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.User;


public interface MobileImportService
{

  public MobileImportParameters getParametersFromXML( String fileName ) throws Exception;
  
  public User getUserInfo( String mobileNumber );
  
  public Period getPeriodInfo( String startDate ) throws Exception;
  
  public List<String> getImportFiles( );
  
  public void moveImportedFile( String fileName );
  
  public void moveFailedFile( String fileName );
    
}
