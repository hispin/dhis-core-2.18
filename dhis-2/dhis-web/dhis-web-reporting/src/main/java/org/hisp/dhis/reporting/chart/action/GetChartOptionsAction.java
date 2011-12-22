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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.comparator.DataElementGroupNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.comparator.IndicatorGroupNameComparator;
import org.hisp.dhis.options.displayproperty.DisplayPropertyHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitLevelComparator;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class GetChartOptionsAction
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

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DisplayPropertyHandler displayPropertyHandler;

    public void setDisplayPropertyHandler( DisplayPropertyHandler displayPropertyHandler )
    {
        this.displayPropertyHandler = displayPropertyHandler;
    }

    private Comparator<Indicator> indicatorComparator;

    public void setIndicatorComparator( Comparator<Indicator> indicatorComparator )
    {
        this.indicatorComparator = indicatorComparator;
    }

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    private Comparator<DataSet> dataSetComparator;

    public void setDataSetComparator( Comparator<DataSet> dataSetComparator )
    {
        this.dataSetComparator = dataSetComparator;
    }

    private Comparator<OrganisationUnit> organisationUnitComparator;

    public void setOrganisationUnitComparator( Comparator<OrganisationUnit> organisationUnitComparator )
    {
        this.organisationUnitComparator = organisationUnitComparator;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String dimension;

    public String getDimension()
    {
        return dimension;
    }

    public void setDimension( String dimension )
    {
        this.dimension = dimension;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Chart chart;

    public Chart getChart()
    {
        return chart;
    }

    private List<IndicatorGroup> indicatorGroups = new ArrayList<IndicatorGroup>();

    public List<IndicatorGroup> getIndicatorGroups()
    {
        return indicatorGroups;
    }

    private List<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>();

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    private List<Indicator> availableIndicators;

    public List<Indicator> getAvailableIndicators()
    {
        return availableIndicators;
    }

    private List<DataElement> availableDataElements;

    public List<DataElement> getAvailableDataElements()
    {
        return availableDataElements;
    }

    private List<DataSet> availableDataSets;

    public List<DataSet> getAvailableDataSets()
    {
        return availableDataSets;
    }

    private List<Indicator> selectedIndicators;

    public List<Indicator> getSelectedIndicators()
    {
        return selectedIndicators;
    }

    private List<DataElement> selectedDataElements;

    public List<DataElement> getSelectedDataElements()
    {
        return selectedDataElements;
    }

    private List<DataSet> selectedDataSets;

    public List<DataSet> getSelectedDataSets()
    {
        return selectedDataSets;
    }

    private List<PeriodType> periodTypes = new ArrayList<PeriodType>();

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    private List<OrganisationUnitLevel> levels = new ArrayList<OrganisationUnitLevel>();

    public List<OrganisationUnitLevel> getLevels()
    {
        return levels;
    }

    private List<OrganisationUnit> availableOrganisationUnits;

    public List<OrganisationUnit> getAvailableOrganisationUnits()
    {
        return availableOrganisationUnits;
    }

    private List<OrganisationUnit> selectedOrganisationUnits;

    public List<OrganisationUnit> getSelectedOrganisationUnits()
    {
        return selectedOrganisationUnits;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        indicatorGroups = new ArrayList<IndicatorGroup>( indicatorService.getAllIndicatorGroups() );

        dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );

        availableIndicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );

        availableDataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );

        availableDataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );

        levels = organisationUnitService.getOrganisationUnitLevels();

        availableOrganisationUnits = new ArrayList<OrganisationUnit>(
            organisationUnitService.getOrganisationUnitsAtLevel( 1 ) );

        Collections.sort( indicatorGroups, new IndicatorGroupNameComparator() );
        Collections.sort( dataElementGroups, new DataElementGroupNameComparator() );
        Collections.sort( availableIndicators, indicatorComparator );
        Collections.sort( availableDataElements, dataElementComparator );
        Collections.sort( availableDataSets, dataSetComparator );
        Collections.sort( levels, new OrganisationUnitLevelComparator() );
        Collections.sort( availableOrganisationUnits, organisationUnitComparator );

        displayPropertyHandler.handle( availableIndicators );
        displayPropertyHandler.handle( availableDataElements );
        displayPropertyHandler.handle( availableDataSets );
        displayPropertyHandler.handle( availableOrganisationUnits );

        if ( id != null )
        {
            chart = chartService.getChart( id );

            selectedIndicators = chart.getIndicators();
            availableIndicators.removeAll( selectedIndicators );

            selectedDataElements = chart.getDataElements();
            availableDataElements.removeAll( selectedDataElements );

            selectedDataSets = chart.getDataSets();
            availableDataSets.removeAll( selectedDataSets );

            selectedOrganisationUnits = chart.getOrganisationUnits();
            availableOrganisationUnits.removeAll( selectedOrganisationUnits );

            displayPropertyHandler.handle( selectedIndicators );
            displayPropertyHandler.handle( selectedDataElements );
            displayPropertyHandler.handle( selectedDataSets );
            displayPropertyHandler.handle( selectedOrganisationUnits );
        }

        return SUCCESS;
    }
}
