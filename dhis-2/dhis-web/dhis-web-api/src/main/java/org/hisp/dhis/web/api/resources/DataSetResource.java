package org.hisp.dhis.web.api.resources;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.beans.factory.annotation.Required;

@Path( "dataSets" )
public class DataSetResource
{

    private DataSetService dataSetService;

    @Context
    UriInfo uriInfo;
    
    @GET
    @Produces( MediaType.TEXT_HTML )
    public String getDataSetList()
    {
        StringBuilder t = new StringBuilder();
        t.append( head( "Data sets available for reporting" ) );

        t.append( "<h2>Data sets available for reporting</h2>\n<ul>\n" );
        for ( DataSet dataSet : dataSetService.getAllDataSets() )
        {
            URI uri = uriInfo.getAbsolutePathBuilder().path( "{uuid}" ).build( dataSet.getUuid() );
            t.append( "<li>" ).append( "<a href=\"" ).append( uri ).append( "\">" ).append( dataSet.getName() )
                .append( "</a></li>\n" );
        }
        t.append( "</ul>" );
        DataValueSetResource.xmlTemplate( t, uriInfo );
        t.append( tail() );

        return t.toString();
    }

    @GET
    @Path( "{uuid}" )
    @Produces( MediaType.TEXT_HTML )
    public String getDataSet( @PathParam( "uuid" ) String uuid )
    {

        DataSet dataSet = dataSetService.getDataSet( uuid );

        if ( dataSet == null )
        {
            throw new IllegalArgumentException( "No dataset with uuid " + uuid );
        }

        StringBuilder t = new StringBuilder();

        t.append( head( "Data set " + dataSet.getName() ) );
        t.append( "<p>Uuid: " ).append( dataSet.getUuid() ).append( "<br>\n" );
        t.append( "Period type: " ).append( dataSet.getPeriodType().getName() ).append( " - " )
            .append( dataSet.getPeriodType().getIsoFormat() );
        t.append( "</p>\n" );

        t.append( "<h2>Org units reporting data set</h2>\n<ul>" );
        for ( OrganisationUnit unit : dataSet.getOrganisationUnits() )
        {
            t.append( "<li><b>" ).append( unit.getName() ).append( "</b> - " ).append( unit.getUuid() )
                .append( "</li>" );
        }
        t.append( "</ul>\n" );

        t.append( "<h2>Data elements in data set</h2>\n<ul>" );
        for ( DataElement element : dataSet.getDataElements() )
        {
            t.append( "<li><b>" ).append( element.getName() ).append( "</b> (" ).append( element.getType() )
                .append( ") - " ).append( element.getUuid() );

            Set<DataElementCategoryOptionCombo> optionCombos = element.getCategoryCombo().getOptionCombos();
            if ( optionCombos.size() > 1 )
            {
                t.append( "<br>CategoryOptionCombos\n<ul>\n" );
                for ( DataElementCategoryOptionCombo optionCombo : optionCombos )
                {
                    t.append( "<li><b>" ).append( optionCombo.getName() ).append( "</b> - " )
                        .append( optionCombo.getUuid() ).append( "</li>" );
                }
                t.append( "</ul>\n" );
            }
            t.append( "</li>\n" );
        }
        t.append( "</ul>" );
        t.append( "<h2>Xml template</h2>\n" );

        DataValueSetResource.xmlTemplate( t, uriInfo );
        t.append( tail() );

        return t.toString();
    }

    private String head( String title )
    {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\"> \n<html><head><title>"
            + title + "</title></head>\n" + "<body>\n<h1>" + title + "</h1>\n";
    }

    private String tail()
    {
        return "</body>\n</html>\n";
    }



    @Required
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }


    
}
