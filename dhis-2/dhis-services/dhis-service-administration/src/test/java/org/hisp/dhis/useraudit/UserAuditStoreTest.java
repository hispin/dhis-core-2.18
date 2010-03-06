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
        userAuditStore.saveLoginFailure( new LoginFailure( "userA", getDate( 2000, 1, 3 ) ) );
        userAuditStore.saveLoginFailure( new LoginFailure( "userA", getDate( 2000, 1, 4 ) ) );
        userAuditStore.saveLoginFailure( new LoginFailure( "userB", getDate( 2000, 1, 5 ) ) );
        
        assertNotNull( userAuditStore.getAllLoginFailures() );
        assertEquals( 3, userAuditStore.getAllLoginFailures().size() );
    }
    
    @Test
    public void get()
    {
        userAuditStore.saveLoginFailure( new LoginFailure( "userA", getDate( 2000, 1, 3 ) ) );
        userAuditStore.saveLoginFailure( new LoginFailure( "userA", getDate( 2000, 1, 4 ) ) );
        userAuditStore.saveLoginFailure( new LoginFailure( "userA", getDate( 2000, 1, 5 ) ) );
        userAuditStore.saveLoginFailure( new LoginFailure( "userA", getDate( 2000, 1, 6 ) ) );
        userAuditStore.saveLoginFailure( new LoginFailure( "userB", getDate( 2000, 1, 7 ) ) );
        
        assertEquals( 3, userAuditStore.getLoginFailures( "userA", getDate( 2000, 1, 4 ) ) );
    }
}
