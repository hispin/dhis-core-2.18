package org.hisp.dhis.patient.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.patient.api.model.ActivityPlan;
import org.hisp.dhis.patient.api.service.ActivityPlanModelService;
import org.springframework.beans.factory.annotation.Autowired;

@Path( "v0.1/orgunits/{id}/activityplan" )
public class ActivityPlanResource
{

    private static final Log LOG = LogFactory.getLog( OrgUnitResource.class );

    @Autowired
    private ActivityPlanModelService service;

    @GET
    @Produces( { MediaType.APPLICATION_XML, DhisMediaType.ACTIVITYPLAN_SERIALIZED } )
    public ActivityPlan getOrgUnitActivityPlan( @PathParam( "id" ) int id )
    {
        return service.getActivityPlan( id );
    }

}
