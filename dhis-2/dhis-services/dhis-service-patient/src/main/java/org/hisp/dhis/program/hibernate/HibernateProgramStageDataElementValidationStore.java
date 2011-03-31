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

package org.hisp.dhis.program.hibernate;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageDataElementValidation;
import org.hisp.dhis.program.ProgramStageDataElementValidationStore;

/**
 * @author Chau Thu Tran
 * @version HibernateProgramStageDataElementValidationStore.java May 10, 2010
 *          11:06:20 AM
 */
public class HibernateProgramStageDataElementValidationStore
    extends HibernateGenericStore<ProgramStageDataElementValidation>
    implements ProgramStageDataElementValidationStore
{
    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageDataElementValidation> getProgramStageDataElementValidations( Program program )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session
            .createQuery( "from ProgramStageDataElementValidation c where c.leftProgramStageDataElement.programStage.program.id = :program" );

        query.setInteger( "program", program.getId() );

        return query.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageDataElementValidation> getProgramStageDataElementValidations(
        ProgramStageDataElement element )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session
            .createQuery( "from ProgramStageDataElementValidation c where "
                + "( c.leftProgramStageDataElement.programStage.id = :programStageId and c.leftProgramStageDataElement.dataElement.id = :dataElementId ) or "
                + "( c.rightProgramStageDataElement.programStage.id = :programStageId and c.rightProgramStageDataElement.dataElement.id = :dataElementId )" );

        query.setInteger( "programStageId", element.getProgramStage().getId() );
        query.setInteger( "dataElementId", element.getDataElement().getId() );

        return query.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<ProgramStageDataElementValidation> getProgramStageDataElementValidations(
        ProgramStage programStage )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session
            .createQuery( "from ProgramStageDataElementValidation c where "
                + "c.leftProgramStageDataElement.programStage.id = :programStageId or "
                + "c.rightProgramStageDataElement.programStage.id = :programStageId" );

        query.setInteger( "programStageId", programStage.getId() );

        return query.list();
    }
}
