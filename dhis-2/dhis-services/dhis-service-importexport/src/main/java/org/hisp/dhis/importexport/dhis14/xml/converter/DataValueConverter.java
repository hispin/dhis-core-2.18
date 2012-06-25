package org.hisp.dhis.importexport.dhis14.xml.converter;

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

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.StatementManager;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.importexport.*;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.importer.DataValueImporter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.MimicingHashMap;
import org.hisp.dhis.system.util.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.hisp.dhis.system.util.CsvUtils.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataValueConverter
    extends DataValueImporter implements CSVConverter 
{
    private static final String SEPARATOR = ",";
    private static final String FILENAME = "RoutineData.txt";

    private DataElementCategoryService categoryService;
    private PeriodService periodService;
    private StatementManager statementManager;
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> dataElementMapping;    
    private Map<Object, Integer> periodMapping;    
    private Map<Object, Integer> sourceMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DataValueConverter( PeriodService periodService, AggregatedDataValueService aggregatedDataValueService,
        StatementManager statementManager )
    {
        this.periodService = periodService;
        this.aggregatedDataValueService = aggregatedDataValueService;
        this.statementManager = statementManager;
    }
    
    /**
     * Constructor for read operations.
     */
    public DataValueConverter( BatchHandler<ImportDataValue> importDataValueBatchHandler,
        DataElementCategoryService categoryService,
        ImportObjectService importObjectService,
        ImportAnalyser importAnalyser,
        ImportParams params )
    {
        this.importDataValueBatchHandler = importDataValueBatchHandler;
        this.categoryService = categoryService;
        this.importObjectService = importObjectService;
        this.importAnalyser = importAnalyser;
        this.params = params;
        this.dataElementMapping = new MimicingHashMap<Object, Integer>();
        this.periodMapping = new MimicingHashMap<Object, Integer>();
        this.sourceMapping = new MimicingHashMap<Object, Integer>();
    }

    // -------------------------------------------------------------------------
    // CSVConverter implementation
    // -------------------------------------------------------------------------
    
    public void write( ZipOutputStream out, ExportParams params )
    {
        try
        {
            out.putNextEntry( new ZipEntry( FILENAME ) );
            
            out.write( getCsvValue( csvEncode( "RoutineDataID" ) ) );
            out.write( getCsvValue( csvEncode( "OrgUnitID" ) ) );
            out.write( getCsvValue( csvEncode( "DataElementID" ) ) );
            out.write( getCsvValue( csvEncode( "DataPeriodID" ) ) );
            out.write( getCsvValue( csvEncode( "EntryText" ) ) );
            out.write( getCsvValue( csvEncode( "EntryYesNo" ) ) );
            out.write( getCsvValue( csvEncode( "EntryNumber" ) ) );
            out.write( getCsvValue( csvEncode( "EntryDate" ) ) );
            out.write( getCsvValue( csvEncode( "EntryMemo" ) ) );
            out.write( getCsvValue( csvEncode( "EntryObject" ) ) );
            out.write( getCsvValue( csvEncode( "Check" ) ) );
            out.write( getCsvValue( csvEncode( "Verified" ) ) );
            out.write( getCsvValue( csvEncode( "Deleted" ) ) );
            out.write( getCsvValue( csvEncode( "Comment" ) ) );
            out.write( getCsvValue( csvEncode( "LastUserID" ) ) );
            out.write( getCsvEndValue( csvEncode( "LastUpdated" ) ) );
            
            out.write( NEWLINE );
            
            if ( params.isIncludeDataValues() )
            {
                if ( params.getStartDate() != null && params.getEndDate() != null )
                {
                    Collection<DeflatedDataValue> values = null;
                
                    Collection<Period> periods = periodService.getIntersectingPeriods( params.getStartDate(), params.getEndDate() );
                    
                    statementManager.initialise();
                    
                    for ( final Integer element : params.getDataElements() )
                    {
                        for ( final Period period : periods )
                        {
                            values = aggregatedDataValueService.getDeflatedDataValues( element, period.getId(), params.getOrganisationUnits() );
                            
                            for ( final DeflatedDataValue value : values )
                            {
                                out.write( getCsvValue( 0 ) );
                                out.write( getCsvValue( value.getSourceId() ) );
                                out.write( getCsvValue( value.getDataElementId() ) );
                                out.write( getCsvValue( value.getPeriodId() ) );
                                out.write( SEPARATOR_B );
                                out.write( SEPARATOR_B );
                                out.write( getCsvValue( csvEncode( value.getValue() ) ) );
                                out.write( SEPARATOR_B );
                                out.write( SEPARATOR_B );
                                out.write( SEPARATOR_B );
                                out.write( getCsvValue( 0 ) );
                                out.write( getCsvValue( 0 ) );
                                out.write( getCsvValue( 0 ) );
                                out.write( getCsvValue( csvEncode( value.getComment() ) ) );
                                out.write( getCsvValue( 1 ) );
                                out.write( getCsvEndValue( DateUtils.getAccessDateString( value.getTimestamp() ) ) );
                                
                                out.write( NEWLINE );
                            }
                        }
                    }
                    
                    statementManager.destroy();
                }
            }           

            StreamUtils.closeZipEntry( out );
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to write data", ex );
        }
    }

    public void read( BufferedReader reader, ImportParams params )
    {
        String line = "";
        
        DataValue value = new DataValue();
        DataElement dataElement = new DataElement();
        Period period = new Period();
        OrganisationUnit organisationUnit = new OrganisationUnit();
        DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        DataElementCategoryOptionCombo proxyCategoryOptionCombo = new DataElementCategoryOptionCombo();
        proxyCategoryOptionCombo.setId( categoryOptionCombo.getId() );
        
        try
        {
            reader.readLine(); // Skip CSV header
            
            while( ( line = reader.readLine() ) != null )
            {
                String[] values = line.split( SEPARATOR );
                
                dataElement.setId( dataElementMapping.get( Integer.parseInt( values[2] ) ) );
                period.setId( periodMapping.get( Integer.parseInt( values[3] ) ) );
                organisationUnit.setId( sourceMapping.get( Integer.parseInt( values[1] ) ) );
                
                value.setDataElement( dataElement );
                value.setPeriod( period );
                value.setSource( organisationUnit );
                //Text
                if ( values[4] != null || !values[4].isEmpty() )
                {
                    value.setValue( values[4].trim() );
                }
                //Yes=1,No=0
                if ( values[5] != null || !values[5].isEmpty() )
                {
                    value.setValue("false");

                    if  ( values[5].trim() == "1" )
                    {
                        value.setValue("true");
                    }

                }
                //Numbers
                if ( values[6] != null  || !values[6].isEmpty() )
                {
                value.setValue( handleNumericValue( values[6] ) );

                }

                value.setComment( values[13] );
                value.setOptionCombo( proxyCategoryOptionCombo );
                
                importObject( value, params );
            }
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Failed to read data", ex );
        }        
    }

    // -------------------------------------------------------------------------
    // CSVConverter implementation
    // -------------------------------------------------------------------------
    
    private String handleNumericValue( String value )
    {
        if ( value != null  )
        {
            //Remove all spaces
            value = value.replaceAll(" ", "");
            //Remove all quotes
            value = value.replaceAll( "\"", "" );
            //Strip trailing zeros
            value = value.replaceAll( "\\.0+$", "" );
        }

        
        return value;
    }

    private static boolean isValidNumeric (String value)
    {
        if ( value == null )
        {
            return false;
        }
        else
        {
            return value.matches("-?\\d+(\\.\\d+)?");
        }
    }

    private static Integer exportCSVField (DataElement dataElement)
    {
        String dataElementType = dataElement.getType();
        Integer csvField = null;

        if ( dataElementType == DataElement.VALUE_TYPE_STRING)
        {
            csvField =   4;
        }

        if ( dataElementType == DataElement.VALUE_TYPE_BOOL )
        {
            csvField = 5;
        }

        if ( dataElementType == DataElement.VALUE_TYPE_NUMBER )
        {
            csvField =  6;
        }

        if ( dataElementType == DataElement.VALUE_TYPE_DATE )
        {
            csvField =  7;
        }

        return csvField;
    }
}
