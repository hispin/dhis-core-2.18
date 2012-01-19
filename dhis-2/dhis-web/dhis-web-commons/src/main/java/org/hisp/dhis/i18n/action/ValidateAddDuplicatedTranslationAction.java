package org.hisp.dhis.i18n.action;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.system.util.LocaleUtils;
import org.hisp.dhis.translation.TranslationService;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2010-03-30
 */
public class ValidateAddDuplicatedTranslationAction
    implements Action
{
    private static final Log log = LogFactory.getLog( ValidateAddDuplicatedTranslationAction.class );

    private static final String PROPERTY_NAME = "name";

    private static final String PROPERTY_SHORTNAME = "shortname";

    private static final String WARNING_MESSAGE = "translation_duplicated_";

    private String className;

    private String id;

    private String loc;

    private String name;

    private String shortName;

    private String message;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    private TranslationService translationService;

    public void setTranslationService( TranslationService translationService )
    {
        this.translationService = translationService;
    }

    // -------------------------------------------------------------------------
    // I18n Object
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    public void setClassName( String className )
    {
        this.className = className;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public void setLoc( String locale )
    {
        this.loc = locale;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        log.info( "Classname: " + className + ", id: " + id + ", loc: " + loc );
        log.info( "name: " + name + ", shortName: " + shortName );

        Locale thisLocale = LocaleUtils.getLocale( loc );

        if ( name == null && shortName == null )
        {
            return SUCCESS;
        }

        if ( name != null )
        {
            name = name.trim();
        }
        if ( shortName != null )
        {
            shortName = shortName.trim();
        }
        if ( (name.length() == 0) && (shortName.length() == 0) )
        {
            return SUCCESS;
        }

        // -------------------------------------------------------------
        // Checking duplicated name
        // -------------------------------------------------------------

        /*
        List<String> propertyNames = i18nService.getPropertyNames( className );

        for ( String propertyName : propertyNames )
        {
            if ( propertyName.equalsIgnoreCase( PROPERTY_NAME ) )
            {
                Translation objMatch = translationService.getTranslation( className, thisLocale, propertyName, name,
                    Integer.parseInt( id ) );

                if ( objMatch != null )
                {
                    message = i18n.getString( WARNING_MESSAGE + PROPERTY_NAME + "_with" )
                        + " <br/><br/> <center><strong>[ " + className + " ]  -  [ "
                        + i18n.getString( "translation_label_id" ) + ":= " + objMatch.getId() + " ]  -  [ "
                        + i18n.getString( "translation_label_name" ) + ":= " + objMatch.getValue()
                        + " ]</strong></center>";

                    return INPUT;
                }
            }
            else if ( propertyName.equalsIgnoreCase( PROPERTY_SHORTNAME ) )
            {
                Translation objMatch = translationService.getTranslation( className, thisLocale, propertyName,
                    shortName, Integer.parseInt( id ) );

                if ( objMatch != null )
                {
                    message = i18n.getString( WARNING_MESSAGE + PROPERTY_SHORTNAME + "_with" )
                        + " <br/><br/> <center><strong>[ " + className + " ]  -  [ "
                        + i18n.getString( "translation_label_id" ) + ":= " + objMatch.getId() + " ]  -  [ "
                        + i18n.getString( "translation_label_short_name" ) + ":= " + objMatch.getValue()
                        + " ]</strong></center>";

                    return INPUT;
                }
            }
        }
*/
        message = "OK";

        return SUCCESS;
    }

}
