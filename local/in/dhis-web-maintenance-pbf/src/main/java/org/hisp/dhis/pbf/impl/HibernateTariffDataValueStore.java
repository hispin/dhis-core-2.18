package org.hisp.dhis.pbf.impl;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueStore;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.objectmapper.DataValueRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

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
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, OrganisationUnitGroup organisationUnitGroup, Date startDate,
        Date endDate )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );        
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );
        criteria.add( Restrictions.eq( "organisationUnitGroup", organisationUnitGroup ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (TariffDataValue) criteria.uniqueResult();
    }

    @Override
    public TariffDataValue getTariffDataValue( int organisationUnitId, int dataElementId, int categoryOptionComboId,
        int organisationUnitGroupId, Date startDate, Date endDate )
    {
        /**
         * TODO
         */
       return null;
    }

    @Override
    public Collection<TariffDataValue> getAllTariffDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit,
        OrganisationUnitGroup organisationUnitGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "organisationUnitGroup", organisationUnitGroup ) );

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

}
