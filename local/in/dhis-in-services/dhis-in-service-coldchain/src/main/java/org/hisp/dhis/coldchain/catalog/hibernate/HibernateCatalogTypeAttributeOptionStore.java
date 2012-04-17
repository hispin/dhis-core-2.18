package org.hisp.dhis.coldchain.catalog.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeOption;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeOptionStore;

public class HibernateCatalogTypeAttributeOptionStore implements CatalogTypeAttributeOptionStore
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
    // CatalogTypeAttributeOption
    // -------------------------------------------------------------------------
    @Override
    public int addCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( catalogTypeAttributeOption );
    }

    @Override
    public void deleteCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( catalogTypeAttributeOption );
    }

    @Override
    public Collection<CatalogTypeAttributeOption> getAllCatalogTypeAttributeOptions()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( CatalogTypeAttributeOption.class ).list();
    }

    @Override
    public void updateCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( catalogTypeAttributeOption );
    }

}
