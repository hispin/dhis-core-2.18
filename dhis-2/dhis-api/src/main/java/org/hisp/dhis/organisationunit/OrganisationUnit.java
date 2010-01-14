package org.hisp.dhis.organisationunit;

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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.source.Source;

/**
 * @author Kristian Nordal
 * @version $Id: OrganisationUnit.java 6251 2008-11-10 14:37:05Z larshelg $
 */
public class OrganisationUnit
    extends Source
{
    private Set<OrganisationUnit> children = new HashSet<OrganisationUnit>();

    private OrganisationUnit parent;
    
    private Date openingDate;

    private Date closedDate;

    private boolean active;
    
    private String type;

    private String comment;
    
    private String geoCode;
    
    private String polygonCoordinates;
    
    private String latitude;
    
    private String longitude;
    
    private String url;

    private Date lastUpdated;

    private Set<OrganisationUnitGroup> groups = new HashSet<OrganisationUnitGroup>();
    
    private transient int level;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public OrganisationUnit()
    {
    }

    public OrganisationUnit( String name )
    {
        this.name = name;
    }
    
    /**
     * @param name
     * @param shortName
     * @param organisationUnitCode
     * @param openingDate
     * @param closedDate
     * @param active
     * @param comment
     */
    public OrganisationUnit( String name, String shortName, String code, Date openingDate,
        Date closedDate, boolean active, String comment )
    {
        this.name = name;
        this.shortName = shortName;
        this.code = code;
        this.openingDate = openingDate;
        this.closedDate = closedDate;
        this.active = active;
        this.comment = comment;
    }

    /**
     * @param name
     * @param parent
     * @param shortName
     * @param organisationUnitCode
     * @param openingDate
     * @param closedDate
     * @param active
     * @param comment
     */
    public OrganisationUnit( String name, OrganisationUnit parent, String shortName, String code,
        Date openingDate, Date closedDate, boolean active, String comment )
    {
        this.name = name;
        this.parent = parent;
        this.shortName = shortName;
        this.code = code;
        this.openingDate = openingDate;
        this.closedDate = closedDate;
        this.active = active;
        this.comment = comment;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean hasCoordinates()
    {
        return latitude != null && latitude.trim().length() > 0 && longitude != null && longitude.trim().length() > 0;
    }
    
    public boolean hasPolygonCoordinates()
    {
        return polygonCoordinates != null && polygonCoordinates.trim().length() > 0;
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof OrganisationUnit) )
        {
            return false;
        }

        final OrganisationUnit other = (OrganisationUnit) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Set<OrganisationUnit> getChildren()
    {
        return children;
    }

    public void setChildren( Set<OrganisationUnit> children )
    {
        this.children = children;
    }

    public OrganisationUnit getParent()
    {
        return parent;
    }

    public void setParent( OrganisationUnit parent )
    {
        this.parent = parent;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }
    
    public String getAlternativeName()
    {
        return getShortName();
    }
    
    public void setAlternativeName( String alternativeName )
    {
        throw new UnsupportedOperationException( "Cannot set alternativename on OrganisationUnit: " + alternativeName );
    }
    
    public Date getOpeningDate()
    {
        return openingDate;
    }

    public void setOpeningDate( Date openingDate )
    {
        this.openingDate = openingDate;
    }

    public Date getClosedDate()
    {
        return closedDate;
    }

    public void setClosedDate( Date closedDate )
    {
        this.closedDate = closedDate;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    public String getGeoCode()
    {
        return geoCode;
    }

    public void setGeoCode( String geoCode )
    {
        this.geoCode = geoCode;
    }

    public String getPolygonCoordinates()
    {
        return polygonCoordinates;
    }

    public void setPolygonCoordinates( String polygonCoordinates )
    {
        this.polygonCoordinates = polygonCoordinates;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude( String latitude )
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude( String longitude )
    {
        this.longitude = longitude;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( Date lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    public Set<OrganisationUnitGroup> getGroups()
    {
        return groups;
    }

    public void setGroups( Set<OrganisationUnitGroup> groups )
    {
        this.groups = groups;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel( int level )
    {
        this.level = level;
    }
}
