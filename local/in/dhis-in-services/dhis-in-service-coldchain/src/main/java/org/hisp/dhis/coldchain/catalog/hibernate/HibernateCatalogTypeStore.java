package org.hisp.dhis.coldchain.catalog.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeStore;

public class HibernateCatalogTypeStore implements CatalogTypeStore
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
    // CatalogType
    // -------------------------------------------------------------------------

    @Override
    public int addCatalogType( CatalogType catalogType )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( catalogType );
    }

    @Override
    public void deleteCatalogType( CatalogType catalogType )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( catalogType );        
    }

    @Override
    public Collection<CatalogType> getAllCatalogTypes()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( CatalogType.class ).list();
    }

    @Override
    public void updateCatalogType( CatalogType catalogType )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( catalogType );        
    }

}
