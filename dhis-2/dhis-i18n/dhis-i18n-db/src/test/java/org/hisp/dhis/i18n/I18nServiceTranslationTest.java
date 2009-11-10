package org.hisp.dhis.i18n;

import static junit.framework.Assert.assertEquals;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.translation.TranslationService;
import org.junit.Before;
import org.junit.Test;

public class I18nServiceTranslationTest
    extends DhisSpringTest
{
    private I18nService i18nService;

    private LocaleManager localeManager;
    
    private TranslationService translationService;
    
    private DataElement dataElementA;
    
    private int dataElementIdA;
    
    private Map<String, String> translationsA;
    
    private Locale localeA = Locale.UK;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Before
    public void setUpTest()
    {
        i18nService = (I18nService) getBean( I18nService.ID );

        localeManager = (LocaleManager) getBean( "org.hisp.dhis.i18n.locale.LocaleManagerDb" );
        
        translationService = (TranslationService) getBean( TranslationService.ID );
        
        localeManager.setCurrentLocale( localeA );
        
        dataElementA = createDataElement( 'A' );
        dataElementA.setId( dataElementIdA );
        
        translationsA = new HashMap<String, String>();
        translationsA.put( "name", "DataElementUkA" );
        translationsA.put( "shortName", "ShortNameUkA" );
        translationsA.put( "code", "CodeUkA" );
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void updateTranslation()
    {
        // No existing translations exist
        
        i18nService.updateTranslation( DataElement.class.getSimpleName(), dataElementIdA, localeA, translationsA );
        
        assertEquals( "DataElementUkA", translationService.getTranslation( DataElement.class.getSimpleName(), dataElementIdA, localeA, "name" ).getValue() );
        assertEquals( "ShortNameUkA", translationService.getTranslation( DataElement.class.getSimpleName(), dataElementIdA, localeA, "shortName" ).getValue() );
        assertEquals( "CodeUkA", translationService.getTranslation( DataElement.class.getSimpleName(), dataElementIdA, localeA, "code" ).getValue() );
        
        // There are existing translations

        translationsA.put( "name", "DataElementUpdatedA" );
        translationsA.put( "shortName", "ShortNameUpdatedA" );
        translationsA.put( "code", "CodeUpdatedA" );

        i18nService.updateTranslation( DataElement.class.getSimpleName(), dataElementIdA, localeA, translationsA );

        assertEquals( "DataElementUpdatedA", translationService.getTranslation( DataElement.class.getSimpleName(), dataElementIdA, localeA, "name" ).getValue() );
        assertEquals( "ShortNameUpdatedA", translationService.getTranslation( DataElement.class.getSimpleName(), dataElementIdA, localeA, "shortName" ).getValue() );
        assertEquals( "CodeUpdatedA", translationService.getTranslation( DataElement.class.getSimpleName(), dataElementIdA, localeA, "code" ).getValue() );
    }
}
