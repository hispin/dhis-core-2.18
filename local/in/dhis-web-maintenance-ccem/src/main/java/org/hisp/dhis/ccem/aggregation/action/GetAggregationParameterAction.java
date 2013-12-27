package org.hisp.dhis.ccem.aggregation.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.model.ModelAttributeValue;
import org.hisp.dhis.coldchain.model.ModelAttributeValueService;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeOption;
import org.hisp.dhis.coldchain.model.ModelTypeAttributeService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;

import com.opensymphony.xwork2.Action;

public class GetAggregationParameterAction implements Action 
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private LookupService lookupService;

	public void setLookupService(LookupService lookupService) 
	{
		this.lookupService = lookupService;
	}
	
    private OptionService optionService; 
    
    public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}
    private ModelAttributeValueService modelAttributeValueService;
    
	public void setModelAttributeValueService(
			ModelAttributeValueService modelAttributeValueService) {
		this.modelAttributeValueService = modelAttributeValueService;
	}

	private ModelTypeAttributeService modelTypeAttributeService;
	
    public void setModelTypeAttributeService(
			ModelTypeAttributeService modelTypeAttributeService) {
		this.modelTypeAttributeService = modelTypeAttributeService;
	}    
    
	// -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------
	private String aggTypeId;
	
    public void setAggTypeId(String aggTypeId) {
		this.aggTypeId = aggTypeId;
	}
    
    private Map<String,List<String>> lookUpParamMap;
    
    public Map<String, List<String>> getLookUpParamMap() {
		return lookUpParamMap;
	}
    List<Lookup> lookups;
	
    public List<Lookup> getLookups() 
    {
		return lookups;
	}

	// -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
	public String execute()
        throws Exception
    {
		Lookup lookup = lookupService.getLookup(Integer.parseInt(aggTypeId));
		String lookupType = lookup.getName();
		
		lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( lookupType ) );
		
		lookUpParamMap = new HashMap<String,List<String>>();
		for(Lookup lp : lookups)
		{
			if(lp.getName().equalsIgnoreCase("optionset"))
			{
				OptionSet os = optionService.getOptionSet(Integer.parseInt(lp.getValue()));
				
				lookUpParamMap.put(lp.getName(), os.getOptions());
			}
			if(lp.getName().equalsIgnoreCase("modeltypeattribute"))
			{
			   ModelTypeAttribute mtAttribute =	modelTypeAttributeService.getModelTypeAttribute(Integer.parseInt(lp.getValue()));
			   
			   List<ModelAttributeValue> modelAttValueList = new ArrayList<ModelAttributeValue>( modelAttributeValueService.getAllModelAttributeValuesByModelTypeAttribute(mtAttribute) );
			   
			   List<String> modelNameList = new ArrayList<String>();
			   
			   for(ModelAttributeValue maValue : modelAttValueList)
			   {
				   modelNameList.add(maValue.getValue());
			   }
			   
			   lookUpParamMap.put(lp.getName(), modelNameList);  
			}
		}
		
    	return SUCCESS;
    }
}
