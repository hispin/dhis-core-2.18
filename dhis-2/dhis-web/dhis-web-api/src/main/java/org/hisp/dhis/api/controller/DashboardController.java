package org.hisp.dhis.api.controller;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.dashboard.DashboardSearchResult;
import org.hisp.dhis.dashboard.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping( value = DashboardController.RESOURCE_PATH )
public class DashboardController
{
    public static final String RESOURCE_PATH = "/dashboards";
        
    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private ContextUtils contextUtils;
    
    @RequestMapping( value = "/search/{query}", method = RequestMethod.GET, produces = { "application/json" } )
    public String search( @PathVariable String query, 
        Model model,
        HttpServletResponse response ) throws Exception
    {
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, CacheStrategy.NO_CACHE );
        
        DashboardSearchResult result = dashboardService.search( query );
        
        model.addAttribute( "model", result );
        
        return "dashboardSearchResult";
    }
}
