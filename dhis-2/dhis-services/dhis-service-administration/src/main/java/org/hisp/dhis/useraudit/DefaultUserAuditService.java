package org.hisp.dhis.useraudit;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

public class DefaultUserAuditService
    implements UserAuditService
{
    private static final Log log = LogFactory.getLog( DefaultUserAuditService.class );
    
    private UserAuditStore userAuditStore;
    
    public void setUserAuditStore( UserAuditStore userAuditStore )
    {
        this.userAuditStore = userAuditStore;
    }

    public void registerLoginSuccess( String username )
    {
        log.info( "User login success: '" + username + "'" );        
    }

    public void registerLogout( String username )
    {
        log.info( "User logout: '" + username + "'" );
    }

    @Transactional
    public void registerLoginFailure( String username )
    {
        log.info( "User login failure: '" + username + "'" );
        
        userAuditStore.saveLoginFailure( new LoginFailure( username, new Date() ) );
        
        int no = userAuditStore.getLoginFailures( username, getDate() );
        
        if ( no >= MAX_NUMBER_OF_ATTEMPTS )
        {
            log.info( "Max number of login attempts exceeded: '" + username + "'" );
            
            userAuditStore.deleteLoginFailures( username );
        }
    }
    
    private Date getDate()
    {
        Calendar cal = Calendar.getInstance();        
        cal.clear();
        cal.add( Calendar.HOUR, TIMEFRAME_NUMBER_OF_HOURS * -1 );
        
        return cal.getTime();
    }
}
