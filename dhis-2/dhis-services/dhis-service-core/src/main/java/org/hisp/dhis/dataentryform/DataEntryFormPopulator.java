package org.hisp.dhis.dataentryform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DataEntryFormPopulator
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( DataEntryFormPopulator.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public void execute()
        throws Exception
    {
        log.info( "Remove datasetid column from dataentryform table" );
        try
        {
            executeTransactional();
        }
        catch ( Exception ex )
        {
            log.debug( ex );
        }
    }

    @Transactional( rollbackFor = Exception.class )
    public void executeTransactional()
        throws Exception
    {
        jdbcTemplate.execute( "INSERT INTO dataentryformassociation  SELECT 'dataset', datasetid, dataentryformid FROM dataentryform;" );
        jdbcTemplate.execute( statementBuilder.getDropDatasetForeignKeyForDataEntryFormTable() );
        jdbcTemplate.execute( "ALTER TABLE dataentryform DROP COLUMN datasetid;" );
    }

}
