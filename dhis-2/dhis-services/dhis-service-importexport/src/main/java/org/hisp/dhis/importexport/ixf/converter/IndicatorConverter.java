package org.hisp.dhis.importexport.ixf.converter;

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

import static org.hisp.dhis.system.util.TextUtils.subString;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.amplecode.quick.BatchHandler;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.converter.AbstractDataElementConverter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

/**
 * @author Lars Helge Overland
 * @version $Id: IndicatorConverter.java 6455 2008-11-24 08:59:37Z larshelg $
 */
public class IndicatorConverter
    extends AbstractDataElementConverter implements XMLConverter
{
    public static final String COLLECTION_NAME = "indicators";
    public static final String ELEMENT_NAME = "indicator";
    
    private static final String FIELD_DEFINITION = "definition";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_TYPE = "type";

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private DataValueService dataValueService;
    
    private PeriodService periodService;
    
    private OrganisationUnitService organisationUnitService;

    private DataMartStore dataMartStore;
    
    private DataElementCategoryCombo categoryCombo;
    
    private BatchHandler<DataValue> dataValueBatchHandler;
    
    private DataElementCategoryOptionCombo categoryOptionCombo;
    
    private XMLConverter dataConverter;
    
    private Map<Object, Integer> periodMapping;
    
    private Map<Object, Integer> sourceMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public IndicatorConverter( DataValueService dataValueService,
        DataElementService dataElementService,
        PeriodService periodService,
        OrganisationUnitService organisationUnitService )
    {
        this.dataValueService = dataValueService;
        this.dataElementService = dataElementService;
        this.periodService = periodService;
        this.organisationUnitService = organisationUnitService;
    }

    /**
     * Constructor for read operations.
     */
    public IndicatorConverter( BatchHandler<DataElement> batchHandler,
        DataElementService dataElementService, 
        ImportObjectService importObjectService,
        DataMartStore dataMartStore,
        DataElementCategoryCombo categoryCombo,
        BatchHandler<DataValue> dataValueBatchHandler,
        DataElementCategoryOptionCombo categoryOptionCombo,
        Map<Object, Integer> periodMapping,
        Map<Object, Integer> sourceMapping )
    {
        this.batchHandler = batchHandler;
        this.dataElementService = dataElementService;
        this.importObjectService = importObjectService;
        this.dataMartStore = dataMartStore;
        this.categoryCombo = categoryCombo;
        this.dataValueBatchHandler = dataValueBatchHandler;
        this.categoryOptionCombo = categoryOptionCombo;
        this.periodMapping = periodMapping;
        this.sourceMapping = sourceMapping;
    }
    
    // -------------------------------------------------------------------------
    // IXFConverter implementation
    // -------------------------------------------------------------------------
    
    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<DataElement> dataElements = dataElementService.getDataElements( params.getDataElements() );
        Collection<Period> periods = periodService.getPeriods( params.getPeriods() );
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnits( params.getOrganisationUnits() );
        
        if ( dataElements != null && dataElements.size() > 0 )
        {
            writer.openElement( COLLECTION_NAME );

            for ( DataElement element : dataElements )
            {
                writer.openElement( ELEMENT_NAME );
    
                writer.openElement( FIELD_DEFINITION, "key", element.getUuid(), 
                    "keyNameSpace", "http://www.unaids.org/ns/cris-ext" );                
    
                writer.writeElement( FIELD_NAME, element.getName(), "lang", "en" );
    
                if ( element.getDescription() != null )
                {
                    writer.writeElement( FIELD_DESCRIPTION, element.getDescription(), "lang", "en" );
                }
    
                writer.writeElement( FIELD_TYPE, "count" );
                
                writer.closeElement();
    
                if ( params.isIncludeDataValues() )
                {
                    // -------------------------------------------------------------
                    // Data values are embedded in the Indicator collection
                    // -------------------------------------------------------------
                    
                    Collection<DataValue> values = dataValueService.getDataValues( element, periods, units );
                    
                    dataConverter = new DataConverter( values );
                    
                    dataConverter.write( writer, params );
                }
                
                writer.closeElement();
            }

            writer.closeElement();
        }
    }

    public void read( XMLReader reader, ImportParams params )
    {
        while ( reader.moveToStartElement( ELEMENT_NAME, COLLECTION_NAME ) )
        {
            final int random = new Random().nextInt( 100000 );
            
            final DataElement element = new DataElement();
            
            reader.moveToStartElement( FIELD_DEFINITION );
            
            element.setUuid( reader.getAttributeValue( "key" ) );
            
            reader.moveToStartElement( FIELD_NAME );            
            element.setName( reader.getElementValue() );                      
            element.setShortName( subString( element.getName(), 0, 17 ) + random );
            
            reader.moveToStartElement( FIELD_DESCRIPTION );            
            element.setDescription( reader.getElementValue() );
            
            element.setActive( true );            
            element.setType( DataElement.VALUE_TYPE_INT );            
            element.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_SUM );
            
            element.setCategoryCombo( categoryCombo );

            read( element, GroupMemberType.NONE, params );

            // -----------------------------------------------------------------
            // Data values are embedded in the Indicator collection
            // -----------------------------------------------------------------
            
            dataConverter = new DataConverter( dataValueBatchHandler, 
                importObjectService,
                dataMartStore,
                params,
                element,
                categoryOptionCombo,
                periodMapping,
                sourceMapping );
            
            dataConverter.read( reader, params );
        }
    }

    // -------------------------------------------------------------------------
    // Overridden methods
    // -------------------------------------------------------------------------
    
    @Override
    protected void importUnique( DataElement object )
    {
        dataElementService.addDataElement( object );
    }    
}
