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

public class RIMSExportFormAction extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private RIMSService rimsService;

        // ---------------------------------------------------------------------
        // Out parameters
        // ---------------------------------------------------------------------
	Map<String,String> rimsDEGroups;
	
	List<RIMS_PHC> rimsDistrictsOfPHCs;
	
    List<RIMS_PHC> rimsPHCs;
    private Map<String, ConnectionDetails> connections;
            
	// --------------------------------------------------------------------------
    // Action Implementation
    // --------------------------------------------------------------------------
    public String execute()
    throws Exception
    {
        connections = new DataBaseConnection().getDBDetailsFromXML( "rims" );
        String firstConnection = connections.keySet().iterator().next();
        
    	rimsDEGroups = new HashMap<String,String>(rimsService.getAllRIMSDEGroups());

        // TODO Create some AJAX to run this when user picks another database.
        rimsDistrictsOfPHCs = new ArrayList<RIMS_PHC>(rimsService.getAllDistrictsofAllPHCs( firstConnection ));
        
    	if(rimsDistrictsOfPHCs != null) 
    	{
    		String firstDistrict = (String) rimsDistrictsOfPHCs.iterator().next().getDistrict_code();
    		rimsPHCs = new ArrayList<RIMS_PHC>(rimsService.getPHCsByDistrict(firstDistrict, firstConnection));    		    		
    	}
    	
    	return SUCCESS;
    }


    public void setRimsService(RIMSService rimsService) 
    {
    	this.rimsService = rimsService;
    }


    public Map<String, String> getRimsDEGroups() 
    {
    	return rimsDEGroups;
    }


    public List<RIMS_PHC> getRimsDistrictsOfPHCs() 
    {
        	return rimsDistrictsOfPHCs;
        }


    public List<RIMS_PHC> getRimsPHCs() 
    {
    	return rimsPHCs;
    }


    public Map<String, ConnectionDetails> getConnections()
    {
        return connections;
    }
}
