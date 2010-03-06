package org.hisp.dhis.security.listener;

import org.hisp.dhis.useraudit.UserAuditService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.event.authentication.AbstractAuthenticationFailureEvent;
import org.springframework.security.event.authentication.AuthenticationSuccessEvent;
import org.springframework.security.userdetails.UserDetails;

public class AuthenticationListener
    implements ApplicationListener
{
    private UserAuditService userAuditService;
    
    public void setUserAuditService( UserAuditService userAuditService )
    {
        this.userAuditService = userAuditService;
    }

    public void onApplicationEvent( ApplicationEvent applicationEvent )
    {        
        if ( applicationEvent != null && applicationEvent instanceof AuthenticationSuccessEvent )
        {
            AuthenticationSuccessEvent event = (AuthenticationSuccessEvent) applicationEvent;
            
            userAuditService.registerLoginSuccess( ((UserDetails) event.getAuthentication().getPrincipal()).getUsername() );
        }
        else if ( applicationEvent != null && applicationEvent instanceof AbstractAuthenticationFailureEvent )
        {
            AbstractAuthenticationFailureEvent event = (AbstractAuthenticationFailureEvent) applicationEvent;
            
            userAuditService.registerLoginFailure( (String) event.getAuthentication().getPrincipal() );
        }
    }
}
