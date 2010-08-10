package org.hisp.dhis.patient.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.activityplan.ActivityPlanService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.api.model.ActivityPlan;
import org.hisp.dhis.patient.api.model.ActivityPlanItem;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivityPlanModelService
{
    @Autowired
    private MappingFactory mappingFactory;

    @Autowired
    private ActivityPlanService activityPlanService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Context
    private UriInfo uriInfo;

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

        ActivityPlan plan = mappingFactory.getBeanMapper( Collection.class, ActivityPlan.class ).getModel( activities,
            mappingFactory, uriInfo );

        return plan;
    }

    public ActivityPlan getAllActivities( OrganisationUnit unit )
    {
        final Collection<Activity> activities = activityPlanService.getActivitiesByProvider( unit );

        return mappingFactory.getBeanMapper( activities, ActivityPlan.class ).getModel( activities, mappingFactory,
            uriInfo );
    }

    public void setActivityPlanService( ActivityPlanService activityPlanService )
    {
        this.activityPlanService = activityPlanService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setMappingManager( MappingFactory mappingFactory )
    {
        this.mappingFactory = mappingFactory;
    }

}
