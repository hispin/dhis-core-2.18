package org.hisp.dhis.pbf.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

public class GetDataElementforTariffAction implements Action
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private DataElementService dataElementService;
	
	public void setDataElementService(DataElementService dataElementService) {
		this.dataElementService = dataElementService;
	}
	
	// -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
	
	private List<String> dataElementList = new ArrayList<String>();
	
    public List<String> getDataElementList() {
		return dataElementList;
	}

	public void setDataElementList(List<String> dataElementList) {
		this.dataElementList = dataElementList;
	}

	// -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute()
    {
    	List<DataElement> dataElements = new ArrayList<DataElement>(dataElementService.getAllDataElements());
    	for(DataElement de : dataElements)
    	{
    		if(!(dataElementList.contains("\""+de.getName()+"\"")))
    		{
    			dataElementList.add("\""+de.getName()+"\"");
    		}
    	}
        return SUCCESS;
    }
}