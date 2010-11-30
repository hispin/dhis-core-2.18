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

package org.hisp.dhis.programattributevalue;

import java.util.Collection;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramAttribute;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.system.deletion.DeletionHandler;

/**
 * @author Chau Thu Tran
 * @version $Id ProgramAttributeValueDeletionHandler.java 2010-11-30 14:06:12Z $
 */

public class ProgramAttributeValueDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramAttributeValueService programAttributeValueService;

    public void setProgramAttributeValueService( ProgramAttributeValueService programAttributeValueService )
    {
        this.programAttributeValueService = programAttributeValueService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return ProgramAttributeValue.class.getSimpleName();
    }

    @Override
    public void deleteProgram( Program program )
    {
        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( program );

        for ( ProgramInstance programInstance : programInstances )
        {
            programAttributeValueService.deleteProgramAttributeValues( programInstance );
        }
    }

    public void deleteProgramAttribute( ProgramAttribute programAttribute )
    {
        Collection<ProgramAttributeValue> attributeValues = programAttributeValueService
            .getProgramAttributeValues( programAttribute );

        if ( attributeValues != null && attributeValues.size() > 0 )
        {
            for ( ProgramAttributeValue attributeValue : attributeValues )
            {
                programAttributeValueService.deleteProgramAttributeValue( attributeValue );
            }
        }
    }
}
