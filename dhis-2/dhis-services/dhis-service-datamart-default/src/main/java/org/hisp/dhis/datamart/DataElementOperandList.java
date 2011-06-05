package org.hisp.dhis.datamart;

import java.util.Arrays;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public class DataElementOperandList
{
    private List<DataElementOperand> operands;
    
    private Object[] valueList;
    
    private boolean hasValues;
    
    public DataElementOperandList( List<DataElementOperand> operands )
    {
        this.operands = operands;
    }
    
    public void init( Period period, OrganisationUnit unit )
    {
        this.hasValues = false;
        
        if ( valid() )
        {
            this.valueList = new Object[operands.size() + 2];
            this.valueList[0] = period.getId();
            this.valueList[1] = unit.getId();
        }
    }
    
    public void addValue( DataElementOperand operand, Double value )
    {
        if ( valid() )
        {
            final int index = operands.indexOf( operand );
            
            if ( index != -1 && value != null )
            {                
                this.valueList[index + 2] = value;
                this.hasValues = true;
            }
        }
    }
    
    public List<Object> getList()
    {
        return valid() ? Arrays.asList( this.valueList ) : null;
    }
    
    public boolean valid()
    {
        return operands != null && operands.size() > 0;
    }
    
    public boolean hasValues()
    {
        return hasValues;
    }
}
