package org.hisp.dhis.patient.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ActivityPlan
{

    private List<ActivityPlanItem> activitiesList;

    @XmlElement( name = "activity" )
    public List<ActivityPlanItem> getActivitiesList()
    {
        return activitiesList;
    }

    public void setActivitiesList( List<ActivityPlanItem> activitiesList )
    {
        this.activitiesList = activitiesList;
    }

}
