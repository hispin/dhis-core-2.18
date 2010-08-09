package org.hisp.dhis.patient.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.patient.api.model.ActivityPlan;
import org.hisp.dhis.patient.api.model.ActivityPlanItem;

public class ActivityPlanMapper extends AbstractEntitiyModelBeanMapper<ActivitiesWrapper, ActivityPlan>
{

    @Override
    public ActivityPlan getModel( ActivitiesWrapper entity, MappingManager mappingManager )
    {
        ActivityPlan plan = new ActivityPlan();

        Collection<Activity> activities = entity.getActivities();

        if ( activities == null || activities.isEmpty() )
        {
            return plan;
        }

        List<ActivityPlanItem> items = new ArrayList<ActivityPlanItem>();
        plan.setActivitiesList( items );

        for ( Activity activity : activities )
        {
            Object item = mappingManager.map( activity );
            
            items.add( (ActivityPlanItem) item );
        }
        
        return plan;
    }


}
