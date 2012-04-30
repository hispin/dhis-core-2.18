package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.inventory.EquipmentDetails;
import org.hisp.dhis.coldchain.inventory.EquipmentDetailsService;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceService;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;

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
    
    private EquipmentDetailsService equipmentDetailsService;

    public void setEquipmentDetailsService( EquipmentDetailsService equipmentDetailsService )
    {
        this.equipmentDetailsService = equipmentDetailsService;
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

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        equipmentInstance = equipmentInstanceService.getEquipmentInstance( equipmentInstanceId );
        
        inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( equipmentInstance.getInventoryType().getInventoryTypeAttributes() );
        
        equipmentValueMap = new HashMap<Integer, String>();
        
        List<EquipmentDetails> equipmentDetailsList = new ArrayList<EquipmentDetails>( equipmentDetailsService.getEquipmentDetails( equipmentInstance ) );
        
        for( EquipmentDetails equipmentDetails : equipmentDetailsList )
        {
            if ( InventoryTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentDetails.getInventoryTypeAttribute().getValueType() ) )
            {
                equipmentValueMap.put( equipmentDetails.getInventoryTypeAttribute().getId(), equipmentDetails.getInventoryTypeAttributeOption().getName() );
            }
            else
            {
                equipmentValueMap.put( equipmentDetails.getInventoryTypeAttribute().getId(), equipmentDetails.getValue() );
            }
        }
        
        return SUCCESS;
    }
}
