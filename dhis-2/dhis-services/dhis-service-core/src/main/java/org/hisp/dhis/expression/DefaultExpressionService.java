package org.hisp.dhis.expression;

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
import static org.hisp.dhis.system.util.MathUtils.calculateExpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.util.MathUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Margrethe Store
 * @author Lars Helge Overland
 * @version $Id: DefaultExpressionService.java 6463 2008-11-24 12:05:46Z larshelg $
 */
@Transactional
public class DefaultExpressionService
    implements ExpressionService
{
    private static final Log log = LogFactory.getLog( DefaultExpressionService.class );
    
    private static final String NULL_REPLACEMENT = "0";
    private static final String FORMULA_EXPRESSION = "(\\[\\d+\\" + SEPARATOR + "\\d+\\])";
    private static final String DESCRIPTION_EXPRESSION = "\\[.+?\\" + SEPARATOR + ".+?\\]";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericStore<Expression> expressionStore;

    public void setExpressionStore( GenericStore<Expression> expressionStore )
    {
        this.expressionStore = expressionStore;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryOptionComboService categoryOptionComboService;

    public void setCategoryOptionComboService( DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.categoryOptionComboService = dataElementCategoryOptionComboService;
    }

    // -------------------------------------------------------------------------
    // Expression CRUD operations
    // -------------------------------------------------------------------------

    public int addExpression( Expression expression )
    {
        return expressionStore.save( expression );
    }

    public void deleteExpression( Expression expression )
    {
        expressionStore.delete( expression );
    }

    public Expression getExpression( int id )
    {
        return expressionStore.get( id );
    }

    public void updateExpression( Expression expression )
    {
        expressionStore.update( expression );
    }

    public Collection<Expression> getAllExpressions()
    {
        return expressionStore.getAll();
    }

    // -------------------------------------------------------------------------
    // Business logic
    // -------------------------------------------------------------------------

    public Double getExpressionValue( Expression expression, Period period, Source source, boolean nullIfNoValues )
    {
        final String expressionString = generateExpression( expression.getExpression(), period, source, nullIfNoValues );

        return expressionString != null ? calculateExpression( expressionString ) : null;
    }    
    
    public Set<DataElement> getDataElementsInCalculatedDataElement( int id )
    {
        final DataElement dataElement = dataElementService.getDataElement( id );
        
        if ( dataElement != null && dataElement instanceof CalculatedDataElement )
        {
            return getDataElementsInExpression( ((CalculatedDataElement) dataElement).getExpression().getExpression() );
        }
        
        return null;
    }

    public Set<DataElement> getDataElementsInExpression( String expression )
    {
        Set<DataElement> dataElementsInExpression = null;

        if ( expression != null )
        {
            dataElementsInExpression = new HashSet<DataElement>();

            final Matcher matcher = getMatcher( "(\\[\\d+\\" + SEPARATOR + "\\d+\\])", expression );

            while ( matcher.find() )
            {
                final DataElement dataElement = dataElementService.getDataElement( getOperand( matcher.group() ).getDataElementId() );

                if ( dataElement != null )
                {
                    dataElementsInExpression.add( dataElement );
                }
            }
        }

        return dataElementsInExpression;
    }
    
    public String convertExpression( String expression, Map<Object, Integer> dataElementMapping, Map<Object, Integer> categoryOptionComboMapping )
    {
        StringBuffer convertedFormula = new StringBuffer();
        
        if ( expression != null )
        {
            final Matcher matcher = getMatcher( FORMULA_EXPRESSION, expression );

            while ( matcher.find() )
            {
                String match = matcher.group();

                final Operand operand = getOperand( match );
                
                final Integer mappedDataElementId = dataElementMapping.get( operand.getDataElementId() );
                final Integer mappedCategoryOptionComboId = categoryOptionComboMapping.get( operand.getOptionComboId() );
                
                if ( mappedDataElementId == null )
                {
                    log.info( "Data element identifier refers to non-existing object: " + operand.getDataElementId() );
                    
                    match = NULL_REPLACEMENT;
                }
                else if ( mappedCategoryOptionComboId == null )
                {
                    log.info( "Category option combo identifer refers to non-existing object: " + operand.getOptionComboId() );
                    
                    match = NULL_REPLACEMENT;
                }
                else
                {
                    match = "[" + mappedDataElementId + SEPARATOR + mappedCategoryOptionComboId + "]";
                }
                
                matcher.appendReplacement( convertedFormula, match );
            }

            matcher.appendTail( convertedFormula );
        }
        
        return convertedFormula.toString();
    }

    public Set<Operand> getOperandsInExpression( String expression )
    {
        Set<Operand> operandsInExpression = null;

        if ( expression != null )
        {
            operandsInExpression = new HashSet<Operand>();

            final Matcher matcher = getMatcher( FORMULA_EXPRESSION, expression );

            while ( matcher.find() )
            {
                operandsInExpression.add( getOperand( matcher.group() ) );
            }
        }

        return operandsInExpression;
    }
    
    public int expressionIsValid( String formula )
    {
        StringBuffer buffer = new StringBuffer();
        
        final Matcher matcher = getMatcher( DESCRIPTION_EXPRESSION, formula );

        int dataElementId = -1;
        int categoryOptionComboId = -1;

        while ( matcher.find() )
        {
            String match = matcher.group();

            match = match.replaceAll( "[\\[\\]]", "" );

            final String dataElementIdString = match.substring( 0, match.indexOf( SEPARATOR ) );
            final String categoryOptionComboIdString = match.substring( match.indexOf( SEPARATOR ) + 1, match.length() );

            try
            {
                dataElementId = Integer.parseInt( dataElementIdString );
            }
            catch ( NumberFormatException ex )
            {
                return DATAELEMENT_ID_NOT_NUMERIC;
            }

            try
            {
                categoryOptionComboId = Integer.parseInt( categoryOptionComboIdString );
            }
            catch ( NumberFormatException ex )
            {
                return CATEGORYOPTIONCOMBO_ID_NOT_NUMERIC;
            }

            if ( dataElementService.getDataElement( dataElementId ) == null )
            {
                return DATAELEMENT_DOES_NOT_EXIST;
            }

            if ( categoryOptionComboService.getDataElementCategoryOptionCombo( categoryOptionComboId ) == null )
            {
                return CATEGORYOPTIONCOMBO_DOES_NOT_EXIST;
            }

            // -----------------------------------------------------------------
            // Replacing the operand with 1 in order to later be able to verify
            // that the formula is mathematically valid
            // -----------------------------------------------------------------

            matcher.appendReplacement( buffer, "1" );
        }
        
        matcher.appendTail( buffer );
        
        if ( MathUtils.expressionHasErrors( buffer.toString() ) )
        {
            return EXPRESSION_NOT_WELL_FORMED;
        }        

        return VALID;
    }

    public String getExpressionDescription( String formula )
    {
        StringBuffer buffer = null;

        if ( formula != null )
        {
            buffer = new StringBuffer();

            final Matcher matcher = getMatcher( DESCRIPTION_EXPRESSION, formula );

            while ( matcher.find() )
            {
                String replaceString = matcher.group();
                
                final Operand operand = getOperand( replaceString );
                
                final DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
                final DataElementCategoryOptionCombo categoryOptionCombo = 
                    categoryOptionComboService.getDataElementCategoryOptionCombo( operand.getOptionComboId() );

                if ( dataElement == null )
                {
                    throw new IllegalArgumentException( "Identifier does not reference a data element: "
                        + operand.getDataElementId() );
                }

                if ( categoryOptionCombo == null )
                {
                    throw new IllegalArgumentException( "Identifier does not reference a category option combo: "
                        + operand.getOptionComboId() );
                }

                replaceString = dataElement.getName() + SEPARATOR + categoryOptionCombo.getName();

                if ( replaceString.endsWith( SEPARATOR ) )
                {
                    replaceString = replaceString.substring( 0, replaceString.length() - 1 );
                }

                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );
        }

        return buffer != null ? buffer.toString() : null;
    }

    public String replaceCDEsWithTheirExpression( String expression )
    {
        StringBuffer buffer = null;

        if ( expression != null )
        {
            buffer = new StringBuffer();

            final Set<DataElement> caclulatedDataElementsInExpression = getDataElementsInExpression( expression );

            final Iterator<DataElement> iterator = caclulatedDataElementsInExpression.iterator();

            while ( iterator.hasNext() )
            {
                if ( !(iterator.next() instanceof CalculatedDataElement) )
                {
                    iterator.remove();
                }
            }

            final Matcher matcher = getMatcher( FORMULA_EXPRESSION, expression );

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                for ( DataElement dataElement : caclulatedDataElementsInExpression )
                {
                    if ( replaceString.startsWith( "[" + dataElement.getId() + SEPARATOR ) )
                    {
                        replaceString = ((CalculatedDataElement) dataElement).getExpression().getExpression();

                        break;
                    }
                }

                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );
        }

        return buffer != null ? buffer.toString() : null;
    }

    public String generateExpression( String expression, Period period, Source source, boolean nullIfNoValues )
    {
        StringBuffer buffer = null;

        if ( expression != null )
        {
            final Matcher matcher = getMatcher( FORMULA_EXPRESSION, expression );

            buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                final Operand operand = getOperand( replaceString );
                
                final DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
                final DataElementCategoryOptionCombo categoryOptionCombo = 
                    categoryOptionComboService.getDataElementCategoryOptionCombo( operand.getOptionComboId() );

                final String value = dataValueService.getValue( dataElement, period, source, categoryOptionCombo );
                
                if ( value == null && nullIfNoValues )
                {
                    return null;
                }
                
                replaceString = ( value == null ) ? NULL_REPLACEMENT : value;
                
                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );
        }

        return buffer != null ? buffer.toString() : null;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Matcher getMatcher( String regex, String expression )
    {
        final Pattern pattern = Pattern.compile( regex );

        return pattern.matcher( expression );
    }
    
    private Operand getOperand( String formula )
    {
        formula = formula.replaceAll( "[\\[\\]]", "" );
        
        final int dataElementId = Integer.parseInt( formula.substring( 0, formula.indexOf( SEPARATOR ) ) );
        final int categoryOptionComboId = Integer.parseInt( formula.substring( formula.indexOf( SEPARATOR ) + 1, formula.length() ) );
        
        return new Operand( dataElementId, categoryOptionComboId ); 
    }
}
