package org.hisp.dhis.sms.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.hisp.dhis.smscommand.SMSCommand;

public class J2MEDataEntryParser
    implements IParser
{
    private SMSCommand smsCommand;

    @Override
    public Map<String, String> parse( String sms )
    {
        String[] keyValuePairs = null;
        if ( sms.indexOf( "#" ) > -1 )
        {
            keyValuePairs = sms.split( "#" );
        }
        else
        {
            keyValuePairs = new String[1];
            keyValuePairs[0] = sms;
        }

        Map<String, String> keyValueMap = new HashMap<String, String>();
        for ( String keyValuePair : keyValuePairs )
        {
            String[] token = keyValuePair.split( Pattern.quote( smsCommand.getSeparator() ) );
            keyValueMap.put( token[0], token[1] );
        }

        return keyValueMap;
    }

    @Override
    public void setSeparator( String separator )
    {
        // TODO Auto-generated method stub
    }

    public SMSCommand getSmsCommand()
    {
        return smsCommand;
    }

    public void setSmsCommand( SMSCommand smsCommand )
    {
        this.smsCommand = smsCommand;
    }
}
