package org.hisp.dhis.pbf.impl;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueStore;

public class HibernateTariffDataValueStore implements TariffDataValueStore
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
    // TariffDataValue
    // -------------------------------------------------------------------------
    
    @Override
    public void addTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( tariffDataValue );
    }

    @Override
    public void updateTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( tariffDataValue );
    }

    @Override
    public void deleteTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( tariffDataValue );
    }

    @Override
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );        
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (TariffDataValue) criteria.uniqueResult();
    }

    @Override
    public Collection<TariffDataValue> getAllTariffDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "organisationUnitGroup", dataSet ) );

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.addOrder(Order.asc("dataSet"));

        return criteria.list();
    }

}
