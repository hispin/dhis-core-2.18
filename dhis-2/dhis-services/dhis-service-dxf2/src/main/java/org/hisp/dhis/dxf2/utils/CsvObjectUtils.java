package org.hisp.dhis.dxf2.utils;

/*
 * Copyright (c) 2004-2014, University of Oslo
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
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.csvreader.CsvReader;

import static org.hisp.dhis.system.util.DateUtils.getMediumDate;

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
        
        MetaData metaData = new MetaData();
        
        if ( DataElement.class.equals( clazz ) )
        {
            metaData.setDataElements( dataElementsFromCsv( reader, input ) );
        }
        else if ( DataElementCategoryOption.class.equals( clazz ) )
        {
            metaData.setCategoryOptions( categoryOptionsFromCsv( reader, input ) );
        }
        else if ( CategoryOptionGroup.class.equals( clazz ) )
        {
            metaData.setCategoryOptionGroups( categoryOptionGroupsFromCsv( reader, input ) );
        }
        else if ( OrganisationUnit.class.equals( clazz ) )
        {
            metaData.setOrganisationUnits( organisationUnitsFromCsv( reader, input ) );
        }
        
        return metaData;
    }
    
    private static List<DataElementCategoryOption> categoryOptionsFromCsv( CsvReader reader, InputStream input )
        throws IOException
    {
        List<DataElementCategoryOption> list = new ArrayList<DataElementCategoryOption>();
        
        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                DataElementCategoryOption object = new DataElementCategoryOption();
                setIdentifiableObject( object, values );
                list.add( object );
            }
        }
        
        return list;
    }    

    private static List<CategoryOptionGroup> categoryOptionGroupsFromCsv( CsvReader reader, InputStream input )
        throws IOException
    {
        List<CategoryOptionGroup> list = new ArrayList<CategoryOptionGroup>();
        
        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                CategoryOptionGroup object = new CategoryOptionGroup();
                setIdentifiableObject( object, values );
                list.add( object );
            }
        }
        
        return list;
    }    
    
    private static List<DataElement> dataElementsFromCsv( CsvReader reader, InputStream input )
        throws IOException
    {
        List<DataElement> list = new ArrayList<DataElement>();
        
        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                DataElement object = new DataElement();
                setIdentifiableObject( object, values );
                object.setShortName( getSafe( values, 3, object.getName(), 50 ) );
                object.setDescription( getSafe( values, 4, null, null ) );
                object.setFormName( getSafe( values, 5, null, 230 ) );
                object.setActive( true );
                object.setDomainType( getSafe( values, 6, DataElement.DOMAIN_TYPE_AGGREGATE, 16 ) );
                object.setType( getSafe( values, 7, DataElement.VALUE_TYPE_INT, 16 ) );
                object.setNumberType( getSafe( values, 8, DataElement.VALUE_TYPE_NUMBER, 16 ) );
                object.setTextType( getSafe( values, 9, null, 16 ) );
                object.setAggregationOperator( getSafe( values, 10, DataElement.AGGREGATION_OPERATOR_SUM, 16 ) );
                object.setUrl( getSafe( values, 11, null, 255 ) );
                object.setZeroIsSignificant( Boolean.valueOf( getSafe( values, 12, "false", null ) ) );
                
                list.add( object );
            }
        }
        
        return list;
    }
    
    public static List<OrganisationUnit> organisationUnitsFromCsv( CsvReader reader, InputStream input )
        throws IOException
    {
        List<OrganisationUnit> list = new ArrayList<OrganisationUnit>();
        
        while ( reader.readRecord() )
        {
            String[] values = reader.getValues();

            if ( values != null && values.length > 0 )
            {
                OrganisationUnit object = new OrganisationUnit();
                setIdentifiableObject( object, values );
                object.setShortName( getSafe( values, 3, object.getName(), 50 ) );
                object.setDescription( getSafe( values, 4, null, null ) );
                object.setUuid( getSafe( values, 5, null, 36 ) );
                object.setOpeningDate( getMediumDate( getSafe( values, 6, "1970-01-01", null ) ) );
                object.setClosedDate( getMediumDate( getSafe( values, 7, "1970-01-01", null ) ) );
                object.setActive( true );
                object.setComment( getSafe( values, 8, null, null ) );
                object.setFeatureType( getSafe( values, 9, null, 50 ) );
                object.setCoordinates( getSafe( values, 10, null, null ) );
                object.setUrl( getSafe( values, 11, null, 255 ) );
                object.setContactPerson( getSafe( values, 12, null, 255 ) );
                object.setAddress( getSafe( values, 13, null, 255 ) );
                object.setEmail( getSafe( values, 14, null, 150 ) );
                object.setPhoneNumber( getSafe( values, 15, null, 150 ) );

                list.add( object );
            }
        }
        
        return list;
    }

    private static void setIdentifiableObject( BaseIdentifiableObject object, String[] values )
    {
        object.setName( getSafe( values, 0, null, 230 ) );
        object.setUid( getSafe( values, 1, CodeGenerator.generateCode(), 11 ) );
        object.setCode( getSafe( values, 2, null, 50 ) );
    }
    
    /**
     * Returns a string from the given array avoiding exceptions.
     * 
     * @param values the string array.
     * @param index the array index of the string to get.
     * @param defaultValue the default value in case index is out of bounds.
     * @param max the max number of characters to return for the string.
     */
    private static String getSafe( String[] values, int index, String defaultValue, Integer max )
    {
        String string = null;
        
        if ( values == null || index < 0 || index >= values.length )
        {
            string = defaultValue;
        }
        else
        {        
            string = values[index];
        }
        
        if ( string != null )
        {
            return max != null ? StringUtils.substring( string, 0, max ) : string;
        }
        
        return null;
    }
}
