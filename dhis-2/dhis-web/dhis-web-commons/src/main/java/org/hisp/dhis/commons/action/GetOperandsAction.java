package org.hisp.dhis.commons.action;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.Operand;

import com.opensymphony.xwork2.Action;

public class GetOperandsAction
    implements Action
{
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    public Collection<Operand> operands;
    
    public Collection<Operand> getOperands()
    {
        return operands;
    }

    public String execute()
    {
        operands = dataElementService.getAllOperands();
        
        return SUCCESS;
    }
}
