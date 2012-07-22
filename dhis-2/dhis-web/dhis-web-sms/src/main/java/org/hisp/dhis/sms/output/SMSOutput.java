package org.hisp.dhis.sms.output;

public interface SMSOutput
{

    public void sendSMS( String message, String mobileNumber );

}
