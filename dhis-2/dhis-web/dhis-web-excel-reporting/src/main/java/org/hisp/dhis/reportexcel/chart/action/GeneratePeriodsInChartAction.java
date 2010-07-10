package org.hisp.dhis.reportexcel.chart.action;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Tran Thanh Tri
 */

public class GeneratePeriodsInChartAction
    extends GenerateChartSupportAction
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    @Override
    public String execute()
        throws Exception
    {
        statementManager.initialise();

        values = new HashMap<String, List<Double>>();

        keys = new ArrayList<String>();

        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        Indicator indicator = indicatorService.getIndicator( yaxis );

        for ( Integer p : xaxis )
        {
            Period period = periodService.getPeriod( p );

            String name = format.formatPeriod( period );

            keys.add( name );
            
            System.out.println("Indicator:" + indicator);
            System.out.println("Period:" + period);
            System.out.println("Organisation Unit:" + organisationUnit.getName());

            Double value = aggregationService.getAggregatedIndicatorValue( indicator, period.getStartDate(), period
                .getEndDate(), organisationUnit );
            
            Double total = aggregationService.getAggregatedIndicatorValue( indicator, period.getStartDate(), period
                .getEndDate(), organisationUnit.getParent() );

            if ( value == null )
            {
                value = 0.0;
            }

            if ( total == null )
            {
                total = 0.0;
            }

            List<Double> values_ = new ArrayList<Double>();
            values_.add( value );
            values_.add( total );

            values.put( name, values_ );

        }

        statementManager.destroy();

        return SUCCESS;
    }

}
