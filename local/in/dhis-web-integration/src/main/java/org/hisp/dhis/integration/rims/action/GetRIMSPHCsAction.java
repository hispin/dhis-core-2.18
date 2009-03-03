package org.hisp.dhis.integration.rims.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hisp.dhis.integration.rims.api.RIMSService;
import org.hisp.dhis.integration.rims.api.tables.RIMS_PHC;

import com.opensymphony.xwork.ActionSupport;

public class GetRIMSPHCsAction extends ActionSupport
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	private RIMSService rimsService;
	
	public void setRimsService(RIMSService rimsService) 
	{
		this.rimsService = rimsService;
	}

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

	private String rimsDistrictId;

	public void setRimsDistrictId(String rimsDistrictId) 
	{
		this.rimsDistrictId = rimsDistrictId;
	}

	private List<RIMS_PHC> rimsPHCs;
    private String connection;
		
	public List<RIMS_PHC> getRimsPHCs() 
	{
		return rimsPHCs;
	}

	// -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
    	rimsPHCs = new ArrayList<RIMS_PHC>(rimsService.getPHCsByDistrict(rimsDistrictId, connection));
        Collections.sort( rimsPHCs, new Comparator<RIMS_PHC>() 
            {
                public int compare( RIMS_PHC arg0, RIMS_PHC arg1 )
                {
                    return arg0.getPhc_name().compareTo( arg1.getPhc_name() );
                }   
            }
            );
    	return SUCCESS;
    }

    public void setConnection( String connection )
    {
        this.connection = connection;
    }
}
