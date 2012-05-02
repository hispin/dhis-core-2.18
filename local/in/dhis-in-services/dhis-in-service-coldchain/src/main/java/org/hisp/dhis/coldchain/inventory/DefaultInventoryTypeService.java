package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

public class DefaultInventoryTypeService implements InventoryTypeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private InventoryTypeStore inventoryTypeStore;

    public void setInventoryTypeStore( InventoryTypeStore inventoryTypeStore )
    {
        this.inventoryTypeStore = inventoryTypeStore;
    }

    // -------------------------------------------------------------------------
    // InventoryType
    // -------------------------------------------------------------------------

    @Transactional
    @Override
    public int addInventoryType( InventoryType inventoryType )
    {
        return inventoryTypeStore.addInventoryType( inventoryType );
    }

    @Transactional
    @Override
    public void deleteInventoryType( InventoryType inventoryType )
    {
        inventoryTypeStore.deleteInventoryType( inventoryType );
    }

    @Transactional
    @Override
    public Collection<InventoryType> getAllInventoryTypes()
    {
        return inventoryTypeStore.getAllInventoryTypes();
    }

    @Transactional
    @Override
    public void updateInventoryType( InventoryType inventoryType )
    {
        inventoryTypeStore.updateInventoryType( inventoryType );
    }
    
    public InventoryType getInventoryTypeByName( String name )
    {
        return inventoryTypeStore.getInventoryTypeByName( name );
    }
    
    public InventoryType getInventoryType( int id )
    {
        return inventoryTypeStore.getInventoryType( id );
    }

}
