package org.hisp.dhis.common;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class DefaultIdentifiableObjectManager
    implements IdentifiableObjectManager
{
    private Set<GenericIdentifiableObjectStore<IdentifiableObject>> objectStores;

    @Autowired
    public void setObjectStores( Set<GenericIdentifiableObjectStore<IdentifiableObject>> objectStores )
    {
        this.objectStores = objectStores;
    }

    @Transactional
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
    
    @Transactional
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
