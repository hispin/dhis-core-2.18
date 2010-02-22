package org.hisp.dhis.patient.action.caseaggregation;

import org.hisp.dhis.caseaggregation.CaseAggregationMapping;
import org.hisp.dhis.caseaggregation.CaseAggregationMappingService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

public class SaveCaseAggregationMappingAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CaseAggregationMappingService caseAggregationMappingService;

    public void setCaseAggregationMappingService( CaseAggregationMappingService caseAggregationMappingService )
    {
        this.caseAggregationMappingService = caseAggregationMappingService;
    }

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

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String aggde;
    
    public void setAggde( String aggde )
    {
        this.aggde = aggde;
    }

    private String expression;

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String statusMessage;

    public String getStatusMessage()
    {
        return statusMessage;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        DataElement de = dataElementService.getDataElement( Integer.parseInt( aggde.split( ":" )[0] ) );

        DataElementCategoryOptionCombo optCombo = categoryService.getDataElementCategoryOptionCombo( Integer.parseInt( aggde.split( ":" )[1] ) );

        CaseAggregationMapping caseAggMapping = caseAggregationMappingService.getCaseAggregationMappingByOptionCombo(
            de, optCombo );

        if ( caseAggMapping == null )
        {
            caseAggMapping = new CaseAggregationMapping( de, optCombo, expression );

            caseAggregationMappingService.addCaseAggregationMapping( caseAggMapping );

            statusMessage = "Expression is Added";
        }
        else
        {
            caseAggMapping.setExpression( expression );

            caseAggregationMappingService.updateCaseAggregationMapping( caseAggMapping );

            statusMessage = "Expression is Updated";
        }

        return SUCCESS;
    }

}
