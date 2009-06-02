package org.hisp.dhis.customvalue.hibernate;

/*
 * Copyright (c) 2004-2009, University of Oslo
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

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.customvalue.CustomValue;
import org.hisp.dhis.customvalue.CustomValueStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.hibernate.HibernateSessionManager;

/**
 * @author Latifov Murodillo Abdusamadovich
 * 
 * @version $Id$
 */
public class HibernateCustomValueStore
    implements CustomValueStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }
    
    // -------------------------------------------------------------------------
    // CustomValueStore implementation
    // -------------------------------------------------------------------------

    public int addCustomValue( CustomValue customValue )
    {
        Session session = sessionManager.getCurrentSession();

        Integer id = (Integer) session.save( customValue );

        return id;
    }

    public void deleteCustomValue( CustomValue customValue )
    {
        Session session = sessionManager.getCurrentSession();

        session.delete( customValue );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<CustomValue> getCustomValuesByDataSet( DataSet dataSet )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomValue.class );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<CustomValue> getCustomValuesByCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomValue.class );
        criteria.add( Restrictions.eq( "optionCombo", categoryCombo ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<CustomValue> getCustomValuesByDataElement( DataElement dataElement )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

    public CustomValue getCustomValuesById( Integer id )
    {
        Session session = sessionManager.getCurrentSession();

        return (CustomValue) session.get( CustomValue.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<CustomValue> getCustomValues( DataSet dataSet, DataElement dataElement,
        DataElementCategoryOptionCombo dataElementCategoryOptionCombo )
    {
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomValue.class );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "optionCombo", dataElementCategoryOptionCombo ) );

        return criteria.list();
    }

	@SuppressWarnings("unchecked")
	public Collection<CustomValue> findCustomValues(String searchValue) 
	{
        Session session = sessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomValue.class );
//        criteria.setProjection(Projections.distinct(Projections.property("customValue")));
        criteria.add( Restrictions.like( "customValue", searchValue, MatchMode.ANYWHERE ) );

        return criteria.list();
	}
}
