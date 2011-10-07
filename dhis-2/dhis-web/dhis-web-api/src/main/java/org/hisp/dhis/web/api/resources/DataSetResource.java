package org.hisp.dhis.web.api.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.dxf2.service.DataSetMapper;
import org.hisp.dhis.system.velocity.VelocityManager;
import org.hisp.dhis.web.api.ResponseUtils;
import org.hisp.dhis.web.api.UrlResourceListener;
import org.springframework.beans.factory.annotation.Required;

import com.sun.jersey.api.json.JSONWithPadding;

@Path( "dataSets/{uuid}" )
public class DataSetResource
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    @Required
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    @Required
    public void setVelocityManager( VelocityManager velocityManager )
    {
        this.velocityManager = velocityManager;
    }
    
    private VelocityManager velocityManager;

    @Context
    private UriInfo uriInfo;

    // -------------------------------------------------------------------------
    // Resource Impl
    // -------------------------------------------------------------------------

    @GET
    @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
    public org.hisp.dhis.importexport.dxf2.model.DataSet getDataSetXml( @PathParam( "uuid" ) String uuid )
    {
        DataSet dataSet = dataSetService.getDataSet( uuid );

        if ( dataSet == null )
        {
            throw new IllegalArgumentException( "No dataset with uuid " + uuid );
        }
        
        org.hisp.dhis.importexport.dxf2.model.DataSet dxfDataSet = new DataSetMapper().convert( dataSet );
        new UrlResourceListener( uriInfo ).beforeMarshal( dxfDataSet );
        return dxfDataSet;
    }

    @GET
    @Produces( { "application/javascript" } )
    public JSONWithPadding getDataSet( @PathParam("uuid") String uuid, @QueryParam( "callback" ) @DefaultValue( "callback" ) String callback )
    {
        DataSet dataSet = dataSetService.getDataSet( uuid );

        if ( dataSet == null )
        {
            throw new IllegalArgumentException( "No dataset with uuid " + uuid );
        }

        org.hisp.dhis.importexport.dxf2.model.DataSet dxfDataSet = new DataSetMapper().convert( dataSet );
        new UrlResourceListener( uriInfo ).beforeMarshal( dxfDataSet );
        
        return new JSONWithPadding( dxfDataSet, callback );
    }

    @GET
    @Produces( MediaType.TEXT_HTML )
    public String getDataSet( @PathParam( "uuid" ) String uuid )
    {
        DataSet dataSet = dataSetService.getDataSet( uuid );

        if ( dataSet == null )
        {
            throw new IllegalArgumentException( "No dataset with uuid " + uuid );
        }

        return velocityManager.render( dataSet, ResponseUtils.TEMPLATE_PATH + "dataSet" );
    }
}
