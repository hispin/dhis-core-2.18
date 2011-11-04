package org.hisp.dhis.mobile.web.resources;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.api.mobile.ActivityReportingService;
import org.hisp.dhis.api.mobile.FacilityReportingService;
import org.hisp.dhis.api.mobile.IProgramService;
import org.hisp.dhis.api.mobile.NotAllowedException;
import org.hisp.dhis.api.mobile.model.ActivityPlan;
import org.hisp.dhis.api.mobile.model.ActivityValue;
import org.hisp.dhis.api.mobile.model.DataSetList;
import org.hisp.dhis.api.mobile.model.DataSetValue;
import org.hisp.dhis.api.mobile.model.MobileModel;
import org.hisp.dhis.api.mobile.model.MobileOrgUnitLinks;
import org.hisp.dhis.api.mobile.model.ModelList;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.mobile.service.ActivityReportingServiceImpl;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.beans.factory.annotation.Required;

@Produces( { MobileMediaTypes.MOBILE_SERIALIZED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
@Consumes( { MobileMediaTypes.MOBILE_SERIALIZED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
@Path( "/mobile/orgUnits/{id}" )
public class OrgUnitResource
{
    private OrganisationUnitService organisationUnitService;

    private static Log log = LogFactory.getLog( ActivityReportingServiceImpl.class );

    private static final boolean DEBUG = log.isDebugEnabled();

    private IProgramService programService;

    private ActivityReportingService activityReportingService;

    private FacilityReportingService facilityReportingService;

    private I18nService i18nService;

    @PathParam( "id" )
    private String id;

    @HeaderParam( "accept-language" )
    private String locale;

    @Context
    UriInfo uriInfo;

    private OrganisationUnit getUnit()
    {
        return organisationUnitService.getOrganisationUnit( Integer.parseInt( id ) );
    }

    /**
     * Get activity plan, program forms and facility forms wrapped in a
     * {@link MobileModel}
     * 
     * @param locale - localize for the given locale
     */
    @GET
    @Path( "all" )
    public MobileModel getAllDataForOrgUnit()
    {
        MobileModel mobileModel = new MobileModel();

        if ( DEBUG )
            log.debug( "Getting all resources for org unit " + getUnit().getName() );

        mobileModel.setActivityPlan( activityReportingService.getCurrentActivityPlan( getUnit(), locale ) );
        mobileModel.setPrograms( programService.getPrograms( getUnit(), locale ) );

        mobileModel.setDatasets( facilityReportingService.getMobileDataSetsForUnit( getUnit(), locale ) );
        mobileModel.setServerCurrentDate( new Date() );
        mobileModel.setLocales( getLocalStrings( i18nService.getAvailableLocales() ) );
        return mobileModel;
    }

    @GET
    @Path( "facility" )
    public MobileModel getFacilityModel()
    {
        MobileModel mobileModel = new MobileModel();

        if ( DEBUG )
            log.debug( "Getting facility reporting resources for org unit " + getUnit().getName() );

        mobileModel.setDatasets( facilityReportingService.getMobileDataSetsForUnit( getUnit(), locale ) );
        mobileModel.setServerCurrentDate( new Date() );
        mobileModel.setLocales( getLocalStrings( i18nService.getAvailableLocales() ) );
        
        return mobileModel;
    }

    @GET
    @Path( "tracking" )
    public MobileModel getTrackingModel()
    {
        MobileModel mobileModel = new MobileModel();

        if ( DEBUG )
            log.debug( "Getting facility reporting resources for org unit " + getUnit().getName() );

        mobileModel.setActivityPlan( activityReportingService.getCurrentActivityPlan( getUnit(), locale ) );
        mobileModel.setPrograms( programService.getPrograms( getUnit(), locale ) );

        mobileModel.setServerCurrentDate( new Date() );
        mobileModel.setLocales( getLocalStrings( i18nService.getAvailableLocales() ) );

        return mobileModel;
    }

    private Collection<String> getLocalStrings( Collection<Locale> locales )
    {
        if ( locales == null || locales.isEmpty() )
        {
            return null;
        }
        Collection<String> localeStrings = new ArrayList<String>();

        for ( Locale locale : locales )
        {
            localeStrings.add( locale.getLanguage() + "-" + locale.getCountry() );
        }
        return localeStrings;
    }

    @POST
    @Path( "updateDataSets" )
    public DataSetList checkUpdatedDataSet( DataSetList dataSetList, @HeaderParam( "accept-language" )
    String locale )
    {
        return facilityReportingService.getUpdatedDataSet( dataSetList, getUnit(), locale );
    }

    @GET
    @Path( "changeLanguageDataSet" )
    public DataSetList changeLanguageDataSet( @HeaderParam( "accept-language" )
    String locale )
    {
        return facilityReportingService.getDataSetsForLocale( getUnit(), locale );
    }

    /**
     * Save a facility report for unit
     * 
     * @param dataSetValue - the report to save
     * @throws NotAllowedException if the {@link DataSetValue} is invalid
     */
    @POST
    @Path( "dataSets" )
    public void saveDataSetValues( DataSetValue dataSetValue )
        throws NotAllowedException
    {
        facilityReportingService.saveDataSetValues( getUnit(), dataSetValue );
    }

    /**
     * Save activity report for unit
     * 
     * @param activityValue - the report to save
     * @throws NotAllowedException if the {@link ActivityValue activity value}
     *         is invalid
     */
    @POST
    @Path( "activities" )
    public void saveActivityReport( ActivityValue activityValue )
        throws NotAllowedException
    {
        activityReportingService.saveActivityReport( getUnit(), activityValue );
    }

    @POST
    @Path( "activitiyplan" )
    public MobileModel updatePrograms( @HeaderParam( "accept-language" )
    String locale, ModelList programsFromClient )
    {
        MobileModel model = new MobileModel();
        model.setPrograms( programService.updateProgram( programsFromClient, locale, getUnit() ) );
        model.setActivityPlan( activityReportingService.getCurrentActivityPlan( getUnit(), locale ) );
        model.setServerCurrentDate( new Date() );
        return model;
    }

    @GET
    @Path( "search" )
    public ActivityPlan search( @HeaderParam( "identifier" )
    String identifier )
        throws NotAllowedException
    {
        return activityReportingService.getActivitiesByIdentifier( identifier );
    }

    public static MobileOrgUnitLinks getOrgUnit( OrganisationUnit unit, UriInfo uriInfo )
    {
        MobileOrgUnitLinks orgUnit = new MobileOrgUnitLinks();

        orgUnit.setId( unit.getId() );
        orgUnit.setName( unit.getShortName() );

        orgUnit.setDownloadAllUrl( getOrgUnitUrlBuilder( uriInfo ).path( "all" ).build( unit.getId() ).toString() );
        orgUnit.setUpdateActivityPlanUrl( getOrgUnitUrlBuilder( uriInfo ).path( "activitiyplan" ).build( unit.getId() )
            .toString() );
        orgUnit.setUploadFacilityReportUrl( getOrgUnitUrlBuilder( uriInfo ).path( "dataSets" ).build( unit.getId() )
            .toString() );
        orgUnit.setUploadActivityReportUrl( getOrgUnitUrlBuilder( uriInfo ).path( "activities" ).build( unit.getId() )
            .toString() );
        orgUnit.setUpdateDataSetUrl( getOrgUnitUrlBuilder( uriInfo ).path( "updateDataSets" ).build( unit.getId() )
            .toString() );
        orgUnit.setChangeUpdateDataSetLangUrl( getOrgUnitUrlBuilder( uriInfo ).path( "changeLanguageDataSet" )
            .build( unit.getId() ).toString() );
        orgUnit.setSearchUrl( getOrgUnitUrlBuilder( uriInfo ).path( "search" ).build( unit.getId() ).toString() );
        return orgUnit;
    }

    private static UriBuilder getOrgUnitUrlBuilder( UriInfo uriInfo )
    {
        return uriInfo.getBaseUriBuilder().path( "/mobile/orgUnits/{id}" );
    }

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

    @Required
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    @Required
    public void setI18nService( I18nService i18nService )
    {
        this.i18nService = i18nService;
    }

}
