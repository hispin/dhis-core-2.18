package org.hisp.dhis.web.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.Marshaller.Listener;

import org.hisp.dhis.importexport.dxf2.model.DataSet;
import org.hisp.dhis.importexport.dxf2.model.DataSetLinks;
import org.hisp.dhis.importexport.dxf2.model.Link;
import org.hisp.dhis.importexport.dxf2.model.OrgUnit;
import org.hisp.dhis.importexport.dxf2.model.OrgUnitLinks;

public class UrlResourceListener
    extends Listener
{
    private UriInfo uriInfo;

    private Map<Class<?>, String> mapping;

    public UrlResourceListener( UriInfo uriInfo )
    {
        super();
        this.uriInfo = uriInfo;

        mapping = new HashMap<Class<?>, String>();
        mapping.put( OrgUnit.class, "orgUnits/{id}" );
        mapping.put( DataSet.class, "dataSets/{id}" );
    }

    @Override
    public void beforeMarshal( Object source )
    {
        if ( source instanceof DataSet )
        {
            addUrls( ((DataSet) source).getOrgUnitLinks(), OrgUnit.class );
        }
        else if ( source instanceof DataSetLinks )
        {
            addUrls( ((DataSetLinks) source).getDataSet(), DataSet.class );
        }
        else if ( source instanceof OrgUnit )
        {
            OrgUnit unit = (OrgUnit) source;
            addUrls( unit.getChildren(), OrgUnit.class );
            addUrl( unit.getParent(), OrgUnit.class );
            addUrls( unit.getDataSets(), DataSet.class );
        }
        else if ( source instanceof OrgUnitLinks )
        {
            addUrls( ((OrgUnitLinks) source).getOrgUnit(), OrgUnit.class );
        }
    }

    private void addUrls( List<Link> links, Class<?> clazz )
    {
        if ( links == null )
        {
            return;
        }

        for ( Link link : links )
        {
            addUrl( link, clazz );
        }
    }

    private void addUrl( Link link, Class<?> clazz )
    {
        if ( link == null )
        {
            return;
        }

        String id = link.getId();
        String path = mapping.get( clazz );
        String url = uriInfo.getBaseUriBuilder().path( path ).build( id ).toString();

        link.setHref( url );
    }
}
