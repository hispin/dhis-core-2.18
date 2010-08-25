package org.hisp.dhis.web.api.service.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.Activity;

public class ActivitiesMapper implements BeanMapper<Collection<org.hisp.dhis.activityplan.Activity>, ActivityPlan>
{

    private ActivityPlanItemMapper activityMapper = new ActivityPlanItemMapper();

    @Override
    public ActivityPlan getModel( Collection<org.hisp.dhis.activityplan.Activity> activities )
    {
        ActivityPlan plan = new ActivityPlan();

        if ( activities == null || activities.isEmpty() )
        {
            return plan;
        }

        List<Activity> items = new ArrayList<Activity>();
        plan.setActivitiesList( items );

        for ( org.hisp.dhis.activityplan.Activity activity : activities )
        {
            items.add(activityMapper.getModel( activity));
        }
        
        return plan;
    }


}
