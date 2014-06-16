package org.hisp.dhis.startup;

import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class InitTableAlteror
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( InitTableAlteror.class );
    
    @Autowired
    private StatementManager statementManager;

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    @Transactional
    public void execute()
    {
        // domain type
        
        executeSql( "update dataelement set domaintype='AGGREGATE' where domaintype='aggregate' or domaintype is null;" );
        executeSql( "update dataelement set domaintype='TRACKER' where domaintype='patient';" );
        executeSql( "alter table dataelement alter column domaintype set not null;" );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private int executeSql( String sql )
    {
        try
        {
            // TODO use jdbcTemplate

            return statementManager.getHolder().executeUpdate( sql );
        }
        catch ( Exception ex )
        {
            log.debug( ex );

            return -1;
        }
    }
}
