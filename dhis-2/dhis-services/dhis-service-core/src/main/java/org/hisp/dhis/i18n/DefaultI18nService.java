package org.hisp.dhis.i18n;

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

import static org.hisp.dhis.system.util.ReflectionUtils.getClassName;
import static org.hisp.dhis.system.util.ReflectionUtils.getId;
import static org.hisp.dhis.system.util.ReflectionUtils.getProperty;
import static org.hisp.dhis.system.util.ReflectionUtils.isCollection;
import static org.hisp.dhis.system.util.ReflectionUtils.setProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationService;

/**
 * @author Oyvind Brucker
 */
public class DefaultI18nService
    implements I18nService
{
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

    public void internationalise( Object object )
    {
        if ( isCollection( object ) )
        {
            internationaliseCollection( (Collection<?>) object );
        }

        internationalise( object, localeManager.getCurrentLocale() );
    }    

    public void localise( Object object, Locale locale )
    {
        if ( isCollection( object ) )
        {
            internationaliseCollection( (Collection<?>) object, locale );
        }

        internationalise( object, locale );
    }

    private void internationalise( Object object, Locale locale )
    {
        I18nObject i18nObject = isI18nObject( object );

        if ( i18nObject != null && locale != null )
        {
            Collection<Translation> translations = translationService.getTranslations( getClassName( object ),
                getId( object ), locale );

            Map<String, String> translationsCurrentLocale = convertTranslations( translations );

            Collection<Translation> translationsFallback = null; // Not initialized unless needed
            Map<String, String> translationsFallbackLocale = null; // Not initialized unless needed

            List<String> propertyNames = i18nObject.getPropertyNames();

            for ( String property : propertyNames )
            {
                String value = translationsCurrentLocale.get( property );

                if ( value != null && !value.isEmpty() )
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

                    if ( value != null && !value.isEmpty() )
                    {
                        setProperty( object, property, value );
                    }
                }
            }
        }
    }

    private void internationaliseCollection( Collection<?> intObjects )
    {
        internationaliseCollection( intObjects, localeManager.getCurrentLocale() );
    }
    
    private void internationaliseCollection( Collection<?> intObjects, Locale locale )
    {
        if ( intObjects == null || intObjects.size() == 0 )
        {
            return;
        }

        I18nObject i18nObject = isI18nObject( intObjects.iterator().next() );

        if ( i18nObject != null && locale != null )
        {
            Collection<Translation> allTranslations = translationService.getTranslations( i18nObject.getClassName(),
                locale );

            Collection<Translation> fallbackTranslations = null; // Not initialized unless needed
            Map<String, String> fallbackTranslationsMap = null; // Not initialized unless needed

            for ( Object object : intObjects )
            {
                Map<String, String> translations = getTranslationsForObject( allTranslations, getId( object ) );
                for ( Map.Entry<String, String> translation : translations.entrySet() )
                {
                    String property = translation.getKey();
                    String value = translation.getValue();

                    if ( value != null && !value.isEmpty() )
                    {
                        setProperty( object, property, value );
                    }
                    else
                    {
                        if ( fallbackTranslations == null )
                        {
                            fallbackTranslations = translationService.getTranslations( i18nObject.getClassName(),
                                locale );

                            fallbackTranslationsMap = getTranslationsForObject( fallbackTranslations, getId( object ) );
                        }

                        value = fallbackTranslationsMap.get( property );

                        if ( value != null && !value.isEmpty() )
                        {
                            setProperty( object, property, value );
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    public void addObject( Object object )
    {
        I18nObject i18nObject = isI18nObject( object );

        Locale locale = localeManager.getCurrentLocale();

        if ( i18nObject != null && locale != null )
        {
            String className = getClassName( object );
            int id = getId( object );

            Map<String, String> translations = new Hashtable<String, String>();

            for ( String property : i18nObject.getPropertyNames() )
            {
                String value = getProperty( object, property );

                if ( value != null && !value.isEmpty() )
                {
                    translations.put( property, value );
                }
            }

            updateTranslation( className, id, locale, translations );
        }
    }

    public void verify( Object object )
    {
        if ( isI18nObject( object ) != null )
        {
            addObject( object );

            // -----------------------------------------------------------------
            // Set properties from fallback locale
            // -----------------------------------------------------------------

            if ( !localeManager.getCurrentLocale().equals( localeManager.getFallbackLocale() ) )
            {
                internationalise( object, localeManager.getFallbackLocale() );
            }
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
        if ( isI18nObject( object ) != null )
        {
            internationalise( object, localeManager.getFallbackLocale() );
        }
    }

    // -------------------------------------------------------------------------
    // Translation
    // -------------------------------------------------------------------------

    public void updateTranslation( String className, int id, Locale locale, Map<String, String> translations )
    {
        for ( Map.Entry<String, String> translationEntry : translations.entrySet() )
        {
            String key = translationEntry.getKey();
            String value = translationEntry.getValue();

            if ( value != null && !value.trim().isEmpty() )
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
        return convertTranslations( translationService.getTranslations( className, id, locale ) );
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

    public Map<String, Map<String, String>> getRulePropertyNames( String className )
    {
        for ( I18nObject i18nObject : objects )
        {
            if ( i18nObject.getClassName().equals( className ) )
            {
                return i18nObject.getRulePropertyNames();
            }
        }

        return null;
    }

    public List<String> getUniquePropertyNames( String className )
    {
        for ( I18nObject i18nObject : objects )
        {
            if ( i18nObject.getClassName().equals( className ) )
            {
                List<String> uniqueProperyNames = new ArrayList<String>();
                Map<String, Map<String, String>> rules = i18nObject.getRulePropertyNames();

                for ( String property : getPropertyNames( className ) )
                {
                    if ( rules.get( property ).get( "unique" ).equals( "true" ) )
                    {
                        uniqueProperyNames.add( property );
                    }
                }

                return uniqueProperyNames;
            }
        }

        return null;
    }

    public Map<String, String> getUniquePropertyNamesLabel( String className )
    {
        for ( I18nObject i18Object : objects )
        {
            if ( i18Object.getClassName().equals( className ) )
            {
                Map<String, String> uniquePropertyNamesLabel = new HashMap<String, String>();

                for ( String uniqueProperty : getUniquePropertyNames( className ) )
                {
                    uniquePropertyNamesLabel.put( uniqueProperty, convertPropertyToKey( uniqueProperty ) );
                }

                return uniquePropertyNamesLabel;
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
     * Returns a map representing Translations for an object matching the given 
     * id where the key is the translation property and the value is the translation 
     * value.
     *
     * @param translations Collection to search.
     * @param id the object id.
     * @return Map of property/value pairs.
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
     * Returns a map for a collection of Translations where the key is the 
     * translation property and the value is the translation value.
     *
     * @param translations the Collection of translations.
     * @return Map containing translations.
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
     * Converts the property to a i18n keystring alternativeName produces alternative_name.
     *
     * @param propName string to parse.
     * @return the modified string.
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
     * Test if an object is not null and enabled for i18n. Returns the I18nObject
     * if so. Returns null if not so.
     *
     * @param object the object to test.
     * @return the I18nObject or null.
     */
    private I18nObject isI18nObject( Object object )
    {
        if ( object != null )
        {
            for ( I18nObject i18nObject : objects )
            {
                if ( i18nObject.match( object ) )
                {
                    return i18nObject;
                }
            }
        }

        return null;
    }	
}
