package org.hisp.dhis.api.sms;

import static org.hisp.dhis.user.UserSettingService.KEY_MESSAGE_SMS_NOTIFICATION;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hisp.dhis.mobile.service.SmsMessageSender;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.junit.Test;

public class SmsMessageSenderTest
{

    SmsMessageSender smsMessageSender;

    @Test
    public void testMessageSender()
    {

        smsMessageSender = new SmsMessageSender();

        OutboundSmsService outboundSmsService = mock( OutboundSmsService.class );
        when( outboundSmsService.isSmsServiceAvailable() ).thenReturn( true );

        UserService userService = mock( UserService.class );
        final User user = getUser();
        Map<User, Serializable> settings = getUserSettings( user );
        when( userService.getUserSettings( KEY_MESSAGE_SMS_NOTIFICATION, false ) ).thenReturn( settings );

        smsMessageSender.setOutboundSmsService( outboundSmsService );
        smsMessageSender.setUserService( userService );
        smsMessageSender.sendMessage( "Hello", "hello", user, getUserSet( user ));

        verify(outboundSmsService).isSmsServiceAvailable();
        verify( userService ).getUserSettings( KEY_MESSAGE_SMS_NOTIFICATION, false );
        verify( outboundSmsService ).sendMessage( eq("From null, Hello: hello"), eq("222222") );
    }

    private HashSet<User> getUserSet( final User user )
    {
        return new HashSet<User>(){{add( user );}};
    }

    private Map<User, Serializable> getUserSettings( final User user )
    {
        Map<User, Serializable> settings = new HashMap<User, Serializable>(){{put(user, true);}};
        return settings;
    }

    private User getUser()
    {
        final User user = new User();
        user.setId( 1 );
        user.setPhoneNumber( "222222" );
        user.setFirstName( "firstName" );
        user.setSurname( "surname" );
        return user;
    }
}
