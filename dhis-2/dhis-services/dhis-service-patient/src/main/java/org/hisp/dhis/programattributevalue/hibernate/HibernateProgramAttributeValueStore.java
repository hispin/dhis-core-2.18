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

package org.hisp.dhis.programattributevalue.hibernate;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.program.ProgramAttribute;
import org.hisp.dhis.program.ProgramAttributeOption;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.programattributevalue.ProgramAttributeValue;
import org.hisp.dhis.programattributevalue.ProgramAttributeValueStore;

/**
 * @author Chau Thu Tran
 * 
 * @version HibernateProgramAttributeValueStore.java Oct 31, 2010 11:20:19 PM
 */
public class HibernateProgramAttributeValueStore
    extends HibernateGenericStore<ProgramAttributeValue>
    implements ProgramAttributeValueStore
{
    public void saveVoid( ProgramAttributeValue programAttributeValue )
    {
        sessionFactory.getCurrentSession().save( programAttributeValue );
    }

    public int delete( ProgramAttribute programAttribute )
    {
        Query query = getQuery( "delete from ProgramAttributeValue where programAttribute = :programAttribute" );
        query.setEntity( "programAttribute", programAttribute );

        return query.executeUpdate();
    }

    public int delete( ProgramInstance programInstance )
    {
        Query query = getQuery( "delete from ProgramAttributeValue where programInstance = :programInstance" );
        query.setEntity( "programInstance", programInstance );

        return query.executeUpdate();
    }

    public ProgramAttributeValue get( ProgramInstance programInstance, ProgramAttribute programAttribute )
    {
        return (ProgramAttributeValue) getCriteria( Restrictions.eq( "programInstance", programInstance ),
            Restrictions.eq( "programAttribute", programAttribute ) ).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public Collection<ProgramAttributeValue> get( ProgramInstance programInstance )
    {
        return getCriteria( Restrictions.eq( "programInstance", programInstance ) ).list();
    }

    @SuppressWarnings("unchecked")
    public Collection<ProgramAttributeValue> get( ProgramAttribute programAttribute )
    {
        return getCriteria( Restrictions.eq( "programAttribute", programAttribute ) ).list();
    }

    @SuppressWarnings("unchecked")
    public Collection<ProgramAttributeValue> search( ProgramAttribute programAttribute, String searchText )
    {
        return getCriteria( Restrictions.eq( "programAttribute", programAttribute ),
            Restrictions.ilike( "value", "%" + searchText + "%" ) ).list();
    }

    @Override
    public int countByProgramAttributeoption( ProgramAttributeOption attributeOption )
    {
        Number rs = (Number) getCriteria( Restrictions.eq( "programAttributeOption", attributeOption ) ).setProjection(
            Projections.rowCount() ).uniqueResult();
        return rs != null ? rs.intValue() : 0;
    }
}
