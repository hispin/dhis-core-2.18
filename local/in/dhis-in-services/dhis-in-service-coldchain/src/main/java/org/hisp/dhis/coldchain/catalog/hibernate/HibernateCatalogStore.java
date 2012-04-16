package org.hisp.dhis.coldchain.catalog.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.coldchain.catalog.Catalog;
import org.hisp.dhis.coldchain.catalog.CatalogStore;

public class HibernateCatalogStore implements CatalogStore
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
    // Catalog
    // -------------------------------------------------------------------------

    public int addCatalog( Catalog catalog )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( catalog );
    }

    public void deleteCatalog( Catalog catalog )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( catalog );
    }

    public void updateCatalog( Catalog catalog )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( catalog );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Catalog> getAllCatalogs()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( Catalog.class ).list();
    }

}
