package org.hisp.dhis.web.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.hisp.dhis.web.api.model.ActivityWrapper;
import org.hisp.dhis.web.api.service.IActivityPlanService;
import org.hisp.dhis.web.api.service.IActivityValueService;
import org.hisp.dhis.web.api.service.IProgramService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/download")
public class ActivityWrapperResource
{
    @Autowired
    private IProgramService programService;
    
    @Autowired
    private IActivityPlanService activityPlanService;
    
    
    @GET   
    @Produces( "application/vnd.org.dhis2.activitywrapper+serialized" )
    public ActivityWrapper getCurrentActivityPlan(@HeaderParam("accept-language") String locale)
    {
        ActivityWrapper activityWrapper = new ActivityWrapper(); 
        activityWrapper.setActivityPlan( activityPlanService.getCurrentActivityPlan( locale ) );
        activityWrapper.setPrograms( programService.getAllProgramsForLocale( locale ) );
        return activityWrapper;
    }
}
