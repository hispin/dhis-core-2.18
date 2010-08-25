package org.hisp.dhis.patient.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.api.model.ActivityPlan;
import org.hisp.dhis.patient.api.service.ActivityPlanModelService;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivitiesResource
{

    @Autowired
    private ActivityPlanModelService service;

    private OrganisationUnit organisationUnit;

    @GET
    @Path( "plan/current" )
    @Produces( MediaType.APPLICATION_XML )
    public ActivityPlan getCurrentActivityPlan()
    {
        return service.getCurrentActivityPlan( organisationUnit );
    }

    @GET
    @Path( "all" )
    @Produces( MediaType.APPLICATION_XML )
    public ActivityPlan getOrgUnitActivityPlan()
    {
        return service.getAllActivities( organisationUnit );
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

}
