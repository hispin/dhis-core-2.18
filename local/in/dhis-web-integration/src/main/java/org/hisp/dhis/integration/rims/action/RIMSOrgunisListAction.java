package org.hisp.dhis.integration.rims.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.integration.rims.api.RIMSService;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_Orgunit;
import org.hisp.dhis.integration.rims.api.RIMS_OrgUnitNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork.ActionSupport;

public class RIMSOrgunisListAction extends ActionSupport
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private RIMSService rimsService;
	
	public void setRimsService(RIMSService rimsService) 
	{
		this.rimsService = rimsService;
	}

	private OrganisationUnitService organisationUnitService;
	
	public void setOrganisationUnitService(
			OrganisationUnitService organisationUnitService) {
		this.organisationUnitService = organisationUnitService;
	}

    // --------------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------------

	Map<String,String> rimsOUNameMap;

	public Map<String, String> getRimsOUNameMap() 
	{
		return rimsOUNameMap;
	}

	List<RIMS_Mapping_Orgunit> rimsOrgUnits;
	
	public List<RIMS_Mapping_Orgunit> getRimsOrgUnits() 
	{
		return rimsOrgUnits;
	}

	// --------------------------------------------------------------------------
    // Action Implementation
    // --------------------------------------------------------------------------
    public String execute()
    throws Exception
    {    	
    	rimsOUNameMap = new HashMap<String,String>();
    	rimsOrgUnits = new ArrayList<RIMS_Mapping_Orgunit>(rimsService.getAllMappingOrgunits());
    	
    	Collections.sort(rimsOrgUnits, new RIMS_OrgUnitNameComparator());
    	
    	Iterator<RIMS_Mapping_Orgunit> iterator1 = rimsOrgUnits.iterator();
    	while(iterator1.hasNext())
    	{
    		
    		RIMS_Mapping_Orgunit mappingOU = (RIMS_Mapping_Orgunit) iterator1.next();
    		rimsOUNameMap.put(mappingOU.getRimsid(), organisationUnitService.getOrganisationUnit(Integer.parseInt(mappingOU.getDhisid())).getName());
    		
    	}
    	    	
    	return SUCCESS;
    }

}
