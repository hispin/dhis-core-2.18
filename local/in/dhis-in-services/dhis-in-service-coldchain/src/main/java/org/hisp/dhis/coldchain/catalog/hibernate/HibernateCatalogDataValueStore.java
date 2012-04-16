package org.hisp.dhis.coldchain.catalog.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.coldchain.catalog.CatalogDataValue;
import org.hisp.dhis.coldchain.catalog.CatalogDataValueStore;

public class HibernateCatalogDataValueStore implements CatalogDataValueStore
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
    // CatalogDataValue
    // -------------------------------------------------------------------------

    @Override
    public int addCatalogDataValue( CatalogDataValue catalogDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( catalogDataValue );
    }

    @Override
    public void deleteCatalogDataValue( CatalogDataValue catalogDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( catalogDataValue );
    }

    @Override
    public Collection<CatalogDataValue> getAllCatalogDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( CatalogDataValue.class ).list();
    }

    @Override
    public void updateCatalogDataValue( CatalogDataValue catalogDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( catalogDataValue );
    }

}
