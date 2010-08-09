package org.hisp.dhis.patient.api.service;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.patient.api.model.ActivityPlanItem;
import org.hisp.dhis.patient.api.model.Beneficiary;
import org.hisp.dhis.patient.api.model.Task;

public class ActivityPlanItemMapper
    extends AbstractEntitiyModelBeanMapper<Activity, ActivityPlanItem>
{

    @Override
    public ActivityPlanItem getModel( Activity activity, MappingManager mappingManager )
    {
        if (activity == null) {
            return null;
        }
        
        ActivityPlanItem item = new ActivityPlanItem();
        
        item.setBeneficiary( (Beneficiary) mappingManager.map(activity.getBeneficiary()) );
        item.setDueDate( activity.getDueDate() );
        item.setTask( (Task) mappingManager.map( activity.getTask() ) );
        return item;
    }

}
