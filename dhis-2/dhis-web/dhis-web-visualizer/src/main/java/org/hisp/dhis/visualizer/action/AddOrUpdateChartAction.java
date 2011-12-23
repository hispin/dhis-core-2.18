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

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Jan Henrik Overland
 */
public class AddOrUpdateChartAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String uid;

    public void setUid( String uid )
    {
        this.uid = uid;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private String series;

    public void setSeries( String series )
    {
        this.series = series;
    }

    private String category;

    public void setCategory( String category )
    {
        this.category = category;
    }

    private String filter;

    public void setFilter( String filter )
    {
        this.filter = filter;
    }

    private Collection<Integer> indicatorIds;

    public void setIndicatorIds( Collection<Integer> indicatorIds )
    {
        this.indicatorIds = indicatorIds;
    }

    private Collection<Integer> dataElementIds;

    public void setDataElementIds( Collection<Integer> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    private boolean lastMonth;

    public void setLastMonth( boolean lastMonth )
    {
        this.lastMonth = lastMonth;
    }

    private boolean last12Months;

    public void setLast12Months( boolean last12Months )
    {
        this.last12Months = last12Months;
    }

    private boolean lastQuarter;

    public void setLastQuarter( boolean lastQuarter )
    {
        this.lastQuarter = lastQuarter;
    }

    private boolean last4Quarters;

    public void setLast4Quarters( boolean last4Quarters )
    {
        this.last4Quarters = last4Quarters;
    }

    private boolean lastSixMonth;

    public void setLastSixMonth( boolean lastSixMonth )
    {
        this.lastSixMonth = lastSixMonth;
    }

    private boolean last2SixMonths;

    public void setLast2SixMonths( boolean last2SixMonths )
    {
        this.last2SixMonths = last2SixMonths;
    }

    private boolean thisYear;

    public void setThisYear( boolean thisYear )
    {
        this.thisYear = thisYear;
    }

    private boolean last5Years;

    public void setLast5Years( boolean last5Years )
    {
        this.last5Years = last5Years;
    }

    private Collection<Integer> organisationUnitIds;

    public void setOrganisationUnitIds( Collection<Integer> organisationUnitIds )
    {
        this.organisationUnitIds = organisationUnitIds;
    }
    
    private Boolean system;

    public void setSystem( Boolean system )
    {
        this.system = system;
    }
    
    private Boolean trendLine;
    
    public void setTrendLine( Boolean trendLine )
    {
        this.trendLine = trendLine;
    }
    
    private Boolean hideSubtitle;
    
    public void setHideSubtitle( Boolean hideSubtitle )
    {
        this.hideSubtitle = hideSubtitle;
    }

    private Boolean hideLegend;

    public void setHideLegend( Boolean hideLegend )
    {
        this.hideLegend = hideLegend;
    }
    
    private Boolean userOrganisationUnit;

    public void setUserOrganisationUnit( Boolean userOrganisationUnit )
    {
        this.userOrganisationUnit = userOrganisationUnit;
    }
    
    private String xAxisLabel;

    public void setXAxisLabel( String xAxisLabel )
    {
        this.xAxisLabel = xAxisLabel;
    }
    
    private String yAxisLabel;

    public void setYAxisLabel( String yAxisLabel )
    {
        this.yAxisLabel = yAxisLabel;
    }
    
    private Double targetLineValue;

    public void setTargetLineValue( Double targetLineValue )
    {
        this.targetLineValue = targetLineValue;
    }

    private String targetLineLabel;

    public void setTargetLineLabel( String targetLineLabel )
    {
        this.targetLineLabel = targetLineLabel;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Chart chart = null;

        if ( uid != null )
        {
            chart = chartService.getChart( uid );
        }
        else
        {
            chart = new Chart();
        }

        if ( name != null )
        {
            chart.setName( name );
        }

        if ( type != null )
        {
            chart.setType( type );
        }

        if ( series != null )
        {
            chart.setSeries( series );
        }

        if ( category != null )
        {
            chart.setCategory( category );
        }

        if ( filter != null )
        {
            chart.setFilter( filter );
        }

        if ( indicatorIds != null )
        {
            chart.setIndicators( new ArrayList<Indicator>( indicatorService.getIndicators( indicatorIds ) ) );
        }

        if ( dataElementIds != null )
        {
            chart.setDataElements( new ArrayList<DataElement>( dataElementService.getDataElements( dataElementIds ) ) );
        }

        if ( lastMonth || last12Months || lastQuarter || last4Quarters || lastSixMonth || last2SixMonths
            || thisYear || last5Years )
        {
            RelativePeriods rp = new RelativePeriods();
            rp.setReportingMonth( lastMonth );
            rp.setLast12Months( last12Months );
            rp.setReportingQuarter( lastQuarter );
            rp.setLast4Quarters( last4Quarters );
            rp.setLastSixMonth( lastSixMonth );
            rp.setLast2SixMonths( last2SixMonths );
            rp.setThisYear( thisYear );
            rp.setLast5Years( last5Years );

            chart.setRelatives( rp );
        }

        if ( organisationUnitIds != null )
        {
            chart.setOrganisationUnits( new ArrayList<OrganisationUnit>( organisationUnitService
                .getOrganisationUnits( organisationUnitIds ) ) );
        }
        
        if ( system == null )
        {
            chart.setUser( currentUserService.getCurrentUser() );
        }
        
        if ( trendLine != null )
        {
            chart.setRegression( trendLine );
        }
        
        if ( hideSubtitle != null )
        {
            chart.setHideSubtitle( hideSubtitle );
        }
        
        if ( hideLegend != null )
        {
            chart.setHideLegend( hideLegend );
        }
        
        if ( userOrganisationUnit != null )
        {
            chart.setUserOrganisationUnit( userOrganisationUnit );
        }
        
        if ( xAxisLabel != null )
        {
            chart.setDomainAxisLabel( xAxisLabel );
        }
        
        if ( yAxisLabel != null )
        {
            chart.setRangeAxisLabel( yAxisLabel );
        }
        
        if ( targetLineValue != null )
        {
            chart.setTargetLineValue( targetLineValue );
        }
        
        if ( targetLineLabel != null )
        {
            chart.setTargetLineLabel( targetLineLabel );
        }

        chartService.saveOrUpdate( chart );

        return SUCCESS;
    }
}
