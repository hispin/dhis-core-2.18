package org.hisp.dhis.expression;

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

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.CategoryOptionComboXmlAdapter;
import org.hisp.dhis.common.adapter.DataElementXmlAdapter;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Set;

/**
 * An Expression is the expression of e.g. a validation rule. It consist of a
 * String representation of the rule as well as references to the data elements
 * and category option combos included in the expression.
 * <p/>
 * The expression can contain numbers and mathematical operators and contain references
 * to data elements and category option combos on the form:
 * <p/>
 * i) [1.2] where 1 refers to the data element identifier and 2 refers to the
 * category option combo identifier.
 * <p/>
 * ii) [1] where 1 refers to the data element identifier, in this case the formula
 * represents the total value for all category option combos for that data element.
 *
 * @author Margrethe Store
 * @version $Id: Expression.java 5011 2008-04-24 20:41:28Z larshelg $
 */
@XmlRootElement( name = "expression", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class Expression
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -4868682510629094282L;

    public static final String SEPARATOR = ".";
    public static final String EXP_OPEN = "[";
    public static final String EXP_CLOSE = "]";
    public static final String PAR_OPEN = "(";
    public static final String PAR_CLOSE = ")";

    /**
     * The unique identifier for this Expression.
     */
    private int id;

    /**
     * The Expression.
     */
    private String expression;

    /**
     * A description of the Expression.
     */
    private String description;

    /**
     * A reference to the DataElements in the Expression.
     */
    private Set<DataElement> dataElementsInExpression;

    /**
     * A reference to the optionCombos in the Expression.
     */
    private Set<DataElementCategoryOptionCombo> optionCombosInExpression;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Default empty Expression
     */
    public Expression()
    {
    }

    /**
     * Constructor with all the parameters.
     *
     * @param expression               The expression as a String
     * @param description              A description of the Expression.
     * @param dataElementsInExpression A reference to the DataElements in the Expression.
     */
    public Expression( String expression, String description, Set<DataElement> dataElementsInExpression,
                       Set<DataElementCategoryOptionCombo> optionCombosInExpression )
    {
        this.expression = expression;
        this.description = description;
        this.dataElementsInExpression = dataElementsInExpression;
        this.optionCombosInExpression = optionCombosInExpression;
    }

    // -------------------------------------------------------------------------
    // Equals and hashCode
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int PRIME = 31;

        int result = 1;

        result = PRIME * result + ((description == null) ? 0 : description.hashCode());

        result = PRIME * result + ((expression == null) ? 0 : expression.hashCode());

        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( obj == null )
        {
            return false;
        }

        if ( getClass() != obj.getClass() )
        {
            return false;
        }

        final Expression other = (Expression) obj;

        if ( description == null )
        {
            if ( other.description != null )
            {
                return false;
            }
        }
        else if ( !description.equals( other.description ) )
        {
            return false;
        }

        if ( expression == null )
        {
            if ( other.expression != null )
            {
                return false;
            }
        }
        else if ( !expression.equals( other.expression ) )
        {
            return false;
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @XmlElement
    @JsonProperty
    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    @XmlElementWrapper( name = "dataElements" )
    @XmlElement( name = "dataElement" )
    @XmlJavaTypeAdapter( DataElementXmlAdapter.class )
    @JsonProperty( value = "dataElements" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public Set<DataElement> getDataElementsInExpression()
    {
        return dataElementsInExpression;
    }

    public void setDataElementsInExpression( Set<DataElement> dataElementsInExpression )
    {
        this.dataElementsInExpression = dataElementsInExpression;
    }

    @XmlElementWrapper( name = "categoryOptionCombos" )
    @XmlElement( name = "categoryOptionCombo" )
    @XmlJavaTypeAdapter( CategoryOptionComboXmlAdapter.class )
    @JsonProperty( value = "categoryOptionCombos" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public Set<DataElementCategoryOptionCombo> getOptionCombosInExpression()
    {
        return optionCombosInExpression;
    }

    public void setOptionCombosInExpression( Set<DataElementCategoryOptionCombo> optionCombosInExpression )
    {
        this.optionCombosInExpression = optionCombosInExpression;
    }

    @XmlElement
    @JsonProperty
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }
}
