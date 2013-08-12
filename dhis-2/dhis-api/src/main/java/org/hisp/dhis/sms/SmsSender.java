package org.hisp.dhis.sms;

import java.util.Set;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.user.User;

public interface SmsSender
{
    String sendMessage( OutboundSms sms, String gatewayId )
        throws SmsServiceException;

    String sendMessage( OutboundSms sms )
        throws SmsServiceException;

    String sendMessage( String message, String phoneNumber )
        throws SmsServiceException;

    String sendMessage( String subject, String text, User sender, Set<User> users, boolean forceSend );
}
