package org.hisp.dhis.reporting.dataset.action;

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

import static org.hisp.dhis.options.SystemSettingManager.AGGREGATION_STRATEGY_REAL_TIME;
import static org.hisp.dhis.options.SystemSettingManager.DEFAULT_AGGREGATION_STRATEGY;
import static org.hisp.dhis.options.SystemSettingManager.KEY_AGGREGATION_STRATEGY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.comparator.DataElementCategoryOptionComboNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.filter.AggregatableDataElementFilter;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.FilterUtils;
import org.hisp.dhis.system.util.MathUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 */
public class GenerateDefaultDataSetReportAction
    implements Action
{
    private static final String DEFAULT_HEADER = "Value";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataValueService dataValueService;

    private SystemSettingManager systemSettingManager;

    private AggregatedDataValueService aggregatedDataValueService;

    private AggregationService aggregationService;

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private OrganisationUnit selectedOrgunit;

    private DataSet selectedDataSet;

    private Period selectedPeriod;

    private boolean selectedUnitOnly;

    private I18nFormat format;

    private I18n i18n;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private Grid grid;

    private String reportingUnit;

    private String reportingPeriod;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    public void setSelectedOrgunit( OrganisationUnit selectedOrgunit )
    {
        this.selectedOrgunit = selectedOrgunit;
    }

    public void setSelectedDataSet( DataSet selectedDataSet )
    {
        this.selectedDataSet = selectedDataSet;
    }

    public void setSelectedPeriod( Period selectedPeriod )
    {
        this.selectedPeriod = selectedPeriod;
    }

    public String getReportingUnit()
    {
        return reportingUnit;
    }

    public String getReportingPeriod()
    {
        return reportingPeriod;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public Grid getGrid()
    {
        return grid;
    }

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    public void setSelectedUnitOnly( boolean selectedUnitOnly )
    {
        this.selectedUnitOnly = selectedUnitOnly;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        List<DataElement> dataElements = new ArrayList<DataElement>( selectedDataSet.getDataElements() );

        if ( dataElements.size() == 0 )
        {
            return SUCCESS;
        }

        Collections.sort( dataElements, dataElementComparator );
        FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );

        String aggregationStrategy = (String) systemSettingManager.getSystemSetting( KEY_AGGREGATION_STRATEGY,
            DEFAULT_AGGREGATION_STRATEGY );

        // ---------------------------------------------------------------------
        // Get the category-option-combos
        // ---------------------------------------------------------------------

        Set<DataElementCategoryOptionCombo> optionCombos = new HashSet<DataElementCategoryOptionCombo>();

        for ( DataElement dataElement : dataElements )
        {
            optionCombos.addAll( dataElement.getCategoryCombo().getOptionCombos() );
        }

        List<DataElementCategoryOptionCombo> orderedOptionCombos = new ArrayList<DataElementCategoryOptionCombo>(
            optionCombos );

        Collections.sort( orderedOptionCombos, new DataElementCategoryOptionComboNameComparator() );

        // ---------------------------------------------------------------------
        // Create a GRID
        // ---------------------------------------------------------------------

        grid = new ListGrid().setTitle( selectedDataSet.getName() );
        grid.setSubtitle( format.formatPeriod( selectedPeriod ) );

        // ---------------------------------------------------------------------
        // Headers for GRID
        // ---------------------------------------------------------------------

        grid.addHeader( new GridHeader( i18n.getString( "dataelement" ), false, true ) );
         
        for ( DataElementCategoryOptionCombo optionCombo : orderedOptionCombos )
        {
            grid.addHeader( new GridHeader( optionCombo.isDefault() ? DEFAULT_HEADER : optionCombo.getName(), false, false ) );
        }

        // ---------------------------------------------------------------------
        // Values for GRID
        // ---------------------------------------------------------------------

        for ( DataElement dataElement : dataElements )
        {
            grid.addRow();

            grid.addValue( dataElement.getName() );

            for ( DataElementCategoryOptionCombo optionCombo : orderedOptionCombos )
            {
                String value = "";

                if ( selectedUnitOnly )
                {
                    DataValue dataValue = dataValueService.getDataValue( selectedOrgunit, dataElement,
                        selectedPeriod, optionCombo );
                    value = (dataValue != null) ? dataValue.getValue() : null;
                }
                else
                {
                    Double aggregatedValue = aggregationStrategy.equals( AGGREGATION_STRATEGY_REAL_TIME ) ? aggregationService
                        .getAggregatedDataValue( dataElement, optionCombo, selectedPeriod.getStartDate(),
                            selectedPeriod.getEndDate(), selectedOrgunit )
                        : aggregatedDataValueService.getAggregatedValue( dataElement, optionCombo, selectedPeriod,
                            selectedOrgunit );

                    value = (aggregatedValue != null) ? String.valueOf( MathUtils.getRounded( aggregatedValue, 0 ) )
                        : null;
                }

                grid.addValue( value );

            }
        }

        reportingUnit = selectedOrgunit.getName();

        reportingPeriod = format.formatPeriod( selectedPeriod );

        return SUCCESS;
    }
}
