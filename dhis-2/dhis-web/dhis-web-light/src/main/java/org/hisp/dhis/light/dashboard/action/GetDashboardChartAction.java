package org.hisp.dhis.light.dashboard.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.Action;

public class GetDashboardChartAction
    implements Action
{
    private static final Log log = LogFactory.getLog( GetDashboardChartAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
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
