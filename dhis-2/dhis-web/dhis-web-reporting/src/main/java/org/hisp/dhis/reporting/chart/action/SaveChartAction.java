package org.hisp.dhis.reporting.chart.action;

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

import static org.hisp.dhis.system.util.ConversionUtils.getIntegerCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.comparator.AscendingPeriodComparator;
import org.hisp.dhis.period.RelativePeriods;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id: UploadDesignAction.java 5207 2008-05-22 12:16:36Z larshelg $
 */
public class SaveChartAction
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
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private String title;

    public void setTitle( String title )
    {
        this.title = title;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }
    
    private String size;

    public void setSize( String size )
    {
        this.size = size;
    }

    private String dimension;

    public void setDimension( String dimension )
    {
        this.dimension = dimension;
    }
    
    private boolean hideLegend;

    public void setHideLegend( boolean hideLegend )
    {
        this.hideLegend = hideLegend;
    }
    
    private boolean verticalLabels;

    public void setVerticalLabels( boolean verticalLabels )
    {
        this.verticalLabels = verticalLabels;
    }
    
    private boolean horizontalPlotOrientation;

    public void setHorizontalPlotOrientation( boolean horizontalPlotOrientation )
    {
        this.horizontalPlotOrientation = horizontalPlotOrientation;
    }

    private boolean regression;

    public void setRegression( boolean regression )
    {
        this.regression = regression;
    }
    
    private List<String> selectedIndicators = new ArrayList<String>();

    public void setSelectedIndicators( List<String> selectedIndicators )
    {
        this.selectedIndicators = selectedIndicators;
    }

    private List<String> selectedPeriods = new ArrayList<String>();

    public void setSelectedPeriods( List<String> selectedPeriods )
    {
        this.selectedPeriods = selectedPeriods;
    }

    private List<String> selectedOrganisationUnits = new ArrayList<String>();

    public void setSelectedOrganisationUnits( List<String> selectedOrganisationUnits )
    {
        this.selectedOrganisationUnits = selectedOrganisationUnits;
    }

    private boolean reportingMonth;

    public void setReportingMonth( boolean reportingMonth )
    {
        this.reportingMonth = reportingMonth;
    }

    private boolean last3Months;

    public void setLast3Months( boolean last3Months )
    {
        this.last3Months = last3Months;
    }

    private boolean last6Months;

    public void setLast6Months( boolean last6Months )
    {
        this.last6Months = last6Months;
    }
    
    private boolean last12Months;

    public void setLast12Months( boolean last12Months )
    {
        this.last12Months = last12Months;
    }
    
    private boolean soFarThisYear;

    public void setSoFarThisYear( boolean soFarThisYear )
    {
        this.soFarThisYear = soFarThisYear;
    }
    
    private boolean last3To6Months;

    public void setLast3To6Months( boolean last3To6Months )
    {
        this.last3To6Months = last3To6Months;
    }

    private boolean last6To9Months;

    public void setLast6To9Months( boolean last6To9Months )
    {
        this.last6To9Months = last6To9Months;
    }

    private boolean last9To12Months;

    public void setLast9To12Months( boolean last9To12Months )
    {
        this.last9To12Months = last9To12Months;
    }
    
    private boolean last12IndividualMonths;

    public void setLast12IndividualMonths( boolean last12IndividualMonths )
    {
        this.last12IndividualMonths = last12IndividualMonths;
    }
    
    private boolean individualMonthsThisYear;

    public void setIndividualMonthsThisYear( boolean individualMonthsThisYear )
    {
        this.individualMonthsThisYear = individualMonthsThisYear;
    }

    private boolean individualQuartersThisYear;

    public void setIndividualQuartersThisYear( boolean individualQuartersThisYear )
    {
        this.individualQuartersThisYear = individualQuartersThisYear;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Chart chart = id == null ? new Chart() : chartService.getChart( id );

        List<Indicator> indicators = new ArrayList<Indicator>();
        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();
        List<Period> periods = new ArrayList<Period>( periodService.getPeriodsByExternalIds( selectedPeriods ) );
 System.out.println("\n\n ======= \n periods : " + periods);       
        for ( Integer id : getIntegerCollection( selectedIndicators ) )
        {
            indicators.add( indicatorService.getIndicator( id ) );
        }
        
        for ( Integer id : getIntegerCollection( selectedOrganisationUnits ) )
        {
            organisationUnits.add( organisationUnitService.getOrganisationUnit( id ) );
        }
        
        Collections.sort( periods, new AscendingPeriodComparator() );
        
        chart.setTitle( title );
        chart.setType( type );
        chart.setSize( size );
        chart.setDimension( dimension );
        chart.setHideLegend( hideLegend );
        chart.setVerticalLabels( verticalLabels );
        chart.setHorizontalPlotOrientation( horizontalPlotOrientation );
        chart.setRegression( regression );
        chart.setIndicators( indicators );
        chart.setPeriods( periods );
        chart.setOrganisationUnits( organisationUnits );

        RelativePeriods relatives = new RelativePeriods();
        
        relatives.setReportingMonth( reportingMonth );
        relatives.setLast3Months( last3Months );
        relatives.setLast6Months( last6Months );
        relatives.setLast12Months( last12Months );
        relatives.setSoFarThisYear( soFarThisYear );
        relatives.setLast3To6Months( last3To6Months );
        relatives.setLast6To9Months( last6To9Months );
        relatives.setLast9To12Months( last9To12Months );
        relatives.setLast12IndividualMonths( last12IndividualMonths );
        relatives.setIndividualMonthsThisYear( individualMonthsThisYear );
        relatives.setIndividualQuartersThisYear( individualQuartersThisYear );
        
        chart.setRelatives( relatives );
        
        chartService.saveChart( chart );
        
        return SUCCESS;
    }
}
