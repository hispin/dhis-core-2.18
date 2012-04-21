package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface InventoryTypeAttributeStore
{
    String ID = InventoryTypeAttributeStore.class.getName();
    
    int addInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    void updateInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    void deleteInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute );

    Collection<InventoryTypeAttribute> getAllInventoryTypeAttributes();
    
    InventoryTypeAttribute getInventoryTypeAttribute( int id );
    
    InventoryTypeAttribute getInventoryTypeAttributeByName( String name );
    
}
