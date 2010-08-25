package org.hisp.dhis.web.api.service.mapping;

import org.hisp.dhis.web.api.model.Activity;

public class ActivityPlanItemMapper
    implements BeanMapper<org.hisp.dhis.activityplan.Activity, Activity>
{

    @Override
    public Activity getModel( org.hisp.dhis.activityplan.Activity activity )
    {
        if ( activity == null )
        {
            return null;
        }

        Activity item = new Activity();

        item.setBeneficiary( new BeneficiaryMapper().getModel( activity.getBeneficiary()) );
        item.setDueDate( activity.getDueDate() );
        item.setTask( new TaskMapper().getModel( activity.getTask()) );
        return item;
    }

}
