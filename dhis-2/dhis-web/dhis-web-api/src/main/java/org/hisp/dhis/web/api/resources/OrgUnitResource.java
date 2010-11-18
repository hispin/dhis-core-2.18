package org.hisp.dhis.web.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.ActivityValue;
import org.hisp.dhis.web.api.model.DataSetValue;
import org.hisp.dhis.web.api.model.MobileModel;
import org.hisp.dhis.web.api.service.ActivityReportingService;
import org.hisp.dhis.web.api.service.FacilityReportingService;
import org.hisp.dhis.web.api.service.IProgramService;
import org.springframework.beans.factory.annotation.Required;

@Produces( DhisMediaType.MOBILE_SERIALIZED )
@Consumes( DhisMediaType.MOBILE_SERIALIZED )
public class OrgUnitResource
{

    private IProgramService programService;

    private ActivityReportingService activityReportingService;

    private FacilityReportingService facilityReportingService;

    // Set by parent resource
    private OrganisationUnit unit;
    
    public void setOrgUnit(OrganisationUnit unit) {
        this.unit = unit;
    }

    @GET
    @Path( "all" )
    public MobileModel getAllDataForUser( @HeaderParam( "accept-language" ) String locale )
    {
        MobileModel mobileModel = new MobileModel();

        mobileModel.setActivityPlan( activityReportingService.getCurrentActivityPlan( unit, locale ) );
        mobileModel.setPrograms( programService.getPrograms( unit, locale ) );
        mobileModel.setDatasets( facilityReportingService.getMobileDataSetsForUnit( unit, locale )  );

        return mobileModel;
    }

    @GET
    @Path( "activitiyplan" )
    public ActivityPlan getCurrentActivityPlan( @HeaderParam( "accept-language" ) String locale )
    {
        return activityReportingService.getCurrentActivityPlan( unit, locale );
    }

    @POST
    @Path( "dataSets" )
    @Produces(MediaType.TEXT_PLAIN)
    public String saveDataSetValues( DataSetValue dataSetValue )
    {
        return facilityReportingService.saveDataSetValues( unit, dataSetValue );
    }

    @POST
    @Path( "activities" )
    @Produces(MediaType.TEXT_PLAIN)
    public String saveActivityReport( ActivityValue activityValue )
    {
        return activityReportingService.saveActivityReport( unit, activityValue );
    }

    // Setters...
    
    @Required
    public void setProgramService( IProgramService programService )
    {
        this.programService = programService;
    }

    @Required
    public void setActivityReportingService( ActivityReportingService activityReportingService )
    {
        this.activityReportingService = activityReportingService;
    }

    @Required
    public void setFacilityReportingService( FacilityReportingService facilityReportingService )
    {
        this.facilityReportingService = facilityReportingService;
    }

}
