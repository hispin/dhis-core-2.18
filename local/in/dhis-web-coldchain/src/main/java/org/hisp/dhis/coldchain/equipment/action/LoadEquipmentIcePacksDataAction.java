package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.inventory.Equipment;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceService;
import org.hisp.dhis.coldchain.inventory.EquipmentService;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;
import org.hisp.dhis.coldchain.inventory.InventoryType_Attribute;
import org.hisp.dhis.coldchain.inventory.InventoryType_AttributeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version LoadEquipmentIcePacksDataAction.javaDec 21, 2012 12:10:22 PM	
 */

public class LoadEquipmentIcePacksDataAction  implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private InventoryTypeService inventoryTypeService;
    
    public void setInventoryTypeService( InventoryTypeService inventoryTypeService )
    {
        this.inventoryTypeService = inventoryTypeService;
    }
    
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

    
    private InventoryType_AttributeService inventoryType_AttributeService;
    
    public void setInventoryType_AttributeService( InventoryType_AttributeService inventoryType_AttributeService )
    {
        this.inventoryType_AttributeService = inventoryType_AttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int inventoryTypeId;
    
    public void setInventoryTypeId( int inventoryTypeId )
    {
        this.inventoryTypeId = inventoryTypeId;
    }
    
    private int orgUnitId;
    

    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    
    private InventoryType inventoryType;

    public InventoryType getInventoryType()
    {
        return inventoryType;
    }   
    
    private List<EquipmentInstance> equipmentInstanceList;

    public List<EquipmentInstance> getEquipmentInstanceList()
    {
        return equipmentInstanceList;
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

    public List<InventoryType_Attribute> inventoryTypeAttributeList;
    
    public List<InventoryType_Attribute> getInventoryTypeAttributeList()
    {
        return inventoryTypeAttributeList;
    }


    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        organisationUnit = organisationUnitService.getOrganisationUnit(  orgUnitId );
        
        inventoryType = inventoryTypeService.getInventoryType(  inventoryTypeId  );
        
        equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( organisationUnit, inventoryType ) );
        
        /*
        
        if( equipmentInstanceList == null || equipmentInstanceList.size() == 0 )
        {
            
        }
        else
        {
            equipmentInstance = equipmentInstanceList.get( 0 );
            
        }
        
        for( EquipmentInstance equipmentInstance : equipmentInstanceList )
        {
            
        }
        */
        
        inventoryTypeAttributeList = new ArrayList<InventoryType_Attribute>();
        
        inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>();
        
        if( equipmentInstanceList != null && equipmentInstanceList.size() > 0 )
        {
            equipmentInstance = equipmentInstanceList.get( 0 );
            
            inventoryTypeAttributeList = new ArrayList<InventoryType_Attribute>( inventoryType_AttributeService.getAllInventoryTypeAttributesByInventoryType( equipmentInstance.getInventoryType() ) );
            
            for( InventoryType_Attribute inventoryType_Attribute : equipmentInstance.getInventoryType().getInventoryType_Attributes() )
            {
                inventoryTypeAttributes.add( inventoryType_Attribute.getInventoryTypeAttribute() );
            }
        
        }
        
        else
        {
            inventoryTypeAttributeList = new ArrayList<InventoryType_Attribute>( inventoryType_AttributeService.getAllInventoryTypeAttributesByInventoryType( inventoryType ) );
        
            for( InventoryType_Attribute inventoryType_Attribute : inventoryTypeAttributeList )
            {
                inventoryTypeAttributes.add( inventoryType_Attribute.getInventoryTypeAttribute() );
            }
        
        }
        
        //inventoryTypeAttributeList = new ArrayList<InventoryType_Attribute>( inventoryType_AttributeService.getAllInventoryTypeAttributesByInventoryType( equipmentInstance.getInventoryType() ) );
        
        equipmentValueMap = new HashMap<Integer, String>();
        
        for( InventoryType_Attribute inventoryTypeAttribute1 : inventoryTypeAttributeList )
        {
            Equipment equipmentDetails = equipmentService.getEquipment( equipmentInstance, inventoryTypeAttribute1.getInventoryTypeAttribute() );
           
            if( equipmentDetails != null && equipmentDetails.getValue() != null )
            {
                equipmentValueMap.put( inventoryTypeAttribute1.getInventoryTypeAttribute().getId(), equipmentDetails.getValue() );
            }
        }
        
        
        return SUCCESS;
    }
}

