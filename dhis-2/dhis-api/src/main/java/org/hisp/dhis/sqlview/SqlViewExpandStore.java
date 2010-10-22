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

    Collection<String> getAllResourceProperties( String resourceTableName );

    boolean isViewTableExists( String viewTableName );

    boolean createView( SqlView sqlViewInstance );

    void dropView( Object object );

    void setUpDataSqlViewTable( SqlViewTable sqlViewTable, String viewTableName );

    String setUpViewTableName( String input );

    String testSqlGrammar( String sql );

    String setUpJoinQuery( Collection<String> tables );
}
