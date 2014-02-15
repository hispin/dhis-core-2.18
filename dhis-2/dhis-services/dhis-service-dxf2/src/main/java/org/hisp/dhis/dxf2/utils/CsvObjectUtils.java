package org.hisp.dhis.dxf2.utils;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dxf2.metadata.MetaData;

import com.csvreader.CsvReader;

/**
 * @author Lars Helge Overland
 */
public class CsvObjectUtils
{
    public static MetaData fromCsv( InputStream input, Class<?> clazz )
        throws IOException
    {
        CsvReader reader = new CsvReader( input, Charset.forName( "UTF-8" ) );
        
        reader.readRecord(); // Ignore first row
        
        List<DataElement> dataElements = new ArrayList<DataElement>();
        
        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();
            
            if ( values == null || values.length == 0 )
            {
                continue;
            }
            
            DataElement element = new DataElement();
            element.setName( getSafe( values, 0, null ) );
            element.setUid( getSafe( values, 1, CodeGenerator.generateCode() ) );
            element.setCode( getSafe( values, 2, null ) );
            element.setShortName( getSafe( values, 3, StringUtils.substring( element.getName(), 0, 50 ) ) );
            element.setDescription( getSafe( values, 4, null ) );
            element.setFormName( getSafe( values, 5, null ) );
            element.setActive( Boolean.valueOf( getSafe( values, 6, "false" ) ) );
            element.setDomainType( getSafe( values, 7, DataElement.DOMAIN_TYPE_AGGREGATE ) );
            element.setType( getSafe( values, 8, DataElement.VALUE_TYPE_INT ) );
            element.setNumberType( getSafe( values, 9, DataElement.VALUE_TYPE_NUMBER ) );
            element.setTextType( getSafe( values, 10, null ) );
            element.setAggregationOperator( getSafe( values, 11, DataElement.AGGREGATION_OPERATOR_SUM ) );
            element.setUrl( getSafe( values, 12, null ) );
            element.setZeroIsSignificant( Boolean.valueOf( getSafe( values, 13, "false" ) ) );
            
            dataElements.add( element );
        }
        
        MetaData metaData = new MetaData();
        metaData.setDataElements( dataElements );
        return metaData;
    }
    
    private static final String getSafe( String[] values, int index, String defaultValue )
    {
        if ( values == null || index < 0 || index >= values.length )
        {
            return null;
        }
        
        String value = values[index];
        
        return value != null ? value : defaultValue;
    }
}
