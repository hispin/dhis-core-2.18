package org.hisp.dhis.caseaggregation.hibernate;

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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.caseaggregation.CaseAggregationMapping;
import org.hisp.dhis.caseaggregation.CaseAggregationMappingStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.patientdatavalue.PatientDataValue;

public class HibernateCaseAggregationMappingStore
    implements CaseAggregationMappingStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // CaseAggregationMapping
    // -------------------------------------------------------------------------

    @Override
    public void addCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( caseAggregationMapping );
    }

    @Override
    public void deleteCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( caseAggregationMapping );
    }

    @Override
    public void updateCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( caseAggregationMapping );
    }

    @Override
    public CaseAggregationMapping getCaseAggregationMappingByOptionCombo( DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CaseAggregationMapping.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );

        return (CaseAggregationMapping) criteria.uniqueResult();
    }

    public int executeMappingQuery( String sQuery )
    {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery( sQuery );
        
        Iterator<?>  it = query.iterate(); 
        int rs = 0;
        while( it.hasNext() )
        {
            Object obj = it.next();
            System.out.println("result object: "+obj);
            rs = NumberUtils.toInt( obj.toString(), 0 );
            
        }
        return rs;
    }

    @SuppressWarnings("unchecked")
    public List<PatientDataValue> executeMappingQueryForListPatientDataValue( String squery )
    {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery( squery );
        
        return query.list(); 
    }

}
