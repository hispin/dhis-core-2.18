package org.hisp.dhis.sqlview;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.schema.annotation.PropertyRange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.collect.Sets;

/**
 * @author Dang Duy Hieu
 */
@JacksonXmlRootElement( localName = "sqlView", namespace = DxfNamespaces.DXF_2_0 )
public class SqlView
    extends BaseIdentifiableObject
{
    public static final String PREFIX_VIEWNAME = "_view";

    public static final Set<String> PROTECTED_TABLES = Sets.newHashSet( "users", "userinfo", 
        "trackedentityinstance", "trackedentityattribute", "trackedentityattributevalue", "relationship" );
    
    public static final Set<String> ILLEGAL_KEYWORDS = Sets.newHashSet( "delete", "alter", "update", 
        "create", "drop", "commit", "createdb", "createuser", "insert", "rename", "replace", "restore", "write" );

    private static final String CRITERIA_SEP = ":";
    private static final String REGEX_SEP = "|";
    
    // -------------------------------------------------------------------------
    // Variables
    // -------------------------------------------------------------------------

    private String description;

    private String sqlQuery;

    private boolean query;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public SqlView()
    {
    }

    public SqlView( String name, String sqlQuery, boolean query )
    {
        this.name = name;
        this.sqlQuery = sqlQuery;
        this.query = query;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public String getViewName()
    {
        final Pattern p = Pattern.compile( "\\W" );

        String input = name;

        String[] items = p.split( input.trim().replaceAll( "_", "" ) );

        input = "";

        for ( String s : items )
        {
            input += s.isEmpty() ? "" : ("_" + s);
        }

        return PREFIX_VIEWNAME + input.toLowerCase();
    }

    public static Map<String, String> getCriteria( Set<String> params )
    {
        Map<String, String> map = new HashMap<>();

        if ( params != null )
        {
            for ( String param : params )
            {
                if ( param != null && param.split( CRITERIA_SEP ).length == 2 )
                {
                    String[] criteria = param.split( CRITERIA_SEP );
                    String filter = criteria[0];
                    String value = criteria[1];

                    if ( StringUtils.isAlphanumericSpace( filter ) && StringUtils.isAlphanumericSpace( value ) )
                    {
                        map.put( filter, value );
                    }
                }
            }
        }

        return map;
    }

    public static String getProtectedTablesRegex()
    {
        StringBuffer regex = new StringBuffer( "^.*?(\"|'|`|\\s|^)(" );

        for ( String table : PROTECTED_TABLES )
        {
            regex.append( table ).append( REGEX_SEP );
        }

        regex.delete( regex.length() - 1, regex.length() );
        
        return regex.append( ")(\"|'|`|\\s|$).*$" ).toString();
    }
    
    public static String getIllegalKeywordsRegex()
    {
        StringBuffer regex = new StringBuffer( "^.*?(\\s|^)(" );
        
        for ( String word : ILLEGAL_KEYWORDS )
        {
            regex.append( word ).append( REGEX_SEP );
        }
        
        regex.delete( regex.length() - 1, regex.length() );
        
        return regex.append( ")(\\s|$).*$" ).toString();
    }
    
    public SqlView cleanSqlQuery()
    {
        sqlQuery = sqlQuery.
            replaceAll( "\\s*;\\s+", ";" ).
            replaceAll( ";+", ";" ).
            replaceAll( "\\s+", " " ).trim();
        
        return this;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    @PropertyRange( min = 2 )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getSqlQuery()
    {
        return sqlQuery;
    }

    public void setSqlQuery( String sqlQuery )
    {
        this.sqlQuery = sqlQuery;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isQuery()
    {
        return query;
    }

    public void setQuery( boolean query )
    {
        this.query = query;
    }
        

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            SqlView sqlView = (SqlView) other;

            description = sqlView.getDescription() == null ? description : sqlView.getDescription();
            sqlQuery = sqlView.getSqlQuery() == null ? sqlQuery : sqlView.getSqlQuery();
            query = sqlView.isQuery();
        }
    }
}
