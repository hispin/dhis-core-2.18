package org.hisp.dhis.coldchain.inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Mithilesh Kumar Thakur
 *
 * @version DefaultInventoryType_AttributeService.java Jun 14, 2012 3:19:02 PM	
 */

public class DefaultInventoryType_AttributeService implements InventoryType_AttributeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private InventoryType_AttributeStore inventoryType_AttributeStore;

    public void setInventoryType_AttributeStore( InventoryType_AttributeStore inventoryType_AttributeStore )
    {
        this.inventoryType_AttributeStore = inventoryType_AttributeStore;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public void addInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute )
    {
        inventoryType_AttributeStore.addInventoryType_Attribute( inventoryType_Attribute );
    }
    
    @Transactional
    @Override
    public void deleteInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute )
    {
        inventoryType_AttributeStore.deleteInventoryType_Attribute( inventoryType_Attribute );
    }
    
    @Transactional
    @Override
    public void updateInventoryType_Attribute( InventoryType_Attribute inventoryType_Attribute )
    {
        inventoryType_AttributeStore.updateInventoryType_Attribute( inventoryType_Attribute );
    }
    
    @Transactional
    @Override
    public Collection<InventoryType_Attribute> getAllInventoryTypeAttributes()
    {
        return inventoryType_AttributeStore.getAllInventoryTypeAttributes();
    }
    
    @Transactional
    @Override
    public InventoryType_Attribute getInventoryTypeAttribute( InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute )
    {
        return inventoryType_AttributeStore.getInventoryTypeAttribute( inventoryType, inventoryTypeAttribute );
    }
    
    @Transactional
    @Override
    public Collection<InventoryType_Attribute> getAllInventoryTypeAttributesByInventoryType( InventoryType inventoryType )
    {
        return inventoryType_AttributeStore.getAllInventoryTypeAttributesByInventoryType( inventoryType );
    }
    
    @Transactional
    @Override
    public Collection<InventoryTypeAttribute> getListInventoryTypeAttribute( InventoryType inventoryType )
    {
        return inventoryType_AttributeStore.getListInventoryTypeAttribute( inventoryType );
    }

    @Transactional
    @Override
    public InventoryType_Attribute getInventoryTypeAttributeForDisplay( InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, boolean display)
    {
        return inventoryType_AttributeStore.getInventoryTypeAttributeForDisplay( inventoryType, inventoryTypeAttribute, display );
    }
    
    @Transactional
    @Override
    public Collection<InventoryType_Attribute> getAllInventoryTypeAttributeForDisplay( InventoryType inventoryType, boolean display )
    {
        return inventoryType_AttributeStore.getAllInventoryTypeAttributeForDisplay( inventoryType, display );
    }
    
    
    public Map<String, String> getOrgUnitAttributeDataValue( String orgUnitIdsByComma, String orgUnitAttribIdsByComma )
    {
        Map<String, String> orgUnitAttributeDataValueMap = new HashMap<String, String>();
        try
        {
            String query = "SELECT organisationunitattributevalues.organisationunitid, attributevalue.attributeid, value FROM attributevalue "+
                                " INNER JOIN organisationunitattributevalues ON attributevalue.attributevalueid = organisationunitattributevalues.attributevalueid "+
                                " WHERE attributeid IN ("+orgUnitAttribIdsByComma+") AND " +
                                    " organisationunitattributevalues.organisationunitid IN ("+ orgUnitIdsByComma +")";
                        
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitID = rs.getInt( 1 );
                Integer attribId = rs.getInt( 2 );
                String value = rs.getString( 3 );

                orgUnitAttributeDataValueMap.put( orgUnitID+":"+attribId, value );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return orgUnitAttributeDataValueMap;
    }
    
    
   
    public Collection<OrganisationUnit> searchOrgUnitByAttributeValue( String orgUnitIdsByComma, Attribute attribute, String searchText )
    {
        //String sql = searchPatientSql( false, searchKeys, orgunit, min, max );
        
        /*
        System.out.println( "--- orgUnitIdsByComma" + orgUnitIdsByComma  );
        
        System.out.println( "--- attribute" + attribute.getName() );
        
        System.out.println( "--- searchText" + searchText );
        */
        
        String sql = "SELECT distinct organisationunitattributevalues.organisationunitid as organisationunitid, attributevalue.attributeid, value FROM attributevalue " +
        
                     "INNER JOIN organisationunitattributevalues ON attributevalue.attributevalueid = organisationunitattributevalues.attributevalueid " +
           
                     "WHERE attributeid = " + attribute.getId() + 
           
                     " AND organisationunitattributevalues.organisationunitid IN ("+ orgUnitIdsByComma +")" +  
                     
                     " AND value like '%" + searchText + "%' " ;

        //System.out.println( "---" + sql );
        
        Collection<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

        try
        {
            organisationUnits = jdbcTemplate.query( sql, new RowMapper<OrganisationUnit>()
            {
                public OrganisationUnit mapRow( ResultSet rs, int rowNum ) throws SQLException
                {
                    //System.out.println( "--- " + rs.getString( "organisationunitid" ) );
                    //return organisationUnitService.getOrganisationUnit( rs.getString( "organisationunitid" ) );
                    return organisationUnitService.getOrganisationUnit( rs.getInt( "organisationunitid" ) );
                }
            } );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        /*
        System.out.println( "--- Service " + organisationUnits.size() );
        
        for( OrganisationUnit orgUnit : organisationUnits )
        {
            System.out.println( "--- " + orgUnit.getId() + "----" + orgUnit.getName() );
        }
        */
        return organisationUnits;
    }

    
    public Map<Integer, String> getEquipmentCountByOrgUnitList( String orgUnitIdsByComma )
    {
        Map<Integer, String> inventoryTypeCountMap = new HashMap<Integer, String>();
        try
        {
            String query = "SELECT equipmentinstance.inventorytypeid,count(*) as total from equipmentinstance "+
                           " WHERE organisationunitid IN (" + orgUnitIdsByComma + " ) " +
                           " group by equipmentinstance.inventorytypeid ";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer inventoryTypeID = rs.getInt( 1 );
                String equipmentCount = rs.getString( 2 );
                
                //System.out.println( "--- " + inventoryTypeID + "----" + equipmentCount );
                
                inventoryTypeCountMap.put( inventoryTypeID, equipmentCount );
            }
        }    
            
       catch( Exception e )
       {
           throw new RuntimeException( "Exception: ", e );
       }
            
       
       //System.out.println( "--- Map Size " + inventoryTypeCountMap.size() );
       return inventoryTypeCountMap;
    }
    
    
}
