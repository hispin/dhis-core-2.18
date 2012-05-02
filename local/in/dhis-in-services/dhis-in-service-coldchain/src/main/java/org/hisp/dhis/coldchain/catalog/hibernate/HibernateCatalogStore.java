package org.hisp.dhis.coldchain.catalog.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.catalog.Catalog;
import org.hisp.dhis.coldchain.catalog.CatalogStore;
import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.hibernate.HibernateGenericStore;

public class HibernateCatalogStore extends HibernateGenericStore<Catalog> implements CatalogStore
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
    
    @Override
    public Catalog getCatalog( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Catalog) session.get( Catalog.class, id );
    }
    
    @Override
    public Catalog getCatalogByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Catalog.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (Catalog) criteria.uniqueResult();
    }
    
    public Collection<Catalog> getCatalogs( CatalogType catalogType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Catalog.class );
        criteria.add( Restrictions.eq( "catalogType", catalogType ) );

        return criteria.list();
    }
    
}
