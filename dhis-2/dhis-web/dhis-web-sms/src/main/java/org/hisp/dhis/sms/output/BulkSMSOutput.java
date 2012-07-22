package org.hisp.dhis.sms.output;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.hisp.dhis.sms.SmsConfigurationManager;
import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class BulkSMSOutput
    implements SMSOutput
{

    @Autowired
    private SmsConfigurationManager smsConfigurationManager;

    @Override
    public void sendSMS( String message, String mobileNumber )
    {

        BulkSmsGatewayConfig gatewayConfig = (BulkSmsGatewayConfig) smsConfigurationManager
            .checkInstanceOfGateway( BulkSmsGatewayConfig.class );

        try
        {
            // Construct data
            String data = "";
            /*
             * Note the suggested encoding for certain parameters, notably the
             * username, password and especially the message. ISO-8859-1 is
             * essentially the character set that we use for message bodies,
             * with a few exceptions for e.g. Greek characters. For a full list,
             * see:
             * http://bulksms.vsms.net/docs/eapi/submission/character_encoding/
             */
            data += "username=" + URLEncoder.encode( gatewayConfig.getUsername(), "ISO-8859-1" );
            data += "&password=" + URLEncoder.encode( gatewayConfig.getPassword(), "ISO-8859-1" );
            data += "&message=" + URLEncoder.encode( message, "ISO-8859-1" );
            data += "&want_report=1";
            data += "&msisdn=" + mobileNumber;

            // Send data
            URL url = new URL( "http://www.bulksms.co.uk:5567/eapi/submission/send_sms/2/2.0" );
            /*
             * If your firewall blocks access to port 5567, you can fall back to
             * port 80: URL url = new
             * URL("http://bulksms.vsms.net/eapi/submission/send_sms/2/2.0");
             * (See FAQ for more details.)
             */

            URLConnection conn = url.openConnection();
            conn.setDoOutput( true );
            OutputStreamWriter wr = new OutputStreamWriter( conn.getOutputStream() );
            wr.write( data );
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
            String line;
            while ( (line = rd.readLine()) != null )
            {
                // Print the response output...
                System.out.println( line );
            }
            wr.close();
            rd.close();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void setSmsConfigurationManager( SmsConfigurationManager smsConfigurationManager )
    {
        this.smsConfigurationManager = smsConfigurationManager;
    }

}
