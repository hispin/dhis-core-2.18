package org.hisp.dhis.web.api.resources;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.web.api.model.Link;
import org.hisp.dhis.web.api.model.MobileWrapper;
import org.hisp.dhis.web.api.model.OrgUnit;
import org.hisp.dhis.web.api.service.IActivityPlanService;
import org.hisp.dhis.web.api.service.IDataSetService;
import org.hisp.dhis.web.api.service.IProgramService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/mobile")
public class MobileResouce {
	@Autowired
    private IProgramService programService;
    
    @Autowired
    private IActivityPlanService activityPlanService;
    
    @Autowired
	private IDataSetService idataSetService;

    @Autowired
    private CurrentUserService currentUserService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces( MediaType.APPLICATION_XML )
    public Response getOrgUnitForUser()
    {
        User user = currentUserService.getCurrentUser();

        Collection<OrganisationUnit> units = user.getOrganisationUnits();

        if ( units.isEmpty() )
        {
            return Response.status( Status.CONFLICT ).entity( "User is not registered to a unit." ).build();
        }
        else if ( units.size() > 1 )
        {
            StringBuilder sb = new StringBuilder("User is registered to more than one unit: ");
            
            int i = units.size();
            for ( OrganisationUnit unit : units )
            {
                sb.append( unit.getName() );
                if (i-- > 1)
                    sb.append( ", " );
            }
            
            return Response.status( Status.CONFLICT ).entity( sb.toString() ).build();
        }
        
        OrganisationUnit unit = units.iterator().next();
        return Response.ok(getOrgUnit(unit)).build();
    }

    
    
    @GET   
    @Path("all")
    @Produces( "application/vnd.org.dhis2.mobileresource+serialized" )
    public MobileWrapper getAllDataForUser(@HeaderParam("accept-language") String locale)
    {
        MobileWrapper mobileWrapper = new MobileWrapper();
        mobileWrapper.setActivityPlan(activityPlanService.getCurrentActivityPlan( locale ));
             
        mobileWrapper.setPrograms( programService.getAllProgramsForLocale( locale ) );
        
        
        mobileWrapper.setDatasets(idataSetService.getAllMobileDataSetsForLocale(locale));
        
//    	ActivityWrapper activityWrapper = new ActivityWrapper(); 
//        activityWrapper.setActivityPlan( activityPlanService.getCurrentActivityPlan( locale ) );
//        activityWrapper.setPrograms( programService.getAllProgramsForLocale( locale ) );
        return mobileWrapper;
    }
    
    private OrgUnit getOrgUnit(OrganisationUnit unit)
    {
        OrgUnit m = new OrgUnit();

        m.setId( unit.getId() );
        m.setName( unit.getShortName() );
        m.setProgramFormsLink( new Link( uriInfo.getRequestUriBuilder().path( "programforms" ).build().toString() ) );
        m.setActivitiesLink( new Link( uriInfo.getRequestUriBuilder().path( "activityplan/current" ).build().toString() ) );

        return m;
    }

}
