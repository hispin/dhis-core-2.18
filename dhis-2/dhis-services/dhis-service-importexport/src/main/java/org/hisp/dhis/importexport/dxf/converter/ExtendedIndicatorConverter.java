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

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.datadictionary.ExtendedDataElement;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.converter.AbstractExtendedIndicatorConverter;
import org.hisp.dhis.importexport.mapping.NameMappingUtil;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: ExtendedIndicatorConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class ExtendedIndicatorConverter
    extends AbstractExtendedIndicatorConverter implements XMLConverter
{
    public static final String COLLECTION_NAME = "extendedIndicators";
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

    private static final String FIELD_MNEMONIC = "mnemonic";
    private static final String FIELD_VERSION = "version";
    private static final String FIELD_CONTEXT = "context";
    private static final String FIELD_SYNONYMS = "synonyms";
    private static final String FIELD_HONONYMS = "hononyms";
    private static final String FIELD_KEYWORDS = "keywords";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_STATUS_DATE = "statusDate";
    private static final String FIELD_DATAELEMENT_TYPE = "dataElementType";
    
    private static final String FIELD_DATA_TYPE = "dataType";
    private static final String FIELD_REPRESENTATIONAL_FORM = "representationalForm";
    private static final String FIELD_REPRESENTATIONAL_LAYOUT = "representationalLayout";
    private static final String FIELD_MINIMUM_SIZE = "minimumSize";
    private static final String FIELD_MAXIMUM_SIZE = "maximumSize";
    private static final String FIELD_DATA_DOMAIN = "dataDomain";
    private static final String FIELD_VALIDATION_RULES = "validationRules";
    private static final String FIELD_RELATED_DATA_REFERENCES = "relatedDataReferences";
    private static final String FIELD_GUIDE_FOR_USE = "guideForUse";
    private static final String FIELD_COLLECTION_METHODS = "collectionMethods";
    
    private static final String FIELD_RESPONSIBLE_AUTHORITY = "responsibleAuthority";
    private static final String FIELD_UPDATE_RULES = "updateRules";
    private static final String FIELD_ACCESS_AUTHORITY = "accessAuthority";
    private static final String FIELD_UPDATE_FREQUENCY = "updateFrequency";
    private static final String FIELD_LOCATION = "location";
    private static final String FIELD_REPORTING_METHODS = "reportingMethods";
    private static final String FIELD_VERSION_STATUS = "versionStatus";
    private static final String FIELD_PREVIOUS_VERSION_REFERENCES = "previousVersionReferences";
    private static final String FIELD_SOURCE_DOCUMENT = "sourceDocument";
    private static final String FIELD_SOURCE_ORGANISATION = "sourceOrganisation";
    private static final String FIELD_COMMENT = "comment";
    private static final String FIELD_SAVED = "saved";
    private static final String FIELD_LAST_UPDATED = "lastUpdated";
    
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
    public ExtendedIndicatorConverter( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
    
    /**
     * Constructor for read operations.
     * 
     * @param batchHandler the batchHandler to use.
     * @param extendedbatchHandler the extendedBatchHandler to use.
     * @param importObjectService the importObjectService to use.
     * @param indicatorService the indicatorService to use.
     * @param expressionService the expressionService to use.
     * @param indicatorTypeMapping the indicatorTypeMapping to use.
     * @param dataElementMapping the dataElementMapping to use.
     * @param categoryOptionComboMapping the categoryOptionComboMapping to use.
     */
    public ExtendedIndicatorConverter( BatchHandler batchHandler,
        BatchHandler extendedDataElementBatchHandler,
        ImportObjectService importObjectService, 
        IndicatorService indicatorService,
        ExpressionService expressionService,
        Map<Object, Integer> indicatorTypeMapping, 
        Map<Object, Integer> dataElementMapping,
        Map<Object, Integer> categoryOptionComboMapping )
    {
        this.batchHandler = batchHandler;
        this.extendedDataElementBatchHandler = extendedDataElementBatchHandler;
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
                if ( indicator.getExtended() == null )
                {
                    indicator.setExtended( new ExtendedDataElement() );
                }
                
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

                writer.writeElement( FIELD_MNEMONIC, indicator.getExtended().getMnemonic() );
                writer.writeElement( FIELD_VERSION, indicator.getExtended().getVersion() );
                writer.writeElement( FIELD_CONTEXT, indicator.getExtended().getContext() );
                writer.writeElement( FIELD_SYNONYMS, indicator.getExtended().getSynonyms() );
                writer.writeElement( FIELD_HONONYMS, indicator.getExtended().getHononyms() );
                writer.writeElement( FIELD_KEYWORDS, indicator.getExtended().getKeywords() );
                writer.writeElement( FIELD_STATUS, indicator.getExtended().getStatus() );
                writer.writeElement( FIELD_STATUS_DATE, DateUtils.getMediumDateString( indicator.getExtended().getStatusDate() ) );
                writer.writeElement( FIELD_DATAELEMENT_TYPE, indicator.getExtended().getDataElementType() );
                
                writer.writeElement( FIELD_DATA_TYPE, indicator.getExtended().getDataType() );
                writer.writeElement( FIELD_REPRESENTATIONAL_FORM, indicator.getExtended().getRepresentationalForm() );
                writer.writeElement( FIELD_REPRESENTATIONAL_LAYOUT, indicator.getExtended().getRepresentationalLayout() );
                writer.writeElement( FIELD_MINIMUM_SIZE, valueOf( indicator.getExtended().getMinimumSize() ) );
                writer.writeElement( FIELD_MAXIMUM_SIZE, valueOf( indicator.getExtended().getMaximumSize() ) );
                writer.writeElement( FIELD_DATA_DOMAIN, indicator.getExtended().getDataDomain() );
                writer.writeElement( FIELD_VALIDATION_RULES, indicator.getExtended().getValidationRules() );
                writer.writeElement( FIELD_RELATED_DATA_REFERENCES, indicator.getExtended().getRelatedDataReferences() );
                writer.writeElement( FIELD_GUIDE_FOR_USE, indicator.getExtended().getGuideForUse() );
                writer.writeElement( FIELD_COLLECTION_METHODS, indicator.getExtended().getCollectionMethods() );
                
                writer.writeElement( FIELD_RESPONSIBLE_AUTHORITY, indicator.getExtended().getResponsibleAuthority() );
                writer.writeElement( FIELD_UPDATE_RULES, indicator.getExtended().getUpdateRules() );
                writer.writeElement( FIELD_ACCESS_AUTHORITY, indicator.getExtended().getAccessAuthority() );
                writer.writeElement( FIELD_UPDATE_FREQUENCY, indicator.getExtended().getUpdateFrequency() );
                writer.writeElement( FIELD_LOCATION, indicator.getExtended().getLocation() );
                writer.writeElement( FIELD_REPORTING_METHODS, indicator.getExtended().getReportingMethods() );
                writer.writeElement( FIELD_VERSION_STATUS, indicator.getExtended().getVersionStatus() );
                writer.writeElement( FIELD_PREVIOUS_VERSION_REFERENCES, indicator.getExtended().getPreviousVersionReferences() );
                writer.writeElement( FIELD_SOURCE_DOCUMENT, indicator.getExtended().getSourceDocument() );
                writer.writeElement( FIELD_SOURCE_ORGANISATION, indicator.getExtended().getSourceOrganisation() );
                writer.writeElement( FIELD_COMMENT, indicator.getExtended().getComment() );
                writer.writeElement( FIELD_SAVED, DateUtils.getMediumDateString( indicator.getExtended().getSaved() ) );
                writer.writeElement( FIELD_LAST_UPDATED, DateUtils.getMediumDateString( indicator.getExtended().getLastUpdated() ) );
                
                writer.closeElement();
            }
            
            writer.closeElement();
        }
    }
    
    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final Map<String, String> values = reader.readElements( ELEMENT_NAME );
            
            final Indicator indicator = new Indicator();

            // -----------------------------------------------------------------
            // Regular attributes
            // -----------------------------------------------------------------

            final IndicatorType type = new IndicatorType();
            indicator.setIndicatorType( type );
            
            indicator.setId( Integer.parseInt( values.get( FIELD_ID ) ) );
            indicator.setUuid( values.get( FIELD_UUID ) );
            indicator.setName( values.get( FIELD_NAME ) );
            indicator.setAlternativeName( values.get( FIELD_ALTERNATIVE_NAME ) );
            indicator.setShortName( values.get( FIELD_SHORT_NAME ) );
            indicator.setCode( values.get( FIELD_CODE ) );
            indicator.setDescription( values.get( FIELD_DESCRIPTION ) );
            indicator.setAnnualized( Boolean.parseBoolean( values.get( FIELD_ANNUALIZED ) ) );
            indicator.getIndicatorType().setId( indicatorTypeMapping.get( Integer.parseInt( values.get( FIELD_INDICATOR_TYPE ) ) ) );
            indicator.setNumerator( expressionService.convertExpression( values.get( FIELD_NUMERATOR ), dataElementMapping, categoryOptionComboMapping ) );
            indicator.setNumeratorDescription( values.get( FIELD_NUMERATOR_DESCRIPTION ) );
            indicator.setNumeratorAggregationOperator( values.get( FIELD_NUMERATOR_AGGREGATION_OPERATOR ) );
            indicator.setDenominator( expressionService.convertExpression( values.get( FIELD_DENOMINATOR ), dataElementMapping, categoryOptionComboMapping ) );
            indicator.setDenominatorDescription( values.get( FIELD_DENOMINATOR_DESCRIPTION ) );
            indicator.setDenominatorAggregationOperator( values.get( FIELD_DENOMINATOR_AGGREGATION_OPERATOR ) );

            // -----------------------------------------------------------------
            // Identifying and Definitional attributes 
            // -----------------------------------------------------------------

            ExtendedDataElement extended = new ExtendedDataElement();            
                 
            extended.setMnemonic( values.get( FIELD_MNEMONIC ) ); 
            extended.setVersion( values.get( FIELD_VERSION ) ); 
            extended.setContext( values.get( FIELD_CONTEXT ) );  
            extended.setSynonyms( values.get( FIELD_SYNONYMS ) );  
            extended.setHononyms( values.get( FIELD_HONONYMS ) );  
            extended.setKeywords( values.get( FIELD_KEYWORDS ) );
            extended.setStatus( values.get( FIELD_STATUS ) );    
            extended.setStatusDate( DateUtils.getMediumDate( values.get( FIELD_STATUS_DATE ) ) );
            extended.setDataElementType( values.get( FIELD_DATAELEMENT_TYPE) );

            // -----------------------------------------------------------------
            // Relational and Representational attributes
            // -----------------------------------------------------------------
      
            extended.setDataType( values.get( FIELD_DATA_TYPE ) );
            extended.setRepresentationalForm( values.get( FIELD_REPRESENTATIONAL_FORM ) );
            extended.setRepresentationalLayout( values.get( FIELD_REPRESENTATIONAL_LAYOUT ) );
            extended.setMinimumSize( parseInteger( values.get( FIELD_MINIMUM_SIZE ) ) );
            extended.setMaximumSize( parseInteger( values.get( FIELD_MAXIMUM_SIZE ) ) );
            extended.setDataDomain( values.get( FIELD_DATA_DOMAIN ) );
            extended.setValidationRules( values.get( FIELD_VALIDATION_RULES ) );
            extended.setRelatedDataReferences( values.get( FIELD_RELATED_DATA_REFERENCES ) );
            extended.setGuideForUse( values.get( FIELD_GUIDE_FOR_USE ) );
            extended.setCollectionMethods( values.get( FIELD_COLLECTION_METHODS ) );

            // -----------------------------------------------------------------
            // Administrative attributes 
            // -----------------------------------------------------------------
         
            extended.setResponsibleAuthority( values.get( FIELD_RESPONSIBLE_AUTHORITY ) );
            extended.setUpdateRules( values.get( FIELD_UPDATE_RULES ) ); 
            extended.setAccessAuthority( values.get( FIELD_ACCESS_AUTHORITY ) );
            extended.setUpdateFrequency( values.get( FIELD_UPDATE_FREQUENCY ) );
            extended.setLocation( values.get( FIELD_LOCATION ) ); 
            extended.setReportingMethods( values.get( FIELD_REPORTING_METHODS ) );
            extended.setVersionStatus( values.get( FIELD_VERSION_STATUS ) );       
            extended.setPreviousVersionReferences( values.get( FIELD_PREVIOUS_VERSION_REFERENCES ) );
            extended.setSourceDocument( values.get( FIELD_SOURCE_DOCUMENT ) );  
            extended.setSourceOrganisation( values.get( FIELD_SOURCE_ORGANISATION ) );
            extended.setComment( values.get( FIELD_COMMENT ) );
            extended.setSaved( DateUtils.getMediumDate( values.get( FIELD_SAVED ) ) );
            extended.setLastUpdated( DateUtils.getMediumDate( values.get( FIELD_LAST_UPDATED ) ) );

            // -----------------------------------------------------------------
            // Only set ExtendedDataElement if it contains values
            // -----------------------------------------------------------------

            indicator.setExtended( extended.isNull() ? null : extended );
            
            NameMappingUtil.addIndicatorMapping( indicator.getId(), indicator.getName() );
            
            read( indicator, GroupMemberType.NONE, params );
        }
    }
}

