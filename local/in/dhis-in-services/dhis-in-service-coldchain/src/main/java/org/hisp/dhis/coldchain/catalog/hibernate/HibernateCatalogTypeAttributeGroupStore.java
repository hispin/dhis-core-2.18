package org.hisp.dhis.coldchain.catalog.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroup;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroupStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version HibernateCatalogTypeAttributeGroupStore.javaOct 9, 2012 3:56:11 PM	
 */

public class HibernateCatalogTypeAttributeGroupStore extends HibernateGenericStore<CatalogTypeAttributeGroup> implements CatalogTypeAttributeGroupStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // CatalogTypeAttributeGroup
    // -------------------------------------------------------------------------
    
    /*
    public void addCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( catalogTypeAttributeGroup );
    }

    public void deleteCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( catalogTypeAttributeGroup );
    }
    
    public void updateCatalogTypeAttributeGroup( CatalogTypeAttributeGroup catalogTypeAttributeGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( catalogTypeAttributeGroup );
    }
    
   */
    @Override
    
    public CatalogTypeAttributeGroup getCatalogTypeAttributeGroupById( int id )
    {
        return (CatalogTypeAttributeGroup) sessionFactory.getCurrentSession().get( CatalogTypeAttributeGroup.class, id );
        
    }
    
    @Override
    
    public CatalogTypeAttributeGroup getCatalogTypeAttributeGroupByName( String name )
    {
        return ( CatalogTypeAttributeGroup ) getCriteria( Restrictions.eq( "name", name ) ).uniqueResult();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CatalogTypeAttributeGroup> getAllCatalogTypeAttributeGroups()
    {
        return sessionFactory.getCurrentSession().createCriteria( CatalogTypeAttributeGroup.class ).list();
    }
   
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroupsByCatalogType( CatalogType catalogType )
    {
        return getCriteria( Restrictions.eq( "catalogType", catalogType ) ).list();
    }
}

