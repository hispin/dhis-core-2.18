package org.hisp.dhis.jforum.manager;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.jdbc.JDBCConfiguration;
import org.hisp.dhis.jdbc.JDBCConfigurationProvider;
import org.hisp.dhis.jdbc.StatementDialect;
import org.hisp.dhis.jdbc.StatementHolder;
import org.hisp.dhis.jdbc.statement.JDBCStatementManager;

/**
 * Requires a forum called 'Data dictionary'.
 * 
 * @author Lars Helge Overland
 * @version $Id$
 */
public class JdbcJForumManager
    implements JForumManager
{
    private static final Log log = LogFactory.getLog( JdbcJForumManager.class );
    
    private static final String PREFIX_DATAELEMENT = "[DE] ";
    private static final String PREFIX_INDICATOR = "[IN] ";
        
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JDBCConfigurationProvider configurationProvider;

    public void setConfigurationProvider( JDBCConfigurationProvider configurationProvider )
    {
        this.configurationProvider = configurationProvider;
    }
    
    private JDBCStatementManager statementManager;

    public void setStatementManager( JDBCStatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }
        
    // -------------------------------------------------------------------------
    // JForumManager implementation
    // -------------------------------------------------------------------------

    public void populateJForum()
    {
        JDBCConfiguration configuration = configurationProvider.getConfiguration();
        
        Connection connection = getJForumConnection();
        
        try
        {
            Statement statement = connection.createStatement();
            
            addJForumTopics( statement );
            
            addJForumPosts( statement, configuration.getDialect() );
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to populate JForum database", ex );
        }
        finally
        {
            try
            {
                connection.close();
            }
            catch ( Exception ex )
            {   
            }
        }
    }
    
    public void populateUrls( String baseUrl )
    {
        Connection jForumConnection = getJForumConnection();
        
        StatementHolder holder = statementManager.getHolder();
        
        try
        {
            Statement statement = jForumConnection.createStatement();
            
            String sql = "SELECT topic_id, topic_title FROM jforum_topics";
            
            ResultSet resultSet = statement.executeQuery( sql );
            
            while ( resultSet.next() )
            {
                int topicId = resultSet.getInt( 1 );
                
                String topicTitle = resultSet.getString( 2 );
                
                if ( topicTitle != null && topicTitle.startsWith( PREFIX_DATAELEMENT ) )
                {                
                    sql = 
                        "UPDATE dataelement SET url='" + baseUrl + "/posts/list/" + topicId + ".page' " +
                        "WHERE name='" + topicTitle.substring( PREFIX_DATAELEMENT.length() ) + "'";
                    
                    log.debug( "Update DataElement SQL: " + sql );
                    
                    holder.getStatement().executeUpdate( sql );
                }
                else if ( topicTitle != null && topicTitle.startsWith( PREFIX_INDICATOR ) )
                {
                    sql = 
                        "UPDATE indicator SET url='" + baseUrl + "/ports/list/" + topicId + ".page' " +
                        "WHERE name='" + topicTitle.substring( PREFIX_INDICATOR.length() ) + "'";
                    
                    log.debug( "Update Indicator SQL: " + sql );
                    
                    holder.getStatement().executeUpdate( sql );
                }
            }
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to populate DHIS 2 URLs", ex );
        }
        finally
        {
            try
            {
                jForumConnection.close();
            }
            catch ( Exception ex )
            {   
            }
            
            try
            {
                holder.close();
            }
            catch ( Exception ex )
            {   
            }
        }
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Adds one topic per data element and indicator to the JForum database.
     * 
     * @param statement the statement to work on.
     * @throws SQLException
     */
    private void addJForumTopics( Statement statement )
        throws SQLException
    {
        String date = getMediumDateString( new Date() );
        
        for ( DataElement element : dataElementService.getAllDataElements() )
        {
            addJForumTopic( PREFIX_DATAELEMENT + element.getName(), date, statement );
        }
        
        for ( Indicator indicator : indicatorService.getAllIndicators() )
        {
            addJForumTopic( PREFIX_INDICATOR + indicator.getName(), date, statement );
        }
    }
    
    /**
     * Adds a topic.
     * 
     * @param title the topic title.
     * @param prefix the topic prefix.
     * @param date the topic date.
     * @param statement the statement to work on.
     * @throws SQLException
     */
    private void addJForumTopic( String title, String date, Statement statement )
        throws SQLException
    {
        if ( !topicExists( statement, title ) )
        {
            String sql = 
                "INSERT INTO jforum_topics ( forum_id, topic_title, user_id, topic_time ) " +
                "VALUES ( ( SELECT forum_id FROM jforum_forums WHERE forum_name = 'Data dictionary' LIMIT 1 ), " +
                "'" + title + "', '2', '" + date + "' )";
            
            log.debug( "Add Topic SQL: " + sql );
            
            statement.executeUpdate( sql );
        }
    }
    
    /**
     * Adds a JForum post for each data element and indicator to the JForum database.
     * 
     * @param statement the statement to work on.
     * @param dialect the DBMS dialect.
     * @throws SQLException
     */
    private void addJForumPosts( Statement statement, StatementDialect dialect )
        throws SQLException
    {
        String date = getMediumDateString( new Date() );
        
        for ( DataElement element : dataElementService.getAllDataElements() )
        {
            addJForumPost( PREFIX_DATAELEMENT + element.getName(), date, statement, dialect );
        }
        
        for ( Indicator indicator : indicatorService.getAllIndicators() )
        {
            addJForumPost( PREFIX_INDICATOR + indicator.getName(), date, statement, dialect );
        }
    }

    /**
     * Adds a JForum post.
     * 
     * @param title the post title.
     * @param date the post date.
     * @param statement the statement to work on.
     * @param dialect the DBMS dialect.
     * @throws SQLException
     */
    private void addJForumPost( String title, String date, Statement statement, StatementDialect dialect )
        throws SQLException
    {
        if ( !postExists( statement, title ) )
        {
            String sql = 
                "INSERT INTO jforum_posts ( topic_id, forum_id, user_id, post_time, poster_ip, post_edit_time ) " +
                "VALUES ( ( SELECT topic_id FROM jforum_topics WHERE topic_title = '" + title + "' LIMIT 1 ), " +
                "( SELECT forum_id FROM jforum_forums WHERE forum_name = 'Data dictionary' LIMIT 1 ), " +
                "'2', '" + date + "', '127.0.0.1', '" + date + "' )";
            
            log.debug( "Add Post SQL: " + sql );

            statement.executeUpdate( sql );
            
            final Integer postId = getLastInsertPostId( statement, dialect );
            
            sql =
                "INSERT INTO jforum_posts_text ( post_id, post_text, post_subject ) " +
                "VALUES ( '" + postId + "', '[b]" + title + "[/b]', '" + title + "' )";
            
            log.debug( "Add Post Text SQL: " + sql );

            statement.executeUpdate( sql );
            
            sql =
                "UPDATE jforum_topics SET " +
                "topic_first_post_id='" + postId + "', " + 
                "topic_last_post_id='" + postId + "' " + 
                "WHERE topic_title='" + title + "'";
            
            log.debug( "Update Topic SQL: " + sql );
            
            statement.executeUpdate( sql );
        }
    }
    
    /**
     * Checks if a topic with the given title already exists.
     * 
     * @param statement the statement to work on.
     * @param title the topic title.
     * @return true if a topic with the given title already exists, false if not.
     * @throws SQLException
     */
    private boolean topicExists( Statement statement, String title )
        throws SQLException
    {
        String sql = "SELECT COUNT(*) FROM jforum_topics WHERE topic_title='" + title + "'";

        ResultSet resultSet = statement.executeQuery( sql );
        
        resultSet.next();
        
        return resultSet.getInt( 1 ) > 0;
    }

    /**
     * Checks if a post with the given subject already exists.
     * 
     * @param statement the statement to work on.
     * @param subject the post subject.
     * @return true if a post with the given subject already exists, false if not.
     * @throws SQLException
     */
    private boolean postExists( Statement statement, String subject )
        throws SQLException
    {
        String sql = "SELECT COUNT(*) FROM jforum_posts_text WHERE post_subject='" + subject + "'";

        ResultSet resultSet = statement.executeQuery( sql );
        
        resultSet.next();
        
        return resultSet.getInt( 1 ) > 0;
    }
    
    /**
     * Retrieves the last auto-increment identifier generated by the DBMS.
     * 
     * @param statement the statement to work on.
     * @param dialect the DBMS dialect.
     * @return the last auto-increment identifier generated by the DBMS.
     * @throws SQLException
     */
    private Integer getLastInsertPostId( Statement statement, StatementDialect dialect )
        throws SQLException
    {
        String sql = null;
        
        if ( dialect.equals( StatementDialect.MYSQL ) || dialect.equals( StatementDialect.H2 ) )
        {
            sql = "SELECT LAST_INSERT_ID()";
        }
        else
        {
            sql = "SELECT currval('jforum_posts_seq')";
        }
        
        ResultSet resultSet = statement.executeQuery( sql );
        
        return resultSet.next() ? resultSet.getInt( 1 ) : 0;
    }
    
    /**
     * Returns a connection to the JForum database.
     * 
     * @return a connection to the JForum database.
     */
    private Connection getJForumConnection()
    {
        try
        {
            JDBCConfiguration configuration = configurationProvider.getConfiguration();
            
            Class.forName( configuration.getDriverClass() );
            
            Connection connection = DriverManager.getConnection( 
                getJForumConnectionUrl( configuration.getConnectionUrl(), configuration.getDialect() ),
                configuration.getUsername(),
                configuration.getPassword() );
            
            return connection;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to create connection", ex );
        }
    }
    
    /**
     * JForum runs on MySQL, PostgreSQL, and HSQLDB and is assumed to run on the 
     * same DBMS and same user as DHIS 2 on database called "jforum". This 
     * method substitutes the DHIS 2 database name in the connection URL with 
     * "jforum".
     * 
     * @param url the connection URL for DHIS 2.
     * @return the connection URL for JForum.
     */
    private String getJForumConnectionUrl( String dhis2Url, StatementDialect dialect )
    {
        String url = null;
        
        if ( dialect.equals( StatementDialect.MYSQL ) )
        {
            url = dhis2Url.substring( 0, dhis2Url.lastIndexOf( "/" ) + 1 );
        }
        else
        {
            url = dhis2Url.substring( 0, dhis2Url.lastIndexOf( ":" ) + 1 );
        }
        
        url += "jforum";
        
        log.debug( "Using JForum connection URL: " + url );
        
        return url;
    }
}
