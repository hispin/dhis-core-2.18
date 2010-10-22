/*
 *
 * Copyright (c) 2004-2010, University of Oslo
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
package org.hisp.dhis;


import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Jason P. Pickering
 */
public class LiveMessagingService
{

    private static final Log log = LogFactory.getLog( TrayApp.class );

    private static final String defaultLanguage = "en";

    private static final String defaultCountry = "GB";

    private ResourceBundle getMessageBundle()
    {
        ResourceBundle messages;
        String currentLanguage = TrayApp.getInstance().getConfig().getLocaleLanguage();
        String currentCountry = TrayApp.getInstance().getConfig().getLocaleCountry();
        Locale currentLocale = new Locale( currentLanguage, currentCountry );
        log.debug( "Current locale set to " + currentLocale.toString() );
        try
        {
            messages = ResourceBundle.getBundle( "messages", currentLocale );
        } catch ( Exception e )
        {
            //problem loading the desired resource bundle
            //fall back to default
            log.error( "The desired resource bundle could not be loaded.Usig default" );
            currentLocale = new Locale( defaultLanguage, defaultCountry );
            messages = ResourceBundle.getBundle( "messages", currentLocale );
        }

        return messages;
    }

    public String getString( String messageName )
    {

        ResourceBundle messages = getMessageBundle();

        String returnMessage = null;
        if ( messageName.isEmpty() )
        {
            returnMessage = "messageName not valid";
            return returnMessage;
        } else
        {

            if ( messages.containsKey( messageName ) )
            {
                returnMessage = messages.getString( messageName );

                if ( returnMessage == null )
                {
                    returnMessage = "Message not found";
                }

            } else
            {
                returnMessage = "Message key " + messageName + " not found.";
            }
            return returnMessage;
        }
    }
}
