package org.hisp.dhis.chart.hibernate;

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

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartStore;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class HibernateChartStore
    extends HibernateIdentifiableObjectStore<Chart> implements ChartStore
{
    public Chart getByTitle( String title )
    {
        return getObject( Restrictions.eq( "name", title ) );
    }

    public int getChartCount()
    {
        return getCount();
    }

    @SuppressWarnings("unchecked")
    public Collection<Chart> getChartsBetween( int first, int max )
    {
        Criteria criteria = getCriteria();
        criteria.addOrder( Order.asc( "name" ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public Collection<Chart> getChartsBetweenByName( String name, int first, int max )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.ilike( "name", "%" + name + "%" ) );
        criteria.addOrder( Order.asc( "name" ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );
        return criteria.list();
    }

    public int getChartCountByName( String name )
    {
        Criteria criteria = getCriteria();
        criteria.setProjection( Projections.rowCount() );
        criteria.add( Restrictions.ilike( "name", "%" + name + "%" ) );
        return ((Number) criteria.uniqueResult()).intValue();
    }    
}
