package org.hisp.dhis.integration.rims.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.integration.rims.api.RIMSService;
import org.hisp.dhis.integration.rims.api.tables.RIMS_PHC;
import org.hisp.dhis.integration.util.DataBaseConnection;
import org.hisp.dhis.integration.util.DataBaseConnection.ConnectionDetails;

import com.opensymphony.xwork.ActionSupport;

public class RIMSImportFormAction extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private RIMSService rimsService;
	
	public void setRimsService(RIMSService rimsService) 
	{
		this.rimsService = rimsService;
	}
	
    // --------------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------------

	Map<String,String> rimsDEGroups;
	
	public Map<String, String> getRimsDEGroups() 
	{
		return rimsDEGroups;
	}

	List<RIMS_PHC> rimsDistrictsOfPHCs;
	
    public List<RIMS_PHC> getRimsDistrictsOfPHCs() 
    {
		return rimsDistrictsOfPHCs;
	}

    List<RIMS_PHC> rimsPHCs;
    private String dbName;
    private Map<String, ConnectionDetails> connections;
            
	public List<RIMS_PHC> getRimsPHCs() 
	{
		return rimsPHCs;
	}

	
	// --------------------------------------------------------------------------
    // Action Implementation
    // --------------------------------------------------------------------------
    public String execute()
    throws Exception
    {
        connections = new DataBaseConnection().getDBDetailsFromXML( "rims" );
        String firstConnection = connections.keySet().iterator().next();
        
        rimsDEGroups = new HashMap<String,String>(rimsService.getAllRIMSDEGroups());
        
    	rimsDistrictsOfPHCs = new ArrayList<RIMS_PHC>(
            rimsService.getAllDistrictsofAllPHCs( firstConnection ) );
    	    	
    	if(rimsDistrictsOfPHCs != null) 
    	{
    		String firstDistrict = (String) rimsDistrictsOfPHCs.iterator().next().getDistrict_code();
    		rimsPHCs = new ArrayList<RIMS_PHC>(
                    rimsService.getPHCsByDistrict( firstDistrict, firstConnection ) );    		    		
    	}
    	
    	return SUCCESS;
    }


    public String getDbName()
    {
        return dbName;
    }


    public void setDbName( String connectionString )
    {
        this.dbName = connectionString;
    }
    
    public Map<String, ConnectionDetails> getConnections()
    {
        return connections;
    }
}
