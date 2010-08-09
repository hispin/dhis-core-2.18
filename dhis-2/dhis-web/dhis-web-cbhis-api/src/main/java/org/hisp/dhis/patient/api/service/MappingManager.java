package org.hisp.dhis.patient.api.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.patient.api.resources.OrgUnitResource;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.ClassUtils;

public class MappingManager
{
    private static final Log LOG = LogFactory.getLog( MappingManager.class );

    
    @Autowired
    Set<EntityModelBeanMapper<?, ?>> mappers;

    public void setMappers( Set<EntityModelBeanMapper<?, ?>> mappers )
    {
        this.mappers = mappers;
    }

    Map<Class<?>, EntityModelBeanMapper<?, ?>> mappingIndex = new HashMap<Class<?>, EntityModelBeanMapper<?, ?>>();

    @PostConstruct
    public void init()
    {
        if ( mappers == null || mappers.isEmpty() )
        {
            throw new BeanInitializationException( "The mapping manager has no bean mappers registered" );
        }

        for ( EntityModelBeanMapper<?, ?> mapper : mappers )
        {
            Class[] a = GenericTypeResolver.resolveTypeArguments( mapper.getClass(), EntityModelBeanMapper.class );
            Class domainClass = a[0];
            Class modelClass = a[1];

            LOG.debug( "Registered mapper from class " + domainClass.getName() + " to " + modelClass.getName() );
            System.out.println( "Registered mapper from class " + domainClass.getName() + " to " + modelClass.getName() );

            if (mappingIndex.containsKey( domainClass )) {
                throw new BeanInitializationException( "Conflicing EntityModelBeanMappers, two mappers mapping from the same entity: " + mapper + " and " + mappingIndex.get( domainClass ));
            }
            mappingIndex.put( domainClass, mapper );
        }
    }

    public Object map( Object entity )
    {
        EntityModelBeanMapper<?, ?> mapper = getMapper( entity );

        if (mapper == null) {
            throw new EntityMappingException("Missing mapper for entity " + entity.getClass().getName());
        }
        
        return mapper.getModelAsObject( entity, this );
    }

    EntityModelBeanMapper<?, ?> getMapper( Object entity )
    {
        return this.mappingIndex.get( ClassUtils.getUserClass( entity ) );
    }

}
