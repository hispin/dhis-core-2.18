package org.hisp.dhis.security.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.useraudit.UserAuditService;
import org.springframework.security.Authentication;
import org.springframework.security.ui.logout.LogoutHandler;
import org.springframework.security.userdetails.UserDetails;

public class UserAuditLogoutFilter
    implements LogoutHandler
{
    private UserAuditService userAuditService;

    public void setUserAuditService( UserAuditService userAuditService )
    {
        this.userAuditService = userAuditService;
    }

    public void logout( HttpServletRequest request, HttpServletResponse response, Authentication authentication )
    {
        String username = (( UserDetails ) authentication.getPrincipal()).getUsername();
        
        userAuditService.registerLogout( username );
    }
}
