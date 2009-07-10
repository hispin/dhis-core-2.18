package org.hisp.dhis.dashboard.maintenance.action;

import org.hisp.dhis.dashboard.util.DashBoardService;
import org.hisp.dhis.dashboard.util.MaintenanceIN;

import com.opensymphony.xwork.Action;

public class InConfigurationsFormAction
    implements Action
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

    public String getMysqlPath()
    {
        return mysqlPath;
    }

    private String rootDataPath;

    public String getRootDataPath()
    {
        return rootDataPath;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        mysqlPath = dashBoardService.getMYSqlPath();

        if ( mysqlPath == null )
            mysqlPath = dashBoardService.setMYSqlDefaultPath( MaintenanceIN.MYSQL_DEFAULT_PATH );

        rootDataPath = dashBoardService.getRootDataPath();

        return SUCCESS;
    }
}
