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

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.BaseIdentifiableObjectXmlAdapter;
import org.hisp.dhis.common.adapter.BaseNameableObjectXmlAdapter;
import org.hisp.dhis.common.adapter.JsonIdentifiableObjectSerializer;
import org.hisp.dhis.common.adapter.JsonNameableObjectSetSerializer;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kristian Nordal
 */
@XmlRootElement( name = "organisationUnitGroup", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class OrganisationUnitGroup extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -1131637847640209166L;

    private Set<OrganisationUnit> members = new HashSet<OrganisationUnit>();

    private OrganisationUnitGroupSet groupSet;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public OrganisationUnitGroup()
    {
    }

    public OrganisationUnitGroup( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addOrganisationUnit( OrganisationUnit unit )
    {
        members.add( unit );
        unit.getGroups().add( this );
    }

    public void removeOrganisationUnit( OrganisationUnit unit )
    {
        members.remove( unit );
        unit.getGroups().remove( this );
    }

    public void updateOrganisationUnits( Set<OrganisationUnit> updates )
    {
        for ( OrganisationUnit unit : new HashSet<OrganisationUnit>( members ) )
        {
            if ( !updates.contains( unit ) )
            {
                removeOrganisationUnit( unit );
            }
        }

        for ( OrganisationUnit unit : updates )
        {
            addOrganisationUnit( unit );
        }
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
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

        if ( !(o instanceof OrganisationUnitGroup) )
        {
            return false;
        }

        final OrganisationUnitGroup other = (OrganisationUnitGroup) o;

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

    @XmlElementWrapper( name = "members" )
    @XmlElement( name = "member" )
    @XmlJavaTypeAdapter( BaseNameableObjectXmlAdapter.class )
    @JsonSerialize( using = JsonNameableObjectSetSerializer.class )
    public Set<OrganisationUnit> getMembers()
    {
        return members;
    }

    public void setMembers( Set<OrganisationUnit> members )
    {
        this.members = members;
    }

    @XmlElement
    @XmlJavaTypeAdapter( BaseIdentifiableObjectXmlAdapter.class )
    @JsonSerialize( using = JsonIdentifiableObjectSerializer.class )
    public OrganisationUnitGroupSet getGroupSet()
    {
        return groupSet;
    }

    public void setGroupSet( OrganisationUnitGroupSet groupSet )
    {
        this.groupSet = groupSet;
    }
}
