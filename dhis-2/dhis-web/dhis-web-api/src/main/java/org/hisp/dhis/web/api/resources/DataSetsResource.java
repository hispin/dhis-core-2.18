package org.hisp.dhis.web.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.dxf2.model.DataSetLinks;
import org.hisp.dhis.importexport.dxf2.service.LinkBuilder;
import org.hisp.dhis.importexport.dxf2.service.LinkBuilderImpl;
import org.hisp.dhis.web.api.UrlResourceListener;
import org.springframework.beans.factory.annotation.Required;

@Path( "dataSets" )
public class DataSetsResource
{
    private LinkBuilder linkBuilder = new LinkBuilderImpl();
    
    private DataSetService dataSetService;

    private VelocityManager velocityManager;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
    public DataSetLinks getDataSetLinks() 
    {
        DataSetLinks dataSetLinks = new DataSetLinks( linkBuilder.getLinks( dataSetService.getAllDataSets() ) );
        new UrlResourceListener( uriInfo ).beforeMarshal( dataSetLinks );
        return dataSetLinks;
    }
    
    @GET
    @Produces( MediaType.TEXT_HTML )
    public String getDataSetList()
    {
        DataSetLinks dataSetLinks = new DataSetLinks( linkBuilder.getLinks( dataSetService.getAllDataSets() ) );
        new UrlResourceListener( uriInfo ).beforeMarshal( dataSetLinks );
        return velocityManager.render( dataSetLinks.getDataSet(), "dataSets" );
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
