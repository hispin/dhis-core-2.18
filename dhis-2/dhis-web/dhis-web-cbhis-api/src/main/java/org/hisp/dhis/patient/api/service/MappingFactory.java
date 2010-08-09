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
import org.hisp.dhis.patient.api.service.mapping.BeanMapper;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.ClassUtils;

/**
 * @author storset
 *
 */
public class MappingFactory
{
    private static final Log LOG = LogFactory.getLog( MappingFactory.class );

    Set<BeanMapper<?, ?>> mappers;

    public void setMappers( Set<BeanMapper<?, ?>> mappers )
    {
        this.mappers = mappers;
    }

    Map<Class<?>, BeanMapper<?, ?>> mappingIndex = new HashMap<Class<?>, BeanMapper<?, ?>>();

    @PostConstruct
    public void init()
    {
        if ( mappers == null || mappers.isEmpty() )
        {
            throw new BeanInitializationException( "The mapping manager has no bean mappers registered" );
        }

        for ( BeanMapper<?, ?> mapper : mappers )
        {
            Class<?>[] a = GenericTypeResolver.resolveTypeArguments( mapper.getClass(), BeanMapper.class );
            Class<?> domainClass = a[0];
            Class<?> modelClass = a[1];

            LOG.debug( "Registered mapper to class to " + modelClass.getName() + "(from " + domainClass.getName() + ")" );

            if ( mappingIndex.containsKey( modelClass ) )
            {
                throw new BeanInitializationException(
                    "Conflicing EntityModelBeanMappers, two mappers mapping to the same entity "
                        + domainClass.getName() + ": " + mapper + " and " + mappingIndex.get( domainClass ) );
            }
            mappingIndex.put( modelClass, mapper );
        }
    }

    
    /**
     * @param <S>
     * @param <T>
     * @param source Class type to map from (S)
     * @param destination Class type to map to (T)
     * @return
     */
    public <S, T> BeanMapper<S, T> getBeanMapper( Class<S> source, Class<T> destination )
    {
        BeanMapper<S, T> beanMapper = (BeanMapper<S, T>) this.mappingIndex.get( destination );
        
        return beanMapper;
    }

    
    
//    private <T> T map( Object entity, Class<T> destination )
//    {
//        return map( entity, destination, entity.getClass() );
//    }
//
//    private <T, S> T map( Object entity, Class<T> destination, Class<S> srcClass )
//    {
//        BeanMapper<S, T> mapper = (BeanMapper<S, T>) this.mappingIndex.get( destination ); 
//        
//        if ( mapper == null )
//        {
//            throw new EntityMappingException( "Missing mapper for entity " + entity.getClass().getName() );
//        }
//
//        return mapper.getModel( (S) entity, this );
//    }

}
