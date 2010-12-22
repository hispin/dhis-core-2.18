package org.hisp.dhis.web.api.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.web.api.model.ActivityPlan;
import org.hisp.dhis.web.api.model.ActivityValue;
import org.hisp.dhis.web.api.model.DataSet;
import org.hisp.dhis.web.api.model.DataSetList;
import org.hisp.dhis.web.api.model.DataSetValue;
import org.hisp.dhis.web.api.model.MobileModel;
import org.hisp.dhis.web.api.service.ActivityReportingService;
import org.hisp.dhis.web.api.service.ActivityReportingServiceImpl;
import org.hisp.dhis.web.api.service.FacilityReportingService;
import org.hisp.dhis.web.api.service.IProgramService;
import org.hisp.dhis.web.api.service.NotAllowedException;
import org.springframework.beans.factory.annotation.Required;

@Produces( { DhisMediaType.MOBILE_SERIALIZED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
@Consumes( { DhisMediaType.MOBILE_SERIALIZED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
public class OrgUnitResource
{

    private static Log log = LogFactory.getLog( ActivityReportingServiceImpl.class );

    private static final boolean DEBUG = log.isDebugEnabled();

    private IProgramService programService;

    private ActivityReportingService activityReportingService;

    private FacilityReportingService facilityReportingService;

    // Set by parent resource
    private OrganisationUnit unit;

    public void setOrgUnit( OrganisationUnit unit )
    {
        this.unit = unit;
    }

    /**
     * Get activity plan, program forms and facility forms wrapped in a
     * {@link MobileModel}
     * 
     * @param locale - localize for the given locale
     */
    @GET
    @Path( "all" )
    public MobileModel getAllDataForOrgUnit( @HeaderParam( "accept-language" ) String locale )
    {
        MobileModel mobileModel = new MobileModel();

        if ( DEBUG )
            log.debug( "Getting all resources for org unit " + unit.getName() );

        mobileModel.setActivityPlan( activityReportingService.getCurrentActivityPlan( unit, locale ) );
        mobileModel.setPrograms( programService.getPrograms( unit, locale ) );
        mobileModel.setDatasets( facilityReportingService.getMobileDataSetsForUnit( unit, locale ) );

        return mobileModel;
    }

    @POST
    @Path( "updateDataSets" )
    public DataSetList checkUpdatedDataSet( DataSetList dataSetList, @HeaderParam( "accept-language" ) String locale )
    {
        if ( DEBUG )
            log.debug( "Checking updated datasets for org unit " + unit.getName() );
        DataSetList updatedDataSetList = new DataSetList();
        List<DataSet> dataSets = facilityReportingService.getMobileDataSetsForUnit( unit, locale );
        List<DataSet> currentDataSets = dataSetList.getCurrentDataSets();
//        List<DataSet> copyCurrentDataSets = new ArrayList<DataSet>(currentDataSets.size());
//        Collections.copy( copyCurrentDataSets, currentDataSets );
        // check added dataset
        for ( DataSet dataSet : dataSets )
        {
            if ( !currentDataSets.contains( dataSet ) )
            {
                if(updatedDataSetList.getAddedDataSets() == null)
                    updatedDataSetList.setAddedDataSets( new ArrayList<DataSet>() );
                updatedDataSetList.getAddedDataSets().add( dataSet );
                currentDataSets.add( dataSet );
            }
        }
        
        // check deleted dataset
        for ( DataSet dataSet : currentDataSets )
        {
            if ( !dataSets.contains( dataSet ) )
            {
                if(updatedDataSetList.getDeletedDataSets() == null)
                    updatedDataSetList.setDeletedDataSets( new ArrayList<DataSet>() );
                updatedDataSetList.getDeletedDataSets().add(  new DataSet( dataSet )  );
            }
        }
        if(updatedDataSetList.getDeletedDataSets() != null){
            for (DataSet dataSet : updatedDataSetList.getDeletedDataSets()){
                currentDataSets.remove( dataSet );
            }
        }    
        
        // check modified dataset        
        Collections.sort( dataSets );
        Collections.sort( currentDataSets );
        
        for ( int i = 0; i < dataSets.size(); i++ )
        {
            if ( dataSets.get( i ).getVersion() != currentDataSets.get( i ).getVersion() )
            {
                if(updatedDataSetList.getModifiedDataSets() == null)
                    updatedDataSetList.setModifiedDataSets( new ArrayList<DataSet>() );
                updatedDataSetList.getModifiedDataSets().add( dataSets.get( i ) );
            }
        }
        return getUpdatedDataSet( updatedDataSetList );
    }

    @GET
    public DataSetList getUpdatedDataSet( DataSetList dataSetList )
    {
        if ( DEBUG )
            log.debug( "Returning updated datasets for org unit " + unit.getName() );
        return dataSetList;
    }

    /**
     * Get a localized representation of the current activity plan
     */
    @GET
    @Path( "activitiyplan" )
    public ActivityPlan getCurrentActivityPlan( @HeaderParam( "accept-language" ) String locale )
    {
        return activityReportingService.getCurrentActivityPlan( unit, locale );
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
        facilityReportingService.saveDataSetValues( unit, dataSetValue );
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
        activityReportingService.saveActivityReport( unit, activityValue );
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

}
