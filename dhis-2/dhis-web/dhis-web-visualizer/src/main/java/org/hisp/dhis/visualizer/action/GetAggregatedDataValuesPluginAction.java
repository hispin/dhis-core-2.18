package org.hisp.dhis.visualizer.action;

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
import java.util.Collection;

import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;

import com.opensymphony.xwork2.Action;

/**
 * @author Jan Henrik Overland
 */
public class GetAggregatedDataValuesPluginAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Collection<Integer> dataElementIds;

    public void setDataElementIds( Collection<Integer> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    private Collection<Integer> organisationUnitIds;

    public void setOrganisationUnitIds( Collection<Integer> organisationUnitIds )
    {
        this.organisationUnitIds = organisationUnitIds;
    }

    private Boolean lastMonth;

    public void setLastMonth( Boolean lastMonth )
    {
        this.lastMonth = lastMonth;
    }

    private Boolean monthsThisYear;

    public void setMonthsThisYear( Boolean monthsThisYear )
    {
        this.monthsThisYear = monthsThisYear;
    }

    private Boolean monthsLastYear;

    public void setMonthsLastYear( Boolean monthsLastYear )
    {
        this.monthsLastYear = monthsLastYear;
    }

    private Boolean lastQuarter;

    public void setLastQuarter( Boolean lastQuarter )
    {
        this.lastQuarter = lastQuarter;
    }

    private Boolean quartersThisYear;

    public void setQuartersThisYear( Boolean quartersThisYear )
    {
        this.quartersThisYear = quartersThisYear;
    }

    private Boolean quartersLastYear;

    public void setQuartersLastYear( Boolean quartersLastYear )
    {
        this.quartersLastYear = quartersLastYear;
    }

    private Boolean thisYear;

    public void setThisYear( Boolean thisYear )
    {
        this.thisYear = thisYear;
    }

    private Boolean lastYear;

    public void setLastYear( Boolean lastYear )
    {
        this.lastYear = lastYear;
    }

    private Boolean lastFiveYears;

    public void setLastFiveYears( Boolean lastFiveYears )
    {
        this.lastFiveYears = lastFiveYears;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<AggregatedDataValue> object;

    public Collection<AggregatedDataValue> getObject()
    {
        return object;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( dataElementIds != null
            && organisationUnitIds != null
            && (lastMonth || monthsThisYear || monthsLastYear || lastQuarter || quartersThisYear || quartersLastYear
                || thisYear || lastYear || lastFiveYears) )
        {
            RelativePeriods rp = new RelativePeriods();
            rp.setReportingMonth( lastMonth );
            rp.setMonthsThisYear( monthsThisYear );
            rp.setMonthsLastYear( monthsLastYear );
            rp.setReportingQuarter( lastQuarter );
            rp.setQuartersThisYear( quartersThisYear );
            rp.setQuartersLastYear( quartersLastYear );
            rp.setThisYear( thisYear );
            rp.setLastYear( lastYear );
            rp.setLast5Years( lastFiveYears );

            Collection<Period> periods = rp.getRelativePeriods();

            Collection<Integer> periodIds = new ArrayList<Integer>();

            for ( Period period : periods )
            {
                periodIds.add( period.getId() );
            }

            object = aggregatedDataValueService
                .getAggregatedDataValueTotals( dataElementIds, periodIds, organisationUnitIds );

            for ( AggregatedDataValue value : object )
            {
                value.setDataElementName( dataElementService.getDataElement( value.getDataElementId() ).getName() );
                value.setPeriodName( format.formatPeriod( periodService.getPeriod( value.getPeriodId() ) ) );
                value.setOrganisationUnitName( organisationUnitService.getOrganisationUnit(
                    value.getOrganisationUnitId() ).getName() );
            }
        }

        return SUCCESS;
    }
}
