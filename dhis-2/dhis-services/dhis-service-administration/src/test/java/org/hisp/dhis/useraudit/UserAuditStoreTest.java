package org.hisp.dhis.useraudit;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

import static junit.framework.Assert.*;

public class UserAuditStoreTest
    extends DhisSpringTest
{
    private UserAuditStore userAuditStore;
    
    @Override
    public void setUpTest()
    {
        userAuditStore = (UserAuditStore) getBean( UserAuditStore.ID );
    }
    
    @Test
    public void save()
    {
        userAuditStore.saveFailedLogin( new FailedLogin( "userA", getDate( 2000, 1, 3 ) ) );
        userAuditStore.saveFailedLogin( new FailedLogin( "userA", getDate( 2000, 1, 4 ) ) );
        userAuditStore.saveFailedLogin( new FailedLogin( "userB", getDate( 2000, 1, 5 ) ) );
        
        assertNotNull( userAuditStore.getAllFailedLogins() );
        assertEquals( 3, userAuditStore.getAllFailedLogins().size() );
    }
    
    @Test
    public void get()
    {
        userAuditStore.saveFailedLogin( new FailedLogin( "userA", getDate( 2000, 1, 3 ) ) );
        userAuditStore.saveFailedLogin( new FailedLogin( "userA", getDate( 2000, 1, 4 ) ) );
        userAuditStore.saveFailedLogin( new FailedLogin( "userA", getDate( 2000, 1, 5 ) ) );
        userAuditStore.saveFailedLogin( new FailedLogin( "userA", getDate( 2000, 1, 6 ) ) );
        userAuditStore.saveFailedLogin( new FailedLogin( "userB", getDate( 2000, 1, 7 ) ) );
        
        assertEquals( 3, userAuditStore.getFailedLogins( "userA", getDate( 2000, 1, 4 ) ) );
    }
}
