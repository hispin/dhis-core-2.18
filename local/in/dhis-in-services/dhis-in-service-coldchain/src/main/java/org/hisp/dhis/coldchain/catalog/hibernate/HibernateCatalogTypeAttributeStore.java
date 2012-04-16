package org.hisp.dhis.coldchain.catalog.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeStore;

public class HibernateCatalogTypeAttributeStore implements CatalogTypeAttributeStore
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
    // CatalogTypeAttribute
    // -------------------------------------------------------------------------

    @Override
    public int addCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( catalogTypeAttribute );
    }

    @Override
    public void deleteCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( catalogTypeAttribute );
    }

    @Override
    public Collection<CatalogTypeAttribute> getAllCatalogTypeAttributes()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( CatalogTypeAttribute.class ).list();
    }

    @Override
    public void updateCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( catalogTypeAttribute );        
    }
    
}
