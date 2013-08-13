package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.catalog.Catalog;
import org.hisp.dhis.coldchain.catalog.CatalogDataValue;
import org.hisp.dhis.coldchain.catalog.CatalogDataValueService;
import org.hisp.dhis.coldchain.catalog.CatalogService;
import org.hisp.dhis.coldchain.catalog.CatalogType;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttribute;
import org.hisp.dhis.coldchain.catalog.CatalogTypeAttributeGroup;
import org.hisp.dhis.coldchain.catalog.comparator.CatalogTypeAttributeComparator;
import org.hisp.dhis.coldchain.catalog.comparator.CatalogTypeAttributeGroupOrderComparator;
import org.hisp.dhis.coldchain.inventory.Equipment;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceService;
import org.hisp.dhis.coldchain.inventory.EquipmentService;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeOption;
import org.hisp.dhis.coldchain.inventory.InventoryType_Attribute;
import org.hisp.dhis.coldchain.inventory.InventoryType_AttributeService;
import org.hisp.dhis.coldchain.inventory.comparator.InventoryTypeAttributeOptionComparator;

import com.opensymphony.xwork2.Action;

public class GetEquipmentInstanceDataAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentInstanceService equipmentInstanceService;

    public void setEquipmentInstanceService( EquipmentInstanceService equipmentInstanceService )
    {
        this.equipmentInstanceService = equipmentInstanceService;
    }
    
    private EquipmentService equipmentService;
    
    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    
    private CatalogService catalogService;
    
    public void setCatalogService( CatalogService catalogService )
    {
        this.catalogService = catalogService;
    }
    
    private InventoryType_AttributeService inventoryType_AttributeService;
    
    public void setInventoryType_AttributeService( InventoryType_AttributeService inventoryType_AttributeService )
    {
        this.inventoryType_AttributeService = inventoryType_AttributeService;
    }
    
    private CatalogDataValueService catalogDataValueService;
    
    public void setCatalogDataValueService( CatalogDataValueService catalogDataValueService )
    {
        this.catalogDataValueService = catalogDataValueService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer equipmentInstanceId;
    
    public void setEquipmentInstanceId( Integer equipmentInstanceId )
    {
        this.equipmentInstanceId = equipmentInstanceId;
    }

    private EquipmentInstance equipmentInstance;

    public EquipmentInstance getEquipmentInstance()
    {
        return equipmentInstance;
    }

    private List<InventoryTypeAttribute> inventoryTypeAttributes;
    
    public List<InventoryTypeAttribute> getInventoryTypeAttributes()
    {
        return inventoryTypeAttributes;
    }

    private Map<Integer, String> equipmentValueMap;
    
    public Map<Integer, String> getEquipmentValueMap()
    {
        return equipmentValueMap;
    }

    private List<Catalog> catalogs;
    
    public List<Catalog> getCatalogs()
    {
        return catalogs;
    }
    
    private int equipmentInstanceCatalogId;
    
    public int getEquipmentInstanceCatalogId()
    {
        return equipmentInstanceCatalogId;
    }

    private Map<Integer, List<InventoryTypeAttributeOption>> inventoryTypeAttributeOptionsMap = new HashMap<Integer, List<InventoryTypeAttributeOption>>();
    
    public Map<Integer, List<InventoryTypeAttributeOption>> getInventoryTypeAttributeOptionsMap()
    {
        return inventoryTypeAttributeOptionsMap;
    }
    
    public List<InventoryType_Attribute> inventoryTypeAttributeList;
    
    public List<InventoryType_Attribute> getInventoryTypeAttributeList()
    {
        return inventoryTypeAttributeList;
    }
    
    private Catalog catalog;
    
    public Catalog getCatalog()
    {
        return catalog;
    }
    
    private List<CatalogTypeAttribute> catalogTypeAttributes = new ArrayList<CatalogTypeAttribute>();
    
    public List<CatalogTypeAttribute> getCatalogTypeAttributes()
    {
        return catalogTypeAttributes;
    }
    
    private Map<Integer, String> catalogTypeAttributeValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getCatalogTypeAttributeValueMap()
    {
        return catalogTypeAttributeValueMap;
    }
    
    
    private List<CatalogTypeAttributeGroup> catalogTypeAttributeGroups;
  
    public List<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroups()
    {
        return catalogTypeAttributeGroups;
    }
    
    private String equipmentCatalogName;
    
    public String getEquipmentCatalogName()
    {
        return equipmentCatalogName;
    }

    public void setEquipmentCatalogName( String equipmentCatalogName )
    {
        this.equipmentCatalogName = equipmentCatalogName;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        equipmentInstance = equipmentInstanceService.getEquipmentInstance( equipmentInstanceId );
        //System.out.println( equipmentInstance.getCatalog().getId() + "-----" + equipmentInstance.getCatalog().getName() );
        
        
        equipmentCatalogName = null;
        
        if( equipmentInstance.getCatalog() != null && equipmentInstance.getCatalog().getName() != "" )
        {
            equipmentCatalogName = equipmentInstance.getCatalog().getName();
        }
        
        //equipmentInstance.getOrganisationUnit().getName();
        
        //equipmentInstance.getInventoryType().getCatalogType();
        
        //equipmentInstance.getInventoryType().getName();
        
        if ( equipmentInstance.getCatalog() != null )
        {
            equipmentInstanceCatalogId = equipmentInstance.getCatalog().getId();
        }
        else
        {
            equipmentInstanceCatalogId = 0;
        }
        
        inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( );
        for( InventoryType_Attribute inventoryType_Attribute : equipmentInstance.getInventoryType().getInventoryType_Attributes() )
        {
            inventoryTypeAttributes.add( inventoryType_Attribute.getInventoryTypeAttribute() );
        }
        
        equipmentValueMap = new HashMap<Integer, String>();
        
        //List<Equipment> equipmentDetailsList = new ArrayList<Equipment>( equipmentService.getEquipments( equipmentInstance ) );
       
        inventoryTypeAttributeList = new ArrayList<InventoryType_Attribute>( inventoryType_AttributeService.getAllInventoryTypeAttributesByInventoryType( equipmentInstance.getInventoryType() ) );
        
        for( InventoryType_Attribute inventoryTypeAttribute1 : inventoryTypeAttributeList )
        {
            Equipment equipmentDetails = equipmentService.getEquipment( equipmentInstance, inventoryTypeAttribute1.getInventoryTypeAttribute() );
            if( equipmentDetails != null && equipmentDetails.getValue() != null )
            {
                equipmentValueMap.put( inventoryTypeAttribute1.getInventoryTypeAttribute().getId(), equipmentDetails.getValue() );
                //equipmentDetailsMap.put( equipmentInstance.getId()+":"+inventoryTypeAttribute1.getInventoryTypeAttribute().getId(), equipmentDetails.getValue() );
            }
        }
   
        /*
        for( Equipment equipmentDetails : equipmentDetailsList )
        {
            if ( InventoryTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentDetails.getInventoryTypeAttribute().getValueType() ) )
            {
                System.out.println(" --- InventoryType Attribute Option Name ---  " + equipmentDetails.getInventoryTypeAttributeOption().getName().toString() );
                equipmentValueMap.put( equipmentDetails.getInventoryTypeAttribute().getId(), equipmentDetails.getInventoryTypeAttributeOption().getName() );
            }
            else
            {
                equipmentValueMap.put( equipmentDetails.getInventoryTypeAttribute().getId(), equipmentDetails.getValue() );
            }
        }
        */
        for( InventoryTypeAttribute inventoryTypeAttribute : inventoryTypeAttributes )
        {       
            List<InventoryTypeAttributeOption> inventoryTypeAttributeOptions = new ArrayList<InventoryTypeAttributeOption>();
            if( InventoryTypeAttribute.TYPE_COMBO.equalsIgnoreCase( inventoryTypeAttribute.getValueType() ) )
            {
                //System.out.println(" inside inventoryTypeAttribute.TYPE_COMBO ");
                inventoryTypeAttributeOptions = new ArrayList<InventoryTypeAttributeOption>( inventoryTypeAttribute.getAttributeOptions() );
                Collections.sort( inventoryTypeAttributeOptions, new InventoryTypeAttributeOptionComparator() );
                inventoryTypeAttributeOptionsMap.put( inventoryTypeAttribute.getId(), inventoryTypeAttributeOptions );
            }

        }
        
        CatalogType tempCatalogType = equipmentInstance.getInventoryType().getCatalogType();
        
        if( tempCatalogType != null )
        {
            catalogs = new ArrayList<Catalog>( catalogService.getCatalogs( tempCatalogType ) );
        }
        
        // catalog Details of Equipment
        
        
        catalog = equipmentInstance.getCatalog();
        
        if ( catalog != null )
        {
            CatalogType catalogType = catalog.getCatalogType();
            
            //catalogTypeAttributes = catalogType.getCatalogTypeAttributes();
            
            catalogTypeAttributes = new ArrayList<CatalogTypeAttribute> ( catalogType.getCatalogTypeAttributes());
            Collections.sort( catalogTypeAttributes, new CatalogTypeAttributeComparator() );
            
            List<CatalogDataValue> catalogDataValues = new ArrayList<CatalogDataValue>( catalogDataValueService.getAllCatalogDataValuesByCatalog( catalog ) );
            
            if ( catalogDataValues != null && catalogDataValues.size() != 0 )
            {
                for( CatalogDataValue catalogDataValue : catalogDataValues )
                {
                    if ( CatalogTypeAttribute.TYPE_COMBO.equalsIgnoreCase( catalogDataValue.getCatalogTypeAttribute().getValueType() ) )
                    {
                        catalogTypeAttributeValueMap.put( catalogDataValue.getCatalogTypeAttribute().getId(), catalogDataValue.getCatalogTypeAttributeOption().getName() );
                    }
                    /*
                    else if ( CatalogTypeAttribute.TYPE_BOOL.equalsIgnoreCase( catalogDataValue.getCatalogTypeAttribute().getValueType() ) )
                    {
                        if ( catalogDataValue.getValue().equalsIgnoreCase( "false" ) )
                        {
                            catalogTypeAttributeValueMap.put( catalogDataValue.getCatalogTypeAttribute().getId(), "No" );
                        }
                        else
                        {
                            catalogTypeAttributeValueMap.put( catalogDataValue.getCatalogTypeAttribute().getId(), "Yes" );
                        }
                        
                    }
                    */
                    else
                    {
                        catalogTypeAttributeValueMap.put( catalogDataValue.getCatalogTypeAttribute().getId(), catalogDataValue.getValue() );
                    }
                }
            }

            catalogTypeAttributeGroups = new ArrayList<CatalogTypeAttributeGroup>( catalogType.getCatalogTypeAttributeGroups() );
            
            Collections.sort( catalogTypeAttributeGroups, new CatalogTypeAttributeGroupOrderComparator() );
            
        }
        
        return SUCCESS;
    }
}
