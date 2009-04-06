package org.hisp.dhis.mapping;

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

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */

public class MapOrganisationUnitRelation
{
    private int id;

    private Map map;

    private OrganisationUnit organisationUnit;

    private String featureId;
    
    public MapOrganisationUnitRelation()
    {
    }

    public MapOrganisationUnitRelation( Map map, OrganisationUnit organisationUnit, String featureId )
    {
        this.map = map;
        this.organisationUnit = organisationUnit;
        this.featureId = featureId;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;

        int result = 1;

        result = prime * result + ( ( featureId == null ) ? 0 : featureId.hashCode() );
        result = prime * result + ( ( map == null ) ? 0 : map.hashCode() );
        result = prime * result + ( ( organisationUnit == null ) ? 0 : organisationUnit.hashCode() );

        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        MapOrganisationUnitRelation other = ( MapOrganisationUnitRelation ) obj;
        if ( featureId == null )
        {
            if ( other.featureId != null )
                return false;
        }
        else if ( !featureId.equals( other.featureId ) )
            return false;
        if ( map == null )
        {
            if ( other.map != null )
                return false;
        }
        else if ( !map.equals( other.map ) )
            return false;
        if ( organisationUnit == null )
        {
            if ( other.organisationUnit != null )
                return false;
        }
        else if ( !organisationUnit.equals( other.organisationUnit ) )
            return false;
        return true;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Map getMap()
    {
        return map;
    }

    public void setMap( Map map )
    {
        this.map = map;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public String getFeatureId()
    {
        return featureId;
    }

    public void setFeatureId( String featureId )
    {
        this.featureId = featureId;
    }
}
