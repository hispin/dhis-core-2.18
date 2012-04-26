package org.hisp.dhis.coldchain.inventory.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceStore;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class HibernateEquipmentInstanceStore 
    extends HibernateGenericStore<EquipmentInstance>
    implements EquipmentInstanceStore
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

    public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( InventoryTypeAttribute.class );
        criteria.add( Restrictions.eq( "organisationUnit", orgUnit ) );

        return criteria.list();
    }
    
    public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit, InventoryType inventoryType )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( InventoryTypeAttribute.class );
        criteria.add( Restrictions.eq( "organisationUnit", orgUnit ) );
        criteria.add( Restrictions.eq( "inventoryType", inventoryType ) );

        return criteria.list();
    }
    
    public int getCountEquipmentInstance( OrganisationUnit orgUnit, InventoryType inventoryType )
    {
        Number rs = (Number) getCriteria( Restrictions.eq( "organisationUnit", orgUnit ) ).add( Restrictions.eq( "inventoryType", inventoryType ) ).setProjection(
            Projections.rowCount() ).uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit, InventoryType inventoryType, int min, int max )
    {
        String hql = "select e from EquipmentInstance e where e.organisationUnit = :orgUnit and e.inventoryType = :inventoryType order by e.id DESC";

        return getQuery( hql ).setEntity( "organisationUnit", orgUnit ).setFirstResult( min ).setMaxResults(
            max ).list();
    }

}
