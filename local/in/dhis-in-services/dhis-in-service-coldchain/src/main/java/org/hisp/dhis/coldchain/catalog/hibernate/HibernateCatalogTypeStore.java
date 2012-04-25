package org.hisp.dhis.coldchain.catalog.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;
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
    
    @Override
    public CatalogType getCatalogType( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (CatalogType) session.get( CatalogType.class, id );
    }
    
    @Override
    public CatalogType getCatalogTypeByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CatalogType.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (CatalogType) criteria.uniqueResult();

    }
    /*
    @Override
    public CatalogType getCatalogTypeByAttribute( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute)
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CatalogType.class );
        criteria.add( Restrictions.eq( "name", catalogTypeAttribute ) );

        return (CatalogType) criteria.uniqueResult();

    }
    */
}
