package org.hisp.dhis.coldchain.inventory.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeService;

import com.opensymphony.xwork2.Action;

public class GetInventoryTypeAttributeListAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private InventoryTypeAttributeService inventoryTypeAttributeService;

    public void setInventoryTypeAttributeService( InventoryTypeAttributeService inventoryTypeAttributeService )
    {
        this.inventoryTypeAttributeService = inventoryTypeAttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<InventoryTypeAttribute> inventoryTypeAttributes;

    public List<InventoryTypeAttribute> getInventoryTypeAttributes()
    {
        return inventoryTypeAttributes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    throws Exception
    {
        inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( inventoryTypeAttributeService.getAllInventoryTypeAttributes() );
        
        /**
         * TODO - need to write comparator for sorting the list
         */
        
        return SUCCESS;
    }

}
