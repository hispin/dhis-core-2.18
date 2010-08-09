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
    private MappingManager mapper;

    @Autowired
    private ActivityPlanService activityPlanService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    public ActivityPlan getActivityPlan( int orgUnitId )
    {
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( orgUnitId );
        final Collection<Activity> activities = activityPlanService.getActivitiesByProvider( unit );

        
        return (ActivityPlan) mapper.map(new ActivitiesWrapper(activities));

    }

    public void setActivityPlanService( ActivityPlanService activityPlanService )
    {
        this.activityPlanService = activityPlanService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setMappingManager( MappingManager mappingManager )
    {
        mapper = mappingManager;
    }

    
    
}
