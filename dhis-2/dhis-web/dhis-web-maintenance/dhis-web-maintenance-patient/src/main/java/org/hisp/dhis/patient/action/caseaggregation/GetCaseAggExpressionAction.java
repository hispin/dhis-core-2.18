package org.hisp.dhis.patient.action.caseaggregation;

import org.hisp.dhis.caseaggregation.CaseAggregationMapping;
import org.hisp.dhis.caseaggregation.CaseAggregationMappingService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

public class GetCaseAggExpressionAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;
    
    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private CaseAggregationMappingService caseAggregationMappingService;
    
    public void setCaseAggregationMappingService( CaseAggregationMappingService caseAggregationMappingService )
    {
        this.caseAggregationMappingService = caseAggregationMappingService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private String aggdeId;
    
    public void setAggdeId( String aggdeId )
    {
        this.aggdeId = aggdeId;
    }
    
    private String expression;
    
    public String getExpression()
    {
        return expression;
    }
        
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {    
        System.out.println("Inside GetCaseAggExpression Action");
        
        DataElement dataElement = dataElementService.getDataElement(  Integer.parseInt( aggdeId.split( ":" )[0] ) );
        
        DataElementCategoryOptionCombo deCoc = categoryService.getDataElementCategoryOptionCombo( Integer.parseInt( aggdeId.split( ":" )[1] ) );
        
        CaseAggregationMapping caseAggMapping = caseAggregationMappingService.getCaseAggregationMappingByOptionCombo( dataElement, deCoc );
        
        if( caseAggMapping != null )
        {
            expression = caseAggMapping.getExpression();
        }
        else
        {
            expression = " ";
        }
        
        return SUCCESS;
    }
}
