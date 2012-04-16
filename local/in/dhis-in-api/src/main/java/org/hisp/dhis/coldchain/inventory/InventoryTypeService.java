package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface InventoryTypeService
{
    String ID = InventoryTypeService.class.getName();
    
    int addInventoryType( InventoryType inventoryType );

    void updateInventoryType( InventoryType inventoryType );

    void deleteInventoryType( InventoryType inventoryType );

    Collection<InventoryType> getAllInventoryTypes();
}
