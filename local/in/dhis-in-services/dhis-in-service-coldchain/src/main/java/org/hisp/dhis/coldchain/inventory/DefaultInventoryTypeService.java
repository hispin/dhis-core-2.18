package org.hisp.dhis.coldchain.inventory;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

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

    @Override
    public int addInventoryType( InventoryType inventoryType )
    {
        return inventoryTypeStore.addInventoryType( inventoryType );
    }

    @Override
    public void deleteInventoryType( InventoryType inventoryType )
    {
        inventoryTypeStore.deleteInventoryType( inventoryType );
    }

    @Override
    public Collection<InventoryType> getAllInventoryTypes()
    {
        return inventoryTypeStore.getAllInventoryTypes();
    }

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
