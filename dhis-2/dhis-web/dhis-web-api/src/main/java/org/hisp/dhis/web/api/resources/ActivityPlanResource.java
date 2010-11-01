package org.hisp.dhis.web.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;

import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.ActivityValue;
import org.hisp.dhis.web.api.service.IActivityPlanService;
import org.hisp.dhis.web.api.service.IActivityValueService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/activityplan")
public class ActivityPlanResource {

	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	@Autowired
	private IActivityPlanService activityPlanService;
	
	@Autowired
	private IActivityValueService iactivityValueService;

	
	// -------------------------------------------------------------------------
    // Resources
    // -------------------------------------------------------------------------		
	
	@GET
    @Path( "current" )    
    @Produces( "application/vnd.org.dhis2.activityplan+serialized" ) 
    public ActivityPlan getCurrentActivityPlan(@HeaderParam("accept-language") String locale)
    {
        return activityPlanService.getCurrentActivityPlan( locale );
    }
	
	@POST
	@Path( "values" )
	@Consumes( "application/vnd.org.dhis2.activityvaluelist+serialized" )
	@Produces("application/xml")	
	public String  getValues(ActivityValue activityValue) 
	{		
		return iactivityValueService.saveValues(activityValue);		
	}	
}
