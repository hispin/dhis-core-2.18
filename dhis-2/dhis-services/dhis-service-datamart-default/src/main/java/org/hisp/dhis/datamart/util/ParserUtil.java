package org.hisp.dhis.datamart.util;

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

import static org.hisp.dhis.expression.Expression.SEPARATOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElementOperand;

/**
 * @author Lars Helge Overland
 * @version $Id: ParserUtil.java 5510 2008-07-30 16:30:27Z larshelg $
 */
public class ParserUtil
{
    private static final String NULL_REPLACEMENT = "0";
    
    private static final Pattern OPERAND_PATTERN = Pattern.compile( "(\\[\\d+\\" + SEPARATOR + "\\d+\\])" );
    
    /**
     * Returns the data element identifiers in the given expression. Returns null
     * if the formula is null, returns an empty set if no data elements exist in
     * the formula, otherwise a set of the data elements in the formula.
     * 
     * @param formula The formula to be parsed.
     */
    public static Set<Integer> getDataElementIdsInExpression( String expression )
    {
        Set<Integer> dataElementIdsInExpression = null;

        if ( expression != null )
        {
            dataElementIdsInExpression = new HashSet<Integer>();

            final Matcher matcher = OPERAND_PATTERN.matcher( expression );

            while ( matcher.find() )
            {
                String replaceString = matcher.group().replaceAll( "[\\[\\]]", "" );

                replaceString = replaceString.substring( 0, replaceString.indexOf( SEPARATOR ) );

                dataElementIdsInExpression.add( Integer.parseInt( replaceString ) );
            }
        }

        return dataElementIdsInExpression;
    }
    
    /**
     * Generates an expression where the Operand identifiers, consisting of 
     * data element id and category option combo id, are replaced
     * by the aggregated value for the relevant combination of data element,
     * period, and source.
     * 
     * @param formula The formula to parse.
     * @param valueMap The map containing data element identifiers and aggregated value.
     */
    public static String generateExpression( final String formula, final Map<DataElementOperand, Double> valueMap )
    {       
        try
        {
            final Matcher matcher = OPERAND_PATTERN.matcher( formula );
            
            final StringBuffer buffer = new StringBuffer();            
            
            while ( matcher.find() )
            {
                String replaceString = matcher.group().replaceAll( "[\\[\\]]", "" );
                
                int dataElementId = Integer.parseInt( replaceString.substring( 0, replaceString.indexOf( SEPARATOR ) ) );
                int categoryOptionComboId = Integer.parseInt( replaceString.substring( replaceString.indexOf( SEPARATOR ) + 1 ) );
                
                final DataElementOperand operand = new DataElementOperand( dataElementId, categoryOptionComboId );
                
                Double aggregatedValue = valueMap.get( operand );
                
                replaceString = ( aggregatedValue == null ) ? NULL_REPLACEMENT : String.valueOf( aggregatedValue );
                
                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            return buffer.toString();
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal data element or category combo id", ex );
        }
    }

    /**
     * Filters the data element operands based on the value type.
     * 
     * @param valueType the value type to filter.
     * @param aggregationOperator the aggregation operator to filter.
     */
    public static Collection<DataElementOperand> filterOperands( Collection<DataElementOperand> operands, String valueType,
        String aggregationOperator )
    {
        final Collection<DataElementOperand> filtered = new ArrayList<DataElementOperand>();

        for ( DataElementOperand operand : operands )
        {
            if ( operand.getValueType().equals( valueType )
                && operand.getAggregationOperator().equals( aggregationOperator ) )
            {
                filtered.add( operand );
            }
        }

        return filtered;
    }
}
