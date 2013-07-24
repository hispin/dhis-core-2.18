package org.hisp.dhis.light.dashboard.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ProvideContentAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DashboardService dashboardService;

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private ChartService chartService;

    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<ReportTable> reportTables;

    public List<ReportTable> getReportTables()
    {
        return reportTables;
    }

    private List<Document> documents;

    public List<Document> getDocuments()
    {
        return documents;
    }

    private List<String> charts = new ArrayList<String>();

    public List<String> getCharts()
    {
        return charts;
    }
    
    private List<Chart> chartsForAll;

    public List<Chart> getChartsForAll()
    {
        return chartsForAll;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        chartsForAll = new ArrayList<Chart>( chartService.getAllCharts() );
        
        User user = currentUserService.getCurrentUser();
        
        //TODO implement new dashboard solution

        return SUCCESS;
    }
}
