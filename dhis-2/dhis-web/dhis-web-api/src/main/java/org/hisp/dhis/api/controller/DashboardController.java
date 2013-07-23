package org.hisp.dhis.api.controller;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.dashboard.Dashboard;
import org.hisp.dhis.dashboard.DashboardSearchResult;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping( value = DashboardController.RESOURCE_PATH )
public class DashboardController
    extends AbstractCrudController<Dashboard>
{
    public static final String RESOURCE_PATH = "/dashboards";
        
    @Autowired
    private DashboardService dashboardService;
    
    @RequestMapping( value = "/q/{query}", method = RequestMethod.GET )
    public String search( @PathVariable String query, 
        Model model,
        HttpServletResponse response ) throws Exception
    {
        DashboardSearchResult result = dashboardService.search( query );
        
        model.addAttribute( "model", result );
        
        return "dashboardSearchResult";
    }
    
    @Override
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        Dashboard dashboard = JacksonUtils.fromJson( input, Dashboard.class );
        
        dashboardService.mergeDashboard( dashboard );
        
        dashboardService.saveDashboard( dashboard );
        
        ContextUtils.createdResponse( response, "Dashboard created", RESOURCE_PATH + "/" + dashboard.getUid() );
    }
}
