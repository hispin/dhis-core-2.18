package org.hisp.dhis.useraudit;

public interface UserAuditService
{
    final int TIMEFRAME_NUMBER_OF_HOURS = 1;
    final int MAX_NUMBER_OF_ATTEMPTS = 3;
    
    void registerLoginSuccess( String username );
    
    void registerLogout( String username );
    
    void registerLoginFailure( String username );
}
