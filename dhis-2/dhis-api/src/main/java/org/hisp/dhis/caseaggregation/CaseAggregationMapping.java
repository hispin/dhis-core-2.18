package org.hisp.dhis.caseaggregation;

import java.io.Serializable;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;

public class CaseAggregationMapping implements Serializable
{

	private DataElement dataElement;
	
	private DataElementCategoryOptionCombo optionCombo;
	
	private String expression;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

	public CaseAggregationMapping()
	{
		
	}
	
	public CaseAggregationMapping( DataElement dataElement, DataElementCategoryOptionCombo optionCombo, String expression )
	{
		this.dataElement = dataElement;

		this.optionCombo = optionCombo;
		
		this.expression = expression;
	}
	
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + optionCombo.hashCode();
        result = result * prime + dataElement.hashCode();

        return result;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof CaseAggregationMapping) )
        {
            return false;
        }

        final CaseAggregationMapping other = (CaseAggregationMapping) o;

        return dataElement.equals( other.getDataElement() ) && optionCombo.equals( other.getOptionCombo() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
	
	public DataElement getDataElement() 
	{
		return dataElement;
	}

	public void setDataElement(DataElement dataElement) 
	{
		this.dataElement = dataElement;
	}

	public String getExpression() 
	{
		return expression;
	}

	public void setExpression(String expression) 
	{
		this.expression = expression;
	}

	public DataElementCategoryOptionCombo getOptionCombo() 
	{
		return optionCombo;
	}

	public void setOptionCombo(DataElementCategoryOptionCombo optionCombo) 
	{
		this.optionCombo = optionCombo;
	}
    	
}
