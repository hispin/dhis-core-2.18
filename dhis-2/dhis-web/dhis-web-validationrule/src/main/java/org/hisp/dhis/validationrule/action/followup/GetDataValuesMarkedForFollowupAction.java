package org.hisp.dhis.validationrule.action.followup;

import java.util.Collection;

import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.datavalue.DeflatedDataValue;

import com.opensymphony.xwork2.Action;

public class GetDataValuesMarkedForFollowupAction
    implements Action
{
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private Collection<DeflatedDataValue> dataValues;
    
    public Collection<DeflatedDataValue> getDataValues()
    {
        return dataValues;
    }

    public String execute()
    {
        dataValues = dataValueService.getDataValuesMarkedForFollowup();
        
        return SUCCESS;
    }
}
