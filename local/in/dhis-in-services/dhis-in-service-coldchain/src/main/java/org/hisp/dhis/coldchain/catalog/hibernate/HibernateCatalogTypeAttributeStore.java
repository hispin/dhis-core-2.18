package org.hisp.dhis.coldchain.catalog.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeStore;
import org.hisp.dhis.reports.Report_in;

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
    @Override
    public CatalogTypeAttribute getCatalogTypeAttribute( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (CatalogTypeAttribute) session.get( CatalogTypeAttribute.class, id );
    }

    @Override
    public CatalogTypeAttribute getCatalogTypeAttributeByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CatalogTypeAttribute.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (CatalogTypeAttribute) criteria.uniqueResult();

    }
    
}
