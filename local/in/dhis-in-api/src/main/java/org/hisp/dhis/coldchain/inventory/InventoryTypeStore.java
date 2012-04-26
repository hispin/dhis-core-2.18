package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

public interface InventoryTypeStore
{
    String ID = InventoryTypeStore.class.getName();
    
    int addInventoryType( InventoryType inventoryType );

    void updateInventoryType( InventoryType inventoryType );

    void deleteInventoryType( InventoryType inventoryType );

    Collection<InventoryType> getAllInventoryTypes();
    
    InventoryType getInventoryTypeByName( String name );
    
    InventoryType getInventoryType( int id );
}
