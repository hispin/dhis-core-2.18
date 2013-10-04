package org.hisp.dhis.i18n;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.i18n.locale.I18nLocale;
import org.hisp.dhis.system.util.LocaleUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultI18nLocaleService
    implements I18nLocaleService
{
    private static final String NAME_SEP = ", ";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<I18nLocale> localeStore;

    public void setLocaleStore( GenericIdentifiableObjectStore<I18nLocale> localeStore )
    {
        this.localeStore = localeStore;
    }

    private Map<String, String> languages;
    
    public void setLanguages( Map<String, String> languages )
    {
        this.languages = languages;
    }

    private Map<String, String> countries;

    public void setCountries( Map<String, String> countries )
    {
        this.countries = countries;
    }

    // -------------------------------------------------------------------------
    // I18nLocaleService implementation
    // -------------------------------------------------------------------------

    public Map<String, String> getAvailableLanguages()
    {
        return languages;
    }
    
    public Map<String, String> getAvailableCountries()
    {
        return countries;
    }
    
    public boolean addI18nLocale( String language, String country )
    {
        String languageName = languages.get( language );
        String countryName = countries.get( country );
        
        if ( language == null || languageName == null )
        {
            return false; // Language is required
        }
        
        if ( country != null && countryName == null )
        {
            return false; // Country not valid
        }
        
        String loc = LocaleUtils.getLocaleString( language, country, null );
        
        String name = languageName + ( countryName != null ? ( NAME_SEP + countryName ) : TextUtils.EMPTY );
        
        I18nLocale locale = new I18nLocale( name, loc );
        
        saveI18nLocale( locale );
        
        return true;
    }
    
    public void saveI18nLocale( I18nLocale locale )
    {
        localeStore.save( locale );
    }
    
    public I18nLocale getI18nLocale( int id )
    {
        return localeStore.get( id );
    }
    
    public I18nLocale getI18nLocaleByUid( String uid )
    {
        return localeStore.getByUid( uid );
    }
    
    public void deleteI18nLocale( I18nLocale locale )
    {
        localeStore.delete( locale );
    }
    
    public int getI18nLocaleCount()
    {
        return localeStore.getCount();
    }

    public int getI18nLocaleCountByName( String name )
    {
        return localeStore.getCountLikeName( name );
    }
    
    public Collection<I18nLocale> getI18nLocalesBetween( int first, int max )
    {
        return localeStore.getAllOrderedName( first, max );
    }
    
    public Collection<I18nLocale> getI18nLocalesBetweenLikeName( String name, int first, int max )
    {
        return localeStore.getAllLikeNameOrderedName( name, first, max );
    }
}
