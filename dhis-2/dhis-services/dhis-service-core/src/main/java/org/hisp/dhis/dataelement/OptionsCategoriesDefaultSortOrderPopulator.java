package org.hisp.dhis.dataelement;

import java.sql.SQLException;

import org.amplecode.quick.StatementHolder;
import org.amplecode.quick.StatementManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.transaction.annotation.Transactional;

public class OptionsCategoriesDefaultSortOrderPopulator
    extends AbstractStartupRoutine
{
    private static final Log LOG = LogFactory.getLog( OptionsCategoriesDefaultSortOrderPopulator.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------
    @Transactional
    public void execute()
        throws Exception
    {
        
        updateCategoryOptionComboAndOptionsTable();
        
        updateCategoryComboAndCategoriesTable();
        
        updateCategoryAndOptionsTable();
        
    }
    
    // -------------------------------------------------------------------------
    // Custom SQL update stmt
    // -------------------------------------------------------------------------
    
    public void updateCategoryOptionComboAndOptionsTable()
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {         
            final String sql = "update categoryoptioncombos_categoryoptions set sort_order=0 where sort_order is NULL";
            
            holder.getStatement().executeUpdate( sql );
        }
        catch ( SQLException ex )
        {
            LOG.warn( "Failed to update categoryoptioncombos_categoryoptions", ex  );            
        }
        finally
        {
            holder.close();
        }
        
        LOG.info( "Updated  categoryoptioncombos_categoryoptions" );
    }
    
    public void updateCategoryComboAndCategoriesTable()
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {            
            final String sql = "update categorycombos_categories set sort_order=0 where sort_order is NULL";
            
            holder.getStatement().executeUpdate( sql );
        }
        catch ( SQLException ex )
        {
            LOG.warn( "Failed to update categorycombos_categories", ex );
        }
        finally
        {
            holder.close();
        }
        
        LOG.info( "Updated  categorycombos_categories" );
    }
    
    public void updateCategoryAndOptionsTable()
    {
        final StatementHolder holder = statementManager.getHolder();
        
        try
        {            
            final String sql = "update categories_categoryoptions set sort_order=0 where sort_order is NULL";
            
            holder.getStatement().executeUpdate( sql );
        }
        catch ( SQLException ex )
        {
            LOG.warn( "Failed to update categories_categoryoptions", ex );
        }
        finally
        {
            holder.close();
        }
        
        LOG.info( "Updated  categories_categoryoptions" );
    }
}
