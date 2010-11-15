package org.hisp.dhis.web.api.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.Form;
import org.hisp.dhis.web.api.service.ActivityPlanModelService;
import org.hisp.dhis.web.api.service.ProgramStageService;
import org.springframework.beans.factory.annotation.Autowired;

@Path( "/orgUnits/{id}" )
public class OrgUnitResource
{
    private OrganisationUnitService organisationUnitService;

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ActivityPlanModelService service;

    public ActivityPlanModelService getService()
    {
        return service;
    }

    public void setService( ActivityPlanModelService service )
    {
        this.service = service;
    }

    private ProgramStageService programStageService;

    public ProgramStageService getProgramStageService()
    {
        return programStageService;
    }

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    @PathParam( "id" )
    private int unitId;

    @GET
    @Path( "activityplan/current" )
    @Produces( MediaType.APPLICATION_XML )
    public ActivityPlan getCurrentActivityPlan()
    {
        return service.getCurrentActivityPlan( organisationUnitService.getOrganisationUnit( unitId ) );
    }

    @GET
    @Path( "programforms" )
    @Produces( MediaType.APPLICATION_XML )
    public List<Form> getAllForms()
    {
        return programStageService.getAllForms();
    }

}
