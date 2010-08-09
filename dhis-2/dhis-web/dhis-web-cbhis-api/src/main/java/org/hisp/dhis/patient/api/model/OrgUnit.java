package org.hisp.dhis.patient.api.model;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.api.resources.ActivityPlanResource;
import org.hisp.dhis.patient.api.resources.ProgramFormsResource;

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
    
    public static OrgUnit create(OrganisationUnit unit, UriInfo uriInfo) {
        OrgUnit m = new OrgUnit();
        m.id = unit.getId();
        m.name = unit.getShortName();
        m.programFormsLink = Link.create(uriInfo, ProgramFormsResource.class, m.id);
        m.activitiesLink = Link.create(uriInfo, ActivityPlanResource.class, m.id);

        
        return m;
    }
}
