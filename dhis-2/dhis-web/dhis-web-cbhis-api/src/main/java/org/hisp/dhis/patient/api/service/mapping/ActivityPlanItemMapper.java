package org.hisp.dhis.patient.api.service.mapping;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.api.model.ActivityPlanItem;
import org.hisp.dhis.patient.api.model.Beneficiary;
import org.hisp.dhis.patient.api.model.Task;
import org.hisp.dhis.patient.api.service.MappingFactory;
import org.hisp.dhis.program.ProgramStageInstance;

public class ActivityPlanItemMapper
    implements BeanMapper<Activity, ActivityPlanItem>
{

    @Override
    public ActivityPlanItem getModel( Activity activity, MappingFactory mappingFactory )
    {
        if ( activity == null )
        {
            return null;
        }

        ActivityPlanItem item = new ActivityPlanItem();

        item.setBeneficiary( mappingFactory.getBeanMapper( Patient.class, Beneficiary.class ).getModel( activity.getBeneficiary(), mappingFactory ) );
        item.setDueDate( activity.getDueDate() );
        item.setTask( mappingFactory.getBeanMapper( ProgramStageInstance.class, Task.class ).getModel( activity.getTask(), mappingFactory ) );
        return item;
    }

}
