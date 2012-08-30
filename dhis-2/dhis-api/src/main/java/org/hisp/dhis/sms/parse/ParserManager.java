package org.hisp.dhis.sms.parse;

public interface ParserManager
{
    public void parse( String sender, String message )
        throws SMSParserException;
}
