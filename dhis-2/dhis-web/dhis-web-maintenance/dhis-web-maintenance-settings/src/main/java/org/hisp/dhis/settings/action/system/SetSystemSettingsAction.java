package org.hisp.dhis.settings.action.system;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import static org.hisp.dhis.options.SystemSettingManager.KEY_AGGREGATION_STRATEGY;
import static org.hisp.dhis.options.SystemSettingManager.KEY_APPLICATION_TITLE;
import static org.hisp.dhis.options.SystemSettingManager.KEY_DISABLE_DATAENTRYFORM_WHEN_COMPLETED;
import static org.hisp.dhis.options.SystemSettingManager.KEY_FACTOR_OF_DEVIATION;
import static org.hisp.dhis.options.SystemSettingManager.KEY_FLAG;
import static org.hisp.dhis.options.SystemSettingManager.KEY_FORUM_INTEGRATION;
import static org.hisp.dhis.options.SystemSettingManager.KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART;
import static org.hisp.dhis.options.SystemSettingManager.KEY_REPORT_FRAMEWORK;
import static org.hisp.dhis.options.SystemSettingManager.KEY_START_MODULE;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.options.style.StyleManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class SetSystemSettingsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private StyleManager styleManager;

    public void setStyleManager( StyleManager styleManager )
    {
        this.styleManager = styleManager;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private String applicationTitle;

    public void setApplicationTitle( String applicationTitle )
    {
        this.applicationTitle = applicationTitle;
    }

    private String flag;

    public void setFlag( String flag )
    {
        this.flag = flag;
    }

    private String startModule;

    public void setStartModule( String startModule )
    {
        this.startModule = startModule;
    }

    private String reportFramework;

    public void setReportFramework( String reportFramework )
    {
        this.reportFramework = reportFramework;
    }

    private Boolean forumIntegration;

    public void setForumIntegration( Boolean forumIntegration )
    {
        this.forumIntegration = forumIntegration;
    }

    private Boolean omitIndicatorsZeroNumeratorDataMart;

    public void setOmitIndicatorsZeroNumeratorDataMart( Boolean omitIndicatorsZeroNumeratorDataMart )
    {
        this.omitIndicatorsZeroNumeratorDataMart = omitIndicatorsZeroNumeratorDataMart;
    }

    private boolean disableDataEntryWhenCompleted;

    public void setDisableDataEntryWhenCompleted( boolean disableDataEntryWhenCompleted )
    {
        this.disableDataEntryWhenCompleted = disableDataEntryWhenCompleted;
    }

    private Double factorDeviation;

    public void setFactorDeviation( Double factorDeviation )
    {
        this.factorDeviation = factorDeviation;
    }

    private String currentStyle;

    public void setCurrentStyle( String style )
    {
        this.currentStyle = style;
    }
    
    private String aggregationStrategy;

    public void setAggregationStrategy( String aggregationStrategy )
    {
        this.aggregationStrategy = aggregationStrategy;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute()
    {
        applicationTitle = StringUtils.trimToNull( applicationTitle );

        if ( flag != null && flag.equals( "NO_FLAG" ) )
        {
            flag = null;
        }

        if ( startModule != null && startModule.equals( "NO_START_PAGE" ) )
        {
            startModule = null;
        }

        systemSettingManager.saveSystemSetting( KEY_APPLICATION_TITLE, applicationTitle );
        systemSettingManager.saveSystemSetting( KEY_FLAG, flag );
        systemSettingManager.saveSystemSetting( KEY_START_MODULE, startModule );
        systemSettingManager.saveSystemSetting( KEY_REPORT_FRAMEWORK, reportFramework );
        systemSettingManager.saveSystemSetting( KEY_FORUM_INTEGRATION, forumIntegration );
        systemSettingManager.saveSystemSetting( KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART, omitIndicatorsZeroNumeratorDataMart );
        systemSettingManager.saveSystemSetting( KEY_DISABLE_DATAENTRYFORM_WHEN_COMPLETED, disableDataEntryWhenCompleted );
        systemSettingManager.saveSystemSetting( KEY_FACTOR_OF_DEVIATION, factorDeviation );
        styleManager.setCurrentStyle( currentStyle );
        systemSettingManager.saveSystemSetting( KEY_AGGREGATION_STRATEGY, aggregationStrategy );
        
        return SUCCESS;
    }
}
