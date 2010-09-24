package org.hisp.dhis.web.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.web.api.model.DataSetValue;
import org.hisp.dhis.web.api.model.DataValue;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultDataValueService implements IDataValueService {

	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	@Autowired
	private org.hisp.dhis.dataset.DataSetService dataSetService;
	
	@Autowired
	private IPeriodService periodService;
	
	@Autowired
	private org.hisp.dhis.dataelement.DataElementCategoryService categoryService;
	
	@Autowired
	private org.hisp.dhis.datavalue.DataValueService dataValueService;
	
	@Autowired
    private CurrentUserService currentUserService;
	
	
	// -------------------------------------------------------------------------
    // DataValueService
    // -------------------------------------------------------------------------


	@Override
	public String saveValues(DataSetValue dataSetValue) {
		
		Collection<OrganisationUnit> units = currentUserService.getCurrentUser().getOrganisationUnits();
        OrganisationUnit unit = null;
        
        if( units.size() > 0 )
        {
        	unit = units.iterator().next();       	
        }
        else
        {
        	return "INVALID_REPORTING_UNIT";
        }
		
		org.hisp.dhis.dataset.DataSet dataSet = dataSetService.getDataSet( dataSetValue.getId() );
		
		if( !dataSetService.getDataSetsBySource( unit ).contains( dataSet) )
		{
			return "INVALID_DATASET_ASSOCIATION";
		}
		
		org.hisp.dhis.period.Period selectedPeriod = periodService.getPeriod( dataSetValue.getpName(), dataSet );
		
		if( selectedPeriod == null )
		{
			return "INVALID_PERIOD";
		}
		
		Collection<org.hisp.dhis.dataelement.DataElement> dataElements = dataSet.getDataElements();			
		Collection<Integer> dataElementIds = new ArrayList<Integer>(dataSetValue.getDataValues().size());
		
		for( DataValue dv : dataSetValue.getDataValues() )
		{
			dataElementIds.add( dv.getId() );		
		}
		
		if( dataElements.size() != dataElementIds.size() )
		{
			return "INVALID_DATASET";
		}
		
		Map<Integer, org.hisp.dhis.dataelement.DataElement> dataElementMap = new HashMap<Integer, org.hisp.dhis.dataelement.DataElement>();
		for( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
		{
			if( !dataElementIds.contains( dataElement.getId()) )
			{
				return "INVALID_DATASET";
			}
			dataElementMap.put(dataElement.getId(), dataElement);
		}
		
		//Everything is fine, hence save		
		saveDataValues( dataSetValue, dataElementMap, selectedPeriod, unit, categoryService.getDefaultDataElementCategoryOptionCombo() );		
		
		return "SUCCESS";
		
	}
	
	// -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------
	
	
	private void saveDataValues(DataSetValue dataSetValue,
			Map<Integer, DataElement> dataElementMap, Period period,
			OrganisationUnit orgUnit, DataElementCategoryOptionCombo optionCombo) {
		
		
		org.hisp.dhis.dataelement.DataElement dataElement;
		String value;
		
		for( DataValue dv : dataSetValue.getDataValues() )
		{			
			value = dv.getVal();		
			
			if ( value != null && value.trim().length() == 0 )
	        {				
	            value = null;
	        }
	        
	        if ( value != null )
	        {
	            value = value.trim();
	        }
			
			dataElement = dataElementMap.get( dv.getId() );
			org.hisp.dhis.datavalue.DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period, optionCombo );
			
			if ( dataValue == null )												
	        {		
				if( value != null )
				{
					dataValue = new org.hisp.dhis.datavalue.DataValue( dataElement, period, orgUnit, value, "", new Date(), "", optionCombo );
	                dataValueService.addDataValue( dataValue );
				}				
	        }
	        else
	        {            
	            dataValue.setValue( value );
	            dataValue.setTimestamp( new Date() );
	            dataValueService.updateDataValue( dataValue );            
	        }				
			
		}		
	}
}
