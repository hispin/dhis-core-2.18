package org.hisp.dhis.coldchain.inventory.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.coldchain.inventory.EquipmentDetails;
import org.hisp.dhis.coldchain.inventory.EquipmentDetailsStore;

public class HibernateEquipmentDetailsStore implements EquipmentDetailsStore
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
    // EquipmentDetails
    // -------------------------------------------------------------------------

    @Override
    public int addEquipmentDetails( EquipmentDetails equipmentDetails )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( equipmentDetails );
    }

    @Override
    public void deleteEquipmentDetails( EquipmentDetails equipmentDetails )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentDetails );
    }

    @Override
    public Collection<EquipmentDetails> getAllEquipmentDetails()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentDetails.class ).list();
    }

    @Override
    public void updateEquipmentDetails( EquipmentDetails equipmentDetails )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentDetails );
    }
    
}
