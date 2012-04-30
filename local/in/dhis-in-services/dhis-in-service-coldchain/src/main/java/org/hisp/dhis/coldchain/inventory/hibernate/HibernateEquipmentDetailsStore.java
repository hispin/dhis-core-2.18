package org.hisp.dhis.coldchain.inventory.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.inventory.EquipmentDetails;
import org.hisp.dhis.coldchain.inventory.EquipmentDetailsStore;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.organisationunit.OrganisationUnit;

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
    public void addEquipmentDetails( EquipmentDetails equipmentDetails )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( equipmentDetails );
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
    
    public Collection<EquipmentDetails> getEquipmentDetails( EquipmentInstance equipmentInstance )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( EquipmentDetails.class );
        
        criteria.add( Restrictions.eq( "equipmentInstance", equipmentInstance ) );
        
        return criteria.list();
    }

    public EquipmentDetails getEquipmentDetails( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( EquipmentDetails.class );
        
        criteria.add( Restrictions.eq( "equipmentInstance", equipmentInstance ) );
        criteria.add( Restrictions.eq( "inventoryTypeAttribute", inventoryTypeAttribute ) );
        
        return (EquipmentDetails) criteria.uniqueResult();
    }

    
}
