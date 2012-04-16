package org.hisp.dhis.coldchain.inventory.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceStore;

public class HibernateEquipmentInstanceStore implements EquipmentInstanceStore
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
    // EquipmentInstance
    // -------------------------------------------------------------------------

    @Override
    public int addEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( equipmentInstance );
    }

    @Override
    public void deleteEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentInstance );
    }

    @Override
    public Collection<EquipmentInstance> getAllEquipmentInstance()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentInstance.class ).list();
    }

    @Override
    public void updateEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentInstance );
    }

}
