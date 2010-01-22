package org.hisp.dhis.dataelement.hibernate;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementStore;
import org.hisp.dhis.hierarchy.HierarchyViolationException;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: HibernateDataElementStore.java 5243 2008-05-25 10:18:58Z
 *          larshelg $
 */
public class HibernateDataElementStore
    implements DataElementStore
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
    // DataElement
    // -------------------------------------------------------------------------

    public int addDataElement( DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( dataElement );
    }

    public void updateDataElement( DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( dataElement );
    }

    public void deleteDataElement( DataElement dataElement )
        throws HierarchyViolationException
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dataElement );
    }

    public DataElement getDataElement( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (DataElement) session.get( DataElement.class, id );
    }

    public DataElement getDataElement( String uuid )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "uuid", uuid ) );

        return (DataElement) criteria.uniqueResult();
    }

    public DataElement getDataElementByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (DataElement) criteria.uniqueResult();
    }

    public DataElement getDataElementByAlternativeName( String alternativeName )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "alternativeName", alternativeName ) );

        return (DataElement) criteria.uniqueResult();
    }

    public DataElement getDataElementByShortName( String shortName )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "shortName", shortName ) );

        return (DataElement) criteria.uniqueResult();
    }

    public DataElement getDataElementByCode( String code )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "code", code ) );

        return (DataElement) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getAllDataElements()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.setCacheable( true );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getAggregateableDataElements()
    {
        Session session = sessionFactory.getCurrentSession();

        Set<String> types = new HashSet<String>();

        types.add( DataElement.VALUE_TYPE_INT );
        types.add( DataElement.VALUE_TYPE_BOOL );

        Criteria criteria = session.createCriteria( DataElement.class );

        criteria.add( Restrictions.in( "type", types ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getAllActiveDataElements()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "active", true ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getDataElementsByAggregationOperator( String aggregationOperator )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "aggregationOperator", aggregationOperator ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getDataElementsByType( String type )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "type", type ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getDataElementsByDomainType( String domainType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "domainType", domainType ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getDataElementByCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "categoryCombo", categoryCombo ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getDataElementsWithGroupSets()
    {
        final String sql = "from DataElement d where d.groupSets.size > 0";

        Query query = sessionFactory.getCurrentSession().createQuery( sql );

        return query.list();
    }

    public void setZeroIsSignificant4DataElements( Collection<Integer> dataElementIds, boolean zeroIsSignificant )
    {
        for ( Integer id : dataElementIds )
        {
            String sql = "update DataElement d set d.zeroIsSignificant=:zeroIsSignificant where d.id=:id";
            
            Query query = sessionFactory.getCurrentSession().createQuery( sql );
            
            query.setParameter( "zeroIsSignificant", zeroIsSignificant );
            
            query.setParameter( "id", id );          
            
            query.executeUpdate();
            
        }
    }
    
    @SuppressWarnings("unchecked")
    public Collection<DataElement> getDataElementsByZeroIsSignificant( boolean zeroIsSignificant )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElement.class );
        criteria.add( Restrictions.eq( "zeroIsSignificant", zeroIsSignificant ) );

        return criteria.list();
    }

    // -------------------------------------------------------------------------
    // CalculatedDataElement
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public Collection<CalculatedDataElement> getAllCalculatedDataElements()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CalculatedDataElement.class );

        return criteria.list();
    }

    public CalculatedDataElement getCalculatedDataElementByDataElement( DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Set<Integer> dataElementIds = new HashSet<Integer>();
        dataElementIds.add( dataElement.getId() );

        Criteria criteria = session.createCriteria( CalculatedDataElement.class ).createCriteria( "expression" )
            .createCriteria( "dataElementsInExpression" ).add( Restrictions.in( "id", dataElementIds ) );

        return (CalculatedDataElement) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<CalculatedDataElement> getCalculatedDataElementsByDataElements(
        Collection<DataElement> dataElements )
    {
        Session session = sessionFactory.getCurrentSession();

        Set<Integer> dataElementIds = new HashSet<Integer>();

        for ( DataElement dataElement : dataElements )
        {
            dataElementIds.add( dataElement.getId() );
        }

        Criteria criteria = session.createCriteria( CalculatedDataElement.class ).createCriteria( "expression" )
            .createCriteria( "dataElementsInExpression" ).add( Restrictions.in( "id", dataElementIds ) );

        return new HashSet<CalculatedDataElement>( criteria.list() );

    }

    // -------------------------------------------------------------------------
    // DataElementGroup
    // -------------------------------------------------------------------------

    public int addDataElementGroup( DataElementGroup dataElementGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( dataElementGroup );
    }

    public void updateDataElementGroup( DataElementGroup dataElementGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( dataElementGroup );
    }

    public void deleteDataElementGroup( DataElementGroup dataElementGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dataElementGroup );
    }

    public DataElementGroup getDataElementGroup( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (DataElementGroup) session.get( DataElementGroup.class, id );
    }

    public DataElementGroup getDataElementGroup( String uuid )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElementGroup.class );
        criteria.add( Restrictions.eq( "uuid", uuid ) );

        return (DataElementGroup) criteria.uniqueResult();
    }

    public DataElementGroup getDataElementGroupByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElementGroup.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (DataElementGroup) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataElementGroup> getAllDataElementGroups()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElementGroup.class );
        criteria.setCacheable( true );

        return criteria.list();
    }

}
