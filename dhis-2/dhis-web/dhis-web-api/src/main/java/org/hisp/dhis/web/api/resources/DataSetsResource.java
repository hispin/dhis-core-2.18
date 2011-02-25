package org.hisp.dhis.web.api.resources;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.dxf2.model.DataSetLinks;
import org.hisp.dhis.importexport.dxf2.model.Link;
import org.hisp.dhis.importexport.dxf2.model.OrgUnitLinks;
import org.hisp.dhis.importexport.dxf2.service.LinkBuilder;
import org.hisp.dhis.importexport.dxf2.service.LinkBuilderImpl;
import org.hisp.dhis.web.api.UrlResourceListener;
import org.springframework.beans.factory.annotation.Required;

@Path( "dataSets" )
public class DataSetsResource
{

    private LinkBuilder linkBuilder = new LinkBuilderImpl();
    
    private DataSetService dataSetService;

    @Context
    UriInfo uriInfo;

    @GET
    @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
    public DataSetLinks getDataSetLinks() {
        DataSetLinks dataSetLinks = new DataSetLinks( linkBuilder.getLinks( dataSetService.getAllDataSets() ) );
        new UrlResourceListener( uriInfo ).beforeMarshal( dataSetLinks );
        return dataSetLinks;
    }
    
    @GET
    @Produces( MediaType.TEXT_HTML )
    public String getDataSetList()
    {
        StringBuilder t = Html.head( "Data sets available for reporting" );

        t.append( "<p>See the <a href=\"dataSets.xml\">xml version</a></p>\n" );
        for ( DataSet dataSet : dataSetService.getAllDataSets() )
        {
            URI uri = uriInfo.getAbsolutePathBuilder().path( "{uuid}" ).build( dataSet.getUuid() );
            t.append( "<li>" ).append( "<a href=\"" ).append( uri ).append( "\">" ).append( dataSet.getName() )
                .append( "</a></li>\n" );
        }
        t.append( "</ul>" );
        Html.xmlTemplate( t, uriInfo );
        t.append( Html.tail() );

        return t.toString();
    }

    
    
    @Required
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

}
