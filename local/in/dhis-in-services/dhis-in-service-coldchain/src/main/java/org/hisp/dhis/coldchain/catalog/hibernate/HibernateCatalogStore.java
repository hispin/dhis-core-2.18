package org.hisp.dhis.coldchain.catalog.hibernate;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.catalog.Catalog;
import org.hisp.dhis.coldchain.catalog.CatalogStore;
import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;
import org.hisp.dhis.hibernate.HibernateGenericStore;

public class HibernateCatalogStore extends HibernateGenericStore<Catalog> implements CatalogStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    /*
    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    */
    // -------------------------------------------------------------------------
    // Catalog
    // -------------------------------------------------------------------------
    /*
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
    */
    // -------------------------------------------------------------------------
    // Catalog
    // -------------------------------------------------------------------------
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Catalog> getAllCatalogs()
    {
        return sessionFactory.getCurrentSession().createCriteria( Catalog.class ).list();
    }
    
    @Override
    public Catalog getCatalog( int id )
    {
        return (Catalog) sessionFactory.getCurrentSession().get( Catalog.class, id );
    }
    
    @Override
    public Catalog getCatalogByName( String name )
    {
        return (Catalog) getCriteria( Restrictions.eq( "name", name ) ).uniqueResult();
    }
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Catalog> getCatalogs( CatalogType catalogType )
    {
        return getCriteria( Restrictions.eq( "catalogType", catalogType ) ).list();
    }
      
    
    
    
    public int getCountCatalog( CatalogType catalogType )
    {
        Number rs = (Number) getCriteria(  Restrictions.eq( "catalogType", catalogType ) ).setProjection( Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }   
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<Catalog> getCatalogs( CatalogType catalogType, int min, int max )
    {
        return getCriteria( Restrictions.eq( "catalogType", catalogType ) ).setFirstResult( min ).setMaxResults( max ).list();
    }

    
    public int getCountCatalog( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute, String searchText )
    {
        String hql = "SELECT COUNT( DISTINCT cat ) FROM Catalog AS cat  " +
                        " WHERE cat IN ( SELECT catdata.catalog FROM CatalogDataValue catdata  WHERE catdata.catalogTypeAttribute.id = "+ catalogTypeAttribute.getId()+" AND catdata.value LIKE '%" + searchText + "%' ) " +
                        " AND cat.catalogType.id = " + catalogType.getId();

        System.out.println( hql );
        Query query = getQuery( hql );

        Number rs = (Number) query.uniqueResult();

        return (rs != null) ? rs.intValue() : 0;
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Catalog> getCatalogs( CatalogType catalogType, CatalogTypeAttribute catalogTypeAttribute, String searchText, int min, int max )
    {
        
        //System.out.println("catalogTypeAttribute " + catalogTypeAttribute.getName() + "--- catalogType Name " + catalogType.getName() +"--- searchText is  " + searchText  );
        String hql = "SELECT DISTINCT cat FROM Catalog AS cat  " +
                        " WHERE cat IN ( SELECT catdata.catalog FROM CatalogDataValue catdata WHERE catdata.catalogTypeAttribute.id = "+ catalogTypeAttribute.getId()+" AND catdata.value LIKE '%" + searchText + "%' ) " +
                        " AND cat.catalogType.id = " + catalogType.getId();

        
        Query query = getQuery( hql ).setFirstResult( min ).setMaxResults( max );

        return query.list();
    }   
    
}
