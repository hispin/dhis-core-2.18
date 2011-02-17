package org.hisp.dhis.i18n;

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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.mock.MockLocaleManager;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationService;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class I18nServiceTranslationTest
    extends DhisSpringTest
{
    private I18nService i18nService;

    private LocaleManager localeManager;
    
    private TranslationService translationService;
    
    private DataElement dataElementA;
    
    private int dataElementIdA = 10;
    
    private String dataElementClassNameA = DataElement.class.getSimpleName();
    
    private Map<String, String> translationsA;
    private Map<String, String> translationsB;
    
    private Locale localeA = Locale.FRANCE;
    private Locale localeB = Locale.GERMANY;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Before
    public void setUpTest()
    {
        i18nService = (I18nService) getBean( I18nService.ID );

        localeManager = new MockLocaleManager(); // (LocaleManager) getBean( "org.hisp.dhis.i18n.locale.LocaleManagerDb" );
        
        translationService = (TranslationService) getBean( TranslationService.ID );

        setDependency( i18nService, "localeManager", localeManager );
        
        localeManager.setCurrentLocale( localeA );
        
        dataElementA = createDataElement( 'A' );
        dataElementA.setId( dataElementIdA );
        
        translationsA = new HashMap<String, String>();
        translationsA.put( "name", "DataElementFrA" );
        translationsA.put( "shortName", "ShortNameFrA" );
        translationsA.put( "description", "DescriptionFrA" );
                
        translationsB = new HashMap<String, String>();
        translationsB.put( "name", "DataElementGeA" );
        translationsB.put( "shortName", "ShortNameGeA" );
        translationsB.put( "description", "DescriptionGeA" );
    }

    // -------------------------------------------------------------------------
    // Internationalise
    // -------------------------------------------------------------------------

    @Test
    public void internationalise()
    {
        i18nService.addObject( dataElementA );
        
        i18nService.internationalise( dataElementA );
        
        assertEquals( "DataElementA", dataElementA.getName() );
        assertEquals( "DataElementShortA", dataElementA.getShortName() );
        assertEquals( "DataElementDescriptionA", dataElementA.getDescription() );
        
        i18nService.updateTranslation( dataElementClassNameA, dataElementIdA, localeB, translationsB );
        
        localeManager.setCurrentLocale( localeB );

        i18nService.internationalise( dataElementA );
        
        assertEquals( "DataElementGeA", dataElementA.getName() );
        assertEquals( "ShortNameGeA", dataElementA.getShortName() );
        assertEquals( "DescriptionGeA", dataElementA.getDescription() );
    }
    
    @Test
    public void internationaliseCollection()
    {
        Collection<DataElement> dataElements = new ArrayList<DataElement>();
        dataElements.add( dataElementA );
        dataElements.add( dataElementA );
        dataElements.add( dataElementA );
        
        i18nService.internationalise( dataElements );
        
        for ( DataElement dataElement : dataElements )
        {
            assertEquals( "DataElementA", dataElement.getName() );
            assertEquals( "DataElementShortA", dataElement.getShortName() );
            assertEquals( "DataElementDescriptionA", dataElement.getDescription() );            
        }

        i18nService.updateTranslation( dataElementClassNameA, dataElementIdA, localeB, translationsB );
        
        localeManager.setCurrentLocale( localeB );
        
        i18nService.internationalise( dataElements );

        for ( DataElement dataElement : dataElements )
        {
            assertEquals( "DataElementGeA", dataElement.getName() );
            assertEquals( "ShortNameGeA", dataElement.getShortName() );
            assertEquals( "DescriptionGeA", dataElement.getDescription() );          
        }
    }
    
    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Test
    public void addObject()
    {
        i18nService.addObject( dataElementA );
        
        Collection<Translation> translations = translationService.getTranslations( dataElementClassNameA, dataElementIdA, localeA );
        
        assertNotNull( translations );
        assertEquals( 3, translations.size() );
        
        i18nService.addObject( null );
    }
    
    // -------------------------------------------------------------------------
    // Translation
    // -------------------------------------------------------------------------

    @Test
    public void updateTranslation()
    {
        // No existing translations exist
        
        i18nService.updateTranslation( dataElementClassNameA, dataElementIdA, localeA, translationsA );
        
        assertEquals( "DataElementFrA", translationService.getTranslation( dataElementClassNameA, dataElementIdA, localeA, "name" ).getValue() );
        assertEquals( "ShortNameFrA", translationService.getTranslation( dataElementClassNameA, dataElementIdA, localeA, "shortName" ).getValue() );
        assertEquals( "DescriptionFrA", translationService.getTranslation( dataElementClassNameA, dataElementIdA, localeA, "description" ).getValue() );
        
        // There are existing translations

        translationsA.put( "name", "DataElementUpdatedA" );
        translationsA.put( "shortName", "ShortNameUpdatedA" );
        translationsA.put( "description", "DescriptionUpdatedA" );

        i18nService.updateTranslation( dataElementClassNameA, dataElementIdA, localeA, translationsA );

        assertEquals( "DataElementUpdatedA", translationService.getTranslation( dataElementClassNameA, dataElementIdA, localeA, "name" ).getValue() );
        assertEquals( "ShortNameUpdatedA", translationService.getTranslation( dataElementClassNameA, dataElementIdA, localeA, "shortName" ).getValue() );
        assertEquals( "DescriptionUpdatedA", translationService.getTranslation( dataElementClassNameA, dataElementIdA, localeA, "description" ).getValue() );
    }
    
    @Test
    public void getTranslations()
    {
        i18nService.updateTranslation( dataElementClassNameA, dataElementIdA, localeA, translationsA );
        
        Map<String, String> translations = i18nService.getTranslations( dataElementClassNameA, dataElementIdA, localeA );
        
        assertEquals( 3, translations.keySet().size() );
        assertEquals( "DataElementFrA", translations.get( "name" ) );
        assertEquals( "ShortNameFrA", translations.get( "shortName" ) );
        assertEquals( "DescriptionFrA", translations.get( "description" ) );
    }
}
