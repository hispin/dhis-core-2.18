package org.hisp.dhis.patient.api.service.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.patient.api.model.ActivityPlan;
import org.hisp.dhis.patient.api.model.ActivityPlanItem;
import org.hisp.dhis.patient.api.service.MappingFactory;

public class ActivityPlanMapper implements BeanMapper<Collection<Activity>, ActivityPlan>
{

    @Override
    public ActivityPlan getModel( Collection<Activity> activities, MappingFactory mappingFactory, UriInfo uriInfo )
    {
        ActivityPlan plan = new ActivityPlan();

        if ( activities == null || activities.isEmpty() )
        {
            return plan;
        }

        List<ActivityPlanItem> items = new ArrayList<ActivityPlanItem>();
        plan.setActivitiesList( items );

        for ( Activity activity : activities )
        {
            items.add(mappingFactory.getBeanMapper( Activity.class, ActivityPlanItem.class ).getModel( activity, mappingFactory, uriInfo ));
        }
        
        return plan;
    }


}
