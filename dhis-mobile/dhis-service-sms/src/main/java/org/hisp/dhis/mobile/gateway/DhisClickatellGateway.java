package org.hisp.dhis.mobile.gateway;


import org.smslib.http.ClickatellHTTPGateway;

public class DhisClickatellGateway extends ClickatellHTTPGateway
{
    public DhisClickatellGateway( String gatewayId, String api_id, String username, String password )
    {
        super( gatewayId, api_id, username, password );
    }
    
    
}
