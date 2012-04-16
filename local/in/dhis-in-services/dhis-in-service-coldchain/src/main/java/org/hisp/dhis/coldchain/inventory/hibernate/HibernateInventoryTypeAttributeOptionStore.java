package org.hisp.dhis.coldchain.inventory.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeOption;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeOptionStore;

public class HibernateInventoryTypeAttributeOptionStore implements InventoryTypeAttributeOptionStore
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
    // InventoryTypeAttributeOption
    // -------------------------------------------------------------------------

    @Override
    public int addInventoryTypeAttributeOption( InventoryTypeAttributeOption inventoryTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( inventoryTypeAttributeOption );
    }

    @Override
    public void deleteInventoryTypeAttributeOption( InventoryTypeAttributeOption inventoryTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( inventoryTypeAttributeOption );
    }

    @Override
    public Collection<InventoryTypeAttributeOption> getAllInventoryTypeAttributeOptions()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( InventoryTypeAttributeOption.class ).list();
    }

    @Override
    public void updateInventoryTypeAttributeOption( InventoryTypeAttributeOption inventoryTypeAttributeOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( inventoryTypeAttributeOption );
    }

}
