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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
@Transactional
public class DefaultIdentifiableObjectManager
    implements IdentifiableObjectManager
{
    private Set<GenericIdentifiableObjectStore<IdentifiableObject>> objectStores;

    @Autowired
    public void setObjectStores( Set<GenericIdentifiableObjectStore<IdentifiableObject>> objectStores )
    {
        this.objectStores = objectStores;
    }

    private Map<Class<IdentifiableObject>, GenericIdentifiableObjectStore<IdentifiableObject>> objectStoreMap;
    
    @PostConstruct
    public void init()
    {
        objectStoreMap = new HashMap<Class<IdentifiableObject>, GenericIdentifiableObjectStore<IdentifiableObject>>();
        
        for ( GenericIdentifiableObjectStore<IdentifiableObject> store : objectStores )
        {
            objectStoreMap.put( store.getClazz(), store );
        }
    }

    public void save( IdentifiableObject object )
    {
        objectStoreMap.get( object.getClass() ).save( object );
    }

    public void update( IdentifiableObject object )
    {
        objectStoreMap.get( object.getClass() ).update( object );
    }

    public void get( Class<IdentifiableObject> clazz, String uid )
    {
        objectStoreMap.get( clazz ).getByUid( uid );
    }

    public void delete( IdentifiableObject object )
    {
        objectStoreMap.get( object.getClass() ).delete( object );
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
