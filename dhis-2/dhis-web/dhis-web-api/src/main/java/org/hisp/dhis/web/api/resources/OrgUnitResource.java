package org.hisp.dhis.web.api.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.Form;
import org.hisp.dhis.web.api.model.Link;
import org.hisp.dhis.web.api.model.OrgUnit;
import org.hisp.dhis.web.api.service.ActivityPlanModelService;
import org.hisp.dhis.web.api.service.ProgramStageService;
import org.springframework.beans.factory.annotation.Autowired;

@Path( "/orgUnits/{id}" )
public class OrgUnitResource
{

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private ActivityPlanModelService service;

    @Autowired
    private ProgramStageService programStageService;

    @Context
    private UriInfo uriInfo;
    
    @PathParam( "id" )
    private int unitId;
    
    @GET
    public OrgUnit getOrgUnit( )
    {
        OrganisationUnit unit = getUnit();
        
        OrgUnit m = new OrgUnit();

        m.setId( unit.getId() );
        m.setName( unit.getShortName() );
        m.setProgramFormsLink( new Link( uriInfo.getRequestUriBuilder().path( "programforms" ).build().toString() ) );
        m.setActivitiesLink( new Link( uriInfo.getRequestUriBuilder().path( "activityplan/current" ).build().toString() ) );

        return m;
    }

    @GET
    @Path( "activityplan/current" )
    @Produces( MediaType.APPLICATION_XML )
    public ActivityPlan getCurrentActivityPlan()
    {
        return service.getCurrentActivityPlan( getUnit() );
    }

    private OrganisationUnit getUnit( )
    {
        return organisationUnitService.getOrganisationUnit( unitId );
    }


    @GET    
    @Path("programforms")
    @Produces(MediaType.APPLICATION_XML)    
    public List<Form> getAllForms() {
        return programStageService.getAllForms();
    }       

}
