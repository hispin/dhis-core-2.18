package org.hisp.dhis.schema;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 */

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.AnyType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hisp.dhis.common.AnalyticalObject;
import org.hisp.dhis.common.BaseAnalyticalObject;
import org.hisp.dhis.common.BaseDimensionalObject;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractPropertyIntrospectorService
    implements PropertyIntrospectorService
{
    // simple alias map for our concrete implementations of the core interfaces.
    private static final ImmutableMap<Class<?>, Class<?>> BASE_ALIAS_MAP = ImmutableMap.<Class<?>, Class<?>>builder()
        .put( IdentifiableObject.class, BaseIdentifiableObject.class )
        .put( NameableObject.class, BaseNameableObject.class )
        .put( DimensionalObject.class, BaseDimensionalObject.class )
        .put( AnalyticalObject.class, BaseAnalyticalObject.class )
        .build();

    private Map<Class<?>, Map<String, Property>> classMapCache = Maps.newHashMap();

    @Autowired
    protected ApplicationContext context;

    @Autowired
    protected SessionFactory sessionFactory;

    @Override
    public List<Property> getProperties( Class<?> klass )
    {
        return Lists.newArrayList( getPropertiesMap( klass ).values() );
    }

    @Override
    public Map<String, Property> getPropertiesMap( Class<?> klass )
    {
        if ( BASE_ALIAS_MAP.containsKey( klass ) )
        {
            klass = BASE_ALIAS_MAP.get( klass );
        }

        if ( !classMapCache.containsKey( klass ) )
        {
            classMapCache.put( klass, scanClass( klass ) );
        }

        return classMapCache.get( klass );
    }

    @Override
    public Class<?> getConcreteClass( Class<?> klass )
    {
        if ( BASE_ALIAS_MAP.containsKey( klass ) )
        {
            return BASE_ALIAS_MAP.get( klass );
        }

        return klass;
    }

    /**
     * Introspect a class and return a map with key=property-name, and value=Property class.
     *
     * @param klass Class to scan
     * @return Map with key=property-name, and value=Property class
     */
    protected abstract Map<String, Property> scanClass( Class<?> klass );

    protected LocalSessionFactoryBean getLocalSessionFactoryBean()
    {
        return (LocalSessionFactoryBean) context.getBean( "&sessionFactory" );
    }

    protected Map<String, Property> getPropertiesFromHibernate( Class<?> klass )
    {
        ClassMetadata classMetadata = sessionFactory.getClassMetadata( klass );

        // is class persisted with hibernate
        if ( classMetadata == null )
        {
            return new HashMap<>();
        }

        LocalSessionFactoryBean sessionFactoryBean = getLocalSessionFactoryBean();
        PersistentClass persistentClass = sessionFactoryBean.getConfiguration().getClassMapping( klass.getName() );

        Iterator propertyIterator = persistentClass.getPropertyClosureIterator();

        Map<String, Property> properties = new HashMap<>();

        while ( propertyIterator.hasNext() )
        {
            Property property = new Property( klass );

            org.hibernate.mapping.Property hibernateProperty = (org.hibernate.mapping.Property) propertyIterator.next();
            Type type = hibernateProperty.getType();

            property.setName( hibernateProperty.getName() );

            property.setSetterMethod( hibernateProperty.getSetter( klass ).getMethod() );
            property.setGetterMethod( hibernateProperty.getGetter( klass ).getMethod() );

            if ( type.isCollectionType() )
            {
                CollectionType collectionType = (CollectionType) type;
                property.setCollection( true );
            }
            else if ( type.isEntityType() )
            {
                EntityType entityType = (EntityType) type;
            }
            else if ( type.isAssociationType() )
            {
                AssociationType associationType = (AssociationType) type;
            }
            else if ( type.isAnyType() )
            {
                AnyType anyType = (AnyType) type;
            }
            else
            {
                Column column = (Column) hibernateProperty.getColumnIterator().next();

                property.setUnique( column.isUnique() );
                property.setNullable( column.isNullable() );

                property.setMaxLength( column.getLength() );
                property.setMinLength( 0 );
            }

            properties.put( property.getName(), property );
        }

        return properties;
    }
}
