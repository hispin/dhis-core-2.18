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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Collection;

/**
 * @author Oyvind Brucker
 * @modifier Dang Duy Hieu
 * @since 2010-04-15
 */
public interface I18nService
{
    String ID = I18nService.class.getName();

    // -------------------------------------------------------------------------
    // Internationalise
    // -------------------------------------------------------------------------

    public void internationalise( Object object );
    
    public void localise( Object object, Locale locale );
    
    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    public void addObject( Object object );

    public void verify( Object object );

    public void removeObject( Object object );

    public void setToFallback( Object object );

    // -------------------------------------------------------------------------
    // Translation
    // -------------------------------------------------------------------------

    public void updateTranslation( String className, int id, Locale thisLocale, Map<String, String> translations );

    public Map<String, String> getTranslations( String className, int id, Locale locale );

    // -------------------------------------------------------------------------
    // Property
    // -------------------------------------------------------------------------

    public List<String> getPropertyNames( String className );

    public List<String> getUniquePropertyNames( String className );

    public Map<String, String> getPropertyNamesLabel( String className );

    public Map<String, String> getUniquePropertyNamesLabel( String className );
    
    public Map<String, Map<String, String>> getRulePropertyNames( String className );

    // -------------------------------------------------------------------------
    // Locale
    // -------------------------------------------------------------------------

    public Collection<Locale> getAvailableLocales();
}
