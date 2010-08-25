package org.hisp.dhis.web.api.service.mapping;



public interface BeanMapper<S, T>
{
    public T getModel( S entity );
}
