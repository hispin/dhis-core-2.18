package org.hisp.dhis.patient.api.service;

import java.util.Collection;

import org.hisp.dhis.activityplan.Activity;

public class ActivitiesWrapper
{

    Collection<Activity> activities;

    public ActivitiesWrapper( Collection<Activity> activities )
    {
        this.activities = activities;
    }

    public Collection<Activity> getActivities()
    {
        return activities;
    }

    public void setActivities( Collection<Activity> activities )
    {
        this.activities = activities;
    }
    
}
