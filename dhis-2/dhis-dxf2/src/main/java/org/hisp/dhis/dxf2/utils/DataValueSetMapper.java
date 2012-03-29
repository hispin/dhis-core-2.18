package org.hisp.dhis.dxf2.utils;

/*
 * Copyright (c) 2008, the original author or authors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the AmpleCode project nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import static org.hisp.dhis.system.util.TextUtils.valueOf;

import java.io.InputStream;
import java.io.OutputStream;

import org.amplecode.staxwax.factory.XMLFactory;
import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dxf2.datavalue.DataValue;
import org.hisp.dhis.dxf2.datavalueset.DataValueSet;

public class DataValueSetMapper
{
    private static final String XMLNS = "xmlns";
    private static final String NS = "http://dhis2.org/schema/dxf/2.0";
    
    private static final String FIELD_DATAVALUESET = "dataValueSet";
    private static final String FIELD_DATAVALUE = "dataValue";
    private static final String FIELD_DATASET = "dataSet";
    private static final String FIELD_COMPLETEDATE = "completeDate";
    private static final String FIELD_DATAELEMENT = "dataElement";
    private static final String FIELD_CATEGORY_OPTION_COMBO = "categoryOptionCombo";
    private static final String FIELD_PERIOD = "period";
    private static final String FIELD_ORGUNIT = "orgUnit";
    private static final String FIELD_VALUE = "value";
    private static final String FIELD_STOREDBY = "storedBy";
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_COMMENT = "comment";
    private static final String FIELD_FOLLOWUP = "followUp";
    
    public static void toXml( DataValueSet dataValueSet, OutputStream out )
    {
        XMLWriter writer = XMLFactory.getXMLWriter( out );
        
        writer.openDocument();        
        writer.openElement( FIELD_DATAVALUESET, XMLNS, NS,
            FIELD_DATASET, dataValueSet.getDataSet(),
            FIELD_COMPLETEDATE, dataValueSet.getCompleteDate(),
            FIELD_PERIOD, dataValueSet.getPeriod(),
            FIELD_ORGUNIT, dataValueSet.getOrgUnit() );
        
        for ( DataValue dataValue : dataValueSet.getDataValues() )
        {
            writer.writeElement( FIELD_DATAVALUE, null,
                FIELD_DATAELEMENT, dataValue.getDataElement(),
                FIELD_CATEGORY_OPTION_COMBO, dataValue.getCategoryOptionCombo(),
                FIELD_PERIOD, dataValueSet.getPeriod(),
                FIELD_ORGUNIT, dataValueSet.getOrgUnit(),
                FIELD_VALUE, dataValue.getValue(),
                FIELD_STOREDBY, dataValue.getStoredBy(),
                FIELD_TIMESTAMP, dataValue.getTimestamp(),
                FIELD_COMMENT, dataValue.getComment(),
                FIELD_FOLLOWUP, valueOf( dataValue.getFollowup() ) );
        }
        
        writer.closeElement();
        writer.closeDocument();
    }
    
    public static DataValueSet fromXml( InputStream in )
    {
        XMLReader reader = XMLFactory.getXMLReader( in );

        reader.moveToStartElement( FIELD_DATAVALUESET );
        
        DataValueSet dataValueSet = new DataValueSet();
        
        dataValueSet.setDataSet( reader.getAttributeValue( FIELD_DATASET ) );
        dataValueSet.setCompleteDate( reader.getAttributeValue( FIELD_COMPLETEDATE ) );
        dataValueSet.setPeriod( reader.getAttributeValue( FIELD_PERIOD ) );
        dataValueSet.setOrgUnit( reader.getAttributeValue( FIELD_ORGUNIT ) );
        
        while ( reader.moveToStartElement( FIELD_DATAVALUE, FIELD_DATAVALUESET ) )
        {
            DataValue dataValue = new DataValue();
            
            dataValue.setDataElement( reader.getAttributeValue( FIELD_DATAELEMENT ) );
            dataValue.setCategoryOptionCombo( reader.getAttributeValue( FIELD_CATEGORY_OPTION_COMBO ) );
            dataValue.setPeriod( reader.getAttributeValue( FIELD_PERIOD ) );
            dataValue.setOrgUnit( reader.getAttributeValue( FIELD_ORGUNIT ) );
            dataValue.setValue( reader.getAttributeValue( FIELD_VALUE ) );
            dataValue.setStoredBy( reader.getAttributeValue( FIELD_STOREDBY ) );
            dataValue.setTimestamp( reader.getAttributeValue( FIELD_TIMESTAMP ) );
            dataValue.setComment( reader.getAttributeValue( FIELD_COMMENT ) );
            dataValue.setFollowup( valueOf( reader.getAttributeValue( FIELD_FOLLOWUP ) ) );
            
            dataValueSet.getDataValues().add( dataValue );
        }
        
        return dataValueSet;
    }
}
