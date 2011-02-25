package org.hisp.dhis.web.api.resources;

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
import org.hisp.dhis.importexport.dxf2.service.DataSetMapper;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.beans.factory.annotation.Required;

@Path( "dataSets/{uuid}" )
public class DataSetResource
{

    private DataSetService dataSetService;

    @Context
    UriInfo uriInfo;

    @GET
    @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
    public org.hisp.dhis.importexport.dxf2.model.DataSet getDataSetXml( @PathParam( "uuid" ) String uuid )
    {
        DataSet dataSet = dataSetService.getDataSet( uuid );

        if ( dataSet == null )
        {
            throw new IllegalArgumentException( "No dataset with uuid " + uuid );
        }
        return new DataSetMapper().convert( dataSet );
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

        StringBuilder t = Html.head( "Data set " + dataSet.getName() );
        t.append( "<p>See the <a href=\"" + uuid + ".xml\">xml version</a></p>\n" );
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
