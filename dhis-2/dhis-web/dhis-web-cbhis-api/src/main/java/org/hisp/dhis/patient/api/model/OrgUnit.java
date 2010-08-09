package org.hisp.dhis.patient.api.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="orgUnit")
public class OrgUnit {

    @XmlAttribute
    private int id;
    @XmlAttribute
    private String name;

    @XmlElement(name="allProgramForms")
    private Link programFormsLink;
    
    @XmlElement(name="currentActivities")
    private Link activitiesLink;

    public void setId( int id )
    {
        this.id = id;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setProgramFormsLink( Link programFormsLink )
    {
        this.programFormsLink = programFormsLink;
    }

    public void setActivitiesLink( Link activitiesLink )
    {
        this.activitiesLink = activitiesLink;
    }
 
    
}
