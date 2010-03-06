package org.hisp.dhis.useraudit;

import java.util.Collection;
import java.util.Date;

public interface UserAuditStore
{
    final String ID = UserAuditStore.class.getName();
    
    void saveFailedLogin( FailedLogin login );
    
    Collection<FailedLogin> getAllFailedLogins();
    
    int getFailedLogins( String username, Date date );
}
