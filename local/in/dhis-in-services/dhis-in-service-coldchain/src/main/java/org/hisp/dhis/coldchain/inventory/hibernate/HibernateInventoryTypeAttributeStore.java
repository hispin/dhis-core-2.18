package org.hisp.dhis.coldchain.inventory.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeStore;
import org.hisp.dhis.reports.Report_in;

public class HibernateInventoryTypeAttributeStore implements InventoryTypeAttributeStore
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
    // Dependencies
    // -------------------------------------------------------------------------

    @Override
    public int addInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( inventoryTypeAttribute );
    }

    @Override
    public void deleteInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( inventoryTypeAttribute );
    }

    @Override
    public Collection<InventoryTypeAttribute> getAllInventoryTypeAttributes()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( InventoryTypeAttribute.class ).list();
    }

    @Override
    public void updateInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( inventoryTypeAttribute );
    }

    public InventoryTypeAttribute getInventoryTypeAttribute( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (InventoryTypeAttribute) session.get( InventoryTypeAttribute.class, id );
    }
    
    public InventoryTypeAttribute getInventoryTypeAttributeByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( InventoryTypeAttribute.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (InventoryTypeAttribute) criteria.uniqueResult();
    }

}
