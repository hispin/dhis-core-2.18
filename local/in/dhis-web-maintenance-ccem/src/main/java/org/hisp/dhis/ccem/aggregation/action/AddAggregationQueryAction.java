package org.hisp.dhis.ccem.aggregation.action;

import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

public class AddAggregationQueryAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CaseAggregationConditionService aggregationConditionService;

    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }
    
    private DataElementService dataElementService;
    
    public void setDataElementService(DataElementService dataElementService) 
    {
		this.dataElementService = dataElementService;
	}
    
    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
	public String execute()
        throws Exception
    {
    	//aggregationConditionService.addCaseAggregationCondition(arg0);
        
        return SUCCESS;
    }

}
