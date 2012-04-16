package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface InventoryTypeAttributeOptionService
{
    String ID = InventoryTypeAttributeOptionService.class.getName();
    
    int addInventoryTypeAttributeOption( InventoryTypeAttributeOption inventoryTypeAttributeOption );

    void updateInventoryTypeAttributeOption( InventoryTypeAttributeOption inventoryTypeAttributeOption );

    void deleteInventoryTypeAttributeOption( InventoryTypeAttributeOption inventoryTypeAttributeOption );

    Collection<InventoryTypeAttributeOption> getAllInventoryTypeAttributeOptions();
}
