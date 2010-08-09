package org.hisp.dhis.patient.api.service;

import java.util.Collection;

import org.hisp.dhis.activityplan.Activity;
import org.hisp.dhis.activityplan.ActivityPlanService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.api.model.ActivityPlan;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivityPlanModelService
{
    @Autowired
    private MappingFactory mappingFactory;

    @Autowired
    private ActivityPlanService activityPlanService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    public ActivityPlan getActivityPlan( int orgUnitId )
    {
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( orgUnitId );
        final Collection<Activity> activities = activityPlanService.getActivitiesByProvider( unit );
        
        return mappingFactory.getBeanMapper(Collection.class, ActivityPlan.class).getModel( activities, mappingFactory );
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
