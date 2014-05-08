package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class which encapsulates a query parameter and value. Operator and filter 
 * are inherited from QueryFilter.
 * 
 * @author Lars Helge Overland
 */
public class QueryItem
{
    private IdentifiableObject item;

    private List<QueryFilter> filters = new ArrayList<QueryFilter>();
    
    private boolean numeric;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public QueryItem( IdentifiableObject item )
    {
        this.item = item;
    }

    public QueryItem( IdentifiableObject item, boolean numeric )
    {
        this.item = item;
        this.numeric = numeric;
    }
    
    public QueryItem( IdentifiableObject item, QueryOperator operator, String filter, boolean numeric )
    {
        this.item = item;
        this.numeric = numeric;
        
        if ( operator != null && filter != null )
        {
            this.filters.add( new QueryFilter( operator, filter ) );
        }
    }
    
    public QueryItem( IdentifiableObject item, List<QueryFilter> filters, boolean numeric )
    {
        this.item = item;
        this.filters = filters;
        this.numeric = numeric;
    }
    
    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------
    
    public String getItemId()
    {
        return item.getUid();
    }
    
    public String getTypeAsString()
    {
        return isNumeric() ? Double.class.getName() : String.class.getName();
    }
    
    public boolean hasFilter()
    {
        return filters != null && !filters.isEmpty();
    }

    public static List<QueryItem> getQueryItems( Collection<? extends IdentifiableObject> objects )
    {
        List<QueryItem> queryItems = new ArrayList<QueryItem>();
        
        for ( IdentifiableObject object : objects )
        {
            queryItems.add( new QueryItem( object, false ) );
        }
        
        return queryItems;
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------
    
    @Override
    public int hashCode()
    {
        return item.hashCode();
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        final QueryItem other = (QueryItem) object;
        
        return item.equals( other.getItem() );
    }

    @Override
    public String toString()
    {
        return "[Item: " + item + ", filters: " + filters + "]";
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public IdentifiableObject getItem()
    {
        return item;
    }

    public void setItem( IdentifiableObject item )
    {
        this.item = item;
    }

    public List<QueryFilter> getFilters()
    {
        return filters;
    }

    public void setFilters( List<QueryFilter> filters )
    {
        this.filters = filters;
    }

    public boolean isNumeric()
    {
        return numeric;
    }

    public void setNumeric( boolean numeric )
    {
        this.numeric = numeric;
    }
}
