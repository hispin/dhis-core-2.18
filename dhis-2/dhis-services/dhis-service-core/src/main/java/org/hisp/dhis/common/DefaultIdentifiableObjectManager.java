package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.common.NameableObject.NameableProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultIdentifiableObjectManager
    implements IdentifiableObjectManager
{
    @Autowired
    private Set<GenericIdentifiableObjectStore<IdentifiableObject>> objectStores;

    @Autowired
    private Set<GenericNameableObjectStore<NameableObject>> nameableObjectStores;

    private Map<Class<IdentifiableObject>, GenericIdentifiableObjectStore<IdentifiableObject>> objectStoreMap;

    private Map<Class<NameableObject>, GenericNameableObjectStore<NameableObject>> nameableObjectStoreMap;

    @PostConstruct
    public void init()
    {
        objectStoreMap = new HashMap<Class<IdentifiableObject>, GenericIdentifiableObjectStore<IdentifiableObject>>();

        for ( GenericIdentifiableObjectStore<IdentifiableObject> store : objectStores )
        {
            objectStoreMap.put( store.getClazz(), store );
        }

        nameableObjectStoreMap = new HashMap<Class<NameableObject>, GenericNameableObjectStore<NameableObject>>();

        for ( GenericNameableObjectStore<NameableObject> store : nameableObjectStores )
        {
            nameableObjectStoreMap.put( store.getClazz(), store );
        }
    }

    public void save( IdentifiableObject object )
    {
        if ( objectStoreMap.get( object.getClass() ) != null )
        {
            objectStoreMap.get( object.getClass() ).save( object );
        }
    }

    public void update( IdentifiableObject object )
    {
        if ( objectStoreMap.get( object.getClass() ) != null )
        {
            objectStoreMap.get( object.getClass() ).update( object );
        }
    }

    public <T extends IdentifiableObject> T get( Class<T> clazz, String uid )
    {
        if ( objectStoreMap.get( clazz ) != null )
        {
            return (T) objectStoreMap.get( clazz ).getByUid( uid );
        }

        return null;
    }

    @Override
    public <T extends IdentifiableObject> T getByCode( Class<T> clazz, String code )
    {
        if ( objectStoreMap.get( clazz ) != null )
        {
            return (T) objectStoreMap.get( clazz ).getByCode( code );
        }

        return null;
    }

    @Override
    public <T extends IdentifiableObject> T getByName( Class<T> clazz, String name )
    {
        if ( objectStoreMap.get( clazz ) != null )
        {
            return (T) objectStoreMap.get( clazz ).getByName( name );
        }

        return null;
    }

    @SuppressWarnings( "unchecked" )
    public <T extends IdentifiableObject> Collection<T> getAll( Class<T> clazz )
    {
        return (Collection<T>) objectStoreMap.get( clazz ).getAll();
    }

    public void delete( IdentifiableObject object )
    {
        objectStoreMap.get( object.getClass() ).delete( object );
    }

    @SuppressWarnings( "unchecked" )
    public <T extends IdentifiableObject> Map<String, T> getIdMap( Class<T> clazz, IdentifiableProperty property )
    {
        Map<String, T> map = new HashMap<String, T>();

        GenericIdentifiableObjectStore<T> store = (GenericIdentifiableObjectStore<T>) objectStoreMap.get( clazz );

        Collection<T> objects = store.getAll();

        for ( T object : objects )
        {
            if ( IdentifiableProperty.ID.equals( property ) )
            {
                if ( object.getId() > 0 )
                {
                    map.put( String.valueOf( object.getId() ), object );
                }
            }
            else if ( IdentifiableProperty.UID.equals( property ) )
            {
                if ( object.getUid() != null )
                {
                    map.put( object.getUid(), object );
                }
            }
            else if ( IdentifiableProperty.NAME.equals( property ) )
            {
                if ( object.getName() != null )
                {
                    map.put( object.getName(), object );
                }
            }
            else if ( IdentifiableProperty.CODE.equals( property ) )
            {
                if ( object.getCode() != null )
                {
                    map.put( object.getCode(), object );
                }
            }
        }

        return map;
    }

    @Override
    public <T extends NameableObject> Map<String, T> getIdMap( Class<T> clazz, NameableProperty property )
    {
        Map<String, T> map = new HashMap<String, T>();

        GenericNameableObjectStore<T> store = (GenericNameableObjectStore<T>) nameableObjectStoreMap.get( clazz );

        Collection<T> objects = store.getAll();

        for ( T object : objects )
        {
            if ( property == NameableProperty.SHORT_NAME )
            {
                if ( object.getShortName() != null )
                {
                    map.put( object.getShortName(), object );
                }
            }
            else if ( property == NameableProperty.ALTERNATIVE_NAME )
            {
                if ( object.getAlternativeName() != null )
                {
                    map.put( object.getAlternativeName(), object );
                }
            }
        }

        return map;
    }

    @SuppressWarnings( "unchecked" )
    public <T extends IdentifiableObject> T getObject( Class<T> clazz, IdentifiableProperty property, String id )
    {
        GenericIdentifiableObjectStore<T> store = (GenericIdentifiableObjectStore<T>) objectStoreMap.get( clazz );

        if ( id != null )
        {
            if ( IdentifiableProperty.ID.equals( property ) )
            {
                if ( Integer.valueOf( id ) > 0 )
                {
                    return store.get( Integer.valueOf( id ) );
                }
            }
            else if ( IdentifiableProperty.UID.equals( property ) )
            {
                return store.getByUid( id );
            }
            else if ( IdentifiableProperty.CODE.equals( property ) )
            {
                return store.getByCode( id );
            }
            else if ( IdentifiableProperty.NAME.equals( property ) )
            {
                return store.getByName( id );
            }
        }

        throw new IllegalArgumentException( String.valueOf( property ) );
    }

    public IdentifiableObject getObject( String uid, String simpleClassName )
    {
        for ( GenericIdentifiableObjectStore<IdentifiableObject> objectStore : objectStores )
        {
            if ( simpleClassName.equals( objectStore.getClass().getSimpleName() ) )
            {
                return objectStore.getByUid( uid );
            }
        }

        return null;
    }

    public IdentifiableObject getObject( int id, String simpleClassName )
    {
        for ( GenericIdentifiableObjectStore<IdentifiableObject> objectStore : objectStores )
        {
            if ( simpleClassName.equals( objectStore.getClazz().getSimpleName() ) )
            {
                return objectStore.get( id );
            }
        }

        return null;
    }
}
