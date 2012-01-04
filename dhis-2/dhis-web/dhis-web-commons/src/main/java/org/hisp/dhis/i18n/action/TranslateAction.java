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

import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.system.util.LocaleUtils;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationService;

import com.opensymphony.xwork2.Action;

/**
 * @author Oyvind Brucker
 * @version $Id$
 * @modifier Dang Duy Hieu
 * @since 2010-03-24
 */
public class TranslateAction
    implements Action
{
    private static final Log log = LogFactory.getLog( TranslateAction.class );

    private static final String SUFFIX_WARNING_MESSAGE = "_with";

    private static final String PREFIX_WARNING_MESSAGE = "translation_duplicated_";

    private String className;

    private String objectId;

    private String loc;

    private String returnUrl;

    private String message;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nService i18nService;

    public void setI18nService( I18nService i18nService )
    {
        this.i18nService = i18nService;
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

    public void setObjectId( String objectId )
    {
        this.objectId = objectId;
    }

    public void setLoc( String locale )
    {
        this.loc = locale;
    }

    public void setReturnUrl( String returnUrl )
    {
        this.returnUrl = returnUrl;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public String getClassName()
    {
        return className;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public String getLocale()
    {
        return loc;
    }

    public String getReturnUrl()
    {
        return returnUrl;
    }

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
        log.info( "\n\nClassname: " + className + ", id: " + objectId + ", loc: " + loc );

        Locale thisLocale = LocaleUtils.getLocale( loc );

        HttpServletRequest request = ServletActionContext.getRequest();

        Map<String, String> translations = new Hashtable<String, String>();

        List<String> propertyNames = i18nService.getPropertyNames( className );
        List<String> uniquePropertyNames = i18nService.getUniquePropertyNames( className );

        for ( String propertyName : propertyNames )
        {
            String[] translation = request.getParameterValues( propertyName );

            if ( translation != null && translation.length > 0  )
            {
                if ( uniquePropertyNames.contains( propertyName ) && translation[0].length() > 0 )
                {
                    if ( this.isDuplicatedInTranslation( thisLocale, propertyName, translation[0] ) )
                    {
                        return INPUT;
                    }
                }
                else if ( translation[0].length() == 0 )
                {
                	translation[0] = "";
                }

                translations.put( propertyName, translation[0] );
            }
        }

        log.info( "\nTranslations: " + translations );

        if ( thisLocale != null && !loc.equals( "heading" ) )
        {
            i18nService.updateTranslation( className, Integer.parseInt( objectId ), thisLocale, translations );
        }

        message = "";

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String getBuildUpMessage( Translation obj, String property )
    {
        String message = i18n.getString( PREFIX_WARNING_MESSAGE + property + SUFFIX_WARNING_MESSAGE )
            + " <br/><br/> <center><strong>[ " + className + " ]  -  [ "
            + i18n.getString( "translation_label_" + property ) + " = \"" + obj.getValue() + "\" ]</strong></center>";

        return message;
    }

    private boolean isDuplicatedInTranslation( Locale locale, String property, String value )
    {
        Translation objMatch = translationService.getTranslation( className, locale, property, value, Integer
            .parseInt( objectId ) );

        if ( objMatch != null )
        {
            message = this.getBuildUpMessage( objMatch, property.toLowerCase() );

            return true;
        }

        return false;
    }

}
