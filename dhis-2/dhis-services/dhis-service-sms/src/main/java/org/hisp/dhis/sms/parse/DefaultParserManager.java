package org.hisp.dhis.sms.parse;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.sms.DHISMessageAlertListener;
import org.hisp.dhis.sms.DataValueSMSListener;
import org.hisp.dhis.sms.J2MEDataValueSMSListener;
import org.hisp.dhis.sms.UnregisteredSMSListener;
import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsService;
import org.hisp.dhis.sms.incoming.SmsMessageStatus;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.hisp.dhis.sms.outbound.OutboundSmsTransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Christian and Magnus
 */
public class DefaultParserManager
    implements ParserManager
{
    public static final String ANONYMOUS_USER_NAME = "Anonymous";

    private static final Log log = LogFactory.getLog( DefaultParserManager.class );

    private J2MEDataValueSMSListener j2meDataValueSMSListener;

    private DataValueSMSListener dataValueSMSListener;

    private UnregisteredSMSListener unregisteredSMSListener;

    private DHISMessageAlertListener dhisMessageAlertListener;

    private OutboundSmsService outboundSmsService;

    @Autowired
    private OutboundSmsTransportService transportService;

    @Autowired
    private IncomingSmsService incomingSmsService;

    @Transactional
    public void parse( IncomingSms sms )
        throws SMSParserException
    {
        try
        {
            if ( dataValueSMSListener.accept( sms ) )
            {
                dataValueSMSListener.receive( sms );
            }
            else if ( j2meDataValueSMSListener.accept( sms ) )
            {
                j2meDataValueSMSListener.receive( sms );
            }
            else if ( dhisMessageAlertListener.accept( sms ) )
            {
                dhisMessageAlertListener.receive( sms );
            }
            else if ( unregisteredSMSListener.accept( sms ) )
            {
                unregisteredSMSListener.receive( sms );
            }
            else
            {
                throw new SMSParserException( "Command does not exist" );
            }
        }
        catch ( SMSParserException e )
        {
            log.error( e.getMessage() );
            sms.setStatus( SmsMessageStatus.FAILED );
            incomingSmsService.update( sms );
            sendSMS( e.getMessage(), sms.getOriginator() );
            throw e;
        }
    }

    private void sendSMS( String message, String sender )
    {
        String gatewayId = transportService.getDefaultGateway();
        if ( outboundSmsService != null )
        {
            outboundSmsService.sendMessage( new OutboundSms( message, sender ), gatewayId );
        }
    }

    @Required
    public IncomingSmsService getIncomingSmsService()
    {
        return incomingSmsService;
    }

    public J2MEDataValueSMSListener getJ2meDataValueSMSListener()
    {
        return j2meDataValueSMSListener;
    }

    public void setJ2meDataValueSMSListener( J2MEDataValueSMSListener j2meDataValueSMSListener )
    {
        this.j2meDataValueSMSListener = j2meDataValueSMSListener;
    }

    public DataValueSMSListener getDataValueSMSListener()
    {
        return dataValueSMSListener;
    }

    public void setDataValueSMSListener( DataValueSMSListener dataValueSMSListener )
    {
        this.dataValueSMSListener = dataValueSMSListener;
    }

    public UnregisteredSMSListener getUnregisteredSMSListener()
    {
        return unregisteredSMSListener;
    }

    public void setUnregisteredSMSListener( UnregisteredSMSListener unregisteredSMSListener )
    {
        this.unregisteredSMSListener = unregisteredSMSListener;
    }

    public DHISMessageAlertListener getDhisMessageAlertListener()
    {
        return dhisMessageAlertListener;
    }

    public void setDhisMessageAlertListener( DHISMessageAlertListener dhisMessageAlertListener )
    {
        this.dhisMessageAlertListener = dhisMessageAlertListener;
    }

    public OutboundSmsService getOutboundSmsService()
    {
        return outboundSmsService;
    }

    public void setOutboundSmsService( OutboundSmsService outboundSmsService )
    {
        this.outboundSmsService = outboundSmsService;
    }

}
