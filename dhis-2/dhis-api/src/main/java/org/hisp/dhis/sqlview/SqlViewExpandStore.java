package org.hisp.dhis.sqlview;

import java.util.Collection;

/**
 * @author Dang Duy Hieu
 * @version $Id SqlViewExpandStore.java July 06, 2010$
 */
public interface SqlViewExpandStore
{
    String ID = SqlViewExpandStore.class.getName();

    // -------------------------------------------------------------------------
    // SqlView expanded
    // -------------------------------------------------------------------------

    Collection<String> getAllSqlViewNames();

    boolean isViewTableExists( String viewTableName );
    
    void setUpDataSqlViewTable( SqlViewTable sqlViewTable, String viewTableName );

    Collection<String> getAllResourceProperties( String resourceTableName );
    
    String testSqlGrammar( String sql );
    
    String setUpJoinQuery( Collection<String> tables );
}
