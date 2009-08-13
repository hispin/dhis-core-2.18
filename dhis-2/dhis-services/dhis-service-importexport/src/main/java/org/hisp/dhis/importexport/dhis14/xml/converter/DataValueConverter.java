package org.hisp.dhis.importexport.dhis14.xml.converter;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.importexport.CSVConverter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.GroupMemberType;
import org.hisp.dhis.importexport.ImportDataValue;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.converter.AbstractDataValueConverter;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.MimicingHashMap;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataValueConverter
    extends AbstractDataValueConverter implements CSVConverter 
{
    private static final String SEPARATOR = ",";
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Map<Object, Integer> dataElementMapping;    
    private Map<Object, Integer> periodMapping;    
    private Map<Object, Integer> sourceMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for read operations.
     */
    public DataValueConverter( BatchHandler<ImportDataValue> importDataValueBatchHandler,
        DataValueService dataValueService,
        ImportObjectService importObjectService,
        ImportParams params )
    {
        this.importDataValueBatchHandler = importDataValueBatchHandler;
        this.dataValueService = dataValueService;
        this.importObjectService = importObjectService;
        this.params = params;
        this.dataElementMapping = new MimicingHashMap<Object, Integer>();
        this.periodMapping = new MimicingHashMap<Object, Integer>();
        this.sourceMapping = new MimicingHashMap<Object, Integer>();
    }

    // -------------------------------------------------------------------------
    // CSVConverter implementation
    // -------------------------------------------------------------------------
    
    public void write( BufferedWriter writer, ExportParams params )
    {
        // Not implemented
    }

    public void read( BufferedReader reader, ImportParams params )
    {
        String line = new String();
        
        final DataValue value = new DataValue();
        final DataElement dataElement = new DataElement();
        final Period period = new Period();
        final OrganisationUnit organisationUnit = new OrganisationUnit();
        final DataElementCategoryOptionCombo categoryOptionCombo = new DataElementCategoryOptionCombo();
        
        categoryOptionCombo.setId( 1 );
        
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
                value.setValue( handleValue( values[6] ) );
                value.setComment( values[13] );
                value.setOptionCombo( categoryOptionCombo );
                
                read( value, GroupMemberType.NONE, params );
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
    
    private String handleValue( String value )
    {
        if ( value != null )
        {
            value = value.replaceAll( "\"", "" );
            value = value.replace( ".", "" );
        }
        
        return value;
    }
}
