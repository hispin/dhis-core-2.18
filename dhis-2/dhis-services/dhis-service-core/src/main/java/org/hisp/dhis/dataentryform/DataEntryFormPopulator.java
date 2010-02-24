package org.hisp.dhis.dataentryform;

import java.sql.Statement;

import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.transaction.annotation.Transactional;

public class DataEntryFormPopulator
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( DataEntryFormPopulator.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    private StatementBuilder statementBuilder;
    
    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------
    
    @Transactional
    public void execute()
        throws Exception
    {
        log.info( "Remove datasetid column from dataentryform table" );
        Statement stmnt = statementManager.getHolder().getStatement();
        try
        {
            stmnt.executeUpdate(  statementBuilder.getDropDatasetForeignKeyForDataEntryFormTable() );
            stmnt.executeUpdate( "INSERT INTO dataentryformassociation  SELECT 'dataset', datasetid, dataentryformid FROM dataentryform;" );
            stmnt.executeUpdate( "ALTER TABLE dataentryform DROP COLUMN datasetid;" );
        }
        catch ( Exception ex )
        {
            log.error( ex );
        }
    }


    

}
