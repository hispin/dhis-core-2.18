package org.hisp.dhis.rt.report;

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

public class OrgUnitSpesificElement
    extends Element
{
    private int organisationUnitId;
    
    private String organisationUnitName;
    
    public OrgUnitSpesificElement( String type, int elementId, String elementName, 
        int organisationUnitId, String organisationUnitName )
    {
        super( type, elementId, elementName );
        
        this.organisationUnitId = organisationUnitId;
        this.organisationUnitName = organisationUnitName;
    }

    public int getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
        
    public String getOrganisationUnitName()
    {
        return organisationUnitName;
    }

    public void setOrganisationUnitName( String organisationUnitName )
    {
        this.organisationUnitName = organisationUnitName;
    }

    public String getElementKey()
    {
        return super.getType() + SEPARATOR + super.getElementName() + SEPARATOR + this.organisationUnitName;      
    }
    
    public boolean equals( Object object )
    {
        if( this == object )
        {
            return true;
        }
        
        if( ( object == null) || ( object.getClass() != this.getClass() ) )
        {
            return false;
        }
        
        OrgUnitSpesificElement element = (OrgUnitSpesificElement) object;
        
        return ( super.getId() == element.getId() && super.getType() == element.getType() && 
            super.getElementId() == element.getElementId() && this.organisationUnitId == element.getOrganisationUnitId() );            
    }

}
