/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.programattributevalue;

import java.io.Serializable;

import org.hisp.dhis.program.ProgramAttribute;
import org.hisp.dhis.program.ProgramAttributeOption;
import org.hisp.dhis.program.ProgramInstance;

/**
 * @author Chau Thu Tran
 * @version $Id ProgramAttributeValue.java 2010-10-30 19:32:09Z $
 */
public class ProgramAttributeValue
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6942663950035085618L;

    private ProgramAttribute programAttribute;

    private ProgramInstance programInstance;

    private String value;

    private ProgramAttributeOption programAttributeOption;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramAttributeValue()
    {

    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((programAttribute == null) ? 0 : programAttribute.hashCode());
        result = prime * result + ((programInstance == null) ? 0 : programInstance.hashCode());
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
        ProgramAttributeValue other = (ProgramAttributeValue) obj;
        if ( programAttribute == null )
        {
            if ( other.programAttribute != null )
                return false;
        }
        else if ( !programAttribute.equals( other.programAttribute ) )
            return false;
        if ( programInstance == null )
        {
            if ( other.programInstance != null )
                return false;
        }
        else if ( !programInstance.equals( other.programInstance ) )
            return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public ProgramAttribute getProgramAttribute()
    {
        return programAttribute;
    }

    public ProgramAttributeOption getProgramAttributeOption()
    {
        return programAttributeOption;
    }

    public void setProgramAttributeOption( ProgramAttributeOption programAttributeOption )
    {
        this.programAttributeOption = programAttributeOption;
    }

    public void setProgramAttribute( ProgramAttribute programAttribute )
    {
        this.programAttribute = programAttribute;
    }

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    public void setProgramInstance( ProgramInstance programInstance )
    {
        this.programInstance = programInstance;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

}
