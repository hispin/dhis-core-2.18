package org.hisp.dhis.patient.api.service.mapping;

import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.patient.api.service.MappingFactory;


public interface BeanMapper<S, T>
{
    public T getModel( S entity, MappingFactory mappingFactory, UriInfo uriInfo );
}
