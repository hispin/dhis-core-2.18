package org.hisp.dhis.coldchain.inventory.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.coldchain.inventory.EquipmentWorkingStatus;
import org.hisp.dhis.coldchain.inventory.EquipmentWorkingStatusStore;

public class HibernateEquipmentWorkingStatusStore implements EquipmentWorkingStatusStore
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
    public int addEquipmentWorkingStatus( EquipmentWorkingStatus equipmentWorkingStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( equipmentWorkingStatus );
    }

    @Override
    public void deleteEquipmentWorkingStatus( EquipmentWorkingStatus equipmentWorkingStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentWorkingStatus );
    }

    @Override
    public Collection<EquipmentWorkingStatus> getAllEquipmentWorkingStatus()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentWorkingStatus.class ).list();
    }

    @Override
    public void updateEquipmentWorkingStatus( EquipmentWorkingStatus equipmentWorkingStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentWorkingStatus );
    }

}
