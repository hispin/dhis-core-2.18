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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.mock.MockLocaleManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Oyvind Brucker
 */
public class I18nServiceTest
    extends DhisSpringTest
{
    private I18nService i18nService;

    private LocaleManager localeManager;

    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Before
    public void setUpTest()
        throws Exception
    {
        i18nService = (I18nService) getBean( I18nService.ID );
        
        localeManager = new MockLocaleManager(); //(LocaleManager) getBean( "org.hisp.dhis.i18n.locale.LocaleManagerDb" );
        
        setDependency( i18nService, "localeManager", localeManager );
    }

    // -------------------------------------------------------------------------
    // Testdata
    // -------------------------------------------------------------------------

    private int id1 = 0;

    private int id2 = 1;

    private int id3 = 2;

    private String className1 = "OrganisationUnit";

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------
    
    @Test
    public void testUpdateTranslation()
        throws Exception
    {
        String organisationUnitNameUK = "organisationUnitName1";

        OrganisationUnit organisationUnit1 = new OrganisationUnit( organisationUnitNameUK, "shortName1",
            "organisationUnitCode1", new Date(), new Date(), true, "comment" );

        organisationUnit1.setId( id1 );

        Map<String, String> translations1 = new Hashtable<String, String>();

        translations1.put( "name", "orgunit_1" );
        translations1.put( "shortName", "org-1" );
        translations1.put( "comment", "Just another orgunit" );

        localeManager.setCurrentLocale( Locale.UK );

        i18nService.updateTranslation( className1, id1, Locale.UK, translations1 );

        i18nService.internationalise( organisationUnit1 );
        assertEquals( organisationUnit1.getName(), "orgunit_1" );

        translations1.put( "name", "orgunit-1" );
        i18nService.updateTranslation( className1, id1, Locale.UK, translations1 );

        i18nService.internationalise( organisationUnit1 );
        assertEquals( organisationUnit1.getName(), "orgunit-1" );

        /**
         * Collection
         */

        String organisationUnitNameUK2 = "organisationUnitName2";
        String organisationUnitNameUK3 = "organisationUnitName1";

        OrganisationUnit organisationUnit2 = new OrganisationUnit( organisationUnitNameUK2, "shortName2",
            "organisationUnitCode2", new Date(), new Date(), true, "comment" );

        OrganisationUnit organisationUnit3 = new OrganisationUnit( organisationUnitNameUK3, "shortName3",
            "organisationUnitCode3", new Date(), new Date(), true, "comment" );
        organisationUnit2.setId( id2 );
        organisationUnit3.setId( id3 );

        List<OrganisationUnit> orgunits = new ArrayList<OrganisationUnit>();
        orgunits.add( organisationUnit1 );
        orgunits.add( organisationUnit2 );
        orgunits.add( organisationUnit3 );

        translations1.put( "name", "orgunit-2" );
        translations1.put( "shortName", "org-2" );
        translations1.put( "comment", "Just another orgunit" );

        i18nService.updateTranslation( className1, id2, Locale.UK, translations1 );
        translations1.put( "name", "orgunit-3" );
        translations1.put( "shortName", "org-3" );
        translations1.put( "comment", "Just another orgunit" );
        i18nService.updateTranslation( className1, id3, Locale.UK, translations1 );

        i18nService.internationalise( orgunits );

        for ( int i = 0; i < orgunits.size(); i++ )
        {
            OrganisationUnit orgunit = orgunits.get( i );

            assertEquals( orgunit.getName(), "orgunit-" + (i + 1) );
        }
    }

    @Test
    public void testAddObject()
        throws Exception
    {
        OrganisationUnit organisationUnit1 = new OrganisationUnit( "orgunit-1", "org-1", "organisationUnitCode1",
            new Date(), new Date(), true, "comment" );
        organisationUnit1.setId( id1 );

        Locale fallbackLocale = localeManager.getFallbackLocale();

        Locale locale = Locale.FRANCE;

        if ( locale.equals( fallbackLocale ) )
        {
            locale = Locale.GERMANY;
        }

        Map<String, String> translationsFr = new Hashtable<String, String>();

        translationsFr.put( "name", "orgunit_1" );
        translationsFr.put( "shortName", "org_1" );
        translationsFr.put( "comment", "Just another orgunit" );

        localeManager.setCurrentLocale( localeManager.getFallbackLocale() );

        i18nService.addObject( organisationUnit1 );
        i18nService.updateTranslation( className1, id1, locale, translationsFr );

        localeManager.setCurrentLocale( locale );

        i18nService.internationalise( organisationUnit1 );

        assertEquals( "orgunit_1", organisationUnit1.getName() );
        assertEquals( "org_1", organisationUnit1.getShortName() );

        localeManager.setCurrentLocale( fallbackLocale );

        i18nService.internationalise( organisationUnit1 );

        assertEquals( "orgunit-1", organisationUnit1.getName() );
        assertEquals( "org-1", organisationUnit1.getShortName() );
    }

    @Test
    public void testVerify()
        throws Exception
    {
        OrganisationUnit organisationUnit1 = new OrganisationUnit( "orgunit-1", "org-1", "organisationUnitCode1",
            new Date(), new Date(), true, "comment" );
        organisationUnit1.setId( id1 );

        /**
         * Find a different locale than the fallback locale
         */
        Locale fallbackLocale = localeManager.getFallbackLocale();

        localeManager.setCurrentLocale( fallbackLocale );

        Locale locale = Locale.FRANCE;

        if ( locale.equals( fallbackLocale ) )
        {
            locale = Locale.GERMANY;
        }

        /**
         * The user adds a new object using the fallback locale
         */

        i18nService.addObject( organisationUnit1 );

        /**
         * Properties are changed using a different locale
         */

        localeManager.setCurrentLocale( locale );

        organisationUnit1.setName( "orgunit_1" );

        i18nService.verify( organisationUnit1 );

        assertEquals( "orgunit-1", organisationUnit1.getName() );

        /**
         * Request a translation to verify that the translation is kept
         */

        localeManager.setCurrentLocale( locale );

        i18nService.internationalise( organisationUnit1 );

        assertEquals( "orgunit_1", organisationUnit1.getName() );
    }

    @Test
    public void testGetAvailableLocales()
        throws Exception
    {
        OrganisationUnit organisationUnit1 = new OrganisationUnit( "orgunit", "shortName1", "organisationUnitCode1",
            new Date(), new Date(), true, "comment" );
        organisationUnit1.setId( id1 );
        Map<String, String> translations1 = new Hashtable<String, String>();
        translations1.put( "name", "orgunit-1" );
        translations1.put( "shortName", "org-1" );
        translations1.put( "comment", "Just another orgunit" );

        i18nService.updateTranslation( className1, id1, Locale.UK, translations1 );
        i18nService.updateTranslation( className1, id1, Locale.FRANCE, translations1 );
        i18nService.updateTranslation( className1, id1, Locale.US, translations1 );

        Collection<Locale> availableLocales = i18nService.getAvailableLocales();

        assertEquals( 3, availableLocales.size() );
        assertTrue( availableLocales.contains( Locale.UK ) );
        assertTrue( availableLocales.contains( Locale.FRANCE ) );
        assertTrue( availableLocales.contains( Locale.US ) );
    }

    @Test
    public void testSetToFallback()
        throws Exception
    {
        OrganisationUnit organisationUnit1 = new OrganisationUnit( "orgunit", "shortName1", "organisationUnitCode1",
            new Date(), new Date(), true, "comment" );
        organisationUnit1.setId( id1 );

        Map<String, String> translationsFallback = new Hashtable<String, String>();
        translationsFallback.put( "name", "orgunit-1" );
        translationsFallback.put( "shortName", "org-1" );

        Map<String, String> translationsNotFallback = new Hashtable<String, String>();
        translationsNotFallback.put( "name", "orgunit_1" );
        translationsNotFallback.put( "shortName", "org_1" );

        Locale notFallbackLocale = Locale.FRANCE;

        Locale fallbackLocale = localeManager.getFallbackLocale();

        if ( notFallbackLocale.equals( fallbackLocale ) )
        {
            notFallbackLocale = Locale.GERMANY;
        }

        i18nService.updateTranslation( className1, id1, fallbackLocale, translationsFallback );
        i18nService.updateTranslation( className1, id1, notFallbackLocale, translationsNotFallback );

        localeManager.setCurrentLocale( notFallbackLocale );

        i18nService.internationalise( organisationUnit1 );

        assertEquals( "orgunit_1", organisationUnit1.getName() );
        assertEquals( "org_1", organisationUnit1.getShortName() );

        i18nService.setToFallback( organisationUnit1 );

        assertEquals( "orgunit-1", organisationUnit1.getName() );
        assertEquals( "org-1", organisationUnit1.getShortName() );
    }
}