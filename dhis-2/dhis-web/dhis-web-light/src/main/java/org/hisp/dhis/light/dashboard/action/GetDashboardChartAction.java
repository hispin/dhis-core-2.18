package org.hisp.dhis.light.dashboard.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.Action;

/**
 * 
 * @author Paul Mark Castillo
 * 
 */
public class GetDashboardChartAction
    implements Action
{
    /**
     * 
     */
    private static final Log log = LogFactory.getLog( GetDashboardChartAction.class );

    /**
     * 
     */
    public GetDashboardChartAction()
    {
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    /**
     * 
     */
    private int id;
    
    /**
     * 
     * @return
     */
    public int getId() {
		return id;
	}

    /**
     * 
     * @param id
     */
	public void setId(int id) {
    	this.id = id;
	}
	
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------




	@Override
    public String execute()
        throws Exception
    {
        return SUCCESS;
    }
}
