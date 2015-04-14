package org.hisp.dhis.ccem.aggregation.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.coldchain.aggregation.CCEIAggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class UpdateAggregationQueryAction  implements Action
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

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    private CCEIAggregationService cceiAggregationService;

    public void setCceiAggregationService( CCEIAggregationService cceiAggregationService )
    {
        this.cceiAggregationService = cceiAggregationService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------

    /*
    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }
    */
    
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private String aggType;
    
    public void setAggType( String aggType )
    {
        this.aggType = aggType;
    }

    private Integer dataElementId;

    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
        /*
        if( name == null &&  name.equalsIgnoreCase( "" ))
        {
            name = dataElement.getName();
        }
        
        */
        
        /**
         * TODO support for category option combo
         */
        
        CaseAggregationCondition expression = aggregationConditionService.getCaseAggregationCondition( id );
        
        if( expression != null )
        {
             
            if ( aggType.equals( Lookup.CCEI_AGG_TYPE_STORAGE_CAPACITY ) )
            {
                expression.setName( dataElement.getName() );
                expression.setOperator( aggType );
                expression.setAggregationExpression( "NONE");
                expression.setAggregationDataElement( dataElement );
                expression.setOptionCombo( dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );
                
                aggregationConditionService.updateCaseAggregationCondition( expression );
                
            }
            else if ( aggType.equals( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_MODEL ) )
            {
                HttpServletRequest request = ServletActionContext.getRequest();
                
                Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_WORKING_STATUS_OPTIONSET );
                
                String[] workingStatusValues = request.getParameterValues( lookup.getName() );
                String workingStatus = "";
                for( String workingStatusValue : workingStatusValues )
                {
                    workingStatus += "'" + workingStatusValue + "',";
                }
                
                lookup = lookupService.getLookupByName( Lookup.CCEI_MODEL_MODELTYPEATTRIBUTE );
                
                String modelName = request.getParameter( lookup.getName() );
                modelName = "'" + modelName + "'";
                Integer modelTypeAttributeId = Integer.parseInt( lookup.getValue() );
                
                workingStatus = workingStatus.substring( 0, workingStatus.length()-1 );
                
                lookup = lookupService.getLookupByName( Lookup.CCEI_REF_EQUIPMENTTYPE_ID );
                
                Integer equipmentTypeId = Integer.parseInt( lookup.getValue() );
                
                String query = cceiAggregationService.getQueryForRefrigeratorWorkingStatus( equipmentTypeId, modelTypeAttributeId, modelName, workingStatus );
                
                expression.setName( dataElement.getName() );
                expression.setOperator( aggType );
                expression.setAggregationExpression( query );
                expression.setAggregationDataElement( dataElement );
                expression.setOptionCombo( dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );
                
                aggregationConditionService.updateCaseAggregationCondition( expression );
                
            }
            else if ( aggType.equals( Lookup.CCEI_AGG_TYPE_REF_UTILIZATION ) )
            {
                HttpServletRequest request = ServletActionContext.getRequest();
                
                Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_UTILIZATION_OPTIONSET );
                String utilization = request.getParameter( lookup.getName() );
                utilization = "'" + utilization  + "'";
                
                lookup = lookupService.getLookupByName( Lookup.CCEI_MODEL_MODELTYPEATTRIBUTE );
                String modelName = request.getParameter( lookup.getName() );
                modelName = "'" + modelName + "'";
                Integer modelTypeAttributeId = Integer.parseInt( lookup.getValue() );

                lookup = lookupService.getLookupByName( Lookup.CCEI_REF_EQUIPMENTTYPE_ID );            
                Integer equipmentTypeId = Integer.parseInt( lookup.getValue() );

                String query = cceiAggregationService.getQueryForRefrigeratorUtilization( equipmentTypeId, modelTypeAttributeId, modelName,  utilization );
                
                expression.setName( dataElement.getName() );
                expression.setOperator( aggType );
                expression.setAggregationExpression( query );
                expression.setAggregationDataElement( dataElement );
                expression.setOptionCombo( dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );
                
                aggregationConditionService.updateCaseAggregationCondition( expression );
                
                
            }
            else if ( aggType.equals( Lookup.CCEI_AGG_TYPE_REF_WORKING_STATUS_BY_TYPE ) )
            {
                HttpServletRequest request = ServletActionContext.getRequest();
                
                Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_WORKING_STATUS_OPTIONSET );
                
                String[] workingStatusValues = request.getParameterValues( lookup.getName() );
                String workingStatus = "";
                for( String workingStatusValue : workingStatusValues )
                {
                    workingStatus += "'" + workingStatusValue + "',";
                }
                
                lookup = lookupService.getLookupByName( Lookup.CCEI_EQUIPMENTTYPE_MODELTYPEATTRIBUTE );
                
                String modelName = request.getParameter( lookup.getName() );
                modelName = "'" + modelName + "'";
                Integer modelTypeAttributeId = Integer.parseInt( lookup.getValue() );
                
                workingStatus = workingStatus.substring( 0, workingStatus.length()-1 );
                
                lookup = lookupService.getLookupByName( Lookup.CCEI_REF_EQUIPMENTTYPE_ID );
                
                Integer equipmentTypeId = Integer.parseInt( lookup.getValue() );
                
                String query = cceiAggregationService.getQueryForRefrigeratorWorkingStatus( equipmentTypeId, modelTypeAttributeId, modelName, workingStatus );
                
                expression.setName( dataElement.getName() );
                expression.setOperator( aggType );
                expression.setAggregationExpression( query );
                expression.setAggregationDataElement( dataElement );
                expression.setOptionCombo( dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );
                
                aggregationConditionService.updateCaseAggregationCondition( expression );
                
            }
            
            else if( aggType.equals( Lookup.CCEI_AGG_TYPE_REF_TEMP_ALARMS ) )
            {
                HttpServletRequest request = ServletActionContext.getRequest();
                
                String refTempAlarm = request.getParameter( Lookup.CCEI_AGG_TYPE_REF_TEMP_ALARMS );
    
                Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_REF_EQUIPMENTTYPE_ID );
                
                Integer equipmentTypeId = Integer.parseInt( lookup.getValue() );

                lookup = lookupService.getLookupByName( refTempAlarm );
                
                if( lookup.getName().equals( Lookup.CCEI_NO_OF_REF_WITH_HIGHTEMP_ALARM) || lookup.getName().equals( Lookup.CCEI_NO_OF_REF_WITH_LOWTEMP_ALARM) )
                {
                    String query = cceiAggregationService.getQueryForRefrigeratorCountByTemperatureAlarm( equipmentTypeId, Integer.parseInt(lookup.getValue() ) );
                    
                    expression.setName( dataElement.getName() );
                    expression.setOperator( aggType );
                    expression.setAggregationExpression( query );
                    expression.setAggregationDataElement( dataElement );
                    expression.setOptionCombo( dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );
                    
                    aggregationConditionService.updateCaseAggregationCondition( expression );
                }
                
                else if( lookup.getName().equals( Lookup.CCEI_FACILITY_WITH_HIGHTEMP_ALARM) || lookup.getName().equals( Lookup.CCEI_FACILITY_WITH_LOWTEMP_ALARM ) || lookup.getName().equals( Lookup.CCEI_FACILITY_WITH_TEMP_ALARM ) )
                {
                    String query = cceiAggregationService.getQueryForRefrigeratorTemperatureAlarmByFacilty( equipmentTypeId, lookup.getValue() );
                    
                    expression.setName( dataElement.getName() );
                    expression.setOperator( aggType );
                    expression.setAggregationExpression( query );
                    expression.setAggregationDataElement( dataElement );
                    expression.setOptionCombo( dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );
                    
                    aggregationConditionService.updateCaseAggregationCondition( expression );
                    
                }
            }
        }
        
        return SUCCESS;
    }
}
