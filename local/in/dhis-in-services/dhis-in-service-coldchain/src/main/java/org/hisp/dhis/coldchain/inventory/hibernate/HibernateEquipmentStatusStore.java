package org.hisp.dhis.coldchain.inventory.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.coldchain.inventory.EquipmentStatus;
import org.hisp.dhis.coldchain.inventory.EquipmentStatusStore;

public class HibernateEquipmentStatusStore implements EquipmentStatusStore
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
    // EquipmentWorkingStatus
    // -------------------------------------------------------------------------

    @Override
    public int addEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( equipmentStatus );
    }

    @Override
    public void deleteEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentStatus );
    }

    @Override
    public Collection<EquipmentStatus> getAllEquipmentStatus()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentStatus.class ).list();
    }

    @Override
    public void updateEquipmentStatus( EquipmentStatus equipmentStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentStatus );
    }

}
