package org.hisp.dhis.web.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ActivityPlan
{

    private List<Activity> activitiesList;

    @XmlElement( name = "activity" )
    public List<Activity> getActivitiesList()
    {
        return activitiesList;
    }

    public void setActivitiesList( List<Activity> activitiesList )
    {
        this.activitiesList = activitiesList;
    }

}
