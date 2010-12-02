package org.hisp.dhis.organisationunit;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The purpose of the OrganisationUnitHierarchy object is to store the parent-child relationship of the
 * registered organisation units together with a timestamp. The parent-child relationships are
 * stored in a Map, where the key column stores the organisation unit id and the value column
 * stores the id of the parent organisation unit.
 * 
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitHierarchy.java 2869 2007-02-20 14:26:09Z andegje $
 */
public class OrganisationUnitHierarchy
{
    private Map<Integer, Collection<Integer>> preparedRelationships = new HashMap<Integer, Collection<Integer>>();
    
    private Collection<OrganisationUnitRelationship> relationships;
    
    public OrganisationUnitHierarchy( Collection<OrganisationUnitRelationship> relationships )
    {
        this.relationships = relationships;
    }
    
    public OrganisationUnitHierarchy prepareChildren( Collection<OrganisationUnit> parents )
    {
        for ( OrganisationUnit unit : parents )
        {
            prepareChildren( unit );
        }
        
        return this;
    }
    
    public OrganisationUnitHierarchy prepareChildren( OrganisationUnit unit )
    {
        preparedRelationships.put( unit.getId(), getChildren( unit.getId() ) );
        
        return this;
    }
    
    public Collection<Integer> getChildren( int parentId )
    {
        if ( preparedRelationships.containsKey( parentId ) )
        {
            return preparedRelationships.get( parentId );
        }
        
        List<Integer> children = new ArrayList<Integer>();
        
        children.add( 0, parentId );

        int childCounter = 1;
        
        for ( int i = 0; i < childCounter; i++ )
        {
            for ( OrganisationUnitRelationship entry : relationships )
            {
                if ( entry.getParentId() == children.get( i ) )
                {
                    children.add( childCounter++, entry.getChildId() );
                }
            }
        }
        
        return children;
    }
    
    public Collection<Integer> getChildren( Collection<Integer> parentIds )
    {
        Set<Integer> children = new HashSet<Integer>();
        
        for ( Integer id : parentIds )
        {
            children.addAll( getChildren( id ) );
        }
        
        return children;
    }
}

