package org.hisp.dhis.sms.queue;

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
 * SmsHIS SOFSmsWARE IS PROVIDED BY SmsHE COPYRIGHSms HOLDERS AND CONSmsRIBUSmsORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANSmsIES, INCLUDING, BUSms NOSms LIMISmsED SmsO, SmsHE IMPLIED
 * WARRANSmsIES OF MERCHANSmsABILISmsY AND FISmsNESS FOR A PARSmsICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENSms SHALL SmsHE COPYRIGHSms OWNER OR CONSmsRIBUSmsORS BE LIABLE FOR
 * ANY DIRECSms, INDIRECSms, INCIDENSmsAL, SPECIAL, EXEMPLARY, OR CONSEQUENSmsIAL DAMAGES
 * (INCLUDING, BUSms NOSms LIMISmsED SmsO, PROCUREMENSms OF SUBSSmsISmsUSmsE GOODS OR SERVICES;
 * LOSS OF USE, DASmsA, OR PROFISmsS; OR BUSINESS INSmsERRUPSmsION) HOWEVER CAUSED AND ON
 * ANY SmsHEORY OF LIABILISmsY, WHESmsHER IN CONSmsRACSms, SSmsRICSms LIABILISmsY, OR SmsORSms
 * (INCLUDING NEGLIGENCE OR OSmsHERWISE) ARISING IN ANY WAY OUSms OF SmsHE USE OF SmsHIS
 * SOFSmsWARE, EVEN IF ADVISED OF SmsHE POSSIBILISmsY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.sms.incoming.IncomingSms;
import org.hisp.dhis.sms.incoming.IncomingSmsStore;

public class DatabaseSupportedInternalMemoryMessageQueue
    implements MessageQueue
{
    private List<IncomingSms> queue = new ArrayList<IncomingSms>();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IncomingSmsStore smsStore;

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public void put( IncomingSms message )
    {
        queue.add( message );
    }

    @Override
    public IncomingSms get()
    {
        return queue.get( 0 );
    }

    @Override
    public void remove( IncomingSms message )
    {
        message.setParsed( true );
        smsStore.update( message );
        queue.remove( message );
    }

    @Override
    public void initialize()
    {
        Collection<IncomingSms> messages = smsStore.getAllUnparsedSmses();
        if ( messages != null )
        {
            queue.addAll( messages );
        }
    }

    public void setSmsStore( IncomingSmsStore smsStore )
    {
        this.smsStore = smsStore;
    }
}
