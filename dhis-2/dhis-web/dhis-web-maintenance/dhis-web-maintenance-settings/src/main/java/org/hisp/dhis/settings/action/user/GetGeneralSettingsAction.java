/*
 * Copyright (c) 2004-2011, University of Oslo
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

package org.hisp.dhis.settings.action.user;

import static org.hisp.dhis.user.UserSettingService.AUTO_SAVE_DATA_ENTRY_FORM;
import static org.hisp.dhis.user.UserSettingService.DEFAULT_CHARTS_IN_DASHBOARD;
import static org.hisp.dhis.user.UserSettingService.KEY_CHARTS_IN_DASHBOARD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;

import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.i18n.resourcebundle.ResourceBundleManager;
import org.hisp.dhis.options.style.StyleManager;
import org.hisp.dhis.user.UserSettingService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ GetAvailableUserSettingsAction.java May 31, 2011 9:31:54 AM $
 * 
 */
public class GetGeneralSettingsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ResourceBundleManager resourceBundleManager;

    public void setResourceBundleManager( ResourceBundleManager resourceBundleManager )
    {
        this.resourceBundleManager = resourceBundleManager;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService i18nService )
    {
        this.i18nService = i18nService;
    }

    private LocaleManager localeManagerDB;

    public void setLocaleManagerDB( LocaleManager localeManagerDB )
    {
        this.localeManagerDB = localeManagerDB;
    }

    private LocaleManager localeManagerInterface;

    public void setLocaleManagerInterface( LocaleManager localeManagerInterface )
    {
        this.localeManagerInterface = localeManagerInterface;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    private StyleManager styleManager;

    public void setStyleManager( StyleManager styleManager )
    {
        this.styleManager = styleManager;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<Locale> availableLocales;

    public List<Locale> getAvailableLocales()
    {
        return availableLocales;
    }

    private Locale currentLocale;

    public Locale getCurrentLocale()
    {
        return currentLocale;
    }

    private List<Locale> availableLocalesDb;

    public List<Locale> getAvailableLocalesDb()
    {
        return availableLocalesDb;
    }

    private Locale currentLocaleDb;

    public Locale getCurrentLocaleDb()
    {
        return currentLocaleDb;
    }

    private List<String> sortOrders;

    public List<String> getSortOrders()
    {
        return sortOrders;
    }

    private String currentSortOrder;

    public String getCurrentSortOrder()
    {
        return currentSortOrder;
    }

    private Integer chartsInDashboard;

    public Integer getChartsInDashboard()
    {
        return chartsInDashboard;
    }

    private List<Integer> chartsInDashboardOptions;

    public List<Integer> getChartsInDashboardOptions()
    {
        return chartsInDashboardOptions;
    }

    private List<String> displayProperties;

    public List<String> getDisplayProperties()
    {
        return displayProperties;
    }

    private String currentDisplayProperty;

    public String getCurrentDisplayProperty()
    {
        return currentDisplayProperty;
    }

    private Boolean autoSave;

    public Boolean getAutoSave()
    {
        return autoSave;
    }

    private String currentStyle;

    public String getCurrentStyle()
    {
        return currentStyle;
    }

    private SortedMap<String, String> styles;

    public SortedMap<String, String> getStyles()
    {
        return styles;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get available locales
        // ---------------------------------------------------------------------

        availableLocales = new ArrayList<Locale>( resourceBundleManager.getAvailableLocales() );

        Collections.sort( availableLocales, new Comparator<Locale>()
        {
            public int compare( Locale locale0, Locale locale1 )
            {
                return locale0.getDisplayName().compareTo( locale1.getDisplayName() );
            }
        } );

        currentLocale = localeManagerInterface.getCurrentLocale();

        // ---------------------------------------------------------------------
        // Get available locales in db
        // ---------------------------------------------------------------------

        availableLocalesDb = new ArrayList<Locale>( i18nService.getAvailableLocales() );

        if ( !availableLocalesDb.contains( localeManagerDB.getFallbackLocale() ) )
        {
            availableLocalesDb.add( localeManagerDB.getFallbackLocale() );
        }

        Collections.sort( availableLocalesDb, new Comparator<Locale>()
        {
            public int compare( Locale locale0, Locale locale1 )
            {
                return locale0.getDisplayName().compareTo( locale1.getDisplayName() );
            }
        } );

        currentLocaleDb = localeManagerDB.getCurrentLocale();

        // ---------------------------------------------------------------------
        // Get Charts in Dashboard
        // ---------------------------------------------------------------------

        chartsInDashboard = (Integer) userSettingService.getUserSetting( KEY_CHARTS_IN_DASHBOARD,
            DEFAULT_CHARTS_IN_DASHBOARD );

        chartsInDashboardOptions = UserSettingService.DASHBOARD_CHARTS_TO_DISPLAY;

        // ---------------------------------------------------------------------
        // Get Auto-save data entry form
        // ---------------------------------------------------------------------

        autoSave = (Boolean) userSettingService.getUserSetting( AUTO_SAVE_DATA_ENTRY_FORM, false );

        // ---------------------------------------------------------------------
        // Get styles
        // ---------------------------------------------------------------------

        styles = styleManager.getStyles();

        currentStyle = styleManager.getCurrentStyle();

        return SUCCESS;
    }
}
