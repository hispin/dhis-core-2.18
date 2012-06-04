package org.hisp.dhis.api.controller;

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

import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.api.webdomain.ChartPluginValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hisp.dhis.api.utils.ContextUtils.CONTENT_TYPE_JSON;
import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.DateUtils.setNames;

@Controller
@RequestMapping( value = ChartPluginController.RESOURCE_PATH )
public class ChartPluginController
{
    public static final String RESOURCE_PATH = "/chartValues";

    @Autowired
    private AggregatedDataValueService aggregatedDataValueService;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private I18nManager i18nManager;

    @Autowired
    private ContextUtils contextUtils;

    @RequestMapping( method = RequestMethod.GET )
    public String getChartValues( @RequestParam( required = false ) Set<String> indicatorIds,
        @RequestParam( required = false ) Set<String> dataElementIds,
        @RequestParam Set<String> organisationUnitIds,
        @RequestParam( required = false ) boolean orgUnitIsParent,
        @RequestParam( required = false ) boolean userOrganisationUnit,
        @RequestParam( required = false ) boolean userOrganisationUnitChildren,
        RelativePeriods relativePeriods, Model model, HttpServletResponse response ) throws Exception
    {
        ChartPluginValue chartValue = new ChartPluginValue();

        I18nFormat format = i18nManager.getI18nFormat();

        // ---------------------------------------------------------------------
        // Periods
        // ---------------------------------------------------------------------

        List<Period> periods = periodService.reloadPeriods( setNames( relativePeriods.getRelativePeriods(), format ) );

        if ( periods.isEmpty() )
        {
            ContextUtils.conflictResponse( response, "No valid periods specified" );
            return null;
        }

        for ( Period period : periods )
        {
            chartValue.getPeriods().add( period.getName() );
        }

        // ---------------------------------------------------------------------
        // Organisation units
        // ---------------------------------------------------------------------

        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();

        if ( userOrganisationUnit || userOrganisationUnitChildren )
        {
            if ( userOrganisationUnit )
            {
                organisationUnits.add( currentUserService.getCurrentUser().getOrganisationUnit() );
            }

            if ( userOrganisationUnitChildren )
            {
                organisationUnits.addAll( new ArrayList<OrganisationUnit>( currentUserService.getCurrentUser()
                    .getOrganisationUnit().getChildren() ) );
            }
        }
        else
        {
            organisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitsByUid( organisationUnitIds ) );
        }

        if ( organisationUnits.isEmpty() )
        {
            ContextUtils.conflictResponse( response, "No valid organisation units specified" );
            return null;
        }

        if ( orgUnitIsParent )
        {
            List<OrganisationUnit> childOrganisationUnits = new ArrayList<OrganisationUnit>();

            for ( OrganisationUnit unit : organisationUnits )
            {
                childOrganisationUnits.addAll( unit.getChildren() );
            }

            organisationUnits = childOrganisationUnits;
        }

        for ( OrganisationUnit unit : organisationUnits )
        {
            chartValue.getOrgUnits().add( unit.getName() );
        }

        // ---------------------------------------------------------------------
        // Indicators
        // ---------------------------------------------------------------------

        if ( indicatorIds != null )
        {
            List<Indicator> indicators = indicatorService.getIndicatorsByUid( indicatorIds );

            if ( indicators.isEmpty() )
            {
                ContextUtils.conflictResponse( response, "No valid indicators specified" );
                return null;
            }

            for ( Indicator indicator : indicators )
            {
                chartValue.getData().add( indicator.getDisplayShortName() );
            }

            Collection<AggregatedIndicatorValue> indicatorValues = aggregatedDataValueService
                .getAggregatedIndicatorValues( getIdentifiers( Indicator.class, indicators ),
                    getIdentifiers( Period.class, periods ), getIdentifiers( OrganisationUnit.class, organisationUnits ) );

            for ( AggregatedIndicatorValue value : indicatorValues )
            {
                String[] record = new String[4];

                record[0] = String.valueOf( value.getValue() );
                record[1] = indicatorService.getIndicator( value.getIndicatorId() ).getDisplayShortName();
                record[2] = format.formatPeriod( periodService.getPeriod( value.getPeriodId() ) );
                record[3] = organisationUnitService.getOrganisationUnit( value.getOrganisationUnitId() ).getName();

                chartValue.getValues().add( record );
            }
        }

        // ---------------------------------------------------------------------
        // Data elements
        // ---------------------------------------------------------------------

        if ( dataElementIds != null )
        {
            List<DataElement> dataElements = dataElementService.getDataElementsByUid( dataElementIds );

            if ( dataElements.isEmpty() )
            {
                ContextUtils.conflictResponse( response, "No valid data elements specified" );
                return null;
            }

            for ( DataElement element : dataElements )
            {
                chartValue.getData().add( element.getDisplayShortName() );
            }

            Collection<AggregatedDataValue> dataValues = aggregatedDataValueService.getAggregatedDataValueTotals(
                getIdentifiers( DataElement.class, dataElements ), getIdentifiers( Period.class, periods ),
                getIdentifiers( OrganisationUnit.class, organisationUnits ) );

            for ( AggregatedDataValue value : dataValues )
            {
                String[] record = new String[4];

                record[0] = String.valueOf( value.getValue() );
                record[1] = dataElementService.getDataElement( value.getDataElementId() ).getDisplayShortName();
                record[2] = format.formatPeriod( periodService.getPeriod( value.getPeriodId() ) );
                record[3] = organisationUnitService.getOrganisationUnit( value.getOrganisationUnitId() ).getName();

                chartValue.getValues().add( record );
            }
        }

        contextUtils.configureResponse( response, CONTENT_TYPE_JSON, CacheStrategy.RESPECT_SYSTEM_SETTING, null, false );

        model.addAttribute( "model", chartValue );

        return "chartValues";
    }
}
