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

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.dhis14.util.Dhis14ParsingUtils;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CalculatedDataElementAssociationConverter
    implements XMLConverter
{
    public static final String ELEMENT_NAME = "DataElementCalculated";
    
    private static final String FIELD_ID = "DataElementCalculatedID";
    private static final String FIELD_DATAELEMENT = "DataElementID";
    private static final String FIELD_FACTOR = "DataElementFactor";
    
    private Map<Integer, String> expressionMap;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public CalculatedDataElementAssociationConverter()
    {
    }

    /**
     * Constructor for read operations.
     */
    public CalculatedDataElementAssociationConverter( Map<Integer, String> expressionMap )
    {
        this.expressionMap = expressionMap;
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        Collection<CalculatedDataElement> elements = params.getCalculatedDataElements();
        
        for ( CalculatedDataElement element : elements )
        {
            Map<Operand, Double> factorMap = Dhis14ParsingUtils.getOperandFactors( element );
            
            for ( Entry<Operand, Double> entry : factorMap.entrySet() )
            {
                writer.openElement( ELEMENT_NAME );
                
                writer.writeElement( FIELD_ID, String.valueOf( element.getId() ) );
                writer.writeElement( FIELD_DATAELEMENT, String.valueOf( entry.getKey().getDataElementId() ) );
                writer.writeElement( FIELD_FACTOR, String.valueOf( entry.getValue() ) );
                
                writer.closeElement();
            }
        }
    }

    public void read( XMLReader reader, ImportParams params )
    {
        final Map<String, String> values = reader.readElements( ELEMENT_NAME );

        final Integer calculatedDataElementId = Integer.parseInt( values.get( FIELD_ID ) );
        final Integer dataElementId = Integer.parseInt( values.get( FIELD_DATAELEMENT ) );
        final Double factor = Double.parseDouble( values.get( FIELD_FACTOR ) );
        
        String formula = "([" + dataElementId + SEPARATOR + 1 + "]*" + factor + ")";
        
        if ( expressionMap.containsKey( calculatedDataElementId ) )
        {
            formula = "+" + formula;
        }
        
        expressionMap.put( calculatedDataElementId, formula );
    }
}
