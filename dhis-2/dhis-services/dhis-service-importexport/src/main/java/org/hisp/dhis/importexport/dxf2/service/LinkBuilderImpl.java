package org.hisp.dhis.importexport.dxf2.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.importexport.dxf2.model.Link;

public class LinkBuilderImpl implements LinkBuilder
{
    public List<Link> getLinks( Collection<? extends IdentifiableObject> targets )
    {
        List<Link> links = new ArrayList<Link>();

        for ( IdentifiableObject target : targets )
        {
            links.add( get( target ) );
        }
        return links;
    }

    public Link get( IdentifiableObject target )
    {
        Link link = new Link();
        link.setName( target.getName() );
        link.setId( target.getUuid() );

        return link;
    }

}
