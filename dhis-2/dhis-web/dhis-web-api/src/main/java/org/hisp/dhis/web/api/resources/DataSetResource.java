package org.hisp.dhis.web.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.dxf2.service.DataSetMapper;
import org.hisp.dhis.web.api.UrlResourceListener;
import org.springframework.beans.factory.annotation.Required;

@Path( "dataSets/{uuid}" )
public class DataSetResource
{
    private DataSetService dataSetService;

    private VelocityManager velocityManager;

    @Context
    private UriInfo uriInfo;

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
    @Produces( MediaType.TEXT_HTML )
    public String getDataSet( @PathParam( "uuid" ) String uuid )
    {
        DataSet dataSet = dataSetService.getDataSet( uuid );

        if ( dataSet == null )
        {
            throw new IllegalArgumentException( "No dataset with uuid " + uuid );
        }
        
        return velocityManager.render( dataSet, "dataSet" );
    }
    
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
}
