package org.hisp.dhis.datasetreport.impl;

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
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datasetreport.DataSetReportService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.filter.AggregatableDataElementFilter;
import org.hisp.dhis.system.util.FilterUtils;

/**
 * @author Abyot Asalefew
 * @author Lars Helge Overland
 */
public class DefaultDataSetReportService
    implements DataSetReportService
{
    private static final Pattern DATAENTRYFORM_PATTERN = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
    private static final Pattern OPERAND_PATTERN = Pattern.compile( "value\\[(.*)\\].value:value\\[(.*)\\].value" );
    private static final String NULL_REPLACEMENT = "";
    private static final String SEPARATOR = ":";

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
    
    public Map<String, String> getAggregatedValueMap( DataSet dataSet, OrganisationUnit unit, Period period, boolean selectedUnitOnly, I18nFormat format )
    {
        String aggregationStrategy = (String) systemSettingManager.getSystemSetting( KEY_AGGREGATION_STRATEGY, DEFAULT_AGGREGATION_STRATEGY );
        
        Collection<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements());
        
        FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );
        
        Map<String, String> map = new TreeMap<String,String>();            
        
        for ( DataElement dataElement : dataElements )
        {
            DataElementCategoryCombo categoryCombo = dataElement.getCategoryCombo();                                        
            
            for ( DataElementCategoryOptionCombo categoryOptionCombo : categoryCombo.getOptionCombos() )
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
    
    public String prepareReportContent( String dataEntryFormCode, Map<String, String> dataValues )
    {        
        StringBuffer buffer = new StringBuffer();

        Matcher matDataElement = DATAENTRYFORM_PATTERN.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields.
        // ---------------------------------------------------------------------
        
        while ( matDataElement.find() )
        {       
            // -----------------------------------------------------------------
            // Get input HTML code
            // -----------------------------------------------------------------
            
            String dataElementCode = matDataElement.group( 1 );
            
            // -----------------------------------------------------------------
            // Pattern to extract data element ID from data element field
            // -----------------------------------------------------------------

            Matcher matDataElementId = OPERAND_PATTERN.matcher( dataElementCode );
            
            if ( matDataElementId.find() && matDataElementId.groupCount() > 0 )
            {   
                // -------------------------------------------------------------
                // Get data element ID of data element.
                // -------------------------------------------------------------
                
                int dataElementId = Integer.parseInt( matDataElementId.group( 1 ) );
                int optionComboId = Integer.parseInt( matDataElementId.group( 2 ) ); 
                
               // --------------------------------------------------------------
               // Find existing value of data element in data set.
               // --------------------------------------------------------------               
                
                String dataElementValue = dataValues.get( dataElementId + SEPARATOR + optionComboId );               
                
                if ( dataElementValue == null )
                {
                    dataElementValue = NULL_REPLACEMENT;
                }
                        
                dataElementCode = dataElementValue;
                
                matDataElement.appendReplacement( buffer, dataElementCode );
            }
        }

        // ---------------------------------------------------------------------
        // Add remaining text 
        // ---------------------------------------------------------------------          
        
        matDataElement.appendTail( buffer );
        
        return buffer.toString();
    }
}
