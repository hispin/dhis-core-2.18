package org.hisp.dhis.dd.action.dataelement;

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
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.system.util.ConversionUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Torgeir Lorange Ostby
 * @author Hans S. Toemmerholt
 * @version $Id: AddDataElementAction.java 6216 2008-11-06 18:06:42Z eivindwa $
 */
public class AddDataElementAction
    extends ActionSupport
{
    private static final String TRUE = "on";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String alternativeName;

    public void setAlternativeName( String alternativeName )
    {
        this.alternativeName = alternativeName;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String domainType;

    public void setDomainType( String domainType )
    {
        this.domainType = domainType;
    }

    private String numberType;

    public void setNumberType( String numberType )
    {
        this.numberType = numberType;
    }

    private String valueType;

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    private String aggregationOperator;

    public void setAggregationOperator( String aggregationOperator )
    {
        this.aggregationOperator = aggregationOperator;
    }

    private String calculated;

    public void setCalculated( String calculated )
    {
        this.calculated = calculated;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }

    private Collection<String> aggregationLevels;

    public void setAggregationLevels( Collection<String> aggregationLevels )
    {
        this.aggregationLevels = aggregationLevels;
    }

    private String saved;

    public void setSaved( String saved )
    {
        this.saved = saved;
    }

    private List<String> dataElementIds;

    public void setDataElementIds( List<String> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    private List<String> factors;

    public void setFactors( List<String> factors )
    {
        this.factors = factors;
    }

    private Integer selectedCategoryComboId;

    public void setSelectedCategoryComboId( Integer selectedCategoryComboId )
    {
        this.selectedCategoryComboId = selectedCategoryComboId;
    }

    private Boolean zeroIsSignificant;

    public void setZeroIsSignificant( Boolean zeroIsSignificant )
    {
        this.zeroIsSignificant = zeroIsSignificant;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        if ( alternativeName != null && alternativeName.trim().length() == 0 )
        {
            alternativeName = null;
        }

        if ( code != null && code.trim().length() == 0 )
        {
            code = null;
        }

        if ( description != null && description.trim().length() == 0 )
        {
            description = null;
        }

        // ---------------------------------------------------------------------
        // Create data element
        // ---------------------------------------------------------------------

        DataElement dataElement = null;

        DataElementCategoryCombo defaultCategoryCombo = dataElementCategoryService
            .getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );

        DataElementCategoryCombo categoryCombo = dataElementCategoryService
            .getDataElementCategoryCombo( selectedCategoryComboId );

        if ( calculated != null && calculated.equals( TRUE ) )
        {
            categoryCombo = defaultCategoryCombo;

            dataElement = new CalculatedDataElement();

            CalculatedDataElement calculatedDataElement = (CalculatedDataElement) dataElement;

            Set<DataElement> expressionDataElements = new HashSet<DataElement>();

            String expressionString = "";

            for ( int i = 0; i < dataElementIds.size(); i++ )
            {
                String operandId = dataElementIds.get( i );

                String dataElementIdString = operandId.substring( 0, operandId.indexOf( SEPARATOR ) );

                DataElement expressionDataElement = dataElementService.getDataElement( Integer
                    .parseInt( dataElementIdString ) );

                if ( expressionDataElement == null )
                {
                    continue;
                }

                Double factor = Double.parseDouble( factors.get( i ) );

                expressionString += " + ([" + operandId + "] * " + factor + ")";

                expressionDataElements.add( expressionDataElement );
            }

            if ( expressionString.length() > 3 )
            {
                expressionString = expressionString.substring( 3 );
            }

            Expression expression = new Expression( expressionString, "", expressionDataElements );

            calculatedDataElement.setExpression( expression );

            calculatedDataElement.setSaved( saved != null );
        }
        else
        {
            dataElement = new DataElement();
        }

        dataElement.setName( name );
        dataElement.setAlternativeName( alternativeName );
        dataElement.setShortName( shortName );
        dataElement.setCode( code );
        dataElement.setDescription( description );
        dataElement.setActive( true );
        dataElement.setDomainType( domainType );
        dataElement.setType( valueType );
        dataElement.setNumberType( numberType );
        dataElement.setAggregationOperator( aggregationOperator );
        dataElement.setUrl( url );
        dataElement.setCategoryCombo( categoryCombo );
        dataElement.setAggregationLevels( new ArrayList<Integer>( ConversionUtils
            .getIntegerCollection( aggregationLevels ) ) );
        
        dataElement.setZeroIsSignificant( zeroIsSignificant );

        dataElementService.addDataElement( dataElement );

        return SUCCESS;
    }
}
