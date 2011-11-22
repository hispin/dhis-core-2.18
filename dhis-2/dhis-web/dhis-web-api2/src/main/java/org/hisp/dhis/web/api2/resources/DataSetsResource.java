package org.hisp.dhis.web.api2.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.dxf2.model.DataSetLinks;
import org.hisp.dhis.importexport.dxf2.service.LinkBuilder;
import org.hisp.dhis.importexport.dxf2.service.LinkBuilderImpl;
import org.hisp.dhis.system.velocity.VelocityManager;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.web.api2.ResponseUtils;
import org.hisp.dhis.web.api2.UrlResourceListener;
import org.springframework.beans.factory.annotation.Required;

import com.sun.jersey.api.json.JSONWithPadding;

@Path( "dataSets" )
public class DataSetsResource
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

    private VelocityManager velocityManager;

    @Required
    public void setVelocityManager( VelocityManager velocityManager )
    {
        this.velocityManager = velocityManager;
    }

    private LinkBuilder linkBuilder = new LinkBuilderImpl();

    @Context
    private UriInfo uriInfo;

    // -------------------------------------------------------------------------
    // Resource Impl
    // -------------------------------------------------------------------------

    @GET
    @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
    public DataSetLinks getDataSetLinks()
    {
        DataSetLinks dataSetLinks = new DataSetLinks( linkBuilder.getLinks( dataSetService.getAllDataSets() ) );
        new UrlResourceListener( uriInfo ).beforeMarshal( dataSetLinks );
        return dataSetLinks;
    }

    @GET
    @Produces( ContextUtils.CONTENT_TYPE_JAVASCRIPT )
    public JSONWithPadding getDataSets( @QueryParam( "callback" ) @DefaultValue( "callback" )
    String callback )
    {
        Collection<DataSet> dataSets = dataSetService.getAllDataSets();
        Map<String, Object> dataSetOutput = new HashMap<String, Object>();

        List<Map<String, Object>> dataSetsArray = new ArrayList<Map<String, Object>>();

        for ( DataSet dataSet : dataSets )
        {
            Map<String, Object> dataSetMap = new HashMap<String, Object>();
            dataSetMap.put( "id", dataSet.getId() );
            dataSetMap.put( "href", uriInfo.getAbsolutePath().toASCIIString() + "/" + dataSet.getId() );
            dataSetMap.put( "name", dataSet.getName() );

            dataSetsArray.add( dataSetMap );
        }

        dataSetOutput.put( "dataSets", dataSetsArray );

        return new JSONWithPadding( dataSetOutput, callback );
    }

    @GET
    @Produces( MediaType.TEXT_HTML )
    public String getDataSetList()
    {
        DataSetLinks dataSetLinks = new DataSetLinks( linkBuilder.getLinks( dataSetService.getAllDataSets() ) );
        new UrlResourceListener( uriInfo ).beforeMarshal( dataSetLinks );
        return velocityManager.render( dataSetLinks.getDataSet(), ResponseUtils.TEMPLATE_PATH + "dataSets" );
    }
}
