package org.hisp.dhis.datasetreport.impl;

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

import static org.hisp.dhis.dataentryform.DataEntryFormService.IDENTIFIER_PATTERN;
import static org.hisp.dhis.dataentryform.DataEntryFormService.INDICATOR_PATTERN;
import static org.hisp.dhis.dataentryform.DataEntryFormService.INPUT_PATTERN;
import static org.hisp.dhis.options.SystemSettingManager.AGGREGATION_STRATEGY_REAL_TIME;
import static org.hisp.dhis.options.SystemSettingManager.DEFAULT_AGGREGATION_STRATEGY;
import static org.hisp.dhis.options.SystemSettingManager.KEY_AGGREGATION_STRATEGY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;

import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.comparator.DataElementCategoryOptionComboNameComparator;
import org.hisp.dhis.dataelement.comparator.DataElementNameComparator;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;
import org.hisp.dhis.datasetreport.DataSetReportService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.filter.AggregatableDataElementFilter;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.FilterUtils;

/**
 * @author Abyot Asalefew
 * @author Lars Helge Overland
 */
public class DefaultDataSetReportService
    implements DataSetReportService
{        
    private static final String NULL_REPLACEMENT = "";
    private static final String SEPARATOR = ":";
    private static final String DEFAULT_HEADER = "Value";
    private static final String TOTAL_HEADER = "Total";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataValueService dataValueService;
    
    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }
    
    private AggregatedDataValueService aggregatedDataValueService;
    
    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }
    
    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // DataSetReportService implementation
    // -------------------------------------------------------------------------
    
    public String getCustomDataSetReport( DataSet dataSet, OrganisationUnit unit, Period period, boolean selectedUnitOnly, I18nFormat format )
    {
        Map<String, String> aggregatedDataValueMap = getAggregatedValueMap( dataSet, unit, period, selectedUnitOnly, format );

        Map<Integer, String> aggregatedIndicatorMap = getAggregatedIndicatorValueMap( dataSet, unit, period, format );
        
        return prepareReportContent( dataSet.getDataEntryForm(), aggregatedDataValueMap, aggregatedIndicatorMap ); 
    }
    
    public List<Grid> getSectionDataSetReport( DataSet dataSet, Period period, OrganisationUnit unit, boolean selectedUnitOnly, I18nFormat format, I18n i18n )
    {
        String aggregationStrategy = (String) systemSettingManager.getSystemSetting( KEY_AGGREGATION_STRATEGY, DEFAULT_AGGREGATION_STRATEGY );
        boolean realTime = aggregationStrategy.equals( AGGREGATION_STRATEGY_REAL_TIME );
        
        List<Section> sections = new ArrayList<Section>( dataSet.getSections() );
        Collections.sort( sections, new SectionOrderComparator() );

        List<Grid> grids = new ArrayList<Grid>();
        
        // ---------------------------------------------------------------------
        // Create a grid for each section
        // ---------------------------------------------------------------------

        for ( Section section : sections )
        {
            Grid grid = new ListGrid().setTitle( section.getName() );

            DataElementCategoryCombo categoryCombo = section.getCategoryCombo();
            
            // -----------------------------------------------------------------
            // Grid headers
            // -----------------------------------------------------------------

            grid.addHeader( new GridHeader( i18n.getString( "dataelement" ), false, true ) ); // Data element header

            List<DataElementCategoryOptionCombo> optionCombos = categoryCombo.getSortedOptionCombos();

            for ( DataElementCategoryOptionCombo optionCombo : optionCombos ) // Value headers
            {
                grid.addHeader( new GridHeader( optionCombo.isDefault() ? DEFAULT_HEADER : optionCombo.getName(), false, false ) );
            }

            if ( categoryCombo.doSubTotals() && !selectedUnitOnly ) // Sub-total headers
            {
                for ( DataElementCategoryOption categoryOption : categoryCombo.getCategoryOptions() )
                {
                    grid.addHeader( new GridHeader( categoryOption.getName(), false, false ) );
                }
            }
            
            if ( categoryCombo.doTotal() && !selectedUnitOnly ) // Total header
            {
                grid.addHeader( new GridHeader( TOTAL_HEADER, false, false ) );
            }
            
            // -----------------------------------------------------------------
            // Grid values
            // -----------------------------------------------------------------

            List<DataElement> dataElements = new ArrayList<DataElement>( section.getDataElements() );
            FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );

            for ( DataElement dataElement : dataElements )
            {
                grid.addRow();
                grid.addValue( dataElement.getName() ); // Data element name

                for ( DataElementCategoryOptionCombo optionCombo : optionCombos ) // Values
                {
                    Double value = null;

                    if ( selectedUnitOnly )
                    {
                        DataValue dataValue = dataValueService.getDataValue( unit, dataElement, period, optionCombo );
                        value = dataValue != null && dataValue.getValue() != null ? Double.parseDouble( dataValue.getValue() ) : null;
                    }
                    else
                    {
                        value = realTime ? 
                            aggregationService.getAggregatedDataValue( dataElement, optionCombo, period.getStartDate(), period.getEndDate(), unit ) : 
                            aggregatedDataValueService.getAggregatedValue( dataElement, optionCombo, period, unit );
                    }
                    
                    grid.addValue( value );
                }
                
                if ( categoryCombo.doSubTotals() && !selectedUnitOnly ) // Sub-total values
                {
                    for ( DataElementCategoryOption categoryOption : categoryCombo.getCategoryOptions() )
                    {
                        Double value = realTime ? 
                            aggregationService.getAggregatedDataValue( dataElement, period.getStartDate(), period.getEndDate(), unit, categoryOption ) : 
                            aggregatedDataValueService.getAggregatedValue( dataElement, categoryOption, period, unit );

                        grid.addValue( value );
                    }
                }
                
                if ( categoryCombo.doTotal() && !selectedUnitOnly ) // Total value
                {
                    Double value = realTime ?
                        aggregationService.getAggregatedDataValue( dataElement, null, period.getStartDate(), period.getEndDate(), unit ) :
                        aggregatedDataValueService.getAggregatedValue( dataElement, period, unit );
                        
                    grid.addValue( value );
                }
            }

            grids.add( grid );
        }

        return grids;
    }
    
    public Grid getDefaultDataSetReport( DataSet dataSet, Period period, OrganisationUnit unit, boolean selectedUnitOnly, I18nFormat format, I18n i18n )
    {
        List<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );

        Collections.sort( dataElements, new DataElementNameComparator() );
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

        Grid grid = new ListGrid().setTitle( dataSet.getName() );
        grid.setSubtitle( format.formatPeriod( period ) );

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
                Double value = null;

                if ( selectedUnitOnly )
                {
                    DataValue dataValue = dataValueService.getDataValue( unit, dataElement, period, optionCombo );
                    value = dataValue != null && dataValue.getValue() != null ? Double.parseDouble( dataValue.getValue() ) : null;
                }
                else
                {
                    value = aggregationStrategy.equals( AGGREGATION_STRATEGY_REAL_TIME ) ? 
                        aggregationService.getAggregatedDataValue( dataElement, optionCombo, period.getStartDate(), period.getEndDate(), unit ) : 
                        aggregatedDataValueService.getAggregatedValue( dataElement, optionCombo, period, unit );
                }

                grid.addValue( value );

            }
        }
        
        return grid;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    /**
     * Generates a map with aggregated data values or regular data values (depending
     * on the selectedUnitOnly argument) mapped to a DataElemenet operand identifier.
     * 
     * @param dataSet the DataSet.
     * @param unit the OrganisationUnit.
     * @param period the Period.
     * @param selectedUnitOnly whether to include aggregated or regular data in the map.
     * @param format the I18nFormat.
     * @return a map.
     */
    private Map<String, String> getAggregatedValueMap( DataSet dataSet, OrganisationUnit unit, Period period, boolean selectedUnitOnly, I18nFormat format )
    {
        String aggregationStrategy = (String) systemSettingManager.getSystemSetting( KEY_AGGREGATION_STRATEGY, DEFAULT_AGGREGATION_STRATEGY );
        
        Collection<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements());
        
        FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );
        
        Map<String, String> map = new TreeMap<String, String>();            
        
        for ( DataElement dataElement : dataElements )
        {
            for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getOptionCombos() )
            {
                String value;
                
                if ( selectedUnitOnly )
                {                                   
                    DataValue dataValue = dataValueService.getDataValue( unit, dataElement, period, categoryOptionCombo );                                  
                    value = ( dataValue != null ) ? dataValue.getValue() : null;
                }
                else
                {
                    Double aggregatedValue = aggregationStrategy.equals( AGGREGATION_STRATEGY_REAL_TIME ) ? 
                        aggregationService.getAggregatedDataValue( dataElement, categoryOptionCombo, period.getStartDate(), period.getEndDate(), unit ) :
                            aggregatedDataValueService.getAggregatedValue( dataElement, categoryOptionCombo, period, unit );
                    
                    value = format.formatValue( aggregatedValue );
                }                 
                
                if ( value != null )
                {
                    map.put( dataElement.getId() + SEPARATOR + categoryOptionCombo.getId(), value );
                }
            }
        }
        
        return map;
    }

    /**
     * Generates a map with aggregated indicator values mapped to an Indicator identifier.
     * 
     * @param dataSet the DataSet.
     * @param unit the OrganisationUnit.
     * @param period the Period.
     * @param format the I18nFormat.
     * @return a map.
     */
    private Map<Integer, String> getAggregatedIndicatorValueMap( DataSet dataSet, OrganisationUnit unit, Period period, I18nFormat format )
    {
        String aggregationStrategy = (String) systemSettingManager.getSystemSetting( KEY_AGGREGATION_STRATEGY, DEFAULT_AGGREGATION_STRATEGY );
        
        Map<Integer, String> map = new TreeMap<Integer, String>();
        
        for ( Indicator indicator : dataSet.getIndicators() )
        {
            Double aggregatedValue = aggregationStrategy.equals( AGGREGATION_STRATEGY_REAL_TIME ) ? 
                aggregationService.getAggregatedIndicatorValue( indicator, period.getStartDate(), period.getEndDate(), unit ) :
                    aggregatedDataValueService.getAggregatedValue( indicator, period, unit );
            
            String value = format.formatValue( aggregatedValue );
            
            if ( value != null )
            {
                map.put( indicator.getId(), value );
            }
        }
        
        return map;
    }

    /**
     * Puts in aggregated datavalues in the custom dataentry form and returns
     * whole report text.
     * 
     * @param dataEntryForm the data entry form.
     * @param a map with aggregated data values mapped to data element operands.
     * @return data entry form HTML code populated with aggregated data in the
     *         input fields.
     */
    private String prepareReportContent( DataEntryForm dataEntryForm, Map<String, String> dataValues, Map<Integer, String> indicatorValues )
    {        
        StringBuffer buffer = new StringBuffer();

        Matcher inputMatcher = INPUT_PATTERN.matcher( dataEntryForm.getHtmlCode() );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields.
        // ---------------------------------------------------------------------
        
        while ( inputMatcher.find() )
        {       
            // -----------------------------------------------------------------
            // Get input HTML code
            // -----------------------------------------------------------------
            
            String inputHtml = inputMatcher.group( 1 );
            
            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( inputHtml );
            Matcher indicatorMatcher = INDICATOR_PATTERN.matcher( inputHtml );

            // -----------------------------------------------------------------
            // Find existing data or indicator value and replace input tag
            // -----------------------------------------------------------------               
             
            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                Integer dataElementId = Integer.parseInt( identifierMatcher.group( 1 ) );
                Integer optionComboId = Integer.parseInt( identifierMatcher.group( 2 ) ); 
                
                String dataValue = dataValues.get( dataElementId + SEPARATOR + optionComboId );               
                
                dataValue = dataValue != null ? dataValue : NULL_REPLACEMENT;
                
                inputMatcher.appendReplacement( buffer, dataValue );
            }
            else if ( indicatorMatcher.find() && indicatorMatcher.groupCount() > 0 )
            {
                Integer indicatorId = Integer.parseInt( indicatorMatcher.group( 1 ) );
                
                String indicatorValue = indicatorValues.get( indicatorId );
                
                indicatorValue = indicatorValue != null ? indicatorValue : NULL_REPLACEMENT;
                
                inputMatcher.appendReplacement( buffer, indicatorValue );
            }
        }

        inputMatcher.appendTail( buffer );
        
        return buffer.toString();
    }
}
