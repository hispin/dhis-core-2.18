package org.hisp.dhis.web.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.hisp.dhis.web.api.model.MobileWrapper;
import org.hisp.dhis.web.api.service.IActivityPlanService;
import org.hisp.dhis.web.api.service.IDataSetService;
import org.hisp.dhis.web.api.service.IProgramService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Tran Ng Minh Luan
 *
 */
@Path("/mobile")
public class MobileResouce {
	@Autowired
    private IProgramService programService;
    
    @Autowired
    private IActivityPlanService activityPlanService;
    
    @Autowired
	private IDataSetService idataSetService;

    
    @GET   
    @Produces( "application/vnd.org.dhis2.mobileresource+serialized" )
    public MobileWrapper getMobileResource(@HeaderParam("accept-language") String locale)
    {
        MobileWrapper mobileWrapper = new MobileWrapper();
        mobileWrapper.setActivityPlan(activityPlanService.getCurrentActivityPlan( locale ));
             
        mobileWrapper.setPrograms( programService.getAllProgramsForLocale( locale ) );
        
        
        mobileWrapper.setDatasets(idataSetService.getAllMobileDataSetsForLocale(locale));
        
//    	ActivityWrapper activityWrapper = new ActivityWrapper(); 
//        activityWrapper.setActivityPlan( activityPlanService.getCurrentActivityPlan( locale ) );
//        activityWrapper.setPrograms( programService.getAllProgramsForLocale( locale ) );
        return mobileWrapper;
    }
}
