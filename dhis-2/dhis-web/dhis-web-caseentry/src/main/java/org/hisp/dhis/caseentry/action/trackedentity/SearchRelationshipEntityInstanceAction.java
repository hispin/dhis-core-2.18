package org.hisp.dhis.caseentry.action.trackedentity;

/*
 * Copyright (c) 2004-2013, University of Oslo
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
import java.util.Iterator;

import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ SearchRelationshipEntityInstanceAction.java May 13, 2011 2:38:12 PM $
 * 
 */
public class SearchRelationshipEntityInstanceAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityInstanceService entityInstanceService;

    public void setEntityInstanceService( TrackedEntityInstanceService entityInstanceService )
    {
        this.entityInstanceService = entityInstanceService;
    }

    private TrackedEntityAttributeService attributeService;

    public void setAttributeService( TrackedEntityAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private TrackedEntityAttributeValueService attributeValueService;

    public void setAttributeValueService( TrackedEntityAttributeValueService attributeValueService )
    {
        this.attributeValueService = attributeValueService;
    }

    private RelationshipService relationshipService;

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer entityInstanceId;

    public void setEntityInstanceId( Integer entityInstanceId )
    {
        this.entityInstanceId = entityInstanceId;
    }

    private String searchText;

    public void setSearchText( String searchText )
    {
        this.searchText = searchText;
    }

    public String getSearchText()
    {
        return searchText;
    }

    private Integer searchingAttributeId;

    public Integer getSearchingAttributeId()
    {
        return searchingAttributeId;
    }

    public void setSearchingAttributeId( Integer searchingAttributeId )
    {
        this.searchingAttributeId = searchingAttributeId;
    }

    private Collection<TrackedEntityInstance> entityInstances = new ArrayList<TrackedEntityInstance>();

    public Collection<TrackedEntityInstance> getEntityInstances()
    {
        return entityInstances;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( searchText != null && searchText.length() > 0 )
            searchText = searchText.trim();

        int index = searchText.indexOf( ' ' );

        if ( index != -1 && index == searchText.lastIndexOf( ' ' ) )
        {
            String[] keys = searchText.split( " " );
            searchText = keys[0] + "  " + keys[1];
        }

        if ( searchText != null && !searchText.isEmpty() )
        {
            if ( searchingAttributeId != null )
            {
                TrackedEntityAttribute entityInstanceAttribute = attributeService.getTrackedEntityAttribute( searchingAttributeId );

                Collection<TrackedEntityAttributeValue> matching = attributeValueService.searchTrackedEntityAttributeValue( 
                    entityInstanceAttribute, searchText );

                for ( TrackedEntityAttributeValue entityInstanceAttributeValue : matching )
                {
                    entityInstances.add( entityInstanceAttributeValue.getEntityInstance() );
                }

            }
        }
        if ( entityInstances != null && !entityInstances.isEmpty() )
        {
            TrackedEntityInstance entityInstance = entityInstanceService.getTrackedEntityInstance( entityInstanceId );

            entityInstances.remove( entityInstance );

            Collection<Relationship> relationships = relationshipService.getRelationshipsForTrackedEntityInstance( entityInstance );

            if ( relationships != null )
            {
                Iterator<Relationship> iter = relationships.iterator();

                while ( iter.hasNext() )
                {
                    Relationship relationship = iter.next();
                    entityInstances.remove( relationship.getEntityInstanceA() );
                    entityInstances.remove( relationship.getEntityInstanceB() );
                }
            }
        }

        return SUCCESS;
    }

}
