package org.hisp.dhis.i18n;

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

import static org.hisp.dhis.system.util.ReflectionUtils.getClassName;
import static org.hisp.dhis.system.util.ReflectionUtils.getId;
import static org.hisp.dhis.system.util.ReflectionUtils.getProperty;
import static org.hisp.dhis.system.util.ReflectionUtils.setProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationService;

/**
 * @author Oyvind Brucker
 */
public class DefaultI18nService
    implements I18nService
{
    private static final Log log = LogFactory.getLog( DefaultI18nService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LocaleManager localeManager;

    public void setLocaleManager( LocaleManager localeManager )
    {
        this.localeManager = localeManager;
    }

    private TranslationService translationService;

    public void setTranslationService( TranslationService translationService )
    {
        this.translationService = translationService;
    }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    private Collection<I18nObject> objects;

    public void setObjects( Collection<I18nObject> objects )
    {
        this.objects = objects;
    }

    // -------------------------------------------------------------------------
    // Internationalise
    // -------------------------------------------------------------------------

    private void internationalise( Object object, Locale locale )
    {
        for ( I18nObject i18nObject : objects )
        {
            if ( i18nObject.match( object ) )
            {
                Collection<Translation> translations = translationService.getTranslations( getClassName( object ),
                    getId( object ), locale );

                Map<String, String> translationsCurrentLocale = convertTranslations( translations );
                
                Collection<Translation> translationsFallback = null; // Dont initiate unless needed
                Map<String, String> translationsFallbackLocale = null;

                List<String> propertyNames = i18nObject.getPropertyNames();

                for ( String property : propertyNames )
                {
                    String value = translationsCurrentLocale.get( property );

                    if ( value != null && !value.equals( "" ) )
                    {
                        setProperty( object, property, value );
                    }
                    else
                    {
                        if ( translationsFallback == null )
                        {
                            translationsFallback = translationService.getTranslations( getClassName( object ),
                                getId( object ), localeManager.getFallbackLocale() );

                            translationsFallbackLocale = convertTranslations( translationsFallback );
                        }

                        value = translationsFallbackLocale.get( property );

                        if ( value != null && !value.equals( "" ) )
                        {
                            setProperty( object, property, value );
                        }
                    }
                }
            }
        }
    }

    public void internationalise( Object object )
    {
        if ( !isI18nObject( object ) | object == null )
        {
            return;
        }

        Locale locale = null;

        locale = localeManager.getCurrentLocale();
        
        if ( locale == null )
        {
            log.error( "Unable to get current locale" );
        }
        else
        {
            internationalise( object, locale );
        }
    }

    public void internationaliseCollection( Collection<?> intObjects )
    {
        Locale locale = localeManager.getCurrentLocale();

        if ( locale == null || intObjects == null || intObjects.isEmpty() )
        {
            return;
        }
        
        for ( I18nObject i18nObject : objects )
        {
            if ( i18nObject.match( intObjects.iterator().next() ) )
            {
                Collection<Translation> allTranslations = translationService.getTranslations( i18nObject
                    .getClassName(), locale );
                                    
                Collection<Translation> fallbackTranslations = null; // Don't initiate unless needed
                Map<String, String> fallbackTranslationsMap = null;

                for ( Object object : intObjects )
                {
                    Map<String, String> translations = getTranslationsForObject( allTranslations, getId( object ) );

                    for ( Map.Entry<String,String> translation : translations.entrySet() )
                    {
                        String property = translation.getKey();
                        String value = translation.getValue();

                        if ( value != null && !value.equals( "" ) )
                        {
                            setProperty( object, property, value );
                        }
                        else
                        {
                            if ( fallbackTranslations == null )
                            {
                                fallbackTranslations = translationService.getTranslations( i18nObject.getClassName(),
                                    locale );

                                fallbackTranslationsMap = getTranslationsForObject( fallbackTranslations,
                                    getId( object ) );
                            }

                            value = fallbackTranslationsMap.get( property );

                            if ( value != null && !value.equals( "" ) )
                            {
                                setProperty( object, property, value );
                            }
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Internationalise
    // -------------------------------------------------------------------------

    public void addObject( Object object )
    {
        if ( !isI18nObject( object ) )
        {
            return;
        }

        Locale locale = localeManager.getCurrentLocale();

        if ( locale == null )
        {
            log.warn( "Failed to get current locale while adding object" );

            return;
        }

        for ( I18nObject i18nObject : objects )
        {
            if ( i18nObject.match( object ) )
            {
                String className = getClassName( object );
                int id = getId( object );

                Map<String, String> translations = new Hashtable<String, String>();

                for ( String property : i18nObject.getPropertyNames() )
                {
                    String value = getProperty( object, property );

                    if ( value != null && !value.equals( "" ) )
                    {
                        translations.put( property, value );
                    }
                }

                if ( !translations.isEmpty() )
                {
                    updateTranslation( className, id, locale, translations );
                }
            }
        }
    }

    public void verify( Object object )
    {
        if ( !isI18nObject( object ) | object == null )
        {
            return;
        }

        Locale locale = localeManager.getCurrentLocale();

        /**
         * Save translations
         */

        for ( I18nObject i18nObject : objects )
        {
            if ( i18nObject.match( object ) )
            {
                String className = getClassName( object );
                int id = getId( object );

                Map<String, String> translations = new Hashtable<String, String>();

                for ( String property : i18nObject.getPropertyNames() )
                {
                    String value = getProperty( object, property );

                    if ( value != null && !value.equals( "" ) )
                    {
                        translations.put( property, value );
                    }
                }

                updateTranslation( className, id, locale, translations );
            }
        }

        /**
         * Set properties to properties from the fallback locale
         */
        
        if ( !locale.equals( localeManager.getFallbackLocale() ) )
        {
            internationalise( object, localeManager.getFallbackLocale() );
        }

    }

    public void verifyCollection( Collection<?> collection )
    {
        for ( Object object : collection )
        {
            verify( object );
        }
    }

    public void removeObject( Object object )
    {
        if ( object != null )
        {
            translationService.deleteTranslations( getClassName( object ), getId( object ) );
        }
    }

    public void setToFallback( Object object )
    {
        if ( !isI18nObject( object ) | object == null )
        {
            return;
        }

        internationalise( object, localeManager.getFallbackLocale() );
    }

    // -------------------------------------------------------------------------
    // Translation
    // -------------------------------------------------------------------------

    public void addTranslation( Object object, String property, String value, Locale locale )
    {
        if ( !isI18nObject( object ) | object == null )
        {
            return;
        }

        String className = getClassName( object );
        int id = getId( object );

        Map<String, String> translations = new Hashtable<String, String>();

        translations.put( property, value );

        updateTranslation( className, id, locale, translations );
    }

    public void updateTranslation( String className, int id, Locale locale, Map<String, String> translations )
    {
        for ( Map.Entry<String,String> translationEntry : translations.entrySet() )
        {
            String key = translationEntry.getKey();
            String value = translationEntry.getValue();

            if ( value != null && value.trim().length() > 0 )
            {
                Translation translation = translationService.getTranslation( className, id, locale, key );
                
                if ( translation != null )
                {
                    translation.setValue( value );
                    translationService.updateTranslation( translation );
                }
                else
                {
                    translation = new Translation( className, id, locale.toString(), key, value );
                    translationService.addTranslation( translation );
                }
            }
        }
    }

    public Map<String, String> getTranslations( String className, int id, Locale locale )
    {
        Collection<Translation> translationsCol = translationService.getTranslations( className, id, locale );

        return convertTranslations( translationsCol );
    }

    // -------------------------------------------------------------------------
    // Property
    // -------------------------------------------------------------------------

    public List<String> getPropertyNames( String className )
    {
        for ( I18nObject i18nObject : objects )
        {
            if ( i18nObject.getClassName().equals( className ) )
            {
                return i18nObject.getPropertyNames();
            }
        }

        return null;
    }

    public Map<String, String> getPropertyNamesLabel( String className )
    {
        for ( I18nObject i18nObject : objects )
        {
            if ( i18nObject.getClassName().equals( className ) )
            {
                Map<String, String> propertyNamesLabel = new Hashtable<String, String>();

                for ( String property : i18nObject.getPropertyNames() )
                {
                    propertyNamesLabel.put( property, convertPropertyToKey( property ) );
                }

                return propertyNamesLabel;
            }
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // Locale
    // -------------------------------------------------------------------------

    public Collection<Locale> getAvailableLocales()
    {
        List<Locale> locales = localeManager.getLocalesOrderedByPriority();

        Collection<Locale> translationLocales = translationService.getAvailableLocales();

        if ( translationLocales != null )
        {
            for ( Locale locale : translationLocales )
            {
                if ( !locales.contains( locale ) )
                {
                    locales.add( locale );
                }
            }
        }

        return locales;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns property/value pairs of translations for one object matching id.
     *
     * @param translations Collection to search
     * @param id           Object id
     * @return Map of property/value pairs
     */
    private Map<String, String> getTranslationsForObject( Collection<Translation> translations, int id )
    {
        Collection<Translation> objectTranslations = new ArrayList<Translation>();

        for ( Translation translation : translations )
        {
            if ( translation.getId() == id )
            {
                objectTranslations.add( translation );
            }
        }

        return convertTranslations( objectTranslations );
    }

    /**
     * Returns property/value pairs of a collection of translations as a map
     *
     * @param translations
     * @return Map containing translations
     */
    private Map<String, String> convertTranslations( Collection<Translation> translations )
    {
        Map<String, String> translationMap = new Hashtable<String, String>();

        for ( Translation translation : translations )
        {
            if ( translation.getProperty() != null && translation.getValue() != null )
            {
                translationMap.put( translation.getProperty(), translation.getValue() );
            }
        }

        return translationMap;
    }

    /**
     * Converts the property to a i18n keystring
     * alternativeName produces alternative_name
     *
     * @param propName string to parse
     * @return Modified string
     */
    private String convertPropertyToKey( String propName )
    {
        StringBuffer str = new StringBuffer();

        char[] chars = propName.toCharArray();

        for ( int i = 0; i < chars.length; i++ )
        {
            if ( Character.isUpperCase( chars[i] ) )
            {
                str.append( "_" ).append( Character.toLowerCase( chars[i] ) );
            }
            else
            {
                str.append( chars[i] );
            }
        }

        return str.toString();
    }

    /**
     * Test if an object is enabled for i18n
     *
     * @param object Object to check
     * @return true if the object is enabled for i18n
     */
    private boolean isI18nObject( Object object )
    {
        for ( I18nObject i18nObject : objects )
        {
            if ( i18nObject.match( object ) )
            {
                return true;
            }
        }

        return false;
    }
}
