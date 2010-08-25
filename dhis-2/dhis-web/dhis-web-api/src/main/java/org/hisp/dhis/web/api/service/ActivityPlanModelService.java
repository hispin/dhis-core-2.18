package org.hisp.dhis.web.api.service;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.activityplan.ActivityPlanService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.service.mapping.ActivitiesMapper;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivityPlanModelService
{
    @Autowired
    private ActivityPlanService activityPlanService;

    /**
     * Gets the current activity plan for an org unit.
     * <p>The current activity plan is tentatively defined as all activities within the current month and all uncompleted activities from earlier.
     */
    public ActivityPlan getCurrentActivityPlan( OrganisationUnit unit )
    {
        DateTime dt = new DateTime();
        DateMidnight from = dt.withDayOfMonth( 1 ).toDateMidnight();
        DateMidnight to = from.plusMonths( 1 );

        final Collection<Activity> allActivities = activityPlanService.getActivitiesByProvider( unit );
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

        return plan;
    }

    public ActivityPlan getAllActivities( OrganisationUnit unit )
    {
        final Collection<Activity> activities = activityPlanService.getActivitiesByProvider( unit );

        return new ActivitiesMapper().getModel( activities );
    }

    public void setActivityPlanService( ActivityPlanService activityPlanService )
    {
        this.activityPlanService = activityPlanService;
    }

}
