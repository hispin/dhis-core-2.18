package org.hisp.dhis.common;

public interface IdentifiableObjectManager
{
    IdentifiableObject getObject( String uid, String simpleClassName );
    
    IdentifiableObject getObject( int id, String simpleClassName );
}
