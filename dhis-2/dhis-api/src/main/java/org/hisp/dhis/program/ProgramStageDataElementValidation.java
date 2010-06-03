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

package org.hisp.dhis.program;

import java.io.Serializable;


/**
 * @author Chau Thu Tran
 * @version ProgramStageValidation.java May 6, 2010 9:34:10 AM
 */
public class ProgramStageDataElementValidation implements Serializable
{
    public static final int OPERATOR_LESS_THAN = -1;

    public static final int OPERATOR_EQUAL_TO = 0;

    public static final int OPERATOR_GREATER_THAN = 1;

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private int id;

    private String description;

    private ProgramStageDataElement leftProgramStageDataElement;

    private int operator;

    private ProgramStageDataElement rightProgramStageDataElement;

    // -------------------------------------------------------------------------
    // equals && hashCode
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        ProgramStageDataElementValidation other = (ProgramStageDataElementValidation) obj;
        if ( id != other.id )
            return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // getters && Setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }
    
    public void setDescription( String description )
    {
        this.description = description;
    }

    public int getOperator()
    {
        return operator;
    }

    public void setOperator( int operator )
    {
        this.operator = operator;
    }

    public ProgramStageDataElement getLeftProgramStageDataElement()
    {
        return leftProgramStageDataElement;
    }

    public void setLeftProgramStageDataElement( ProgramStageDataElement leftProgramStageDataElement )
    {
        this.leftProgramStageDataElement = leftProgramStageDataElement;
    }

    public ProgramStageDataElement getRightProgramStageDataElement()
    {
        return rightProgramStageDataElement;
    }

    public void setRightProgramStageDataElement( ProgramStageDataElement rightProgramStageDataElement )
    {
        this.rightProgramStageDataElement = rightProgramStageDataElement;
    }

}
