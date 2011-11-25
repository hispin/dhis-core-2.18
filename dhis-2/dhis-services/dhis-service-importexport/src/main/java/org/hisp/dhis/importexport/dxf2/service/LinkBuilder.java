package org.hisp.dhis.importexport.dxf2.service;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.importexport.dxf2.model.Link;

public interface LinkBuilder
{
    public List<Link> getLinks( Collection<? extends BaseIdentifiableObject> targets  );

    public Link get( BaseIdentifiableObject target  );
}
