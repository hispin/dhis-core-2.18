package org.hisp.dhis.coldchain.inventory.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;

import com.opensymphony.xwork2.Action;

public class GetInventoryTypeListAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private InventoryTypeService inventoryTypeService;

    public void setInventoryTypeService( InventoryTypeService inventoryTypeService )
    {
        this.inventoryTypeService = inventoryTypeService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<InventoryType> inventoryTypes;

    public List<InventoryType> getInventoryTypes()
    {
        return inventoryTypes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        inventoryTypes = new ArrayList<InventoryType>( inventoryTypeService.getAllInventoryTypes() );
        
        /**
         * TODO - need to write comparator for sorting the list
         */
        
        return SUCCESS;
    }

}
