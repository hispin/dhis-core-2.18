package org.hisp.dhis.coldchain.inventory.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.coldchain.inventory.Equipment;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceStore;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateEquipmentInstanceStore 
    extends HibernateGenericStore<EquipmentInstance>
    implements EquipmentInstanceStore
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
    // EquipmentInstance
    // -------------------------------------------------------------------------

    /*
    public int addEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( equipmentInstance );
    }

    
    public void deleteEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( equipmentInstance );
    }

    
    public Collection<EquipmentInstance> getAllEquipmentInstance()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( EquipmentInstance.class ).list();
    }

    
    public void updateEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( equipmentInstance );
    }

    */
    
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( InventoryTypeAttribute.class );
        criteria.add( Restrictions.eq( "organisationUnit", orgUnit ) );

        return criteria.list();
        */
        
        return getCriteria( Restrictions.eq( "organisationUnit", orgUnit ) ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit, InventoryType inventoryType )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( InventoryTypeAttribute.class );
        criteria.add( Restrictions.eq( "organisationUnit", orgUnit ) );
        criteria.add( Restrictions.eq( "inventoryType", inventoryType ) );

        return criteria.list();
        */
        
        Criteria crit = getCriteria();
        Conjunction con = Restrictions.conjunction();

        con.add( Restrictions.eq( "organisationUnit", orgUnit ) );
        con.add( Restrictions.eq( "inventoryType", inventoryType ) );

        crit.add( con );
        
       // Restrictions.in( "organisationUnit", values );
        return crit.list();
    }
    
   // public int getCountEquipmentInstance( OrganisationUnit orgUnit, InventoryType inventoryType )
    public int getCountEquipmentInstance( List<OrganisationUnit> orgUnitList, InventoryType inventoryType )
    {
        Number rs = 0;
        
        //System.out.println(" Size of orgUnitList is : " + orgUnitList.size() + " -- " + inventoryType.getId() + inventoryType.getName() );
       
        if ( orgUnitList != null && orgUnitList.size() != 0 )
        {
            rs = (Number) getCriteria( Restrictions.in( "organisationUnit", orgUnitList ) ).add( Restrictions.eq( "inventoryType", inventoryType ) ).setProjection(
                Projections.rowCount() ).uniqueResult();
        }
        
        /*
        Number rs = (Number) getCriteria( Restrictions.in( "organisationUnit", orgUnitList ) ).add( Restrictions.eq( "inventoryType", inventoryType ) ).setProjection(
            Projections.rowCount() ).uniqueResult();
        */
        
        //System.out.println(" RS is : " + rs );
        
        return rs != null ? rs.intValue() : 0;
    }

    @SuppressWarnings( "unchecked" )
    //public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit, InventoryType inventoryType, int min, int max )
    public Collection<EquipmentInstance> getEquipmentInstances( List<OrganisationUnit> orgUnitList, InventoryType inventoryType, int min, int max )
    {
        List<EquipmentInstance> equipmentInstanceList  = new ArrayList<EquipmentInstance>();
        
        if ( orgUnitList != null && orgUnitList.size() != 0 )
        {
            return getCriteria( Restrictions.in( "organisationUnit", orgUnitList ) ).add( Restrictions.eq( "inventoryType", inventoryType ) ).setFirstResult( min ).setMaxResults( max ).list();
        }
        
        else
        {
            return equipmentInstanceList;
        }
        
        //return getCriteria( Restrictions.in( "organisationUnit", orgUnitList ) ).add( Restrictions.eq( "inventoryType", inventoryType ) ).setFirstResult( min ).setMaxResults( max ).list();
    }

    //public int getCountEquipmentInstance( OrganisationUnit orgUnit, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchText )
    public int getCountEquipmentInstance( String orgUnitIdsByComma, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchText, String searchBy )
    {
        /*
        String hql = "SELECT COUNT( DISTINCT ei ) FROM EquipmentInstance AS ei  " +
                        " WHERE ei IN ( SELECT ed.equipmentInstance FROM Equipment AS ed WHERE ed.inventoryTypeAttribute.id = "+ inventoryTypeAttribute.getId()+" AND ed.value LIKE '%" + searchText + "%' ) " +
                        " AND ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
                        " AND ei.inventoryType.id = " + inventoryType.getId();

       */ 
       
        String hql = "";
        
        if( searchBy.equalsIgnoreCase( Equipment.PREFIX_CATALOG_NAME ) )
        {
            hql = "SELECT COUNT( DISTINCT ei ) FROM EquipmentInstance AS ei, Catalog AS cat" +
            " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.inventoryType.id = " + inventoryType.getId() + 
            "AND cat.name like '%" + searchText + "%' AND cat.id = ei.catalog.id " ;
        }
        
        else if ( searchBy.equalsIgnoreCase( Equipment.PREFIX_ORGANISATIONUNIT_NAME ))
        {
            hql = "SELECT COUNT( DISTINCT ei ) FROM EquipmentInstance AS ei, OrganisationUnit AS orgUnit" +
            " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.inventoryType.id = " + inventoryType.getId() + 
            "AND orgUnit.name like '%" + searchText + "%' AND orgUnit.id = ei.organisationUnit.id " ;
        }
        
        else
        {
            hql = "SELECT COUNT( DISTINCT ei ) FROM EquipmentInstance AS ei  " +
            " WHERE ei IN ( SELECT ed.equipmentInstance FROM Equipment AS ed WHERE ed.inventoryTypeAttribute.id = "+ inventoryTypeAttribute.getId()+" AND ed.value LIKE '%" + searchText + "%' ) " +
            " AND ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.inventoryType.id = " + inventoryType.getId();
        }
        
        
        Query query = getQuery( hql );

        Number rs = (Number) query.uniqueResult();

        return (rs != null) ? rs.intValue() : 0;
    }

    @SuppressWarnings( "unchecked" )
   // public Collection<EquipmentInstance> getEquipmentInstances( OrganisationUnit orgUnit, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchText, int min, int max )
    public Collection<EquipmentInstance> getEquipmentInstances( String orgUnitIdsByComma, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchText, String searchBy, int min, int max )
    {
        /*
        String hql = "SELECT DISTINCT ei FROM EquipmentInstance AS ei  " +
                        " WHERE ei IN ( SELECT ed.equipmentInstance FROM Equipment AS ed WHERE ed.inventoryTypeAttribute.id = "+ inventoryTypeAttribute.getId()+" AND ed.value like '%" + searchText + "%' ) " +
                        " AND ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
                        " AND ei.inventoryType.id = " + inventoryType.getId();

        */
        
        String hql = "";
        
        //if( Equipment.PREFIX_CATALOG_NAME.equalsIgnoreCase( "catalogname" ))
        if( searchBy.equalsIgnoreCase( Equipment.PREFIX_CATALOG_NAME ))
        {
            hql = "SELECT DISTINCT ei FROM EquipmentInstance AS ei, Catalog AS cat" +
            " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.inventoryType.id = " + inventoryType.getId() + 
            "AND cat.name like '%" + searchText + "%' AND cat.id = ei.catalog.id " ;
        }
        
        else if ( searchBy.equalsIgnoreCase( Equipment.PREFIX_ORGANISATIONUNIT_NAME))
        {
            hql = "SELECT DISTINCT ei FROM EquipmentInstance AS ei, OrganisationUnit AS orgUnit" +
            " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.inventoryType.id = " + inventoryType.getId() + 
            "AND orgUnit.name like '%" + searchText + "%' AND orgUnit.id = ei.organisationUnit.id " ;
        }
            
        else
        {
            hql = "SELECT DISTINCT ei FROM EquipmentInstance AS ei  " +
            " WHERE ei IN ( SELECT ed.equipmentInstance FROM Equipment AS ed WHERE ed.inventoryTypeAttribute.id = "+ inventoryTypeAttribute.getId()+" AND ed.value like '%" + searchText + "%' ) " +
            " AND ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
            " AND ei.inventoryType.id = " + inventoryType.getId();
        }
            
            
         /*
        String hql1 = "SELECT DISTINCT ei FROM EquipmentInstance AS ei, Catalog AS cat" +
        " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
        " AND ei.inventoryType.id = " + inventoryType.getId() + 
        "AND cat.name like '%" + searchText + "%' AND cat.id = ei.catalog.id " ;
        */
        
        /*
        String hql1 = "SELECT DISTINCT ei FROM EquipmentInstance AS ei, organisationunit AS orgUnit" +
        " WHERE ei.organisationUnit.id IN (" + orgUnitIdsByComma  + " ) " +
        " AND ei.inventoryType.id = " + inventoryType.getId() + 
        "AND orgUnit.name like '%" + searchText + "%' AND orgUnit.id = ei.organisationUnit.id " ;
        */
        
        //select EI.*, org.name from equipmentinstance as EI ,organisationunit as org where org.name like '%district%' and EI.organisationunitid = org.organisationunitid;
        
        
        //select EI.*, CAT.name from equipmentinstance as EI ,catalog as CAT where CAT.name like '%VC%' and EI.catalogid = CAT.catalogid;
        
        Query query = getQuery( hql ).setFirstResult( min ).setMaxResults( max );

        return query.list();
        
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> searchOrgUnitListByName( String searchText )
    {
        String hql = "SELECT orgUnit FROM OrganisationUnit AS orgUnit WHERE orgUnit.name like '%" + searchText + "%'";
        
        Query query = getQuery( hql );
        
        return query.list();

        /*
        Criteria criteria = getCriteria();
        
        criteria.add(Restrictions.like( "OrganisationUnit.name", "%" + searchText + "%"));
        
        return criteria.list();
        */
        
    }
    
    // for orgUnit list according to orGUnit Attribute values for paging purpose
    public int countOrgUnitByAttributeValue( Collection<Integer> orgunitIds, Attribute attribute, String searchText )
    {
        Criteria criteria = getCriteria( Restrictions.eq( "organisationUnitAttribute", true ));
        
        criteria.createAlias( "attributeValues", "attributeValue");
        criteria.add(Restrictions.eq( "attributeValue.attribute", attribute));
        criteria.add(Restrictions.like( "attributeValue.value", "%" + searchText + "%"));
        /*
        criteria.add(Restrictions.eq( "attributeValues.attribute", attribute));
        criteria.add(Restrictions.eq( "attributeValues.attribute", attribute));
        */
        criteria.add(Restrictions.in( "id",orgunitIds));
        
        Number rs = (Number) criteria.uniqueResult();

        return (rs != null) ? rs.intValue() : 0;
    }
    
    // for orgUnit list according to orGUnit Attribute values for paging purpose
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> searchOrgUnitByAttributeValue( Collection<Integer> orgunitIds, Attribute attribute, String searchText, Integer min, Integer max )
    {
        Criteria criteria = getCriteria( Restrictions.eq( "organisationUnitAttribute", true ));
        criteria.createAlias( "attributeValues", "attributeValue");
        criteria.add(Restrictions.eq( "attributeValue.attribute", attribute));
        criteria.add(Restrictions.like( "attributeValue.value", "%" + searchText + "%"));
        criteria.add(Restrictions.in( "id",orgunitIds));
        
        criteria.setFirstResult( min ).setMaxResults( max );

        return criteria.list();
    }
 
    
    
    
    
}
