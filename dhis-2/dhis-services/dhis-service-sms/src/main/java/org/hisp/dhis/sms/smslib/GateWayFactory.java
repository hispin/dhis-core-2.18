package org.hisp.dhis.sms.smslib;

import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;
import org.hisp.dhis.sms.config.ClickatellGatewayConfig;
import org.hisp.dhis.sms.config.ModemGatewayConfig;
import org.hisp.dhis.sms.config.SmsGatewayConfig;
import org.smslib.AGateway;
import org.smslib.AGateway.Protocols;
import org.smslib.http.BulkSmsHTTPGateway;
import org.smslib.http.ClickatellHTTPGateway;
import org.smslib.modem.SerialModemGateway;

public class GateWayFactory
{

    public AGateway create( SmsGatewayConfig config )
    {
        if ( config instanceof BulkSmsGatewayConfig )
            return createBulkSmsGateway( (BulkSmsGatewayConfig) config );
        else if ( config instanceof ClickatellGatewayConfig )
            return createClickatellGateway( (ClickatellGatewayConfig) config );
        else if ( config instanceof ModemGatewayConfig )
            return createModemGateway( (ModemGatewayConfig) config );

        throw new SmsServiceException( "Gateway config of unknown type: " + config.getClass().getName() );

    }

    public AGateway createBulkSmsGateway( BulkSmsGatewayConfig config )
    {
        BulkSmsHTTPGateway gateway = new BulkSmsHTTPGateway( "bulksms.http.1", config.getUsername(),
            config.getPassword() );
        gateway.setOutbound( true );
        gateway.setInbound( false );
        return gateway;
    }

    public AGateway createModemGateway( ModemGatewayConfig c )
    {

        // TODO: DETECT MODEM CLASS AND INSTANTIATE
        SerialModemGateway gateway = new SerialModemGateway( c.getName(), c.getPort(), c.getBaudRate(),
            c.getManufacturer(), c.getModel() );

        if ( c.getSimMemLocation() != null )
        {
            gateway.getATHandler().setStorageLocations( c.getSimMemLocation() );
        }

        if ( c.getPin() != null )
        {
            gateway.setSimPin( c.getPin() );
        }

        gateway.setProtocol( Protocols.PDU );
        gateway.setInbound( c.isInbound() );
        gateway.setOutbound( c.isOutbound() );

        return gateway;
    }

    public AGateway createClickatellGateway( ClickatellGatewayConfig c )
    {
        ClickatellHTTPGateway gateway = new ClickatellHTTPGateway( c.getName(), c.getApiId(), c.getUsername(),
            c.getPassword() );
        gateway.setOutbound( true );
        gateway.setInbound( false );
        return gateway;
    }


}
