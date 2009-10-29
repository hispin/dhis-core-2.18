package org.hisp.dhis.i18n.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.i18n.locale.LocaleManager;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2009-10-28
 */

public class DefaultTranslationUserSettingSupport
    implements TranslationUserSettingSupport
{

    private static final String UNDERSCORE = "_";

    private static final String GET_ID_METHOD = "getId";

    private static final String TRANSLATION_PROPERTY_NAME = "name";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    private LocaleManager localeManager;

    public void setLocaleManager( LocaleManager localeManager )
    {
        this.localeManager = localeManager;
    }

    // -------------------------------------------------------------------------
    // Local variables
    // -------------------------------------------------------------------------

    private Class<?> clazz;

    private List<Object> objectList;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DefaultTranslationUserSettingSupport()
    {
        super();
    }

    // -------------------------------------------------------------------------
    // Implementing methods
    // -------------------------------------------------------------------------

    public Locale getCurrentLocale()
    {
        return localeManager.getCurrentLocale();
    }

    public Locale getCurrentRefLocale()
    {
        return localeManager.getFallbackLocale();
    }

    @SuppressWarnings( "static-access" )
    public List<String> getPropertyNames()
    {
        return i18nService.getPropertyNames( this.clazz.getSimpleName() );
    }

    public void setClazz( Class<?> clazz )
    {
        this.clazz = clazz;
    }

    public void setObjectList( List<Object> objectList )
    {
        this.objectList = new ArrayList<Object>( objectList );
    }

    @SuppressWarnings( "static-access" )
    public Map<String, String> initTranslations( Map<String, String> translationsClazz )
    {
        translationsClazz = i18nService.getTranslations( this.clazz.getSimpleName(), this.TRANSLATION_PROPERTY_NAME,
            getCurrentLocale() );

        /**
         * Fill in empty strings for null values
         */

        for ( Object o : objectList )
        {
            System.out.println( "\no.getId = " + getId( o ) );

            for ( String property : getPropertyNames() )
            {
                if ( translationsClazz.get( getId( o ) + this.UNDERSCORE + property ) == null )
                {
                    translationsClazz.put( getId( o ) + this.UNDERSCORE + property, "" );
                }
            }
        }
        return translationsClazz;
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    /**
     * Calls the method getId for this object, throws exception if this fails.
     * 
     * @param object object to call method on, needs to have the public method
     *        getId():int
     * @return The id
     */
    @SuppressWarnings( "static-access" )
    private String getId( Object object )
    {
        int result = -1;

        Class<?> c = object.getClass();

        Method method;

        try
        {
            method = c.getMethod( this.GET_ID_METHOD );

            result = (Integer) method.invoke( object );
        }
        catch ( NoSuchMethodException e )
        {
            System.out.println( e );
        }
        catch ( IllegalAccessException e )
        {
            System.out.println( e );
        }
        catch ( InvocationTargetException e )
        {
            System.out.println( e );
        }

        return String.valueOf( result );
    }

}
