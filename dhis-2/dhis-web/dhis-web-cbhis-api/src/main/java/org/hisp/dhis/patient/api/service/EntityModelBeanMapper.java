package org.hisp.dhis.patient.api.service;


public interface EntityModelBeanMapper<S, T>
{

    public T getModel( S entity, MappingManager mappingManager );

    public Object getModelAsObject( Object entity, MappingManager mappingManager);
    
}
