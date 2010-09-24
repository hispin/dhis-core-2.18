/**
 * 
 */
package org.hisp.dhis.web.api.service;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.service.mapping.ActivitiesMapper;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author abyotag_adm
 *
 */
public class DefaultActivityPlanService implements IActivityPlanService {

	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	@Autowired
	private org.hisp.dhis.activityplan.ActivityPlanService activityPlanService;
	
	@Autowired
    private CurrentUserService currentUserService;
	
	// -------------------------------------------------------------------------
    // MobileDataSetService
    // -------------------------------------------------------------------------	
	
	public ActivityPlan getCurrentActivityPlan(String localeString) 
	{
		Collection<OrganisationUnit> units = currentUserService.getCurrentUser().getOrganisationUnits();
        OrganisationUnit unit = null;
        
        if( units.size() > 0 )
        {
        	unit = units.iterator().next();       	
        }
        else
        {
        	return null;
        }		
		
		DateTime dt = new DateTime();
        DateMidnight from = dt.withDayOfMonth( 1 ).toDateMidnight();
        DateMidnight to = from.plusMonths( 1 );

        Collection<Activity> allActivities = activityPlanService.getActivitiesByProvider( unit );
        Collection<Activity> activities = new ArrayList<Activity>();
        for ( Activity activity : allActivities )
        {
            long dueTime = activity.getDueDate().getTime();
            if ( to.isBefore( dueTime ) )
            {
                continue;
            }
            
            if (from.isBefore( dueTime ) || !activity.getTask().isCompleted()) {
                activities.add( activity );
            }
        }

        ActivityPlan plan = new ActivitiesMapper().getModel( activities );
        
        System.out.println("The size of the plan is:  " + plan.getActivitiesList().size());

        return plan;
	}
}
