package org.hisp.dhis.patient.api.service;


public abstract class AbstractEntitiyModelBeanMapper<S, T>
    implements EntityModelBeanMapper<S, T>
{

    @Override
    public Object getModelAsObject( Object entity, MappingManager mappingManager )
    {
        return getModel( (S) entity, mappingManager );
    }

}