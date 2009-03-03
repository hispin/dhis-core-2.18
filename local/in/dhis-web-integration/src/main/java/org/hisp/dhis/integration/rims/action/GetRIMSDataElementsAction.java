package org.hisp.dhis.integration.rims.action;

import java.util.Map;
import java.util.TreeMap;

import org.hisp.dhis.integration.rims.api.RIMSService;

import com.opensymphony.xwork.ActionSupport;

public class GetRIMSDataElementsAction extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	@SuppressWarnings("unused")
    private RIMSService rimsService;
	
	public void setRimsService(RIMSService rimsService) 
	{
		this.rimsService = rimsService;
	}

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------
	@SuppressWarnings("unused")
    private String rimsDeGroupId;
	
	public void setRimsDeGroupId(String rimsDeGroupId) 
	{
		this.rimsDeGroupId = rimsDeGroupId;
	}
		
	private Map<String,String> rimsDataElements;
	
	public Map<String, String> getRimsDataElements() 
	{
		return rimsDataElements;
	}

	// -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
    	rimsDataElements = new TreeMap<String,String>(rimsDataElements);    	
    	
    	return SUCCESS;
    }
}
