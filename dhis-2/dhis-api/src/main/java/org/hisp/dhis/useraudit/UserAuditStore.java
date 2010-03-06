package org.hisp.dhis.useraudit;

import java.util.Collection;
import java.util.Date;

public interface UserAuditStore
{
    final String ID = UserAuditStore.class.getName();
    
    void saveLoginFailure( LoginFailure login );
    
    Collection<LoginFailure> getAllLoginFailures();
    
    void deleteLoginFailures( String username );
    
    int getLoginFailures( String username, Date date );
}
