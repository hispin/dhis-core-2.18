package org.hisp.dhis.useraudit;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultUserAuditService
    implements UserAuditService
{
    private static final Log log = LogFactory.getLog( DefaultUserAuditService.class );
    
    private UserAuditStore userAuditStore;
    
    public void setUserAuditStore( UserAuditStore userAuditStore )
    {
        this.userAuditStore = userAuditStore;
    }

    public void registerLogin( String username )
    {
        log.info( "User login: '" + username + "'" );        
    }

    public void registerLogout( String username )
    {
        log.info( "User logout: '" + username + "'" );
    }

    public void registerLoginFailed( String username )
    {
        log.info( "User login failed: '" + username + "'" );
        
        userAuditStore.saveFailedLogin( new FailedLogin( username, new Date() ) );
        
        int no = userAuditStore.getFailedLogins( username, getDate() );
        
        if ( no > MAX_NUMBER_OF_ATTEMPTS )
        {
            log.info( "Max number of login attempts exceeded: '" + username + "'" );
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
