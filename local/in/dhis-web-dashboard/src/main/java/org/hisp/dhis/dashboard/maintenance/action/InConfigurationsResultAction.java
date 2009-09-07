package org.hisp.dhis.dashboard.maintenance.action;

import org.hisp.dhis.dashboard.util.DashBoardService;

import com.opensymphony.xwork2.Action;

public class InConfigurationsResultAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DashBoardService dashBoardService;

    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }

    // -------------------------------------------------------------------------
    // Input and Output Parameters
    // -------------------------------------------------------------------------

    private String mysqlPath;
    
    public void setMysqlPath( String mysqlPath )
    {
        this.mysqlPath = mysqlPath;
    }
    
    private String rootDataPath;
    
    public void setRootDataPath( String rootDataPath )
    {
        this.rootDataPath = rootDataPath;
    }
        
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {

        dashBoardService.setUserdefinedConfigurations( mysqlPath, rootDataPath );
        
        return SUCCESS;
    }

}
