package org.hisp.dhis.coldchain.inventory.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.inventory.Equipment;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentStore;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.hibernate.HibernateGenericStore;

public class HibernateEquipmentStore extends HibernateGenericStore<Equipment> implements EquipmentStore
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
    // EquipmentDetails
    // -------------------------------------------------------------------------
    /*
    @Override
    public void addEquipmentDetails( Equipment equipmentDetails )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( equipmentDetails );
    }

    @Override
    public void deleteEquipmentDetails( Equipment equipmentDetails )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentDetails );
    }

    @Override
    public Collection<Equipment> getAllEquipmentDetails()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( Equipment.class ).list();
    }

    @Override
    public void updateEquipmentDetails( Equipment equipmentDetails )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentDetails );
    }
    
    
    @Override
    public Collection<Equipment> getAllEquipmentDetails()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( Equipment.class ).list();
    }
    
    
    public Collection<Equipment> getEquipmentDetails( EquipmentInstance equipmentInstance )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( Equipment.class );
        
        criteria.add( Restrictions.eq( "equipmentInstance", equipmentInstance ) );
        
        return criteria.list();
    }

    public Equipment getEquipmentDetails( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( Equipment.class );
        
        criteria.add( Restrictions.eq( "equipmentInstance", equipmentInstance ) );
        criteria.add( Restrictions.eq( "inventoryTypeAttribute", inventoryTypeAttribute ) );
        
        return (Equipment) criteria.uniqueResult();
    }
    */
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Equipment> getAllEquipments()
    {
        return sessionFactory.getCurrentSession().createCriteria( Equipment.class ).list();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<Equipment> getEquipments( EquipmentInstance equipmentInstance )
    {
        return getCriteria( Restrictions.eq( "equipmentInstance", equipmentInstance ) ).list();
    }

    public Equipment getEquipment( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute )
    {
        return (Equipment) getCriteria( Restrictions.eq( "equipmentInstance", equipmentInstance ),Restrictions.eq( "inventoryTypeAttribute", inventoryTypeAttribute ) ).uniqueResult();
    }
}
