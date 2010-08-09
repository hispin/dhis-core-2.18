package org.hisp.dhis.patient.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.api.model.ActivityPlan;
import org.hisp.dhis.patient.api.service.ActivityPlanModelService;
import org.springframework.beans.factory.annotation.Autowired;


public class ActivitiesResource
{

    private static final Log LOG = LogFactory.getLog( OrgUnitResource.class );

    @Autowired
    private ActivityPlanModelService service;

    private OrganisationUnit organisationUnit;

    @GET
    @Path("plan/current")
    @Produces( { MediaType.APPLICATION_XML, DhisMediaType.ACTIVITYPLAN_SERIALIZED } )
    public ActivityPlan getCurrentActivityPlan()
    {
        return service.getCurrentActivityPlan( organisationUnit );
    }

    @GET
    @Path("all")
    @Produces( { MediaType.APPLICATION_XML, DhisMediaType.ACTIVITYPLAN_SERIALIZED } )
    public ActivityPlan getOrgUnitActivityPlan()
    {
        return service.getAllActivities( organisationUnit );
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

}
