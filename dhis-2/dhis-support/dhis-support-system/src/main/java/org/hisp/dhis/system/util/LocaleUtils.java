package org.hisp.dhis.system.util;

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

import java.util.Locale;

/**
 * @author Oyvind Brucker
 */
public class LocaleUtils
{
    private static final String SEP = "_";
    
    /**
     * Creates a Locale object based on the input string.
     *
     * @param localeStr String to parse
     * @return A locale object or null if not valid
     */
    public static Locale getLocale( String localeStr ) 
    {
        return org.apache.commons.lang.LocaleUtils.toLocale( localeStr );
    }
        
    /**
     * Createa a locale string based on the given language, country and varient.
     * 
     * @param language the language, cannot be null.
     * @param country the country, can be null.
     * @param variant the variant, can be null.
     * @return a locale string.
     */
    public static String getLocaleString( String language, String country, String variant )
    {
        if ( language == null )
        {
            return null;
        }
        
        String locale = language;
        
        if ( country != null )
        {
            locale += SEP + country;
        }
        
        if ( variant != null )
        {
            locale += SEP + variant;
        }
        
        return locale;
    }
}
