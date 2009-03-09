package org.hisp.dhis.importexport.dxf.converter;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;
import java.util.Map;

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.converter.AbstractIndicatorConverter;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.BatchHandler;

/**
 * @author Lars Helge Overland
 * @version $Id: IndicatorConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class IndicatorConverter
    extends AbstractIndicatorConverter implements XMLConverter
{
    public static final String COLLECTION_NAME = "indicators";
    public static final String ELEMENT_NAME = "indicator";
    
    private static final String FIELD_ID = "id";
    private static final String FIELD_UUID = "uuid";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_ALTERNATIVE_NAME = "alternativeName";
    private static final String FIELD_SHORT_NAME = "shortName";
    private static final String FIELD_CODE = "code";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_ANNUALIZED = "annualized";
    private static final String FIELD_INDICATOR_TYPE = "indicatorType";
    private static final String FIELD_NUMERATOR = "numerator";
    private static final String FIELD_NUMERATOR_DESCRIPTION = "numeratorDescription";
    private static final String FIELD_NUMERATOR_AGGREGATION_OPERATOR = "numeratorAggregationOperator";
    private static final String FIELD_DENOMINATOR = "denominator";
    private static final String FIELD_DENOMINATOR_DESCRIPTION = "denominatorDescription";
    private static final String FIELD_DENOMINATOR_AGGREGATION_OPERATOR = "denominatorAggregationOperator";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private ExpressionService expressionService;
    
    private Map<Object, Integer> indicatorTypeMapping;
    
    private Map<Object, Integer> dataElementMapping;
    
    private Map<Object, Integer> categoryOptionComboMapping;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public IndicatorConverter( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param indicatorService the indicatorService to use.
     * @param expressionService the expressionService to use.
     * @param indicatorTypeMapping the indicatorTypeMapping to use.
     * @param dataElementMapping the dataElementMapping to use.
     * @param categoryOptionComboMapping the categoryOptionComboMapping to use.
     */
    public IndicatorConverter( BatchHandler batchHandler, 
        ImportObjectService importObjectService, 
        IndicatorService indicatorService,
        ExpressionService expressionService,
        Map<Object, Integer> indicatorTypeMapping, 
        Map<Object, Integer> dataElementMapping,
        Map<Object, Integer> categoryOptionComboMapping )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.indicatorService = indicatorService;
        this.expressionService = expressionService;
        this.indicatorTypeMapping = indicatorTypeMapping;
        this.dataElementMapping = dataElementMapping;
        this.categoryOptionComboMapping = categoryOptionComboMapping;
    }    

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<Indicator> indicators = indicatorService.getIndicators( params.getIndicators() );
        
        if ( indicators != null && indicators.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );
            
            for ( Indicator indicator : indicators )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( indicator.getId() ) );
                writer.writeElement( FIELD_UUID, indicator.getUuid() );
                writer.writeElement( FIELD_NAME, indicator.getName() );
                writer.writeElement( FIELD_ALTERNATIVE_NAME, indicator.getAlternativeName() );
                writer.writeElement( FIELD_SHORT_NAME, indicator.getShortName() );
                writer.writeElement( FIELD_CODE, indicator.getCode() );
                writer.writeElement( FIELD_DESCRIPTION, indicator.getDescription() );
                writer.writeElement( FIELD_ANNUALIZED, String.valueOf( indicator.getAnnualized() ) );
                writer.writeElement( FIELD_INDICATOR_TYPE, String.valueOf( indicator.getIndicatorType().getId() ) );
                writer.writeElement( FIELD_NUMERATOR, indicator.getNumerator() );
                writer.writeElement( FIELD_NUMERATOR_DESCRIPTION, indicator.getNumeratorDescription() );
                writer.writeElement( FIELD_NUMERATOR_AGGREGATION_OPERATOR, indicator.getNumeratorAggregationOperator() );
                writer.writeElement( FIELD_DENOMINATOR, indicator.getDenominator() );
                writer.writeElement( FIELD_DENOMINATOR_DESCRIPTION, indicator.getDenominatorDescription() );
                writer.writeElement( FIELD_DENOMINATOR_AGGREGATION_OPERATOR, indicator.getDenominatorAggregationOperator() );
                            
                writer.closeElement();
            }
            
            writer.closeElement();
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Indicator indicator = new Indicator();
            
            final IndicatorType type = new IndicatorType();
            indicator.setIndicatorType( type );
            
            reader.moveToStartElement( FIELD_ID );
            indicator.setId( Integer.parseInt( reader.getElementValue() ) );
            
            reader.moveToStartElement( FIELD_UUID );            
            indicator.setUuid( reader.getElementValue() );
            
            reader.moveToStartElement( FIELD_NAME );
            indicator.setName( reader.getElementValue() );
            
            reader.moveToStartElement( FIELD_ALTERNATIVE_NAME );
            indicator.setAlternativeName( reader.getElementValue() );

            reader.moveToStartElement( FIELD_SHORT_NAME );
            indicator.setShortName( reader.getElementValue() );

            reader.moveToStartElement( FIELD_CODE );
            indicator.setCode( reader.getElementValue() );

            reader.moveToStartElement( FIELD_DESCRIPTION );
            indicator.setDescription( reader.getElementValue() );

            reader.moveToStartElement( FIELD_ANNUALIZED );
            indicator.setAnnualized( Boolean.parseBoolean( reader.getElementValue() ) );
            
            reader.moveToStartElement( FIELD_INDICATOR_TYPE );
            indicator.getIndicatorType().setId( indicatorTypeMapping.get( Integer.parseInt( reader.getElementValue() ) ) );
            
            reader.moveToStartElement( FIELD_NUMERATOR );
            indicator.setNumerator( expressionService.convertExpression( reader.getElementValue(), dataElementMapping, categoryOptionComboMapping ) );
            
            reader.moveToStartElement( FIELD_NUMERATOR_DESCRIPTION );
            indicator.setNumeratorDescription( reader.getElementValue() );

            reader.moveToStartElement( FIELD_NUMERATOR_AGGREGATION_OPERATOR );
            indicator.setNumeratorAggregationOperator( reader.getElementValue() );
            
            reader.moveToStartElement( FIELD_DENOMINATOR );
            indicator.setDenominator( expressionService.convertExpression( reader.getElementValue(), dataElementMapping, categoryOptionComboMapping ) );
            
            reader.moveToStartElement( FIELD_DENOMINATOR_DESCRIPTION );
            indicator.setDenominatorDescription( reader.getElementValue() );

            reader.moveToStartElement( FIELD_DENOMINATOR_AGGREGATION_OPERATOR );
            indicator.setDenominatorAggregationOperator( reader.getElementValue() );
            
            NameMappingUtil.addIndicatorMapping( indicator.getId(), indicator.getName() );
            
            read( indicator, GroupMemberType.NONE, params );
        }
    }
}