/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.sms.outbound;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.message.MessageSender;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.smslib.SmsLibService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.user.UserSetting;
import org.hisp.dhis.user.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Nguyen Kim Lai
 * 
 * @version SmsSender.java 10:29:11 AM Apr 16, 2013 $
 */
public class SmsSender
    implements MessageSender
{
    private static final Log log = LogFactory.getLog( SmsSender.class );

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private static final int MAX_HEX_CHAR = 280;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    private OutboundSmsService outboundSmsService;

    public void setOutboundSmsService( OutboundSmsService outboundSmsService )
    {
        this.outboundSmsService = outboundSmsService;
    }

    @Autowired
    private OutboundSmsTransportService transportService;

    /**
     * Note this methods is invoked asynchronously.
     */
    // @Async
    @Override
    public String sendMessage( String subject, String text, User sender, Set<User> users, boolean forceSend )
    {
        String message = null;
        
        if ( transportService == null || SmsLibService.gatewayMap == null )
        {
            message = "No gateway";
            return message;
        }

        Set<User> toSendList = new HashSet<User>();

        String gatewayId = transportService.getDefaultGateway();
        
        if ( gatewayId != null && !gatewayId.trim().isEmpty() )
        {
            for ( User user : users )
            {
                if ( currentUserService.getCurrentUser() != null )
                {
                    if ( !currentUserService.getCurrentUser().equals( user ) )
                    {
                        if ( isQualifiedReceiver( user ) )
                        {
                            toSendList.add( user );
                        }
                    }
                }
                else if ( currentUserService.getCurrentUser() == null )
                {
                    if ( isQualifiedReceiver( user ) )
                    {
                        toSendList.add( user );
                    }
                }
            }

            Set<String> phoneNumbers = null;

            if ( outboundSmsService != null || outboundSmsService.isEnabled() )
            {
                phoneNumbers = getRecipientsPhoneNumber( toSendList );
                /*if ( SmsLibService.gatewayMap.get( "bulk_gw" ).equals( gatewayId ) )
                {
                    try
                    {
                        String hexText = toHex( text.getBytes( "unicode" ) );
                        if ( hexText.length() > MAX_HEX_CHAR )
                        {
 
                            List<String> splitTextList = new ArrayList<String>();
                            splitTextList = splitLongHexString( hexText, text, splitTextList );
                            for ( String each: splitTextList )
                            {
                                //text = createMessage( subject, each, sender );
                                
                                if ( !phoneNumbers.isEmpty() && phoneNumbers.size() > 0 )
                                {
                                    //message = sendMessage( text, phoneNumbers, gatewayId );
                                    message = sendMessage( each, phoneNumbers, gatewayId );
                                }
                            }
                        }
                        else
                        {
                            //text = createMessage( subject, text, sender );
                            
                            if ( !phoneNumbers.isEmpty() && phoneNumbers.size() > 0 )
                            {
                                message = sendMessage( text, phoneNumbers, gatewayId );
                            }
                        }
                    }
                    catch ( UnsupportedEncodingException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                else*/
                {
                    text = createMessage( subject, text, sender );
                    
                    if ( !phoneNumbers.isEmpty() && phoneNumbers.size() > 0 )
                    {
                        message = sendMessage( text, phoneNumbers, gatewayId );
                    }
                }
            }
        }
        
        return message;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private boolean isQualifiedReceiver( User user )
    {
        // if receiver is raw number
        if ( user.getFirstName() == null )
        {
            return true;
        }
        // if receiver is user
        else
        {
            UserSetting userSetting = userService
                .getUserSetting( user, UserSettingService.KEY_MESSAGE_SMS_NOTIFICATION );
            if ( userSetting != null )
            {
                boolean sendSMSNotification = (Boolean) userSetting.getValue();
                if ( sendSMSNotification == true )
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
    }

    private String createMessage( String subject, String text, User sender )
    {
        String name = "DHIS";

        if ( sender != null )
        {
            name = sender.getUsername();
        }

        if ( subject == null || subject.isEmpty() )
        {
            subject = "";
        }
        else
        {
            subject = " - " + subject;
        }

        text = name + subject + ": " + text;

        // Simplistic cut off 160 characters
        int length = text.length();

        return (length > 160) ? text.substring( 0, 157 ) + "..." : text;
    }

    private Set<String> getRecipientsPhoneNumber( Set<User> users )
    {
        Set<String> recipients = new HashSet<String>();

        for ( User user : users )
        {
            String phoneNumber = user.getPhoneNumber();

            if ( phoneNumber != null && !phoneNumber.trim().isEmpty() )
            {
                recipients.add( phoneNumber );
            }
        }

        return recipients;
    }

    private String sendMessage( String text, Set<String> recipients, String gateWayId )
    {
        String message = null;
        OutboundSms sms = new OutboundSms();
        sms.setMessage( text );
        sms.setRecipients( recipients );

        try
        {
            message = outboundSmsService.sendMessage( sms, gateWayId );
        }
        catch ( SmsServiceException e )
        {
            message = "Unable to send message through sms: " + sms + e.getCause().getMessage();

            log.warn( "Unable to send message through sms: " + sms, e );
        }

        return message;
    }

    public String toHex( byte[] buf )
    {
        char[] chars = new char[2 * buf.length];
        for ( int i = 0; i < buf.length; ++i )
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String( chars );
    }

    public List<String> splitLongHexString( String hexString, String message, List<String> result )
    {

        String firstTempHex = null;
        String secondTempHex = null;
        String firstTempString = null;
        String secondTempString = null;
        int indexToCut;

        firstTempHex = hexString.substring( 0, MAX_HEX_CHAR );

        int lastSpaceIndex = firstTempHex.lastIndexOf( "0020" );

        firstTempHex = firstTempHex.substring( 0, lastSpaceIndex );
        indexToCut = (firstTempHex.length() - 4) / 4;
        firstTempString = message.substring( 0, indexToCut );
        result.add( firstTempString );

        secondTempHex = "feff" + hexString.substring( firstTempHex.length() + 4, hexString.length() );
        secondTempString = message.substring( indexToCut + 1, message.length() );
        if ( secondTempHex.length() <= MAX_HEX_CHAR )
        {
            result.add( secondTempString );
            return result;
        }
        else
        {
            return splitLongHexString( secondTempHex, secondTempString, result );
        }
    }
}
