package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface InventoryTypeAttributeService
{
    String ID = InventoryTypeAttributeService.class.getName();
    
    int addInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    void updateInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    void deleteInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    Collection<InventoryTypeAttribute> getAllInventoryTypeAttributes();

}
